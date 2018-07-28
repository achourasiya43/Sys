package com.tech.ac.sys.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.model.CommentInfo;
import com.tech.ac.sys.util.Constant;
import com.tech.ac.sys.util.Utils;

import java.util.List;

/**
 * Created by mindiii on 16/8/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {


    private List<CommentInfo> commentList;
    private Context mContext;
    private Activity activity;
    private DatabaseReference databaseReference;
    private String uid;


    public CommentAdapter(List<CommentInfo> commentList, Context mContext, Activity activity, String uid) {
        this.commentList = commentList;
        this.mContext = mContext;
        this.activity = activity;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.uid = uid;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item_layout,parent,false);
        CommentAdapter.ViewHolder view = new CommentAdapter.ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, final int position) {
        final CommentInfo commentListInfo = commentList.get(position);

        if(!commentListInfo.profileImage.equals("") && mContext != null){

            Glide.with(mContext).load(commentListInfo.profileImage).into(holder.iv_profileImage);

        }
        if(commentListInfo.comments_by_uid.equals(uid)){
            holder.iv_delete_comments.setVisibility(View.VISIBLE);
        }else   holder.iv_delete_comments.setVisibility(View.GONE);

        holder.iv_delete_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Alart!").setMessage("Do you wants to delete this comment.").
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String commentId = commentList.get(position).commentId;
                        databaseReference.child(Constant.COMMENTS).child(commentList.get(position).productId).child(commentId).
                                setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                commentList.remove(position);
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

        holder.tv_user_name.setText(commentListInfo.userName);
        holder.tv_comments.setText(commentListInfo.comment);
        holder.tv_comments_time.setText(converteTimestamp(commentListInfo.commentsTime)+"");

        if(commentListInfo != null)
        if(commentListInfo.comments_by_uid.equals(uid)){
            holder.itemView.setLongClickable(true);
        }else  holder.itemView.setLongClickable(false);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView iv_profileImage,iv_delete_comments;
        TextView tv_user_name,tv_comments,tv_comments_time,tv_like_count;


        public ViewHolder(View itemView) {
            super(itemView);

            tv_user_name =  itemView.findViewById(R.id.tv_user_name);
            tv_comments =  itemView.findViewById(R.id.tv_comments);
            tv_comments_time =  itemView.findViewById(R.id.tv_comments_time);
            iv_profileImage = itemView.findViewById(R.id.iv_profileImage);
            iv_delete_comments = itemView.findViewById(R.id.iv_delete_comments);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {

        }
    }
    private CharSequence converteTimestamp(String mileSegundos){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
