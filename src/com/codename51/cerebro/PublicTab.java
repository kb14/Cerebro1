package com.codename51.cerebro;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PublicTab extends Activity
{
	AsyncTask<Void, Void, Void> logoutTask;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publictab);
    }
	
	/* Initiating Menu XML file (menu.xml) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.bottommenu1, menu);
        return true;
    }
    
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch (item.getItemId())
        {
        case R.id.menu_logout:
        	// Single menu item is selected do something
        	// Ex: launching new activity/screen or show alert message
        	
            
            logoutTask = new AsyncTask<Void, Void, Void>(){
            	
            	@Override
                protected Void doInBackground(Void... params) {
            		SqliteHandler lo = new SqliteHandler(getApplicationContext());
                    UserFunctions uf = new UserFunctions();
                    HashMap<String,String> user = lo.getUserDetails();
                    String serverid = user.get("serverid");
                    Log.d("SERVERID", serverid);
            		JSONObject js = uf.logoutUserFromServer(serverid);
            		try {
						Log.d("SUCCESS", js.getString("success"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		uf.logoutUser(getApplicationContext());
            		MessageHandler mh = new MessageHandler(getApplicationContext());
            		mh.resetTables();
            		return null;
            	}
            	protected void onPostExecute(Void result) {
					// dismiss the dialog after getting all restaurants
					//pDialog.dismiss();
					logoutTask = null;
					
            	}
            };
            logoutTask.execute(null, null, null);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }    

    }
}
    