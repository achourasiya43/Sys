package com.tech.ac.sys.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tech.ac.sys.R;
import com.tech.ac.sys.database.DBOpration;
import com.tech.ac.sys.model.AddProductInfo;

import java.net.UnknownHostException;


public class DetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private AddProductInfo annuncedInfo;
    private ImageView iv_product;
    private Context mContext;
    private Toolbar toolbar;
    FloatingActionButton fbtn_save;
    private  DBOpration dbOpration;
    private TextView tv_title;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(AddProductInfo annuncedInfo) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, annuncedInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            annuncedInfo = (AddProductInfo) getArguments().getSerializable(ARG_PARAM1);
        }
        dbOpration = new DBOpration(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        fbtn_save = view.findViewById(R.id.btn_save);
        iv_product = view.findViewById(R.id.iv_product);
        tv_title = view.findViewById(R.id.tv_title);

        final ProgressBar progressBar = view.findViewById(R.id.spin_kit);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        Glide.with(mContext)
                .load(annuncedInfo.productImg)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if(e instanceof UnknownHostException)
                            progressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(iv_product);
       // Glide.with(mContext).load(annuncedInfo.productImg).placeholder(R.drawable.app_icon).into(iv_product);
        tv_title.setText(annuncedInfo.productTitle);

        fbtn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(annuncedInfo);
                dbOpration.insertJson(annuncedInfo,json);

            }
        });

        iv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full_screen_photo_dialog();

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;

    }

    public void full_screen_photo_dialog(){
        Dialog openDialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.full_image_view_dialog);
        openDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        PhotoView photoView = openDialog.findViewById(R.id.photo_view);
        Glide.with(mContext).load(annuncedInfo.productImg).into(photoView);

        openDialog.show();

    }



}
