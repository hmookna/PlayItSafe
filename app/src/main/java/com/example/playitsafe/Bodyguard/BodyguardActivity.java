package com.example.playitsafe.Bodyguard;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.R;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BodyguardActivity extends Activity  {
    private ImageButton button;
    ArrayList<Bodyguard> blist = new ArrayList<>();
    private Context context;
    Uri uriContact;
    GridView gridView;
    ImageAdapter imageAdapter;
    SessionManager session;
    private String contactID;
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ADD ="addFrom";
    Bodyguard bodyguard;
    DatabaseManager databaseManager;
    String emailuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodyguard);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();
        gridView = (GridView)findViewById(R.id.gridView);
        context = getApplicationContext();
        createBlist();
        imageAdapter = new ImageAdapter(context, blist);
        imageAdapter.notifyDataSetChanged();
        gridView.setAdapter(imageAdapter);
        button = (ImageButton) findViewById(R.id.button);
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> profile= session.getUserDetails();
        emailuser  = profile.get("email");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(BodyguardActivity.this, button, Gravity.CENTER);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_bodyguard, popup.getMenu());


                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                BodyguardActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        if (item.getItemId() == R.id.one) {
                            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 1);
                            Log.i("debug", "intent");


                        }
                        if (item.getItemId() == R.id.two) {
                            Intent intent = new Intent(BodyguardActivity.this, AppContact.class);
                            Log.i("debug", "intent2");
                            startActivityForResult(intent, 0);

                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }


        }); //closing the setOnClickListener method
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BodyguardActivity.this, ModifyBodyguard.class);
                int keyId = blist.get(position).getbID();
                intent.putExtra("position", position);
                intent.putExtra("KEY_ID", keyId);
                intent.putExtra("name",imageAdapter.getBodyguard(position).getbName());
                intent.putExtra("email",imageAdapter.getBodyguard(position).getbEmail());
                intent.putExtra("phone",imageAdapter.getBodyguard(position).getbPhone());
                intent.putExtra("photo",imageAdapter.getBodyguard(position).getbPhoto().toString());
                Log.i("debug", "intent3");
                startActivityForResult(intent, 2);
            }
        });

    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 1 && resultCode == RESULT_OK) {
            Log.d("debug", "Response: " + data.toString());
            String record = null;
            uriContact = data.getData();
            if(getPhotoUri() == null) {
                String path = "android.resource://com.example.playitsafe/drawable/bodyguard_user";
                record = retrieveContactName() + "##" + retrieveContactNumber() + "##" + getNameEmailDetails() + "##" + path + "##Phone book";
            }
            else{
                record = retrieveContactName() + "##" + retrieveContactNumber() + "##" + getNameEmailDetails() + "##" + getPhotoUri().toString() + "##Phone book";
            }
            try {
                record = URLEncoder.encode(record, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(databaseManager.checkNo(retrieveContactNumber())==0) {
                new SendDBToServer().execute(record);
                new AddToDB().execute();

            }
            else Toast.makeText(context, "This contact is already exist", Toast.LENGTH_LONG).show();
        }
        if (reqCode == 0 && resultCode == RESULT_OK) {
            Log.d("debug", "Response: " + data.toString());
            uriContact = data.getData();
            String record = data.getStringExtra ("insert");
            try {
                record = URLEncoder.encode(record, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("debug", record);
            if(databaseManager.checkNo(data.getStringExtra("number"))==0&&data.getStringExtra("number")!=null) {
                new SendDBToServer().execute(record);
                new AddToDB().execute();

            }
            else Toast.makeText(context, "This contact is already exist", Toast.LENGTH_LONG).show();
        }
        if (reqCode == 2 && resultCode == RESULT_OK) {
            Log.d("debug","req2");
            new ReceiveDB().execute();

        }


    }
    private String retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        if(contactNumber!=null){
            contactNumber = contactNumber.replace(" ", "");
            contactNumber = contactNumber.replace("-", "");
            contactNumber = contactNumber.replace("(", "");
            contactNumber = contactNumber.replace(")", "");
            contactNumber = contactNumber.replace("+66", "0");
        }


        cursorPhone.close();
        //Log.d("debug", "Contact Phone Number: " + contactNumber);
        return contactNumber;
    }

    private String retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        return contactName;
    }
    public String getNameEmailDetails() {

        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);;
        int idx;
        int colIdx;
        String email = "";
        String id ="";
        if (cursor.moveToFirst()) {
            idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            id = cursor.getString(idx);
        }
        cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                null, null);
        if (cursor.moveToFirst()) {
            colIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            email = cursor.getString(colIdx);
        }
        cursor.close();
        return email;
    }
    public Uri getPhotoUri() {

        String contactID = "";
        try {
            Cursor cursorID = context.getContentResolver().query(uriContact,
                    new String[]{ContactsContract.Contacts._ID},
                    null, null, null);
            if (cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }
            Cursor cur = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactID + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uriContact);
            if (input == null) {
                return null;
            }
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(contactID));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void createBlist(){
        blist.clear();
        Uri photo;
        Cursor cursor = databaseManager.selectAllBodyguard();
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

        }

        for(int i=0;i<blist.size();i++){
            System.out.println(blist.size());
            System.out.println(blist.get(i).getbName() + blist.get(i).getbEmail() + blist.get(i).getbPhone() + blist.get(i).getaddFrom());
        }

        databaseManager.close();
    }

    private class SendDBToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = AppConfig.URL_STOREBODYGUARD+"?rec="+params[0]+"&bodyguardOf="+emailuser;
            Log.d("debug", "url " + url);
            OkHttpClient httpClient = new OkHttpClient();

            String response = null;

            try {

                //url = url.replace(" ", "%20");
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

            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
            Log.i("debug", "response"+response);



            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            if (result != null) {

                if (result.equals("success")) {

                    Toast.makeText(context, "The result is " + result, Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
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

    public class AddToDB extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            databaseManager.open();
            String url = "http://playitsafe-1309.appspot.com/retrievebodyguard?email="+emailuser;
            Log.d("debug", "url " + url);
            OkHttpClient httpClient = new OkHttpClient();

            String response = null;

            try {

                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url),
                        httpClient);

                for (String subString : response.split("<script", 2)) {
                    response = subString;
                    break;

                }
            } catch (MalformedURLException e) {
                Log.i("debug", "Malform" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                Log.i("debug", "IO" + e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.print(response);
            Log.i("debug", "response"+ response);
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                System.out.print(result);
                Log.d("debug","result");
                if (result.contains("norecord")) {

                    Toast.makeText(context, "Database has no record", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "Database has record : ",Toast.LENGTH_LONG).show();
                    System.out.print(result);
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
                        JSONArray jsonarray = new JSONArray(result);
                        blist.clear();

                        String jsonobject = (String)jsonarray.get(jsonarray.length()-1);
                        Map json = (Map)parser.parse(jsonobject, containerFactory);
                        Iterator iter = json.entrySet().iterator();
                        String name = (String) json.get("name");
                        String mobile = (String) json.get("mobile");
                        String email = (String) json.get("email");
                        String img = (String) json.get("img");
                        String strid = (String) json.get("id");
                        String addFrom = (String) json.get("from");
                        int id = Integer.parseInt(strid);
                        System.out.print(id + " " + name + " " + mobile + " " + email + " " + img + " " + addFrom);
                        if(!img.equals(null)&&!img.equals("x")){
                            //blist.add(new Bodyguard(id,name,mobile,email,getImageContentUri(context,new File(img)),addFrom));
                            databaseManager.insertBodyguard(databaseManager.getLastKId()+1, name, mobile, email, img, addFrom);
                            Log.d("debug", "has img path");

                        }
                        else {
                            //blist.add(new Bodyguard(id, name, mobile, email, null,addFrom));
                            databaseManager.insertBodyguard(databaseManager.getLastKId()+1, name, mobile, email, null, addFrom);
                            Log.d("debug", "no img path");

                        }
                        Log.d("debug",databaseManager.getRowNo()+"");



                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    createBlist();
                    imageAdapter = new ImageAdapter(context, blist);
                    imageAdapter.notifyDataSetChanged();
                    gridView.setAdapter(imageAdapter);
                }
            }else{

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }
            databaseManager.close();

        }

    }

    public class ReceiveDB extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            databaseManager.open();
            databaseManager.deleteAll();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            String url = "http://playitsafe-1309.appspot.com/retrievebodyguard";
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
                            if (!img.equals(null) && !img.equals("x")) {
                                //blist.add(new Bodyguard(id,name,mobile,email,getImageContentUri(context,new File(img)),addFrom));
                                databaseManager.insertBodyguard(databaseManager.getLastKId() + 1, name, mobile, email, img, addFrom);
                            } else {
                                //blist.add(new Bodyguard(id, name, mobile, email, null,addFrom));
                                databaseManager.insertBodyguard(databaseManager.getLastKId() + 1, name, mobile, email, null, addFrom);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    createBlist();
                    imageAdapter = new ImageAdapter(context, blist);
                    imageAdapter.notifyDataSetChanged();
                    gridView.setAdapter(imageAdapter);
                }
            } else {

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }
            databaseManager.close();
        }

    }
}

