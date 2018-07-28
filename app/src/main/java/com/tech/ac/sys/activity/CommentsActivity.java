package com.tech.ac.sys.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.CommentAdapter;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.CommentInfo;
import com.tech.ac.sys.model.UserInfoFCM;

import java.util.ArrayList;
import java.util.Calendar;

import static com.tech.ac.sys.util.Constant.COMMENTS;

public class CommentsActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private Button btn_post_comments;
    private EditText ed_comments;
    private Context mContext;
    private ArrayList<CommentInfo> commentList;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerView;
    private String product_id ="";
    private FirebaseAuth mFirebaseAuth;
    private Session app_Session;
    private ImageView iv_back;
    private UserInfoFCM userInfoFCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        if(getIntent() != null){
            product_id = getIntent().getStringExtra("product_id");
            app_Session = new Session(this);
            userInfoFCM = app_Session.getUser();
        }


        getAllCommnets();
        commentList = new ArrayList<>();
        btn_post_comments = (Button) findViewById(R.id.btn_post_comments);
        ed_comments = (EditText) findViewById(R.id.ed_comments);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        iv_back = (ImageView) findViewById(R.id.iv_back);

        commentAdapter = new CommentAdapter(commentList,this,this,userInfoFCM.uid);
        recyclerView.setAdapter(commentAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        btn_post_comments.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String comments = ed_comments.getText().toString().trim();
                if(!comments.equals("")){
                    post_commnets(comments);
                }

            }

            private void post_commnets(String comments) {
                DatabaseReference key = databaseReference.child(COMMENTS).child(product_id).push();
                CommentInfo commentInfo = new CommentInfo();
                commentInfo.productId = product_id;
                commentInfo.commentId = key.getKey();
                commentInfo.comment = comments ;
                commentInfo.userName = userInfoFCM.name;
                commentInfo.profileImage = userInfoFCM.profilePic;
                commentInfo.commentsTime = Calendar.getInstance().getTime().getTime()+"";
                commentInfo.comments_by_uid = userInfoFCM.uid;

                key.setValue(commentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ed_comments.setText("");
                    }
                });
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void getAllCommnets(){
        FirebaseDatabase.getInstance().getReference().child(COMMENTS).child(product_id).getRef().orderByKey().limitToLast(25).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CommentInfo  commentInfo = dataSnapshot.getValue(CommentInfo.class);
                commentList.add(commentInfo);

                commentAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(commentList.size() - 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
