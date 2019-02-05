package com.example.milica.nbp_florer;

import java.util.List;

/**
 * Created by Milica on 16-Jan-19.
 */

public class User {

    private int id;
    private String username;
    private String fullname;
    private String email;
    private String lokacija_slike;
    private String motherland;
    private String bio;
    private List<Plant> biljke;

    public User(int id, String username, String fullname, String email, String land, List<Plant> biljke) {

        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.lokacija_slike = "http://martinovic.webhop.me:32080/user_images/" + username + ".jpg";
        this.motherland = land;
        this.biljke = biljke;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLokacija_slike() {
        return lokacija_slike;
    }

    public void setLokacija_slike(String lokacija_slike) {
        this.lokacija_slike = lokacija_slike;
    }

    public String getMotherland() {
        return motherland;
    }

    public void setMotherland(String motherland) {
        this.motherland = motherland;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Plant> getBiljke() {
        return biljke;
    }

    public void setBiljke(List<Plant> biljke) {
        this.biljke = biljke;
    }
}
