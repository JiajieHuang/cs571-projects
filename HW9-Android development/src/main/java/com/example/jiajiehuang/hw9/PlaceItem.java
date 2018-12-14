package com.example.jiajiehuang.hw9;

import android.os.Parcelable;

import java.io.Serializable;

public class PlaceItem implements Serializable{
    private String name="";
    private String address;
    private String icon;
    private String placeId;

    public PlaceItem(String name, String address, String icon, String placeId){
        this.name=name;
        this.address=address;
        this.icon=icon;
        this.placeId=placeId;
    }

    @Override
    public String toString() {
        return "{name:"+name+",address:"+address+",icon:"+icon+",placeId:"+placeId+"}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address =address;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon= icon;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @Override
    public int hashCode(){
        return placeId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return placeId.equals(((PlaceItem) obj).getPlaceId());
    }
}
