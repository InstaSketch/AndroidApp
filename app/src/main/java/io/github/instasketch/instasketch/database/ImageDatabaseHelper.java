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
    private static final String DATABASE_NAME = "imageDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_IMAGES = "images";

    public static final String KEY_PATH = "path";

    public static final String KEY_LOCAL_STRUCTURAL_TYPE = "struct_local_type";
    public static final String KEY_LOCAL_STRUCTURAL_DESCRIPTOR = "struct_local_desc";
//    used to store SIFT descriptors, unused for now
    public static final String KEY_GLOBAL_STRUCTURAL_DESCRIPTOR = "struct_global_desc";
    public static final String KEY_COLOR_DESCRIPTOR = "color_desc";

    /*public ImageDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    public ImageDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE "+TABLE_IMAGES+"("+KEY_PATH+" TEXT "+KEY_LOCAL_STRUCTURAL_DESCRIPTOR+" BLOB " +
        KEY_GLOBAL_STRUCTURAL_DESCRIPTOR + " BLOB " + KEY_COLOR_DESCRIPTOR + " BLOB " + ")";

        db.execSQL(CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public void addImage(Image img){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PATH, img.getPath());

        if (img.getLocalStructuralDescriptor() != null) {
            values.put(KEY_LOCAL_STRUCTURAL_DESCRIPTOR, new SerializableMatWrapper(img.getLocalStructuralDescriptor()).toByteArray());
        }

        if (img.getGlobalStructuralDescriptor() != null) {
            values.put(KEY_GLOBAL_STRUCTURAL_DESCRIPTOR, new SerializableMatWrapper(img.getGlobalStructuralDescriptor()).toByteArray());
        }

        if (img.getColorDescriptor() != null) {
            values.put(KEY_COLOR_DESCRIPTOR, new SerializableMatWrapper(img.getColorDescriptor()).toByteArray());
        }
        db.insert(TABLE_IMAGES, null, values);
        db.close();

    }

    public Image getImage(String path) throws IOException, ClassNotFoundException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, new String[] {KEY_LOCAL_STRUCTURAL_DESCRIPTOR, KEY_GLOBAL_STRUCTURAL_DESCRIPTOR, KEY_COLOR_DESCRIPTOR}
        , KEY_PATH + "=?", new String[] { path }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Image img = new Image(path);
            if (!cursor.isNull(0)){
                ByteArrayInputStream byteStream  = new ByteArrayInputStream(cursor.getBlob(0));
                ObjectInputStream objStream  = new ObjectInputStream(byteStream);
                img.setLocalStructuralDescriptor( ( (SerializableMatWrapper) objStream.readObject()).getMat());
            }
            if (!cursor.isNull(1)){
                ByteArrayInputStream byteStream  = new ByteArrayInputStream(cursor.getBlob(1));
                ObjectInputStream objStream  = new ObjectInputStream(byteStream);
                img.setGlobalStructuralDescriptor(((SerializableMatWrapper) objStream.readObject()).getMat());
            }
            if (!cursor.isNull(2)){
                ByteArrayInputStream byteStream  = new ByteArrayInputStream(cursor.getBlob(2));
                ObjectInputStream objStream  = new ObjectInputStream(byteStream);
                img.setColorDescriptor(((SerializableMatWrapper) objStream.readObject()).getMat());
            }
            return img;

        }
        else {
            return null;
        }


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
