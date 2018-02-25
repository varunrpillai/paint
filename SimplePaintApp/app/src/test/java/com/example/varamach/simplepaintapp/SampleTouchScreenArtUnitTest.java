package com.example.varamach.simplepaintapp;

import android.graphics.Color;

import com.example.varamach.simplepaintapp.controller.ColorPickerFragment;
import com.example.varamach.simplepaintapp.model.BrushStroke;
import com.example.varamach.simplepaintapp.model.TouchScreenArt;

import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SampleTouchScreenArtUnitTest {
    @Test
    public void sizeAndColorIsSet() throws Exception {

        TouchScreenArt.SavedArtReadListener listener = new TouchScreenArt.SavedArtReadListener() {

            @Override
            public void notifyLoaded(Stack<BrushStroke> strokes) {

            }
        };
        int selectedColor = 3;
        int sizeofBrush = 5;
        BrushStroke stroke = new BrushStroke(selectedColor, sizeofBrush);
        TouchScreenArt art = new TouchScreenArt(listener);
        art.modifyPicture(stroke);
        assertEquals(art.getCurrentChange().getColor(), selectedColor);
        assertEquals(art.getCurrentChange().getSizeOfBrush(), sizeofBrush);
    }

}