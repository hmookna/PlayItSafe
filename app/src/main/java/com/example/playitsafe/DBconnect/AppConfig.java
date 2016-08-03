package com.example.playitsafe.DBconnect;


/**
 * Created by Mook on 11/01/2016.
 */
public class AppConfig {

    // Server user login url 192.168.1.41 // 10.99.6.1 // 172.20.10.2

    public static String URL_LOGIN = "http://playitsafe-1309.appspot.com/index";

    public static String URL_EDIT_PROFILE = "http://playitsafe-1309.appspot.com/edit_profile";

    public static String URL_REGISTER_GCM = "http://playitsafe-1309.appspot.com/check_regist";

    public static String URL_SENDNOTI_GCM = "http://playitsafe-1309.appspot.com/send_message";

    public static String URL_LOCATION = "http://playitsafe-1309.appspot.com/location";

    public static String FILE_UPLOAD_URL = "http://playitsafe-1309.appspot.com/sendPhoto";

    // server URL configuration
    public static final String URL_REQUEST_SMS = "http://playitsafe-1309.appspot.com/request_sms";

    public static final String URL_VERIFY_OTP = "http://playitsafe-1309.appspot.com/verify_otp";

    public static final String URL_VERIFY_IDCARD = "http://playitsafe-1309.appspot.com/verify_idcard";

    //Email verification
    public static final String URL_VERIFY_EMAIL = "http://playitsafe-1309.appspot.com/verify_email";

    // get photo to show
    public static final String URL_PHOTO = "http://playitsafe-1309.appspot.com/getPhoto";

   // public static final String URL_RETRIEVEBODYGUARD = "http://playitsafe-1309.appspot.com/retrieveBodyguard";

    public static final String URL_STOREBODYGUARD = "http://playitsafe-1309.appspot.com/storebodyguard";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Play It Safe File";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "THSMSAPI";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";

    public static final String EMAIL ="playitsafe.application@gmail.com";
    public static final String PASSWORD ="playitsafe@application";

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}
