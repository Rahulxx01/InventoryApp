package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.CursorAdapter;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.name;
import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.view.View.GONE;
import static com.example.android.inventoryapp.R.id.increaseBy;
import static com.example.android.inventoryapp.R.id.price;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int quantity = 0;
    private int userquantity = 0;
    public EditText mEditProductName;
    public EditText mEditProductQuantity;
    public EditText mEditProductPrice;
    public ImageView mImageView;
    public Button mSaveButton;
    public Button mDeleteButton;
    public Button mOrderButton;
    public Button mIncrease;
    public Button mDecrease;
    public TextView mQuantityTextView;
    public Button mIncreaseBY;
    public Button mDecreaseBY;
    public static final String LOG_TAG = "Editor activity";
    //THESE variables are for images//
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    Uri currentInventoryUri;
    String mCurrentPhotoPath;
    InventoryCursorAdapter mCursorAdapter;
    boolean flag;
    public EditText mUserEditQuantity;
    public String orderString;
    private Button salesButton;

    private boolean inventoryHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            inventoryHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        mEditProductName = (EditText) findViewById(R.id.edit_product_name);
        //  mEditProductQuantity = (EditText) findViewById(R.id.edit_quantity_name);
        mEditProductPrice = (EditText) findViewById(R.id.edit_price_name);
        mImageView = (ImageView) findViewById(R.id.product_Image);
        mSaveButton = (Button) findViewById(R.id.Save);
        mDeleteButton = (Button) findViewById(R.id.Delete);
        mOrderButton = (Button) findViewById(R.id.Order);
        mIncrease = (Button) findViewById(R.id.increase);
        mDecrease = (Button) findViewById(R.id.decrease);
        mQuantityTextView = (TextView) findViewById(R.id.edit_quantity_name);
        mUserEditQuantity = (EditText) findViewById(R.id.edit_increDec);
        mIncreaseBY = (Button) findViewById(R.id.increaseBy);
        mDecreaseBY = (Button) findViewById(R.id.decreaseBy);
        salesButton = (Button) findViewById(R.id.sale);

        //getUserQuantity();


        mIncreaseBY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userquantityString = mUserEditQuantity.getText().toString();
                if (!TextUtils.isEmpty(userquantityString)) {
                    userquantity = Integer.parseInt(userquantityString);
                }

                quantity = quantity + userquantity;
                String quantityString = Integer.toString(quantity);
                mQuantityTextView.setText(quantityString);
            }
        });
        mDecreaseBY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userquantityString = mUserEditQuantity.getText().toString();
                if (!TextUtils.isEmpty(userquantityString)) {
                    userquantity = Integer.parseInt(userquantityString);
                }
                quantity = quantity - userquantity;
                if (quantity < 0) {
                    quantity = 0;
                }
                String quantityString = Integer.toString(quantity);
                mQuantityTextView.setText(quantityString);
            }
        });


        Intent intent = getIntent();
        currentInventoryUri = intent.getData();

        mEditProductName.setOnTouchListener(mTouchListener);
        mEditProductPrice.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);


        if (currentInventoryUri == null) {
            setTitle("Add an inventory");
            mOrderButton.setVisibility(GONE);
            mDeleteButton.setVisibility(GONE);
        } else {
            setTitle("Edit the inventory");
            getLoaderManager().initLoader(0, null, this);


        }

        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                String quantityString = Integer.toString(quantity);
                mQuantityTextView.setText(quantityString);
            }

        });
        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity--;
                if (quantity < 0) {
                    quantity = 0;
                }
                String quantityString = Integer.toString(quantity);
                mQuantityTextView.setText(quantityString);

            }
        });
        //
        flag = true;
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dispatchTakePictureIntent();
                selectImage();
            }
        });
        //To save the data//
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false) {
                    finish();
                } else {
                    saveInventory();
                }


                finish();
            }
        });
