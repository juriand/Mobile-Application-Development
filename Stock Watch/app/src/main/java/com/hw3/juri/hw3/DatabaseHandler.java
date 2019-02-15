package com.hw3.juri.hw3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockDB";
    private static final String TABLE_NAME = "StockTable";

    private static final String SYMBOL = "Symbol";
    private static final String COMPANY = "Company";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public HashMap<String, String> loadStocks() {
        Log.d(TAG, "loadStocks: START");
        HashMap<String, String> stockList = new HashMap<String, String>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL, COMPANY}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                stockList.put(symbol, company);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");
        return stockList;
    }

    public boolean queryStock(String symbol){
        String selection = "SYMBOL = ?";
        String[] selectionArgs = new String[]{symbol};
        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{SYMBOL}, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            if(cursor.getCount() > 0){
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }
        }
         return false;
    }

    public void addStock(Stock stock) {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: " + key);
    }

    public void deleteStock(String name) {
        Log.d(TAG, "deleteStock: " + name);

        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);

                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void shutDown() {
        database.close();
    }
}