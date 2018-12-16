package erdlingproductions.anynote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import erdlingproductions.anynote.data.NoteContract.NoteEntry;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "anynote.db";

    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NOTES_NAME + "("
            + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + NoteEntry.COLUMN_LONGTEXT + " TEXT, "
            + NoteEntry.COLUMN_DATECREATED + " TEXT NOT NULL, "
            + NoteEntry.COLUMN_DATEFINISHED + " TEXT, "
            + NoteEntry.COLUMN_LASTUPDATED + " TEXT, "
            + NoteEntry.COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 0, "
            + NoteEntry.COLUMN_DONE + " INTEGER NOT NULL DEFAULT 0, "
            + NoteEntry.COLUMN_ARCHIVED + " INTEGER NOT NULL DEFAULT 0, "
            + NoteEntry. COLUMN_CATEGORY  + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

