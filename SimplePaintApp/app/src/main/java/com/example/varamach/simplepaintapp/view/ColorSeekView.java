package com.example.varamach.simplepaintapp.view;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

/**
 * Created by varamach on 2/23/18.
 * An extension of Seekbar View that will be having a gradient overlay.
 */

public class ColorSeekView extends AppCompatSeekBar {

    public ColorSeekView(Context context) {
        this(context, null);
    }

    public ColorSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float x1 = getLeft();
        float x2 = getRight();
        float padX2 = getPaddingRight();
        float padX1 = getPaddingLeft();

        //TODO: expectation was x+pad1 and x2-pad2. But the gradient is not rendered as expected.
        // Below are adjusted values. Since it is a 2 day project, I didnt had time to complete and test it.
        LinearGradient overlay = new LinearGradient(x1, 0.0f, x2-padX1-padX2, 0.0f,
                new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(overlay);
        setProgressDrawable(shape);
    }
}
