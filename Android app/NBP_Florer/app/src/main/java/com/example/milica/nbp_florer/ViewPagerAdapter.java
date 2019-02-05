package com.example.milica.nbp_florer;

import android.content.Context;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.milica.nbp_florer.R;

/**
 * Created by Milica on 15-Jun-18.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] images;

    public ViewPagerAdapter(Context ctx, String[] images) {

        this.context = ctx;
        this.images = images;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_viewpager, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.custom_viewPager_iamge);

        Glide.with(context).load(images[position]).into(imageView);
        ViewPager viewPager = (ViewPager)container;
        viewPager.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager)container;
        View view = (View) object;
        vp.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }
}