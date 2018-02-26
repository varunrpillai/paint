package com.example.varamach.simplepaintapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.varamach.simplepaintapp.R;
import com.example.varamach.simplepaintapp.model.BrushStroke;
import com.example.varamach.simplepaintapp.model.SavedArt;
import com.example.varamach.simplepaintapp.model.TouchScreenArt;
import com.example.varamach.simplepaintapp.view.CanvasAreaView;

import java.util.ArrayList;
import java.util.Stack;

import static android.graphics.Color.BLACK;

/**
 * Created by varamach on 2/23/18.
 *
 * Paint Activity handles a view which allows the user to paint an art, pick brush color and size,
 * and perform undo/clear/discard/save on the current artwork. It also handles a model that
 * stores the artwork information.
 */
public class PaintActivity extends AppCompatActivity implements CanvasAreaView.CanvasController, ColorPickerFragment.ColorPickerListener, SizePickerFragment.SizePickerListener, TouchScreenArt.SavedArtReadListener {

    private static final int DEFAULT_COLOR = BLACK;
    private static final int DEFAULT_BRUSH_SIZE = 25;
    private static final String PAINT_NAME = "paintName";
    public static final String PREFERENCES = "paint_pref" ;
    public static final String COLOR = "color" ;
    public static final String PROGRESS = "progress" ;
    public static final String ART_BUNDLE_ID = "art";

    //Model that handles the artwork
    private TouchScreenArt mArt;
    //currently selected brush color
    private int mSelectedColor;
    //currently selected brush size
    private int mBrushSize = DEFAULT_BRUSH_SIZE;
    //Paint configuration
    private final Paint mPaintConfig = new Paint();
    //Saved name of artwork
    private String mSavedName;

