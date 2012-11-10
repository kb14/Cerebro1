package com.codename51.cerebro;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import static com.codename51.cerebro.CommonUtilities.SENDER_ID;
import static com.codename51.cerebro.CommonUtilities.SERVER_URL;

public class MainActivity extends Activity implements OnClickListener {
	
	Button newu, existingu;
	// Internet detector
    ConnectionDetector cd;
    // alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    
    UserFunctions userFunctions = new UserFunctions();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
			Intent dashboard = new Intent(getApplicationContext(), Tabbed.class);
			dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(dashboard);
        	// Closing dashboard screen
        	finish();
		}
        else{
        	setContentView(R.layout.activity_main);
	        cd = new ConnectionDetector(getApplicationContext());
	        // Check if Internet present
	        if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
	            alert.showAlertDialog(MainActivity.this,
	                    "Internet Connection Error",
	                    "Please connect to working Internet connection", false);
	            // stop executing code by return
	            return;
	        }
	        // Check if GCM configuration is set
	        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
	                || SENDER_ID.length() == 0) {
	            // GCM sernder id / server url is missing
	            alert.showAlertDialog(MainActivity.this, "Configuration Error!",
	                    "Please set your Server URL and GCM Sender ID", false);
	            // stop executing code by return
	             return;
	        }
	        
	        newu=(Button) findViewById(R.id.newu);
	        existingu=(Button) findViewById(R.id.existingu);
	        newu.setOnClickListener(this);
	        existingu.setOnClickListener(this);
        }
    }
    
    @Override
	public void onClick(View v){
		switch(v.getId())
		{
		case R.id.newu:
			Intent i=new Intent(this, SignUp.class);
			startActivity(i);
			finish();
			break;
		case R.id.existingu:
			Intent j=new Intent(this, Login.class);
			startActivity(j);
			finish();
			break;
		}
	}

    
}
