package erdlingproductions.anynote.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class NoteContract {
    private NoteContract(){}

    public static final String CONTENT_AUTHORITY = "erdlingproductions.anynote";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NOTE = "note";

    public final static class NoteEntry implements BaseColumns {

        public final static String TABLE_NOTES_NAME = "notes";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_LONGTEXT = "longtext";
        public final static String COLUMN_DATECREATED = "timecreated";
        public final static String COLUMN_DATEFINISHED = "timefinished";
        public final static String COLUMN_LASTUPDATED = "lastupdated";
        public final static String COLUMN_PRIORITY = "priority";
        public final static String COLUMN_DONE = "done";
        public final static String COLUMN_ARCHIVED = "archived";
        public final static String COLUMN_CATEGORY = "category";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTE);


        //The MIME type of the {@link #CONTENT_URI} for a list of items.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        // The MIME type of the {@link #CONTENT_URI} for a single item.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
    }
}