package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;

import static android.R.attr.name;
import static com.example.android.inventoryapp.R.id.price;
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
        TextView quntityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button salesButton = (Button) view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        int imageByteColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);
        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int  quantity = cursor.getInt(quantityColumnIndex);
//        byte[] imageByte = cursor.getBlob(imageByteColumnIndex);
        ///String price = Integer.toString(price1);
        //String quantity = Integer.toString(quantity1);
        //Bitmap image  = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);

        productNameTextView.setText(name);
        quntityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(String.valueOf(price));


    }
}
