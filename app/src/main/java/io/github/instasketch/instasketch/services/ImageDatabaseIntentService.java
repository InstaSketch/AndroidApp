package io.github.instasketch.instasketch.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;

import android.content.OperationApplicationException;
import android.database.CursorJoiner;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.database.Cursor;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.instasketch.instasketch.activities.AlbumPickerActivity;
import io.github.instasketch.instasketch.database.ImageDatabaseContentProvider;
import io.github.instasketch.instasketch.database.ImageDatabaseHelper;
import io.github.instasketch.instasketch.database.SearchResult;
import io.github.instasketch.instasketch.database.SharedPreferencesManager;
import io.github.instasketch.instasketch.descriptors.ColorDescriptor;
import io.github.instasketch.instasketch.descriptors.ColorDescriptorNative;
import io.github.instasketch.instasketch.fragments.DatabaseFragment;
import io.github.instasketch.instasketch.fragments.SearchResultFragment;


/**
 * Created by transfusion on 16-2-28.
 */
public class ImageDatabaseIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static final int REQ_REPOPULATE_DB = 0;
    public static final int REQ_ABORT_POPULATE = 3;
    public static final int REQ_QUERY_ENTIRE_DB = 2;
    public static final int REQ_IMAGE_BUCKETS = 1;
    public static final int REQ_UPDATE_DB = 4;

    public static final String REQUEST = "request";

    public static final String RECEIVER_KEY = "service_receiver";

    public static final String STATUS_POPULATE_PROGRESS_KEY = "status_populate_progress";
    public static final String STATUS_POPULATE_PROGRESS_PATH_KEY = "status_populate_path_key";
    public static final String STATUS_POPULATE_BATCH_COUNT_KEY = "status_populate_batch_count_key";
    public static final String STATUS_POPULATE_COMPLETED_KEY = "status_populate_completed";

    public static final String QUERY_IMAGE_URI = "query_image_uri";

    private SharedPreferencesManager sharedPreferencesManager;

