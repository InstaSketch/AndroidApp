package io.github.instasketch.instasketch.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import io.github.instasketch.instasketch.R;


public class BrushSizeChooserFragment extends DialogFragment {

    private int currentBrushSize;

    private TextView minValue, maxValue, currentValue;
    private SeekBar brushSizeSeekBar;
    private OnNewBrushSizeSelectedListener mNewBrushListener;

    public void setOnNewBrushSizeSelectedListener (OnNewBrushSizeSelectedListener listener) {
        mNewBrushListener = listener;
    }

    public interface OnNewBrushSizeSelectedListener {
        void onNewBrushSizeSelected(float newBrushSize);
    }

    public BrushSizeChooserFragment() {
        // Required empty public constructor
    }

    public static BrushSizeChooserFragment newInstance(int size){
        BrushSizeChooserFragment fragment = new BrushSizeChooserFragment();
        Log.i("initing fragment", fragment.toString());
        Bundle args = new Bundle();
        if (size > 0){
            args.putInt("current_brush_size", size);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("current_brush_size")) {
            int brushSize = args.getInt("current_brush_size", 0);
            if (brushSize > 0) {
                currentBrushSize = brushSize;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActionBar(), )
        final LayoutInflater inflater = getActivity().getLayoutInflater();
//        System.out.println(builder);
        final View dialogView = inflater.inflate(R.layout.fragment_brush_size_chooser, null);
        if (dialogView != null){
            minValue = (TextView) dialogView.findViewById(R.id.text_view_min_value);
            maxValue = (TextView) dialogView.findViewById(R.id.text_view_max_value);

            minValue.setText(String.valueOf(getResources().getInteger(R.integer.min_size)));
            maxValue.setText(String.valueOf(getResources().getInteger(R.integer.brush_max_size)));

            currentValue = (TextView)dialogView.findViewById(R.id.text_view_brush_size);
            if (currentBrushSize > 0){
                currentValue.setText(getResources().getString(R.string.label_brush_size) + currentBrushSize);
            }

            brushSizeSeekBar = (SeekBar)dialogView.findViewById(R.id.seek_bar_brush_size);
            brushSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                    currentValue.setText(getResources().getString(R.string.label_brush_size) + progress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    System.out.println("Tracking stopped "+progressChanged);
                    mNewBrushListener.onNewBrushSizeSelected(progressChanged);
                }
            });

        }
        builder.setTitle("Choose new Brush Size")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(dialogView);


        return builder.create();
    }

    
}
