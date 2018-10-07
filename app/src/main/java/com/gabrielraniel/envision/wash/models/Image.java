package com.gabrielraniel.envision.wash.models;

/**
 * Created by gabrielraniel on 05/11/2017.
 */

public class Image {
    private String image_ID;
    private String image_name;
    private String download_url;
    private String business_id;

    public Image(){

    }

    public Image(String image_ID, String image_name, String download_url, String business_id) {
        this.image_ID = image_ID;
        this.image_name = image_name;
        this.download_url = download_url;
        this.business_id = business_id;
    }

    public String getImage_ID() {
        return image_ID;
    }

    public String getImage_name() {
        return image_name;
    }

    public String getDownload_url() {
        return download_url;
    }

    public String getBusiness_id() {
        return business_id;
    }
}
