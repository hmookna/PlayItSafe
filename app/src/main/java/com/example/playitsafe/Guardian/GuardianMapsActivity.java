package com.example.playitsafe.Guardian;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class GuardianMapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient googleApiClient;
    GoogleMap map;
    double lat, lng;
    Context context;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imhere_show_location);
        context = getApplicationContext();
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> profile = session.getUserDetails();
        email = profile.get("email");

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();


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
            // googleApiClient.disconnect();
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
                    .setInterval(5000);
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

        LatLng point = new LatLng(lat,lng);


        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        options.title("I'm here");
        map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                point, 16));
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


            String url = "http://playitsafe-1309.appspot.com/updateLocation?sender="+email+"&receiver="+"oei"+"&lat="+lat+"&lng="+lng;
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
            Log.i("pavan", "response"+response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();

            if (result != null) {
                if (result.contains("success")) {

                    Toast.makeText(context, "The result is success", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }


            }else{

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
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
    }

    private void handleNewLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

}