package io.github.instasketch.instasketch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.fragments.*;

public class MainActivity extends AppCompatActivity implements HistoryFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener, DatabaseFragment.OnFragmentInteractionListener {

    protected DrawerLayout mDrawerLayout;
    protected Toolbar mToolBar;
    protected NavigationView mNavView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(mDrawerLayout);

        mNavView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);

        mNavView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener(){
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        System.out.println(menuItem.getItemId());
                        navigate(menuItem);
                        return true;
                    }
                }
        );

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        try {
            fragmentTransaction.replace(R.id.activity_content, new MainFragment());
        } catch (Exception e){
            e.printStackTrace();
        }
        fragmentTransaction.commit();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void navigate(MenuItem menuItem){
        Fragment fragment = null;

        int itemID = menuItem.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (itemID == R.id.drawer_home){

            try {
                fragment = MainFragment.class.newInstance();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            menuItem.setChecked(true);
            setTitle(R.string.app_name);
        }

        if (itemID == R.id.drawer_dbmanager){

            try {
                fragment = DatabaseFragment.class.newInstance();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        }

        if (itemID == R.id.drawer_history){
            try {
                fragment = HistoryFragment.class.newInstance();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
        }

        if (itemID == R.id.drawer_settings){
            Intent i = new Intent(this, GlobalPreferencesActivity.class);
            startActivity(i);
            return;
        }

        fragmentTransaction.replace(R.id.activity_content, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}