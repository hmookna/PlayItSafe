package com.example.playitsafe.SOS;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.AppController;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mook on 23/01/2016.
 */
public class RegisterGCM extends Activity {
    private static final String TAG = RegisterGCM.class.getSimpleName();
    private Context context;
    SessionManager session;
    public RegisterGCM(){}

    public RegisterGCM(Context context) {
        this.context = context;
        session = new SessionManager(context);
    }

    public void registerGcmId(final String email, final String password, final String regId) {
        // Tag used to cancel the request
        String tag_string_req = "req_registGcmId";

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER_GCM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RegisterGcm Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Log.i("regID","register gcm id already!!");

                    } else {
                        // Error in location. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Log.i(TAG,errorMsg);
                        // Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "RegisterGcm Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "RegisterGcm");
                params.put("email", email);
                params.put("password", password);
                params.put("regId", regId);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void sendNoti(final String email, final String regId, final Double latitude, final Double longtitude, final String imgStr, final String imgName) {
        // Tag used to cancel the request
        String tag_string_req = "req_sendNoti";

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_SENDNOTI_GCM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RegisterGcm Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Log.i("sendNoti","send noti already!!");


                    } else {
                        // Error in location. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Log.i(TAG,errorMsg);
                        // Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "SendingNoti Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "SendGcm");
                params.put("email", email);
                params.put("regId", regId);
                params.put("latitude", String.valueOf(latitude));
                params.put("longtitude", String.valueOf(longtitude));
                params.put("imgStr",imgStr);
                params.put("imgName",imgName);
                params.put("message", "Help me please my bodyguards. I need your help!!");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void sendNotiOtherRound(final String email, final String regId, final String filePath) {
        // Tag used to cancel the request
        String tag_string_req = "req_sendNoti2";

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_SENDNOTI_GCM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RegisterGcm Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Log.i("sendNoti","send notification again!!");

                    } else {
                        // Error in location. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Log.i(TAG,errorMsg);
                        // Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "SendingNoti Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "SendGcm");
                params.put("email", email);
                params.put("regId", regId);
                params.put("round","2");
                params.put("filePath",filePath);
                params.put("message", "Have no bodyguards yet!! Help me please!! I need your help!!");
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




}

