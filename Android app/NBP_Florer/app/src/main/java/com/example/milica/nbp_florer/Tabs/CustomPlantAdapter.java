package com.example.milica.nbp_florer.Tabs;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;

import com.bumptech.glide.GlideBuilder;
import com.example.milica.nbp_florer.MyAccountActivity;
import com.example.milica.nbp_florer.Plant;
import com.example.milica.nbp_florer.PlantInfoActivity;
import com.example.milica.nbp_florer.R;
import com.example.milica.nbp_florer.Session;

/**
 * Created by Milica on 25-Apr-18.
 */

public class CustomPlantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Plant> plants;
   // private String updateLikesURL = "http://martinovic.webhop.me:32080/php/update_likes.php";
    private Session session;

    public CustomPlantAdapter(Context context, List<Plant> my_data) {

        this.context = context;
        this.plants = my_data;
        session = new Session(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == 0) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);

            return new PlantViewHolder(itemView);
        }
//        else if(viewType == 1) {
//
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cardview,parent,false);
//
//            return new UserViewHolder(itemView);
//        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        final ProgressBar progressBar = new ProgressBar(context);

        if(viewType == 0) {

            final PlantViewHolder plantViewHolder = (PlantViewHolder) holder;

            plantViewHolder.latinski_naziv.setText(plants.get(position).getLatinski_naziv());
            Glide.with(context).
                    load(plants.
                            get(position).
                            getLokacija_slike()).into(plantViewHolder.imageView);
            plantViewHolder.lnkAccount.setText(plants.get(position).getUser_name());
            plantViewHolder.plant = plants.get(position);
        }
//
//            if(session.loggedin()) {
//
//                plantViewHolder.like.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (plants.get(position).isLiked()) {
//
//                          //  plantViewHolder.like.setImageResource(R.mipmap.thumbup_icon);
//                           // plantViewHolder.brojLajkova.setText(Integer.toString(Integer.parseInt(plantViewHolder.brojLajkova.getText().toString()) - 1));
//                            String id = session.getId();
//
//                            send_unlike(Integer.parseInt(id), plants.get(position).getId_plant());
//
//                          //  plants.get(position).setLiked(false);
//                        } else {
//
//                           // plantViewHolder.like.setImageResource(R.mipmap.liked_icon);
//                            plantViewHolder.brojLajkova.setText(Integer.toString(Integer.parseInt(plantViewHolder.brojLajkova.getText().toString()) + 1));
//                            String id = session.getId();
//
//                            send_like(Integer.parseInt(id), plants.get(position).getId_plant());
//
//                   //         plants.get(position).setLiked(true);
//                        }
//                    }
//                });
//            }
//        }

//        else if(viewType == 1) {
//
//            UserViewHolder userViewHolder = (UserViewHolder)holder;
//            userViewHolder.txtUsername.setText(users.get(position).getUsername());
//            userViewHolder.txtFullname.setText(users.get(position).getFullname());
//            userViewHolder.txtEmail.setText(users.get(position).getEmail());
//            userViewHolder.txtMotherland.setText(users.get(position).getMotherland());
//            Glide.with(context).
//                    load(users.
//                            get(position).
//                            getLokacija_slike()).listener(new RequestListener<String, GlideDrawable>() {
//                @Override
//                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//
//                    progressBar.setVisibility(View.GONE);
//
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//
//                    progressBar.setVisibility(View.GONE);
//
//                    return false;
//                }
//            }).crossFade(500).
//                    into(userViewHolder.imgUserImage);
//
//            userViewHolder.btnUserInfo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(v.getContext(), MyAccountActivity.class);
//                    intent.putExtra("user_view", users.get(position).getUsername());
//                    intent.putExtra("id_user_view", Integer.toString(users.get(position).getId()));
//                    context.startActivity(intent);
//                }
//            });
//        }
    }

//    private void send_like(final int id_user, final int id_plant) {
//
//        stringRequestLike = new StringRequest(com.android.volley.Request.Method.POST, updateLikesURL, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("id_user", Integer.toString(id_user));
//                hashMap.put("id_plant", Integer.toString(id_plant));
//                hashMap.put("option", "like");
//
//                return hashMap;
//            }
//        };
//
//        RequestSingleton.getInstance(context).addToRequestQueue(stringRequestLike);
//    }
//
//    private void send_unlike(final int id_user, final int id_plant) {
//
//        stringRequestUnLike = new StringRequest(com.android.volley.Request.Method.POST, updateLikesURL, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {}
//
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("id_user", Integer.toString(id_user));
//                hashMap.put("id_plant", Integer.toString(id_plant));
//                hashMap.put("option", "unlike");
//
//                return hashMap;
//            }
//        };
//
//        RequestSingleton.getInstance(context).addToRequestQueue(stringRequestUnLike);
//    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public int getItemCount() {

        return plants.size();
    }

    public class PlantViewHolder extends  RecyclerView.ViewHolder{

        public TextView latinski_naziv;
        public ImageView imageView;
        public Plant plant;
        public TextView lnkAccount;
        public ImageButton user_image_button;
        public ImageButton plant_info;

        public PlantViewHolder(View itemView) {

            super(itemView);
            latinski_naziv = itemView.findViewById(R.id.plant_card_view_plant_name);
            imageView = itemView.findViewById(R.id.card_image);
            lnkAccount = itemView.findViewById(R.id.plant_card_view_added_by);
            user_image_button = itemView.findViewById(R.id.plant_card_view_user_info);
            plant_info = itemView.findViewById(R.id.plant_card_view_plant_info);

            user_image_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), MyAccountActivity.class);
                    intent.putExtra("user_view", plant.getUser_name());
                    intent.putExtra("id_user_view", plant.getUser_name());
                    context.startActivity(intent);
                }
            });

            plant_info.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), PlantInfoActivity.class);
                    intent.putExtra("plant_url", plant.getLokacija_slike());
                    intent.putExtra("plant_latin", plant.getLatinski_naziv());
                    intent.putExtra("addedBy", plant.getUser_name());
                    intent.putExtra("id_biljke", plant.getId_plant());
                    context.startActivity(intent);
                }
            });
        }
    }
//
//    public class UserViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView txtUsername;
//        public TextView txtFullname;
//        public TextView txtEmail;
//        public TextView txtMotherland;
//        public ImageView imgUserImage;
//        public ImageButton btnUserInfo;
//
//        public UserViewHolder(View itemView) {
//
//            super(itemView);
//
//            txtUsername = itemView.findViewById(R.id.cardview_username);
//            txtFullname = itemView.findViewById(R.id.cardview_fullname);
//            txtEmail = itemView.findViewById(R.id.cardview_email);
//            txtMotherland = itemView.findViewById(R.id.cardview_motherland);
//            imgUserImage = itemView.findViewById(R.id.cardview_user_image);
//            btnUserInfo = itemView.findViewById(R.id.user_card_view_user_info);
//        }
//    }
}