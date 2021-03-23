package com.smg.thewardrobe.models;

public class FavoriteWearModel {

    public long id;
    public long top_id;
    public long bottom_id;

    public FavoriteWearModel(long id, long top_id, long bottom_id ){
        this.id = id;
        this.top_id = top_id;
        this.bottom_id = bottom_id;
    }

}
