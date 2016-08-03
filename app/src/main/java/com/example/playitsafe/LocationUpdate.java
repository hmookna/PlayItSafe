package com.example.playitsafe;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.AppController;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mook on 07/02/2016.
 */

public class LocationUpdate extends Activity {
    private static final String TAG = LocationUpdate.class.getSimpleName();

    public LocationUpdate() {}

    public void location(final String email, final double latitude, final double longtitude) {
        // Tag used to cancel the request
        String tag_string_req = "req_location";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "LocationUpdate Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.i("senior","not error in location send data");
                        //  Toast.makeText(getApplicationContext(), "not error in location send data", Toast.LENGTH_LONG).show();
                    } else {
                        // Error in location. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //   Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "LocationUpdate Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "location");
                params.put("email", email);
                params.put("latitude", String.valueOf(latitude));
                params.put("longtitude", String.valueOf(longtitude));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
