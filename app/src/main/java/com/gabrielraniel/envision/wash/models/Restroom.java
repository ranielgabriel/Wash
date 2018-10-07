package com.gabrielraniel.envision.wash.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class Restroom {
    String restroom_ID;
    String business_ID;
    String location;
    String restroom_name;
    String enabled;
    String restroom_owner;
    String restroom_floor;
    int report_counts;


    public Restroom() {

    }

    public Restroom(String restroom_ID, String business_ID, String location, String restroom_name, String enabled, String restroom_owner, String restroom_floor, int report_counts) {
        this.restroom_ID = restroom_ID;
        this.business_ID = business_ID;
        this.location = location;
        this.restroom_name = restroom_name;
        this.enabled = enabled;
        this.restroom_owner = restroom_owner;
        this.restroom_floor = restroom_floor;
        this.report_counts = report_counts;
    }

    public String getRestroom_ID() {
        return restroom_ID;
    }

    public String getBusiness_ID() {
        return business_ID;
    }

    public String getLocation() {
        return location;
    }

    public String getRestroom_name() {
        return restroom_name;
    }

    public String getEnabled() {
        return enabled;
    }

    public String getRestroom_owner() {
        return restroom_owner;
    }

    public String getRestroom_floor() {
        return restroom_floor;
    }

    public int getReport_counts() {
        return report_counts;
    }
}
