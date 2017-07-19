package com.example.android.imgkeeper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

import static android.R.attr.action;
import static android.R.attr.id;

/**
 * Created by Ajish on 17-07-2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "imageManager";
    private static final String TABLE_IMAGE = "imagw";
    private static final String KEY_ID = "id";
    private static final String KEY_URI = "uri";
    private static final String KEY_CAPTION = "caption";
    private ContentResolver mcr;
    public DataBaseHandler(Context context, ContentResolver cr) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mcr = cr;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_URI + " TEXT NOT NULL,"
                + KEY_CAPTION + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(db);
    }

    public void addImg(Myimg img){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URI, img.getUri().toString());
        values.put(KEY_CAPTION, img.getmCaption());
        db.insert(TABLE_IMAGE, null, values);
        db.close();

    }



    public void deleteImg(Myimg img){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGE, KEY_ID + " = ?",
                new String[] { img.getId()+"" });
        db.close();

    }

    public ArrayList<Myimg> getAllImg(){

        ArrayList<Myimg> imgList = new ArrayList<Myimg>();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String uri = cursor.getString(cursor.getColumnIndex(KEY_URI));
                String caption = cursor.getString(cursor.getColumnIndex(KEY_CAPTION));
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                Myimg myimg = new Myimg(Uri.parse(uri),caption,mcr);
                myimg.setId(id);
                imgList.add(myimg);
            } while (cursor.moveToNext());
        }

        return imgList;

    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_IMAGE);
    }
    public void update(Myimg myimg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URI, myimg.getUri().toString());
        values.put(KEY_CAPTION, myimg.getmCaption());
        db.update(TABLE_IMAGE,values,KEY_ID+"="+myimg.getId(),null);
        db.close();
    }
}
