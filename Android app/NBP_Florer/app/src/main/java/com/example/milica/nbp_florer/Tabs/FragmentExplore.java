package com.example.milica.nbp_florer.Tabs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.example.milica.nbp_florer.*;
/**
 * Created by Milica on 16-Apr-18.
 */

public class FragmentExplore extends Fragment implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private MapResolver mapResolver;
    private StringRequest stringRequest;
    private StringRequest stringLocationRequest;
    private String[] plant_names;
    private String getPlantNamesURL = Constants.serverUrl + "/plants";
    private String getPlantLocationsURL = Constants.serverUrl + "/plantLocations";
    private ImageButton magnifier;

    private View view;
    private PlantLocationList locationList;

    private AutoCompleteTextView autoCompleteTextView;
    private CheckInternet checkInternet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_explore, container, false);

        autoCompleteTextView = view.findViewById(R.id.map_explore_auto_complete);
        autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        locationList = new PlantLocationList();

        checkInternet = new CheckInternet();
        checkInternet.checkLocation(view.getContext());

        mapResolver = new MapResolver(getContext());

        initMap();

        stringRequest = new StringRequest(Request.Method.GET, getPlantNamesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray j;

                try {

                    j = new JSONArray(response);

                    plant_names = new String[j.length()];

                    for(int i = 0; i < j.length(); i++)
                    {
                        JSONObject jObj = j.getJSONObject(i);
                        plant_names[i] = jObj.getString("latinski_naziv");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, plant_names);
                    autoCompleteTextView.setAdapter(adapter);

                    setClickListener();

                }
                catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {}

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();

                return hashMap;
            }
        };

        RequestSingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

        return view;
    }

    private void setClickListener() {

        /*autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!checkInternet.isInternetAvailable(getContext()) || !checkInternet.checkLocation(getContext())) {

                    Toast.makeText(getContext(), "Not connected to Network or GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                stringLocationRequest = new StringRequest(Request.Method.POST, getPlantLocationsURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONArray j;

                        try {

                            locationList.clearList();
                            mMap.clear();

                            j = new JSONArray(response);

                            for(int i = 0; i < j.length(); i++)
                            {
                                JSONObject jObj = j.getJSONObject(i);
                                String longitude = jObj.getString("LONGITUDE");
                                String latitude = jObj.getString("LATITUDE");
                                locationList.addToList(new PlantLocation(longitude, latitude));
                            }

                            addMarkers();
                        }
                        catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {}

                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("latinski_naziv", autoCompleteTextView.getText().toString());

                        return hashMap;
                    }
                };

                RequestSingleton.getInstance(getContext()).addToRequestQueue(stringLocationRequest);
            }
        });*/

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(!checkInternet.isInternetAvailable(getContext()) || !checkInternet.checkLocation(getContext())) {

                        Toast.makeText(getContext(), "Not connected to Network or GPS", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    stringLocationRequest = new StringRequest(Request.Method.GET,
                            getPlantLocationsURL + "?plant_name=" + autoCompleteTextView.getText().toString(),
                            new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONArray j;

                            try {

                                locationList.clearList();
                                mMap.clear();

                                j = new JSONArray(response);
                                for(int i = 0; i< j.length(); i++)
                                {
                                    JSONObject object  = j.getJSONObject(i);
                                    JSONArray arr = object.getJSONArray("slike");
                                    for(int k = 0; k < arr.length(); k++)
                                    {
                                        JSONObject jObj = arr.getJSONObject(k);

                                        String longitude = jObj.getString("longitude");
                                        String latitude = jObj.getString("latitude");
                                        locationList.addToList(new PlantLocation(longitude, latitude));
                                    }
                                }
//                                JSONArray arr = j.getJSONArray("slike");
//                                for(int i = 0; i < arr.length(); i++)
//                                {
//                                    JSONObject jObj = arr.getJSONObject(i);
//
//                                    String longitude = jObj.getString("longitude");
//                                    String latitude = jObj.getString("latitude");
//                                    locationList.addToList(new PlantLocation(longitude, latitude));
//                                }

                                addMarkers();
                            }
                            catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {}

                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            HashMap<String, String> hashMap = new HashMap<>();
                            //hashMap.put("latinski_naziv", autoCompleteTextView.getText().toString());

                            return hashMap;
                        }
                    };

                    RequestSingleton.getInstance(getContext()).addToRequestQueue(stringLocationRequest);

                    return  true;
                }

                return  false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(checkInternet.checkLocation(getContext()) && mapResolver.getLastKnownLocation() != null) {

            moveCamera(new LatLng(mapResolver.getLastKnownLocation().getLatitude(), mapResolver.getLastKnownLocation().getLongitude()), 1f);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
        else {

            Toast.makeText(getContext(), "GPS not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_explore);

        mapFragment.getMapAsync(FragmentExplore.this);
    }

    private void moveCamera(LatLng loc, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }

    private void getLocationPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this.getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this.getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;

                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;

                    initMap();
                }
            }
        }
    }

    private void addMarkers() {

        for (int i = 0; i < locationList.getLocations().size(); i++) {

            double latitude = Double.parseDouble(locationList.getLocations().get(i).getLatitude());
            double longitude = Double.parseDouble(locationList.getLocations().get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(Integer.toString(i)));//.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_test2)));
        }
    }
}
