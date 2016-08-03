package com.example.playitsafe.SOS;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.example.playitsafe.DBconnect.AppConfig;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.MainActivity;
import com.example.playitsafe.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.LogRecord;

/**
 * Created by Mook on 11/01/2016.
 */
public class SOSActivity extends AppCompatActivity {
    private static final String TAG = SOSActivity.class.getSimpleName();
    private Button btnSafe,sound;
    private int count = 0;
    private Handler mHandler;
    private Runnable mTimer;
    String email,regId,status,ImgName;
    Double latitude,longitude;
    SessionManager session;
    MediaPlayer mpBgm;
    int chkSound=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();
        HashMap<String, String> profile = session.getUserDetails();
        email = profile.get("email");
        regId = profile.get("regId");
        ImgName = intent.getStringExtra("ImgName");
     //   status = intent.getStringExtra("noti");

        mHandler = new Handler();
        mTimer = new Runnable() {
            @Override
            public void run() {
                count++;
                // TODO Auto-generated method stub
                // 5 sec
                if (count > 0 ) {
                    Log.d("DEBUG","count: "+count);
                    RegisterGCM regGcm = new RegisterGCM();
                    regGcm.sendNotiOtherRound(email , regId, ImgName);
                    mHandler.postDelayed(this, 60000);
                }
                if (count == 5){
                    count =0;
                    mHandler.removeCallbacks(mTimer);
                    session.setIsShortcut(false);
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        };
        mHandler.postDelayed(mTimer, 60000);

        mpBgm = MediaPlayer.create(SOSActivity.this, R.raw.siren);
        //Sound maximum
        // Get the AudioManager
        AudioManager audioManager = (AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
        // Set the volume of played media to maximum.
        audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        btnSafe = (Button) findViewById(R.id.btnSafe);
        sound = (Button)findViewById(R.id.sound);

        sound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(chkSound==0){
                    sound.setBackgroundResource(R.drawable.button_music_on_pressed);
                    mpBgm.setLooping(true);
                    mpBgm.start();
                    chkSound =1;
                }else if(chkSound==1){
                    sound.setBackgroundResource(R.drawable.button_music_off_pressed);
                    mpBgm.pause();
                    chkSound =0;
                }

            }
        });

        // Link to Register Screen
        btnSafe.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mHandler.removeCallbacks(mTimer);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    public void onResume() {
        super.onResume();
        if(chkSound==1)
            mpBgm.start();
    }

    public void onPause() {
        super.onPause();
        if(chkSound==0)
            mpBgm.pause();
    }

    public void onDestroy() {
        super.onDestroy();
        mpBgm.stop();
        mpBgm.release();
        mpBgm = null;
    }

}

