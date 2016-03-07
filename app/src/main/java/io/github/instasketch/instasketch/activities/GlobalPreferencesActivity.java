package io.github.instasketch.instasketch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.github.instasketch.instasketch.R;

/**
 * Created by transfusion on 2015/10/1.
 */
public class GlobalPreferencesActivity extends AppCompatActivity {

    protected Toolbar mToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getFragmentManager().beginTransaction().replace(R.id.prefScreenFrame, new MyPreferenceFragment()).commit();
        mToolBar = (Toolbar) findViewById(R.id.preferences_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.global_preferences);
            Preference albums = (Preference) findPreference("albums");

            albums.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), AlbumPickerActivity.class);
                    startActivity(i);
                    return false;
                }
            });
        }
    }
}
