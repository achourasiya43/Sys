package com.tech.ac.sys.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by mindiii on 22/9/17.
 */

public class NotifyInfo implements Serializable {
    public String NotificationId;
    public String image_FirebaseURL;
    public String title;
    public String description;
    public String timestamp;

    public NotifyInfo() {
    }

    public NotifyInfo(String image_FirebaseURL, String title, String description,String timestamp) {
        this.image_FirebaseURL = image_FirebaseURL;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }
}
