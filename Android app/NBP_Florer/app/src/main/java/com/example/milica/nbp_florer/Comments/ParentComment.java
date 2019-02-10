package com.example.milica.nbp_florer.Comments;

import android.content.Context;

import com.example.milica.nbp_florer.Session;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Milica on 06-Feb-19.
 */

public class ParentComment extends ExpandableGroup {
    private String komentar;
    private String datum;
    private String username;
    private String urlSlike;
    private int numOfUpvotes;
    private boolean isUpvoted;
    private boolean editTextVisibiliy;
    private String _id;

    public String getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    private Session session;
    private String plant_id;

    public boolean isEditTextVisibiliy() {
        return editTextVisibiliy;
    }

    public void setEditTextVisibiliy(boolean editTextVisibiliy) {
        this.editTextVisibiliy = editTextVisibiliy;
    }

    public boolean getEditTextVisibility() {
        return this.editTextVisibiliy;
    }

    public String get_id() {
        return this._id;
    }
    public void set_id(String id) {
        this._id = id;
    }

    public boolean isUpvoted() {
        return isUpvoted;
    }

    public void setUpvoted(boolean upvoted) {
        isUpvoted = upvoted;
    }

    public int getBrojUpvote() {
        return numOfUpvotes;
    }

    public void setBrojUpvote(int brojUpvote) {
        this.numOfUpvotes = brojUpvote;
    }

    public String getUrlSlike() {
        return urlSlike;
    }
    public void setUrlSlike(String urlSlike) {
        this.urlSlike = urlSlike;
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

    public ParentComment(String title, List items, Context context) {
        super(title, items);
        session = new Session(context);
    }
}
