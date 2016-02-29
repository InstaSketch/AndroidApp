package io.github.instasketch.instasketch.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;

import android.net.Uri;
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


/**
 * Created by transfusion on 16-2-28.
 */
public class ImageDatabaseIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static final int REPOPULATE_DB = 0;

    public static final String REQUEST = "request";

    public ImageDatabaseIntentService(){
        super("img_db_thread");
    }

    public ImageDatabaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int n = intent.getIntExtra(REQUEST, REPOPULATE_DB);
        switch(n){
            case REPOPULATE_DB:
                repopulateEntireDB();
        }
    }

//    Methods below all interface with MediaStore and ImageDatabaseContentProvider.

    private void repopulateEntireDB() {
        ColorDescriptorNative c = new ColorDescriptorNative();
        Cursor mCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        Log.i("cursor.getCount()) :", mCursor.getCount() + "");

        String TAG = "Image DB: ";
        mCursor.moveToFirst();
        /*while(!mCursor.isAfterLast()) {
            Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d(TAG, " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mCursor.moveToNext();
        }*/

        Mat m = Highgui.imread(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        Log.i("read img", m.size().toString());
        float[] colorDesc = c.getDesc(m);
        Log.i("got desc", String.valueOf(colorDesc.length));


        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bas);
        for (float f : colorDesc)
            try {
                ds.writeFloat(f);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        byte[] bytes = bas.toByteArray();
        ContentValues values = new ContentValues();
        values.put(ImageDatabaseHelper.KEY_PATH, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        values.put(ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR, bytes);
//        Uri uri = ImageDatabaseContentProvider.CONTENT_URI +
        Uri todoUri = getContentResolver().insert(ImageDatabaseContentProvider.CONTENT_URI, values);

        Log.i("internal URI: ", todoUri.toString());

        /*String[] projection = {ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR};
        String[] selection = {ImageDatabaseHelper.KEY_ID +" = "+ ContentUris.parseId(todoUri)};
        Cursor testRetrieve = getContentResolver().query(ImageDatabaseContentProvider.CONTENT_URI, projection, ImageDatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(ContentUris.parseId(todoUri))}, null, null);

        testRetrieve.moveToFirst();
        byte[] buffer = testRetrieve.getBlob(0);

        ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
        DataInputStream dis = new DataInputStream(bis);
        float[] fArr = new float[buffer.length / 4];  // 4 bytes per float
        for (int i = 0; i < fArr.length; i++)
        {
            try {
                fArr[i] = dis.readFloat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Arrays.equals(colorDesc, fArr)){
            Log.i("retrieved :", "they are equal");
        }*/

    }
}
