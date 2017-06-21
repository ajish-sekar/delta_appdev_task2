package com.example.android.imgkeeper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ajish on 17-06-2017.
 */

public class ImgAdapter extends ArrayAdapter<Myimg> {
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


        imageView.setImageURI(currentMyimg.getUri());

        TextView textView = (TextView) listItemView.findViewById(R.id.pic_caption);
        textView.setText(currentMyimg.getmCaption());

        Button btn = (Button) listItemView.findViewById(R.id.rem_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(currentMyimg);
                MainActivity.reduce();

            }
        });


        return listItemView;
    }






}
