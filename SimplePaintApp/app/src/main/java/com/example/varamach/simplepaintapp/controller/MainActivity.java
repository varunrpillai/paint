package com.example.varamach.simplepaintapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.varamach.simplepaintapp.R;
import com.example.varamach.simplepaintapp.model.SavedArt;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by varamach on 2/23/18.
 *
 * Main Activity handles a view which allows to create an artwork, show saved artworks and option to
 * open or delete saved items. It also handles a model which stores the list of saved artworks.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener {


    private final static int ACTIVITY_ID = 100;

    private ListView mMainListView ;
    private ArrayAdapter<String> mListAdapter ;
    private ArrayList<String> mSelectedForDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup main layout
        setContentView(R.layout.activity_main);


        //Setup drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Setup the navigation and the layout when drawer is opened
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Setup 'Add Art' Fab button
        FloatingActionButton addArtButton = findViewById(R.id.addArt);
        addArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                startActivityForResult(intent, ACTIVITY_ID);
            }
        });


        //Restore item selected for delete.
        if (savedInstanceState != null) {
            mSelectedForDelete = savedInstanceState.getStringArrayList("selectedForDelete");
        } else {
            mSelectedForDelete = new ArrayList<>();
        }

        //Retrieve/Restore saved art names
        LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
        refreshHelperText(artNames);

        //Main list view
        mMainListView = findViewById(R.id.main_list_view);
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, artNames);
        mMainListView.setAdapter( mListAdapter );
        mMainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mMainListView.setMultiChoiceModeListener(this);
        mMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String  paintName    = (String) mMainListView.getItemAtPosition(position);

                // Show Activity
                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                intent.putExtra("paintName", paintName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save item selected for delete
        outState.putStringArrayList("selectedForDelete", mSelectedForDelete);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Added to handle new art creation.
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case ACTIVITY_ID : {
                if (resultCode == Activity.RESULT_OK) {
                    boolean added = data.getBooleanExtra(getString(R.string.saved), false);
                    if (added) {
                        //When new one added, see if we need to hide the information textview.
                        LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
                        refreshHelperText(artNames);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Save the art names.
        SavedArt.getInstance(this).persistArtNames(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            //Show About dialog
            new AlertDialog.Builder(this)
                    .setMessage(R.string.about_message)
                    .setTitle(R.string.about_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //CAB Handlers
    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        if (checked) {
            mSelectedForDelete.add(mListAdapter.getItem(position));
        } else {
            mSelectedForDelete.remove(mListAdapter.getItem(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.listitem_cab, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete:
                //Remove selected list items
                for (String item : mSelectedForDelete) {
                    SavedArt.getInstance(this).removeArt(item);
                }

                //See if helper text needs to be shown
                LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
                refreshHelperText(artNames);

                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    private void refreshHelperText( LinkedList<String> artNames) {
        TextView textView = findViewById(R.id.main_text_view);
        textView.setVisibility(artNames == null || artNames.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mSelectedForDelete.clear();
    }

}
