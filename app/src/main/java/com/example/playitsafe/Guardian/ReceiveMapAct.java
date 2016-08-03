package com.example.playitsafe.Guardian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.MainActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveMapAct extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    GoogleMap map;
    double lat, lng;
    double flat,flng,onflat,onflng;
    String res,email,sender;
    ArrayList<LatLng> markerPoints;
    Context context;
    LatLng point;
    LatLng point2;
    Intent myIntent;
    Marker mMarker;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_show_location_wstop);
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String,String> profile = session.getUserDetails();
        email = profile.get("email");
        BroadcastReceiver receive_location;
        showAlert();
        markerPoints = new ArrayList<LatLng>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();
        myIntent = getIntent();
        sender = myIntent.getStringExtra("sender");

     //   flat = Double.parseDouble(myIntent.getStringExtra("flat"));
    //    flng = Double.parseDouble(myIntent.getStringExtra("flng"));
     //   Toast.makeText(getApplicationContext()," sender : "+sender,Toast.LENGTH_LONG).show();

        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient.disconnect();
                map.stopAnimation();
                res = "stop";
                new SendGcmToServer().execute(res);
                finish();
                Intent intent = new Intent(getApplicationContext(), Guardain.class);
                startActivity(intent);
            }
        });

        receive_location=new BroadcastReceiver() {
            String msg;
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("status").equals("stop")){
                    googleApiClient.disconnect();
                    Log.d("debug","Status : stop");
                    map.stopAnimation();
                    finish();
                    Intent myintent = new Intent(getApplicationContext(), Guardain.class);
                    startActivity(myintent);
                } else {
                    onflat = Double.parseDouble(intent.getStringExtra("flat"));
                    onflng = Double.parseDouble(intent.getStringExtra("flng"));
                    Bundle extra = intent.getExtras();
                    map.clear();
                  //  point = new LatLng(lat, lng);
                    point2 = new LatLng(onflat, onflng);
                    markerPoints.add(point);

                    // Setting the position of the marker
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            point, 14));
                    MarkerOptions options2 = new MarkerOptions();

                    // Setting the position of the marker
                    options2.position(point2);
                    options2.title(sender)
                            .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource( getResources(), R.drawable.guardian_user)));
                    mMarker = map.addMarker(options2);

                    mMarker.showInfoWindow();
                    //Log.d("oeii", "in local board " + extra.getString("location"));

                    // Toast.makeText(getApplicationContext(),extra.size(),Toast.LENGTH_LONG).show();
                    ;

                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receive_location, new IntentFilter("respond"));
    }
    public void showAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("would you like to be a guardian?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map = ((SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map)).getMap();
                        res = "follow";

                        flat = Double.parseDouble(myIntent.getStringExtra("flat"));
                        flng = Double.parseDouble(myIntent.getStringExtra("flng"));
                        sender = myIntent.getStringExtra("sender");

                        point2 = new LatLng(flat,flng);
                        markerPoints.add(point);

                        // Setting the position of the marker

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14));
                        MarkerOptions options2 = new MarkerOptions();

                        // Setting the position of the marker
                        options2.position(point2);
                        options2.title(sender)
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource( getResources(), R.drawable.guardian_user)));
                        mMarker = map.addMarker(options2);

                        mMarker.showInfoWindow();
                        new SendGcmToServer().execute(res);

                    }

                })
                .setTitle("Guardain")
                .setNegativeButton("Ignore", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        res = "ignore";
                        new SendGcmToServer().execute(res);
                        finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .create();
        alert.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

        @Override
        public void onStop() {
            super.onStop();


        if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
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
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
        } else {
            // Do something when Location Provider not available
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            // Blank for a moment...
        }
        else {
            handleNewLocation(location);
            point = new LatLng(location.getLatitude(),location.getLongitude());
        };
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
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.clear();

        point2 = new LatLng(flat,flng);
        MarkerOptions options2 = new MarkerOptions();
        // Setting the position of the marker
        options2.position(point2);
        options2.title(sender)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource( getResources(), R.drawable.guardian_user)));
        mMarker = map.addMarker(options2);

        mMarker.showInfoWindow();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                point, 14));

    }
    private void handleNewLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }



    class SendGcmToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = "http://playitsafe-1309.appspot.com/actionrespond?email="+sender+"&follower="+email+"&res="+res;
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
                if (result.contains("ignore")||result.contains("stop")) {
                    map.clear();
                    googleApiClient.disconnect();

                    //Toast.makeText(context, "The result is " + result, Toast.LENGTH_LONG).show();

                } else {

                    //Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }


            }else{

                //Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
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
            for (int count; (count = in.read(buffer)) != -1;) {
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        }


    }


}
