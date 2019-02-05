package com.example.milica.nbp_florer;

import android.Manifest;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.milica.nbp_florer.CheckInternet;
import com.example.milica.nbp_florer.FileResolver;
import com.example.milica.nbp_florer.MainActivity;
import com.example.milica.nbp_florer.MapResolver;
import com.example.milica.nbp_florer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import com.example.milica.nbp_florer.Session;

import static okhttp3.MediaType.*;


public class PlantUploadActivity extends AppCompatActivity {

    private ImageView plantView;
    private EditText txtlatinskiNaziv;
    private Button btnUpload;
    private String plant_name;
    private Session session;
    private String mCurrentPhotoPath;
    private FileResolver fileResolver;
    private File photoPath;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    private LocationListener listener;
    private double latitude;
    private double longitude;
    private String country;
    private MapResolver mapResolver;
    private RequestQueue requestQueue;
    private CheckInternet checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_upload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        plantView = (ImageView) findViewById(R.id.plantView);
        txtlatinskiNaziv = (EditText) findViewById(R.id.txtLatinskiNaziv);
        btnUpload = (Button) findViewById(R.id.btnUploadImage);
        session = new Session(getApplicationContext());
        fileResolver = new FileResolver();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        configure_button();
        mCurrentPhotoPath = getIntent().getStringExtra("path");
        photoPath = getPhoto();
        mapResolver = new MapResolver(this);
        checkInternet = new CheckInternet();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 15:
                configure_button();
                break;
            default:
                break;
        }
    }

    private void configure_button() {

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkInternet.checkLocation(getApplicationContext()) || !checkInternet.checkLocation(getApplicationContext()) ||
                        mapResolver.getLastKnownLocation() == null)
                {
                    Toast.makeText(getApplicationContext(), "Location or internet not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                plant_name = txtlatinskiNaziv.getText().toString();
                if (plant_name.matches("")) {

                    Toast.makeText(getApplicationContext(), "Latinski naziv mora da bude unesen", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 15);
                    }
                    return;
                }

                Location location = mapResolver.getLastKnownLocation();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //getAddress(latitude, longitude);
                makeRequest();
                //Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public File getPhoto() {

        Bitmap bitmap;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        File f = new File(mCurrentPhotoPath);
        bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, bmOptions);
            fileResolver.setBitmap(bitmap);
            this.plantView.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();
        };

        return f;
    }

    public void makeRequest() {

        progressDialog = new ProgressDialog(PlantUploadActivity.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        final String username = session.getUsername();

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                String content_type = getMimeType(photoPath.getPath());
                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse(content_type), photoPath);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", content_type)
                        .addFormDataPart("uploaded_file", mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/") + 1), fileBody)
                        .build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(Constants.serverUrl + "/upload?latinski_naziv=" + plant_name + "&username=" + username +
                                "&lon=" + Double.toString(longitude) + "&lat=" + Double.toString(latitude) + "&tag=" + "Serbia")
                        .post(requestBody)
                        .build();

                try {

                    okhttp3.Response response = client.newCall(request).execute();

                    if(!response.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        throw new IOException("Error: " + response);
                    }

                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                catch (IOException e) {

                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public void getAddress(double lat, double lng) {

        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                Double.toString(lat) + "," + Double.toString(lng) + "&key=" + getString(R.string.google_maps_API_key), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String formatted_address = null;

                        try {
                            formatted_address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");

                            parseCountryName(formatted_address);
                            //Toast.makeText(getApplicationContext(), country, Toast.LENGTH_SHORT).show();

                            makeRequest();
                        } catch (JSONException e) {

                            if(formatted_address == null) {

                                country = null;
                                Toast.makeText(getApplicationContext(), country, Toast.LENGTH_SHORT).show();

                                makeRequest();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void parseCountryName(String address) {

        int lastIndexOf = address.lastIndexOf(" ");

        country = address.substring(lastIndexOf + 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
