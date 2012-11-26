package com.codename51.cerebro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class Global extends Application {
	
	public static ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
	public static int currentUser;
	public static int indicator;

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

}
