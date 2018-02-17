package com.example.android.runvolution.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.runvolution.history.HistoryItem;

import java.util.Date;

/**
 * Created by iqbal on 16/02/18.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();

    /* Database Information */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "runvolution_db";

    /* History Table */
    private static final String HISTORY_TABLE_TABLENAME = "history";
    public static final String HISTORY_COLUMN_ID = "_id";
    public static final String HISTORY_COLUMN_DATE = "date";
    public static final String HISTORY_COLUMN_STEPS = "steps";
    public static final String HISTORY_COLUMN_DISTANCE = "distance";
    private static final String[] HISTORY_TABLE_COLUMNS = {
            HISTORY_COLUMN_ID,
            HISTORY_COLUMN_DATE,
            HISTORY_COLUMN_STEPS,
            HISTORY_COLUMN_DISTANCE
    };
    private static final String HISTORY_TABLE_CREATE =
            "CREATE TABLE " + HISTORY_TABLE_TABLENAME + " ( " +
                    HISTORY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    HISTORY_COLUMN_DATE + " INTEGER, " +
                    HISTORY_COLUMN_STEPS + " INTEGER, " +
                    HISTORY_COLUMN_DISTANCE + " INTEGER )";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HISTORY_TABLE_CREATE);

        fillDatabaseWithData(db);
    }

    // TODO: Remove this hardcoded data later.
    private void fillDatabaseWithData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        for (int i = 0; i < 100; i++) {
            values.put(HISTORY_COLUMN_ID, i);
            values.put(HISTORY_COLUMN_DATE, new Date().getTime());
            values.put(HISTORY_COLUMN_STEPS, (i*100));
            values.put(HISTORY_COLUMN_DISTANCE, 0);
            db.insert(HISTORY_TABLE_TABLENAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_TABLENAME);
        onCreate(db);
    }

    public HistoryItem query(int position) {
        String query = " SELECT * FROM " + HISTORY_TABLE_TABLENAME +
                " ORDER BY " + HISTORY_COLUMN_DATE + " ASC " +
                "LIMIT " + position + ", 1";

        HistoryItem entry = new HistoryItem();

        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        try (Cursor cursor = mReadableDB.rawQuery(query, null)) {
            cursor.moveToFirst();
            entry = getHistoryItemFromCursor(cursor);
        } catch (Exception e) {
            Log.d(TAG, "query: " + e.getMessage());
        }

        return entry;
    }

    public long getHistoryCount() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, HISTORY_TABLE_TABLENAME);
    }

    private HistoryItem getHistoryItemFromCursor(Cursor cursor) {
        HistoryItem entry = new HistoryItem();
        int idIdx = cursor.getColumnIndex(HISTORY_COLUMN_ID);
        int dateIdx = cursor.getColumnIndex(HISTORY_COLUMN_DATE);
        int stepsIdx = cursor.getColumnIndex(HISTORY_COLUMN_STEPS);
        int distanceIdx = cursor.getColumnIndex(HISTORY_COLUMN_DISTANCE);

        entry.setId(cursor.getInt(idIdx));
        entry.setDate(new Date(cursor.getLong(dateIdx)));
        entry.setSteps(cursor.getInt(stepsIdx));
        entry.setDistance(cursor.getFloat(distanceIdx));
        return entry;
    }
}
