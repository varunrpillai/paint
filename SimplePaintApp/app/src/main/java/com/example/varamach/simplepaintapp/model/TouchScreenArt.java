package com.example.varamach.simplepaintapp.model;

import android.content.Context;
import android.os.AsyncTask;

import java.io.Serializable;
import java.util.Stack;

/**
 * Created by varamach on 2/23/18.
 * Model that represents information about a single artwork.
 */

@SuppressWarnings("unchecked")
public class TouchScreenArt implements Picture<BrushStroke>, Serializable {

    public interface SavedArtReadListener {
        /**
         * Notify when brush strokes are asynchronously read back from storage.
         * @param strokes List of strokes that form the painting
         */
        void notifyLoaded(Stack<BrushStroke> strokes);
    }
    private Stack<BrushStroke> mBrushStrokes = new Stack<>();
    private final SavedArtReadListener mListener;

    @Override
    public void modifyPicture(BrushStroke change) {
        mBrushStrokes.push(change);

    }

    @Override
    public BrushStroke getCurrentChange() {
        return mBrushStrokes.peek();
    }

    @Override
    public void undoLastChange() {
        if (!mBrushStrokes.isEmpty()) {
            mBrushStrokes.pop();
        }
    }

    @Override
    public void clear() {
        mBrushStrokes.clear();
    }

    @Override
    public void save(String name, Context context) {
        ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
        handler.saveObjectData(mBrushStrokes, name);
    }

    @Override
    public void retrieve(String name, Context context) {
        ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
        Stack<BrushStroke> strokes = (Stack<BrushStroke>) handler.readObjectData(name);

        if (strokes != null){
            mBrushStrokes = strokes;
        }

        //  new ArtReadOperation().execute(name, context);
    }

    void artLoaded() {
        mListener.notifyLoaded(mBrushStrokes);
    }

    @Override
    public Stack<BrushStroke> allStrokes() {
        return mBrushStrokes;
    }


    public TouchScreenArt(SavedArtReadListener listener) {
        mListener = listener;
    }


    //TODO:Not used. Need to figure out why it cannot be read back sometimes. Seems issue with context. Since
    // it is a 2 day project, I didn't have time to complete and test it.
    private class ArtReadOperation extends AsyncTask<Object, Void, Stack<BrushStroke>> {

        @Override
        protected Stack<BrushStroke> doInBackground(Object... handlers) {
            String name = (String) handlers[0];
            Context context  = (Context) handlers[1];
            ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
            Stack<BrushStroke> strokes = (Stack<BrushStroke>) handler.readObjectData(name);
            return strokes;
        }

        @Override
        protected void onPostExecute(Stack<BrushStroke> strokes) {
            if (strokes!= null) {
                mBrushStrokes = strokes;
                artLoaded();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
