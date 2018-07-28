package com.tech.ac.sys.model;

/**
 * Created by mindiii on 6/9/17.
 */
public class UserInfoFCM {

    public String uid = "";
    public String email = "";
    public String name = "";
    public String firebaseToken = "";
    public String profilePic = "";
    public String address = "";
    public String phone = "";

    public UserInfoFCM() {
    }

    public UserInfoFCM(String uid, String email,
                       String name, String firebaseToken,
                       String profilePic,String address ,String phone) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.firebaseToken = firebaseToken;
        this.profilePic = profilePic;
        this.address = address;
        this.phone = phone;
    }


}
