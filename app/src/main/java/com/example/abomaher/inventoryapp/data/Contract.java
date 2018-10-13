package com.example.abomaher.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
    public final static String CONTENT_AUTHORITY = "com.example.abomaher.inventoryapp";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_DATA = "inventory";
    public static Uri  CONTENT_URI =Uri.withAppendedPath(BASE_CONTENT_URI,PATH_DATA);
    public static final class InventoryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID =BaseColumns._ID;
        public final static String PRODUCT_NAME = "productName";
        public final static String PRICE  = "price";
        public final static String QUANTITY  = "quantity";
        public final static String SUPPLIER_NAME  = "supplierName";
        public final static String SUPPLIER_PHONE_NUMBER   = "supplierPhoneNumber";
    }}
