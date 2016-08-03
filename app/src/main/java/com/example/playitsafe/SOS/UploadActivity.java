package com.example.playitsafe.SOS;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.MainActivity;
import com.example.playitsafe.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Mook on 03/03/2016.
 */
public class UploadActivity extends Activity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    //  private ProgressBar progressBar;
    private String filePath = null;
    //  private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    long totalSize = 0;
    private File fileTo;
    private String email,ImgName,bytearray,imgStr;
    private String regId;
    private double latitude,longtitude;
    private String noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        // Receiving the data from previous activity
        Intent i = getIntent();
        // image or video path that is captured in previous activity
     //   filePath = i.getStringExtra("filePath");
    //   email = i.getStringExtra("email");
     //   regId = i.getStringExtra("regId");
     //   ImgName = i.getStringExtra("imgName");
     //   bytearray = i.getStringExtra("bytearray");
      //  latitude = i.getDoubleExtra("latitude",0.0);
     //   longtitude = i.getDoubleExtra("longtitude",0.0);
     //   noti = i.getStringExtra("noti");
        // boolean flag to identify the media type, image or video
     //   boolean isImage = i.getBooleanExtra("isImage", true);
        SessionManager pref = new SessionManager(getApplicationContext());
        HashMap<String, String> profile = pref.getUserDetails();
        imgStr = profile.get("regId");

        if (imgStr != null) {
         //   Log.d("test file"," "+filePath);
            // Displaying the image or video on the screen
            previewMedia(true, imgStr);
        } else {
            Toast.makeText(getApplicationContext(), "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage,String img) {
        // Checking whether captured media is image or video
        if (isImage) {
            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

          //  final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            int nh = (int) ( decodedByte.getHeight() * (512.0 / decodedByte.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(decodedByte, 512, nh, true);

            imgPreview.setImageBitmap(scaled);
        }
    }


}