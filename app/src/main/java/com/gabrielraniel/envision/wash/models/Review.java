package com.gabrielraniel.envision.wash.models;

import java.util.Map;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class Review {
    private String review_ID;
    private String restroom_name;
    private String user;
    private String review;
    private float user_rating;
    private String date_time;

    private Review() {

    }

    public Review(String review_ID, String restroom_name, String user, String review, float user_rating, String date_time) {
        this.review_ID = review_ID;
        this.restroom_name = restroom_name;
        this.user = user;
        this.review = review;
        this.user_rating = user_rating;
        this.date_time = date_time;
    }

    public String getReview_ID() {
        return review_ID;
    }

    public String getRestroom_name() {
        return restroom_name;
    }

    public String getUser() {
        return user;
    }

    public String getReview() {
        return review;
    }

    public float getUser_rating() {
        return user_rating;
    }

    public String getDate_time() {
        return date_time;
    }
}
