package com.example.milica.nbp_florer.Comments;

import android.content.Context;

import com.example.milica.nbp_florer.Session;

/**
 * Created by Milica on 06-Feb-19.
 */

public class ChildComment {

    private String _id;
    private String komentar;
    private String datum;
    private String username;
    private String plant;
    private int numOfUpvotes;
    private String usernameImage;
    private Session session;

    public String get_id() {
        return this._id;
    }

    public void set_id(String id) {
        this._id = id;
    }

    public int getNumOfUpvotes() {
        return numOfUpvotes;
    }

    public void setNumOfUpvotes(int numOfUpvotes) {
        this.numOfUpvotes = numOfUpvotes;
    }

    public void incrementNumOfUpvotes() {
        this.numOfUpvotes++;
    }


    public String getUsernameImage() {
        return usernameImage;
    }

    public void setUsernameImage(String usernameImage) {
        this.usernameImage = usernameImage;
    }



    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public ChildComment(String komentar, String datum, String username, Context context) {
        this.komentar = komentar;
        this.datum = datum;
        this.username = username;
        session = new Session(context);
    }

}
