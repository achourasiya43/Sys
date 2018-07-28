package com.tech.ac.sys.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.ac.sys.model.AddProductInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tech.ac.sys.database.FevouriteDB.FEV_TABLE;

/**
 * Created by Vicky on 24-Sep-17.
 */

public class DBOpration {

    private FevouriteDB helper;
    private SQLiteDatabase db;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ProgressDialog progressDialog;
    private Context mContext;

    public static String TAG = DBOpration.class.getName();

    public DBOpration(Context mContext) {
        this.mContext = mContext;
        helper = new FevouriteDB(mContext,FevouriteDB.DATABASE_NAME,null,FevouriteDB.DATABASE_VERSION);
        this.db = helper.getWritableDatabase();
    }

    public void insertJson(AddProductInfo annuncedInfo , String map){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String rowId = annuncedInfo.ProductId;
        values.put(FevouriteDB.ID, rowId);
        values.put(FevouriteDB.countryname, map);

        long l = db.insert(FEV_TABLE, null, values);
        if(l == -1){
                Toast.makeText(mContext, "Already Saved in database", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Post Saved Successfully", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public ArrayList<AddProductInfo> getAllHashMap() {
        ArrayList<AddProductInfo> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + FEV_TABLE;

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, AddProductInfo> hashMap = new HashMap<>();
                Gson gson = new Gson();
                String key = cursor.getString(0);
                String value = cursor.getString(1);
                AddProductInfo annuncedInfo = gson.fromJson(value, AddProductInfo.class);
                list.add(annuncedInfo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public boolean deleteContact(String rowId){
        boolean bool = false;
        open();
        rowId = rowId.replace(" ","%20");
        int SPLASH_TIME_OUT = 800;
        final String finalRowId = rowId;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading...");
        progressDialog.setProgress(0);
        progressDialog.setMessage("please Wait");
        progressDialog.show();

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                db.beginTransaction();
                db.delete(FEV_TABLE, FevouriteDB.ID + " = ?", new String[]{finalRowId});
                db.setTransactionSuccessful();
                db.endTransaction();
                progressDialog.dismiss();

            }
        }, SPLASH_TIME_OUT);
        return bool;
    }


    public void open() {
        try {
            db = helper.getWritableDatabase();
        }
        catch (Exception e) {
            Log.e(TAG, e.toString());}
    }

    public void close() {
        try {
            db.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());}
    }
}
