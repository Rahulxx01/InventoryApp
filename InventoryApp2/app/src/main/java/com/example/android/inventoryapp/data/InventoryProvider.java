package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import static android.R.attr.id;

public class InventoryProvider extends ContentProvider {
    private InventoryDbHelper mDbhelper;
    private static final int INVENT = 0;
    private static final int INVENT_ID = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = "Inventory provider";

    static {
        sUriMatcher.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.PATH_INVENTORY, INVENT);
        sUriMatcher.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.PATH_INVENTORY+ "/#", INVENT_ID);
    }

    // Initialize the provider and the database helper object.


    @Override
    public boolean onCreate() {
        mDbhelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbhelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENT:
                //perform on entire table//
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                // cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case INVENT_ID:
                //perfrom on single row of the table//
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                //cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknowm Uri" + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENT:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENT_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENT:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported" + uri);

        }

    }

    private Uri insertInventory(Uri uri, ContentValues contentValues) {
        // Check that the name is not null
        String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is valid
        Integer price = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {

            throw new IllegalArgumentException("Product requires valid price");
        }
        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid qunatity");
        }
        //check image
        byte[] imageInByte = contentValues.getAsByteArray(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);
        if(imageInByte == null){
            throw new IllegalArgumentException("Product requires a valid image");
        }

        // Get writeable database
        SQLiteDatabase db = mDbhelper.getWritableDatabase();
        long newRowID = db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, contentValues);
        if (newRowID == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        int rowsDeleted=0;
        SQLiteDatabase database = mDbhelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENT:
                // Delete all rows that match the selection and selection args
                rowsDeleted =  database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case INVENT_ID:
                // Delete a single row given by the ID in the URI
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                 rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

               break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int updatedRows;
        switch(match){
            case INVENT:
                updatedRows = updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            case INVENT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                updatedRows =  updateInventory(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
       return  updatedRows;

    }

    private int updateInventory(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Check that the name is not null
        String name = contentValues.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is valid
        Integer price = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null && price < 0) {

            throw new IllegalArgumentException("Product requires valid price");
        }
        // If the quantity is provided, check that it's greater than or equal to 0 kg
        Integer quantity = contentValues.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid qunatity");
        }
        //check image
        byte[] imageInByte = contentValues.getAsByteArray(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);
        if(imageInByte == null){
            throw new IllegalArgumentException("Product requires a valid image");
        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

}

