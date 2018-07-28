package com.tech.ac.sys.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;
import com.tech.ac.sys.util.Validation;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {
    private EditText Edemail,Edpassword,EdfullName;
    private ImageView profileimage;
    private String email,password,fullname;
    private Button btnRegister;
    private Bitmap profileImageBitmap;
    private LinearLayout lyLoginScreen;
    private ProgressBar progressBar;
    private Uri downloadUrl;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tech.ac.sys.R.layout.activity_registraction);
        init();
        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(RegistrationActivity.this, TempleActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    private void init() {
        Edemail =  findViewById(R.id.ed_email);
        Edpassword = findViewById(R.id.ed_password);
        EdfullName = findViewById(R.id.ed_userName);
        btnRegister = findViewById(R.id.btn_signup);
        profileimage =  findViewById(R.id.profilePic);
        progressBar = findViewById(R.id.spin_kit);
        lyLoginScreen =  findViewById(R.id.login_layout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = Edemail.getText().toString().trim();
                password = Edpassword.getText().toString().trim();
                fullname = EdfullName.getText().toString().trim();
                if(isValidField())
                    firebaseUserRegister(email,password);
            }
        });
        lyLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionAndPicImage();

            }
        });
    }

    private boolean isValidField() {
        Validation v = new Validation(this);
        if (profileImageBitmap == null) {
            Toasty.error(this,"Select profile picture").show();
            return false;

        } else if (!v.isNullValue(EdfullName)) {
            Toasty.error(this,"Enter full name").show();
            EdfullName.requestFocus();
            return false;

        } else if (!v.isNullValue(Edemail)) {
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
            Toasty.error(this,"Atleast 4 characters required").show();
            return false;
        }
        return true;
    }

    public void firebaseUserRegister(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.e("TAG", "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()){

                            addUserFirebaseDatabase(task.getResult().getUser());
                           // progressBar.setVisibility(View.GONE);

                        }
                        else {
                            Toasty.error(RegistrationActivity.this," Email address is already register...").show();
                            progressBar.setVisibility(View.GONE);                        }
                    }
                });
    }

    private void addUserFirebaseDatabase(FirebaseUser firebaseUser) {
        final Session app_session = new Session(RegistrationActivity.this);
        String name = EdfullName.getText().toString().trim();

        String firebaseToken= FirebaseInstanceId.getInstance().getToken();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        UserInfoFCM user = new UserInfoFCM(firebaseUser.getUid(),firebaseUser.getEmail(),name,firebaseToken,downloadUrl.toString(),"","");
        app_session.createSession(user);
        database.child(Constant.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        if (task.isSuccessful()) {

                            sendVerificationEmail();
                            //Toast.makeText(Regis
                            // terActivity.this, "SuccessFully Stored In Firebase", Toast.LENGTH_SHORT).show();
                          /*  Intent intent =new Intent(RegistrationActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();*/
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Not Store", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                            int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(RegistrationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }}
            break;}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                profileImageBitmap = ImagePicker.getImageFromResult(RegistrationActivity.this, requestCode, resultCode, data);
                if (profileImageBitmap != null)
                    profileimage.setImageBitmap(profileImageBitmap);

                Uri uri = getImageUri(RegistrationActivity.this,profileImageBitmap);
                creatFirebaseProfilePicUrl(uri);
            }}
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void creatFirebaseProfilePicUrl(Uri selectedImageUri ) {
        storageRef = storage.getReference("chat_photos_sys" + getString(R.string.app_name));
        StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete( Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().getDownloadUrl();
                }}
        });
    }
}
