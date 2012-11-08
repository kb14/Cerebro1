package com.codename51.cerebro;

import java.util.ArrayList;
import java.util.List;
import static com.codename51.cerebro.CommonUtilities.LOGIN_URL;
import static com.codename51.cerebro.CommonUtilities.LOGOUT_URL;
import static com.codename51.cerebro.CommonUtilities.CHAT_URL;
import static com.codename51.cerebro.CommonUtilities.GETUSERS_URL;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	
	private JSONParser jsonParser;
	
	int bn = 0;
    
	// constructor
		public UserFunctions(){
			jsonParser = new JSONParser();
		}
		
		
		 /**
	     * Login user into the device.
	     *
	     */
	    
	    public JSONObject loginUser (String name, String password){
	    	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("password", password));
			
			JSONObject json = jsonParser.getJSONFromUrl(LOGIN_URL, params);		
			if(jsonParser.an == 1){
				bn = 1;
			}
			return json;
			
	    }
	    
	    /**
	     * Logout user from the device.
	     *
	     */
	    
	    public JSONObject logoutUserFromServer(String serverid){
	    	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	params.add(new BasicNameValuePair("serverid", serverid));
	    	
	    	JSONObject json = jsonParser.getJSONFromUrl(LOGOUT_URL, params);
	    	if(jsonParser.an == 1){
				bn = 1;
			}
			return json;
	    	
	    }
	    
	    /**
	     * Get Online Users
	     */
	    
	    public JSONObject getUsers(){
	    	List<NameValuePair> params = new ArrayList<NameValuePair>(); 
	    	//Here goes the location stuff
	    	JSONObject json = jsonParser.makeHttpRequest(GETUSERS_URL, "GET", params);
	    	if(jsonParser.an == 1){
				bn = 1;
			}
			return json;
	    }
	    
	    
	    /**
	     * Send a chat message
	     *
	     */
	    
	    public JSONObject sendChat(String serverId, String name, String email, String regId, String message){
	    	
	    	List<NameValuePair> params = new ArrayList<NameValuePair>();
	    	
	    	params.add(new BasicNameValuePair("serverId", serverId));
	    	params.add(new BasicNameValuePair("name", name));
	    	params.add(new BasicNameValuePair("email", email));
	    	params.add(new BasicNameValuePair("regId", regId));
	    	params.add(new BasicNameValuePair("message", message));
	    	
	    	JSONObject json = jsonParser.getJSONFromUrl(CHAT_URL, params);		
			if(jsonParser.an == 1){
				bn = 1;
			}
			return json;
	    }
	    
	    
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		SqliteHandler db = new SqliteHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		SqliteHandler db = new SqliteHandler(context);
		db.resetTables();
		return true;
	}
}
