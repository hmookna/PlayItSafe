package com.example.playitsafe.Bodyguard;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.playitsafe.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
public class ModifyBodyguard extends Activity{
    String name;
    String phone;
    String email;
    Uri photo;
    Button save,delete;
    Context context;
    ImageView showPhoto;
    Intent intent;
    Uri targetUri;
    String realUri="";
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bodyguard_contact_page);
        context = getApplicationContext();

        intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        email = intent.getStringExtra("email");
        photo = Uri.parse(intent.getStringExtra("photo"));

        final EditText editName = (EditText) findViewById(R.id.editText);
        final EditText editPhone = (EditText) findViewById(R.id.editText2);
        final EditText editEmail = (EditText) findViewById(R.id.editText3);

        editName.setText(name);
        editPhone.setText(phone);
        editEmail.setText(email);
        showPhoto = (ImageView) findViewById(R.id.imageView4);

        Bitmap bitmap = null;

        Log.d("debug", intent.getStringExtra("photo"));
        if(photo.getPath() != ""){
            Log.d("debug",""+ photo.getPath() );
            showPhoto.setImageURI(photo);


        }
        else{
            Log.i("debug", "No : ");
            showPhoto.setImageDrawable(getResources().getDrawable(R.drawable.user));

        }


        ImageButton editPhoto = (ImageButton) findViewById(R.id.imageButton);
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        save = (Button) findViewById(R.id.button3);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = "save";
                String newName = editName.getText().toString();
                String newEmail= editEmail.getText().toString();
                String newPhone= editPhone.getText().toString();
                String content = newName+"##"+newEmail+"##"+newPhone+"##"+realUri;

                try {
                    content = URLEncoder.encode(content, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, intent );
                new SendGcmToServer().execute(phone, content, "save");



            }
        });

        delete = (Button) findViewById(R.id.button4);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = "save";
                setResult(RESULT_OK, intent );
                new SendGcmToServer().execute(phone, "", "delete");


            }
        });

    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        showPhoto = (ImageView) findViewById(R.id.imageView4);
        Log.d("debug",""+data);
        if (requestCode == 1){
            tag = "photo";
            if(data!=null){
                targetUri = data.getData();
                realUri = getRealPathFromURI(targetUri);
                showPhoto.setImageURI(targetUri);
                Log.i("debug_photo", "Uri1 : " + photo.toString() + "realUSI : " + realUri);
            }
            else{Log.d("debug","nothing");}
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
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

            String url = "http://playitsafe-1309.appspot.com/modifydb?bodyguardOf="+email+"&mobile="+params[0]+"&content="+params[1]+"&tag="+params[2];
            Log.i("debug", "url" + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();

            String response = null;
            // String response=Utility.callhttpRequest(url);

            try {
                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url), client_for_getMyFriends);
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
            Log.i("debug", "response" + response);
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
            Log.d("debug", "finish222");

            if(tag=="save"||tag=="delete"){
                finish();
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

}

