package com.example.android.inventoryapp;

import android.graphics.Bitmap;

import java.sql.Blob;

/**
 * Created by RAHUL YADAV on 18-01-2017.
 */

public class Inventory {
    private String mProductName;
    private String mQuantity;
    private String mPrice;
    private Bitmap mImagepath;

    public Inventory(String productName, String quantity, String price,Bitmap image) {
        mProductName = productName;
        mQuantity = quantity;
        mPrice = price;
        mImagepath = image;
    }

    public Bitmap getmImage() {
        return mImagepath;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getmProductName() {
        return mProductName;
    }
}
