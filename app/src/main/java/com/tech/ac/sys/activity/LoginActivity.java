package com.tech.ac.sys.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;
import com.tech.ac.sys.util.Validation;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout lysignup;
    private EditText Edemail,Edpassword;
    private Button btnLogin;
    private String email,password;
    private String fcmToken;
    private ProgressBar progressBar;
    private String TAG = "SYS";
    private UserInfoFCM userInfo;
    CheckBox remindme;
    Session app_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lysignup = (LinearLayout) findViewById(R.id.ly_createAccount);
        Edemail = (EditText) findViewById(R.id.ed_userName);
        Edpassword = (EditText) findViewById(R.id.ed_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        remindme = findViewById(R.id.remindme);
        app_session = new  Session(this);

        if(app_session != null){
            Edemail.setText(app_session.getemail());
            Edpassword.setText(app_session.getpass());
            remindme.setChecked(app_session.getIschecked());

        }

        userInfo = new UserInfoFCM();
        lysignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = Edemail.getText().toString().trim();
                password = Edpassword.getText().toString().trim();
                fcmToken = FirebaseInstanceId.getInstance().getToken();
                if(isValidField())
                loginFirebaseDataBase(email,password);

            }
        });
    }

    private boolean isValidField() {
        Validation v = new Validation(this);
        if (!v.isNullValue(Edemail)) {
           Toasty.error(this,"Enter email address").show();
            Edemail.requestFocus();
            return false;

        } else if (!v.isEmailValid(Edemail)) {
            Toasty.error(this,"Enter valid email").show();
            return false;

        } else if (!v.isNullValue(Edpassword)) {
            Toasty.error(this,"Enter password").show();
            Edpassword.requestFocus();
            return false;

        } else if (!v.isPasswordValid(Edpassword)) {
            Toasty.error(this,"Atleast_4_characters_required").show();
            return false;
        }
        return true;
    }
    private void loginFirebaseDataBase(final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.d("TAG", "performFirebaseLogin:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {

                            checkIfEmailVerified();

                        } else {

                            progressBar.setVisibility(View.GONE);
                            Toasty.error(LoginActivity.this,"Email Address is not registered").show();

                        }
                    }
                });
    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            app_session.createAdmin(email);
            if (remindme.isChecked()) {
                app_session.saveEmailPass(email, password, true);
            } else {
                app_session.uncheck();
            }

            getCurrentUserNode();
            progressBar.setVisibility(View.GONE);
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(LoginActivity.this, TempleActivity.class));
            finish();
            FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
    }

    private void addUserFirebaseDatabase(GoogleSignInAccount account) {
        String firebaseToken= FirebaseInstanceId.getInstance().getToken();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final UserInfoFCM user = new UserInfoFCM(FirebaseAuth.getInstance().getCurrentUser().getUid(),account.getEmail(),account.getDisplayName(),firebaseToken,account.getPhotoUrl().toString(),"","");

        database.child(Constant.ARG_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        if (task.isSuccessful()) {
                            getCurrentUserNode();
                            //app_session.createSession(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCurrentUserNode(){

        final Session  app_session = new Session(LoginActivity.this);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(uid != null){
            FirebaseDatabase.getInstance().getReference().
                    child(Constant.ARG_USERS).
                    child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userInfo =  dataSnapshot.getValue(UserInfoFCM.class);
                    app_session.createSession(userInfo);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}
