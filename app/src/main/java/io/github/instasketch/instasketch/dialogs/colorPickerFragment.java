package io.github.instasketch.instasketch.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;

import io.github.instasketch.instasketch.R;

/**
 * Created by xiaoyang on 16/3/21.
 */
public class colorPickerFragment extends android.support.v4.app.DialogFragment {
    private LobsterPicker mLobsterPicker;
    private onColorSelectedListener mColorSelectedListener;

    public void setOnColorSelectedListener (onColorSelectedListener listener) {
        mColorSelectedListener = listener;
    }

    public colorPickerFragment(){

    }
//    public interface
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    //        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActionBar(), )
        final LayoutInflater inflater = getActivity().getLayoutInflater();
    //        System.out.println(builder);
        final View dialogView = inflater.inflate(R.layout.material_colorpicker, null);
        mLobsterPicker = (LobsterPicker) dialogView.findViewById(R.id.lobsterpicker);
        mLobsterPicker.addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {

            }

            @Override
            public void onColorSelected(@ColorInt int color) {
                mColorSelectedListener.onColorSelected(color);
            }
        });
       // Log.i("balabala", mLobsterPicker.toString() );
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(dialogView);
//        Log.i("babababa", String.valueOf(mLobsterPicker.getColor()));
        return builder.create();
    }

    public static colorPickerFragment newInstance(){
        colorPickerFragment colorpickerfragment = new colorPickerFragment();

        return colorpickerfragment;
    }

    public interface onColorSelectedListener {
        void onColorSelected(int color);
    }

    public int getColor() {
        return mLobsterPicker.getColor();
    }
}
