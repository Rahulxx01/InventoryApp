package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;

import static com.example.android.inventoryapp.R.id.quantity;

/**
 * Created by RAHUL YADAV on 26-01-2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_list_item,parent,false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameTextView = (TextView)view.findViewById(R.id.product_name);
        TextView quntityTextView = (TextView) view.findViewById(R.id.price);
        TextView priceTextView = (TextView) view.findViewById(quantity);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);

        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);

        productNameTextView.setText(name);
        quntityTextView.setText(quantity);
        priceTextView.setText(price);


    }
}
