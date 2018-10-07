package com.gabrielraniel.envision.wash.models;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class Rating {
    private String rating_ID;
    private String restroom_name;
    private String user;
    private float user_rating;

    public Rating() {

    }

    public Rating(String rating_ID, String restroom_name, String user, float user_rating) {
        this.rating_ID = rating_ID;
        this.restroom_name = restroom_name;
        this.user = user;
        this.user_rating = user_rating;
    }

    public String getRating_ID() {
        return rating_ID;
    }

    public String getRestroom_name() {
        return restroom_name;
    }

    public String getUser() {
        return user;
    }

    public float getUser_rating() {
        return user_rating;
    }
}
