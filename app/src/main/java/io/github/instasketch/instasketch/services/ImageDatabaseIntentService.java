package io.github.instasketch.instasketch.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.net.Uri;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.database.Cursor;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import io.github.instasketch.instasketch.database.ImageDatabaseContentProvider;
import io.github.instasketch.instasketch.database.ImageDatabaseHelper;
import io.github.instasketch.instasketch.descriptors.ColorDescriptorNative;
import io.github.instasketch.instasketch.receivers.ImageDatabaseResultReceiver;


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

    public static final String REQUEST = "request";

    public static final String RECEIVER_KEY = "service_receiver";

    public static final String STATUS_POPULATE_PROGRESS_KEY = "status_populate_progress";

//    private ImageDatabaseResultReceiver receiver; CREATOR has not been redefined; for all intents and purposes it is identical to its parent class
    private ResultReceiver receiver;

    public ImageDatabaseIntentService(){
        super("img_db_thread");
    }

    public ImageDatabaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int n = intent.getIntExtra(REQUEST, REQ_REPOPULATE_DB);
        this.receiver = intent.getParcelableExtra(RECEIVER_KEY);

        switch(n){
            case REQ_REPOPULATE_DB:
                repopulateEntireDB();
        }
    }

//    Methods below all interface with MediaStore and ImageDatabaseContentProvider.

    private void repopulateEntireDB() {
        getContentResolver().delete(ImageDatabaseContentProvider.CONTENT_URI, "1", null);

        ColorDescriptorNative c = new ColorDescriptorNative();

        Cursor mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        Log.i("cursor.getCount()) :", mCursor.getCount() + "");

        String TAG = "Image DB: ";
        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
//            Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            String filePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, " - File Name : " + filePath);
//            Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            Mat m = Highgui.imread(filePath);
//            Log.i("read img", m.size().toString());
            float[] colorDesc = c.getDesc(m);
//            Log.i("got desc", String.valueOf(colorDesc.length));
            ContentValues values = new ContentValues();
            values.put(ImageDatabaseHelper.KEY_PATH, filePath);
            try {
                values.put(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR, c.serializeFloatArr(colorDesc));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri todoUri = getContentResolver().insert(ImageDatabaseContentProvider.CONTENT_URI, values);
            mCursor.moveToNext();
        }


    }
}
