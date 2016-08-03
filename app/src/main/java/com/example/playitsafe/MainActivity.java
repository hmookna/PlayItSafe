package com.example.playitsafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.playitsafe.Bodyguard.BodyguardSplash;
import com.example.playitsafe.Bodyguard.DatabaseManager;
import com.example.playitsafe.Camera.CameraActivity;
import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.AppController;
import com.example.playitsafe.DBconnect.ConnectionDetector;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.Design.FragmentDrawer;
import com.example.playitsafe.Design.FragmentEdit;
import com.example.playitsafe.Design.Notification.NotificationActivity;
import com.example.playitsafe.Design.fragment.GuideFragment;
import com.example.playitsafe.Design.fragment.MsgFragment;
import com.example.playitsafe.EmergencyCall.EmergencyCall;
import com.example.playitsafe.FakeCall.FakecallActivity;
import com.example.playitsafe.Guardian.Guardain;
import com.example.playitsafe.Imhere.Imhere;
import com.example.playitsafe.Imhere.ImhereMap;
import com.example.playitsafe.LoginRegister.LoginActivity;
import com.example.playitsafe.LoginRegister.activity.Mail;
import com.example.playitsafe.SOS.RegisterGCM;
import com.example.playitsafe.SOS.SOSActivity;
import com.example.playitsafe.SafeZone.SafeZoneActivity;
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

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    //variable
    private SessionManager session;
    private Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    private String regId;
    private Button guardian,safezone,fakecall,emergency;
    private Button btnSOS,emailver,fakeFriend,fakeCamera,fakeNav,fakeMap;
    private GoogleApiClient googleApiClient;
    Double latitude,longtitude;
    GoogleMap mMap;
    Marker mMarker;
    String getEmail,name,getPass,imgStr;
    Boolean isInternetPresent = false; // flag for Internet connection status
    int pattern = 0;
    int count=0;
    JSONObject jsonObj;
    ConnectionDetector cd; // Connection detector class
    private static final String SHOWCASE_ID = "custom example";
    //Camera
    private Camera camera;
    private Camera mCamera=null;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private String str;
    String filePath = "";
    DatabaseManager dbmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //User Login
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        enablingToolbar(toolbar);
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Show user detail
        HashMap<String, String> profile = session.getUserDetails();
        name = profile.get("name");
        getEmail = profile.get("email");
        getPass = profile.get("password");
        regId = profile.get("regId");

        Log.d("REG","Main: regId"+regId);

        cd = new ConnectionDetector(getApplicationContext());
        emailver = (Button)findViewById(R.id.emailver);
        btnSOS = (Button)findViewById(R.id.btnSOS);
        fakeFriend = (Button)findViewById(R.id.fakeFriend);
        fakeCamera = (Button)findViewById(R.id.fakeCamera);
        fakeNav = (Button)findViewById(R.id.fakeNav);
        fakeMap = (Button)findViewById(R.id.fakeMap);

        fakeFriend.setVisibility(View.INVISIBLE);
        fakeCamera.setVisibility(View.INVISIBLE);
        fakeNav.setVisibility(View.INVISIBLE);
        fakeMap.setVisibility(View.INVISIBLE);

        updateSOSbutton();
        btnSOS.setOnClickListener(btnSOSListener);

        guardian = (Button) findViewById(R.id.guardian);
        guardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Guardain.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "guardian", Toast.LENGTH_LONG).show();
            }
        });

        safezone = (Button) findViewById(R.id.safezone);
        safezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SafeZoneActivity.class);
                startActivity(intent);
            }
        });

        fakecall = (Button) findViewById(R.id.fakecall);
        fakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FakecallActivity.class);
                startActivity(intent);
            }
        });

        emergency = (Button) findViewById(R.id.emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyCall.class);
                startActivity(intent);
            }
        });

        //show map
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Intent intent = new Intent(getApplicationContext(), Imhere.class);
                intent.putExtra("lat",latitude);
                intent.putExtra("lng",longtitude);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "imhere", Toast.LENGTH_LONG).show();
            }
        });

        //Guide - ShowcaseView
        presentShowcaseSequence(); // one second delay

        //Location Update
        // Create Google API Client instance
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Log Out");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to log out?");
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_logout);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                session.clearSession();
                googleApiClient.disconnect();
                // Launching the login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                // Write your code here to invoke YES event
                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on Cencel", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void addBodyguard(){
        Intent intentBodyguard = new Intent(MainActivity.this,BodyguardSplash.class);
        startActivity(intentBodyguard);
    }

    private void cameraPage(){
        Intent intentCamera = new Intent(MainActivity.this,CameraActivity.class);
        startActivity(intentCamera);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the BodyguardSplash/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            cameraPage();
            return true;
        }

        if (id == R.id.action_friends) {
            addBodyguard();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    //Key event for volume button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // return to the App's BodyguardSplash Activity
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
           // Toast.makeText(getApplicationContext(), "volume down", Toast.LENGTH_LONG).show();
            pattern = 1;
            count++;
            Toast.makeText(getApplicationContext(), "volume down count: "+ count, Toast.LENGTH_LONG).show();
            if(session.isShortcut() && count == 4){
                btnSOS.performClick();
            }
        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
           // Toast.makeText(getApplicationContext(), "volume up", Toast.LENGTH_LONG).show();
            pattern = 2;
            count++;
            Toast.makeText(getApplicationContext(), "volume up count: "+ count, Toast.LENGTH_LONG).show();
            if(session.isShortcut() && count == 4){
                btnSOS.performClick();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect to Google API Client
        googleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
      /*  if (googleApiClient != null && googleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            googleApiClient.disconnect();
        }*/
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i("Location Change","Latitude : " + location.getLatitude() +" Longitude : " + location.getLongitude());
        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        if (mMarker != null)
            mMarker.remove();
        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
        mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude))
                                                    .icon(BitmapDescriptorFactory.fromBitmap(
                                                         BitmapFactory.decodeResource( getResources(), R.drawable.icon_user)))
                                                    .title(name));

        mMarker.showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16));

        LocationUpdate updateLocat = new LocationUpdate();
        updateLocat.location(getEmail,location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void updateSOSbutton(){
        if(session.isAuthenticated()){
            btnSOS.setEnabled(true);
            emailver.setVisibility(View.INVISIBLE);
        }else{
            btnSOS.setEnabled(false);
            emailver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chkEmailverification(getEmail);
                }
            });
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
     /*
     * This functions converts Bitmap picture to a string which can be
     * JSONified.
     * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public void cameraCapture(){
        mCamera = Camera.open();
        SurfaceView view = new SurfaceView(getApplicationContext());

        try {
            mCamera.setPreviewDisplay(view.getHolder()); // feed dummy surface to surface
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        android.os.Handler handler = new android.os.Handler();
        Runnable r =  new Runnable() {
            @Override
            public void run() {
                try {
                    mCamera.startPreview();
                    mCamera.takePicture(null, null, null, jpegCallBack);

                }catch (Exception e){

                }

            }
        };
        handler.postDelayed(r, 1000);

    }

    Camera.PictureCallback jpegCallBack=new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            // set file destination and file name
            Date date = new Date();
            str = "image_"+dateFormat.format(date) + ".jpg";

            Log.d("DEBUG:","Photo: " + str);
            File destination= new File(Environment.getExternalStorageDirectory()+"/"+AppConfig.IMAGE_DIRECTORY_NAME);

            boolean success = true;
            if (!destination.exists()) {
                success = destination.mkdirs();
                destination= new File(Environment.getExternalStorageDirectory()+"/"+AppConfig.IMAGE_DIRECTORY_NAME,str);

                filePath = "" + destination;
                Log.d("DEBUG:","Destination1: " + destination);

                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                int nh = (int) ( bmp.getHeight() * (512.0 / bmp.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, nh, true);
                imgStr = getStringFromBitmap(scaled);

                JSONObject jsonObj = new JSONObject();
                try {

                    jsonObj.put("imgStr",imgStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("test jsonn",jsonObj.toString());
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    RegisterGCM regGcm = new RegisterGCM();
                    regGcm.sendNoti(getEmail,regId,latitude,longtitude,jsonObj.toString(),str);
                    Log.d("DEBUG","Internet Connection");
                    //   regGcm.sendNoti(getEmail , regId , latitude, longtitude);
                } else {
                    // Internet connection is not present
                    dbmanager = new DatabaseManager(getApplicationContext());
                    ArrayList<String> phoneList = dbmanager.getPhoneNumberBodyguards();
                    for(int i=0; i < phoneList.size(); i++){
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String sms = "I need your help! \n I'm here: http://maps.google.com/maps?q="+latitude+","+longtitude+"&ll="+latitude+","+longtitude+"&z=17";
                            smsManager.sendTextMessage( phoneList.get(i), null, sms, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    Log.d("phoneList", phoneList.get(0) + " " + phoneList.size());
                    Log.d("DEBUG","No Internet Connection");
                    //    regGcm.sendSms(getEmail , latitude, longtitude);
                }

                Intent intent = new Intent(getApplicationContext(), SOSActivity.class);
                intent.putExtra("regId", regId);
                intent.putExtra("ImgName",str);
                startActivity(intent);
                finish();
            }
            else{
                destination= new File(Environment.getExternalStorageDirectory()+"/"+AppConfig.IMAGE_DIRECTORY_NAME,str);
                filePath = ""+destination;
                Log.d("DEBUG:","Destination2: " + destination);

                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                int nh = (int) ( bmp.getHeight() * (512.0 / bmp.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, nh, true);
                imgStr = getStringFromBitmap(scaled);

                Log.d("test json",imgStr);
                JSONObject jsonObj = new JSONObject();
                try {

                    jsonObj.put("imgStr",imgStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("test jsonn",jsonObj.toString());
                isInternetPresent = cd.isConnectingToInternet();
                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    RegisterGCM regGcm = new RegisterGCM();
                    regGcm.sendNoti(getEmail,regId,latitude,longtitude,jsonObj.toString(),str);
                    Log.d("DEBUG","Internet Connection");
                    //   regGcm.sendNoti(getEmail , regId , latitude, longtitude);
                } else {
                    // Internet connection is not present
                    dbmanager = new DatabaseManager(getApplicationContext());
                    ArrayList<String> phoneList = dbmanager.getPhoneNumberBodyguards();
                    for(int i=0; i < phoneList.size(); i++){
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String sms = "I need your help! \n I'm here: http://maps.google.com/maps?q="+latitude+","+longtitude+"&ll="+latitude+","+longtitude+"&z=17";
                            smsManager.sendTextMessage( phoneList.get(i), null, sms, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    Log.d("phoneList", phoneList.get(0) + " " + phoneList.size());
                    Log.d("DEBUG","No Internet Connection");
                    //    regGcm.sendSms(getEmail , latitude, longtitude);
                }

                Intent intent = new Intent(getApplicationContext(), SOSActivity.class);
                intent.putExtra("regId", regId);
                intent.putExtra("ImgName",str);
                startActivity(intent);
                finish();

            }
            if (success) {
                try {
                    Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // set file out stream
                    FileOutputStream out = new FileOutputStream(destination);
                    // set compress format quality and stream
                    userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);

                    if(mCamera!=null){
                        mCamera.stopPreview();
                        mCamera.setPreviewCallback(null);
                        mCamera.release();
                        mCamera = null;
                    }

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    };

    private View.OnClickListener btnSOSListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            sendGCM();
        }
    };

    public void sendGCM(){
        //take picture before send GCM
        cameraCapture();
    }

    //Cheecking email verification
    public void chkEmailverification(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_emailVerify";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VERIFY_EMAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(MainActivity.class.getName(), "RegisterGcm Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String isemail = jObj.getString("isemail");
                    JSONObject obj = new JSONObject(isemail);
                    String emailStatus = obj.getString("status");

                    // Check for error node in json
                    if (!error && emailStatus.equals("2")) {
                        Log.d("REG Log","Login + emailStatus: " +emailStatus);

                        session.setAuthentication(true);
                        updateSOSbutton();

                    }else if(!error){

                        session.setAuthentication(false);

                    } else {
                        // Error in login. Get the error message
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
                Log.e(MainActivity.class.getName(), "Email ckecking Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "ChkEmail");
                params.put("email", email);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void enablingToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        // display the first navigation drawer view on app launch
        displayView(0);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new MsgFragment();
                title = getString(R.string.app_name);
                break;
            case 1:
                fragment = new MsgFragment();
                title = getString(R.string.title_friends);
                break;
            case 2:
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                title = getString(R.string.title_messages);
                break;
            case 3:
                Intent intentGuide = new Intent(getApplicationContext(), GuideFragment.class);
                startActivity(intentGuide);
                title = getString(R.string.title_friends);
                break;
            case 4:
                logoutUser();
                title = getString(R.string.title_logout);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fakeNav || v.getId() == R.id.fakeCamera || v.getId() == R.id.fakeFriend
                || v.getId() == R.id.map || v.getId() == R.id.btnSOS || v.getId() == R.id.emergency
                || v.getId() == R.id.fakecall || v.getId() == R.id.safezone || v.getId() == R.id.guardian ) {

            presentShowcaseSequence();

        }
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
              //  Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this)
                .setTarget(fakeNav)
                .setDismissText("GOT IT")
                .setContentText("Navigation Bar - You can access Notification page, see some guides, and Log out")
                .withRectangleShape()
                .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fakeCamera)
                        .setDismissText("GOT IT")
                        .setContentText("Photo - You can send photo and text to selected bodyguards.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fakeFriend)
                        .setDismissText("GOT IT")
                        .setContentText("Bodyguard - You can view friends and add friends to be your bodyguards.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fakeMap)
                        .setDismissText("GOT IT")
                        .setContentText("I'm here - You can share your location to your bodyguards by clicking the map.")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnSOS)
                        .setDismissText("GOT IT")
                        .setContentText("SOS Request - You can ask for help from someone.")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(emergency)
                        .setDismissText("GOT IT")
                        .setContentText("Emergency Call - You can contact some related agencies where can provide some help.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(fakecall)
                        .setDismissText("GOT IT")
                        .setContentText("Fake Call - You can make a fake phone call.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(safezone)
                        .setDismissText("GOT IT")
                        .setContentText("SafeZone - You can view unsafe zone areas and receive the notification.")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(guardian)
                        .setDismissText("GOT IT")
                        .setContentText("Guardian - You can invite your bodyguards to track your location.")
                        .withRectangleShape()
                        .build()
        );

        sequence.start();

    }


}
