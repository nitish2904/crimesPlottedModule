package com.example.mapdir;
import android.util.Log;
import android.widget.Toast;

import java.util.*;
import java.lang.*;
import java.util.Arrays;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class Predict {
    private Double[] latitude;
    private Double[] longitude;
    private Double[] threat;
    private Double[] distanceall;
    private Double checklat,checklong;
    private Double Answer=0.0;
    private int size;

    public Predict(int size,HashMap<String,Pincode>Hash,Double latitudeofuser,Double longitudeofuser)
    {
        latitude = new Double[size];
        longitude = new Double[size];
        threat = new Double[size];
        distanceall = new Double[size];
        this.checklat = latitudeofuser;
        this.checklong = longitudeofuser;
        this.size = size;

        Iterator it = Hash.entrySet().iterator();
        int count =0;
        while(it.hasNext())
        {
           // Pincode details = me.getValue();
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Pincode value =(Pincode) pair.getValue();
            latitude[count]= Double.valueOf(value.getP_latitude());
            longitude[count]= Double.valueOf(value.getP_longitude());
            threat[count]=Double.valueOf(value.getThreatlevel());
            //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
          //  Log.i(TAG, "Predict: "+value.getP_latitude());
            //System.out.println(value.getP_latitude());
        }
        for(int i=0;i<this.size;i++)
        {
            distanceall[i] = distance(latitude[i],checklat,longitude[i],checklong);
        }
//normalise(threat);
//normalise(distanceall);
       this.Answer=GetPrediction(threat,distanceall);
    }


    private Double GetPrediction(Double[] threat1, Double[] distanceall1)
    {
        Double sum=0.000;
        for(int i=0;i<size;i++)
        {
            sum+=threat1[i]*distanceall1[i];
        }
        return sum/5;
    }


    private void normalise(Double[] threat1) {
        Double min = Collections.min(Arrays.asList(threat1));
        Double max = Collections.max(Arrays.asList(threat1));
        for(int i=0;i<5;i++)
        {
            threat1[i]=(threat1[i]-min)/(max-min);
        }
    }

    public Double prediction()
    {
        Double ansswer=this.Answer;
        return  ansswer;
    }





    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

// The math module contains a function
// named toRadians which converts from
// degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

// Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

// Radius of earth in kilometers. Use 3956
// for miles
        double r = 6371;

// calculate the result
        return(c * r)*1000;
    }
}