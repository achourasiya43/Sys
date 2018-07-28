package com.tech.ac.sys.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tech.ac.sys.R;
import com.tech.ac.sys.adapter.ProductAdapter;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.database.DBOpration;

import static com.tech.ac.sys.util.Constant.FEVOURITE_STATE;

public class FavouriteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private DBOpration opration;
    private Context mContext;
    private ProductAdapter productAdapter;
    private RecyclerView recycler_view;
    private ProgressBar progressBar;
    private Session session;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance(String param1, String param2) {
        FavouriteFragment fragment = new FavouriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        opration = new DBOpration(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        session = new Session(mContext);
        String uid = session.getUser().uid;
        productAdapter = new ProductAdapter(opration.getAllHashMap(),this,FEVOURITE_STATE,opration, uid);
        recycler_view = view.findViewById(R.id.recycler_view);
        recycler_view.setAdapter(productAdapter);
        progressBar = (ProgressBar)view.findViewById(R.id.spin_kit);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

}
