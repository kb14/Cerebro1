package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PrivateTab extends Activity
{
	private ArrayAdapter<CharSequence> pvtAdap;
	ListView pvtList;
	
	private static final String TAG_USERS = "users";
    private static final String TAG_NAME = "name";
    private static final String TAG_ID = "id";
    private static final String TAG_REGID = "gcm_regid";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LAT = "latitude";
    private static final String TAG_LONG = "longitude";
    JSONObject c;
    JSONObject json, ojson;
    JSONArray userArray = null;
    UserFunctions userFunctions = new UserFunctions();
    SqliteHandler db ;
    //AsyncTask
    AsyncTask<Void, Void, Void> getUsersTask;
    // Asyntask for bottom menu
    AsyncTask<Void, Void, Void> logoutTask, refreshTask;
    
    
    String cServerid="",cName="",cRegid="";
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatetab);
        
        db = new SqliteHandler(getApplicationContext());
        HashMap<String,String> user = db.getUserDetails();
        cServerid = user.get("serverid");
        cName = user.get("name");
        cRegid = user.get("regid");
        Log.d("cServerid PRIVATE.JAVA", cServerid);
        
        pvtList=(ListView) findViewById(R.id.pvt_list);
        final ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
        final ArrayList<CharSequence> only_names=new ArrayList<CharSequence>();
        
        getUsersTask = new AsyncTask<Void, Void, Void>(){
        	
        	@Override
            protected Void doInBackground(Void... params) {
		        //ojson = new JSONObject(user_json);
		        // LATITUDE LONGITUDE WILL GO AS ARGUMENT WHEN LOCATION WILL BE implemented
		        json = userFunctions.getUsers();
		        //Log.d("ALL THE USERS: PRIVATETAB", json.toString());
		        if (userFunctions.bn == 1){
		        	return null;
		        }
		        try{
		        	// Checking for SUCCESS TAG
		        	if (json.getString(KEY_SUCCESS) != null) {
						String res = json.getString(TAG_SUCCESS);
						if(Integer.parseInt(res) == 1){
							userArray = json.getJSONArray(TAG_USERS);
							 for(int i = 0; i < userArray.length(); i++){
								 c = userArray.getJSONObject(i);
								 String id = c.getString(TAG_ID);
								 String regId = c.getString(TAG_REGID);
								 String name = c.getString(TAG_NAME);
								 String lat = c.getString(TAG_LAT);
								 String lon = c.getString(TAG_LONG);
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("serverid", id);
								map.put("regId", regId);
								map.put("name", name);
								map.put("lat", lat);
								map.put("lon", lon);
								// creating new HashMap
								HashMap<String, String> map1 = new HashMap<String, String>();
								map1.put("regId", regId);
								if(!cServerid.equals(id)){
									 only_names.add(name);
									 userList.add(map);
									 Global.userList.add(map1);
								 }
							 }
							 
						}
		        	}
        		}catch (JSONException e) {
    				e.printStackTrace();
    			}
				return null; 
		        
        	}
	        @Override
            protected void onPostExecute(Void result) {
	        	// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						pvtAdap=new ArrayAdapter<CharSequence>(PrivateTab.this, R.layout.private_tab_list_item, R.id.private_item, only_names);
						pvtList.setAdapter(pvtAdap);
				        pvtList.setOnItemClickListener(new OnItemClickListener()
						  {
						    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
						    {
						    	Intent i=new Intent(getApplicationContext(), PrivateChat.class);
						    	i.putExtra("serverid", userList.get(position).get("serverid"));
						    	i.putExtra("regId", userList.get(position).get("regId"));
						    	i.putExtra("name", userList.get(position).get("name"));
						    	startActivity(i);
						    }
						  });   
					}
				});	
             }
        };
        
        getUsersTask.execute(null, null, null);
        
	}
	
	/* Initiating Menu XML file (menu.xml) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.bottommenu, menu);
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
            
        case R.id.menu_refresh:
        	final ArrayList<CharSequence> only_names = new ArrayList<CharSequence>();
        	final ArrayList<HashMap<String, String>> userList1 = new ArrayList<HashMap<String, String>>();
        	final ArrayList<HashMap<String, String>> userList2 = new ArrayList<HashMap<String, String>>();
        	refreshTask = new AsyncTask<Void, Void, Void>(){
        		@Override
                protected Void doInBackground(Void... params) {
        			json = userFunctions.getUsers();
    		        if (userFunctions.bn == 1){
    		        	return null;
    		        }
    		        try{
    		        	// Checking for SUCCESS TAG
    		        	if (json.getString(KEY_SUCCESS) != null) {
    						String res = json.getString(TAG_SUCCESS);
    						if(Integer.parseInt(res) == 1){
    							userArray = json.getJSONArray(TAG_USERS);
    							//Global.userList.clear();
    							 for(int i = 0; i < userArray.length(); i++){
    								 c = userArray.getJSONObject(i);
    								 String id = c.getString(TAG_ID);
    								 String regId = c.getString(TAG_REGID);
    								 String name = c.getString(TAG_NAME);
    								 String lat = c.getString(TAG_LAT);
    								 String lon = c.getString(TAG_LONG);
    								// creating new HashMap
    								HashMap<String, String> map = new HashMap<String, String>();
    								map.put("serverid", id);
    								map.put("regId", regId);
    								map.put("name", name);
    								map.put("lat", lat);
    								map.put("lon", lon);
    								// creating new HashMap
    								HashMap<String, String> map1 = new HashMap<String, String>();
    								map1.put("regId", regId);
    								if(!cServerid.equals(id)){
    									 only_names.add(name);
    									 userList1.add(map);
    									 userList2.add(map1);
    								 }
    							 }
    							 Global.userList = userList2;
    							 
    						}
    		        	}
            		}catch (JSONException e) {
        				e.printStackTrace();
        			}
        			return null;
        		}
        		 @Override
                 protected void onPostExecute(Void result) {
     	        	// updating UI from Background Thread
     				runOnUiThread(new Runnable() {
     					public void run() {
     						pvtAdap=new ArrayAdapter<CharSequence>(PrivateTab.this, R.layout.private_tab_list_item, R.id.private_item, only_names);
     						pvtList.setAdapter(pvtAdap);
     				        pvtList.setOnItemClickListener(new OnItemClickListener()
     						  {
     						    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
     						    {
     						    	Intent i=new Intent(getApplicationContext(), PrivateChat.class);
     						    	i.putExtra("serverid", userList1.get(position).get("serverid"));
     						    	i.putExtra("regId", userList1.get(position).get("regId"));
     						    	i.putExtra("name", userList1.get(position).get("name"));
     						    	startActivity(i);
     						    }
     						  });   
     					}
     				});	
                  }
        		
        	};
        	refreshTask.execute(null, null, null);
        	return true;
        
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	 @Override
	    protected void onDestroy() {
	    	if (getUsersTask != null) {
	            getUsersTask.cancel(true);
	        }
	    	super.onDestroy();
	    }
}