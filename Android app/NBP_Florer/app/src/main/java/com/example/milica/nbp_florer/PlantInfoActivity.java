package com.example.milica.nbp_florer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.UnicodeSetSpanner;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.milica.nbp_florer.Comments.ChildComment;
import com.example.milica.nbp_florer.Comments.ParentComment;
import com.example.milica.nbp_florer.Comments.ParentCommentAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PlantInfoActivity extends AppCompatActivity implements CustomReplyDialog.CustomReplyDialogListener{

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
    private StringRequest stringRequestComments;
    private StringRequest stringRequestAddCommen;
    private String mPlantLocationsURL = Constants.serverUrl + "/plantLocations";
    private String mPlantImagesURL = Constants.serverUrl + "/plantImages";
    private String mPlantTagsURL = Constants.serverUrl + "/plantTags";
    private String deleteTagForPlantURL = Constants.serverUrl + "/deleteTag";
    private String addTagForPlant = Constants.serverUrl + "/addTag";
    private String addSuggestionForPlant = Constants.serverUrl + "/addSuggestion";
    private String mComments = Constants.serverUrl + "/comments";
    private String mAddComment = Constants.serverUrl + "/addComment";
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
    private ImageView imgLeaveComment;
    private EditText etLeaveComment;
    private Button btnLeaveComment;
    private String sendReply = Constants.serverUrl + "/addComment";
    private StringRequest stringRequestAddComment;

    //-----------------------
    private RecyclerView recyclerView;
    private ParentCommentAdapter parentCommentAdapter;
    private List<ParentComment> parentCommentList;
    //-----------------------

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
        btnLeaveComment = findViewById(R.id.btnLeaveComment);
        etLeaveComment = findViewById(R.id.etLeaveComment);
        imgLeaveComment = findViewById(R.id.imgLeaveCommentPicture);

        getIncomingIntent();

        //------------------------------------------
        recyclerView = findViewById(R.id.recycler_view_comment);

        parentCommentList = new ArrayList<>();


        getComments(id_biljke);
        //-------------------------------------------

        getPlant_images(id_biljke);

        getPlant_tags(id_biljke);

        SetupLeaveComment();

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

    public String EpochSecondsToDate(long milis) {
        Date date = new Date(milis);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    private void getComments(final String id_biljke) {
        stringRequestComments = new StringRequest(Request.Method.POST, mComments, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONArray array = new JSONArray(response);
                    for(int i = 0; i<array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        JSONObject comment = obj.getJSONObject("comment");
                        JSONArray children = obj.getJSONArray("children");
                        List<ChildComment> childComments = new ArrayList<>();
                        for(int j = 0; j<children.length(); j++)
                        {
                            JSONObject child = (JSONObject) children.get(j);
                            String date = EpochSecondsToDate(Long.parseLong(child.getString("datum_dodavanja")));
                            childComments.add(new ChildComment(child.getString("komentar"),
                                                                date,
                                                                child.getString("username"),
                                                                getApplicationContext()));
                            childComments.get(j).setUsernameImage("https://cdn1.vectorstock.com/i/1000x1000/25/70/user-icon-woman-profile-human-avatar-vector-10552570.jpg");
                            childComments.get(j).setNumOfUpvotes(child.getInt("numOfUpvotes"));
                            childComments.get(j).set_id(child.getString("_id"));
                        }
                        parentCommentList.add(new ParentComment("", childComments, getApplicationContext()));
                        parentCommentList.get(i).setKomentar(comment.getString("komentar"));
                        String date = EpochSecondsToDate(Long.parseLong(comment.getString("datum_dodavanja")));
                        parentCommentList.get(i).setDatum(date);
                        parentCommentList.get(i).setUsername(comment.getString("username"));
                        parentCommentList.get(i).setUrlSlike("https://cdn1.vectorstock.com/i/1000x1000/25/70/user-icon-woman-profile-human-avatar-vector-10552570.jpg");
                        parentCommentList.get(i).setBrojUpvote(comment.getInt("numOfUpvotes"));
                        parentCommentList.get(i).set_id(comment.getString("_id"));
                        parentCommentList.get(i).setPlant_id(id_biljke);
                    }
                    parentCommentAdapter = new ParentCommentAdapter(parentCommentList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PlantInfoActivity.this));
                    recyclerView.setAdapter(parentCommentAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_plant", id_biljke);

                return hashMap;
            }
        };
        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestComments);

    }

    public void SetupLeaveComment() {
        btnLeaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendComment(etLeaveComment.getText().toString());
            }
        });
    }

    public void SendComment(final String comment) {
        stringRequestAddCommen = new StringRequest(Request.Method.POST, mAddComment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_SHORT).show();
                parentCommentList.add(new ParentComment("",null,getApplicationContext()));
                parentCommentList.get(parentCommentList.size() - 1).setUsername(session.getUsername());
                parentCommentList.get(parentCommentList.size() - 1).setBrojUpvote(0);
                parentCommentList.get(parentCommentList.size() - 1).setDatum(new Date().toString());
                parentCommentList.get(parentCommentList.size() - 1).setKomentar(comment);

                parentCommentAdapter = new ParentCommentAdapter(parentCommentList);
                recyclerView.setLayoutManager(new LinearLayoutManager(PlantInfoActivity.this));
                recyclerView.setAdapter(parentCommentAdapter);

                etLeaveComment.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Something happened, try again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("plant_id", id_biljke);
                hashMap.put("username", session.getUsername());
                hashMap.put("comment", comment);
                hashMap.put("parent", "");
                return hashMap;
            }
        };
        RequestSingleton.getInstance(PlantInfoActivity.this).addToRequestQueue(stringRequestAddCommen);
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

    @Override
    public void applyResult(final String comment, final String comm_id) {
        Toast.makeText(getApplicationContext(), "ovde uslo plant ingfo", Toast.LENGTH_SHORT).show();
        stringRequestAddComment = new StringRequest(Request.Method.POST, sendReply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Successfully replied", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Something happened, try again", Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("username", session.getUsername());
                hashMap.put("comment", comment);
                hashMap.put("parent", comm_id);
                hashMap.put("plant_id", id_biljke);
                return hashMap;
            }
        };
        RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestAddComment);
    }
}
