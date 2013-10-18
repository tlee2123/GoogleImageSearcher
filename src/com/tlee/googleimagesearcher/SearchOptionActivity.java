
package com.tlee.googleimagesearcher;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchOptionActivity extends Activity {
  
  private Spinner imageSize;
  private Spinner color;
  private Spinner type;
  private EditText site;
  private Button save;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_option);

    this.imageSize = (Spinner)findViewById(R.id.spinner1);
    this.imageSize.setSelection(1);
    this.color = (Spinner)findViewById(R.id.spinner2);
    this.color.setSelection(1);
    this.type = (Spinner)findViewById(R.id.spinner3);
    this.type.setSelection(1);
    this.site = (EditText)findViewById(R.id.editText1);
    this.save = (Button)findViewById(R.id.button1);
    
    registerListeners();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.search_option, menu);
    return true;
  }

  
  public void registerListeners() {
    this.save.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        Bundle extras = new Bundle();
        extras.putString("imgsz", imageSize.getSelectedItem().toString());
        extras.putString("imgcolor", color.getSelectedItem().toString());
        extras.putString("imgtype", type.getSelectedItem().toString());
        extras.putString("as_sitesearch", site.getText().toString() == "" ? "*" : site.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(extras);
        setResult(RESULT_OK, intent);
        finish();
      }
      
    });
  }

}
