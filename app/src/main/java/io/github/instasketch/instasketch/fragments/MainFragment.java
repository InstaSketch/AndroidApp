package io.github.instasketch.instasketch.fragments;

/**
 * Created by transfusion on 15-10-6.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static  final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Toolbar sketchPalette;

    private SketchView mSketchView;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.toolbar_menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
