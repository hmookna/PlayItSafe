package com.example.playitsafe.Guardian;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playitsafe.Bodyguard.Bodyguard;
import com.example.playitsafe.Bodyguard.DatabaseManager;
import com.example.playitsafe.Bodyguard.ImageAdapter;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.R;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Guardain extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ArrayList<Bodyguard> blist = new ArrayList<>();
    Uri uriContact;
    ImageAdapter imageAdapter;
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADD ="addFrom";
    DatabaseManager databaseManager;
    GridView gridView;
    ShowContAdapter showContAdapter;
    String inputEmail = "",email;
    private GoogleApiClient googleApiClient;
    double lat, lng;
    Context context;
    String res,status;
    Button stopTrack;
    TextView nouser,who;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);
        final TextView textView = (TextView) findViewById(R.id.textView6);
        nouser = (TextView) findViewById(R.id.nouser);
        who = (TextView) findViewById(R.id.who);
        who.setVisibility(View.INVISIBLE);
        textView.setText("SELECT FRIENDS TO FOLLOW YOU");

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String,String> profile = session.getUserDetails();
        email = profile.get("email");

        stopTrack = (Button) findViewById(R.id.button6);
        stopTrack.setVisibility(View.INVISIBLE);
        gridView = (GridView) findViewById(R.id.gridView3);
        context = getApplicationContext();
        BroadcastReceiver receive_respond;
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();
        createBlist();
        showContAdapter = new ShowContAdapter(context, blist);
        gridView.setAdapter(showContAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("oeii", "back");
                if (showContAdapter.getBodyguard(position).getaddFrom().equals("App")) {
                    inputEmail = showContAdapter.getBodyguard(position).getbEmail();
                    Log.d("debug", inputEmail);
                    new SendGcmToServer().execute();
                } else Toast.makeText(context, "Send SMS", Toast.LENGTH_LONG).show();
            }
        });
        receive_respond=new BroadcastReceiver() {
            String msg;
            @Override
            public void onReceive(Context context, Intent intent) {
                res = intent.getStringExtra("res");
                if(res.contains("follow")){
                    status="update";
                    googleApiClient.connect();
                    stopTrack.setVisibility(View.VISIBLE);
                    String follower = intent.getStringExtra("follower");
                    who.setVisibility(View.VISIBLE);
                    who.setText(follower+" is following you.");
                    gridView.setVisibility(View.INVISIBLE);

                }
                else{
                    Log.d("debug","disconnect");
                    googleApiClient.disconnect();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receive_respond, new IntentFilter("respond"));
        stopTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient.disconnect();
                lat=0;
                lng=0;
                status="stop";
                new locationUpdate().execute();
                stopTrack.setVisibility(View.INVISIBLE);
                who.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
            }
        });
        Log.d("debug", "connect");
    }



    public void createBlist(){
        blist.clear();
        Cursor cursor = databaseManager.selectAppBodyguard();
        Uri photo;
        int contactid;
        String contactNumber;
        String contactName;
        String contactEmail;
        String contactPhoto;
        String contactAddFrom;
        int count =0;
        Log.i("DEBUG", "Count: " + String.valueOf(cursor.getCount()));
        if(cursor.moveToFirst()) {
            do {
                contactid = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                contactName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                contactEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
                contactPhoto = cursor.getString(cursor.getColumnIndex(KEY_PHOTO));
                contactAddFrom = cursor.getString(cursor.getColumnIndex(KEY_ADD));
                Log.i("DEBUG", "CREATE BLIST >>> " + " blistIndex : " + count + " bId(DB) : " + contactid + "name:  " + contactName + "phone : " + contactNumber + "email :  " + contactEmail);

                if (contactPhoto == null) {
                    photo = Uri.parse("android.resource://com.example.playitsafe/drawable/bodyguard_user");
                    blist.add(count++, new Bodyguard(contactid, contactName, contactNumber, contactEmail, photo, contactAddFrom));
                } else {
                    blist.add(count++, new Bodyguard(contactid, contactName, contactNumber, contactEmail, Uri.parse(contactPhoto), contactAddFrom));
                }


            }while (cursor.moveToNext());

        }else{
            nouser.setText("No friends in Play It Safe. Please add your friends.");
        }

        for(int i=0;i<blist.size();i++){
            System.out.println(blist.size());
            System.out.println(blist.get(i).getbName() + blist.get(i).getbEmail() + blist.get(i).getbPhone() + blist.get(i).getaddFrom());
        }

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

    private class SendGcmToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String url = "http://playitsafe-1309.appspot.com/beguardain?email="+inputEmail+"&sender="+email;
            Log.i("debug", "url" + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();

            String response = null;
            // String response=Utility.callhttpRequest(url);

            try {
                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url),
                        client_for_getMyFriends);
                for (String subString : response.split("<script", 2)) {
                    response = subString;
                    break;
                }
            } catch (MalformedURLException e) {
                //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                Log.i("oeii", "Malform" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(),"hi2",Toast.LENGTH_LONG).show();
                Log.i("oeii", "IO" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
            Log.i("debug", "response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();

            if (result != null) {
                if (result.contains("follow")) {

                    //Toast.makeText(context, "The result is " + result, Toast.LENGTH_LONG).show();

                } else {

                 //   Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Do something when connected with Google API Client

        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            // Do something when Location Provider not available
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            Log.d("pavan","location pen null na");
            // Blank for a moment...
        }
        else {
            //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
            handleNewLocation(location);
        };
        lat=location.getLatitude();
        lng=location.getLongitude();

        new locationUpdate().execute();

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do something when Google API Client connection was suspended

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Do something when Google API Client connection failed

    }

    @Override
    public void onLocationChanged(final Location loc) {
        // Do something when got new current location
        lat = loc.getLatitude();
        lng = loc.getLongitude();
        new locationUpdate().execute();
    }

    class locationUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = "http://playitsafe-1309.appspot.com/updateLocation?sender="+email+"&receiver="+inputEmail+"&lat=" + lat + "&lng=" + lng+"&status="+status;
            Log.i("pavan", "url" + url);

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
                Log.i("oeii", "Malform" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(),"hi2",Toast.LENGTH_LONG).show();
                Log.i("oeii", "IO" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
            Log.i("pavan", "response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();

            if (result != null) {
                if (result.contains("success")) {

                 //   Toast.makeText(context, "The result is success", Toast.LENGTH_LONG).show();

                } else {

                 //   Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void handleNewLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }
}

