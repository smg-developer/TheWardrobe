package com.smg.thewardrobe.models;

public class ImageWearModel {

    public long id;
    public byte[] imageBlob;

    public ImageWearModel(long id, byte[] imageBlob){
        this.id = id;
        this.imageBlob = imageBlob;
    }

}
