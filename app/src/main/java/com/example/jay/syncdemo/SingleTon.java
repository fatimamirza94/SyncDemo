package com.example.jay.syncdemo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jay on 06-06-2017.
 */

//  Class for Sending data to server
public class SingleTon  {

    public static SingleTon singleTon;
    private RequestQueue requestQueue;
    private static Context context;

    private SingleTon(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized SingleTon getInstance(Context context){
        if(singleTon==null){
            singleTon = new SingleTon(context);
        }
        return singleTon;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
