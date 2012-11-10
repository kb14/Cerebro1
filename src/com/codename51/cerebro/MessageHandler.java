package com.codename51.cerebro;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageHandler extends SQLiteOpenHelper {
	
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "cerebro_messages";

	// Message table name
	private static final String TABLE_MESSAGE = "message";
	
	//Message table column names
	private static final String KEY_ID = "id";
	private static final String KEY_TOID = "toid";
	private static final String KEY_DIR = "dir";
	private static final String KEY_MESSAGE = "message";
	
	public MessageHandler(Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TOID + " INTEGER,"
				+ KEY_DIR + " INTEGER,"
				+ KEY_MESSAGE + " TEXT" + ")";
				
		db.execSQL(CREATE_LOGIN_TABLE);
	}
	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);

		// Create tables again
		onCreate(db);
	}
	
	/**
	 * Storing a message
	 **/
	public void storeMessage(int toid, int dir, String message){
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TOID, toid); 
		values.put(KEY_DIR, dir);
		values.put(KEY_MESSAGE, message); 
		
		// Inserting Row
		db.insert(TABLE_MESSAGE, null, values);
		db.close(); // Closing database connection
	}
	
	/**
	 * Getting relevant chat history
	 */
	public ArrayList<String> getChatHistory(int toid, String name){
		
		ArrayList<String> chatHistory = new ArrayList<String>();
		 
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MESSAGE, new String[] { KEY_ID, KEY_TOID, KEY_DIR, KEY_MESSAGE},
        		KEY_TOID + "=?", new String[] {String.valueOf(toid)}, null, null, null, null);
        
        // looping through all rows and adding to list
        if(cursor.moveToFirst()){
        	do {
        		int dir = Integer.parseInt(cursor.getString(2));
        		String message = cursor.getString(3);
        		if(dir == 0){
        			
        			message = "ME: " + message;
        		}
        		else{
        			message =  name + ": " + message;
        		}
        		chatHistory.add(message);
        		
        	} while(cursor.moveToNext()) ;
        	
        }
        cursor.close();
        db.close();
        
        return chatHistory;
	}
	
	
	/**
	 * Re-create database
	 * Delete all tables and create them again
	 * */
	public void resetTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_MESSAGE, null, null);
		db.close();
	}

}
