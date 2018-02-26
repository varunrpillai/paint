package com.example.varamach.simplepaintapp.model;

import android.content.Context;
import android.os.AsyncTask;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by varamach on 2/23/18.
 * Singleton Model that stores the list of save art names.
 */

public class SavedArt implements Serializable {

    private LinkedList<String> mArtNames = new LinkedList<>();
    private static SavedArt mModel = null;

    private SavedArt(Context context) {
        ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
        LinkedList<String> artNames = (LinkedList<String>) handler.readObjectData("artnames");
        if (artNames!= null) {
            mArtNames = artNames;
        }
        //  new ReadOperation().execute(context);
    }

    public static SavedArt getInstance(Context context)
    {
        if (mModel == null)
            mModel = new SavedArt(context);

        return mModel;
    }


    //Add a new art name
    public boolean addArt(String artName) {
        if (mArtNames.contains(artName)) {
            return false;
        }
        mArtNames.add(artName);
        return true;
    }

    public void removeArt(String artName) {
        mArtNames.remove(artName);
    }

    public LinkedList<String> getArtNames() {
        return mArtNames;
    }

    /**
     * Persist all of the art names.
     */
    public void persistArtNames(Context context) {
        ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
        handler.saveObjectData(mArtNames, "artnames");
    }

    //TODO:Not used. Need to figure out why it cannot be read back sometimes. Seems issue with context.
    // Since it is a 2 day project, I didn't have time to complete and test it.
    private class ReadOperation extends AsyncTask<Context, Void, LinkedList<String>> {

        @Override
        protected LinkedList<String> doInBackground(Context... handlers) {
            Context context  = handlers[0];
            ObjectPersistanceHandler handler = new ObjectPersistanceHandler(context);
            LinkedList<String> artNames = (LinkedList<String>) handler.readObjectData("artnames");
            return artNames;
        }

        @Override
        protected void onPostExecute(LinkedList<String> artNames) {
            if (artNames!= null) {
                mArtNames = artNames;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
