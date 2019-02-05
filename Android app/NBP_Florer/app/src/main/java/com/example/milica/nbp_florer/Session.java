package com.example.milica.nbp_florer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Milica on 13-Apr-18.
 */

public class Session {

    public SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;

    public Session(Context ctx){
        this.context = ctx;
        prefs = ctx.getSharedPreferences("FlorerExplorer", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean loggedin){
        editor.putBoolean("loggedInmode",loggedin);
        editor.commit();
    }

    public void setUser(String user) {

        editor.putString("user", user);
        editor.commit();
    }

    public String getUsername() {
        return prefs.getString("user", "");
    }

    public void setPlantLike(String id_biljke) {

        editor.putBoolean(id_biljke, true);
        editor.commit();
    }

    public void unsetPlantLike(String id_biljke) {

        editor.putBoolean(id_biljke, false);
        editor.commit();
    }

    public boolean isLiked(String id_biljke) {

        return prefs.getBoolean(id_biljke, false);
    }

    public void setID(String id) {

        editor.putString("id", id);
        editor.commit();
    }

    public void resetPrefs() {

        prefs.edit().clear().commit();
    }
    public String getId() {

        return prefs.getString("id", "0");
    }

    public boolean loggedin(){

        return prefs.getBoolean("loggedInmode", false);
    }
}
