package com.example.milica.nbp_florer.Comments;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
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
import com.bumptech.glide.TransitionOptions;
import com.example.milica.nbp_florer.Constants;
import com.example.milica.nbp_florer.CustomReplyDialog;
import com.example.milica.nbp_florer.R;
import com.example.milica.nbp_florer.RequestSingleton;
import com.example.milica.nbp_florer.Session;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.gms.maps.SupportMapFragment;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milica on 06-Feb-19.
 */

public class ParentCommentViewHolder extends GroupViewHolder  implements CustomReplyDialog.CustomReplyDialogListener {

    private TextView comment;
    private TextView username;
    private ImageView usernameImage;
    private TextView datum;
    private ImageButton btnUpvote;
    private Context context;
    private TextView numOfUpvotes;
    private Session session;
    private EditText etLeaveComment;
    private StringRequest stringRequestUpvotes;
    private StringRequest stringRequestUpvote;
    private StringRequest stringRequestAddComment;
    private String getUsersUpvotes = Constants.serverUrl + "/upvotes";
    private String sendUpvote = Constants.serverUrl + "/upvote";
    private String sendReply = Constants.serverUrl + "/addComment";
    private String id;
    private Boolean upvoted;
    private ImageButton btnReply;
    private String plant_id;

    public String getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public ParentCommentViewHolder(View itemView, Context ctx) {
        super(itemView);
        comment = itemView.findViewById(R.id.txtComment);
        username = itemView.findViewById(R.id.txtCommentUsername);
        usernameImage = itemView.findViewById(R.id.comment_profile_pic);
        datum = itemView.findViewById(R.id.txtCommentDate);
        btnUpvote = itemView.findViewById(R.id.btnUpvote);
        btnReply = itemView.findViewById(R.id.btnReply);
        numOfUpvotes = itemView.findViewById(R.id.txtNumOfUpvotes);
        etLeaveComment = itemView.findViewById(R.id.etComment);

        context = ctx;
        session = new Session(ctx);
        upvoted = false;
        checkIfUpvoted();

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        CustomReplyDialog customReplyDialog = new CustomReplyDialog().newInstance(id);
        customReplyDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "tag");
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


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return this.id;
    }
    public TextView getComment() {
        return comment;
    }

    public void setVisibilityComment(Boolean visibilityComment) {
        if(visibilityComment) {
            this.etLeaveComment.setVisibility(View.VISIBLE);
            comment.setVisibility(View.GONE);
        }
        else {
            this.etLeaveComment.setVisibility(View.GONE);
            comment.setVisibility(View.VISIBLE);
            checkIfUpvoted();
        }

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

    public void setNumOfUpvotes(String numOfUpvotes) {
        this.numOfUpvotes.setText(numOfUpvotes);
    }
    public String getNumOfUpvotes() {
        return this.numOfUpvotes.getText().toString();
    }

    @Override
    public void applyResult(final String comment, String comm_id) {


    }
}
