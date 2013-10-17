package com.tlee.googleimagesearcher.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tlee.googleimagesearcher.R;
import com.tlee.googleimagesearcher.adapters.ImageLoaderAdapter;
import com.tlee.googleimagesearcher.models.ImageModel;
import com.tlee.googleimagesearcher.services.GoogleImageService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
 
  private GridView gridView;
  ImageLoaderAdapter imageLoaderAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.gridView = (GridView) findViewById(R.id.gridView1);
		imageLoaderAdapter = ImageLoaderAdapter.getInstance(MainActivity.this, 0);
		registerListeners();
	}
	
	private void registerListeners() {
	  this.gridView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> gridView, View imageView, int position, long id) {
        // TODO Auto-generated method stub
        ImageLoaderAdapter adapter = (ImageLoaderAdapter)gridView.getAdapter();
        ImageLoader imageLoader = adapter.getImageLoader();

        showPopup(position, adapter, imageLoader);
        //Toast.makeText(MainActivity.this, imageLoaderAdapter.getItem(position).getTitle(),
            //Toast.LENGTH_SHORT).show();
      }
	    
	  });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void search(View view) {
	  imageLoaderAdapter.clear();
	  EditText queryView = (EditText) findViewById(R.id.editText1);
	  RequestParams params = new RequestParams("q", queryView.getText().toString());
	  params.put("v", "1.0");
	  params.put("userip", "10.100.10.13");
	  
    for (int i=0; i<64; i+=4) {
      queryImageWithCursor(params, i);
    }
    
    gridView.setAdapter(imageLoaderAdapter);
    
    MainActivity.this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // TODO Auto-generated method stub
        imageLoaderAdapter.notifyDataSetChanged();
      }
    });

    hideKeyboard();
	}
	
	private void hideKeyboard() {
	  EditText edit = (EditText)findViewById(R.id.editText1);
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
	}
	
  private void queryImageWithCursor(RequestParams params, int cursor) {
    params.put("start", String.valueOf(cursor));
    
    GoogleImageService.get("/ajax/services/search/images", params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(JSONObject response) {
        try {
          JSONArray results = response.getJSONObject("responseData").getJSONArray("results");
          for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String title = result.getString("titleNoFormatting");
            String tbUrl = result.getString("tbUrl");
            String url = result.getString("url");
            Log.i("SUCCESS", title);
            imageLoaderAdapter.add(new ImageModel(title, tbUrl, url));
          }
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
  }
  
  private void showPopup(final int position, final ImageLoaderAdapter adapter, final ImageLoader loader) {
    getActionBar().hide();
    
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.activity_image_popup);
    dialog.setTitle("Image Detail");
    dialog.show();
    
    ImageView imageView = (ImageView)dialog.findViewById(R.id.imageView1);
    TextView textView = (TextView)dialog.findViewById(R.id.textView1);
    textView.setText(adapter.getItem(position).getTitle());
    loader.displayImage(adapter.getItem(position).getUrl(), imageView);
    Button cancelButton = (Button)dialog.findViewById(R.id.button1);
    cancelButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        dialog.dismiss();
      }
      
    });

    Log.i("POPUP", adapter.getItem(position).getUrl());
  }

}
