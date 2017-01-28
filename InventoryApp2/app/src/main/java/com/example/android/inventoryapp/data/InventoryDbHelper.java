package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.EditorActivity;

/**
 * Created by RAHUL YADAV on 22-01-2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "storage.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE "+ InventoryContract.InventoryEntry.TABLE_NAME+" ("
                +InventoryContract.InventoryEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME+" TEXT NOT NULL,"
                +InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY+" INTEGER,"
                +InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE+" INTEGER);";


        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
