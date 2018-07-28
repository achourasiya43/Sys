package com.tech.ac.sys.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vicky on 24-Sep-17.
 */

public class FevouriteDB extends SQLiteOpenHelper {

    Context mContext;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FEVOURITEDB";
    public static final String FEV_TABLE = "fevorite";

    // Contacts Table Columns names
    public static final String ID = "id";
    public static final String countryname = "countryname";

    public FevouriteDB(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE2 = "CREATE TABLE " + FEV_TABLE + "("
                + ID + " TEXT PRIMARY KEY,"
                + countryname + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
