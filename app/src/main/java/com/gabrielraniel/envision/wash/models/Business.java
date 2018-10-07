package com.gabrielraniel.envision.wash.models;

/**
 * Created by gabrielraniel on 06/12/2017.
 */

public class Business {

    private String business_ID;
    private String business_owner;
    private String business_name;

    public Business(){

    }

    public Business(String business_ID, String business_owner, String business_name) {
        this.business_ID = business_ID;
        this.business_owner = business_owner;
        this.business_name = business_name;
    }

    public String getBusiness_ID() {
        return business_ID;
    }

    public String getBusiness_owner() {
        return business_owner;
    }

    public String getBusiness_name() {
        return business_name;
    }
}
