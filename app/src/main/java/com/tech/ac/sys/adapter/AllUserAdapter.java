package com.tech.ac.sys.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tech.ac.sys.R;
import com.tech.ac.sys.model.UserInfoFCM;
import java.util.List;

import static com.thefinestartist.utils.service.ServiceUtil.getSystemService;

/**
 * Created by Anil on 9/8/17.
 */

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.ViewHolder> {

    private Context mContext;
    private List<UserInfoFCM> alluserList;

    public AllUserAdapter(Context mContext, List<UserInfoFCM> alluserList) {
        this.mContext = mContext;
        this.alluserList = alluserList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_image_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvUserName.setText(alluserList.get(position).name);

        if(!alluserList.get(position).profilePic.equals(""))
        Glide.with(mContext)
                .load(alluserList.get(position).profilePic).placeholder(R.drawable.app_icon)
                .into(holder.ivUserImg);
    }

    @Override
    public int getItemCount() {
        return alluserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivUserImg;
        TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUserImg =  itemView.findViewById(R.id.iv_user_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


}
