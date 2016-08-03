package com.example.playitsafe.LoginRegister.activity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mook.myapplication.backend.registration.Registration;
import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.AppController;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.MainActivity;
import com.example.playitsafe.R;
import com.example.playitsafe.SOS.RegisterGCM;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Mook on 11/03/2016.
 */
public class IDcardActivity extends AppCompatActivity {

    private static String TAG = IDcardActivity.class.getSimpleName();

    private Button btn_verify_idcard;
    private EditText inputIDcardnum;
    private String idnumber,name,email,password;
    private String regId ="";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);

        inputIDcardnum = (EditText) findViewById(R.id.inputIDcardnum);
        btn_verify_idcard = (Button) findViewById(R.id.btn_verify_idcard);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        password = i.getStringExtra("password");


        // button Click Event
        btn_verify_idcard.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                idnumber = inputIDcardnum.getText().toString().trim();

                // Check for empty data in the form
                if (!idnumber.isEmpty() ) {
                    // login user
                    verifyIDcard(idnumber);

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the ID card number!", Toast.LENGTH_LONG).show();

                }
            }

        });

    }

    private void verifyIDcard(final String idnumber) {
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_VERIFY_IDCARD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {

                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {

                        new GcmRegistrationAsyncTask(getApplicationContext()).execute();
                        // parsing the user profile information
                      //  JSONObject profileObj = responseObj.getJSONObject("profile");

                     //   String name = profileObj.getString("name");
                     //   String email = profileObj.getString("email");
                      //  String password = profileObj.getString("password");

                        SessionManager pref = new SessionManager(getApplicationContext());
                        pref.createLogin(name, email,password);
                        pref.setLogin(true);

                        Intent intent = new Intent(IDcardActivity.this, EmailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.putExtra("isSendEmail",true);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idcard", idnumber);
                params.put("email", email);
                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
        private static final String SENDER_ID = "702737300426";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
               /* Registration.Builder builder =
                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                                new AndroidJsonFactory(), null);
                regService = builder.build();*/
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        // .setRootUrl("https://play-it-safe.appspot.com//_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code
                regService = builder.build();
            }

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;
                SessionManager pref = new SessionManager(getApplicationContext());
                pref.setRegId(regId);

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            if(regId != null){
                RegisterGCM regGcm = new RegisterGCM();
                regGcm.registerGcmId(email,password,regId);
            }
        }
    }

}