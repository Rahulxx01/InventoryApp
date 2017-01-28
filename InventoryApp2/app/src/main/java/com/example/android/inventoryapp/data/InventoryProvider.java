package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.SwipeDismissBehavior;
import android.util.Log;

import com.example.android.inventoryapp.Inventory;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.inventoryapp.EditorActivity.LOG_TAG;
import static com.example.android.inventoryapp.data.InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;

/**
 * Created by RAHUL YADAV on 24-01-2017.
 */

public class InventoryProvider extends ContentProvider {
    private InventoryDbHelper mDbhelper;
    private static final int INVENT = 100;
    private static final int INVENT_ID = 101;
    private static final UriMatcher sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = "Inventory provider";

    static {
        sUriMather.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.TABLE_NAME, INVENT);
        sUriMather.addURI(InventoryContract.InventoryEntry.CONTENT_AUTHORITY, InventoryContract.InventoryEntry.TABLE_NAME + "/#", INVENT_ID);
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
        int match = sUriMather.match(uri);
        cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME,projection,selection,selectionArgs,
                null,null,sortOrder);
               cursor.setNotificationUri(getContext().getContentResolver(),uri);

         return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMather.match(uri);
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
        final  int match = sUriMather.match(uri);
        switch (match){
            case INVENT:

                return insertInventory(uri,contentValues);

        }
        return null;
    }
    private Uri insertInventory(Uri uri,ContentValues contentValues){
        SQLiteDatabase db = mDbhelper.getWritableDatabase();
        long newRowID = db.insert(InventoryContract.InventoryEntry.TABLE_NAME,null,contentValues);
        if(newRowID == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
