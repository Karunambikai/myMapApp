package com.example.gmapsapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "checkINDB.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME_CHECK_IN = "CheckInItem";
	public static final String TABLE_NAME_RECORDING = "Recordings";
	public static final String TABLE_NAME_IMAGE = "Images";

	public static final String CI_ID = "Id";
	public static final String CI_TITLE = "Title";
	public static final String CI_DESCRIPTION = "Description";
	public static final String CI_RECORD = "Record";
	public static final String CI_LOCATION = "Location";
	public static final String CI_LAT = "Lat";
	public static final String CI_LNG = "Lng";
	public static final String CI_TYPE = "Type";
	public static final String CI_DATE = "Date";

	public static final String ID = "Id";
	public static final String NAME = "TitleName";
	public static final String RPATH = "RPath";

	public static final String IMG_ID = "Id";
	public static final String IMG_TITLE = "TitleName";
	public static final String IMG_PATH = "Path";

	private static final String TABLE_CREATE_CHECK_IN = "CREATE TABLE "
			+ TABLE_NAME_CHECK_IN + " (" + CI_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CI_TITLE + " TEXT, "
			+ CI_DESCRIPTION + " TEXT, " + CI_RECORD + " TEXT, " + CI_LOCATION
			+ " TEXT, " + CI_LAT + " TEXT, " + CI_LNG + " TEXT, " + CI_TYPE
			+ " TEXT, " + CI_DATE + " TEXT " + ")";

	private static final String TABLE_CREATE_RECORDING = "CREATE TABLE "
			+ TABLE_NAME_RECORDING + " (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + RPATH
			+ " TEXT " + ")";

	private static final String TABLE_CREATE_IMAGE = "CREATE TABLE "
			+ TABLE_NAME_IMAGE + " (" + IMG_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMG_TITLE + " TEXT, "
			+ IMG_PATH + " TEXT " + ")";

	public OpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_RECORDING);
		db.execSQL(TABLE_CREATE_IMAGE);
		db.execSQL(TABLE_CREATE_CHECK_IN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECORDING);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_IMAGE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_CHECK_IN);
		onCreate(db);
	}
}
