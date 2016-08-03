package com.example.playitsafe.SOS;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.playitsafe.Camera.showPhotoMsg;
import com.example.playitsafe.DBconnect.AppController;
import com.example.playitsafe.DBconnect.SessionManager;
import com.example.playitsafe.Design.Notification.NotificationDatabase;
import com.example.playitsafe.Guardian.ReceiveMapAct;
import com.example.playitsafe.Imhere.ImhereMap;
import com.example.playitsafe.MainActivity;
import com.example.playitsafe.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alice on 1/8/2016.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public int msgID=-1;
    SessionManager session;
    NotificationDatabase notiDB;
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        session =  new SessionManager(getApplicationContext());
        notiDB = new NotificationDatabase(getApplicationContext());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Log.e("ectras",extras.toString());
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if(extras != null && !extras.isEmpty()){

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                if (intent.hasExtra("imgName") && !intent.hasExtra("text")) {
                    msgID++;
                    session.setMsgID(msgID);
                    Date date = new Date();
                    notiDB.insertNotification(extras.getString("sender"), extras.getString("imgName"), extras.getString("latitude"),extras.getString("longtitude"),dateFormat.format(date));
                    showToast(extras.getString("message"));
                    showNotification(extras.getString("message"), extras.getString("latitude"), extras.getString("longtitude"), extras.getString("imgName"), extras.getString("sender"));

                } else if (intent.hasExtra("text")) {
                    showToast(extras.getString("text"));
                    showPhoto(extras.getString("imgName"), extras.getString("sender"), extras.getString("text"));

                }

            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
    public void showNotification(String msg,String lat,String lng,String imgName,String sender){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_logo);
        builder.setContentTitle("Play It Safe - Help Request");
        builder.setContentText(msg);
        Intent intent = new Intent(this,RouteActivity.class);//when click notification
        intent.putExtra("userlatitude",lat);
        intent.putExtra("userlongtitude",lng);
        intent.putExtra("sender",sender);
        //Log.d("test intent bytearray",bytearray);
        intent.putExtra("imgName",imgName);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RouteActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NM.notify(msgID,builder.build());

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getApplicationContext().getPackageName() + "/raw/siren.mp3");
        Ringtone r = RingtoneManager.getRingtone(AppController.getInstance().getApplicationContext(), alarmSound);
        r.play();

    }

    public void showPhoto( String imgName, String sender, String text ){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_logo);
        builder.setContentTitle("Message from " + sender );
        builder.setContentText(text);
        Intent i = new Intent(this,showPhotoMsg.class);//when click notification
        i.putExtra("sender",sender);
        i.putExtra("text",text);
        i.putExtra("imgName",imgName);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RouteActivity.class);
        stackBuilder.addNextIntent(i);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NM.notify(0,builder.build());

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getApplicationContext().getPackageName() + "/raw/siren.mp3");
        Ringtone r = RingtoneManager.getRingtone(AppController.getInstance().getApplicationContext(), alarmSound);
        r.play();
    }


}
