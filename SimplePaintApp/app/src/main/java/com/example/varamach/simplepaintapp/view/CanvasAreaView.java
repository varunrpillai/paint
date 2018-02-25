package com.example.varamach.simplepaintapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by varamach on 2/23/18.
 * Canvas Area View where user will be painting and
 * is extended to identify the user actions on the canvas.
 */

public class CanvasAreaView extends View {


    public interface CanvasController {

        /**
         * The position where finger is touched on the screen.
         */
        void fingerTouchedAt(float x, float y);

        /**
         * The position where finger is currently moved on the screen.
         */
        void fingerMovedTo(float x, float y);

        /**
         * The position where finger is currently raised from the screen.
         */
        void fingerRaised(float x, float y);

        /**
         * Draw the picture in the given canvas.
         */
        void drawPicture(Canvas canvas);
    }

    private CanvasController canvasController;

    public CanvasAreaView(Context context) {
        this(context, null);
    }

    public CanvasAreaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (!(context instanceof CanvasController)) {
            return;
        }
        canvasController = (CanvasController) context;
    }

    /**
     * This method feeds the finger movement on this canvas view
     * as a brush stroke of the picture.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final float xPos = event.getX();
        final float yPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canvasController.fingerTouchedAt(xPos, yPos);
                break;
            case MotionEvent.ACTION_MOVE:
                canvasController.fingerMovedTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                canvasController.fingerRaised(xPos, yPos);
                break;

        }
        invalidate();
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvasController.drawPicture(canvas);
    }
}
