package com.example.milica.nbp_florer.Comments;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.milica.nbp_florer.Constants;
import com.example.milica.nbp_florer.R;
import com.example.milica.nbp_florer.RequestSingleton;
import com.example.milica.nbp_florer.Session;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milica on 06-Feb-19.
 */

public class ChildCommentViewHolder extends ChildViewHolder {

    private TextView comment;

    public TextView getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.setText(comment);
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public ImageView getUsernameImage() {
        return usernameImage;
    }

    public void setUsernameImage(String usernameImage) {
        Glide.with(context).load(usernameImage).into(this.usernameImage);
    }

    public TextView getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum.setText(datum);
    }

    public ImageButton getBtnUpvote() {
        return btnUpvote;
    }

    public void setBtnUpvote(ImageButton btnUpvote) {
        this.btnUpvote = btnUpvote;
    }

    public void setEditTextVisibility(boolean commentEdit) {
        if(commentEdit) {
            etComment.setVisibility(View.VISIBLE);
            btnAddComm.setVisibility(View.VISIBLE);
            etComment.setHint("Leave a comment...");
            comment.setVisibility(View.INVISIBLE);
        }
        else {
            btnAddComm.setVisibility(View.INVISIBLE);
            etComment.setVisibility(View.VISIBLE);
            comment.setVisibility(View.INVISIBLE);
        }

    }

    public int getNumOfUpvotes() {
        return Integer.parseInt(numOfUpvotes.getText().toString());
    }
    public void setNumOfUpvotes(String num) {
        numOfUpvotes.setText(num);
    }

    public void IncrementNumOfUpvotes() {
        int num = Integer.parseInt(numOfUpvotes.getText().toString());
        num++;
        numOfUpvotes.setText(Integer.toString(num));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private TextView numOfUpvotes;
    private TextView username;
    private ImageView usernameImage;
    private TextView datum;
    private ImageButton btnUpvote;
    private EditText etComment;
    private Context context;
    private ImageButton btnAddComm;
    private String getUsersUpvotes = Constants.serverUrl + "/upvotes";
    private String sendUpvote = Constants.serverUrl + "/upvote";
    private StringRequest stringRequestUpvotes;
    private StringRequest stringRequestUpvote;
    private String id;
    private Boolean upvoted;
    private Session session;

    public ChildCommentViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        comment = itemView.findViewById(R.id.txtComment);
        username = itemView.findViewById(R.id.txtCommentUsername);
        usernameImage = itemView.findViewById(R.id.comment_profile_pic);
        datum = itemView.findViewById(R.id.txtCommentDate);
        etComment = itemView.findViewById(R.id.etxtComment);
        btnUpvote = itemView.findViewById(R.id.btnUpvoteChild);
        numOfUpvotes = itemView.findViewById(R.id.txtUproves);
        session = new Session(context);
        upvoted = false;
        checkIfUpvoted();
    }

    private void checkIfUpvoted() {
        stringRequestUpvotes = new StringRequest(Request.Method.POST, getUsersUpvotes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray upvotes = object.getJSONArray("upvotes");
                            for(int i = 0; i<upvotes.length(); i++) {
                                JSONObject upvote = (JSONObject) upvotes.get(i);
                                if(upvote.getString("_id").equals(id))
                                    upvoted = true;
                            }
                            if(upvoted)
                                btnUpvote.setVisibility(View.GONE);
                            btnUpvoteSetup();

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
                hashMap.put("username", session.getUsername());

                return hashMap;
            }
        };
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequestUpvotes);
    }

    private void btnUpvoteSetup() {
        btnUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upvoted) {
                    btnUpvote.setVisibility(View.GONE);
                }
                else {
                    sendUpvote();
                }
            }
        });
    }

    private void sendUpvote() {
        stringRequestUpvote = new StringRequest(Request.Method.POST, sendUpvote,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        int upvotes = Integer.parseInt(numOfUpvotes.getText().toString());
                        upvotes++;
                        numOfUpvotes.setText(Integer.toString(upvotes));
                        upvoted = true;
                        btnUpvote.setVisibility(View.GONE);


                        Toast.makeText(context, "Upvoted", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Something happened, try again",
                        Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("username", session.getUsername());
                hashMap.put("comm_id", id);
                return hashMap;
            }
        };
        RequestSingleton.getInstance(context).addToRequestQueue(stringRequestUpvote);
    }
}
