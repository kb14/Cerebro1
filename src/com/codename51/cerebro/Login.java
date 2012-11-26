package com.codename51.cerebro;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.codename51.cerebro.LocationHelper.LocationResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;

public class Login extends Activity implements OnClickListener
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
	
	Context cxt;
	Button go;
	// Progress Dialog
	private ProgressDialog pDialog;
	EditText loginName,loginPassword;
	TextView loginErrorMsg;
	String name = "", password="", asyncresult="";
	//To receive user details from the server
	JSONObject json;
		
	//AsyncTask
	AsyncTask<Void, Void, Void> loginTask, updateLocationTask;
		
	UserFunctions userFunctions = null;
   @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        cxt = getApplicationContext();
        userFunctions = new UserFunctions();   
        go=(Button) findViewById(R.id.go);
        go.setOnClickListener(this);
        loginName = (EditText) findViewById(R.id.enter_id);
        loginPassword = (EditText) findViewById(R.id.enter_pass);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        
        locHelper = new LocationHelper(this, this);
        Context context = getApplicationContext();
		locHelper.getLocation(context, locationResult);
		

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
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.go:
			name = loginName.getText().toString();
			password = loginPassword.getText().toString();
			loginTask = new AsyncTask<Void, Void, Void>(){
				
				/**
				 * Before starting background thread Show Progress Dialog
				 */
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(Login.this);
					pDialog.setMessage("Logging In. Please wait...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
				}
				
				@Override
                protected Void doInBackground(Void... params) {
					
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
						
			        }
			        final String lat = Double.toString(latitude);
					final String lon = Double.toString(longitude);
					
					//Login already registered user into the system with his name.
					
					json = userFunctions.loginUser(name,password);
					if (userFunctions.bn == 1){
						return null;
					}
					
					// check for login response
					try {
						if (json.getString(KEY_SUCCESS) != null) {
							String res = json.getString(KEY_SUCCESS);
							if(Integer.parseInt(res) == 1){
								// user successfully logged in
								// Store user details in SQLite Database
								SqliteHandler db = new SqliteHandler(getApplicationContext());
								JSONObject json_user = json.getJSONObject("user");
								// Clear all previous data in database
								userFunctions.logoutUser(getApplicationContext());
								//Log.d("Login Lat longs", lat + " " + lon);
								db.addUserwLatLong(json_user.getInt("id"), json_user.getString("name"), json_user.getString("regid"), lat, lon);
								JSONObject json1 = userFunctions.updateLocation(Integer.toString(json_user.getInt("id")), lat, lon);
								// Launch My Account Type Screen
								Intent dashboard = new Intent(getApplicationContext(), Tabbed.class);
								
								// Close all views before launching My Account Screen
								dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(dashboard);
								
								// Close Login Screen
								finish();
								
							}
							else{
								//User Not Found
								asyncresult = "fail";
							}
						}
					}catch (JSONException e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				/**
				 * After completing background task Dismiss the progress dialog
				 * **/
				@Override
                protected void onPostExecute(Void result) {
					// dismiss the dialog after getting all restaurants
					pDialog.dismiss();
					if(userFunctions.bn == 1){
						
						Toast.makeText(cxt, "Sorry! Connection Timeout", Toast.LENGTH_LONG).show();

					}
					else{
						if (asyncresult.equals("fail")){
							loginErrorMsg.setText("Incorrect Username/Password");
						}
						else{
							loginErrorMsg.setText("");
						}
					}
					loginTask = null;
                 }
				
			};
			loginTask.execute(null,null,null);
			break;
		}
	}
}