package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String TAG = "DBHelper";

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "bh_hostess_event.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EVENTS.TABLE_CREATE);
        Log.d(TAG, EVENTS.TABLE_CREATE);

        db.execSQL(CLIENTS.TABLE_CREATE);
        Log.d(TAG, CLIENTS.TABLE_CREATE);

        db.execSQL(USERS.TABLE_CREATE);
        Log.d(TAG, USERS.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    ///////////////// Table interfaces //////////////////////////////
    public interface CLIENTS {
        String TABLE_NAME = "clients";

        //public final static String ID = BaseColumns._ID;
        String ID = BaseColumns._ID;
        String NAME = "name";
        String PHONE = "phone";
        String MEMO = "memo";
        String LAST_EVENT = "last_event";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER NOT NULL PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL, " +
                        PHONE + " TEXT NOT NULL, " +
                        MEMO + " TEXT NOT NULL, " +
                        LAST_EVENT + " INTEGER NOT NULL DEFAULT 0" +
                        ");" +
                        " CREATE INDEX idx_name ON " + TABLE_NAME +
                        "(" + NAME + ");" +
                        " CREATE INDEX idx_phone ON " + TABLE_NAME +
                        "(" + PHONE + ");";
        public static final String[] COLUMNS = {
                ID, NAME, PHONE,MEMO,LAST_EVENT
        };
    }

    public interface USERS {
        String TABLE_NAME = "users";

        String ID = BaseColumns._ID;
        String NAME = "name";
        String TYPE = "type";
        String USERNAME = "username";
        String PASSWORD = "password";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER NOT NULL PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL, " +
                        TYPE + " INTEGER NOT NULL, " +
                        USERNAME + " TEXT NOT NULL, " +
                        PASSWORD + " TEXT NOT NULL " +
                        ");";
        public static final String[] COLUMNS = {
                ID, NAME, TYPE, USERNAME, PASSWORD
        };
    }

    public interface EVENTS {
        String TABLE_NAME = "events";

        String ID = BaseColumns._ID;
        String TYPE = "type";
        String CLIENT_ID = "client_id";
        String CLIENT_NAME = "client_name";
        String CLIENT_PHONE = "client_phone";
        String TM_START = "tm_start";
        String TM_END = "tm_end";
        String TM_CREATE = "tm_create";
        String TM_UPDATE = "tm_update";
        String USER_ID = "user";
        String MEMO = "memo";
        String TBL = "tbl";
        String GUESTS = "guests";
        String GUESTS_EXTRA = "guests_extra";
        String STATUS = "status";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER NOT NULL PRIMARY KEY, " +
                        TYPE + " INTEGER NOT NULL, " +
                        CLIENT_ID + " INTEGER NOT NULL, " +
                        CLIENT_NAME + " TEXT NOT NULL, " +
                        CLIENT_PHONE + " TEXT NOT NULL, " +
                        TM_START + " INTEGER NOT NULL, " +
                        TM_END + " INTEGER NOT NULL, " +
                        TM_CREATE + " INTEGER NOT NULL, " +
                        TM_UPDATE + " INTEGER NOT NULL, " +
                        USER_ID + " INTEGER NOT NULL, " +
                        TBL + " INTEGER NOT NULL DEFAULT 0, " +
                        GUESTS + " INTEGER NOT NULL, " +
                        GUESTS_EXTRA + " INTEGER NOT NULL DEFAULT 0, " +
                        STATUS + " INTEGER NOT NULL, " +
                        MEMO + " TEXT NOT NULL " +
                        ");"+
                        " CREATE INDEX idx_name ON " + TABLE_NAME +
                        "(" + TM_START + ");"+
                        " CREATE INDEX idx_name ON " + TABLE_NAME +
                        "(" + CLIENT_ID + ");";
        public static final String[] COLUMNS = {
                ID, TYPE, CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, TM_START, TM_END,TABLE_CREATE, TM_UPDATE, USER_ID, TBL, GUESTS, GUESTS_EXTRA, STATUS, MEMO
        };
    }
}
