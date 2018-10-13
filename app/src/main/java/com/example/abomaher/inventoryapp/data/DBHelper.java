package com.example.abomaher.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String COMMA =" , ";
    private static final int DATA_BASE_VERSION = 1;
    private static final String DATABASE_NAME ="inventory.db";
    private static final String CREATE_SQL_DATABASE ="CREATE TABLE "+ Contract.InventoryEntry.TABLE_NAME+" ( "
            +Contract.InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT"+COMMA
            +Contract.InventoryEntry.PRODUCT_NAME+" TEXT NOT NULL"+COMMA
            +Contract.InventoryEntry.PRICE+" INTEGER NOT NULL DEFAULT 0"+COMMA
            +Contract.InventoryEntry.QUANTITY+" INTEGER NOT NULL DEFAULT 0"+COMMA
            +Contract.InventoryEntry.SUPPLIER_NAME+" TEXT"+COMMA
            +Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER+" INTEGER);";
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
