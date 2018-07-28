package com.tech.ac.sys.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;

public class TempleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple);

        Intent intent;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            intent = new Intent(this , MainActivity.class);
            startActivity(intent);
            finish();
        }



        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session session = new Session(TempleActivity.this);
                session.logout();
            }
        });

    }
}
