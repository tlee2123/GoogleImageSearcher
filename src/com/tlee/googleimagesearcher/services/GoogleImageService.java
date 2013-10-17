package com.tlee.googleimagesearcher.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GoogleImageService extends Service {
  
  private static final String URL = "https://ajax.googleapis.com";
  
  private static AsyncHttpClient client = new AsyncHttpClient();
  
	public GoogleImageService() {
		client.addHeader("Referer", "http://www.google.com");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
	  client.get(getAbsoluteUrl(url), params, handler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
	    client.post(getAbsoluteUrl(url), params, handler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
	  return URL + relativeUrl;
	}
}
