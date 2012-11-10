package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.SENDER_ID;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
@SuppressWarnings("deprecation")
public class Tabbed extends TabActivity
{
    // TabSpec Names
    private static final String Private_SPEC = "Private Chat";
    private static final String Public_SPEC = "Public Chat";
    private static final String Location_SPEC = "Track Location";
    
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask, updateTask;
    //private ProgressDialog pDialog;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    // Connection detector
    ConnectionDetector cd;
    String regId = "";
    UserFunctions userFunctions;
    JSONObject json;
    
    public static String name;
    public static String password;
    public static String serverId;
    public static String regId1;
    public static int indicator = 0;
 
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
 
        TabHost tabHost = getTabHost();
        
        userFunctions = new UserFunctions();
        cd = new ConnectionDetector(getApplicationContext());
 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(Tabbed.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        // Get GCM registration id
        regId = GCMRegistrar.getRegistrationId(this);
        
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
        	if (regId.equals("")) {
        		indicator = 1;
        		// Registration is not present, register now with GCM
                GCMRegistrar.register(this, SENDER_ID);
            }
            else{
            	//Update Registration Id for the User logged in.
            	updateTask = new AsyncTask<Void, Void, Void>(){
            		@Override
                    protected Void doInBackground(Void... params) {
		            	UserFunctions uf = new UserFunctions();
		    	        SqliteHandler ur = new SqliteHandler(getApplicationContext());
		    	        HashMap<String,String> user = ur.getUserDetails();
		    	        String serverid = user.get("serverid");
		    	        JSONObject json1 = uf.updateRegid(serverid, regId);
		    	        try {
		    				Log.d("SUCCESS_GCMINTENTSERVICE", json1.getString("success"));
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		    	        return null;
            		}
            		@Override
	    	        protected void onPostExecute(Void result) {
						// dismiss the dialog after getting all restaurants
						//pDialog.dismiss();
						updateTask = null;
						
	            	}
            	};
            	updateTask.execute(null, null, null);
    	        
            }
        }
        // Inbox Tab
        TabSpec privateSpec = tabHost.newTabSpec(Private_SPEC);
        // Tab Icon
        privateSpec.setIndicator(Private_SPEC, getResources().getDrawable(android.R.drawable.btn_dialog));
        Intent privateIntent = new Intent(this, PrivateTab.class);
        // Tab Content
        privateSpec.setContent(privateIntent);
 
        // Outbox Tab
        TabSpec publicSpec = tabHost.newTabSpec(Public_SPEC);
        publicSpec.setIndicator(Public_SPEC, getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
        Intent publicIntent = new Intent(this, PublicTab.class);
        
        publicSpec.setContent(publicIntent);
 
        // Profile Tab
        TabSpec locationSpec = tabHost.newTabSpec(Location_SPEC);
        locationSpec.setIndicator(Location_SPEC, getResources().getDrawable(android.R.drawable.ic_menu_mapmode));
        Intent profileIntent = new Intent(this, LocationTab.class);
        locationSpec.setContent(profileIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(privateSpec); // Adding Inbox tab
        tabHost.addTab(publicSpec); // Adding Outbox tab
        tabHost.addTab(locationSpec); // Adding Profile tab
    }
    
    
    
    
   /* @Override
    protected void onDestroy() {
    	if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
    	super.onDestroy();
    }*/
    
    
    
}