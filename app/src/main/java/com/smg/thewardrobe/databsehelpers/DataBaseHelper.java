package com.smg.thewardrobe.databsehelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "WARDROBE_INFO.DB";

    // Table Names
    public static final String TOPWEAR_TABLE_NAME = "WARDROBE_TOPS";
    public static final String BOTTOMWEAR_TABLE_NAME = "WARDROBE_BOTTOMS";
    public static final String FAVORITES_TABLE_NAME = "WARDROBE_FAVORITES";


    // Table columns
    public static final String TOPWEAR_ID = "_id";
    public static final String TOP_IMAGE = "top_iamge";
    public static final String BOTTOMWEAR_ID = "_id";
    public static final String BOTTOM_IMAGE = "bottom_iamge";
    public static final String FAVORITE_ID = "_id";
    public static final String FAV_TOP_IMAGE_ID = "top_iamge_id";
    public static final String FAV_BOTTOM_IMAGE_ID = "bottom_iamge_id";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    /*private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT NOT NULL, " + DESC + " TEXT);";*/

    private static final String CREATE_TOPWEAR_TABLE = "create table " + TOPWEAR_TABLE_NAME + " (" + TOPWEAR_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOP_IMAGE + " BLOB NOT NULL);";

    private static final String CREATE_BOTTOMWEAR_TABLE = "create table " + BOTTOMWEAR_TABLE_NAME + " (" + BOTTOMWEAR_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BOTTOM_IMAGE + " BLOB NOT NULL);";

    private static final String CREATE_FAVORITES_TABLE = "create table " + FAVORITES_TABLE_NAME + " (" + FAVORITE_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FAV_TOP_IMAGE_ID + " LONG, " + FAV_BOTTOM_IMAGE_ID + " LONG);";


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TOPWEAR_TABLE);
        db.execSQL(CREATE_BOTTOMWEAR_TABLE);
        db.execSQL(CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TOPWEAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BOTTOMWEAR_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME);
        onCreate(db);
    }


}
