package com.tlee.googleimagesearcher.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tlee.googleimagesearcher.models.ImageModel;

import android.R;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
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
  private int estimatedLoadingPosition = 0;
  
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
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater)this.context
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(0x7f030003, null);
      
      holder = new ViewHolder();
      holder.view = (ImageView)convertView.findViewById(0x7f09000c);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder)convertView.getTag();
    }
    imageLoader.displayImage(this.getItem(position).getThumbnailUrl(), holder.view);
    estimatedLoadingPosition = position;
    return convertView;
  }
  
  public ImageLoader getImageLoader() {
    return this.imageLoader;
  }
  
  public int getEstimatedPosition() {
    return estimatedLoadingPosition;
  }
  
  static class ViewHolder {
    ImageView view;
  }

}
