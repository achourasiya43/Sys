package com.tech.ac.sys.app_session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.gson.Gson;
import com.tech.ac.sys.activity.LoginActivity;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;

/**
 * Created by mindiii on 5/9/17.
 */

public class Session {

    private Context _context;
    private SharedPreferences mypref ,mypref2;
    private SharedPreferences.Editor editor,editor2;
    private static final String PREF_NAME = "SYS";
    private static final String PREF_NAME2 = "SYS2";
    private static final String IS_FIrebaseLogin = "isFirebaseLogin";

    public Session(Context context){
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mypref2 = _context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor2 = mypref2.edit();
        editor.apply();
        editor2.apply();
    }

    public void createSession(UserInfoFCM userInfo) {
        createSession(userInfo,false);

    }

    public void createSession(UserInfoFCM userInfo, boolean isFirebaseLogin) {
        editor.putString(Constant.USER_ID, userInfo.uid);
        editor.putString(Constant.EMAIL, userInfo.email);
        editor.putString(Constant.FULL_NAME, userInfo.name);
        editor.putString(Constant.firebase_token, userInfo.firebaseToken);
        editor.putString(Constant.Profile_pic, userInfo.profilePic);
        editor.putString(Constant.Address,userInfo.address);
        editor.putString(Constant.Phone,userInfo.phone);
        editor.putBoolean(IS_FIrebaseLogin, isFirebaseLogin);
        editor.commit();
    }

    public void createAdmin(String admin){
        editor.putString(Constant.ADMIN, admin);
        editor.commit();
    }
    public String getAdmin(){
       String admin =  mypref.getString(Constant.ADMIN, "");
       return admin;
    }

    public UserInfoFCM getUser(){
        UserInfoFCM userInfoFCM = new UserInfoFCM();
        userInfoFCM.uid = mypref.getString(Constant.USER_ID, "");
        userInfoFCM.name = (mypref.getString(Constant.FULL_NAME, ""));
        userInfoFCM.profilePic = (mypref.getString(Constant.Profile_pic, ""));
        userInfoFCM.email = (mypref.getString(Constant.EMAIL, ""));
        userInfoFCM.firebaseToken = (mypref.getString(Constant.firebase_token, ""));
        userInfoFCM.phone = (mypref.getString(Constant.Phone, ""));
        userInfoFCM.address = (mypref.getString(Constant.Address, ""));

        return userInfoFCM;
    }

    public void setUid(String uid) {
        editor.putString(Constant.USER_ID, uid);
        editor.commit();

    }

    public void saveEmailPass(String email,String password,Boolean ischecked){
        editor2.putString(Constant.EMAIL_ID, "anil@gmail.com");
        editor2.putString(Constant.PASSWORD, "123456");
        editor2.putBoolean(Constant.IsChecked, true);
        editor2.commit();
        editor2.apply();
    }

    public void uncheck(){
       // editor2.clear();
       // editor2.apply();
    }
    public String getemail(){
        String e = mypref2.getString(Constant.EMAIL_ID, "");
        return e;
    }
    public String getpass(){
        return mypref2.getString(Constant.PASSWORD, "");
    }

    public Boolean getIschecked(){
        return mypref2.getBoolean(Constant.IsChecked,false);
    }

    public String getUid(){
        return mypref.getString(Constant.USER_ID,"");
    }


    public boolean getIsFirebaseLogin(){
        return mypref.getBoolean(IS_FIrebaseLogin, false);
    }


    public void logout(){
        editor.clear();
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent showLogin = new Intent(_context,LoginActivity.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);

    }

}
