package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import com.codename51.cerebro.LocationHelper.LocationResult;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationTab extends MapActivity implements OnClickListener{
	
	// lat, longs and other Location Declarations
	double latitude = 0;
	double longitude = 0;
	//Find User's lat/lon
	double usrLat = 0;
	double usrLon = 0;
	LocationHelper locHelper;
	Location currentLocation;
	private boolean hasLocation = false;
	List<Overlay> mapOverlays;
	
	Context cxt;
	JSONObject json;
	AsyncTask<Void, Void, Void> logoutTask, findUserTask, updateLocationTask;
	// Buttons and EditTexts
	Button btnFind;
	EditText txtUsername;
	
	MapView myMapView = null;
	MapController mapController;
	MyLocationOverlay myLocationOverlay;
	AlertDialogManager alert = new AlertDialogManager();
	//Find Username
	String userName = "";
	UserFunctions userFunctions = new UserFunctions();
	SqliteHandler db;
	int error = 0;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationtab);
        
        cxt = getApplicationContext();
        db = new SqliteHandler(cxt);
        txtUsername = (EditText) findViewById(R.id.nameBox) ;
        btnFind = (Button) findViewById(R.id.findButton);
        btnFind.setOnClickListener(this);
        //Get a reference to the MapView
        myMapView = (MapView)findViewById(R.id.myMapView);
        //Get the Map View’s controller
    	mapController = myMapView.getController();
    	
    	//Configure the map display options
    	myMapView.setSatellite(false);
    	
    	//myMapView.setStreetView(true);
    	myMapView.setBuiltInZoomControls(true);
    	
    	//Zoom in
    	mapController.setZoom(17);
        
    }
	
	public void onClick(View v){
		switch (v.getId()){
		case R.id.findButton:
			userName = txtUsername.getText().toString();
			findUserTask = new AsyncTask<Void, Void, Void>(){
				@Override
                protected Void doInBackground(Void... params) {
					json = userFunctions.getUserLatLong(userName);
					if (userFunctions.bn == 1){
						return null;
					}
					
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							String res = json.getString(KEY_SUCCESS);
							if(Integer.parseInt(res) == 1){
								String lat1 = json.getString("latitude");
								String lon1 = json.getString("longitude");
								Log.d("LAT LONG", lat1+" "+lon1);
								if(Double.parseDouble(lat1)==0 && Double.parseDouble(lon1)==0){
									//User location not updated
									error = 1;
								}
								else{
									//Now Go to Clone Location Tab
									//Or try to find a solution here
									usrLat = Double.parseDouble(lat1);
									usrLon = Double.parseDouble(lon1);
								}
							}
							else{
								//USERNAME NOT FOUND OR NOT ONLINE
								//Do Something
								error = 2;
								
							}
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
					
					return null;
				}
				@Override
                protected void onPostExecute(Void result) {
					if(userFunctions.bn == 1){
						Toast.makeText(cxt, "Sorry! Connection Timeout", Toast.LENGTH_LONG).show();
					}
					else{
						if(error == 1){
							Toast.makeText(cxt, userName+" has not updated location", Toast.LENGTH_LONG).show();
							error = 0;
						}
						else if(error == 2){
							Toast.makeText(cxt, userName+" is not online right now.", Toast.LENGTH_LONG).show();
							error= 0;
						}
						else{
							//Lat longitude found for the given username
							runOnUiThread(new Runnable() {
								public void run() {
									//Drawable and Marker Stuff
							        mapOverlays = myMapView.getOverlays();
							        if(!mapOverlays.isEmpty()){ 
								        mapOverlays.clear(); 
								        myMapView.invalidate();

							        }
							        Drawable drawable = cxt.getResources().getDrawable(R.drawable.ic_maps_indicator_current_position);
							        MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable, cxt);
									GeoPoint point = getPoint(usrLat, usrLon);
						    		OverlayItem overlayitem = new OverlayItem(point, userName, "Last known location");
						    		itemizedoverlay.addOverlay(overlayitem);
						    		
						    		mapController.setCenter(getPoint(usrLat, usrLon));
						    		mapOverlays.add(itemizedoverlay);
								}
							});
						}
					}
                    findUserTask = null;
                 }
			};
			findUserTask.execute(null, null, null);
			
			break;
		}
	}
	
	/* Initiating Menu XML file (menu.xml) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.bottommenu1, menu);
        return true;
    }
    
    public void onResume() {
        super.onResume();
        
        alert.showAlertDialog(LocationTab.this, "Location Feature",
                "Please turn on GPS/Wi-Fi for better location results", true);
        
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
        
        if(currentLocation != null && isOnline()){
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
					// dismiss the dialog after getting all restaurants
					//pDialog.dismiss();
					updateLocationTask = null;
					
            	}
			};	
			updateLocationTask.execute(null, null, null);
			//Drawable and Marker Stuff
	        mapOverlays = myMapView.getOverlays();
	        if(!mapOverlays.isEmpty()){ 
		        mapOverlays.clear(); 
		        myMapView.invalidate();

	        }
	        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_maps_indicator_current_position);
	        MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable, this);
			GeoPoint point = getPoint(latitude,longitude);
    		OverlayItem overlayitem = new OverlayItem(point, "Current Location", "Latitude: "+lat+", Longitude: "+lon);
    		itemizedoverlay.addOverlay(overlayitem);
    		
    		mapController.setCenter(getPoint(latitude,longitude));
    		mapOverlays.add(itemizedoverlay);
        }
        
        
    
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   
		if (keyCode == KeyEvent.KEYCODE_Z) {
	      myMapView.displayZoomControls(true);
	      return(true);
	    }
	    
	    return(super.onKeyDown(keyCode, event));
	  }
    
    private GeoPoint getPoint(double lat, double lon) {
	    return(new GeoPoint((int)(lat*1000000.0),
	                          (int)(lon*1000000.0)));
	  }
    
    public LocationResult locationResult = new LocationResult(){
        @Override
        public void gotLocation(final Location location)
        {
            currentLocation = new Location(location);
            hasLocation = true;
        }
    };
    
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    
    //To Check if there is Internet Connection
  	public boolean isOnline() {
  	    ConnectivityManager cm =
  	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

  	    return cm.getActiveNetworkInfo() != null && 
  	       cm.getActiveNetworkInfo().isConnectedOrConnecting();
  	}
    
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        
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
            		/*
            		 * Uncommenting the code below will result in not saving message history after log out
            		 */
            		MessageHandler mh = new MessageHandler(getApplicationContext());
            		mh.resetTables();
            		MessageHandler2 mh2 = new MessageHandler2(getApplicationContext());
            		mh2.resetTables();
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