package com.example.playitsafe.DBconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Mook on 11/01/2016.
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PlayItSafe";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SHORTCUT = "isShortcut";
    private static final String KEY_VERIFY = "isAuthenticated";
    private static final String KEY_REGID = "regId";
    private static final String KEY_NOTIFICATIONS_ID = "msgID";
    private static final String KEY_PROFILE_PIC="profile_pic";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_EDIT ="edit";


    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void createLogin(String name, String email,String password) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.commit();
    }

    public void setRegId(String regId){
        editor.putString(KEY_REGID, regId);
        editor.commit();
    }


    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void setProfilePic(String imgStr) {
        editor.putString(KEY_PROFILE_PIC, imgStr);
        editor.commit();
    }

    public String getProfilePic() {
        return pref.getString(KEY_PROFILE_PIC, null);
    }

    public void setKeyEdit (String edit) {
        editor.putString(KEY_EDIT, edit);
        editor.commit();
    }
    public String getKeyEdit() {
        return pref.getString(KEY_EDIT, null);
    }


    public void setIsShortcut(boolean isShortcut){
        editor.putBoolean(KEY_SHORTCUT, isShortcut);
        editor.commit();
    }

    public boolean isShortcut() {return pref.getBoolean(KEY_SHORTCUT, false); }

    public void setAuthentication(boolean isAuthenticated) {
        editor.putBoolean(KEY_VERIFY, isAuthenticated);
        // commit changes
        editor.commit();
        Log.d(TAG, "User's email verification session modified!");
    }

    public boolean isAuthenticated(){
        return pref.getBoolean(KEY_VERIFY, false);
    }

    public void setMsgID(int msgID) {
        editor.putInt(KEY_NOTIFICATIONS_ID, msgID);
        // commit changes
        editor.commit();
        Log.d(TAG, "message id is modified");
    }

    public int getMsgID(){
        return pref.getInt(KEY_NOTIFICATIONS_ID, 0);
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("password", pref.getString(KEY_PASSWORD, null));
        profile.put("regId", pref.getString(KEY_REGID, null));
        return profile;
    }

}
