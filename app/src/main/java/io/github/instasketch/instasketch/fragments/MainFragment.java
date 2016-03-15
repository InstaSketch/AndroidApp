package io.github.instasketch.instasketch.fragments;

/**
 * Created by transfusion on 15-10-6.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.activities.SearchActivity;
import io.github.instasketch.instasketch.dialogs.BrushSizeChooserFragment;
import io.github.instasketch.instasketch.views.SketchView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Toolbar sketchPalette;

    private SketchView mSketchView;

    static final int PICK_PICTURE = 1;

/*    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.toolbar_menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_select_picture:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_PICTURE);
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PICTURE){
            if (data != null){
                Uri selectedImage = data.getData();
                String[] projection = {MediaStore.Images.Thumbnails._ID};
                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection, MediaStore.Images.Thumbnails.IMAGE_ID + "=?", new String[]{selectedImage.getLastPathSegment()}, null);

//                cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(getActivity().getContentResolver(), imageID, MediaStore.Images.Thumbnails.DATA, null);
//                cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, )
                /*cursor.moveToFirst();
                int thumbnailID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
                Uri thumbnailUri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, String.valueOf(thumbnailID));*/

//                String thumbnailPath = cursor.getString(cursor.getColumnIndex(projection[0]));
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                searchIntent.putExtra(SearchActivity.INPUT_IS_SKETCH, false);
                //                searchIntent.putExtra(SearchActivity.INPUT_THUMBNAIL_URI, thumbnailUri);

                searchIntent.putExtra(SearchActivity.INPUT_IMAGE_URI, selectedImage);
                startActivity(searchIntent);


            }
        }
    }
    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivity(i);
            }
        });

        sketchPalette = (Toolbar) view.findViewById(R.id.sketch_palette_toolbar);
        sketchPalette.inflateMenu(R.menu.sketch_palette_menu);

        sketchPalette.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleSketchPaletteAction(item.getItemId());
                return false;
            }
        });

        mSketchView = (SketchView) view.findViewById(R.id.sketch_view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
        public void onFragmentInteraction(Uri uri);
    }

//    The methods below handle the launching of fragments upon tapping on icons in the sketch palette
    private void handleSketchPaletteAction(int itemId){
        System.out.println("clicked "   +itemId);
        switch (itemId){
            case R.id.action_brushsize:
                brushSizePicker();
                break;
            case R.id.action_erase:
                break;
        }
    }

    private void brushSizePicker(){
        BrushSizeChooserFragment brushChooserDialog = BrushSizeChooserFragment.newInstance((int)
                mSketchView.getLastBrushSize());
        brushChooserDialog.setOnNewBrushSizeSelectedListener(new BrushSizeChooserFragment.OnNewBrushSizeSelectedListener() {
            @Override
            public void onNewBrushSizeSelected(float newBrushSize) {
                System.out.println("new brush size!! "+newBrushSize);
                mSketchView.setBrushSize(newBrushSize);
                mSketchView.setLastBrushSize(newBrushSize);
            }
        });
        brushChooserDialog.show(getFragmentManager(), "Dialog");
    }
}
