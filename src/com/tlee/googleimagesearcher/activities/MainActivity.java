package com.tlee.googleimagesearcher.activities;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tlee.googleimagesearcher.R;
import com.tlee.googleimagesearcher.SearchOptionActivity;
import com.tlee.googleimagesearcher.adapters.ImageLoaderAdapter;
import com.tlee.googleimagesearcher.models.ImageModel;
import com.tlee.googleimagesearcher.services.GoogleImageService;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  
  private static final int REQUEST_CODE = 0;
  private static final long MAX_RESULT = 10000;
  private static final long THRESHOLD_COUNT = 64;
 
  private GridView gridView;
  private Button searchView;
  ImageLoaderAdapter imageLoaderAdapter;
  Bundle extras = new Bundle();
  private HashMap<String,String> savedParams = new HashMap<String,String>();
  long counter = THRESHOLD_COUNT;
  int lastQueryStart = 0;
  long gridTotalItemCount = 0;
  long gridVisibleItemCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.gridView = (GridView)findViewById(R.id.gridView1);
		this.searchView = (Button)findViewById(R.id.button1);
		imageLoaderAdapter = ImageLoaderAdapter.getInstance(MainActivity.this, 0);
    imageLoaderAdapter.clear();
    this.gridView.setAdapter(imageLoaderAdapter);
    
		this.savedParams.put("v", "1.0");
    this.savedParams.put("userip", "10.100.10.15");
    
		registerListeners();
	}
	
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
    }
 
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
     switch (item.getItemId()) {
       case R.id.action_settings:
         showSearchOptions();
         //Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();
         break;
       default:
         break;
     }
     return true;
   }
 
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
   super.onActivityResult(requestCode, resultCode, intent);
   if (requestCode == REQUEST_CODE) {
     if (resultCode == RESULT_OK) {
       for (String key : intent.getExtras().keySet()) {
         Log.i(key, intent.getExtras().getString(key));
         this.savedParams.put(key, intent.getExtras().getString(key));
       }
     }
   }
  }
 
  private void showSearchOptions() {
    Intent intent = new Intent(getApplicationContext(), SearchOptionActivity.class);
    startActivityForResult(intent, REQUEST_CODE);
  }
	
	private void registerListeners() {
	  
	  this.gridView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> gridView, View imageView, int position, long id) {
        // TODO Auto-generated method stub
        ImageLoaderAdapter adapter = (ImageLoaderAdapter)gridView.getAdapter();
        ImageLoader imageLoader = adapter.getImageLoader();

        showPopup(position, adapter, imageLoader);
      }
	    
	  });

	  
	  this.gridView.setOnScrollListener(new OnScrollListener() {

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        // TODO Auto-generated method stub
        gridVisibleItemCount = visibleItemCount;
        gridTotalItemCount = totalItemCount;

      }
      
      
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {       
          long remainingItemCount = gridTotalItemCount - gridVisibleItemCount;
          if (remainingItemCount < THRESHOLD_COUNT) {
            long start = gridTotalItemCount + 4;
            Log.i("ONSEARCH", String.valueOf(gridTotalItemCount));
            scrollSearch(view, start);
          }
          
        }
      }
	    
	  });
	  
	  
	  this.searchView.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        // TODO Auto-generated method stub
        buttonSearch(view);
      }
	    
	  });
	 
	}
	
	public void buttonSearch(View view) {
	  Log.i("BUTTONSEARCH", "TEST1");
	  imageLoaderAdapter.clear();
    search(view, 0);
	}
	
	public void scrollSearch(View view, long start) {
	   Log.i("SCROLLSEARCH", "TEST1");
	  search(view, start);
	}

	private void search(View view, long start) {
	  EditText queryView = (EditText) findViewById(R.id.editText1);
    if (queryView.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "Enter your search terms", Toast.LENGTH_LONG).show();
      return;
    }
	  RequestParams params = new RequestParams();
	  params.put("q", queryView.getText().toString());
	  for (String key : this.savedParams.keySet()) {
	    params.put(key, this.savedParams.get(key));
	  }
	  
    for (long i = start; i < counter; i += 4) {
      Log.i("SEARCH", String.valueOf(start));
      queryImageWithCursor(params, i);
    }
    
    
    MainActivity.this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
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
	
	long getResultCount(JSONObject response) {
	  String count = null;
	  try {
	    if (response.getJSONObject("responseData") != null) {
	      count = response.getJSONObject("responseData").getJSONObject("cursor")
	          .getString("estimatedResultCount");
	      return Long.parseLong(count);
	    }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	  return 0;
	}
	
	private void query(RequestParams params) {
    GoogleImageService.get("/ajax/services/search/images", params, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(JSONObject response) {
        try {
          long tmpCounter = getResultCount(response);
          if (tmpCounter > 0) {
            counter = MAX_RESULT < tmpCounter  ? MAX_RESULT : tmpCounter;
            JSONArray results = response.getJSONObject("responseData").getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
              JSONObject result = results.getJSONObject(i);
              String title = result.getString("titleNoFormatting");
              String tbUrl = result.getString("tbUrl");
              String url = result.getString("url");
              imageLoaderAdapter.add(new ImageModel(title, tbUrl, url));
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
	}
	
  private void queryImageWithCursor(RequestParams params, long cursor) {
    params.put("start", String.valueOf(cursor));
    query(params);
  }
  
  private void showPopup(final int position, final ImageLoaderAdapter adapter, final ImageLoader loader) {
    
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

  }

}
