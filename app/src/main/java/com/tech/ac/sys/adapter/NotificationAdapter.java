package com.tech.ac.sys.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.model.NotifyInfo;
import java.util.List;

import static com.tech.ac.sys.activity.NotificationActivity.NOTIFICATION;

/**
 * Created by Vicky on 23-Sep-17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private  List<NotifyInfo> notificationList;
    public NotificationAdapter(List<NotifyInfo> notificationList, Context mContext) {
        this.notificationList = notificationList;
        this.mContext = mContext;
    }
    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final NotifyInfo notifyInfo = notificationList.get(position);
        holder.tv_title.setText(notifyInfo.title);
        holder.tv_description.setText(notifyInfo.description+"");

        holder.tv_date.setText(converteTimestamp(notifyInfo.timestamp));
        holder.tv_serial_number.setText(position+1+"");
        Glide.with(mContext).load(notifyInfo.image_FirebaseURL).placeholder(R.drawable.user_place_holder).into(holder.iv_product);

        if(notificationList.size() == (position+1)){
            holder.view_connector_line.setVisibility(View.GONE);
        }else holder.view_connector_line.setVisibility(View.VISIBLE);

        holder.iv_delete_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child(NOTIFICATION).child(notifyInfo.NotificationId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        notificationList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView iv_product,iv_delete_notification;
        View view_connector_line;
        TextView tv_date,tv_title,tv_description,tv_serial_number;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_product = itemView.findViewById(R.id.iv_product);
            iv_delete_notification = itemView.findViewById(R.id.iv_delete_notification);
            view_connector_line = itemView.findViewById(R.id.view_connector_line);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_serial_number = itemView.findViewById(R.id.tv_serial_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            full_screen_photo_dialog(notificationList.get(getAdapterPosition()).image_FirebaseURL);
        }
    }

    private CharSequence converteTimestamp(String mileSegundos){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public void full_screen_photo_dialog(String image_url){
        Dialog openDialog = new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        openDialog.setContentView(R.layout.full_image_view_dialog);

        PhotoView photoView = openDialog.findViewById(R.id.photo_view);
        Glide.with(mContext).load(image_url).into(photoView);

        openDialog.show();

    }
}
