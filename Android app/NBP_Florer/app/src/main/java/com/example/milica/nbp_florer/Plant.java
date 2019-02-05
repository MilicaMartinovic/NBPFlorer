package com.example.milica.nbp_florer;

import android.support.annotation.NonNull;

/**
 * Created by Milica on 16-Jan-19.
 */

public class Plant implements Comparable<Plant>{

    private String id_plant;
    private int id_user;
    private String latinski_naziv;
    private String lokacija_slike;
    private String user_name;
    private String datum_dodavanja;

    /*public Plant(int id_plant, String user_name, String lat, String lok) {

        this.user_name = user_name;
        this.id_plant = id_plant;
        this.latinski_naziv = lat;
        this.lokacija_slike = lok;
    }*/

    public Plant(String id_plant, String user_name, String lat, String lok, String datum) {

        this.id_plant = id_plant;
        this.user_name = user_name;
        this.latinski_naziv = lat;
        this.lokacija_slike = lok;
        this.datum_dodavanja = datum;
    }
    public String getId_plant() {
        return id_plant;
    }

    public void setId_plant(String id_plant) {
        this.id_plant = id_plant;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getLatinski_naziv() {
        return latinski_naziv;
    }

    public void setLatinski_naziv(String latinski_naziv) {
        this.latinski_naziv = latinski_naziv;
    }

    public String getLokacija_slike() {
        return Constants.ftpServerUrl + "/uploads/" + lokacija_slike;
    }

    public void setLokacija_slike(String lokacija_slike) {
        this.lokacija_slike = lokacija_slike;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDatum_dodavanja() {
        return datum_dodavanja;
    }

    public void setDatum_dodavanja(String datum_dodavanja) {
        this.datum_dodavanja = datum_dodavanja;
    }

    @Override
    public int compareTo(@NonNull Plant o) {
        return 0;
    }
}
