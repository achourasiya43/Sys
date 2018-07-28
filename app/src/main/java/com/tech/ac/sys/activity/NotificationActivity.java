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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;
import com.tech.ac.sys.R;
import com.tech.ac.sys.fcm.SendNotification;
import com.tech.ac.sys.model.NotifyInfo;
import com.tech.ac.sys.util.Constant;
import com.tech.ac.sys.util.Utils;
import com.tech.ac.sys.util.Validation;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {
    private ImageView product_img,iv_back;
    private EditText ed_title,ed_description;
    private Button btn_notify;
    private Bitmap profileImageBitmap;
    SendNotification sendNotification;

    public static final String AUTH_KEY = "key=AAAAZmErjLU:APA91bEv3XmGY3vlJczfZAyfVr3OxhXGhKGDFqnGyKR2uRHrQNywMjCdBh6OSdrnznvt52BbWglPActv3_5tLJvUmzhyEwxkHejGQX_9htv6S74yR532lHw2MuvDywKVJTCioRM1UPxT";

    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseApp app;
    private Uri image_FirebaseURL;
    private DatabaseReference mFirebaseDatabaseReference;
    public static String NOTIFICATION = "notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationpro);
        product_img = (ImageView) findViewById(R.id.product_img);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_description = (EditText) findViewById(R.id.ed_description);
        btn_notify = (Button) findViewById(R.id.btn_notify);

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        sendNotification = new SendNotification();

        product_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionAndPicImage();
            }
        });

        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ed_title.getText().toString().trim();
                String description = ed_description.getText().toString().trim();

                if(Utils.isConnectingToInternet(NotificationActivity.this)){

                    if(isValidField(view)){
                        send_notifiaction(image_FirebaseURL,title,description);
                        sendNotification.sendWithOtherThread("topic",title,description,image_FirebaseURL);
                    }

                }else Utils.defaultDialog(NotificationActivity.this);


            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void send_notifiaction(Uri image_FirebaseURL,String title,String description){

        DatabaseReference key = mFirebaseDatabaseReference.child(NOTIFICATION).push();

        NotifyInfo notifyInfo  = new NotifyInfo();
        notifyInfo.NotificationId = key.getKey();
        notifyInfo.image_FirebaseURL = ""+image_FirebaseURL;
        notifyInfo.title = title;
        notifyInfo.description = description;
        notifyInfo.timestamp = Calendar.getInstance().getTime().getTime()+"";

        key.setValue(notifyInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                finish();
            }
        });
    }

   /* private void sendWithOtherThread(final String type, final String title, final String description, final Uri image_FirebaseURL) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(type,title,description,image_FirebaseURL);
            }
        }).start();
    }

    private void pushNotification(String type,final String title,final String description,Uri image_FirebaseURL) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", title);
            jNotification.put("body", description);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_launcher");

            Uri uri;

            String stringUri = image_FirebaseURL.toString();

            jData.put("picture_url", stringUri);

            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
                final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                    // mTextView.setText(resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
*/
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
                    Toast.makeText(NotificationActivity.this, "YOU DENIED PERMISSION CANNOT SELECT IMAGE", Toast.LENGTH_LONG).show();
                }
            }
            break;

            case Constant.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, Constant.REQUEST_CAMERA);
                    getPermissionAndPicImage();
                } else {
                    Toast.makeText(NotificationActivity.this, "YOUR  PERMISSION DENIED ", Toast.LENGTH_LONG).show();
                }}
            break;}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 234) {
                profileImageBitmap = ImagePicker.getImageFromResult(NotificationActivity.this, requestCode, resultCode, data);
                if (profileImageBitmap != null)
                    product_img.setImageBitmap(profileImageBitmap);

                Uri uri = getImageUri(NotificationActivity.this,profileImageBitmap);
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
        else if (!v.isNullValue(ed_description)) {
            Snackbar snackbar = Snackbar.make(view, "Please enter description", Snackbar.LENGTH_LONG);
            snackbar.show();
            ed_description.requestFocus();
            return false;
        }
        return true;
    }
}
