package com.codename51.cerebro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	
	int an = 0;
	
	// constructor
	public JSONParser() {

	}

	
	
	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

		// Making HTTP request
		try {
			// defaultHttpClient
			//DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 10000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			
		
			HttpResponse httpResponse = httpClient.execute(httpPost);
			
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			an = 1;
			e.printStackTrace();
			return jObj;
		} catch (ClientProtocolException e) {
			an = 1;
			e.printStackTrace();
			return jObj;
		} catch (IOException e) {
			an = 1;
			//System.out.println("Check 1 Thts it");
			e.printStackTrace();
			return jObj;
		} 
		

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.e("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);			
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
	
	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {
		// Making HTTP request
		
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 30000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				
				HttpEntity httpEntity = httpResponse.getEntity();
			
				is = httpEntity.getContent();
				
				
			}else if(method == "GET"){
				// request method is GET
				
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				//System.out.println(url);
				HttpGet httpGet = new HttpGet(url);
				
				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 30000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}			
			

		} catch (UnsupportedEncodingException e) {
			an = 1;
			e.printStackTrace();
			return jObj;
		} catch (ClientProtocolException e) {
			an = 1;
			e.printStackTrace();
			return jObj;
		} catch (IOException e) {
			an = 1;
			e.printStackTrace();
			return jObj;
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			Log.d("JSONWALA",json);
			
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "TError parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}

