package com.example.playitsafe.Imhere;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playitsafe.Bodyguard.Bodyguard;
import com.example.playitsafe.Bodyguard.DatabaseManager;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.Guardian.ShowContAdapter;
import com.example.playitsafe.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Imhere extends Activity {

    ArrayList<Bodyguard> blist = new ArrayList<>();
    //GoogleMap mMap;
    double lat, lng;
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADD ="addFrom";
    DatabaseManager databaseManager;
    GridView gridView;
    ShowContAdapter showContAdapter;
    BroadcastReceiver recieve_noti;
    Context context;
    String inputEmail = "" , email;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    Intent intent;
    SessionManager session;
    DatabaseManager dbmanager;
    TextView nouser,who;
    Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);
        context = getApplicationContext();
        gridView = (GridView)findViewById(R.id.gridView3);
        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        intent = getIntent();
        lat = intent.getDoubleExtra("lat",0.0);
        lng = intent.getDoubleExtra("lng",0.0);
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> profile = session.getUserDetails();
        email = profile.get("email");
        who = (TextView) findViewById(R.id.who);
        who.setVisibility(View.INVISIBLE);
        nouser = (TextView) findViewById(R.id.nouser);
        stop = (Button) findViewById(R.id.button6);

        stop.setVisibility(View.INVISIBLE);

        createBlist();
        showContAdapter = new ShowContAdapter(context, blist);
        gridView.setAdapter(showContAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("oeii", "back");
                if (showContAdapter.getBodyguard(position).getaddFrom().equals("App")) {
                    inputEmail = showContAdapter.getBodyguard(position).getbEmail();
                    Log.d("oeii", inputEmail);
                    new SendGcmToServer().execute();
                } else {

                    dbmanager = new DatabaseManager(getApplicationContext());
                    ArrayList<String> phoneList = dbmanager.getPhoneNumberBodyguards();
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        String sms = "I'm here: http://maps.google.com/maps?q="+lat+","+lng+"&ll="+lat+","+lng+"&z=17";
                        String number = showContAdapter.getBodyguard(position).getbPhone();
                        smsManager.sendTextMessage( number, null, sms, null, null);
                        Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    //Toast.makeText(context, "Send SMS", Toast.LENGTH_LONG).show();
                }
            }
        });

        recieve_noti = new BroadcastReceiver() {
            String msg;

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extra = intent.getExtras();
                // GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                //Toast.makeText(context,extra.getString("message"),Toast.LENGTH_LONG).show();
                // String message=intent.getStringExtra("message");

                Log.d("oeii", "in local board " + extra.getString("message"));

                // Toast.makeText(getApplicationContext(),extra.size(),Toast.LENGTH_LONG).show();
                String data = extra.getString("message");
                if (data.equals(null)) {
                    return;
                }

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(recieve_noti, new IntentFilter("message_received"));


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

            String url = "http://play-it-safe.appspot.com/imhere.php?email="+inputEmail+"&sender="+email+"&Latitute=" + lat + "&Longtitute=" + lng;
            Log.i("oeii", "url" + url);

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
            Log.i("oeii", "response" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();

            if (result != null) {
                if (result.equals("found")) {

                    Toast.makeText(context, "The result is " + result, Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }


            } else {

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }

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

    public void createBlist(){
        blist.clear();
        Cursor cursor = databaseManager.selectAllBodyguard();
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
                    photo = Uri.parse("android.resource://example.com.bodyguard/drawable/bodyguard_user");
                    blist.add(count++, new Bodyguard(contactid, contactName, contactNumber, contactEmail, photo, contactAddFrom));
                } else {
                    blist.add(count++, new Bodyguard(contactid, contactName, contactNumber, contactEmail, Uri.parse(contactPhoto), contactAddFrom));
                }


            }while (cursor.moveToNext());

        }else{
            nouser.setText("No one is your bodyguard. Please add the bodyguards.");
        }

        for(int i=0;i<blist.size();i++){
            System.out.println(blist.size());
            System.out.println(blist.get(i).getbName() + blist.get(i).getbEmail() + blist.get(i).getbPhone() + blist.get(i).getaddFrom());
        }

    }
}