package com.example.playitsafe.Bodyguard;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactsBodyguard";

    // Contacts table name
    private static final String TABLE_CONTACTS = "Bodyguard";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADD = "addFrom";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER NOT NULL PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_PHOTO +" TEXT," + KEY_ADD +" TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        Log.i(TAG, "onCreate");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO auto-generated method stub
        try {
            db.execSQL("DROP TABLE IF EXISTS Bodyguard");
            onCreate(db);
            throw new SQLException("Fail");
        } catch (SQLException e) {
            Log.e("DEBUG","DROP DB FAIL ");
        }

    }

}
