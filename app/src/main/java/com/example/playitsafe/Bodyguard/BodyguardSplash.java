package com.example.playitsafe.Bodyguard;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Showing splashscreen while making network calls to download necessary
 * data before launching the app Will use AsyncTask to make http call
 * Created by Alice on 2/28/2016.
 */
public class BodyguardSplash extends Activity {

    String regid,email;
    Context context;
    DatabaseManager databaseManager;
    SessionManager session;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bodyguard_splash);
        context = getApplicationContext();

        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        databaseManager.deleteAll();

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> profile = session.getUserDetails();
        regid = profile.get("regId");
        email = profile.get("email");

        new ReceiveBodyguardDB().execute();

    }


    String callOkHttpRequest(URL url, OkHttpClient tempClient)
            throws IOException {

        HttpURLConnection connection = tempClient.open(url);

        connection.setConnectTimeout(40000);
        InputStream in = null;
        try {
            // Read the response.
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new String(response, "UTF-8");
        } finally {
            if (in != null)
                in.close();
        }
    }

    byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    public class ReceiveBodyguardDB extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = "http://playitsafe-1309.appspot.com/retrievebodyguard?email="+email;
            Log.d("pavan", "url " + url);
            OkHttpClient httpClient = new OkHttpClient();

            String response = null;
            // String response=Utility.callhttpRequest(url);

            try {

                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url),
                        httpClient);

                for (String subString : response.split("<script", 2)) {
                    response = subString;
                    break;

                }
            } catch (MalformedURLException e) {
                //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                Log.i("debug", "Malform" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(),"hi2",Toast.LENGTH_LONG).show();
                Log.i("debug", "IO" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.print(response);
            Log.i("pavan", "response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                System.out.print(result);
                if (result.contains("norecord")) {

                    Toast.makeText(context, "Database has no record", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "Database has record : ", Toast.LENGTH_LONG).show();
                    System.out.print(result);
                    JSONParser parser = new JSONParser();
                    ContainerFactory containerFactory = new ContainerFactory() {
                        public List creatArrayContainer() {
                            return new LinkedList();
                        }

                        public Map createObjectContainer() {
                            return new LinkedHashMap();
                        }
                    };
                    try {
                        JSONArray jsonarray = new JSONArray(result);
                        Log.d("debug", "" + jsonarray.get(0));
                        Log.d("debug", jsonarray.length() + "");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            String jsonobject = (String) jsonarray.get(i);
                            Map json = (Map) parser.parse(jsonobject, containerFactory);
                            Iterator iter = json.entrySet().iterator();
                            String name = (String) json.get("name");
                            String mobile = (String) json.get("mobile");
                            String email = (String) json.get("email");
                            String img = (String) json.get("img");
                            String strid = (String) json.get("id");
                            String addFrom = (String) json.get("from");
                            int id = Integer.parseInt(strid);
                            Log.d("debug", id + " " + name + " " + mobile + " " + email + " " + img + " " + addFrom);
                            if (!img.equals(null) && !img.equals("x")) {
                                //blist.add(new Bodyguard(id,name,mobile,email,getImageContentUri(context,new File(img)),addFrom));
                                databaseManager.insertBodyguard(databaseManager.getLastKId() + 1, name, mobile, email, img, addFrom);
                                Log.d("debug", "has img path");
                            } else {
                                //blist.add(new Bodyguard(id, name, mobile, email, null,addFrom));
                                databaseManager.insertBodyguard(databaseManager.getLastKId() + 1, name, mobile, email, null, addFrom);
                                Log.d("debug", "no img path");
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(BodyguardSplash.this,BodyguardActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(BodyguardSplash.this,BodyguardActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }
            databaseManager.close();
        }

    }
}
