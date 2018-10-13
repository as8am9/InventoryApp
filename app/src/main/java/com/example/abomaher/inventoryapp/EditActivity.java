package com.example.abomaher.inventoryapp;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abomaher.inventoryapp.data.Contract;
import com.example.abomaher.inventoryapp.data.DBHelper;
import com.example.abomaher.inventoryapp.data.InventoryProvider;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText supplierName;
    private EditText supplierPhone;
    private TextView quantityTextView;
    private Button plusButton;
    private Button minusButton;
    private Button orderButton;
    private int quantity;
    private Uri currentProductUri;
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        supplierName = (EditText) findViewById(R.id.supplier_name);
        supplierPhone = (EditText) findViewById(R.id.supplier_phone);
        quantityTextView = (TextView) findViewById(R.id.edit_quantity_text_view);
        plusButton = (Button) findViewById(R.id.button_plus);
        minusButton = (Button) findViewById(R.id.button_minus);
        orderButton = (Button) findViewById(R.id.button_order);
        if (currentProductUri == null) {
            setTitle("Add Product");
            supplierName.setEnabled(true);
            supplierPhone.setEnabled(true);
            orderButton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Product");
            supplierName.setEnabled(false);
            supplierPhone.setEnabled(false);
            orderButton.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, EditActivity.this);
        }
        nameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        supplierName.setOnTouchListener(mTouchListener);
        supplierPhone.setOnTouchListener(mTouchListener);
        minusButton.setOnTouchListener(mTouchListener);
        plusButton.setOnTouchListener(mTouchListener);
        orderButton.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void order(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhone.getText().toString()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    public boolean saveProduct() {
        boolean allFilledOut = false;

        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String supplierNameString = supplierName.getText().toString().trim();
        String supplierPhoneString = supplierPhone.getText().toString().trim();
        String quantityString = quantityTextView.getText().toString();

        if (currentProductUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString) &&
                TextUtils.isEmpty(quantityString)) {
            allFilledOut = true;
            return allFilledOut;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Product Name Missing", Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        ContentValues values = new ContentValues();
        values.put(Contract.InventoryEntry.PRODUCT_NAME, nameString);

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Product Price Missing", Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(Contract.InventoryEntry.PRICE, priceString);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Supplier Name Missing", Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(Contract.InventoryEntry.SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, "Supplier Name Missing", Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Quantity Missing", Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }
        values.put(Contract.InventoryEntry.QUANTITY, quantityString);
        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(Contract.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (mProductHasChanged) {
                    Toast.makeText(this, "Saved",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        allFilledOut = true;
        return allFilledOut;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                Contract.InventoryEntry._ID,
                Contract.InventoryEntry.PRODUCT_NAME,
                Contract.InventoryEntry.PRICE,
                Contract.InventoryEntry.QUANTITY,
                Contract.InventoryEntry.SUPPLIER_NAME,
                Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.SUPPLIER_PHONE_NUMBER);
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sPhone = cursor.getString(sPhoneColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            nameEditText.setText(name);
            priceEditText.setText(price);
            supplierName.setText(sName);
            supplierPhone.setText(sPhone);
            quantityTextView.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        supplierName.setText("");
        supplierPhone.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteProduct() {
        if (currentProductUri != null) {
            int rowDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowDeleted == 0) {
                Toast.makeText(this, "Delete Product Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete Product Successfull", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void increment(View view) {
        quantity++;
        displayQuantity();
    }

    public void decrement(View view) {
        if (quantity == 0) {
            Toast.makeText(this, "No Less Quantity", Toast.LENGTH_SHORT).show();
        } else {
            quantity--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        quantityTextView.setText(String.valueOf(quantity));
    }
}