package com.example.android.imgkeeper;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

import static android.R.attr.dial;
import static android.R.attr.type;
import static android.R.attr.y;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity {
    Uri selectedImageUri;
    String  mCurrentPhotoPath;
    ListView listView;
    ArrayList<Myimg> image;
    ImgAdapter imgAdapter ;
    Button btn2;
    EditText editText;
    String caption;
    static int number =0;
    DataBaseHandler db;
    int position=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.MANAGE_DOCUMENTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},123);

        }
        db=new DataBaseHandler(this,this.getContentResolver());
        listView = (ListView) findViewById(R.id.photo_list);

        btn2 = (Button) findViewById(R.id.add_btn);
        editText = (EditText) findViewById(R.id.caption);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caption = editText.getText().toString();
                db.addImg(new Myimg(selectedImageUri,caption,getContentResolver()));
                image = db.getAllImg();
                imgAdapter = new ImgAdapter(getApplicationContext(), image);
                listView.setAdapter(imgAdapter);
                editText.setText("");
                editText.setVisibility(View.INVISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                number++;
            }
        });

        Button btn = (Button) findViewById(R.id.add_photo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
        image = db.getAllImg();
        if(image.size()>0) {
            Log.v("Test","Hoasdasd");
            imgAdapter = new ImgAdapter(getApplicationContext(), image);
            listView.setAdapter(imgAdapter);
        }

        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                db.deleteImg(image.get(info.position));
                imgAdapter.remove(image.get(info.position));
                imgAdapter.notifyDataSetChanged();
                listView.setAdapter(imgAdapter);
                return true;
            case R.id.crop:
                position=info.position;
                performCrop(image.get(info.position).getUri());

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void performCrop(Uri picUri) {
        CropImage.activity(picUri)
                .start(this);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED){

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Note");
                builder.setMessage("Allow Permissions To Use This App");
                builder.setNeutralButton("Got It!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }

        }

    }

    private void setImage(){
        String items[] = {"Take Photo","Choose From Gallery","Close"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0)
                    cameraIntent();
                else if (which==1)
                    galleryIntent();
                else  if (which==2)
                    dialog.dismiss();
            }
        });

        builder.show();
    }

    private void cameraIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.v("Error","IO Exception");

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }



    }

    private void galleryIntent(){
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }

    }



    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.getData() != null) {
                selectedImageUri = data.getData();
            }
            if (requestCode == 0 && resultCode == RESULT_OK) {


                File f = new File(mCurrentPhotoPath);
                selectedImageUri = Uri.fromFile(f);

                editText.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);


            }

            if (requestCode == 1) {
                editText.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    image.get(position).setmUri(resultUri);
                    db.update(image.get(position));
                    image = db.getAllImg();
                    imgAdapter=new ImgAdapter(getApplicationContext(),image);
                    listView.setAdapter(imgAdapter);
                }

            }

        }
    }






    private File createImageFile() throws IOException {


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ImgKeeper");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

         /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );*/


        mCurrentPhotoPath = mediaFile.getAbsolutePath();
        return mediaFile;
    }


    public static void reduce(){
        number--;
    }





}






