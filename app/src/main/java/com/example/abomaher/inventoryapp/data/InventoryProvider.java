package com.example.abomaher.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;


public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    private static DBHelper dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_DATA, INVENTORY);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_DATA+ "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = sqLiteDatabase.query(Contract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = Contract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(Contract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return Contract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return Contract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertHelper(Contract.CONTENT_URI, contentValues);
            default:
                throw new IllegalArgumentException("Uri Not Supported");
        }
    }

    public Uri insertHelper(Uri uri, ContentValues contentValues) {
        /*String productName = contentValues.getAsString(Contract.InventoryEntry.PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("requires a name");
        }
        Integer price = contentValues.getAsInteger(Contract.InventoryEntry.PRICE);
        if (price == null) {
            throw new IllegalArgumentException("requires a price");
        }
        Integer quantity = contentValues.getAsInteger(Contract.InventoryEntry.QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("requires a quantity");
        }
        String supplierName = contentValues.getAsString(Contract.InventoryEntry.SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("requires a supplier name");
        }
        Integer supplierNumber = contentValues.getAsInteger(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("requires a supplier name");
        }*/
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(Contract.InventoryEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int match = uriMatcher.match(uri);
        int rowsDeleted=0;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(Contract.InventoryEntry.TABLE_NAME, s, strings);
                break;
            case INVENTORY_ID:
                 s = Contract.InventoryEntry._ID +"=?";
                 strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.InventoryEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
           if (rowsDeleted!=0) {
               getContext().getContentResolver().notifyChange(uri, null);
           }
           return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                 return updateHelper(uri,contentValues,selection,selectionArgs);
            case INVENTORY_ID:
                selection = Contract.InventoryEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHelper(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateHelper(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
      /*  if (values.containsKey(Contract.InventoryEntry.PRODUCT_NAME)) {
            String productName = values.getAsString(Contract.InventoryEntry.PRODUCT_NAME);
            if (productName == null) {
                Toast.makeText(getContext(), "Wrong With Entry !!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        if (values.containsKey(Contract.InventoryEntry.PRICE)) {
            Integer price = values.getAsInteger(Contract.InventoryEntry.PRICE);
            if (price == null) {
                Toast.makeText(getContext(), "Wrong With Entry !!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        if (values.containsKey(Contract.InventoryEntry.QUANTITY)) {
            Integer quantity = values.getAsInteger(Contract.InventoryEntry.QUANTITY);
            if (quantity == null) {
                Toast.makeText(getContext(), "Wrong With Entry !!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        if (values.containsKey(Contract.InventoryEntry.SUPPLIER_NAME)) {
            String supplierName = values.getAsString(Contract.InventoryEntry.SUPPLIER_NAME);
            if (supplierName == null) {
                Toast.makeText(getContext(), "Wrong With Entry !!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }
        if (values.containsKey(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER)) {
            Integer supplierNumber = values.getAsInteger(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER);
            if (supplierNumber == null) {
                Toast.makeText(getContext(), "Wrong With Entry !!", Toast.LENGTH_LONG).show();
                return 0;
            }
        }*/
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowUpdated = database.update(Contract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }
}
