package io.github.instasketch.instasketch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.adapters.RecyclerViewAdapter;
import io.github.instasketch.instasketch.database.SearchResult;
import io.github.instasketch.instasketch.receivers.ImageDatabaseResultReceiver;
import io.github.instasketch.instasketch.services.ImageDatabaseIntentService;


public class SearchResultFragment extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String RESULTS_LIST = "RESULTS_LIST";
    public static final int LOCAL_RESULTS = 1;
    public static final int SERVER_RESULTS = 2;
    private int mPage;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private List<SearchResult> searchResultList;
    private resultFragmentInit activityData;
    public ImageDatabaseResultReceiver dbStatusReceiver;

    public static final int QUERY_STARTED = 0;
//    public static final int QUERY_PROGRESS = 1;
    public static final int QUERY_COMPLETED = 2;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        else {

        }
        mPage = getArguments().getInt(ARG_PAGE);
        /*if (savedInstanceState != null){
            this.dbStatusReceiver = savedInstanceState.getParcelable(ImageDatabaseIntentService.RECEIVER_KEY);
        }
        else {*/
            setupServiceReceiver();
//        }
    }

    public interface resultFragmentInit {
        public Uri getQueryImageURI();
        public Uri getQueryThumbnailURI();
        public boolean isSketch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        TextView sampleText = (TextView) view.findViewById(R.id.local_frag_text);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.searchResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        if(mPage == LOCAL_RESULTS){
//            sampleText.setText("Local Results");
            populateLocalSearchResults();
        }
        else if (mPage == SERVER_RESULTS){

        }
        return view;
    }


    private void setupServiceReceiver(){
        this.dbStatusReceiver = new ImageDatabaseResultReceiver(new Handler());

        this.dbStatusReceiver.setReceiver(new ImageDatabaseResultReceiver.Receiver(){
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData){
                if (resultCode == QUERY_STARTED){
//                    Log.i)
                }
                else if (resultCode == QUERY_COMPLETED){
                    mRecyclerViewAdapter.swap((List<SearchResult>) resultData.get(RESULTS_LIST), RecyclerViewAdapter.SORT_BY_COLOR);
                }
            }
        });
    }

    protected void populateLocalSearchResults(){

        Intent testIntent = new Intent(getActivity(), ImageDatabaseIntentService.class);
        testIntent.putExtra(ImageDatabaseIntentService.REQUEST, ImageDatabaseIntentService.REQ_QUERY_ENTIRE_DB);
        testIntent.putExtra(ImageDatabaseIntentService.QUERY_IMAGE_URI, activityData.getQueryImageURI());
        testIntent.putExtra(ImageDatabaseIntentService.RECEIVER_KEY, dbStatusReceiver);
        getActivity().startService(testIntent);

        /*searchResultList = new ArrayList<>();
        SearchResult result = new SearchResult();
        if (!activityData.isSketch()){
            result.setSimilarityIndex(100);
            result.setImageUrl(activityData.getQueryImageURI().toString());
        }
        searchResultList.add(result);
            result.setThumbnailImageUrl();*/
        searchResultList = new ArrayList<>();
        mRecyclerViewAdapter = new RecyclerViewAdapter(this.getActivity(), searchResultList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }




    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityData = (resultFragmentInit) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
