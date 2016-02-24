package io.github.instasketch.instasketch.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.w3c.dom.Text;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.database.ImageDatabaseContentProvider;
import io.github.instasketch.instasketch.database.ImageDatabaseHelper;

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

    public DatabaseFragment() {
        // Required empty public constructor
    }

    static {
        System.loadLibrary("myjni");
    }

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
        System.out.println(data);
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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatabaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatabaseFragment newInstance(String param1, String param2) {
        DatabaseFragment fragment = new DatabaseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mResolver = getContext().getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor mCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        Log.i("cursor.getCount()) :", mCursor.getCount() + "");

        String TAG = "Image DB: ";
        mCursor.moveToFirst();
        /*while(!mCursor.isAfterLast()) {
            Log.d(TAG, " - _ID : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            Log.d(TAG, " - File Name : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            Log.d(TAG, " - File Path : " + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            mCursor.moveToNext();
        }*/
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        else {
            Mat m = Highgui.imread(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            Log.i("read img", m.size().toString());
//            Log.i("KMeans ", Core.kmeans());
        }
        mCursor.close();

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

        return view;

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
