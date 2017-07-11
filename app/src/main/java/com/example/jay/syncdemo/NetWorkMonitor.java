package com.example.jay.syncdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.jay.syncdemo.MainActivity.dbHelper;

/**
 * Created by Jay on 06-06-2017.
 */

public class NetWorkMonitor extends BroadcastReceiver {

    StringRequest stringRequest;
    //  When any change will be there in the connection then this method will be called.
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (CheckInternetConnection(context)) {

            final SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
            final Cursor cursor = dbHelper.ReadFromLocalDatabase(sqLiteDatabase);

            while (cursor.moveToNext()) {
                int Sync_Status = cursor.getInt(cursor.getColumnIndex(DBContact.Sync_Status));
                //If sync_status is failed then we have to sync data to the database
				if (Sync_Status == DBContact.Sync_Status_Failed) {
                    final String Name = cursor.getString(cursor.getColumnIndex(DBContact.Name));
                    final String Email = cursor.getString(cursor.getColumnIndex(DBContact.Email));

                    stringRequest = new StringRequest(Request.Method.POST, DBContact.SERVER_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                String Response = jsonObject.getString("response");
                                if (Response.equals("OK")) {
                                        dbHelper.UpdataLocalDatabase(Name, Email, DBContact.Sync_Status_OK, sqLiteDatabase);
                                        context.sendBroadcast(new Intent(DBContact.UI_UPDATE_BROADCAST));
                                }
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
                            Map<String, String> params = new HashMap<>();
                            params.put("Name", Name);
                            params.put("Email", Email);
                            return params;
                        }
                    };
                    SingleTon.getInstance(context).addToRequestQueue(stringRequest);
                }
            }
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
    }

    public boolean CheckInternetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

}
