/*
 *  
 * 
 * <company> HUST-Laboratory 411-Samsung EEG project</company>
 * 
 * <author> Tran Ngoc Linh </author>
 * 
 * <email> tranngoclinh.bk@gmail.com </email>
 * 
 * <date> Aug 26, 2014 - 3:11:37 PM </date>
 * 
 * <purpose> 
 * Class communication with SQLITE Database
 * </purpose>
 */
package com.lab411.eegmedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class DemoDatabase.
 */
public class AppDatabase extends SQLiteOpenHelper{
	
	/** The database name. */
	static String DATABASE_NAME = "Rating.db";
	
	/** The Constant DATABASE_VERSION. */
	public static final int DATABASE_VERSION = 1;
	
	public static String create = "create table SongTB (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME text , RATE INTEGER);";
	
	    //Constructor function
    /**
     * Instantiates a new demo database.
     *
     * @param context the context
     */
    public AppDatabase (Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(create);
		Log.d("DB", "Create database was successful!");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		onCreate(db);
	}
	
	/**
	 * Insert_tbgui.
	 *
	 * @param gui_name the gui_name
	 * @param gui_type the gui_type
	 */
	public void insertSong(String name, int Rate){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
	
		values.put("NAME", name);
		values.put("RATE", Rate);
		
		db.insert("SongTB", null, values);
		db.close();
	}
	
	public int GetRate(String name) {
		int result = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("Select * from SongTB where NAME =?",
				new String[] {name });
		while (cursor.moveToNext()) {
			cursor.getString(1);
			result = cursor.getInt(2);
		}
		db.close();
		return result;
	}
	
	public void deleteSong(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("SongTB", "NAME = ?",
                new String[] {name });
        db.close();
    }
	
	public int updateSong(String name,int rate) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("RATE", rate);
 
        // updating row
        int result = db.update("SongTB", values, "NAME = ?",
                new String[] { name });
        db.close();
        return result;
    }
}