package io.github.instasketch.instasketch.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.database.Cursor;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.instasketch.instasketch.activities.AlbumPickerActivity;
import io.github.instasketch.instasketch.database.ImageDatabaseContentProvider;
import io.github.instasketch.instasketch.database.ImageDatabaseHelper;
import io.github.instasketch.instasketch.descriptors.ColorDescriptor;
import io.github.instasketch.instasketch.descriptors.ColorDescriptorNative;
import io.github.instasketch.instasketch.fragments.DatabaseFragment;


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
    public static final int REQ_QUERY_ENTIRE_DB = 2;
    public static final int REQ_IMAGE_BUCKETS = 1;

    public static final String REQUEST = "request";

    public static final String RECEIVER_KEY = "service_receiver";

    public static final String STATUS_POPULATE_PROGRESS_KEY = "status_populate_progress";

    public static final String QUERY_IMAGE_URI = "query_image_uri";

//    private ImageDatabaseResultReceiver receiver; CREATOR has not been redefined; for all intents and purposes it is identical to its parent class
    private ResultReceiver receiver;

    public ImageDatabaseIntentService(){
        super("img_db_thread");
    }

    public ImageDatabaseIntentService(String name) {
        super(name);
    }

    public static volatile boolean isRunning = false;
    @Override
    protected void onHandleIntent(Intent intent) {
        int n = intent.getIntExtra(REQUEST, REQ_REPOPULATE_DB);
        this.receiver = intent.getParcelableExtra(RECEIVER_KEY);

        switch(n){
            case REQ_REPOPULATE_DB:
//                Bundle notifyUI = new Bundle();
                receiver.send(DatabaseFragment.POPULATE_STARTED, Bundle.EMPTY);
                isRunning = true;
                repopulateEntireDB();
                break;
            case REQ_IMAGE_BUCKETS:
                getImageBuckets();
                isRunning = true;
                break;
            case REQ_QUERY_ENTIRE_DB:
                imageMatcher(intent.<Uri>getParcelableExtra(QUERY_IMAGE_URI));
                break;
        }
    }

    private void imageMatcher(Uri inputImg){
        String path = getRealPathFromURI(inputImg);
        Log.i("searching for", path);
        Mat m = Highgui.imread(path);
        ColorDescriptorNative c = new ColorDescriptorNative();
        float[] colorDesc = c.getDesc(m);
        float[] arr;
        m.release();
        if (!(colorDesc.length == 0)){
            String[] projection = {ImageDatabaseHelper.KEY_PATH, ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR};
            Cursor cr = getContentResolver().query(ImageDatabaseContentProvider.CONTENT_URI, projection, null, null, null);
            if(cr != null && cr.moveToFirst()){
                Log.i("db working!", String.valueOf(cr.getCount()));
                int descIndex, descPath;
                while(!cr.isAfterLast()){
                    descIndex = cr.getColumnIndex(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR);
                    descPath = cr.getColumnIndex(ImageDatabaseHelper.KEY_PATH);
                    arr = c.deserializeFloatArr(cr.getBlob(descIndex));
                    Log.i("comparing with", cr.getString(descPath));
                    Log.i("distance!", String.valueOf(c.chiSquared(colorDesc, colorDesc.length, arr, arr.length)));

                    cr.moveToNext();
                }

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

    private void repopulateEntireDB() {
        getContentResolver().delete(ImageDatabaseContentProvider.CONTENT_URI, "1", null);

        ColorDescriptorNative c = new ColorDescriptorNative();

        Cursor mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        Log.i("cursor.getCount()) :", mCursor.getCount() + "");

        String TAG = "Image DB: ";
        mCursor.moveToFirst();
        ArrayList<ContentValues> values_array = new ArrayList<ContentValues>();
        while(!mCursor.isAfterLast()) {
//            Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            String filePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, " - File Name : " + filePath);
//            Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            Mat m = Highgui.imread(filePath);
//            Log.i("read img", m.size().toString());
            float[] colorDesc = c.getDesc(m);
            m.release();

            if (!(colorDesc.length == 0)){
                ContentValues values = new ContentValues();
                values.put(ImageDatabaseHelper.KEY_PATH, filePath);
                values.put(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR, c.serializeFloatArr(colorDesc));
                values_array.add(values);
//                Uri todoUri = getContentResolver().insert(ImageDatabaseContentProvider.CONTENT_URI, values);
            }
//            Log.i("got desc", String.valueOf(colorDesc.length));
            mCursor.moveToNext();
        }
        getContentResolver().bulkInsert(ImageDatabaseContentProvider.CONTENT_URI, values_array.toArray(new ContentValues[values_array.size()]));
        isRunning = false;

    }
}
