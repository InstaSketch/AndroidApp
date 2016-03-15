package io.github.instasketch.instasketch.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.github.instasketch.instasketch.fragments.SearchResultFragment;

/**
 * Created by transfusion on 16-3-7.
 */
public class SearchFragmentPagerAdapter extends FragmentPagerAdapter {
    /*final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };*/

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Local", "Server"};

    private Context context;

    public SearchFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return SearchResultFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}