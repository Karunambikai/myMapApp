package com.example.gmapsapp.db;

import java.util.ArrayList;
import java.util.List;

import com.example.gmapsapp.Check_In_Item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSource {

	SQLiteOpenHelper dbhelper;
	SQLiteDatabase database;
	Context x;

	@SuppressWarnings("unused")
	public DataSource(Context context) {
		dbhelper = new OpenHelper(context);
		x = context;
		open();
	}

	public void open() {
		if (database == null)
			database = dbhelper.getWritableDatabase();
	}

	public void close() {
		dbhelper.close();
	}

	public void Enter_Record(String name, String path) {
		ContentValues content = new ContentValues();
		content.put(OpenHelper.NAME, name);
		content.put(OpenHelper.RPATH, path);
		database.insert(OpenHelper.TABLE_NAME_RECORDING, null, content);
	}

	public void Enter_Check_In(String title, String description,
			String location, String record, String lat, String lng,
			String type, String date) {
		ContentValues content = new ContentValues();
		content.put(OpenHelper.CI_TITLE, title);
		content.put(OpenHelper.CI_DESCRIPTION, description);
		content.put(OpenHelper.CI_RECORD, record);
		content.put(OpenHelper.CI_LOCATION, location);
		content.put(OpenHelper.CI_LAT, lat);
		content.put(OpenHelper.CI_LNG, lng);
		content.put(OpenHelper.CI_TYPE, type);
		content.put(OpenHelper.CI_DATE, date);
		database.insert(OpenHelper.TABLE_NAME_CHECK_IN, null, content);
	}

	public void Enter_Image_Record(String title, String path) {
		ContentValues content = new ContentValues();
		content.put(OpenHelper.IMG_TITLE, title);
		content.put(OpenHelper.IMG_PATH, path);
		database.insert(OpenHelper.TABLE_NAME_IMAGE, null, content);
	}

	public int getCount() {
		Cursor cursor = database.rawQuery("SELECT * FROM "
				+ OpenHelper.TABLE_NAME_CHECK_IN, null);
		return cursor.getCount();
	}

	public int getImageCount() {
		Cursor cursor = database.rawQuery("SELECT * FROM "
				+ OpenHelper.TABLE_NAME_IMAGE, null);
		return cursor.getCount();
	}

	public List<String> List_Images(String title) {
		Cursor cursor = null;
		cursor = database.rawQuery("SELECT * FROM "
				+ OpenHelper.TABLE_NAME_IMAGE + " WHERE "
				+ OpenHelper.IMG_TITLE + " = '" + title + "'", null);
		List<String> list = new ArrayList<String>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				list.add(cursor.getString(cursor
						.getColumnIndex(OpenHelper.IMG_PATH)));
			}
		}
		return list;
	}

	public List<Check_In_Item> List_All_Check_Ins(String type) {
		Cursor cursor = null;
		if (type == "All") {
			cursor = database.rawQuery("SELECT " + OpenHelper.CI_TITLE + ","
					+ OpenHelper.CI_LAT + "," + OpenHelper.CI_LNG + ","
					+ OpenHelper.CI_TYPE + " FROM "
					+ OpenHelper.TABLE_NAME_CHECK_IN, null);
		} else {
			cursor = database.rawQuery("SELECT " + OpenHelper.CI_TITLE + ","
					+ OpenHelper.CI_LAT + "," + OpenHelper.CI_LNG + ","
					+ OpenHelper.CI_TYPE + " FROM "
					+ OpenHelper.TABLE_NAME_CHECK_IN + " WHERE "
					+ OpenHelper.CI_TYPE + " = '" + type + "'", null);
		}
		List<Check_In_Item> list = new ArrayList<Check_In_Item>();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				Check_In_Item item = new Check_In_Item();
				item.setTitle(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_TITLE)));
				item.setLat(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_LAT)));
				item.setLng(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_LNG)));
				item.setType(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_TYPE)));
				list.add(item);
			}
		}
		return list;
	}

	public Check_In_Item getItem(String title) {
		Cursor cursor = null;
		cursor = database.rawQuery("SELECT * FROM "
				+ OpenHelper.TABLE_NAME_CHECK_IN + " WHERE "
				+ OpenHelper.CI_TITLE + " = '" + title + "'", null);
		Check_In_Item item = new Check_In_Item();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				item.setTitle(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_TITLE)));
				item.setLat(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_LAT)));
				item.setLng(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_LNG)));
				item.setType(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_TYPE)));
				item.setDescription(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_DESCRIPTION)));
				item.setLocation(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_LOCATION)));
				item.setRecord(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_RECORD)));
				item.setDate(cursor.getString(cursor
						.getColumnIndex(OpenHelper.CI_DATE)));
			}
		}
		return item;
	}

	/*
	 * public ArrayList<TripItem> TripDetails() {
	 * 
	 * ArrayList<TripItem> tripItemList = new ArrayList<>(); Cursor cursor =
	 * database .rawQuery("SELECT fromAdd,time FROM Taxi", null); if
	 * (cursor.getCount() > 0) { while (cursor.moveToNext()) { TripItem item =
	 * new TripItem(); item.setFromadd(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.FROM))); item.setTime(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.TIME))); tripItemList.add(item); } } return
	 * tripItemList; }
	 * 
	 * public TripItem SingleTripDetails(int index) {
	 * 
	 * TripItem item = new TripItem(); Cursor cursor =
	 * database.rawQuery("SELECT * FROM Taxi where Id = '" + index + "'", null);
	 * if (cursor.getCount() > 0) { while (cursor.moveToNext()) {
	 * item.setName(cursor.getString(cursor .getColumnIndex(OpenHelper.NAME)));
	 * item.setContact(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.CONTACT)));
	 * item.setFromadd(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.FROM))); item.setToadd(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.TO))); item.setCar(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.CAR))); item.setTime(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.TIME)));
	 * item.setFromlat(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.FROMLAT)));
	 * item.setFromlng(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.FROMLNG)));
	 * item.setTolat(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.TOLAT)));
	 * item.setTolng(cursor.getString(cursor
	 * .getColumnIndex(OpenHelper.TOLNG))); } } return item; }
	 */
}