package com.ravencoin.tools.sqlite;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 9/25/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ravencoin.presenter.entities.AddressItem;
import com.ravencoin.tools.manager.BRReportsManager;
import com.ravencoin.tools.util.BRConstants;
import com.ravencoin.tools.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddressDataSource implements DataSourceInterface {
    private static final String TAG = AddressDataSource.class.getName();

    List<OnDataChanged> onDataChangedListeners = new ArrayList<>();

    // Database fields
    private SQLiteDatabase database;
    private final RSQLiteHelper dbHelper;
    private final String[] allColumns = {
            RSQLiteHelper.ADDRESS_TITLE,
            RSQLiteHelper.ADDRESS,
            RSQLiteHelper.ADDRESS_TIME_STAMP,
            RSQLiteHelper.ADDRESS_ISO,
    };

    private static AddressDataSource instance;

    public static AddressDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new AddressDataSource(context);
        }
        return instance;
    }

    public AddressDataSource(Context context) {
        dbHelper = RSQLiteHelper.getInstance(context);
    }

    public void putAddress(Context app, String iso, String address, String title) {
        if (address == null || title == null) {
            Log.e(TAG, "putAddress: failed");
            return;
        }

        try {
            database = openDatabase();
            database.beginTransaction();
            int failed = 0;

            ContentValues values = new ContentValues();

            if (Utils.isNullOrEmpty(address) || Utils.isNullOrEmpty(title)) {
                failed++;
            } else {
                values.put(RSQLiteHelper.ADDRESS, address);
                values.put(RSQLiteHelper.ADDRESS_TITLE, title);
                values.put(RSQLiteHelper.ADDRESS_ISO, iso.toUpperCase());
                database.insertWithOnConflict(RSQLiteHelper.ADDRESS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            if (failed != 0) Log.e(TAG, "putAddress: failed:");
            database.setTransactionSuccessful();
            for (OnDataChanged list : onDataChangedListeners) if (list != null) list.onChanged();
        } catch (Exception ex) {
            Log.e(TAG, "putAddress: failed: ", ex);
            BRReportsManager.reportBug(ex);

            //Error in between database transaction
        } finally {
            database.endTransaction();
            closeDatabase();
        }
    }

    public void deleteAllAddresses(Context app, String iso) {
        try {
            database = openDatabase();
            database.delete(RSQLiteHelper.ADDRESS_TABLE_NAME, RSQLiteHelper.ADDRESS_ISO + " = ?", new String[]{iso.toUpperCase()});
            for (OnDataChanged list : onDataChangedListeners) if (list != null) list.onChanged();
        } finally {
            closeDatabase();
        }
    }

    public List<AddressItem> getAllAddresses(Context app, String iso) {

        List<AddressItem> addresses = new ArrayList<>();
        Cursor cursor = null;
        try {
            database = openDatabase();

            cursor = database.query(RSQLiteHelper.ADDRESS_TABLE_NAME, allColumns, RSQLiteHelper.ADDRESS_ISO + " = ? COLLATE NOCASE",
                    new String[]{iso.toUpperCase()}, null, null, "\'" + RSQLiteHelper.ADDRESS_TIME_STAMP + "\'");

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                AddressItem address = cursorToAddress(cursor);
                addresses.add(address);
                cursor.moveToNext();
            }
            // make sure to close the cursor
        } finally {
            if (cursor != null)
                cursor.close();
            closeDatabase();
        }
        Log.e(TAG, "getAllCurrencies: size:" + addresses.size());
        return addresses;
    }

    private void printTest() {
        Cursor cursor = null;
        try {
            database = openDatabase();
            StringBuilder builder = new StringBuilder();

            cursor = database.query(RSQLiteHelper.ADDRESS_TABLE_NAME,
                    allColumns, null, null, null, null, null);
            builder.append("Total: " + cursor.getCount() + "\n");
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                AddressItem ent = cursorToAddress(cursor);
                builder.append("address: " + ent.address + ", title: " + ent.title + ", iso: " + ent.iso + "\n");
                cursor.moveToNext();
            }
            Log.e(TAG, "printTest: " + builder.toString());
        } finally {
            if (cursor != null)
                cursor.close();
            closeDatabase();
        }
    }

    private AddressItem cursorToAddress(Cursor cursor) {
        return new AddressItem(cursor.getString(cursor.getColumnIndex(RSQLiteHelper.ADDRESS_ISO)),
                cursor.getString(cursor.getColumnIndex(RSQLiteHelper.ADDRESS_TITLE)),
                cursor.getString(cursor.getColumnIndex(RSQLiteHelper.ADDRESS)));
    }

    @Override
    public SQLiteDatabase openDatabase() {
//        if (mOpenCounter.incrementAndGet() == 1) {
        // Opening new database
        if (database == null || !database.isOpen())
            database = dbHelper.getWritableDatabase();
        dbHelper.setWriteAheadLoggingEnabled(BRConstants.WAL);
//        }
//        Log.d("Database open counter: ",  String.valueOf(mOpenCounter.get()));
        return database;
    }

    @Override
    public void closeDatabase() {
//        if (mOpenCounter.decrementAndGet() == 0) {
//            // Closing database
//        database.close();
//
//        }
//        Log.d("Database open counter: " , String.valueOf(mOpenCounter.get()));
    }

    public void addOnDataChangedListener(OnDataChanged list) {
        if (!onDataChangedListeners.contains(list)) {
            onDataChangedListeners.add(list);
        }
    }

    public interface OnDataChanged {
        void onChanged();
    }
}