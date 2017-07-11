package com.example.jay.syncdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText editTextName,editTextEmail;
    String Email,Name;
    Button Submit;

    RecyclerAdapter adapter;
    ArrayList<Contact> arrayList = new ArrayList<>();
    RecyclerView.LayoutManager  layoutManager;
    BroadcastReceiver broadcastReceiver;

    public static DBHelper dbHelper;

    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = (EditText)findViewById(R.id.EmailET);
        editTextName = (EditText)findViewById(R.id.NameET);

        Submit = (Button)findViewById(R.id.SubmitButton);

        recyclerView = (RecyclerView) findViewById(R.id.RView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        dbHelper = DBHelper.getInstance(this);

        ReadFromLocalDatabase();

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = editTextEmail.getText().toString();
                Name = editTextName.getText().toString();
                SaveInfoToServer(Name,Email);
                editTextName.setText("");
                editTextEmail.setText("");
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ReadFromLocalDatabase();
            }
        };

        dbHelper.close();
    }

    //  Read Info From LocalDatabase
    //  when we open app then if some data is stored in sqlite database then load it to recycler view
    private void ReadFromLocalDatabase(){
        //If there was previous data available on adapter we have to clear that one.
        arrayList.clear();

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.ReadFromLocalDatabase(sqLiteDatabase);

        while(cursor.moveToNext()){
            String Name = cursor.getString(cursor.getColumnIndex(DBContact.Name));
            String Email = cursor.getString(cursor.getColumnIndex(DBContact.Email));
            int Sync_Status = cursor.getInt(cursor.getColumnIndex(DBContact.Sync_Status));
            arrayList.add(new Contact(Name,Email,Sync_Status));
        }

        //Refresh the RecyclerView
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    // In here we are storing info to the localdatabase if internet connection is not available
    //  If Internet connection is available then store data into both databases
    private void SaveInfoToServer(final String Name, final String Email){
        if(CheckInternetConnection()){
         stringRequest = new StringRequest(Request.Method.POST, DBContact.SERVER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                String Response = jsonObject.getString("response");
                                if(Response.equals("OK")){
                                    SaveInfoToLocalDatabase(Name,Email,DBContact.Sync_Status_OK);
                                }
                                else{
                                    SaveInfoToLocalDatabase(Name,Email,DBContact.Sync_Status_Failed);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    SaveInfoToLocalDatabase(Name,Email,DBContact.Sync_Status_Failed);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Name",Name);
                    params.put("Email",Email);
                    return params;
                }
            };
            SingleTon.getInstance(this).addToRequestQueue(stringRequest);
        }
        else{
            SaveInfoToLocalDatabase(Name,Email,DBContact.Sync_Status_Failed);
        }
    }

    public boolean CheckInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    private void SaveInfoToLocalDatabase(String Name,String Email,int Sync_Status){
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        //  No Network Available
        dbHelper.SaveToLocalDatabase(Name,Email,Sync_Status,sqLiteDatabase);
        //  Show Newly Added Data on RecyclerView
        ReadFromLocalDatabase();
    }

    @Override
    protected void onStart() {
        super.onStart();  
        registerReceiver(broadcastReceiver,new IntentFilter(DBContact.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
