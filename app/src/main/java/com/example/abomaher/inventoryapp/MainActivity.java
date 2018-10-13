package com.example.abomaher.inventoryapp;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abomaher.inventoryapp.data.Contract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    InventoryAdapter cursorAdapter;
    View emptyView;
    private static final int INVENTORY_LOADER = 0;
    String LOG_TAG = MainActivity.class.getSimpleName();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        emptyView = findViewById(R.id.empty_view);
        cursorAdapter = new InventoryAdapter(this, null);
        recyclerView.setAdapter(cursorAdapter);
        getSupportLoaderManager().initLoader(INVENTORY_LOADER, null, MainActivity.this);
    }

    public void onProductClick(long id) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        Uri currentProductUri = ContentUris.withAppendedId(Contract.CONTENT_URI, id);
        intent.setData(currentProductUri);
        startActivity(intent);
    }
    public void onBuyNowClick(long id, int quantity) {
        Uri currentProductUri = ContentUris.withAppendedId(Contract.CONTENT_URI, id);
        quantity--;
        ContentValues values = new ContentValues();
        values.put(Contract.InventoryEntry.QUANTITY, quantity);
        int rowsEffected = getContentResolver().update(currentProductUri, values, null, null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {Contract.InventoryEntry._ID,
                Contract.InventoryEntry.PRODUCT_NAME,
                Contract.InventoryEntry.QUANTITY,
                Contract.InventoryEntry.PRICE};
        return new CursorLoader(this,
                Contract.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);

        } else {
            emptyView.setVisibility(View.GONE);
        }
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void deleteAllData() {
        int rowsDeleted = getContentResolver().delete(Contract.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from database");
    }

    public void insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(Contract.InventoryEntry.PRODUCT_NAME, "Mobile");
        values.put(Contract.InventoryEntry.PRICE, 96);
        values.put(Contract.InventoryEntry.QUANTITY, 2);
        values.put(Contract.InventoryEntry.SUPPLIER_NAME, "Mahmoud");
        values.put(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER, 01020417600);
        Uri uri = getContentResolver().insert(Contract.CONTENT_URI, values);
    }
}
