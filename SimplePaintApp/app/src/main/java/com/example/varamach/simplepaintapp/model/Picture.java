package com.example.varamach.simplepaintapp.model;

import android.content.Context;

import java.util.Stack;

/**
 * Created by varamach on 2/23/18.
 * Generalization of a picture.
 */

public interface Picture<T> {

    /**
     * Modifies the picture with given change
     * @param change The change done to the picture.
     */
    void modifyPicture(T change);

    /**
     * Get the ongoing change.
     */
    T getCurrentChange();

    /**
     * Undo the last change done on the picture.
     */
    void undoLastChange();

    /**
     * Clears the picture.
     */
    void clear();

    /**
     * Saves the picture
     * @param name Name used for uniquely identify the picture.
     */
    void save(String name, Context context);

    /**
     *  Retrieve the picture with given name and context.
     * @param name
     * @param context
     */
    void retrieve(String name, Context context);

    /**
     * retrieve all strokes associated with the picture.
     * @return strokes
     */
    Stack<T> allStrokes();
}
