package com.example.varamach.simplepaintapp.controller;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.varamach.simplepaintapp.R;
import com.example.varamach.simplepaintapp.view.ColorSeekView;

/**
 * Created by varamach on 2/23/18.
 * Controller that handles color picker view. It also handles settings where it stores the picked color.
 */

public class ColorPickerFragment extends DialogFragment {


    //For time being, the activity that calls fragment must be an instance of ColorPickerListener
    //for the callback.
    public interface ColorPickerListener {
        //Notify the caller about the picked color
        void onConfirmColorPick(int color);
    }

    private ColorSeekView mColorSeekBar;
    private int mColor;
    private int mProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_color_picker, container);

        //retrieve last set progress and set it on seekbar
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PaintActivity.PREFERENCES, Context.MODE_PRIVATE);
        int progress = sharedpreferences.getInt(PaintActivity.PROGRESS, 0);

        //set the color based on progress
        mColor = getColor(progress);
        setupSeekbar(view, progress);
        setupConfirmButton(view);
        return view;
    }

    private void setupConfirmButton(View view) {
        ImageButton imagebutton = view.findViewById(R.id.seekbar_button);
        imagebutton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                //On confirmation, inform the caller about the picked color
                if (getActivity() instanceof ColorPickerListener) {
                    ((ColorPickerListener) getActivity()).onConfirmColorPick(mColor);
                }
                //Store the progress and color in preferences
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences(PaintActivity.PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(PaintActivity.PROGRESS, mProgress);
                editor.putInt(PaintActivity.COLOR, mColor);
                editor.apply();

                //dismiss the dialog
                getDialog().dismiss();
            }
        });
    }

    private void setupSeekbar(View view, int progress) {
        mColorSeekBar = view.findViewById(R.id.color_seekbar);
        mColorSeekBar.setMax(255 * 7 -1);
        mColorSeekBar.setProgress(progress);
        mColorSeekBar.setBackgroundColor(mColor);
        mColorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //set the new color based on progress
                    mColor = getColor(progress);
                    mColorSeekBar.setBackgroundColor(mColor);
                    mProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }


    /**
     * Progress to rgb conversion is taken from https://stackoverflow.com/questions/29163925/256-colors-in-8-bit-color-picker
     * @param progress
     */
     private int getColor(int progress) {
        int r = 0;
        int g = 0;
        int b = 0;

        if (progress < 256) {
            r = 0;
            g = 0;
            b = progress;
        } else if (progress < 256 * 2) {
            r = 0;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 3) {
            r = 0;
            g = 255;
            b = progress % 256;
        } else if (progress < 256 * 4) {
            r = progress % 256;
            g = 256 - progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 5) {
            r = 255;
            g = 0;
            b = progress % 256;
        } else if (progress < 256 * 6) {
            r = 255;
            g = progress % 256;
            b = 256 - progress % 256;
        } else if (progress < 256 * 7) {
            r = 255;
            g = 255;
            b = progress % 256;
        }

        return Color.argb(255, r, g, b);
    }

}
