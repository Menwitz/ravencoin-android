package com.ravencoin.tools.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = RSQLiteHelper.class.getName();
    private static RSQLiteHelper instance;

    private RSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static RSQLiteHelper getInstance(Context context) {
        if (instance == null) instance = new RSQLiteHelper(context);
        return instance;
    }

    public static final String DATABASE_NAME = "breadwallet.db"; //TODO
    private static final int DATABASE_VERSION = 15;

    /**
     * MerkleBlock table
     */
    public static final String MB_TABLE_NAME_OLD = "merkleBlockTable";
    public static final String MB_TABLE_NAME = "merkleBlockTable_v2";
    public static final String MB_COLUMN_ID = "_id";
    public static final String MB_BUFF = "merkleBlockBuff";
    public static final String MB_HEIGHT = "merkleBlockHeight";
    public static final String MB_ISO = "merkleBlockIso";

    private static final String MB_DATABASE_CREATE = "create table if not exists " + MB_TABLE_NAME + " (" +
            MB_COLUMN_ID + " integer primary key autoincrement, " +
            MB_BUFF + " blob, " +
            MB_ISO + " text DEFAULT 'RVN' , " +
            MB_HEIGHT + " integer);";

    /**
     * Transaction table
     */

    public static final String TX_TABLE_NAME_OLD = "transactionTable";
    public static final String TX_TABLE_NAME = "transactionTable_v2";
    public static final String TX_COLUMN_ID = "_id";
    public static final String TX_BUFF = "transactionBuff";
    public static final String TX_BLOCK_HEIGHT = "transactionBlockHeight";
    public static final String TX_TIME_STAMP = "transactionTimeStamp";
    public static final String TX_ISO = "transactionISO";

    private static final String TX_DATABASE_CREATE = "create table if not exists " + TX_TABLE_NAME + " (" +
            TX_COLUMN_ID + " text, " +
            TX_BUFF + " blob, " +
            TX_BLOCK_HEIGHT + " integer, " +
            TX_TIME_STAMP + " integer, " +
            TX_ISO + " text DEFAULT 'RVN' );";

    /**
     * Peer table
     */

    public static final String PEER_TABLE_NAME_OLD = "peerTable";
    public static final String PEER_TABLE_NAME = "peerTable_v2";
    public static final String PEER_COLUMN_ID = "_id";
    public static final String PEER_ADDRESS = "peerAddress";
    public static final String PEER_PORT = "peerPort";
    public static final String PEER_TIMESTAMP = "peerTimestamp";
    public static final String PEER_ISO = "peerIso";

    private static final String PEER_DATABASE_CREATE = "create table if not exists " + PEER_TABLE_NAME + " (" +
            PEER_COLUMN_ID + " integer primary key autoincrement, " +
            PEER_ADDRESS + " blob," +
            PEER_PORT + " blob," +
            PEER_TIMESTAMP + " blob," +
            PEER_ISO + "  text default 'RVN');";
    /**
     * Currency table
     */

    public static final String CURRENCY_TABLE_NAME_OLD = "currencyTable";
    public static final String CURRENCY_TABLE_NAME = "currencyTable_v2";
    public static final String CURRENCY_CODE = "code";
    public static final String CURRENCY_NAME = "name";
    public static final String CURRENCY_RATE = "rate";
    public static final String CURRENCY_ISO = "iso";//iso for the currency of exchange (BTC, RVN)

    private static final String CURRENCY_DATABASE_CREATE = "create table if not exists " + CURRENCY_TABLE_NAME + " (" +
            CURRENCY_CODE + " text," +
            CURRENCY_NAME + " text," +
            CURRENCY_RATE + " integer," +
            CURRENCY_ISO + " text DEFAULT 'RVN', " +
            "PRIMARY KEY (" + CURRENCY_CODE + ", " + CURRENCY_ISO + ")" +
            ");";

    /**
     * Address table
     */

    public static final String ADDRESS_TABLE_NAME = "addressTable";
    public static final String ADDRESS_COLUMN_ID = "_id";
    public static final String ADDRESS_TITLE = "title";
    public static final String ADDRESS = "address";
    public static final String ADDRESS_TIME_STAMP = "addressTimeStamp";
    public static final String ADDRESS_ISO = "addressISO";

    private static final String ADDRESS_DATABASE_CREATE = "create table if not exists " + ADDRESS_TABLE_NAME + " (" +
            ADDRESS_COLUMN_ID + " text, " +
            ADDRESS_TITLE + " text, " +
            ADDRESS + " text, " +
            ADDRESS_TIME_STAMP + " integer, " +
            ADDRESS_ISO + " text DEFAULT 'RVN' );";


    /**
     * Stats table
     */

    public static final String STATS_TABLE_NAME = "statsTable";
    public static final String STATS_COLUMN_ID = "_id";
    public static final String STATS_VOLUME = "volume";
    public static final String STATS_ASK = "ask";
    public static final String STATS_BID = "bid";
    public static final String STATS_TIME_STAMP = "statsTimeStamp";
    public static final String STATS_ISO = "addressISO";

    private static final String STATS_DATABASE_CREATE = "create table if not exists " + STATS_TABLE_NAME + " (" +
            STATS_COLUMN_ID + " text, " +
            STATS_VOLUME + " integer, " +
            STATS_ASK + " integer, " +
            STATS_BID + " integer, " +
            STATS_TIME_STAMP + " integer, " +
            STATS_ISO + " text DEFAULT 'RVN_BTC' );";


    @Override
    public void onCreate(SQLiteDatabase database) {
        //drop peers table due to multiple changes

        Log.e(TAG, "onCreate: " + MB_DATABASE_CREATE);
        Log.e(TAG, "onCreate: " + TX_DATABASE_CREATE);
        Log.e(TAG, "onCreate: " + PEER_DATABASE_CREATE);
        Log.e(TAG, "onCreate: " + CURRENCY_DATABASE_CREATE);
        Log.e(TAG, "onCreate: " + ADDRESS_DATABASE_CREATE);
        Log.e(TAG, "onCreate: " + STATS_DATABASE_CREATE);
        database.execSQL(MB_DATABASE_CREATE);
        database.execSQL(TX_DATABASE_CREATE);
        database.execSQL(PEER_DATABASE_CREATE);
        database.execSQL(CURRENCY_DATABASE_CREATE);
        database.execSQL(ADDRESS_DATABASE_CREATE);
        database.execSQL(STATS_DATABASE_CREATE);

//        printTableStructures(database, MB_TABLE_NAME);
//        printTableStructures(database, TX_TABLE_NAME);
//        printTableStructures(database, PEER_TABLE_NAME);
//        printTableStructures(database, CURRENCY_TABLE_NAME);

//        database.execSQL("PRAGMA journal_mode=WAL;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 13 && (newVersion >= 13)) {
            boolean migrationNeeded = !tableExists(MB_TABLE_NAME, db);
            onCreate(db); //create new db tables

            if (migrationNeeded)
                migrateDatabases(db);
        } else {
            //drop everything maybe?
//            db.execSQL("DROP TABLE IF EXISTS " + MB_TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS " + TX_TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE_NAME);
//            db.execSQL("DROP TABLE IF EXISTS " + CURRENCY_TABLE_NAME);
//            db.execSQL("PRAGMA journal_mode=WAL;");
        }
        //recreate if needed

    }

    private void migrateDatabases(SQLiteDatabase db) {
        db.beginTransaction();
        try {

            db.execSQL("INSERT INTO " + MB_TABLE_NAME + " (_id, merkleBlockBuff, merkleBlockHeight) SELECT _id, merkleBlockBuff, merkleBlockHeight FROM " + MB_TABLE_NAME_OLD);
            db.execSQL("INSERT INTO " + TX_TABLE_NAME + " (_id, transactionBuff, transactionBlockHeight, transactionTimeStamp) SELECT _id, transactionBuff, transactionBlockHeight, transactionTimeStamp FROM " + TX_TABLE_NAME_OLD);
            db.execSQL("INSERT INTO " + CURRENCY_TABLE_NAME + " (code, name, rate) SELECT code, name, rate FROM " + CURRENCY_TABLE_NAME_OLD);

            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE_NAME_OLD);//drop this table (fully refactored schema)
            db.execSQL("DROP TABLE IF EXISTS " + MB_TABLE_NAME_OLD);
            db.execSQL("DROP TABLE IF EXISTS " + TX_TABLE_NAME_OLD);
            db.execSQL("DROP TABLE IF EXISTS " + CURRENCY_TABLE_NAME_OLD);

            db.setTransactionSuccessful();
            Log.e(TAG, "migrateDatabases: SUCCESS");
        } finally {
            Log.e(TAG, "migrateDatabases: ENDED");
            db.endTransaction();
        }
    }

    public boolean tableExists(String tableName, SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void printTableStructures(SQLiteDatabase db, String tableName) {
        Log.e(TAG, "printTableStructures: " + tableName);
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        Log.e(TAG, "SQL:" + tableString);
    }

}
