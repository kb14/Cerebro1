package com.codename51.cerebro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

public class Global extends Application {
	
	public static ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

}
