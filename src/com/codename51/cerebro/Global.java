// Singleton class that holds global data structures,variables, etc. used in multiple activities

package com.codename51.cerebro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class Global extends Application {
	
	public static ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();  //online user list
	public static int currentUser;
	public static int indicator;

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

}
