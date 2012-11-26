package com.codename51.cerebro;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageHandler2 extends SQLiteOpenHelper {
	
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "cerebro_messages_public";

	// Message table name
	private static final String TABLE_MESSAGE = "message";	
	
	//Message table column names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_MESSAGE = "message";
	
	public MessageHandler2(Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Creating Tables
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," 
					+ KEY_NAME + " TEXT,"
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
		 * Storing a public message
		 **/
		public void storeMessage(String name, String message){
			
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues(); 
			values.put(KEY_NAME, name);
			values.put(KEY_MESSAGE, message); 
			
			// Inserting Row
			db.insert(TABLE_MESSAGE, null, values);
			db.close(); // Closing database connection
			
		}
		
		/**
		 * Getting relevant chat history
		 */
		public ArrayList<String> getChatHistory(){
			
			ArrayList<String> chatHistory = new ArrayList<String>();
			 
	        SQLiteDatabase db = this.getReadableDatabase();
	        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGE;
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        // looping through all rows and adding to list
	        if(cursor.moveToFirst()){
	        	do {
	        		String name = cursor.getString(1);
	        		String message = cursor.getString(2);
	        		
	        		message =  name + ": " + message;
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
