package io.github.instasketch.instasketch.activities;

/**
 * Created by transfusion on 15-10-6.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.ServerAPI.*;
import io.github.instasketch.instasketch.adapters.RecyclerViewAdapter;

import io.github.instasketch.instasketch.adapters.SearchFragmentPagerAdapter;
import io.github.instasketch.instasketch.database.SearchResult;
import io.github.instasketch.instasketch.fragments.SearchResultFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SearchActivity extends AppCompatActivity implements SearchResultFragment.OnFragmentInteractionListener, SearchResultFragment.resultFragmentInit {

    Random random = new Random();

    private static final String BASE_URL = "http://image.baidu.com";
    private List<SearchResult> searchResultList;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    public static final String INPUT_IMAGE_URI = "INPUT_IMAGE_URI";
    public static final String INPUT_THUMBNAIL_URI = "INPUT_THUMBNAIL_URI";
    public static final String INPUT_IS_SKETCH = "INPUT_IS_SKETCH";

    private boolean isSketch;
    private Uri queryImageURI;
    private Uri queryThumbnailURI;

    protected Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle args = getIntent().getExtras();
        if(!args.getBoolean(INPUT_IS_SKETCH)){
            isSketch = false;
            queryImageURI = args.getParcelable(INPUT_IMAGE_URI);
            queryThumbnailURI = args.getParcelable(INPUT_THUMBNAIL_URI);
        }
        else {
            isSketch = true;
            queryImageURI = args.getParcelable(INPUT_IMAGE_URI);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SearchFragmentPagerAdapter(getSupportFragmentManager(),
                SearchActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*mRecyclerView = (RecyclerView) findViewById(R.id.searchResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));*/

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        populateSearchResults();
    }


    @Override
    public Uri getQueryImageURI() {
        return queryImageURI;
    }

    @Override
    public Uri getQueryThumbnailURI(){
        return queryThumbnailURI;
    }

    @Override
    public boolean isSketch(){
        return isSketch;
    }

    protected void populateSearchResults(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        baiduAPIInterface apiInterface = retrofit.create(baiduAPIInterface.class);

        Call<SearchResultsModel> call = apiInterface.getResults();
        call.enqueue(new Callback<SearchResultsModel>() {

            @Override
            public void onResponse(Response<SearchResultsModel> response, Retrofit retrofit) {
                searchResultList = new ArrayList<>();
                SearchResultsModel s = response.body();
                for (ImageModel i : s.data) {
                    if (i.objURL != null) {
                        SearchResult result = new SearchResult();
                        result.setThumbnailImageUrl(i.objURL);
                        result.setSimilarityIndex(random.nextInt(10));
                        searchResultList.add(result);
                    }

                }
                mRecyclerViewAdapter = new RecyclerViewAdapter(SearchActivity.this, searchResultList);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}