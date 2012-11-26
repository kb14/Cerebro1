package com.codename51.cerebro;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
 
import com.google.android.gcm.GCMBaseIntentService;
 
import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;
import static com.codename51.cerebro.CommonUtilities.SENDER_ID;
import static com.codename51.cerebro.CommonUtilities.displayMessage;
 
public class GCMIntentService extends GCMBaseIntentService {
 
    private static final String TAG = "GCMIntentService";
    
 // Asyntask
    AsyncTask<Void, Void, Void> updateTask, registerTask;
    
    //To receive user details from the server
    JSONObject json;
 
    public GCMIntentService() {
        super(SENDER_ID);
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        //Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, "Your device registered with GCM");
        //Log.d("NAME", MainActivity.name);
        if(Global.indicator == 1){
        	System.out.println("IDHAR AAYA OYE!!");
        	final Context cxt = context;
        	final String ri = registrationId;
        	registerTask = new AsyncTask<Void, Void, Void>(){
        		@Override
                protected Void doInBackground(Void... params) {
        			SqliteHandler db = new SqliteHandler(cxt);
        	        ServerUtilities.register(cxt, SignUp.name, SignUp.password, ri);
        	        
        	        UserFunctions userFunctions = new UserFunctions();
        	        json = userFunctions.loginUser(SignUp.name, SignUp.password);
        	        try {
        				if (json.getString(KEY_SUCCESS) != null) {
        					String res = json.getString(KEY_SUCCESS);
        					if(Integer.parseInt(res) == 1){
        						// user successfully logged in
        						
        						JSONObject json_user = json.getJSONObject("user");
        						// Clear all previous data in database
        						userFunctions.logoutUser(getApplicationContext());
        						db.addUser(json_user.getInt("id"), json_user.getString("name"), json_user.getString("regid"));
        					}
        					else{
        						
        					}
        				}
        			}catch (JSONException e) {
        				e.printStackTrace();
        			}
        			return null;
        		}
        		
        	};
	        registerTask.execute(null, null, null);
        }
        else{
        	final String ri = registrationId;
        	//Update Registration Id for the User logged in.
        	updateTask = new AsyncTask<Void, Void, Void>(){
        		@Override
                protected Void doInBackground(Void... params) {
		        	UserFunctions userFunctions = new UserFunctions();
			        SqliteHandler db = new SqliteHandler(getApplicationContext());
			        HashMap<String,String> user = db.getUserDetails();
			        String serverid = user.get("serverid");
			        JSONObject json1 = userFunctions.updateRegid(serverid, ri);
			        try {
						Log.d("SUCCESS_GCMINTENTSERVICE", json1.getString("success"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        return null;
        		}
        	};
        	updateTask.execute(null, null, null);
        }
        
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered), "n");
        ServerUtilities.unregister(context, registrationId);
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        String sid = intent.getExtras().getString("sid");
        if(Integer.parseInt(sid) == -1){
        	String name = intent.getExtras().getString("name");
        	MessageHandler2 mh2 = new MessageHandler2(getApplicationContext());
        	mh2.storeMessage(name, message);
        	displayMessage(context, name.toUpperCase()+ ": " +message, "public");
        	// notifies user
            generateNotification(context, message, "Public", name);
        }
        else{
        	String name = intent.getExtras().getString("name");
        	MessageHandler mh = new MessageHandler(getApplicationContext());
        	mh.storeMessage(Integer.parseInt(sid), 1, message);
        	if(Global.currentUser == Integer.parseInt(sid)){
        		displayMessage(context, message, "private");
        
        	}
        	// notifies user
            generateNotification(context, message, "Private", name);
        }
        
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message, "n");
        // notifies user
        generateNotification(context, message, "", "");
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId), "n");
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId), "n");
        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message, String type, String name) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")
		Notification notification = new Notification(icon, message, when);
 
        String title = "";
        if(type.equals("Private")){
        	title = name;
        }
        else{
        	title = "Public";
        }
        
        UserFunctions userFunctions = new UserFunctions();
        Intent notificationIntent;
        if(userFunctions.isUserLoggedIn(context)){
        	notificationIntent = new Intent(context, Tabbed.class);
        }
        else{
        	notificationIntent = new Intent(context, MainActivity.class);
        }
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
 
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
 
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      
 
    }
 
}