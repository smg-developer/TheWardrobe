package com.smg.thewardrobe.databsehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.smg.thewardrobe.models.FavoriteWearModel;
import com.smg.thewardrobe.models.ImageWearModel;
import com.smg.thewardrobe.utilities.ImageUtility;

import java.util.ArrayList;

public class DatabaseManipulator {
    private DataBaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DatabaseManipulator(Context c) {
        context = c;
    }

    public DatabaseManipulator open() throws SQLException {
        dbHelper = new DataBaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }
/*

    public void insertTopWear(String top_image) {

        //if(!checkIfUserInserted(userInfo.u_name)) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DataBaseHelper.TOP_IMAGE, top_image);
            database.insert(DataBaseHelper.TOPWEAR_TABLE_NAME, null, contentValue);
        //}
    }
*/

    public long insertTopWear( Bitmap imageBitmap) {
        ContentValues contentValue = new  ContentValues();
        contentValue.put(DataBaseHelper.TOP_IMAGE, ImageUtility.getBytes(imageBitmap));
        long id = database.insert( DataBaseHelper.TOPWEAR_TABLE_NAME, null, contentValue);

        return id;
    }

    public long insertBottomWear( Bitmap imageBitmap) {
        ContentValues contentValue = new  ContentValues();
        contentValue.put(DataBaseHelper.BOTTOM_IMAGE, ImageUtility.getBytes(imageBitmap));
        long id = database.insert( DataBaseHelper.BOTTOMWEAR_TABLE_NAME, null, contentValue);

        return id;
    }

    public long insertFavoriteWear(long top_image_id, long bottom_image_id) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DataBaseHelper.FAV_TOP_IMAGE_ID, top_image_id);
        contentValue.put(DataBaseHelper.FAV_BOTTOM_IMAGE_ID, bottom_image_id);
        long id = database.insert(DataBaseHelper.FAVORITES_TABLE_NAME, null, contentValue);

        return id;
    }

    public void removeFromFavorites(long fav_id){
        database.execSQL("DELETE FROM "+ DataBaseHelper.FAVORITES_TABLE_NAME + " WHERE " + DataBaseHelper.FAVORITE_ID + " = "+fav_id +"");
    }

    public ArrayList<FavoriteWearModel> getAllFavoriteWears (){

        ArrayList<FavoriteWearModel> arrayFavoriteWears = new ArrayList<>();

        String[] columns = new String[] { DataBaseHelper.FAVORITE_ID, DataBaseHelper.FAV_TOP_IMAGE_ID, DataBaseHelper.FAV_BOTTOM_IMAGE_ID};
        Cursor cursor = database.query(DataBaseHelper.FAVORITES_TABLE_NAME, columns, null, null, null, null, "_id DESC", null);

        if(cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    long id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.FAVORITE_ID));
                    long top_id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.FAV_TOP_IMAGE_ID));
                    long bottom_id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.FAV_BOTTOM_IMAGE_ID));

                    FavoriteWearModel favModel = new FavoriteWearModel(id, top_id, bottom_id);

                    arrayFavoriteWears.add(favModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        Log.d("WardrobeDB","FavoriteWear size : "+arrayFavoriteWears.size());
        return arrayFavoriteWears;

    }

    public ArrayList<ImageWearModel> getAllTopWears (){
        ArrayList<ImageWearModel> arrayTopWears = new ArrayList<>();

        String[] columns = new String[] { DataBaseHelper.TOPWEAR_ID, DataBaseHelper.TOP_IMAGE};
        Cursor cursor = database.query(DataBaseHelper.TOPWEAR_TABLE_NAME, columns, null, null, null, null, "_id DESC", null);

        if(cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    long id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.TOPWEAR_ID));
                    byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.TOP_IMAGE));

                    ImageWearModel imgModel = new ImageWearModel(id, imageBlob);

                    arrayTopWears.add(imgModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        Log.d("WardrobeDB","TopWear size : "+arrayTopWears.size());
        return arrayTopWears;

    }

    public ArrayList<ImageWearModel> getAllBottomWears (){
        ArrayList<ImageWearModel> arrayBottomWears = new ArrayList<>();

        String[] columns = new String[] { DataBaseHelper.BOTTOMWEAR_ID, DataBaseHelper.BOTTOM_IMAGE};
        Cursor cursor = database.query(DataBaseHelper.BOTTOMWEAR_TABLE_NAME, columns, null, null, null, null, "_id DESC", null);

        if(cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    long id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.BOTTOMWEAR_ID));
                    byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.BOTTOM_IMAGE));

                    ImageWearModel imgModel = new ImageWearModel(id, imageBlob);

                    arrayBottomWears.add(imgModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        Log.d("WardrobeDB","BottomWear size : "+arrayBottomWears.size());
        return arrayBottomWears;

    }

    public void getBottomWearAfterThis (int current_bottom_wear_id){

    }

    public void getRandomFavoriteWear (int favorite_id){

    }

    /*public Cursor fetchInfo() {
        String[] columns = new String[] { DataBaseHelper._ID, DataBaseHelper.USERNAME, DataBaseHelper.DESC, DataBaseHelper.DATE,
                DataBaseHelper.PROFILEPIC, DataBaseHelper.STATUS };
        //Kept Limit as 30 for the sake of simplicity as this is dummy app. And all the data will be loaded at a time.
        // We can get rid of limit by using swipe to refresh layout
        Cursor cursor = database.query(DataBaseHelper.TABLE_NAME, columns, null, null, null, null, "_id DESC", "30");

        return cursor;
    }

    public boolean checkIfUserInserted(String userName) {
        String[] columns = new String[] { DataBaseHelper._ID, DataBaseHelper.USERNAME, DataBaseHelper.DESC, DataBaseHelper.DATE,
                DataBaseHelper.PROFILEPIC, DataBaseHelper.STATUS };
        Cursor cursor = database.query(DataBaseHelper.TABLE_NAME, columns, DataBaseHelper.USERNAME + " = '"+userName.trim()+"'", null, null, null, null);

        if(cursor != null)
        {
            if(cursor.moveToFirst())
                return true;
        }
        return false;
    }

    public int updateStatus(String name, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.STATUS, status);
        int i = database.update(DataBaseHelper.TABLE_NAME, contentValues, DataBaseHelper.USERNAME + " = '"+name.trim()+"'", null);
        return i;
    }*/
}
