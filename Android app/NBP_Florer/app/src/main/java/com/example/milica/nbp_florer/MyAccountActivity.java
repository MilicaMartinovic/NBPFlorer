package com.example.milica.nbp_florer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.milica.nbp_florer.Tabs.CustomPlantAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MyAccountActivity extends AppCompatActivity {

    private TextView username, fullname, email, motherland, bio;
    private ProgressDialog progressDialog;
    private Session session;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private StringRequest updateUserRequest;
    private ImageView imageView;
    private Button btnSlikaj, btnIzaberiSliku;
    private FileResolver fileResolver;
    private List<Plant> plant_list;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private CustomPlantAdapter adapter;
    private static final String updateUserURL = "http://martinovic.webhop.me:32080/php/update_user.php";
    private static final String myAccountActvityURL = Constants.serverUrl + "/account";
    private static String user_images_folder = "http://martinovic.webhop.me:32080/user_images/";
    private String getUserPlantsURL = "http://martinovic.webhop.me:32080/php/get_user_plants.php";
    private String user_view;
    private String id_user_view;
    private ImageButton changeImage;

    private EditText editEmail;
    private EditText editUserName;
    private EditText editFullName;
    private EditText editMotherland;
    private EditText editBio;
    private ImageButton btnEdit;
    private Button btnShowContributions;
    private int pageNo;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.lblUserName);
        fullname = findViewById(R.id.lblFullName);
        email = findViewById(R.id.lblEmail);
        motherland = findViewById(R.id.lblMotherland);
        bio = findViewById(R.id.lblBio);
        changeImage = findViewById(R.id.btn_change_image);
        //btnShowContributions = findViewById(R.id.btnViewContributions);
        pageNo = 1;
        plant_list = new ArrayList<>();
        scrollView = findViewById(R.id.scroll_view);

        editEmail = findViewById(R.id.my_account_email_edit);
        editFullName = findViewById(R.id.my_account_full_name_edit);
        editUserName = findViewById(R.id.my_account_user_name_edit);
        editMotherland = findViewById(R.id.my_account_motherland_edit);
        editBio = findViewById(R.id.my_account_bio_edit);
        editEmail.setVisibility(View.INVISIBLE);
        editUserName.setVisibility(View.INVISIBLE);
        editFullName.setVisibility(View.INVISIBLE);
        editMotherland.setVisibility(View.INVISIBLE);
        editBio.setVisibility(View.INVISIBLE);

        this.recyclerView = findViewById(R.id.recycler_view_account);
        fileResolver = new FileResolver();

        getSupportActionBar().setElevation(0);

        recyclerView.setNestedScrollingEnabled(false);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        session = new Session(this);

        if (getIntent().hasExtra("user_view") && getIntent().hasExtra("id_user_view")) {
            user_view = getIntent().getStringExtra("user_view");
            id_user_view = getIntent().getStringExtra("id_user_view");
        }

        requestQueue = Volley.newRequestQueue(this);
        imageView = findViewById(R.id.img);

        stringRequest = new StringRequest(Request.Method.POST, myAccountActvityURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject j;

                try {

                    j = new JSONObject(response);
                    username.setText(j.getString("username"));
                    fullname.setText(j.getString("fullname"));
                    email.setText(j.getString("email"));
                    motherland.setText(j.getString("motherland"));
                    String myImageURL = j.getString("lokacija_slike");
                    bio.setText(j.getString("bio") != "" ? j.getString("bio") : "not available");
                    JSONArray biljcice = j.getJSONArray("slike_biljaka");
                    for( int i = 0; i < biljcice.length(); i++) {

                        JSONObject jobj = biljcice.getJSONObject(i);
                        Plant plant = new Plant(jobj.getString("_id"),
                                                j.getString("username"),
                                                jobj.getString("biljka"),
                                                jobj.getString("lokacija_slike"),
                                                jobj.getString("datum_dodavanja"));

                        plant_list.add(plant);
                    }

                    adapter = new CustomPlantAdapter(getApplicationContext(), plant_list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


//                    if (user_view != null) {
//                        myImageURL = user_images_folder + user_view + ".jpg";
//                    } else {
//                        myImageURL = user_images_folder + session.prefs.getString("user", "") + ".jpg";
//                    }
                    if ((user_view != null && id_user_view != null) || !session.prefs.getString("user", "").equals(username.getText().toString())) {

                        changeImage.setVisibility(View.INVISIBLE);
                        btnEdit.setVisibility(View.INVISIBLE);
                    }

                    Glide.with(getApplicationContext()).
                            load(myImageURL)//user_image_missing
                            .into(imageView);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();

                if (user_view != null)
                    hashMap.put("username", user_view);
                else
                    hashMap.put("username", session.prefs.getString("user", ""));

                return hashMap;
            }
        };

        RequestSingleton.getInstance(MyAccountActivity.this).addToRequestQueue(stringRequest);

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyAccountActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_alert_dialog_change_image, null);
                btnSlikaj = mView.findViewById(R.id.btnTakePhoto);
                btnIzaberiSliku = mView.findViewById(R.id.btnGallery);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnSlikaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                        dialog.dismiss();
                    }
                });

                btnIzaberiSliku.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            onGalleryPickerStarted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editUserName.getVisibility() == View.INVISIBLE) {

                    username.setVisibility(View.INVISIBLE);
                    fullname.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    motherland.setVisibility(View.INVISIBLE);
                    bio.setVisibility(View.INVISIBLE);

                    editEmail.setVisibility(View.VISIBLE);
                    editEmail.setText(email.getText());
                    editUserName.setVisibility(View.VISIBLE);
                    editUserName.setText(username.getText());
                    editFullName.setVisibility(View.VISIBLE);
                    editFullName.setText(fullname.getText());
                    editMotherland.setVisibility(View.VISIBLE);
                    editMotherland.setText(motherland.getText());
                    editBio.setVisibility(View.VISIBLE);
                    editBio.setText(bio.getText());

                    btnEdit.setImageResource(R.drawable.common_google_signin_btn_icon_dark);//checkmark_icon
                }
                else {

                    editEmail.setVisibility(View.INVISIBLE);
                    editUserName.setVisibility(View.INVISIBLE);
                    editFullName.setVisibility(View.INVISIBLE);
                    editMotherland.setVisibility(View.INVISIBLE);
                    editBio.setVisibility(View.INVISIBLE);

                    username.setVisibility(View.VISIBLE);
                    username.setText(editUserName.getText());
                    fullname.setVisibility(View.VISIBLE);
                    fullname.setText(editFullName.getText());
                    email.setVisibility(View.VISIBLE);
                    email.setText(editEmail.getText());
                    motherland.setVisibility(View.VISIBLE);
                    motherland.setText(editMotherland.getText());
                    bio.setVisibility(View.VISIBLE);
                    bio.setText(editBio.getText());

                    btnEdit.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused);//pencil_edit_icon

                    updateUserProfile();
                }
            }
        });

        /*btnShowContributions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnShowContributions.getText().equals("view contributions")) {

                    //load_data_from_server("0");

                    adapter = new CustomPlantAdapter(getApplicationContext(), plant_list);
                    recyclerView.setAdapter(adapter);
                    btnShowContributions.setText("hide contributions");
                    adapter.notifyDataSetChanged();
                }
                else {

                    int size = plant_list.size();
                    plant_list.clear();
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeRemoved(0, size);
                    btnShowContributions.setText("view contributions");
                }
            }
        });*/

        /*scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (!v.canScrollVertically(1) && btnShowContributions.getText().equals("hide contributions")) {

                    load_data_from_server(Integer.toString(recyclerView.getAdapter().getItemCount()));
                }
            }
        });*/
    }

    private void load_data_from_server(String limit) {

        Task t = new Task();
        t.execute(limit);
    }

    private class Task extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            String id = null;

            if(user_view != null && id_user_view != null)
                id = id_user_view;
            else
                id = session.prefs.getString("id", "");

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(getUserPlantsURL + "?id=" + id + "&limit=" + strings[0])
                    .build();

            try {

                okhttp3.Response response = client.newCall(request).execute();
                JSONArray array = new JSONArray(response.body().string());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObject = array.getJSONObject(i);
                    Plant plant = new Plant(jObject.getString("ID_BILJKE"),
                            jObject.getString("USERNAME"),
                            jObject.getString("LATINSKI_NAZIV"),
                            jObject.getString("LOKACIJA_SLIKE"),
                            "asd");
                    plant_list.add(plant);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            adapter.notifyDataSetChanged();
        }
    }

    private void onGalleryPickerStarted() throws IOException {

        File photoFile = fileResolver.createImageFile(getApplicationContext());
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                "com.example.android.milica",
                photoFile);

        fileResolver.setFile_uri(photoURI);
        photoPicker.setDataAndType(photoURI, "image/*");
        photoPicker.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(photoPicker, 10);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = fileResolver.createImageFile(getApplicationContext());
                    }
                    catch (IOException ex) {
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.milica",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 1);
                    }

                } else {
                    makeText(getApplicationContext(), "camera permission denied", LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bitmap bitmap;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            File f = new File(fileResolver.getmCurrentPhotoPath());
            bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {

                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, bmOptions);
                fileResolver.setBitmap(bitmap);
            }
            catch (FileNotFoundException e) {

                e.printStackTrace();
            }

            makeRequest(true);
            imageView.setImageBitmap(fileResolver.getBitmap());
        }
        else if (requestCode == 10 && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            }
            catch (FileNotFoundException e) {

                e.printStackTrace();
            }
            catch (IOException e) {

                e.printStackTrace();
            }

            imageView.setImageBitmap(bitmap);

            fileResolver.setmCurrentPhotoPath(fileResolver.saveToInternalStorage(this, bitmap));

            makeRequest(false);
        }
    }

    private void makeRequest(boolean fromCamera) {

        progressDialog = new ProgressDialog(MyAccountActivity.this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        final String uploaded_file;

        if(user_view!=null) {

            uploaded_file = user_view + "jpg";
        }
        else {

            uploaded_file = session.prefs.getString("user", "") + ".jpg";
        }

        final File photoPath;

        if(fromCamera)
            photoPath = new File(fileResolver.getmCurrentPhotoPath());
        else
            photoPath = new File(fileResolver.getmCurrentPhotoPath(), "profile.jpg");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String content_type = getMimeType(photoPath.getPath());
                OkHttpClient client = new OkHttpClient();
                RequestBody fileBody = RequestBody.create(MediaType.parse(content_type), photoPath);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", content_type)
                        .addFormDataPart("uploaded_file", uploaded_file, fileBody)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://martinovic.webhop.me:32080/php/user_image_upload_BIG.php?image_name=" + uploaded_file)
                        .post(requestBody)
                        .build();
                try {

                    okhttp3.Response response = client.newCall(request).execute();

                    if (!response.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "Image upload failed :(", LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        throw new IOException("Error: " + response);
                    }

                    progressDialog.dismiss();
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

    private void updateUserProfile() {

        updateUserRequest = new StringRequest(Request.Method.POST, updateUserURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("id", session.prefs.getString("id", ""));
                hashMap.put("user_name", username.getText().toString());
                hashMap.put("full_name", fullname.getText().toString());
                hashMap.put("email", email.getText().toString());
                hashMap.put("motherland", motherland.getText().toString());
                hashMap.put("bio", bio.getText().toString());
                return hashMap;
            }
        };

        RequestSingleton.getInstance(MyAccountActivity.this).addToRequestQueue(updateUserRequest);
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