    private FloatingActionButton mBrushColorButton;
    private FloatingActionButton mBrushSizeButton;
    private CanvasAreaView mCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        //Since the scope of this project does not include handling a canvas
        //resizing on rotation, keeping it in portrait.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Retrieve an existing artwork
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mSavedName = intent.getExtras().getString(PAINT_NAME);
        }

        if (savedInstanceState != null) {
            mArt = (TouchScreenArt) savedInstanceState.getSerializable(ART_BUNDLE_ID);
        } else if (mSavedName != null && !mSavedName.isEmpty()) {
            mArt = new TouchScreenArt(this);
            mArt.retrieve(mSavedName, this);
        } else {
            mArt = new TouchScreenArt(this);
            mSavedName =  "";
        }

        mCanvasView = findViewById(R.id.canvasView);

        //Setup rightmost button that allows selection of brush color
        setupBrushColor();

        setUpBrushSize();

        //Important: Style property defaults to FILL and
        // setting it to stroke allows the expected result of tracing the finger.
        mPaintConfig.setStyle(Paint.Style.STROKE);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable (ART_BUNDLE_ID, mArt);

    }

    private void setupBrushColor() {
        //Show the last used color
        SharedPreferences sharedpreferences = this.getSharedPreferences(PaintActivity.PREFERENCES, Context.MODE_PRIVATE);
        mSelectedColor = sharedpreferences.getInt(COLOR, DEFAULT_COLOR);
        mBrushColorButton = findViewById(R.id.brushcolor);
        mBrushColorButton.setBackgroundTintList(ColorStateList.valueOf(mSelectedColor));
        //On click, open the color picker
        mBrushColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager manager = getFragmentManager();
                android.app.Fragment frag = manager.findFragmentByTag("fragment_brush_color_selection");
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }
                ColorPickerFragment alertDialogFragment = new ColorPickerFragment();
                alertDialogFragment.show(manager, "fragment_brush_color_selection");
            }
        });
    }

    private void setUpBrushSize() {

        mBrushSizeButton = findViewById(R.id.brushsize);
        //On click, open the size picker
        mBrushSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager manager = getFragmentManager();
                android.app.Fragment frag = manager.findFragmentByTag("fragment_brush_size_selection");
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }
                SizePickerFragment alertDialogFragment = new SizePickerFragment();
                alertDialogFragment.show(manager, "fragment_brush_size_selection");

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                handleExit();
                break;
            case R.id.action_undo:
                undoChange();
                break;
            case R.id.action_clear:
                clearPicture();
                break;
            case R.id.action_toggle_menu:
                mBrushColorButton.setVisibility(mBrushColorButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                mBrushSizeButton.setVisibility(mBrushSizeButton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleExit();
    }

    private void handleExit()
    {
        // Present user a dialog with three options
        // Continue Painting.
        // Save the artwork with a name.
        // Exit without saving.
        final EditText input = new EditText(this);
        input.setText(mSavedName);
        input.requestFocus();
        input.setPadding(100,50,100,50);

        DialogInterface.OnClickListener saveListener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String text = input.getText().toString();
                if (mSavedName.equals(text)) {
                    mArt.save(text, getApplicationContext());
                    finish();
                } else if (SavedArt.getInstance(getApplicationContext()).addArt(text)) {
                    mArt.save(text, getApplicationContext());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("saved", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    //TODO: The dialog is getting closed without explicit dismiss. Since it
                    // is a 2 day project, I didnt had time to complete and test it.
                    input.setHint("Duplicate Art Name");
                    input.clearComposingText();
                }
            }
        };

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Save")
                .setMessage("Please enter name for your art work.")
                .setView(input)
                .setPositiveButton(R.string.save, saveListener)
                .setNegativeButton(R.string.exit,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                            }
                        })
                .setNeutralButton(R.string.painting,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        })
                .create();
        //Show the keyboard
        if (dialog != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.show();
        }
    }


    @Override
    public void fingerTouchedAt(float x, float y) {
        //Hide the floating buttons
        mBrushColorButton.setVisibility(View.GONE);
        mBrushSizeButton.setVisibility(View.GONE);

        //Create a brush stroke and move the Path
        BrushStroke stroke = new BrushStroke(mSelectedColor,mBrushSize);
        stroke.moveTo(x,y);
        mArt.modifyPicture(stroke);
    }

    @Override
    public void fingerMovedTo(float x, float y) {
        BrushStroke stroke = mArt.getCurrentChange();
        stroke.lineTo(x,y);
    }

    @Override
    public void fingerRaised(float x, float y) {
        //Show the floating button
        mBrushColorButton.setVisibility(View.VISIBLE);
        mBrushSizeButton.setVisibility(View.VISIBLE);
    }


    public void undoChange() {
        //Ask model to undo the last change
        mArt.undoLastChange();
        //Ask view to refresh the UI.
        mCanvasView.invalidate();
    }

    private void clearPicture()
    {
        //Warn the user before clearing
        new AlertDialog.Builder(this)
                .setTitle("Clear")
                .setMessage("Do you want to clear the artwork?")
                .setPositiveButton(R.string.clear,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mArt.clear();
                                CanvasAreaView canvasView = findViewById(R.id.canvasView);
                                canvasView.invalidate();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .show();
    }

    @Override
    public void drawPicture(Canvas canvas) {
        //Draw all brush strokes of the artwork on the canvas.
        for (BrushStroke stroke: mArt.allStrokes()) {
            mPaintConfig.setColor(stroke.getColor());
            mPaintConfig.setStrokeWidth(stroke.getSizeOfBrush());
            canvas.drawPath(stroke.getPathOfStroke(), mPaintConfig);
        }
    }


    @Override
    public void onConfirmColorPick(int color) {
        mSelectedColor = color;
        mBrushColorButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void onConfirmSizePick(int size) {
        mBrushSize = size;
    }

    @Override
    public int getCurrentSize() {
        return mBrushSize;
    }

    @Override
    public void notifyLoaded(Stack<BrushStroke> strokes) {
        mCanvasView.invalidate();
    }
}
