package com.example.milica.nbp_florer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Milica on 25-May-18.
 */

public class PlantLocation implements Parcelable {

    private String latitude;
    private String longitude;

    public PlantLocation(String lon, String lat){

        this.longitude = lon;
        this.latitude = lat;
    }

    public PlantLocation(Parcel in) {
        latitude = in.readString();
        longitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    //PARCEABLE STUFF
    public static final Creator<PlantLocation> CREATOR = new Creator<PlantLocation>() {
        @Override
        public PlantLocation createFromParcel(Parcel in) {
            return new PlantLocation(in);
        }

        @Override
        public PlantLocation[] newArray(int size) {
            return new PlantLocation[size];
        }
    };

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String lon)
    {
        this.longitude = lon;
    }

    public void setLatitude(String lat)
    {
        this.latitude = lat;
    }




    @Override
    public int describeContents() {
        return 0;
    }
}
