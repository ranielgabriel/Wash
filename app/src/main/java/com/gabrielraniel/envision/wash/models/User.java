package com.gabrielraniel.envision.wash.models;

/**
 * Created by gabrielraniel on 14/10/2017.
 */

public class User {
    private String UID;
    private String user_type;
    private String email;
    private String password;
    private String guide;

    private User() {

    }

    public User(String UID, String user_type, String email, String password, String guide) {
        this.UID = UID;
        this.user_type = user_type;
        this.email = email;
        this.password = password;
        this.guide = guide;
    }

    public String getUID() {
        return UID;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGuide() {
        return guide;
    }
}
