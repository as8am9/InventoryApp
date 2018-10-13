package com.example.abomaher.inventoryapp;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.abomaher.inventoryapp.data.Contract;

public class InventoryAdapter extends CursorRecyclerAdapter<InventoryAdapter.ViewHolder> {

    private MainActivity activity = new MainActivity();

    public InventoryAdapter(MainActivity context, Cursor c) {
        super(context, c);
        this.activity = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView priceTextView;
        protected TextView quantityTextView;
        protected TextView availableTextView;
        protected TextView buy;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.textName);
            priceTextView = (TextView) itemView.findViewById(R.id.textPrice);
            quantityTextView = (TextView) itemView.findViewById(R.id.textQuantity);
            buy = (TextView) itemView.findViewById(R.id.buy);
            availableTextView = (TextView) itemView.findViewById(R.id.text_available);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        final long id;
        final int mQuantity;

        id = cursor.getLong(cursor.getColumnIndex(Contract.InventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.InventoryEntry.QUANTITY);
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        mQuantity = quantity;
        viewHolder.nameTextView.setText(productName);
        viewHolder.priceTextView.setText(productPrice);
        viewHolder.quantityTextView.setText(String.valueOf(quantity));
        if (quantity == 0) {
            viewHolder.quantityTextView.setVisibility(View.GONE);
            viewHolder.availableTextView.setTextColor(activity.getResources().getColor(R.color.red));
            viewHolder.availableTextView.setText("Sold Out");
        } else {
            viewHolder.quantityTextView.setVisibility(View.VISIBLE);
            viewHolder.quantityTextView.setTextColor(activity.getResources().getColor(R.color.lightDarkLine));
            viewHolder.availableTextView.setTextColor(activity.getResources().getColor(R.color.lightDarkLine));
            viewHolder.availableTextView.setText("Avilable");
        }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onProductClick(id);
            }
        });

        viewHolder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuantity > 0) {
                    activity.onBuyNowClick(id, mQuantity);
                } else {
                    Toast.makeText(activity,"Sold Out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}