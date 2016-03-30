package io.github.instasketch.instasketch.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.instasketch.instasketch.activities.AlbumPickerActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by transfusion on 16-3-17.
 */
public class SharedPreferencesManager {

    public static final String PREFS_NAME = "InstaSketch_Prefs";
    public static final String PREFS_KEY_ACTIVE_BUCKETS = "Active_Buckets";
    public static final String PREFS_KEY_LAST_POPULATED = "Last_Populated";

    private SharedPreferences settings;
    private SharedPreferences defaultSettings;
    private Editor editor;
    private Context mContext;

    public SharedPreferencesManager(Context context){
        this.mContext = context;
        this.settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.defaultSettings = getDefaultSharedPreferences(context);
        this.editor = settings.edit();
    }


    public void savePopulatedAlbums(Set<String> buckets){
        editor.putStringSet(PREFS_KEY_ACTIVE_BUCKETS, buckets);
        editor.commit();
    }

    public Set<String> getPopulatedAlbums(){
//        if(settings.contains(PREFS_KEY_ACTIVE_BUCKETS)){
            return settings.getStringSet(PREFS_KEY_ACTIVE_BUCKETS, null);
        /*}
        else {
            return null;
        }*/
    }

    public boolean isPopulatedAlbum(String bucketName){
        return settings.getStringSet(PREFS_KEY_ACTIVE_BUCKETS, Collections.EMPTY_SET).contains(bucketName);
    }

    public long getLastPopulatedDate(){
        return settings.getLong(PREFS_KEY_LAST_POPULATED, -1);
    }

    public void setLastPopulatedDate(long date){
        editor.putLong(PREFS_KEY_LAST_POPULATED, date);
        editor.commit();
    }

    public int getDistanceMeasure(){
        int s = Integer.valueOf(defaultSettings.getString("distance_measure", "0"));
        Log.i("preferences called", String.valueOf(s));
        return s;
    }
}