//    private ImageDatabaseResultReceiver receiver; CREATOR has not been redefined; for all intents and purposes it is identical to its parent class
    private ResultReceiver receiver;

    public ImageDatabaseIntentService(){
        super("img_db_thread");
    }

    public ImageDatabaseIntentService(String name) {
        super(name);
    }

    public static volatile boolean isRunning = false;

    private ColorDescriptorNative colorDesc;

    @Override
    protected void onHandleIntent(Intent intent) {
        int n = intent.getIntExtra(REQUEST, REQ_REPOPULATE_DB);
        this.receiver = intent.getParcelableExtra(RECEIVER_KEY);
        this.sharedPreferencesManager = new SharedPreferencesManager(this);
        this.colorDesc = new ColorDescriptorNative(8,12,5);

        switch(n){
            case REQ_REPOPULATE_DB:
//                Bundle notifyUI = new Bundle();
                isRunning = true;
                repopulateEntireDB();
                break;
            case REQ_UPDATE_DB:
                isRunning = true;
                updateDB();
                break;
            case REQ_ABORT_POPULATE:
                break;
            case REQ_IMAGE_BUCKETS:
                isRunning = true;
                getImageBuckets();
                break;
            case REQ_QUERY_ENTIRE_DB:
                receiver.send(SearchResultFragment.QUERY_STARTED, Bundle.EMPTY);
                isRunning = true;
                imageMatcher(intent.<Uri>getParcelableExtra(QUERY_IMAGE_URI));
                break;
        }
    }

    private void imageMatcher(Uri inputImg){
        String path = getRealPathFromURI(inputImg);
        Log.i("searching for", path);
        Mat m = Highgui.imread(path);
        ColorDescriptorNative c = new ColorDescriptorNative(8,12,5);
        float[] colorDesc = c.getDesc(m);
        float[] arr;
        m.release();
        if (!(colorDesc.length == 0)){
            String[] projection = {ImageDatabaseHelper.KEY_IMAGE_ID, ImageDatabaseHelper.KEY_PATH, ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR};
            Cursor cr = getContentResolver().query(ImageDatabaseContentProvider.CONTENT_URI, projection, null, null, null);
            ArrayList<SearchResult> searchResultList = new ArrayList<>();
            if(cr != null && cr.moveToFirst()){
//                Log.i("db working!", String.valueOf(cr.getCount()));
                int descIndex, descPath, imageID;
                while(!cr.isAfterLast()){
                    descIndex = cr.getColumnIndex(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR);
                    descPath = cr.getColumnIndex(ImageDatabaseHelper.KEY_PATH);
                    imageID = cr.getColumnIndex(ImageDatabaseHelper.KEY_IMAGE_ID);

                    arr = c.deserializeFloatArr(cr.getBlob(descIndex));
                    SearchResult s = new SearchResult();
                    s.setImageUrl(cr.getString(descPath));
                    s.setSimilarityIndex(c.chiSquared(colorDesc, colorDesc.length, arr, arr.length));
                    s.setImageID(cr.getInt(imageID));

                    searchResultList.add(s);
//                    Log.i("comparing with", cr.getString(descPath));
//                    Log.i("distance!", String.valueOf(c.chiSquared(colorDesc, colorDesc.length, arr, arr.length)));

                    cr.moveToNext();
                }
                cr.close();
                Bundle b = new Bundle();
                b.putParcelableArrayList(SearchResultFragment.RESULTS_LIST, searchResultList);
                receiver.send(SearchResultFragment.QUERY_COMPLETED, b);
            }


        }
        isRunning = false;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void getImageBuckets(){
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        Map<String, Integer> buckets = new HashMap<String, Integer>();

        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (c.getCount() > 0){
            c.moveToFirst();
            do {
                String bucketDisplayName = c.getString(c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if(!buckets.containsKey(bucketDisplayName)) {
                    buckets.put(bucketDisplayName, 1);

                }
                else {
                    buckets.put(bucketDisplayName, buckets.get(bucketDisplayName) + 1);
                }
            }
            while (c.moveToNext());
        }
        c.close();
        ArrayList<AlbumPickerActivity.BucketReference> l = new ArrayList<AlbumPickerActivity.BucketReference>();
        for (Map.Entry<String, Integer> entry : buckets.entrySet()){
            l.add(new AlbumPickerActivity.BucketReference(entry.getKey(), entry.getValue()));
        }
        Bundle b = new Bundle();
        b.putParcelableArrayList(AlbumPickerActivity.ALBUMS_LIST, l);
        receiver.send(AlbumPickerActivity.POPULATE_FINISHED, b);
        isRunning = false;

    }
//    Methods below all interface with MediaStore and ImageDatabaseContentProvider.

    private void repopulateEntireDB(){
        getContentResolver().delete(ImageDatabaseContentProvider.CONTENT_URI, "1", null);


        Set<String> albums = sharedPreferencesManager.getPopulatedAlbums();

        StringBuilder inList = new StringBuilder(albums.size()*2);
        for (int i = 0; i < albums.size(); i++){
            if (i > 0){
                inList.append(",");
            }
            inList.append("?");
        }

        String[] albumsList = albums.toArray(new String[albums.size()]);

        Cursor mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "BUCKET_DISPLAY_NAME IN (" + inList.toString() + ")", albumsList, null);

        int batchCount = mCursor.getCount();
        Bundle b = new Bundle();
        b.putInt(DatabaseFragment.POPULATE_BATCH_COUNT, batchCount);

        receiver.send(DatabaseFragment.POPULATE_STARTED, b);

        String TAG = "Image DB: ";
        mCursor.moveToFirst();
        int currentImg = 1;
        ArrayList<ContentValues> values_array = new ArrayList<ContentValues>();
//        b.clear();
        while(!mCursor.isAfterLast()) {
            String filePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, " - File Name : " + filePath);
            Mat m = Highgui.imread(filePath);
            float[] colorDesc = this.colorDesc.getDesc(m);
            m.release();
            if (!(colorDesc.length == 0)){
                ContentValues values = new ContentValues();
                values.put(ImageDatabaseHelper.KEY_IMAGE_ID, mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
                values.put(ImageDatabaseHelper.KEY_PATH, filePath);
                values.put(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR, this.colorDesc.serializeFloatArr(colorDesc));
                values_array.add(values);
            }
            b.putInt(STATUS_POPULATE_PROGRESS_KEY, currentImg++);
            b.putString(STATUS_POPULATE_PROGRESS_PATH_KEY, filePath);
            receiver.send(DatabaseFragment.POPULATE_PROGRESS, b);
            mCursor.moveToNext();
        }
        mCursor.close();
        getContentResolver().bulkInsert(ImageDatabaseContentProvider.CONTENT_URI, values_array.toArray(new ContentValues[values_array.size()]));
        sharedPreferencesManager.setLastPopulatedDate(System.currentTimeMillis());
        receiver.send(DatabaseFragment.POPULATE_COMPLETED, Bundle.EMPTY);
        isRunning = false;
    }

    private void updateDB(){
        Set<String> albums = sharedPreferencesManager.getPopulatedAlbums();
        ColorDescriptorNative c = new ColorDescriptorNative(8,12,5);

        StringBuilder inList = new StringBuilder(albums.size()*2);
        for (int i = 0; i < albums.size(); i++){
            if (i > 0){
                inList.append(",");
            }
            inList.append("?");
        }

        String[] albumsList = albums.toArray(new String[albums.size()]);

        Cursor mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "BUCKET_DISPLAY_NAME IN (" + inList.toString() + ")", albumsList, MediaStore.Images.Media._ID+" ASC");

        String[] projection = {ImageDatabaseHelper.KEY_IMAGE_ID};
        Cursor cr = getContentResolver().query(ImageDatabaseContentProvider.CONTENT_URI, projection, null, null, ImageDatabaseHelper.KEY_IMAGE_ID+" ASC");

        CursorJoiner results = new CursorJoiner(mCursor, new String[]{MediaStore.Images.Media._ID}, cr, new String[]{ImageDatabaseHelper.KEY_IMAGE_ID});

        receiver.send(DatabaseFragment.UPDATE_STARTED, Bundle.EMPTY);
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        ArrayList<ContentValues> values_array = new ArrayList<ContentValues>();

        ContentProviderOperation operation;
        ContentValues values;
        for (CursorJoiner.Result result : results){
            switch(result){
                case RIGHT:
//                    Implies that the image has been deleted from the user's storage
                    operation = ContentProviderOperation.newDelete(ImageDatabaseContentProvider.CONTENT_URI).withSelection(ImageDatabaseHelper.KEY_IMAGE_ID + " = ?",
                            new String[] {String.valueOf(cr.getInt(cr.getColumnIndex(ImageDatabaseHelper.KEY_IMAGE_ID)))}).build();
                    operations.add(operation);
                    break;
                case LEFT:
//                    Log.i("New!!", mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    values = dbFriendlyFormat(mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID)), mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    if (values != null){
                        values_array.add(values);
                    }
                    break;
                case BOTH:
                    break;
            }
        }

        try {
            getContentResolver().applyBatch(ImageDatabaseContentProvider.PROVIDER_NAME, operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getContentResolver().bulkInsert(ImageDatabaseContentProvider.CONTENT_URI, values_array.toArray(new ContentValues[values_array.size()]));
        sharedPreferencesManager.setLastPopulatedDate(System.currentTimeMillis());
        receiver.send(DatabaseFragment.UPDATE_COMPLETED, Bundle.EMPTY);
        isRunning = false;

    }

    private ContentValues dbFriendlyFormat(int id, String path){
        Mat m = Highgui.imread(path);
        float[] colorDesc = this.colorDesc.getDesc(m);
        m.release();
        if (!(colorDesc.length == 0)) {
            ContentValues values = new ContentValues();
            values.put(ImageDatabaseHelper.KEY_IMAGE_ID, id);
            values.put(ImageDatabaseHelper.KEY_PATH, path);
            values.put(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR, this.colorDesc.serializeFloatArr(colorDesc));
            return values;
        }
        else {
            return null;
        }
    }
}
