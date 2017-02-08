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
    private Blob mImage;

    public Inventory(String productName, String quantity, String price,Blob image) {
        mProductName = productName;
        mQuantity = quantity;
        mPrice = price;
        mImage = image;
    }

    public Blob getmImage() {
        return mImage;
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
