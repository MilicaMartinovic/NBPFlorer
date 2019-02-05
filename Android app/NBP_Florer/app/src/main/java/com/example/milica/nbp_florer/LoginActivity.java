package com.example.milica.nbp_florer;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private TextView createAccount;
    private Session session;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private static final String loginURL = Constants.serverUrl + "/login";
    private Activity loginA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginA = this;

        username = (EditText)findViewById(R.id.etxtUsername);
        password = (EditText)findViewById(R.id.etxtPassword);
        createAccount = findViewById(R.id.txtCreateAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
              //  startActivity(intent);
            }
        });

        session = new Session(this);
        requestQueue = Volley.newRequestQueue(this);

        if(session.loggedin()) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void onLogin(View view) {

        final String user_name = username.getText().toString();
        final String pass_word = password.getText().toString();
        final Intent in = new Intent(getApplicationContext(), LoginActivity.class);

        stringRequest = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!="") {

                    session.setLoggedin(true);
                    session.setUser(user_name);
                    session.setID(response);

                    NavUtils.navigateUpFromSameTask(loginA);
                }
                else {

                    Toast.makeText(getApplicationContext(), "Username and password not matching", Toast.LENGTH_SHORT).show();
                    session.setLoggedin(false);
                    session.setUser("");
                    session.setID("");
                }

                /*startActivity(in);
                finish();*/
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("username", user_name);
                hashMap.put("password", pass_word);

                return hashMap;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }}
