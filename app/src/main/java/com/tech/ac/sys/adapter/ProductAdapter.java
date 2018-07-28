package com.tech.ac.sys.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.ac.sys.R;
import com.tech.ac.sys.activity.CommentsActivity;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.database.DBOpration;
import com.tech.ac.sys.fragments.DetailsFragment;
import com.tech.ac.sys.model.AddProductInfo;
import com.tech.ac.sys.model.LikeInfo;
import com.tech.ac.sys.util.Constant;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.tech.ac.sys.activity.AddProductActivity.PROMO;
import static com.thefinestartist.utils.service.ServiceUtil.getSystemService;

/**
 * Created by Vicky on 10-Sep-17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private  List<AddProductInfo> annunuceList;
    private  Fragment mContext;
    private int FRAGMENT_STATE;
    private Fragment fr;
    private DBOpration opration;
    private String current_uid = "";

    public ProductAdapter(List<AddProductInfo> annunuceList, Fragment mContext, int state, DBOpration opration, String uid) {
        this.annunuceList = annunuceList;
        this.mContext = mContext;
        this.FRAGMENT_STATE = state;
        this.opration = opration;
        this.current_uid = uid;
      // Toast.makeText(mContext.getContext(),  annuncedInfo.like_table.size()+"", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemViewType(int position) {
        return FRAGMENT_STATE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == Constant.HOME_STATE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item_adpter_view,parent,false);
            return new ViewHolder(v);
        }else if(viewType == Constant.SEARCH_STATE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
            return new ViewHolder(v);
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_item_adpter_view, parent, false);
            return new ViewHolder(v);
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final AddProductInfo annuncedInfo = annunuceList.get(position);
        holder.tv_title.setText(annuncedInfo.productTitle);
        holder.tv_time_stamp.setText(converteTimestamp(annuncedInfo.timeStamp));
        holder.iv_like_heart.setImageResource(R.drawable.heart_icons);

        if(FRAGMENT_STATE == Constant.SEARCH_STATE){
            holder.iv_deletePost.setVisibility(View.GONE);
        }else  holder.iv_deletePost.setVisibility(View.VISIBLE);

        holder.iv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new DetailsFragment().newInstance(annunuceList.get(position)),true,R.id.fragment_place);
            }
        });

      //  Picasso mPicasso = Picasso.with(mContext.getActivity());
      //  mPicasso.setIndicatorsEnabled(true);

        Glide.with(mContext).load(annuncedInfo.productImg).into(holder.iv_product);

      /*  mPicasso.load(annuncedInfo.productImg)
                .placeholder(R.drawable.user_place_holder).into(holder.iv_product);*/

        holder.iv_deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (FRAGMENT_STATE){
                    case 1:{
                        FirebaseDatabase.getInstance().getReference().child(PROMO).child(annuncedInfo.ProductId).
                                setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                annunuceList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;}
                    case 2:{
                        break;
                    }
                    case 3:{
                        opration.deleteContact(annuncedInfo.ProductId);
                        annunuceList.remove(position);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
        });


        if(annuncedInfo.like_table != null)
            if(annuncedInfo.like_table.containsKey(current_uid)){
                holder.iv_like_heart.setImageResource(R.drawable.liked_icon);
                Log.d("likeCount",+annunuceList.get(position).like_table.size()+"");

            }


        if(FRAGMENT_STATE == Constant.FEVOURITE_STATE){
            holder.iv_comments.setVisibility(View.GONE);
        }else holder.iv_comments.setVisibility(View.VISIBLE);

        holder.iv_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =  new Intent(mContext.getContext(), CommentsActivity.class);
                intent.putExtra("product_id",annuncedInfo.ProductId);
                mContext.startActivity(intent);
            }
        });

        holder.iv_like_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String ref =  FirebaseDatabase.getInstance()
                        .getReference().child(PROMO)
                        .child(annunuceList.get(position).ProductId)
                        .child(Constant.Like_Table)
                        .child(current_uid).getKey();


                FirebaseDatabase.getInstance()
                        .getReference().child(PROMO)
                        .child(annunuceList.get(position).ProductId)
                        .child(Constant.Like_Table)
                        .child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LikeInfo  likeInfo = dataSnapshot.getValue(LikeInfo.class);
                        if(likeInfo != null){

                            if(ref.equals(likeInfo.likeby_uid)){
                                unLike_Method(position,holder);
                            }
                        }else{
                            Like_method(position,holder);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }


        });
    }

    private void Like_method(final int position, final ViewHolder holder) {
        final LikeInfo likeInfo = new LikeInfo();
        likeInfo.likeby_uid = current_uid;
        likeInfo.likeCount = 1;
        likeInfo.likeStatus = true;

        FirebaseDatabase.getInstance()
                .getReference().child(PROMO)
                .child(annunuceList.get(position).ProductId)
                .child(Constant.Like_Table)
                .child(current_uid)
                .setValue(likeInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    if(annunuceList.get(position).like_table != null){

                        annunuceList.get(position).like_table.clear();
                        annunuceList.get(position).like_table.put(current_uid,likeInfo);
                        holder.iv_like_heart.setImageResource(R.drawable.liked_icon);
                        notifyItemChanged(position);
                        Toast.makeText(mContext.getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    }else Toast.makeText(mContext.getContext(), "Something went wrong please try again later", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void unLike_Method(final int position, final ViewHolder holder) {
        FirebaseDatabase.getInstance()
                .getReference().child(PROMO)
                .child(annunuceList.get(position).ProductId)
                .child(Constant.Like_Table)
                .child(current_uid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    if(annunuceList.get(position).like_table != null){
                        annunuceList.get(position).like_table.remove(current_uid);
                        holder.iv_like_heart.setImageResource(R.drawable.holo_like);
                        notifyItemChanged(position);
                        Toast.makeText(mContext.getContext(), "UnLiked", Toast.LENGTH_SHORT).show();
                    }
                    else  Toast.makeText(mContext.getContext(), "Something went wrong please try again later", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return annunuceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title,tv_time_stamp;
        ImageView iv_product,iv_deletePost,iv_comments,iv_like_heart;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_time_stamp = itemView.findViewById(R.id.tv_time_stamp);
            iv_product = itemView.findViewById(R.id.iv_product);
            iv_deletePost = itemView.findViewById(R.id.iv_deletePost);
            iv_comments = itemView.findViewById(R.id.iv_comments);
            iv_like_heart = itemView.findViewById(R.id.iv_like_heart);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = mContext.getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
            transaction.add(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }
    private CharSequence converteTimestamp(String mileSegundos){
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }
}
