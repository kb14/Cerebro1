package com.codename51.cerebro;

import android.content.Context;
import android.content.Intent;
 
public final class CommonUtilities {
 
    // give your server registration url here
	//10.0.2.2 -> localhost
    static final String SERVER_URL = "http://192.168.43.35/gcm_server_php/register.php";   
    static final String CHAT_URL = "http://192.168.43.35/gcm_server_php/send_chat.php";
    static final String LOGIN_URL = "http://192.168.43.35/gcm_server_php/login.php";
    static final String GETUSERS_URL = "http://192.168.43.35/gcm_server_php/get_users.php";
    static final String LOGOUT_URL = "http://192.168.43.35/gcm_server_php/logout.php";
    static final String UPDATEREGID_URL = "http://192.168.43.35/gcm_server_php/update_regid.php";
    static final String GETLATLONG_URL = "http://192.168.43.35/gcm_server_php/getlatlong.php";
    
    static final String KEY_SUCCESS = "success";
 
    // Google project id
    static final String SENDER_ID = "493105976362"; 
 
    /**
     * Tag used on log messages.
     */
    static final String TAG = "Cerebro GCM";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.codename51.cerebro.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "message";
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
