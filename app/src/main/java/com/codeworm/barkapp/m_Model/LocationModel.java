package com.codeworm.barkapp.m_Model;

/**
 * Created by Mariah on 11/03/2018.
 */

public class LocationModel {
    Long lat,lng,ratio,total_slot;
    String name;
    String key;

    public LocationModel(){

    }
    public Long getLat(){return lat;}
    public void setLat(Long lat){this.lat=lat;}

    public Long getLng(){return lng;}
    public void setLng(Long lng){this.lng=lng;}


    public Long getRatio(){ return ratio;}
    public void setRatio(Long ratio){this.ratio = ratio;}

    public Long getTotal_slot(){ return total_slot;}
    public void setTotal_slot(Long total_slot){this.total_slot = total_slot;}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(LocationModel newlocationModel) {
        name = newlocationModel.name;
    }


}
