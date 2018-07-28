package com.tech.ac.sys.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.tech.ac.sys.app_session.Session;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent  intent = null;
        Session session = new Session(this);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            intent = new Intent(this , TempleActivity.class);
        }else  intent = new Intent(this,LoginActivity.class);

        startActivity(intent);
        finish();
      /*  if(session.isLoggedIn()){
            intent = new Intent(this,MainActivity.class);

        }else {
            intent = new Intent(this,LoginActivity.class);

        }*/


    }
}