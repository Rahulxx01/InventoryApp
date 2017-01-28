package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public EditText mEditProductName;
    public EditText mEditProductQuantity;
    public EditText mEditProductPrice;
    public ImageView mImageView;
    public Button mSaveButton;
    public Button mDeleteButton;
    public Button mOrderButton;
    public static final String LOG_TAG = "Editor activity";
    //THESE variables are for images//
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mEditProductName = (EditText) findViewById(R.id.edit_product_name);
        mEditProductQuantity = (EditText) findViewById(R.id.edit_quantity_name);
        mEditProductPrice = (EditText) findViewById(R.id.edit_price_name);
        mImageView = (ImageView) findViewById(R.id.product_Image);
        mSaveButton = (Button) findViewById(R.id.Save);
        mDeleteButton = (Button) findViewById(R.id.Delete);
        mOrderButton = (Button) findViewById(R.id.Order);
     //
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
                saveInventory();

                finish();
            }
        });
//        getLoaderManager().initLoader(0,null,this);
        //to delete data//
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteInventory();
                finish();
            }
        });
        //To fire the email app when order button is presses//
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //order//
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this;
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }

    public void selectImage() {
        Toast.makeText(this, "InsertImage", Toast.LENGTH_SHORT).show();
        final CharSequence[] items = {"Take Photo", "Take from Galery", "Cancel"};
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

                } else if (items[item].equals("Take from Galery")) {
                    /*if(result){
                        galleryIntent();
                    }*/

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
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.inventoryapp", photoFile);
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

    public ContentValues saveInventory() {
        //String imagePath = mCurrentPhotoPath.getBytes().toString().trim();
        String productString = mEditProductName.getText().toString().trim();
        String quantityString = mEditProductQuantity.getText().toString().trim();
        String priceString = mEditProductPrice.getText().toString().trim();

        //Initialize database//
        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
      //  Log.e(LOG_TAG,"image position"+imagePath);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, productString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        //values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE, imagePath);


       Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI,values);
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this,"Data failed",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, "Insertion succesfull",
                    Toast.LENGTH_SHORT).show();
        }
        return values;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {InventoryContract.InventoryEntry._ID
                                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME
                                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE
                                , InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY} ;

        return new CursorLoader(this,InventoryContract.InventoryEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.getCount() < 1){
            return;
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
}
