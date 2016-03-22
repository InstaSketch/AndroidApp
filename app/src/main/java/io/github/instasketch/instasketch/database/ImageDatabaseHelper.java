package io.github.instasketch.instasketch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by transfusion on 16-2-20.
 */
public class ImageDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "imageDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_IMAGES = "images";

    public static final String KEY_ID = "_id";

    public static final String KEY_IMAGE_ID = "image_id";

    public static final String KEY_PATH = "path";

    public static final String KEY_LOCAL_STRUCTURAL_TYPE = "structLocalType";
    public static final String KEY_LOCAL_STRUCTURAL_DESCRIPTOR = "structLocalDesc";
//    used to store SIFT descriptors, unused for now
    public static final String KEY_GLOBAL_STRUCTURAL_DESCRIPTOR = "structGlobalDesc";
    public static final String KEY_COLOR_DESCRIPTOR = "colorDesc";

    /*public ImageDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    public ImageDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE "+TABLE_IMAGES+"("+
                KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_IMAGE_ID+" INTEGER NOT NULL, "+
                KEY_PATH+" TEXT NOT NULL, "+
                KEY_LOCAL_STRUCTURAL_DESCRIPTOR+" blob, " +
        KEY_GLOBAL_STRUCTURAL_DESCRIPTOR + " blob, " +
                KEY_COLOR_DESCRIPTOR + " blob" +
                ")";

        db.execSQL(CREATE_IMAGES_TABLE);

        String ENFORCE_UNIQUE_PATH = "CREATE UNIQUE INDEX PATH_UNQ on "+TABLE_IMAGES + "(" + KEY_PATH +")";
        db.execSQL(ENFORCE_UNIQUE_PATH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public long getImagesCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(db, TABLE_IMAGES);
        db.close();
        return cnt;
    }

//    May run out of memory!!
//    public List<Image> getAllImages(){
//
//    }

    public List<String> getAllImagePaths(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> names = new ArrayList<String>();
        Cursor cursor = db.query(TABLE_IMAGES, new String[] {KEY_PATH}, null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                names.add(cursor.getString(0));
            }
            while (cursor.moveToNext());

        }
        return names;
    }
}
