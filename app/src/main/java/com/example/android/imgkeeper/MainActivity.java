package com.example.android.imgkeeper;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.type;
import static android.os.Build.VERSION_CODES.M;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends AppCompatActivity {
    Uri selectedImageUri;
    String  mCurrentPhotoPath;
    ListView listView;
    ArrayList<Myimg> image;
    ImgAdapter imgAdapter;
    Button btn2;
    EditText editText;
    String caption;
    static int number =0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn2 = (Button) findViewById(R.id.add_btn);
        editText = (EditText) findViewById(R.id.caption);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caption = editText.getText().toString();

                imgAdapter.add(new Myimg(selectedImageUri,caption));
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
        image = new ArrayList<>();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Image_Prefs",MODE_PRIVATE);
        number = preferences.getInt("number",0);
        Log.v("Main",number+"");
        for(int i=1;i<=number;i++){
            Uri uri = Uri.parse(preferences.getString("uri"+i,""));
            Log.v("Main",uri.toString());
            String caption = preferences.getString("caption"+i,"");
            image.add(new Myimg(uri, caption));

        }
        imgAdapter = new ImgAdapter(getApplicationContext(),image);
        listView = (ListView) findViewById(R.id.photo_list);
        listView.setAdapter(imgAdapter);



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
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Image_Prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int i=1;i<=number;i++){
            editor.putString("uri"+i,imgAdapter.getItem(i-1).getUri().toString());
            Log.v("Main",imgAdapter.getItem(i-1).getUri().toString());
            editor.putString("caption"+i,imgAdapter.getItem(i-1).getmCaption());
        }
        editor.putInt("number",number);
        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(data.getData() != null){
                selectedImageUri = data.getData();
            }
            if (requestCode == 0 && resultCode == RESULT_OK) {


                File f = new File(mCurrentPhotoPath);
                selectedImageUri = Uri.fromFile(f);
                editText.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);


            }

            if (requestCode == 1)
            {
                editText.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
            }

        }

    }



    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public static void reduce(){
        number--;
    }





}






