package com.example.mapdir;

import com.google.android.gms.maps.model.LatLng;

public class Pincode {
    private String threatlevel;
    private String P_latitude;
    private String P_longitude;
    private String pincode;

    public String getP_latitude() {
        return P_latitude;
    }

    public void setP_latitude(String p_latitude) {
        P_latitude = p_latitude;
    }

    public String getP_longitude() {
        return P_longitude;
    }

    public void setP_longitude(String p_longitude) {
        P_longitude = p_longitude;
    }

    public Pincode() {

    }

    public String getThreatlevel() {
        return threatlevel;
    }

    public void setThreatlevel(String threatlevel) {
        this.threatlevel = threatlevel;
    }



    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


}
