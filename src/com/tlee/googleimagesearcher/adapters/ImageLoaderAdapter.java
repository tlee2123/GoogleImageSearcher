package com.tlee.googleimagesearcher.adapters;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tlee.googleimagesearcher.models.ImageModel;

import android.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageLoaderAdapter extends ArrayAdapter<ImageModel> {
  
  private static ImageLoaderAdapter instance = null;
  
  private ImageLoader imageLoader = ImageLoader.getInstance();
  private DisplayImageOptions options;
  private Context context;
  
  // Singleton to prevent reinitialization of the ImageLoader.
  protected ImageLoaderAdapter() {
    super(null, 0);
  }

  protected ImageLoaderAdapter(Context context, int resource) {
    super(context, resource);
    // TODO Auto-generated constructor stub
    this.context = context;
    
    this.options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .build();
    
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(this.options)
        .threadPoolSize(3)
        .denyCacheImageMultipleSizesInMemory()
        .memoryCacheSize(2 * 1024 * 1024)
        .discCacheSize(50 * 1024 * 1024)
        .writeDebugLogs()
        .build();
    
    imageLoader.init(config);
  }
  
  public static ImageLoaderAdapter getInstance(Context context, int resource) {
    if (instance == null) {
      instance = new ImageLoaderAdapter(context, resource);
    }
    return instance;
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    
    ImageView imageView;
    if (convertView == null) {
      imageView = new ImageView(this.context);
      imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setPadding(8, 8, 8, 8);
    } else {
      imageView = (ImageView) convertView;
    }
    Log.i("getView", String.valueOf(position));
    imageLoader.displayImage(this.getItem(position).getThumbnailUrl(), imageView);
    return imageView;
  }
  
  public ImageLoader getImageLoader() {
    return this.imageLoader;
  }

}
