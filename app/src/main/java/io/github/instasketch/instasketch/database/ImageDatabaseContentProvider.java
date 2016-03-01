package io.github.instasketch.instasketch.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by transfusion on 16-2-22.
 */
public class ImageDatabaseContentProvider extends ContentProvider {

    private ImageDatabaseHelper database;

    private static final int IMAGES = 10;
    private static final int IMAGE_ID = 20;

    private static final String PROVIDER_NAME = "io.github.instasketch.instasketch.database.ImageDatabaseContentProvider";

    private static final String BASE_PATH = "images";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "images";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "image";


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH, IMAGES);
        sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH + "/#", IMAGE_ID);
    }

    private void checkColumns(String[] projection){
        String[] available = {ImageDatabaseHelper.KEY_ID, ImageDatabaseHelper.KEY_PATH, ImageDatabaseHelper.KEY_LOCAL_STRUCTURAL_DESCRIPTOR,
        ImageDatabaseHelper.KEY_GLOBAL_STRUCTURAL_DESCRIPTOR, ImageDatabaseHelper.KEY_COLOR_DESCRIPTOR};
        if (projection != null){
            Set<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            Set<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }

    }

    @Override
    public boolean onCreate() {
        database = new ImageDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);
        queryBuilder.setTables(ImageDatabaseHelper.TABLE_IMAGES);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case IMAGES:
                break;
            case IMAGE_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ImageDatabaseHelper.KEY_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case IMAGES:
                id = sqlDB.insert(ImageDatabaseHelper.TABLE_IMAGES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch(uriType){
            case IMAGES:
                rowsDeleted = sqlDB.delete(ImageDatabaseHelper.TABLE_IMAGES, selection, selectionArgs);
                break;

            case IMAGE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)){
                    rowsDeleted = sqlDB.delete(ImageDatabaseHelper.TABLE_IMAGES, ImageDatabaseHelper.KEY_ID + "=" + id, null);

                }
                else {
                    rowsDeleted = sqlDB.delete(ImageDatabaseHelper.TABLE_IMAGES, ImageDatabaseHelper.KEY_ID + "=" + id + " and "+selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch(uriType) {
            case IMAGES:
                rowsUpdated = sqlDB.update(ImageDatabaseHelper.TABLE_IMAGES, values, selection, selectionArgs);
                break;
            case IMAGE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ImageDatabaseHelper.TABLE_IMAGES, values, ImageDatabaseHelper.KEY_PATH + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(ImageDatabaseHelper.TABLE_IMAGES, values, ImageDatabaseHelper.KEY_PATH +
                            "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
