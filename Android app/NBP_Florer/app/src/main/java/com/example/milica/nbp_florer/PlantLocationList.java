package com.example.milica.nbp_florer;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.milica.nbp_florer.PlantLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milica on 25-May-18.
 */

public class PlantLocationList implements Parcelable {

    List<PlantLocation> locationList;

    public PlantLocationList() {

        locationList = new ArrayList<PlantLocation>();
    }

    public void addToList(PlantLocation location) {

        this.locationList.add(location);
    }

    public void clearList() {

        this.locationList.clear();
    }

    public void removeFromList(PlantLocation pl) {

        this.locationList.remove(pl);
    }

    public List<PlantLocation> getLocations(){

        return  this.locationList;
    }

    protected PlantLocationList(Parcel in) {
        locationList = new ArrayList<PlantLocation>();
        in.readTypedList(locationList, PlantLocation.CREATOR);
    }

    public static final Creator<PlantLocationList> CREATOR = new Creator<PlantLocationList>() {
        @Override
        public PlantLocationList createFromParcel(Parcel in) {
            return new PlantLocationList(in);
        }

        @Override
        public PlantLocationList[] newArray(int size) {
            return new PlantLocationList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeTypedList(this.locationList);
    }
}
