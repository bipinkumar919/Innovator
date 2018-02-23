package com.example.akshay.noblind;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by akshay on 3/1/18.
 */

public class ModelMarker implements Serializable {
    private double lat;
    private double lung;
    private String Cname;
    private String Cdesc;
    private boolean inRange;

    public boolean isInRange() {
        return inRange;
    }

    public void setInRange(boolean inRange) {
        this.inRange = inRange;
    }

    public ModelMarker(ModelMarker m){
        this.lat = m.lat;
        this.lung = m.lung;
        this.Cname = m.Cname;
        this.Cdesc = m.Cdesc;
        inRange = false;
    }

    public String getCname() {
        return Cname;
    }

    public String getCdesc() {
        return Cdesc;
    }

    public double getLat() {
        return lat;
    }

    public double getLung() {
        return lung;
    }

    public ModelMarker(String Cname, String Cdesc, double lat, double lung){
        this.Cname = Cname;
        this.Cdesc = Cdesc;
        this.lat = lat;
        this.lung = lung;
        inRange = false;

    }

    public ModelMarker(){

    }
}

