package erdlingproductions.anynote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import android.text.TextWatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import erdlingproductions.anynote.data.NoteContract.NoteEntry;
import erdlingproductions.anynote.utilityClasses.DialogKeyListener;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // Identifier for the data loader
    private static final int LOADER_INITIAL = 0;
    private static final int LOADER_SINGLE_ITEM = 1;
    private Uri singleItemUri;

    // Callback for the loader manager
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;

    // Adapter for the ListView
    NoteCursorAdapter CursorAdapter;

    // Declaration of the dialog "dialogDetail"
    Dialog dialogDetail;

    // Declaration of the views of the dialog "dialogDetail"
    ImageView ivDone;
    ImageView ivClose;
    ImageView ivDelete;
    ImageView ivArchive;
    TextView etTitle;
    TextView etLongtext;
    ConstraintLayout clDialogDetail;

    // dpWidth will be used to set the width of the dialog to xx%
    float dpWidth;

    // Declaration of the DialogKeyListener to check the back button for clicks
    DialogKeyListener dialogKeyListener = new DialogKeyListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Get display width in dp to later set the dialog views width to XX% of the screen width
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels; // displayMetrics.density;

        // Initialize the detail dialog
        dialogDetail = new Dialog(this);
        // The dialog can not be canceled by clicking outside of the dialog
        dialogDetail.setCanceledOnTouchOutside(false);

        // Initialize the context of the dialogKeyListener
        dialogKeyListener.setContext(this);

        // Initialize the Callbacks for the loader manager for the single item
        callbacks = this;

        // Set up FAB to open the dialog for a new entry
        FloatingActionButton fabAddItem = findViewById(R.id.fab);

        // Set up the click listener for the FAB
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reset the uri, so that in the save method it can be differeniated, whether its a new or an existing entry
                singleItemUri = null;

                // Set up the dialogs views
                setUpDialogViews();

                // Hide the delete and archive views, as they are not required when entering a new item
                ivDelete.setVisibility(View.GONE);
                ivArchive.setVisibility(View.GONE);
            }
        });

        // Find the ListView which will be populated with the data
        ListView itemListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no data yet (until the loader finishes) so pass in null for the Cursor.
        CursorAdapter = new NoteCursorAdapter(this, null);
        itemListView.setAdapter(CursorAdapter);

        // Setup the item click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create a uri for the single item
                singleItemUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);

                // Set up the dialogs views
                setUpDialogViews();

                // Start the loader for the single item
                // restartLoader is used, because onCreateLoader has to fetch new data
                getLoaderManager().restartLoader(LOADER_SINGLE_ITEM, null, callbacks);
            }
        });

        // Kick off the loader for the initial list view
        getLoaderManager().initLoader(LOADER_INITIAL, null, this);
    }


    public void setUpDialogViews() {

        // Attach the dialogKeyListener to the dialog to detect the back-press
        dialogDetail.setOnKeyListener(dialogKeyListener);

        // Load up the detail dialog
        dialogDetail.setContentView(R.layout.dialog_detail);
        dialogDetail.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogDetail.show();

        // Bind the views in the dialog
        ivDone = dialogDetail.findViewById(R.id.ivDone);
        ivClose = dialogDetail.findViewById(R.id.ivClose);
        ivDelete = dialogDetail.findViewById(R.id.ivDelete);
        ivArchive = dialogDetail.findViewById(R.id.ivArchive);
        etTitle = dialogDetail.findViewById(R.id.etTitle);
        etLongtext = dialogDetail.findViewById(R.id.etLongtext);
        clDialogDetail = dialogDetail.findViewById(R.id.dialogDetailConstraintLayout);


        // dialogDetail is set to 90% Screenwidth
        clDialogDetail.setMinimumWidth((int) Math.round(dpWidth * 0.9));
        clDialogDetail.setMaxWidth((int) Math.round(dpWidth * 0.9));


        // Set up the onClickListener for the close button
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checkStatus checks, if data has been entered; if not, close the dialog
                dialogKeyListener.checkStatus(dialogDetail);
            }
        });

        // Set up the onClickListener for the delete button
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The ConfirmationDialog handles, if the item will be deleted or not by asking the user
                showDeleteConfirmationDialog();
            }
        });

        // Set up the onClickListener for the delete button
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the saveItem method
                saveItem();
            }
        });


        // attach addTextChangedListeners, so that if the text is changed in the dialog,
        // the user has to confirm when closing the dialog without saving
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                // not used
            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
                // not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                dialogKeyListener.setItemHasChangedTrue();
            }
        });

        etLongtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                // not used
            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
                // not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                dialogKeyListener.setItemHasChangedTrue();
            }
        });
    }


    // Creation of the options menu on the top right of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Listener for the clicked element in the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Dummy Data for quick testdata
    private void insertDummyItem() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_TITLE, "Testeintrag");
        values.put(NoteEntry.COLUMN_LONGTEXT, "Das ist ein Testeintrag");
        values.put(NoteEntry.COLUMN_DATECREATED, "29.10.2018");
        values.put(NoteEntry.COLUMN_PRIORITY, "1");
        values.put(NoteEntry.COLUMN_DONE, "false");
        values.put(NoteEntry.COLUMN_ARCHIVED, "false");
        values.put(NoteEntry.COLUMN_CATEGORY, "Tasks Rgbg");


        // Receive the new content URI that will allow us to access the items data in the future
        Uri newUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);
    }


    private void saveItem() {
        // Read from input fields, use trim to eliminate leading or trailing white space
        String itemTitle = etTitle.getText().toString().trim();
        String itemLongtext = etLongtext.getText().toString().trim();

        // Check if data was entered
        if (singleItemUri != null && TextUtils.isEmpty(itemTitle) && TextUtils.isEmpty(itemLongtext)) {
            // no items were entered, dismiss dialog and show Toast
            dialogDetail.dismiss();
            Toast.makeText(this, "No item saved", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(itemTitle) && !TextUtils.isEmpty(itemLongtext)) {
            // give the title a temporary name, when no title is provided
            itemTitle = "[unnamed]";
        }

        // Get the timestamp and cast it into a format
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = dateFormat.format(date);

        //set up the ContentValues, that will be saved
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_TITLE, itemTitle);
        values.put(NoteEntry.COLUMN_LONGTEXT, itemLongtext);
        values.put(NoteEntry.COLUMN_DATECREATED, formattedDate);
        //values.put(NoteEntry.COLUMN_PRIORITY, "1");
        values.put(NoteEntry.COLUMN_DONE, "false");
        values.put(NoteEntry.COLUMN_ARCHIVED, "false");
        values.put(NoteEntry.COLUMN_CATEGORY, "Tasks Rgbg");


        // If the singleItemUri is null, add a new note, otherwise update the existing item
        if (singleItemUri == null) {
            // This is a new item, so insert a new item into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(NoteEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Adding note failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an existing item, so update the item with content URI: singleItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because singleItemUri will already identify the correct row in the database that we want to modify.
            int rowsAffected = getContentResolver().update(singleItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Updating note failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            }
        }
        dialogDetail.dismiss();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete that note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteItem() {
        // Only perform the deletion if this is an existing item.
        if (singleItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the singleItemUri content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(singleItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Deleting item failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Item succesfully deleted", Toast.LENGTH_SHORT).show();
            }
            dialogDetail.dismiss();
        }

    }


    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(NoteEntry.CONTENT_URI, null, null);
        Log.v("NoteActivity", rowsDeleted + " rows deleted from item database");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case 0:
                // Define a projection that specifies the columns from the table we care about.
                String[] projection = {
                        NoteEntry._ID,
                        NoteEntry.COLUMN_TITLE,
                        NoteEntry.COLUMN_LONGTEXT,
                        NoteEntry.COLUMN_DATECREATED,
                        NoteEntry.COLUMN_PRIORITY,
                        NoteEntry.COLUMN_DONE,
                        NoteEntry.COLUMN_ARCHIVED,
                        NoteEntry.COLUMN_CATEGORY};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this, // Parent activity context
                        NoteEntry.CONTENT_URI,        // Provider content URI to query
                        projection,                   // Columns to include in the resulting Cursor
                        null,                // No selection clause
                        null,             // No selection arguments
                        null);               // Default sort order

            case 1:
                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        singleItemUri,                  // Query the content URI for the current pet
                        null,                 // Columns to include in the resulting Cursor
                        null,                  // No selection clause
                        null,               // No selection arguments
                        null);                 // Default sort order

            default:
                Toast.makeText(getApplicationContext(), "No valid loader ID was given to onCreateLoader", Toast.LENGTH_SHORT).show();
                return null;
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case 0: // This is the initial load up of the list from the database
                // Update CursorAdapter with this new cursor containing updated item data
                CursorAdapter.swapCursor(cursor);
                break;

            case 1: // This will either add a new item or modify an existing one
                if (cursor == null || cursor.getCount() < 1) {
                    return;
                }

                // Proceed with moving to the first row of the cursor and reading data from it
                // (This should be the only row in the cursor)
                if (cursor.moveToFirst()) {
                    // Find the columns of pet attributes that we're interested in
                    int titleColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_TITLE);
                    int longtextColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_LONGTEXT);

                    // Extract out the value from the Cursor for the given column index
                    String title = cursor.getString(titleColumnIndex);
                    String longtext = cursor.getString(longtextColumnIndex);


                    // Update the views on the screen with the values from the database;
                    etLongtext.setText(longtext);


                    // If the title is "[unnamed]", so to speak empty, do not set the title
                    if (!title.equals("[unnamed]")) {
                        // Append is used instead of setText in order to get the cursor to the end of the inserted text
                        etTitle.append(title);
                    }
                }

                // Workaround: the ChangeListener gets triggerd when the laod manager is done, so here ItemHasChanged is set to false
                dialogKeyListener.setItemHasChangedFalse();

                break;
            default:
                Toast.makeText(getApplicationContext(), "No valid loader ID was given to onLoadFinished", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        CursorAdapter.swapCursor(null);
    }
}