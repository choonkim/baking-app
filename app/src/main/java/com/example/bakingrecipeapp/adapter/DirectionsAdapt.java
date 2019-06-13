package com.example.bakingrecipeapp.adapter;

import org.parceler.Parcel;

@Parcel
public class DirectionsAdapt {

    String id;
    String shortDescription;
    String description;
    String videoURL;
    String thumbnailURL;

    public DirectionsAdapt(){
        // constructor
    }

    public String getId() {
        return id;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public String getDescription() {
        return description;
    }
    public String getVideoURL() {
        return videoURL;
    }
    public String getThumbnailURL() {
        return thumbnailURL;
    }
}
