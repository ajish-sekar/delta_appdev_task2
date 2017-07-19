package com.example.android.imgkeeper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.R.attr.layout_height;
import static android.R.attr.layout_width;

/**
 * Created by Ajish on 17-06-2017.
 */

public class ImgAdapter extends ArrayAdapter<Myimg> {

    DataBaseHandler db;
    public ImgAdapter(Context context, ArrayList<Myimg> image){
        super(context, 0, image);
    }

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        final Myimg currentMyimg = getItem(position);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.img_view);

        if(currentMyimg.getMbitmap()==null) {
            imageView.setImageURI(currentMyimg.getUri());
        }else{
            imageView.setImageBitmap(currentMyimg.getMbitmap());
        }
        TextView textView = (TextView) listItemView.findViewById(R.id.pic_caption);

        textView.setText(currentMyimg.getmCaption());

       /* Button btn = (Button) listItemView.findViewById(R.id.rem_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DataBaseHandler(getContext());
                db.deleteImg(currentMyimg);
                remove(currentMyimg);
                MainActivity.reduce();

            }
        });
        <Button
        android:id="@+id/rem_btn"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:text="X"/>
        */

        return listItemView;
    }






}
