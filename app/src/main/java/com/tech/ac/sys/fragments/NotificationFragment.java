package com.tech.ac.sys.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.NotificationAdapter;
import com.tech.ac.sys.model.NotifyInfo;

import java.util.ArrayList;
import java.util.Iterator;

import static com.tech.ac.sys.activity.NotificationActivity.NOTIFICATION;


public class NotificationFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private Context mContext;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotifyInfo> notificationList;
    private ProgressBar progressBar;

    public NotificationFragment() {

    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView = view.findViewById(R.id.rv_all_notification);
        progressBar = view.findViewById(R.id.progressBar);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList,mContext);
        recyclerView.setAdapter(notificationAdapter);
        getAllNotification();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void getAllNotification(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child(NOTIFICATION).getRef().orderByKey().
                limitToLast(25).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                NotifyInfo notifyInfo = dataSnapshot.getValue(NotifyInfo.class);
                notificationList.add(notifyInfo);

                notificationAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(notificationList.size() -1);
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
