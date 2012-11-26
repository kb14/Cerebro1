package com.codename51.cerebro;

import static com.codename51.cerebro.CommonUtilities.KEY_SUCCESS;
import static com.codename51.cerebro.CommonUtilities.displayMessage;
import static com.codename51.cerebro.CommonUtilities.EXTRA_MESSAGE;
import static com.codename51.cerebro.CommonUtilities.DISPLAY_MESSAGE_ACTION;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PrivateChat extends Activity implements OnClickListener{
	
	// label to display gcm messages
    TextView lblMessage;
    EditText chat;
    Button btnSendMessage;
    String chatMessage = "";
    
    // Asyntask
    AsyncTask<Void, Void, Void> sendMessageTask;
    
    public static String name;
    public static String serverid;
    public static String regId;
    int indicator = 0;
    
    UserFunctions userFunctions;
	 @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_privatechat);
	        userFunctions = new UserFunctions();
	        lblMessage = (TextView) findViewById(R.id.lblMessage);
	        lblMessage.setMovementMethod(new ScrollingMovementMethod());
	        chat = (EditText) findViewById(R.id.chat) ;
	        btnSendMessage = (Button) findViewById(R.id.sendMessageButton);
	        btnSendMessage.setOnClickListener(this);
	        
	        registerReceiver(mHandleMessageReceiver, new IntentFilter(
	                DISPLAY_MESSAGE_ACTION));
	        
	        Intent i = getIntent();
	        name = i.getStringExtra("name");
	        serverid = i.getStringExtra("serverid");
	        regId = i.getStringExtra("regId");
	        Global.currentUser = Integer.parseInt(serverid);
	        
	        MessageHandler mh = new MessageHandler(getApplicationContext());
	        ArrayList<String> chatHistory = new ArrayList<String>();
	        chatHistory = mh.getChatHistory(Integer.parseInt(serverid), name);
	        for(int j = 0; j<chatHistory.size(); j++){
	        	lblMessage.append(chatHistory.get(j) + "\n");
	        }
	        
	    }
	 
	 public void onClick(View v){
		 switch (v.getId()){
		 	case R.id.sendMessageButton:
		 		if(chat.getText().toString().equals(""))
		 			break;
		 		chatMessage = chat.getText().toString();
		 		sendMessageTask = new AsyncTask<Void, Void, Void>(){
		 			@Override
	                protected Void doInBackground(Void... params) {
		 				//Sends the chat message to our servers
		    			//From our servers->GCM server->intended receiver (user we're chatting with)
		 				SqliteHandler usr = new SqliteHandler(getApplicationContext());
		 				HashMap<String,String> user = usr.getUserDetails();
		 				String sid = user.get("serverid");
		 				String nme = user.get("name");
		    			JSONObject json1 = userFunctions.sendChat(regId, chatMessage, nme, sid);
		    			try {
	    					if (json1.getString(KEY_SUCCESS) != null) {
	    						String res = json1.getString(KEY_SUCCESS);
	    						if(Integer.parseInt(res) == 1){
	    							Log.d("MESSAGE", "Message Sent");
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
	    		displayMessage(getApplicationContext(),"ME: "+ chatMessage, "private");
	    		indicator = 1;
	    		sendMessageTask.execute(null, null, null);
	    		MessageHandler mh = new MessageHandler(getApplicationContext());
	    		mh.storeMessage(Integer.parseInt(serverid) , 0, chatMessage);
		 		break;
		 }
		 
		}
	 	/**
	     * Receiving push messages
	     * */
	    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	            String helper = intent.getExtras().getString("helper");
	            
	            if(helper.equals("private")){
		            // Waking up mobile if it is sleeping
		            WakeLocker.acquire(getApplicationContext());
		 
		            /**
		             * Displaying message on the screen 
		             * */
		            
		            // Showing received message
		            if(indicator == 1 ){
		            	lblMessage.append(newMessage + "\n");
		            }
		            else{
		            	lblMessage.append(name.toUpperCase()+ ": " + newMessage + "\n");
		            } 
		            indicator = 0;
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
}
