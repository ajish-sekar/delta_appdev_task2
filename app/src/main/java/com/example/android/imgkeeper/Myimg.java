package com.example.android.imgkeeper;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

import static android.R.attr.bitmap;


/**
 * Created by Ajish on 17-06-2017.
 */

public class Myimg {
    private Uri mUri;
    private String mCaption;
    private int id;
    private Bitmap mbitmap;
    private ContentResolver mcr;


    public Myimg(Uri uri, String caption, ContentResolver cr)
    {
        mCaption = caption;
        mUri = uri;
        mcr = cr;
        mbitmap=uriToBitmap();
    }

    public Uri getUri(){
        return mUri;
    }

    public String getmCaption() {
        return mCaption;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap uriToBitmap(){
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mcr, mUri);
        }catch (IOException e){
            Log.v("Error","IO");
        }
        return bitmap;
    }

    public void setMbitmap(Bitmap mbitmap) {
        this.mbitmap = mbitmap;
    }

    public Bitmap getMbitmap() {
        return mbitmap;
    }

    public void setCr(ContentResolver cr) {
        this.mcr = cr;
    }

    public ContentResolver getCr() {
        return mcr;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }
}
