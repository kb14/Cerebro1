package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;
import static com.codename51.cerebro.CommonUtilities.SENDER_ID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener
{
	Context cxt;
	Button create;
	EditText txtName,txtPassword,cnfmPassword;
	// Connection detector
    ConnectionDetector cd;
    UserFunctions userFunctions;
	// alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    String regId = "";
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask, updateTask;
    JSONObject json;
    // Progress Dialog
 	private ProgressDialog pDialog;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        cxt = getApplicationContext();
        userFunctions = new UserFunctions();
        cd = new ConnectionDetector(getApplicationContext());
        
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(SignUp.this,
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
        
        txtName = (EditText) findViewById(R.id.fname);
        txtPassword = 	(EditText) findViewById(R.id.pass);
        cnfmPassword = (EditText) findViewById(R.id.confirm_pass);
        create= (Button) findViewById(R.id.create);
        create.setOnClickListener(this);		
    }
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.create:
			//Toast.makeText(this, "Account Created", Toast.LENGTH_LONG);
			// Read EditText dat
            final String name = txtName.getText().toString();
            final String password = txtPassword.getText().toString();
            String cPassword = cnfmPassword.getText().toString();
            
            // Check if user filled the form
            if(name.trim().length() > 0 && password.trim().length() > 0 && cPassword.trim().length() > 0){
            	if(password.equals(cPassword)){
            		// Check if regid already presents
                    if (regId.equals("")) {
                    	// Registration is not present, register now with GCM
                        GCMRegistrar.register(this, SENDER_ID);
                    }
                    else{
                    	final Context context = this;
                    	mRegisterTask = new AsyncTask<Void, Void, Void>() {
                    		/**
            				 * Before starting background thread Show Progress Dialog
            				 */
            				@Override
            				protected void onPreExecute() {
            					super.onPreExecute();
            					pDialog = new ProgressDialog(SignUp.this);
            					pDialog.setMessage("Registering. Please wait...");
            					pDialog.setIndeterminate(false);
            					pDialog.setCancelable(false);
            					pDialog.show();
            				}
                    		@Override
                            protected Void doInBackground(Void... params) {
                    			// Register on our server
                                // On server creates a new user
                                ServerUtilities.register(context, name, password, regId);
                                Log.d("HELLO", "HELLO");
                                UserFunctions uFunctions = new UserFunctions();
                                
                                json = uFunctions.loginUser(name, password);
                                Log.d("LOGIN DURIN REG SIGNUP.JAVA", json.toString());
                                try {
                        			if (json.getString(KEY_SUCCESS) != null) {
                        				String res = json.getString(KEY_SUCCESS);
                        				if(Integer.parseInt(res) == 1){
                        					// user successfully logged in
                        					SqliteHandler db = new SqliteHandler(context);
                                            
                        					JSONObject json_user = json.getJSONObject("user");
                        					// Clear all previous data in database
                        					uFunctions.logoutUser(getApplicationContext());
                        					db.addUser(json_user.getInt("id"), json_user.getString("name"), json_user.getString("regid"));
                        					// Launch Tabbed View
                                    		Intent i = new Intent(getApplicationContext(), Tabbed.class);
                                    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            // Close Signup Screen
                        					finish();
                        				}
                        				else{
                        					
                        				}
                        			}
                        		}catch (JSONException e) {
                        			e.printStackTrace();
                        		}             
                                return null;
                    		}
                    		@Override
                            protected void onPostExecute(Void result) {
                    			// dismiss the dialog after getting all restaurants
            					pDialog.dismiss();
            					if(userFunctions.bn == 1){
            						
            						Toast.makeText(cxt, "Sorry! Connection Timeout", Toast.LENGTH_LONG).show();

            					}
                                mRegisterTask = null;
                            }
         
                       
                        
                    	};
                    	mRegisterTask.execute(null, null, null);
                    }
            		
            	}
            	else{
            		alert.showAlertDialog(SignUp.this, "Registration Error!", "Please put same passwords", false);
            	}
            }
            else{
            	alert.showAlertDialog(SignUp.this, "Registration Error!", "Please fill up complete details", false);
            }
			
			break;
		}
	}
}
