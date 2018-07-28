package com.tech.ac.sys.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.model.ChatInfo;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vicky on 28-Sep-17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    
    private List<ChatInfo> chatList;
    private Context mContext;
    private Activity activity;
    private String uid = "";
    private HashMap<String,UserInfoFCM> infoFCMHashMap;

    public ChatAdapter(List<ChatInfo> chatList, Context mContext, Activity activity) {
        this.chatList = chatList;
        this.mContext = mContext;
        this.activity = activity;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        infoFCMHashMap = new HashMap<>();

        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserInfoFCM userInfoFCM = dataSnapshot.getValue(UserInfoFCM.class);
                infoFCMHashMap.put(userInfoFCM.uid,userInfoFCM);
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

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, final int position) {
        final ChatInfo chatInfo = chatList.get(position);

        if(infoFCMHashMap.containsKey(chatInfo.UserId)){
            UserInfoFCM userInfoFCM = infoFCMHashMap.get(chatInfo.UserId);
            Glide.with(mContext).load(userInfoFCM.profilePic).into(holder.iv_profileImage);
        }
        if (chatInfo.UserId.equals(uid)) {
            holder.iv_delete_chat.setVisibility(View.VISIBLE);

        }else  holder.iv_delete_chat.setVisibility(View.GONE);

        holder.tv_user_name.setText(chatInfo.userName);
        holder.tv_chating.setText(chatInfo.chating);
        holder.tv_chating_time.setText(converteTimestamp(chatInfo.commentsTime)+"");

        holder.iv_delete_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Alart!").setMessage("Do you want to delete this Chat").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String chatId = chatList.get(position).chatId;

                        FirebaseDatabase.getInstance().getReference().child(Constant.CHAT_ROOM).child(chatId).
                                setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                chatList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.setCancelable(true);
                    }
                }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView iv_profileImage,iv_delete_chat;
        TextView tv_user_name,tv_chating,tv_chating_time;
        
        public ViewHolder(View itemView) {
            super(itemView);

            tv_user_name =  itemView.findViewById(R.id.tv_user_name);
            tv_chating =  itemView.findViewById(R.id.tv_chating);
            tv_chating_time =  itemView.findViewById(R.id.tv_chating_time);
            iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
            iv_delete_chat = itemView.findViewById(R.id.iv_delete_chat);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            return true;
        }
    }
    private CharSequence converteTimestamp(String mileSegundos){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
