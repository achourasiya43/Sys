
package com.tech.ac.sys.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;

import java.io.ByteArrayOutputStream;

import static com.tech.ac.sys.util.Constant.PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_edit,profile_Img,profile_Img_rounded;
    private Button btn_update;
    private EditText ed_user_Name,ed_email,ed_phone;
    private TextView tv_address;
    private Session app_session;
    private  String Uid = "";
    private  String address;
    private CardView cv_address;
    private Bitmap profileImageBitmap;
    private Uri downloadUrl;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        app_session = new Session(this);

        if(app_session.getUser() != null){
            UserInfoFCM userInfoFCM =  app_session.getUser();
            Uid = userInfoFCM.uid;
            String user_name = userInfoFCM.name;
            String user_email = userInfoFCM.email;
            String user_profile_img = userInfoFCM.profilePic;
            String phone = userInfoFCM.phone;
            ed_user_Name.setText(user_name);
            ed_email.setText(user_email);
            ed_phone.setText(phone);
            tv_address.setText(userInfoFCM.address);

            Glide.with(this).load(user_profile_img).into(profile_Img);
            Glide.with(this).load(user_profile_img).into(profile_Img_rounded);
        }
    }

    private void init() {
        tv_address = (TextView) findViewById(R.id.tv_address);
        cv_address = (CardView) findViewById(R.id.cv_address);
        btn_update = (Button) findViewById(R.id.btn_update);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        profile_Img = (ImageView) findViewById(R.id.profile_Img);
        profile_Img_rounded = (ImageView) findViewById(R.id.profile_Img_rounded);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        ed_user_Name = (EditText) findViewById(R.id.ed_user_Name);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_phone = (EditText) findViewById(R.id.ed_phone);

        profile_Img.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        cv_address.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        ed_user_Name.setEnabled(false);
        cv_address.setEnabled(false);
        ed_phone.setEnabled(false);
        tv_address.setEnabled(false);
        btn_update.setEnabled(false);
        profile_Img.setEnabled(false);

        btn_update.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id;
        switch (id = view.getId()){
            case R.id.iv_edit:{
                ed_user_Name.setEnabled(true);
                cv_address.setEnabled(true);
                ed_phone.setEnabled(true);
                tv_address.setEnabled(true);
                btn_update.setEnabled(true);
                profile_Img.setEnabled(true);
                iv_edit.setVisibility(View.GONE);
                btn_update.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.cv_address:{
                try {
                    cv_address.setEnabled(false);
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
                break;
            }
            case R.id.btn_update:{
                ed_user_Name.setEnabled(false);
                cv_address.setEnabled(false);
                ed_phone.setEnabled(false);
                tv_address.setEnabled(false);
                btn_update.setEnabled(false);
                profile_Img.setEnabled(false);
                iv_edit.setVisibility(View.VISIBLE);

                String name = ed_user_Name.getText().toString().trim();
                String address = tv_address.getText().toString().trim();
                String phone = ed_phone.getText().toString().trim();
                updateProfile(name,address,phone,downloadUrl);
                break;
            } case R.id.profile_Img:{
                getPermissionAndPicImage();
                break;
            }case R.id.iv_back:{
               onBackPressed();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            tv_address.setEnabled(true);
            if (resultCode == -1) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                address = place.getAddress().toString();
                double lat = latLng.latitude;
                double lng = latLng.longitude;
                tv_address.setText(address);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                profileImageBitmap = ImagePicker.getImageFromResult(ProfileActivity.this, requestCode, resultCode, data);
                if (profileImageBitmap != null)
                    profile_Img.setImageBitmap(profileImageBitmap);
                profile_Img_rounded.setImageBitmap(profileImageBitmap);

                Uri uri = getImageUri(ProfileActivity.this,profileImageBitmap);
                creatFirebaseProfilePicUrl(uri);
            }}
    }
    private void updateProfile(String name,String address,String phone,Uri profileImage){
        final UserInfoFCM userInfoFCM = app_session.getUser();
        userInfoFCM.email = app_session.getUser().email;
        userInfoFCM.firebaseToken = app_session.getUser().firebaseToken;
        userInfoFCM.uid = app_session.getUser().uid;

        if(!name.equals("")){
            userInfoFCM.name = name;
        }else  userInfoFCM.name = app_session.getUser().name;

        if(!address.equals("")){
           userInfoFCM.address  =  address;
        }else userInfoFCM.address = app_session.getUser().address;

        if(!phone.equals("")){
            userInfoFCM.phone = phone;
        }else   userInfoFCM.phone = app_session.getUser().phone;

        if(profileImage != null){
            userInfoFCM.profilePic = profileImage.toString();
        }else userInfoFCM.profilePic = app_session.getUser().profilePic;

        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).child(Uid).setValue(userInfoFCM).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Profile update uuccessfully", Toast.LENGTH_SHORT).show();
                app_session.createSession(userInfoFCM);
                }
            }
        });

    }

    public void getPermissionAndPicImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 105);
                ImagePicker.pickImage(this);
            } else {
                ImagePicker.pickImage(this);
            }
        } else {
            ImagePicker.pickImage(this);}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constant.SELECT_FILE);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(ProfileActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(ProfileActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }}
            break;}
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void creatFirebaseProfilePicUrl(Uri selectedImageUri ) {
        FirebaseApp app = FirebaseApp.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance(app);
        StorageReference storageRef = storage.getReference("chat_photos_sys" + getString(R.string.app_name));
        StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().getDownloadUrl();
                }}
        });
    }
}