//        getLoaderManager().initLoader(0,null,this);
        //to delete data//
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deleteInventory();
                showDeleteConfirmationDialog();

            }
        });
        //To fire the email app when order button is presses//
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //order//
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this;
                intent.putExtra(Intent.EXTRA_SUBJECT, "INVENTORY INFORMATION: ");
                intent.putExtra(Intent.EXTRA_TEXT, orderString);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!inventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        if (inventoryHasChanged) {
            showUnsavedChangesDialog(discardButtonClickListener);
        }

    }

    private void showUnsavedChangesDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton("DISCARD", discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void selectImage() {
        Toast.makeText(this, "InsertImage", Toast.LENGTH_SHORT).show();
        final CharSequence[] items = {"Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("ADD a photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                //boolean result = Utility.checkPermission(EditorActivity.this);
                if (items[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                    // if(result){
                    //   cameraIntent();
                    //}

                } else if (items[item].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            //Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(LOG_TAG, "Error in creating file" + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
            //the startActivityForResult() method is protected by a condition that calls resolveActivity(),
            // which returns the first activity component that can handle the intent.
            // Performing this check is important because if you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //The Android Camera application encodes the photo in the return Intent delivered to onActivityResult()
            // as a small Bitmap in the extras, under the key "data".
            // The following code retrieves this image and displays it in an ImageView.
            // Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            galleryAddPic();
            setPic();
        }
    }

    //Once you decide the directory for the file, you need to create a collision-resistant file name.
    // You may wish also to save the path in a member variable for later use.
    // Here's an example solution in a method that returns a unique file name for a new photo using a date-time stamp:
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e(LOG_TAG, "createImagefile");
        return image;
    }

    //The following example method demonstrates
    // how to invoke the system's media scanner to add your photo to the Media Provider's database,
    // making it available in the Android Gallery application and to other apps.
    private void galleryAddPic() {
        Log.e(LOG_TAG, "galleryADDpic" + mCurrentPhotoPath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPremultiplied = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        mImageView.setImageBitmap(bitmap);
    }

    public void saveInventory() {
        String productName = mEditProductName.getText().toString().trim();
        String productQuantity1 = mQuantityTextView.getText().toString().trim();
        String productPrice1 = mEditProductPrice.getText().toString().trim();

        //      int productQuantity =  Integer.parseInt(productQuantity1);
//        int productPrice  = Integer.parseInt(productPrice1);
        Drawable imageView = mImageView.getDrawable();


        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        byte[] imageInByte = stream.toByteArray();
        if (currentInventoryUri == null && TextUtils.isEmpty(productName)
                && TextUtils.isEmpty(productPrice1) && TextUtils.isEmpty(productQuantity1)) {
            flag = false;
            return;
        }
        int productQuantity = 0;
        int productPrice = 0;
        if (!TextUtils.isEmpty(productQuantity1)) {
            productQuantity = Integer.parseInt(productQuantity1);

        }
        if (!TextUtils.isEmpty(productPrice1)) {

            productPrice = Integer.parseInt(productPrice1);
        }

        // ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        // Create database helper
        InventoryDbHelper mDbhelper = new InventoryDbHelper(this);

        SQLiteDatabase db = mDbhelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        contentValues.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE, imageInByte);

        if (currentInventoryUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving Inventory", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "Inventory saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentInventoryUri, contentValues, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "no rows updated",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "update successfull",
                        Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[] = {InventoryContract.InventoryEntry._ID
                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME
                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY
                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE
                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE};
        return new CursorLoader(this, currentInventoryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);
            String name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String quantity1 = Integer.toString(cursor.getInt(quantityColumnIndex));
            String price = Integer.toString(cursor.getInt(priceColumnIndex));
            byte[] image = cursor.getBlob(imageColumnIndex);
            Bitmap imageView = BitmapFactory.decodeByteArray(image, 0, image.length);

            mEditProductName.setText(name);
            mQuantityTextView.setText(quantity1);
            mEditProductPrice.setText(price);
            mImageView.setImageBitmap(imageView);
            orderString = "NAME : " + name + ".\nQuantity : " + quantity1 + ".\nPrice : " + price + "\t$.";
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mEditProductName.setText("");
        mQuantityTextView.setText("");
        mEditProductPrice.setText("");
        mImageView.setImageBitmap(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteInventory() {
        int rowsDeleted = 0;
        if (currentInventoryUri != null) {
            rowsDeleted = getContentResolver().delete(currentInventoryUri, null, null);
        }
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();

    }
}
