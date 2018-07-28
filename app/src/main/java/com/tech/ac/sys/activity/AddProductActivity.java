package com.tech.ac.sys.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.fcm.SendNotification;
import com.tech.ac.sys.model.AddProductInfo;
import com.tech.ac.sys.model.LikeInfo;
import com.tech.ac.sys.util.Constant;
import com.tech.ac.sys.util.MySnackBar;
import com.tech.ac.sys.util.Utils;
import com.tech.ac.sys.util.Validation;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddProductActivity extends AppCompatActivity {

    ImageView iv_product;
    EditText ed_title,ed_descriton,ed_details;
    Button btn_send;

    ImageView product_img;
    public static String PROMO = "promo";

    private DatabaseReference mFirebaseDatabaseReference;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseApp app;
    private Uri image_FirebaseURL;
    private ProgressBar progressBar;
    private Session app_session;
    LikeInfo  likeInfo;
    Bitmap profileImageBitmap;
    SendNotification sendNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        iv_product = (ImageView) findViewById(R.id.iv_product);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_descriton = (EditText) findViewById(R.id.ed_descriton);
        ed_details = (EditText) findViewById(R.id.ed_details);
        btn_send = (Button) findViewById(R.id.btn_send);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        app_session = new Session(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final AddProductActivity addProductActivity = new AddProductActivity();

        sendNotification = new SendNotification();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = ed_title.getText().toString().trim();
                String Desctiption = ed_descriton.getText().toString().trim();
                String Details = ed_details.getText().toString().trim();
                long timStamp = Calendar.getInstance().getTime().getTime();

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if(Utils.isConnectingToInternet(AddProductActivity.this)){
                        if(isValidField(view)) {
                            sendData(uid,Title,Desctiption,Details,timStamp+"");
                        }

                    }else Utils.defaultDialog(AddProductActivity.this);

            }
        });
        iv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionAndPicImage();
            }
        });

    }
    public void sendData(String uid, String title, String desctiption, String details, String timStamp){


        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference key = mFirebaseDatabaseReference.child(PROMO).push();

        AddProductInfo addProductInfo = new AddProductInfo();

        addProductInfo.UId = uid;
        addProductInfo.ProductId = key.getKey();
        addProductInfo.productImg = ""+image_FirebaseURL;
        addProductInfo.productTitle = title;
        addProductInfo.productDescription = desctiption;
        addProductInfo.productFullDetails = details;
        addProductInfo.timeStamp = timStamp;
       // addProductInfo.likeInfo = likeInfo;


        key.setValue(addProductInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                sendNotification.sendWithOtherThread("topic","New Product Added","By Sys the computer shop",image_FirebaseURL);
                finish();
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
                    Toast.makeText(AddProductActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(AddProductActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }}
            break;}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                profileImageBitmap = ImagePicker.getImageFromResult(AddProductActivity.this, requestCode, resultCode, data);
                if (profileImageBitmap != null)
                    iv_product.setImageBitmap(profileImageBitmap);

                Uri uri = getImageUri(AddProductActivity.this,profileImageBitmap);
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
                    image_FirebaseURL = task.getResult().getDownloadUrl();
                }}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean isValidField(View view) {
        Validation v = new Validation(this);
        if(profileImageBitmap == null){
            Snackbar snackbar = Snackbar.make(view, "Please select product image", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        else if (!v.isNullValue(ed_title)) {
            Snackbar snackbar = Snackbar.make(view, "Please enter title", Snackbar.LENGTH_LONG);
            snackbar.show();
            ed_title.requestFocus();
            return false;
        }
        else if (!v.isNullValue(ed_descriton)) {
            Snackbar snackbar = Snackbar.make(view, "Please enter description", Snackbar.LENGTH_LONG);
            snackbar.show();
            ed_descriton.requestFocus();
            return false;
        }
        else if (!v.isNullValue(ed_details)) {
            Snackbar snackbar = Snackbar.make(view, "Please enter details", Snackbar.LENGTH_LONG);
            snackbar.show();
            ed_details.requestFocus();
            return false;
        }
        return true;
    }
}
