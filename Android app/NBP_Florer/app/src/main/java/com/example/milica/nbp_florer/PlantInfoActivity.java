package com.example.milica.nbp_florer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantInfoActivity extends AppCompatActivity {

    private ImageView plant_image_full;
    private TextView etxtLatin;
    private TextView etxtAddedBy;
    private ImageButton btnMap;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private StringRequest stringRequestAddSuggestion;
    private StringRequest stringRequestAddTag;
    private StringRequest stringRequestDelete;
    private StringRequest stringRequestImages;
    private StringRequest stringRequestTags;
    private String mPlantLocationsURL = Constants.serverUrl + "/plantLocations";
    private String mPlantImagesURL = Constants.serverUrl + "/plantImages";
    private String mPlantTagsURL = Constants.serverUrl + "/plantTags";
    private String deleteTagForPlantURL = Constants.serverUrl + "/deleteTag";
    private String addTagForPlant = Constants.serverUrl + "/addTag";
    private String addSuggestionForPlant = Constants.serverUrl + "/addSuggestion";
    private String latitude, longitude;
    private String id_biljke;
    private PlantLocationList locationList;
    private ViewPager viewPager;
    private Session session;
    private String[] plant_images;
    public List<TextView> tags;
    private RelativeLayout tagsLayout;
    private RelativeLayout suggestionsLayout;
    private ImageButton btnWiki;
    private EditText etPlantName;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etxtLatin = findViewById(R.id.plant_latin);
        etxtAddedBy = findViewById(R.id.etxtAddedBy);
        btnMap = findViewById(R.id.btnMap);
        viewPager = findViewById(R.id.plant_info_viewPager);
        id_biljke = getIntent().getStringExtra("id_biljke");
        locationList = new PlantLocationList();
        tagsLayout = findViewById(R.id.tags);
        suggestionsLayout = findViewById(R.id.suggestions);
        etPlantName = findViewById(R.id.etxtPlantName);
        etPlantName.setVisibility(View.INVISIBLE);
        btnWiki = findViewById(R.id.btnWiki);
        session = new Session(this);
        getIncomingIntent();

        getPlant_images(id_biljke);

        getPlant_tags(id_biljke);

        btnWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = new Intent(getApplicationContext(), WikiActivity.class);
               // i.putExtra("plant_name",etxtLatin.getText().toString());
                //startActivity(i);
            }
        });


        if(isServicesOK()){

            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PlantInfoActivity.this, MapActivity.class);
                    intent.putExtra("lokacije", locationList);
                    startActivity(intent);
                }
            });
        }

        requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, mPlantLocationsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray j;

                try {

                    j = new JSONArray(response);

                    for(int i = 0; i < j.length(); i++)
                    {
                        JSONObject jObj = j.getJSONObject(i);
                        String longitude = jObj.getString("longitude");
                        String latitude = jObj.getString("latitude");
                        locationList.addToList(new PlantLocation(longitude, latitude));
                    }
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
                hashMap.put("id_biljke", id_biljke);

                return hashMap;
            }
        };

        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequest);

    }

    public void getPlant_images(final String id_plant) {

        stringRequestImages = new StringRequest(Request.Method.POST, mPlantImagesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray j;

                try {

                    j = new JSONArray(response);
                    plant_images = new String[j.length()];

                    for(int i = 0; i < j.length(); i++)
                    {
                        JSONObject jObj = j.getJSONObject(i);
                        String imageURL = Constants.ftpServerUrl + "/uploads/" + jObj.getString("lokacija_slike");
                        plant_images[i] = imageURL;
                    }

                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), plant_images);
                    viewPager.setAdapter(viewPagerAdapter);
                }
                catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_biljke", id_plant);

                return hashMap;
            }
        };

        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestImages);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PlantInfoActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PlantInfoActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getIncomingIntent() {

        if(getIntent().hasExtra("plant_latin") && getIntent().hasExtra("addedBy")) {
            this.etxtLatin.setText(getIntent().getStringExtra("plant_latin"));
            this.etxtAddedBy.setText(getIntent().getStringExtra("addedBy"));

        }
    }

    public void getPlant_tags(final String id_plant) {

        stringRequestTags = new StringRequest(Request.Method.POST, mPlantTagsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray j;

                try {

                    j = new JSONArray(response);
                    tags = new ArrayList<>();

                    for(int i = 0; i < j.length(); i++)
                    {
                        JSONObject jObj = j.getJSONObject(i);
                        String tag = jObj.getString("tag");

                        TextView tagg = new TextView(getApplicationContext());
                        tagg.setText(tag);
                        tagg.setPadding(15,5,5,5);
                        tagg.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                        //tagg.setBackgroundResource(R.color.colorPrimary);
                       // if(session.loggedin())
                          //  tagg.setBackgroundResource(R.drawable.left_tag_corners);
                       // else
                        //    tagg.setBackgroundResource(R.drawable.all_tag_borders);

                        tagg.setTypeface(null, Typeface.BOLD);

                        tags.add(tagg);
                    }

                    ShowTags(id_plant);
                    ShowAddSuggestion(id_plant);
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
                hashMap.put("id_biljke", id_plant);

                return hashMap;
            }
        };

        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestTags);
    }

    public void ShowTags(final String id_biljke){

        for(int i = 0; i < tags.size(); i++) {

            if(i == 0) {

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = i * 110;
                params.setMarginStart(20);

                tags.get(i).setLayoutParams(params);
                tags.get(i).setElevation(10);
                tags.get(i).setTextSize(20);

                tagsLayout.addView(tags.get(i));
            }
            else {

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = i * 110;
                params.setMarginStart(20);

                tags.get(i).setLayoutParams(params);
                tags.get(i).setElevation(10);
                tags.get(i).setTextSize(20);

                tagsLayout.addView(tags.get(i));
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = i * 110;

            tags.get(i).measure(0, 0);
            params.setMarginStart(tags.get(i).getMeasuredWidth() + 20);
            params.height = tags.get(i).getMeasuredHeight();

            if(session.loggedin()) {

                ImageButton button = new ImageButton(getApplicationContext());
           //     button.setImageResource(R.drawable.ic_close_white_24dp);
            //    button.setBackgroundResource(R.drawable.right_tag_corners);
                button.setElevation(10);

                button.setLayoutParams(params);
                button.setTag(i);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        delete_tag_from_db(tags.get((int) v.getTag()).getText().toString(), id_biljke);

                        tagsLayout.removeView(tags.get((int) v.getTag()));
                        tags.remove(tags.get((int) v.getTag()));

                        tagsLayout.removeView(v);
                        tagsLayout.removeAllViews();
                        tagsLayout.refreshDrawableState();

                        ShowTags(id_biljke);
                    }
                });

                tagsLayout.addView(button);
            }
        }

        if(session.loggedin()) {

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = tags.size() * 110;
            params.setMarginStart(15);
            params.width = 400;

            final EditText editText = new EditText(getApplicationContext());
            editText.setHint("add tag");
            editText.setLayoutParams(params);
            tagsLayout.addView(editText);

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params1.topMargin = tags.size() * 110;
            params1.setMarginStart(400 + 30);
            editText.measure(0, 0);
            params1.height = editText.getMeasuredHeight();

            ImageButton imgButton = new ImageButton(getApplicationContext());
         //   imgButton.setBackgroundResource(R.color.transparent);
        //    imgButton.setImageResource(R.drawable.plus_icon);
            imgButton.setLayoutParams(params1);
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String txtTag = editText.getText().toString();

                    if (!txtTag.equals("")) {

                        for (TextView t : tags) {

                            if (t.getText().toString().equalsIgnoreCase(txtTag))
                                return;
                        }

                        insert_tag_to_db(txtTag, id_biljke);
                    }
                }
            });

            tagsLayout.addView(imgButton);
        }
    }

    public void ShowAddSuggestion(final String id_biljke)
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 50;
        params.setMarginStart(15);
        params.width = 400;

        final EditText editText = new EditText(getApplicationContext());
        editText.setHint("add suggestion");
        editText.setLayoutParams(params);
        suggestionsLayout.addView(editText);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.topMargin = 50;
        params1.setMarginStart(400 + 30);
        editText.measure(0, 0);
        params1.height = editText.getMeasuredHeight();

        ImageButton imgButton = new ImageButton(getApplicationContext());
        //   imgButton.setBackgroundResource(R.color.transparent);
        //    imgButton.setImageResource(R.drawable.plus_icon);
        imgButton.setLayoutParams(params1);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suggestion = editText.getText().toString();

                insert_suggestion(suggestion, id_biljke);

            }
        });

        suggestionsLayout.addView(imgButton);
    }


    private void insert_suggestion(final String txtSuggestion, final String id_biljke) {

        stringRequestAddSuggestion = new StringRequest(Request.Method.POST, addSuggestionForPlant,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    String newName = j.getString("latinski_naziv");
                    if(!newName.equals(etPlantName.getText().toString())) {
                        etPlantName.setText(newName);
                        Toast.makeText(getApplicationContext(), "Your suggestion "
                                        + newName + " will be considered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Successfully inserted",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                };

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_biljke", id_biljke);
                hashMap.put("suggestion", txtSuggestion);

                return hashMap;
            }
        };
        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestAddSuggestion);
    }

    private void insert_tag_to_db(final String txtTag, final String id_biljke) {

        stringRequestAddTag = new StringRequest(Request.Method.POST, addTagForPlant,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                TextView tagg = new TextView(getApplicationContext());
                tagg.setText(txtTag);
                tagg.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

                if(session.loggedin())
                    tagg.setBackgroundResource(R.drawable.left_tag_corners);
                else
                    tagg.setBackgroundResource(R.drawable.all_tag_borders);

                tagg.setTypeface(null, Typeface.BOLD);
                tagg.setPadding(15,5,5,5);

                tags.add(tagg);

                tagsLayout.removeAllViews();
                tagsLayout.refreshDrawableState();

                ShowTags(id_biljke);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {}

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_biljke", id_biljke);
                hashMap.put("tag", txtTag);

                return hashMap;
            }
        };

        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestAddTag);
    }

    private void delete_tag_from_db(final String tag, final String id_biljke) {

        stringRequestDelete = new StringRequest(Request.Method.POST, deleteTagForPlantURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {}

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_biljke", id_biljke);
                hashMap.put("tag", tag);

                return hashMap;
            }
        };

        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestDelete);
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
