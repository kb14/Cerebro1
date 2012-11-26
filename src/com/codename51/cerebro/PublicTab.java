package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.codename51.cerebro.CommonUtilities.EXTRA_MESSAGE;
import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;
import static com.codename51.cerebro.CommonUtilities.displayMessage;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PublicTab extends Activity implements OnClickListener{
	// label to display gcm messages
    TextView lblMessage;
    EditText chat;
    Button btnSendMessage;
    String chatMessage = "";
	AsyncTask<Void, Void, Void> logoutTask, sendMessageTask;
	String cName = "" ;
	UserFunctions userFunctions;
	//User List
	ArrayList <HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publictab);
        userFunctions = new UserFunctions();
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        lblMessage.setMovementMethod(new ScrollingMovementMethod());
        chat = (EditText) findViewById(R.id.chat) ;
        btnSendMessage = (Button) findViewById(R.id.sendMessageButton);
        btnSendMessage.setOnClickListener(this);
       
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        
        
    }
	
	 public void onResume() {
         super.onResume();
         
         //Commenting Out for now
        /* MessageHandler2 mh2 = new MessageHandler2(getApplicationContext());
	     ArrayList<String> chatHistory = new ArrayList<String>();
	     chatHistory = mh2.getChatHistory();
	        for(int j = 0; j<chatHistory.size(); j++){
	        	lblMessage.append(chatHistory.get(j) + "\n");
	        }*/
	     
	 }    
		
	
	
	public void onClick(View v){
		switch (v.getId()){
		
		case R.id.sendMessageButton:
			chatMessage = chat.getText().toString();
			sendMessageTask = new AsyncTask<Void, Void, Void>(){
				@Override
                protected Void doInBackground(Void... params) {
					SqliteHandler usr = new SqliteHandler(getApplicationContext());
	 				HashMap<String,String> user = usr.getUserDetails();
	 				cName = user.get("name");
	 				String sid = user.get("serverid");
	 				String lat = user.get("latitude");
	 				String lon = user.get("longitude");
	 				Log.d("PUBLIC TAB LAT CHECK", lat);
	 				JSONObject json1 = userFunctions.sendPublicChat(cName, chatMessage, sid, lat, lon);
	 				try {
    					if (json1.getString(KEY_SUCCESS) != null) {
    						String res = json1.getString(KEY_SUCCESS);
    						if(Integer.parseInt(res) == 1){
    							Log.d("MESSAGE_FROMPHP", "Message Sent");
    						}
    						else{
    							Log.d("MESSAGE", "Message Sending Error");
    						}
    					}
	    			}catch (JSONException e) {
	    				e.printStackTrace();
	    			}
					       
					return null;
				}
				@Override
                protected void onPostExecute(Void result) {
                    sendMessageTask = null;
                 }
			};
			chat.setText("");
			SqliteHandler db = new SqliteHandler(getApplicationContext());
			HashMap<String,String> user = db.getUserDetails();
			cName = user.get("name");
			displayMessage(getApplicationContext(), cName + ": "+ chatMessage, "public");
			sendMessageTask.execute(null, null, null);
			MessageHandler2 mh2 = new MessageHandler2(getApplicationContext());
    		mh2.storeMessage(cName, chatMessage);
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
    
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
    	 @Override
	     public void onReceive(Context context, Intent intent) {
    		 String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
    		 String helper = intent.getExtras().getString("helper");
    		 if(helper.equals("public")){
	    		 // Waking up mobile if it is sleeping
		         WakeLocker.acquire(getApplicationContext());
		         lblMessage.append(newMessage + "\n");
		         // find the amount we need to scroll.  This works by
		         // asking the TextView's internal layout for the position
		         // of the final line and then subtracting the TextView's height
		         final int scrollAmount = lblMessage.getLayout().getLineTop(lblMessage.getLineCount())
		                 -lblMessage.getHeight();
		         // if there is no need to scroll, scrollAmount will be <=0
		         if(scrollAmount>0)
		             lblMessage.scrollTo(0, scrollAmount);
		         else
		             lblMessage.scrollTo(0,0);
		         // Releasing wake lock
		         WakeLocker.release();
    		 }
    	 }
    };
    
    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
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
    