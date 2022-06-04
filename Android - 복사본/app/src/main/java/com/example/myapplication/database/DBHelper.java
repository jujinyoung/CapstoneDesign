package com.example.myapplication.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
    private static final String TAG = "DBHelper";

    private static DBHelper database;

    /**
     * table name for MEMO
     */
    public static final String TABLE_NAME = "DIARY";

    /**
     * version
     */
    public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    public DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 인스턴스
     */
    public SQLiteDatabase db;

    /**
     * 컨텍스트 객체
     */
    private Context context;

    /**
     * 생성자
     */
    private DBHelper(Context context) {
        this.context = context;
    }

    /**
     * 인스턴스 가져오기
     */
    public static DBHelper getInstance(Context context) {
        if (database == null) {
            database = new DBHelper(context);
        }

        return database;
    }

    /**
     * 데이터베이스 열기
     */
    public boolean open() {
        dbHelper = new DatabaseHelper(context);
//        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
        db.close();

        database = null;
    }

    public SQLiteDatabase Writedb(){
        db = dbHelper.getWritableDatabase();
        return db;
    }

    public SQLiteDatabase Readdb(){
        db = dbHelper.getReadableDatabase();
        return db;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }



    /**
     * Database Helper inner class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, "diary.db", null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {

            // TABLE_NOTE
            println("creating table [" + TABLE_NAME + "].");

            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_NAME;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // create table
            String CREATE_SQL = "create table " + TABLE_NAME + "("
                    + "  _id TEXT NOT NULL PRIMARY KEY, "
                    + "  PICTURE0 TEXT DEFAULT '', "
                    + "  FOOD0 TEXT DEFAULT '', "
                    + "  MOOD0 TEXT , "
                    + "  COMMENT0 TEXT DEFAULT '', "
                    + "  PICTURE1 TEXT DEFAULT '', "
                    + "  FOOD1 TEXT DEFAULT '', "
                    + "  MOOD1 TEXT , "
                    + "  COMMENT1 TEXT DEFAULT '', "
                    + "  PICTURE2 TEXT DEFAULT '', "
                    + "  FOOD2 TEXT DEFAULT '', "
                    + "  MOOD2 TEXT , "
                    + "  COMMENT2 TEXT DEFAULT '', "
                    + "  PICTURE3 TEXT DEFAULT '', "
                    + "  FOOD3 TEXT DEFAULT '', "
                    + "  MOOD3 TEXT , "
                    + "  COMMENT3 TEXT DEFAULT '', "
                    + "  TAN TEXT DEFAULT '', "
                    + "  DAN TEXT DEFAULT '', "
                    + "  GI TEXT DEFAULT '', "
                    + "  KCAL TEXT DEFAULT '' "
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
            }

            // create index
            String CREATE_INDEX_SQL = "create index " + TABLE_NAME + "_IDX ON " + TABLE_NAME + "("
                    + "CREATE_DATE"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }
}

