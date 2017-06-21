package com.example.android.imgkeeper;


import android.net.Uri;



/**
 * Created by Ajish on 17-06-2017.
 */

public class Myimg {
    private Uri mUri;
    private String mCaption;


    public Myimg(Uri uri, String caption)
    {
        mCaption = caption;
        mUri = uri;
    }

    public Uri getUri(){
        return mUri;
    }

    public String getmCaption() {
        return mCaption;
    }
}
