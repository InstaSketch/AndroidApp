package io.github.instasketch.instasketch.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;

import io.github.instasketch.instasketch.R;

/**
 * Created by xiaoyang on 16/3/21.
 */
public class ColorPickerFragment extends android.support.v4.app.DialogFragment {
    private LobsterPicker mLobsterPicker;
    private LobsterShadeSlider mLobsterShadeSlider;

    private onColorSelectedListener mColorSelectedListener;

    public void setOnColorSelectedListener (onColorSelectedListener listener) {
        mColorSelectedListener = listener;
    }

    public ColorPickerFragment(){

    }
//    public interface
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.material_colorpicker, null);
        configuraColorPicker(dialogView);
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setView(dialogView);
        return builder.create();
    }

    private void configuraColorPicker(View view){
        mLobsterPicker = (LobsterPicker) view.findViewById(R.id.lobsterpicker);
        mLobsterShadeSlider = (LobsterShadeSlider) view.findViewById(R.id.shadeslider);
        //To connect them
        mLobsterPicker.addDecorator(mLobsterShadeSlider);
    }

    public static ColorPickerFragment newInstance(){
        return new ColorPickerFragment();
    }

    public interface onColorSelectedListener {
        void onColorSelected(int color);
    }

    public int getColor() {
        return mLobsterPicker.getColor();
    }
}
