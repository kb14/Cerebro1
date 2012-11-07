package com.codename51.cerebro;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PrivateTab extends Activity
{
	String user_json="{\"AllUsers\":[{" +
					"\"user_name\": \"Sunny Leone\","+
					"\"reg_id\": \"1\","+
					"\"others\" : \"Goddess\"    },"+
					
					"{\"user_name\": \"Ashlynn Brooke\","+
					"\"reg_id\": \"2\","+
					"\"others\" : \"beauty\"    },"+
					
					"{\"user_name\": \"Kayden Kross\","+
					"\"reg_id\": \"3\","+
					"\"others\" : \"sexy\"    },"+
					
					"{\"user_name\": \"Gianna Michaels\","+
					"\"reg_id\": \"4\","+
					"\"others\" : \"Boobs\"    },"+
						"]}";
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
    //AsyncTask
    AsyncTask<Void, Void, Void> getUsersTask;
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatetab);
        
        ListView pvtList=(ListView) findViewById(R.id.pvt_list);
        final ArrayList<String[]> userList=new ArrayList<String[]>();
        final ArrayList<CharSequence> only_names=new ArrayList<CharSequence>();
        
        getUsersTask = new AsyncTask<Void, Void, Void>(){
        	
        	@Override
            protected Void doInBackground(Void... params) {
		        //ojson = new JSONObject(user_json);
		        // LATITUDE LONGITUDE WILL GO AS ARGUMENT WHEN LOCATION WILL BE implemented
		        ojson = userFunctions.getUsers();
		        if (userFunctions.bn == 1){
		        	return null;
		        }
		        try{
		        	// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);
					if(success == 1){
						userArray = ojson.getJSONArray(TAG_USERS);
						 for(int i = 0; i < userArray.length(); i++){
							 c = userArray.getJSONObject(i);
							 String id = c.getString(TAG_ID);
							 String regId = c.getString(TAG_REGID);
							 String name = c.getString(TAG_NAME);
							 String lat = c.getString(TAG_LAT);
							 String lon = c.getString(TAG_LONG);
							 only_names.add(name);
							 userList.add(new String[]{id, regId, name, lat, lon});
						 }
					}
        		}catch (JSONException e) {
    				e.printStackTrace();
    			}
				return null; 
		        
        	}
	        @Override
            protected void onPostExecute(Void result) {
                getUsersTask = null;
             }
        };
        
        getUsersTask.execute(null, null, null);
        
        //ArrayAdapter<CharSequence> pvtAdap=new ArrayAdapter<CharSequence>(this, R.layout.private_tab_list_item, userList);
        ArrayAdapter<CharSequence> pvtAdap=new ArrayAdapter<CharSequence>(this, R.layout.private_tab_list_item, R.id.private_item, only_names);
        
        pvtList.setAdapter(pvtAdap);
        pvtList.setOnItemClickListener(new OnItemClickListener()
		  {
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		    {
		    	Intent i=new Intent(getApplicationContext(), PrivateChat.class);
		    	i.putExtra("userSpecs", userList.get(position));
		    	startActivity(i);
		    }
		  });   
    }
}