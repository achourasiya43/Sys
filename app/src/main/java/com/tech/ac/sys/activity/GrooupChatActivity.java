package com.tech.ac.sys.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.ChatAdapter;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.ChatInfo;
import com.tech.ac.sys.model.CommentInfo;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;

import java.util.ArrayList;
import java.util.Calendar;

public class GrooupChatActivity extends AppCompatActivity {

    private Session app_Session;
    private EditText ed_chatting;
    private TextView tv_send_msg;
    private ImageView iv_delete_all_chat;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatInfo> chatList;
    private RecyclerView recyclerView;
    private UserInfoFCM userInfoFCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grooup_chat);
        app_Session = new Session(this);
        userInfoFCM = app_Session.getUser();

        ed_chatting = (EditText) findViewById(R.id.ed_chatting);
        iv_delete_all_chat = (ImageView) findViewById(R.id.iv_delete_all_chat);
        tv_send_msg = (TextView) findViewById(R.id.tv_send_msg);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList,this,this);
        recyclerView.setAdapter(chatAdapter);
        getAllchat();

        tv_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chat = ed_chatting.getText().toString().trim();
                if(!chat.equals("")){
                    createChateRoom(chat);
                }

            }
        });

        iv_delete_all_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GrooupChatActivity.this);
                builder.setTitle("Alart").setMessage("Delete All Chat").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllChat();
                    }
                }).show();
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getAllchat(){
        FirebaseDatabase.getInstance().getReference().child(Constant.CHAT_ROOM).getRef().orderByKey().limitToLast(25).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatInfo chatInfo = dataSnapshot.getValue(ChatInfo.class);
                chatList.add(chatInfo);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatAdapter.notifyDataSetChanged();
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

    private void createChateRoom(String chatting){
        DatabaseReference key =  FirebaseDatabase.getInstance().getReference().child(Constant.CHAT_ROOM).push();

        ChatInfo chatInfo = new ChatInfo();
        chatInfo.chatId = key.getKey();
        chatInfo.chating = chatting ;
        chatInfo.userName = userInfoFCM.name;
        chatInfo.UserId = userInfoFCM.uid;
        chatInfo.commentsTime = Calendar.getInstance().getTime().getTime()+"";

        key.setValue(chatInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ed_chatting.setText("");
            }
        });
    }

    private void deleteAllChat(){
        FirebaseDatabase.getInstance().getReference().child(Constant.CHAT_ROOM).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatList.clear();
                    chatAdapter.notifyDataSetChanged();

                }
            }
        });
    }
}
