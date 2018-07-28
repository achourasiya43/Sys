package com.tech.ac.sys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.ProductAdapter;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.AddProductInfo;
import com.tech.ac.sys.model.LikeInfo;
import com.tech.ac.sys.util.Utils;

import java.util.ArrayList;

import static com.tech.ac.sys.activity.AddProductActivity.PROMO;
import static com.tech.ac.sys.util.Constant.HOME_STATE;


public class HomeFragment extends Fragment {


    private ProductAdapter productAdapter;
    private RecyclerView recycler_view;
    private ProgressBar progressBar;
    private Context mContext;
    private ArrayList<AddProductInfo> annunuceList;
    private Session session;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        annunuceList = new ArrayList<>();
        String uid = session.getUser().uid;
        productAdapter = new ProductAdapter(annunuceList,this,HOME_STATE,null,uid);
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setAdapter(productAdapter);
        progressBar = view.findViewById(R.id.spin_kit);
        return view;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Utils.isConnectingToInternet(mContext)){
            getAllProduct();
        }else Utils.defaultDialog(mContext);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void getAllProduct() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child(PROMO).orderByKey().limitToLast(25).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                AddProductInfo annuncedInfo = dataSnapshot.getValue(AddProductInfo.class);
                annunuceList.add(annuncedInfo);
                recycler_view.smoothScrollToPosition(annunuceList.size()-1);
                productAdapter.notifyDataSetChanged();
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
