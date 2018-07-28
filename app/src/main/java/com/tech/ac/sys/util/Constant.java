package com.tech.ac.sys.util;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Calendar;


public class Constant {
    public static String Admin_Email = "anilchourasiya0786@gmail.com";
    public static int HOME_STATE = 1;
    public static int SEARCH_STATE = 2;
    public static int FEVOURITE_STATE = 3;
    public static String COMMENTS = "Comments";
    public static String CHAT_ROOM = "chat_room";
    public static String Like_Table = "like_table";

    public static String ADMIN = "admin";
    public static String USER_ID = "uid";
    public static String EMAIL = "email";
    public static String FULL_NAME = "name";
    public static String firebase_token = "firebase_token";
    public static String Profile_pic = "Profile_pic";
    public static String Address = "address";
    public static String Phone = "phone";

    public static String EMAIL_ID  = "email";
    public static String PASSWORD = "password";
    public static String IsChecked = "ischecked";

    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    // key for run time permissions
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int REQUEST_CODE_PICK_CONTACTS = 100;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    public static final int PERMISSION_REQUEST_CONTACT = 103;
    public static final int PERMISSION_READ_PHONE_STATE = 104;
    public static final int MY_PERMISSIONS_REQUESTACCESS_FINE_LOCATION = 106;
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;


   //FireBase....................................................
    public static final String ARG_USERS = "users";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final String ARG_FRIENDS = "friends";
    public static final String ARG_UID = "uid";
    public static String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month1 = month + 1;
        return (day + "-" + month1 + "-" + year);

    }

    public static String getHashKey(String packageName, Activity context) {
        PackageInfo info;
        String something = "";
        try {
            info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                something = new String(Base64.encode(md.digest(), 0));

                Log.e("hash key", something);
            }
        } catch (Exception e1) {
            Log.e("name not found", e1.toString());
        }
        return something;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
