package com.tech.ac.sys.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.AllUserAdapter;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.util.Constant;

import java.util.ArrayList;
import java.util.Iterator;

public class AllUserListActivity extends AppCompatActivity {

    private ArrayList<UserInfoFCM> users ;
    private AllUserAdapter  allUserAdapter;
    private RecyclerView rv_all_user;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user_list);

        users = new ArrayList<>();
        allUserAdapter = new AllUserAdapter(this,users);
        rv_all_user = (RecyclerView) findViewById(R.id.rv_all_user);
        rv_all_user.setAdapter(allUserAdapter);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        getAllUsersFromFirebase();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getAllUsersFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child(Constant.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                users.clear();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    UserInfoFCM user = dataSnapshotChild.getValue(UserInfoFCM.class);
                    if(user != null)
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
                allUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AllUserListActivity.this, "FireBase User Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
