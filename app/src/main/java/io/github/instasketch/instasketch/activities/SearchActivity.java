package io.github.instasketch.instasketch.activities;

/**
 * Created by transfusion on 15-10-6.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.ServerAPI.*;
import io.github.instasketch.instasketch.adapters.RecyclerViewAdapter;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SearchActivity extends AppCompatActivity {

    Random random = new Random();

    private static final String BASE_URL = "http://image.baidu.com";
    private List<SearchResult> searchResultList;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    protected Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.searchResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateSearchResults();
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
                    if (i.objURL != null){
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
}