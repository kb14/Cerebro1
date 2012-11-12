package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.codename51.cerebro.CommonUtilities.EXTRA_MESSAGE;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
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
        /*
         * Test Code Start
         */
        //HashMap<String, String> map = new HashMap<String, String>();
        //map.put("regId", "avracadabra5678");
        //userList.add(map);
        //Test code end 
        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        
        
    }
	
	 public void onResume() {
         super.onResume();
         
         Log.d("TESTING MAN", "Testing man");
         MessageHandler mh = new MessageHandler(getApplicationContext());
	     ArrayList<String> chatHistory = new ArrayList<String>();
	     
	 }    
		
	
	
	public void onClick(View v){
		switch (v.getId()){
		
		case R.id.sendMessageButton:
			chatMessage = chat.getText().toString();
			sendMessageTask = new AsyncTask<Void, Void, Void>(){
				@Override
                protected Void doInBackground(Void... params) {
					userList = Global.userList;
					JSONArray userArray = new JSONArray(userList);
					Log.d("PUBLIC: JSONARRAY USER", userArray.toString());
			        
				       
					return null;
				}
				@Override
                protected void onPostExecute(Void result) {
                    sendMessageTask = null;
                 }
			};
			sendMessageTask.execute(null, null, null);
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
    