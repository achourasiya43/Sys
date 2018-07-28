package com.tech.ac.sys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.tech.ac.sys.adapter.ProductAdapter;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.model.AddProductInfo;
import com.tech.ac.sys.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.tech.ac.sys.activity.AddProductActivity.PROMO;
import static com.tech.ac.sys.util.Constant.SEARCH_STATE;

public class SearchFragment extends Fragment {

    private Context mContext;
    private TextView tv_no_result;
    private ProductAdapter productAdapter;
    private RecyclerView recycler_view;
    private ProgressBar progressBar;
    private ArrayList<AddProductInfo> annunuceList;
    private Session session;

    public SearchFragment() {
    }

       public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        session = new Session(mContext);

        tv_no_result = view.findViewById(R.id.tv_no_result);
        SearchView searchview = view.findViewById(R.id.searchview);

        annunuceList = new ArrayList<>();
        final String uid = session.getUser().uid;

        productAdapter = new ProductAdapter(annunuceList,this,SEARCH_STATE,null, uid);
        recycler_view = view.findViewById(R.id.recycler_view);

        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setLayoutManager(new GridLayoutManager(mContext, 2));
        recycler_view.setAdapter(productAdapter);
        progressBar = view.findViewById(R.id.spin_kit);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<AddProductInfo> infoList = searchTitle(annunuceList,newText);
                productAdapter = new ProductAdapter(infoList, SearchFragment.this,SEARCH_STATE,null, uid);
                recycler_view.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();

                if(infoList.size() == 0){
                    tv_no_result.setVisibility(View.VISIBLE);
                }else tv_no_result.setVisibility(View.GONE);

                return false;
            }
        });
        return view;
    }

    public ArrayList<AddProductInfo> searchTitle(ArrayList<AddProductInfo> annunuceList, String searchtext){
        ArrayList<AddProductInfo> tempList = new ArrayList<>();
        if(searchtext.equals("")){
            tempList.addAll(annunuceList);
            return tempList;
        }
        for(AddProductInfo annuncedInfo : annunuceList){
            if(annuncedInfo.productTitle.contains(searchtext)){
                tempList.add(annuncedInfo);
            }
        }

        return tempList;
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
        FirebaseDatabase.getInstance().getReference().child(PROMO).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressBar.setVisibility(View.GONE);
                AddProductInfo annuncedInfo = dataSnapshot.getValue(AddProductInfo.class);
                annunuceList.add(annuncedInfo);
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "FireBase User Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
