package com.example.milica.nbp_florer.Tabs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.milica.nbp_florer.CheckInternet;
import com.example.milica.nbp_florer.Constants;
import com.example.milica.nbp_florer.Plant;
import com.example.milica.nbp_florer.R;
import com.example.milica.nbp_florer.RequestSingleton;
import com.example.milica.nbp_florer.Session;
import com.example.milica.nbp_florer.User;
/**
 * Created by Milica on 16-Apr-18.
 */

public class FragmentFeed extends Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CustomPlantAdapter adapter;
    private List<Plant> plant_list;
    public static String getPlantsURL = Constants.serverUrl + "/plants";
    private String getTagsURL = "http://martinovic.webhop.me:32080/php/get_all_tags.php";
    private String getPlantsByTagURL = "http://martinovic.webhop.me:32080/php/get_plants_by_tag.php";
    private String getUsersURL = "http://martinovic.webhop.me:32080/php/get_users.php";
    private String getUsersByTagURL = "http://martinovic.webhop.me:32080/php/get_users_by_tag.php";

    private SwipeRefreshLayout swipeRefreshLayout;
    private Session session;
    private AutoCompleteTextView autoCompleteTextView;
    private StringRequest stringRequest;
    private List<String> mTags;
    private Button btnCriteria;
    private List<User> users;
    private ImageButton magnifier;
    private TextView appName;
    private CheckInternet checkInternet;

    private boolean search_mode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1, container, false);
        setUserVisibleHint(false);
        session = new Session(view.getContext());
        recyclerView = view.findViewById(R.id.recycler_view);
        plant_list = new ArrayList<>();
        checkInternet = new CheckInternet();
        users = new ArrayList<>();
      //  btnCriteria = view.findViewById(R.id.search_criteria);
        swipeRefreshLayout = view.findViewById(R.id.fragment_feed_swipe_refresh);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (!recyclerView.canScrollVertically(1)) {

                    if(search_mode) {

//                        if(btnCriteria.getText().toString().equals("search users")) {
//
//                            GetPlantsByTag gpbt = new GetPlantsByTag();
//                            gpbt.execute(autoCompleteTextView.getText().toString(), Integer.toString(recyclerView.getAdapter().getItemCount()));
//                        }
//                        else {
                            //GetUsersByTag getUsersByTag = new GetUsersByTag();
                            //getUsersByTag.execute(autoCompleteTextView.getText().toString(), Integer.toString(recyclerView.getAdapter().getItemCount()));
//                        }
                    }
                    else {


//                        if(btnCriteria.getText().toString().equals("show users")) {
//
//                            load_plants_from_server(Integer.toString(recyclerView.getAdapter().getItemCount()));
//                        }
//                        else {

                            load_users_from_server(Integer.toString(recyclerView.getAdapter().getItemCount()));
//                        }

                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(!checkInternet.isInternetAvailable(getContext()))
                {
                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

                if(!search_mode) {

                    swipeRefreshLayout.setRefreshing(true);
                   // if (btnCriteria.getText().toString().equals("show plants")) {

                        plant_list.clear();
                        load_plants_from_server("0");

                //    } else {
//
                //        users.clear();
               //         load_users_from_server("0");
                 //   }
                }
                else {

                    swipeRefreshLayout.setRefreshing(true);

//                    if (btnCriteria.getText().toString().equals("search users")) {
//
//                        plant_list.clear();
//                        GetPlantsByTag getPlantsByTag = new GetPlantsByTag();
//                        getPlantsByTag.execute(autoCompleteTextView.getText().toString(), "0");
//
//                    } else {

                        users.clear();
                        //GetUsersByTag getUsersByTag = new GetUsersByTag();
                        //getUsersByTag.execute(autoCompleteTextView.getText().toString(), "0");
           //         }

                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary);

        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                // Fetching data from server
                //load_plants_from_server("0");
            }
        });


        if(!checkInternet.isInternetAvailable(getContext()))
            Toast.makeText(getContext(), "Not connected to Internet", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void Init() {

        load_plants_from_server("0");

        gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new CustomPlantAdapter(this.getContext(), plant_list);
        recyclerView.setAdapter(adapter);

        autoCompleteTextView = getActivity().findViewById(R.id.search_auto_complete);
        autoCompleteTextView.setVisibility(View.INVISIBLE);
        appName = getActivity().findViewById(R.id.app_name);
        magnifier = getActivity().findViewById(R.id.btnSearchBar);

        search_mode = false;

        // GET TAGS

        stringRequest = new StringRequest(com.android.volley.Request.Method.POST, getTagsURL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray j;

                try {

                    j = new JSONArray(response);


                    mTags = new ArrayList<String>(j.length());

                    for(int i = 0; i < j.length(); i++)
                    {

                        JSONObject jObj = j.getJSONObject(i);
                        if(jObj.has("TAG")) {

                            if(!mTags.contains(jObj.getString("TAG")))
                                mTags.add(jObj.getString("TAG"));
                        }
                        else if(jObj.has("USERNAME")) {

                            if(!mTags.contains(jObj.getString("USERNAME")))
                                mTags.add(jObj.getString("USERNAME"));
                        }
                        else if(jObj.has("FULL_NAME")) {

                            if(!mTags.contains(jObj.getString("FULL_NAME")))
                                mTags.add(jObj.getString("FULL_NAME"));
                        }
                        else if(jObj.has("MOTHERLAND")) {

                            if(!mTags.contains(jObj.getString("MOTHERLAND")))
                                mTags.add(jObj.getString("MOTHERLAND"));
                        }
                        else if(jObj.has("LATINSKI_NAZIV")) {

                            if(!mTags.contains(jObj.getString("LATINSKI_NAZIV")))
                                mTags.add(jObj.getString("LATINSKI_NAZIV"));
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, mTags);
                    autoCompleteTextView.setAdapter(adapter);

                    setClickListener();

                }
                catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

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

        // ---------------------------------------------------------------------------------


//        btnCriteria.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(!checkInternet.isInternetAvailable(getContext())) {
//
//                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(btnCriteria.getText().equals("search users")  || btnCriteria.getText().equals("show users")) {
//
//                    if(!search_mode) {
//
//                        users.clear();
//
//                        load_users_from_server("0");
//                        btnCriteria.setText("show plants");
//                    }
//                    else {
//
//                        btnCriteria.setText("search plants");
//                    }
//
//                    gridLayoutManager = new GridLayoutManager(getContext(), 1);
//                    recyclerView.setLayoutManager(gridLayoutManager);
//                    adapter = new CustomPlantAdapter(getContext(), new ArrayList<MyPlants>(), users);
//                    recyclerView.setAdapter(adapter);
//                }
//                else {
//
//                    if(!search_mode) {
//
//                        plant_list.clear();
//
//                        load_plants_from_server("0");
//                        btnCriteria.setText("show users");
//                    }
//                    else {
//
//                        btnCriteria.setText("search users");
//                    }
//
//                    gridLayoutManager = new GridLayoutManager(v.getContext(), 1);
//                    recyclerView.setLayoutManager(gridLayoutManager);
//                    adapter = new CustomPlantAdapter(v.getContext(), plant_list, new ArrayList<User>());
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//        });

        magnifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appName.getVisibility() != View.INVISIBLE) {

                    appName.setVisibility(View.INVISIBLE);
                    autoCompleteTextView.setVisibility(View.VISIBLE);
                 //   magnifier.setImageResource(R.drawable.close_icon);
                }
                else {

                    autoCompleteTextView.setVisibility(View.INVISIBLE);
                    appName.setVisibility(View.VISIBLE);
               //     magnifier.setImageResource(R.drawable.search_icon);

                    search_mode = false;
                    autoCompleteTextView.clearFocus();
                    autoCompleteTextView.setText("");

//                    if(btnCriteria.getText().equals("search users"))
//                        btnCriteria.setText("show users");
//                    else if(btnCriteria.getText().equals("search plants"))
//                        btnCriteria.setText("show plants");
                }
            }
        });
    }

    private void setClickListener() {

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH) {

                 //   GetPlantsByTag getPlantsByTag = new GetPlantsByTag();
                    plant_list.clear();
                //    getPlantsByTag.execute(autoCompleteTextView.getText().toString(), "0");

                   // GetUsersByTag getUsersByTag = new GetUsersByTag();
                    users.clear();
                  //  getUsersByTag.execute(autoCompleteTextView.getText().toString());

                    search_mode = true;

//                    if(btnCriteria.getText().equals("show users"))
//                        btnCriteria.setText("search users");
//                    else if(btnCriteria.getText().equals("show plants"))
//                        btnCriteria.setText("search plants");

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
                    tabLayout.getTabAt(0).select();

                    return  true;
                }

                return  false;
            }
        });
    }

    // GET PLANTS BY TAG

    private class GetPlantsByTag extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getPlantsByTagURL + "?tag=" + strings[0] + "&limit=" + strings[1] +
                            "&id_user=" + session.prefs.getString("id", ""))
                    .build();
            try {

                Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for(int i = 0; i<array.length(); i++) {

                    JSONObject jObject = array.getJSONObject(i);
                    Plant plant = new Plant(jObject.getString("ID_BILJKE"),
                            jObject.getString("USERNAME"),
                            jObject.getString("LATINSKI_NAZIV"),
                            jObject.getString("LOKACIJA_SLIKE"),
                            "asd");

                   // plant.setBrojLajkova(Integer.parseInt(jObject.getString("BROJ_LAJKOVA")));
                //    plant.setLiked(jObject.getString("LAJKOVANA").equals("1"));

                    plant_list.add(plant);
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }

    // --------------------------------------------------------------------------

    // GET USERS BY TAG

    /*private class GetUsersByTag extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getUsersByTagURL + "?tag=" + strings[0] + "&limit=" + strings[1])
                    .build();
            try {

                Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for(int i = 0; i<array.length(); i++) {

                    JSONObject jObject = array.getJSONObject(i);
                    User user = new User(jObject.getInt("ID"),
                            jObject.getString("USERNAME"),
                            jObject.getString("FULL_NAME"),
                            jObject.getString("EMAIL"),
                            jObject.getString("MOTHERLAND"));

                    users.add(user);
                }

            }
            catch (IOException e) {

                e.printStackTrace();
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }*/

    // ----------------------------------------------------------------------------

    // LOAD PLANTS FORM SERVER

    private void load_plants_from_server(String limit) {

        Task task = new Task();
        task.execute(limit);
    }

    private class Task extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... strings) {

            //swipeRefreshLayout.setRefreshing(true);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getPlantsURL) //(getPlantsURL + "?limit=" + strings[0] + "&id_user=" + session.prefs.getString("id", ""))
                    .build();
            try {

                Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for(int i = 0; i<array.length(); i++) {

                    JSONObject jobj = array.getJSONObject(i);
                    Plant plant = new Plant(jobj.getString("_id"),
                            jobj.getString("username"),
                            jobj.getString("latinski_naziv"),
                            jobj.getString("lokacija_slike"),
                            jobj.getString("datum_dodavanja"));

//                    plant.setBrojLajkova(Integer.parseInt(jObject.getString("BROJ_LAJKOVA")));
//                    plant.setLiked(jObject.getString("LAJKOVANA").equals("1"));

                    plant_list.add(plant);
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }

    // --------------------------------------------------------------------

    // LOAD USERS FROM SERVER

    private void load_users_from_server(String limit) {

        GetUsers getUsers = new GetUsers();
        getUsers.execute(limit);
    }

    private class GetUsers extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getUsersURL + "?limit=" + strings[0])
                    .build();
            try {

                Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for(int i = 0; i<array.length(); i++) {

                    JSONObject jObject = array.getJSONObject(i);
                    User user = new User(jObject.getInt("ID"),
                            jObject.getString("USERNAME"),
                            jObject.getString("FULL_NAME"),
                            jObject.getString("EMAIL"),
                            jObject.getString("MOTHERLAND"),
                            new ArrayList<Plant>());

                    users.add(user);
                }

            }
            catch (IOException e) {

                e.printStackTrace();
            }
            catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }

    // -------------------------------------------------------------------------

    @Override
    public void onResume() {

        super.onResume();
        int size = plant_list.size();
        plant_list.clear();
        Init();
        adapter.notifyItemRangeRemoved(0, size);
    }

}
