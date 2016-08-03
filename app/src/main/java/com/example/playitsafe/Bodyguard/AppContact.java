package com.example.playitsafe.Bodyguard;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.playitsafe.R;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppContact extends Activity {
    String phoneNumber;
    ArrayList<String> arrayList;
    ArrayList<String> arruser = new ArrayList<String>();
    ArrayList<String> arrmobile = new ArrayList<String>();
    ArrayList<String> arremail = new ArrayList<String>();
    ArrayList<String> arrregID = new ArrayList<String>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bodyguard_listview);
        arrayList = new ArrayList<String>();
        getNumber(this.getContentResolver());
        new SendArrayToServer().execute();
        context = getApplicationContext();
    }

    public void getNumber(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int i;
        Log.d("oeii", "getnum");
        while (phones.moveToNext()) {
            //String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");
            phoneNumber = phoneNumber.replace("+66", "0");

            if(phoneNumber.length()==10){
                arrayList.add(phoneNumber);
            }
        }
        phones.close();
    }

    private class SendArrayToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }
        @Override

        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = "http://playitsafe-1309.appspot.com/usercontact?key="+arrayList;
            Log.d("oeii", "url " + url);
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
            Log.i("oeii", "response" + response);

            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();
            String[] str;

            if (result != null) {
                String s = result;
                System.out.println(s);
                JSONParser parser = new JSONParser();
                ContainerFactory containerFactory = new ContainerFactory(){
                    public List creatArrayContainer() {
                        return new LinkedList();
                    }

                    public Map createObjectContainer() {
                        return new LinkedHashMap();
                    }
                };
                try {
                    JSONArray jsonarray = new JSONArray(s);

                    System.out.println(jsonarray.get(0));
                    System.out.println(jsonarray.length());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        String jsonobject = (String)jsonarray.get(i);
                        Map json = (Map)parser.parse(jsonobject, containerFactory);
                        Iterator iter = json.entrySet().iterator();
                        String name = (String) json.get("name");
                        String mobile = (String) json.get("mobile");
                        String email = (String) json.get("email");
                        String regID = (String) json.get("regId");
                        System.out.println(name + "       " + email + "        " + mobile + "       " + regID);
                        arruser.add(name);
                        arrmobile.add(mobile);
                        arremail.add(email);
                        arrregID.add(regID);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }
                String[] arrname = arruser.toArray(new String[0]);
                String[] mobile = arrmobile.toArray(new String[0]);
                String[] email = arremail.toArray(new String[0]);

                System.out.print(arrname);

                final CustomAdapter adapter = new CustomAdapter(getApplicationContext(), arrname,mobile,email);

                ListView listView = (ListView)findViewById(R.id.listView);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        String record = adapter.getName(position)+"##"+adapter.getMobile(position)+"##"+adapter.getEmail(position)+"##x##App";
                        Log.d("debug", record);
                        Intent intent = new Intent ( );
                        intent.putExtra("insert", record );
                        intent.putExtra("number",adapter.getMobile(position));
                        setResult ( RESULT_OK, intent );
                        Log.d("debug","back");

                        finish ();

                    }
                });

                if (result.equals("success")) {

               //     Toast.makeText(context, "The result is " + result, Toast.LENGTH_LONG).show();

                } else {

                 //   Toast.makeText(context, "No friends in Play It Safe" + result, Toast.LENGTH_LONG).show();
                }


            }else{

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
        for (int count; (count = in.read(buffer)) != -1;) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }


}

