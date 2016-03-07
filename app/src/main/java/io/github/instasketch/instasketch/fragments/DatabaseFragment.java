package io.github.instasketch.instasketch.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.provider.MediaStore;
import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.util.HashMap;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.database.ImageDatabaseContentProvider;
import io.github.instasketch.instasketch.database.ImageDatabaseHelper;
import io.github.instasketch.instasketch.receivers.ImageDatabaseResultReceiver;
import io.github.instasketch.instasketch.services.ImageDatabaseIntentService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatabaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatabaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatabaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//    private ContentResolver mResolver;
    private Cursor mCursor;
    private OnFragmentInteractionListener mListener;

    private TextView rowCountView;
    private Button repopulateBtn;

    public ImageDatabaseResultReceiver dbStatusReceiver;

    public static final int POPULATE_STARTED = 0;
    public static final int POPULATE_PROGRESS = 1;
    public static final int POPULATE_COMPLETED = 2;

//    public static final int RECEIVER = -1;

    public DatabaseFragment() {
        // Required empty public constructor
    }

    static {
        System.loadLibrary("myjni");
    }

    public native String getMessage();

    public native void getMat(long emptyMatAddr);
//        replaces the internal reference to the underlying native object of the argument.

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Let's just try to get the number of rows in total at this point...
        String[] projection = {ImageDatabaseHelper.KEY_PATH};

//        note: first arg of cursorloader requires a Context.
        CursorLoader cursorLoader = new CursorLoader(getActivity(), ImageDatabaseContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        rowCountView = (TextView) getActivity().findViewById(R.id.db_rows);
        rowCountView.setText(String.valueOf(data.getCount()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DatabaseFragment.
     */
    public static DatabaseFragment newInstance() {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] projection = {MediaStore.Images.Media.DATA};
        Log.i("Test JNI Interface", getMessage());

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        else {

        }
//        should be moved into onCreateView if manipulating UI Views

        if (savedInstanceState != null){
            this.dbStatusReceiver = savedInstanceState.getParcelable(ImageDatabaseIntentService.RECEIVER_KEY);
        }
        else {
            setupServiceReceiver();
        }
    }

    private static final int IMAGE_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        Directly interacting with the Database Helper is already working fine
        View view = inflater.inflate(R.layout.fragment_database, container, false);
//        ImageDatabaseHelper imgDB = new ImageDatabaseHelper(this.getActivity());
        rowCountView = (TextView) view.findViewById(R.id.db_rows);
//        v.setText(String.valueOf(imgDB.getImagesCount()));

        getLoaderManager().initLoader(IMAGE_LOADER, null, this);

        repopulateBtn = (Button) view.findViewById(R.id.btn_repopulate);

        if (ImageDatabaseIntentService.isRunning){
            repopulateBtn.setAlpha(.5f);
            repopulateBtn.setEnabled(false);
        }

        repopulateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent testIntent = new Intent(DatabaseFragment.this.getActivity(), ImageDatabaseIntentService.class);
            Log.i("Receiver Type: ", dbStatusReceiver.toString());
            testIntent.putExtra(ImageDatabaseIntentService.RECEIVER_KEY, dbStatusReceiver);
            testIntent.putExtra(ImageDatabaseIntentService.REQUEST, ImageDatabaseIntentService.REQ_REPOPULATE_DB);
            DatabaseFragment.this.getActivity().startService(testIntent);
            }
        });

        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(ImageDatabaseIntentService.RECEIVER_KEY, this.dbStatusReceiver);
    }

    private void setupServiceReceiver(){
        this.dbStatusReceiver = new ImageDatabaseResultReceiver(new Handler());

        this.dbStatusReceiver.setReceiver(new ImageDatabaseResultReceiver.Receiver(){
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData){
            if (resultCode == POPULATE_STARTED){
                repopulateBtn.setAlpha(.5f);
                repopulateBtn.setEnabled(false);
            }
            if (resultCode == POPULATE_PROGRESS ){
                int imgs = resultData.getInt(ImageDatabaseIntentService.STATUS_POPULATE_PROGRESS_KEY);
                Log.i("Pop in progress: ", String.valueOf(imgs));
            }
            else if (resultCode == POPULATE_COMPLETED){
                int imgCount = resultData.getInt("images");
                repopulateBtn.setEnabled(true);
                Log.i("Finished populating, ", String.valueOf(imgCount));
            }
            }
        });
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
