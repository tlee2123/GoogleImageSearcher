package com.tlee.googleimagesearcher.models;

public class ImageModel {
  
  private String thumbnailUrl;
  private String url;
  private String title;
  
  public ImageModel(String title, String thumbnailUrl, String url) {
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public String getThumbnailUrl() {
    return this.thumbnailUrl;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public ImageModel getNewCopy() {
    return new ImageModel(this.title, this.thumbnailUrl, this.url);
  }

}
