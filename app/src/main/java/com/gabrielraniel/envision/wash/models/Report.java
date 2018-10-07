package com.gabrielraniel.envision.wash.models;

/**
 * Created by gabrielraniel on 26/10/2017.
 */

public class Report {

    String report_ID;
    String restroom_ID;
    String report_type;
    String restroom_name;
    String user;

    public Report(){

    }

    public Report(String report_ID, String restroom_ID, String report_type, String restroom_name, String user) {
        this.report_ID = report_ID;
        this.restroom_ID = restroom_ID;
        this.report_type = report_type;
        this.restroom_name = restroom_name;
        this.user = user;
    }

    public String getReport_ID() {
        return report_ID;
    }

    public String getRestroom_ID() {
        return restroom_ID;
    }

    public String getReport_type() {
        return report_type;
    }

    public String getRestroom_name() {
        return restroom_name;
    }

    public String getUser() {
        return user;
    }
}
