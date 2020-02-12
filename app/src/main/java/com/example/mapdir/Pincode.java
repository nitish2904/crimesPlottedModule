package com.example.mapdir;

import com.google.android.gms.maps.model.LatLng;

public class Pincode {
    private String threatlevel;
    private LatLng coordinates;
    private String pincode;

    public Pincode() {

    }

    public String getThreatlevel() {
        return threatlevel;
    }

    public void setThreatlevel(String threatlevel) {
        this.threatlevel = threatlevel;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


}
