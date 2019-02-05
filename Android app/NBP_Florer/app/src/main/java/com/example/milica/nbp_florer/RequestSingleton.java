package com.example.milica.nbp_florer;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Milica on 19-Apr-18.
 */

public class RequestSingleton {

    private static RequestSingleton mRequestSingleton;
    private RequestQueue mRequestQue;
    private static Context mCtx;

    private RequestSingleton(Context ctx) {

        mCtx = ctx;
        mRequestQue = getRequestQue();
    }

    public RequestQueue getRequestQue() {

        if(mRequestQue == null) {

            mRequestQue = new Volley().newRequestQueue(mCtx.getApplicationContext());
        }

        return mRequestQue;
    }

    public static synchronized RequestSingleton getInstance(Context ctx) {

        if(mRequestSingleton==null) {

            mRequestSingleton = new RequestSingleton(ctx);
        }
        return
                mRequestSingleton;
    }

    public <T> void addToRequestQueue(Request<T> request) {

        mRequestQue.add(request);
    }
}
