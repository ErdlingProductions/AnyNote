package erdlingproductions.anynote;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import erdlingproductions.anynote.data.NoteContract.NoteEntry;

public class NoteCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link NoteCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvNoteTitle = view.findViewById(R.id.textview_note_title);
        TextView tvNoteLongtext = view.findViewById(R.id.textview_note_longtext);
        TextView tvNoteDateCreated = view.findViewById(R.id.textview_note_date_created);

        // find the columns of note attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(NoteEntry._ID);
        int noteTitleColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_TITLE);
        int longtextColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_LONGTEXT);
        int dateCreatedColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_DATECREATED);

        // red the pet attributes from the Cursor for the current pet
        String noteTitle = cursor.getString(noteTitleColumnIndex);
        String noteLongtext = cursor.getString(longtextColumnIndex);
        String dateCreated = cursor.getString(dateCreatedColumnIndex);
        final long row = Long.parseLong(cursor.getString(idColumnIndex));

        tvNoteTitle.setText(noteTitle);
        tvNoteDateCreated.setText(dateCreated);
        // If the noteLongtext has no content, hide it, so that it does not use up real estate on the screen
        if(noteLongtext.equals("")){
            tvNoteLongtext.setVisibility(View.GONE);
        }else{
            tvNoteLongtext.setVisibility(View.VISIBLE);
            tvNoteLongtext.setText(noteLongtext);
        }
    }

}
