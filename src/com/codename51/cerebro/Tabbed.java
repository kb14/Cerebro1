package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.SENDER_ID;

import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import com.codename51.cerebro.LocationHelper.LocationResult;
import com.google.android.gcm.GCMRegistrar;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
@SuppressWarnings("deprecation")
public class Tabbed extends TabActivity
{
	// lat, longs and other Location Declarations
	double latitude = 0;
	double longitude = 0;
	//Find User's lat/lon
	double usrLat = 0;
	double usrLon = 0;
	LocationHelper locHelper;
	Location currentLocation;
	private boolean hasLocation = false;
	
    // TabSpec Names
    private static final String Private_SPEC = "Private Chat";
    private static final String Public_SPEC = "Public Chat";
    private static final String Location_SPEC = "Track Location";
    
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask, updateTask, updateLocationTask;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    // Connection detector
    ConnectionDetector cd;
    String regId = "";
    UserFunctions userFunctions;
    SqliteHandler db;
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
        db = new SqliteHandler(getApplicationContext());
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
        
        locHelper = new LocationHelper(this, this);
        Context context = getApplicationContext();
		locHelper.getLocation(context, locationResult);
		
		//Wait 10 seconds to see if we can get a location from either network or GPS, otherwise stop
		
		Long t = Calendar.getInstance().getTimeInMillis();
        while (!hasLocation && Calendar.getInstance().getTimeInMillis() - t < 15000) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        if(currentLocation != null){
        	latitude = currentLocation.getLatitude();
			longitude = currentLocation.getLongitude();
			final String lat = Double.toString(latitude);
			final String lon = Double.toString(longitude);
			updateLocationTask = new AsyncTask<Void, Void, Void>(){	
				@Override
                protected Void doInBackground(Void... params) {
					//Update Location
					HashMap<String, String> userdb = db.getUserDetails();
					String serverid = userdb.get("serverid");
					String name = userdb.get("name");
					String regid = userdb.get("regid");
					// Clear all previous data in database
					userFunctions.logoutUser(getApplicationContext());
					db.addUserwLatLong(Integer.parseInt(serverid), name, regid, lat, lon);
					JSONObject json1 = userFunctions.updateLocation(serverid, lat, lon);
					try {
						Log.d("SUCCESS IN UPDATE LOC", json1.getString("success"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				protected void onPostExecute(Void result) {
					updateLocationTask = null;
					
            	}
			};	
			updateLocationTask.execute(null, null, null);
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
    
    public LocationResult locationResult = new LocationResult(){
        @Override
        public void gotLocation(final Location location)
        {
            currentLocation = new Location(location);
            hasLocation = true;
        }
    };
    
    
   /* @Override
    protected void onDestroy() {
    	if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
    	super.onDestroy();
    }*/
    
    
    
}