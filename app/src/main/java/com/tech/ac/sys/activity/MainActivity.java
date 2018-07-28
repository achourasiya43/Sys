package com.tech.ac.sys.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.tech.ac.sys.R;
import com.tech.ac.sys.app_session.Session;
import com.tech.ac.sys.custom_click.GetJson;
import com.tech.ac.sys.fragments.FavouriteFragment;
import com.tech.ac.sys.fragments.HomeFragment;
import com.tech.ac.sys.fragments.NotificationFragment;
import com.tech.ac.sys.fragments.SearchFragment;
import com.tech.ac.sys.model.UserInfoFCM;
import com.tech.ac.sys.myInterface.AddReplaceFragment;
import com.tech.ac.sys.util.Constant;
import com.thefinestartist.finestwebview.FinestWebView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AddReplaceFragment,GetJson {

    private static final float END_SCALE = 0.7f;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LinearLayout contentView;
    private Fragment fr;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.app_icon);
        contentView = (LinearLayout) findViewById(R.id.content);

        fr = new HomeFragment();
        addFragment(fr,false,R.id.fragment_place);
        session = new Session(this);

        try {
                if(getIntent() != null){
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    String image = bundle.getString("notification_fragment");
                    fr = new NotificationFragment();
                    addFragment(fr,false,R.id.fragment_place);}

        } catch (Exception e) {
                e.printStackTrace();
                Log.e("getStringExtra_EX", e + "");
        }


        toolbar.setTitle("Sys Computer     ");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));

        toolbar.setNavigationIcon(new DrawerArrowDrawable(this));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (drawerLayout.isDrawerOpen(navigationView)) {
                                                         drawerLayout.closeDrawer(navigationView);
                                                     } else {
                                                         drawerLayout.openDrawer(navigationView);
                                                     }
                                                 }
                                             });

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                           @Override
                                           public void onDrawerSlide(View drawerView, float slideOffset) {
                                               //labelView.setVisibility(slideOffset > 0 ? View.VISIBLE : View.GONE);

                                               // Scale the View based on current slide offset
                                               final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                               final float offsetScale = 1 - diffScaledOffset;
                                               contentView.setScaleX(offsetScale);
                                               contentView.setScaleY(offsetScale);

                                               // Translate the View, accounting for the scaled width
                                               final float xOffset = drawerView.getWidth() * slideOffset;
                                               final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                               final float xTranslation = xOffset - xOffsetDiff;  //for left side drawer
                                               //final float xTranslation = xOffsetDiff - xOffset; //for right side drawer
                                               contentView.setTranslationX(xTranslation);
                                           }


                                           @Override
                                           public void onDrawerClosed(View drawerView) {
                                               //labelView.setVisibility(View.GONE);
                                           }
                                       }
        );

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View hView =  navigationView.getHeaderView(0);
        final TextView tv_name = (TextView)hView.findViewById(R.id.tv_name);
        final ImageView iv_header_image = hView.findViewById(R.id.iv_header_image);
        if(!session.getAdmin().equals(Constant.Admin_Email)){
            hideItem();
        }

        UserInfoFCM userInfoFCM = session.getUser();
        if(userInfoFCM != null){
            tv_name.setText(userInfoFCM.name);
           // Picasso.with(this).load(userInfoFCM.profilePic).into(iv_header_image);
        }
        navigationView.setNavigationItemSelectedListener(this);
        /*.................................firebase notification.........................,,*/

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                tmp += key + ": " + value + "\n\n";
            }
           // mTextView.setText(tmp);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                UserInfoFCM userInfoFCM = session.getUser();
                hideKeyboard(drawerView);
                if(userInfoFCM != null){
                    tv_name.setText(userInfoFCM.name);
                    if(!userInfoFCM.profilePic.equals(""))
                        Glide.with(MainActivity.this).load(userInfoFCM.profilePic).placeholder(R.drawable.user_place_holder).into(iv_header_image);
                }            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        
        
    }

    public void hideKeyboard(View view) {
        if(getCurrentFocus()!=null){
            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideItem()
    {
        navigationView = findViewById(R.id.navigation_view);
        Menu nav_Menu = navigationView.getMenu();

        nav_Menu.findItem(R.id.nav_gallery).setVisible(false);
        nav_Menu.findItem(R.id.nav_allmember).setVisible(false);
        nav_Menu.findItem(R.id.nav_send_request).setVisible(false);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;

        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_gallery) {
            intent = new Intent(MainActivity.this,AddProductActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_allmember) {
            intent = new Intent(MainActivity.this,AllUserListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send_request) {

            intent = new Intent(MainActivity.this,NotificationActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_requirement) {
            intent = new Intent(MainActivity.this,GrooupChatActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_webview) {

            String url = "http://www.systechnology.in/";
            new FinestWebView.Builder(this).show(url);

        }
        else if (id == R.id.nav_logout) {

            Session  session = new Session(this);
            session.logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fr = new HomeFragment();
                    replaceFragment(fr,false,R.id.fragment_place);
                    return true;
                case R.id.navigation_dashboard:
                    fr = new SearchFragment();
                    replaceFragment(fr,false,R.id.fragment_place);
                    return true;
                case R.id.navigation_fevourite:
                    fr = new FavouriteFragment();
                    replaceFragment(fr,false,R.id.fragment_place);
                    return true;
                case R.id.navigation_notifications:
                    fr = new NotificationFragment();
                    replaceFragment(fr,false,R.id.fragment_place);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        while(i>0){ fm.popBackStackImmediate(); i--; }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_UNSET);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void getJson(RemoteMessage remoteMessage) {
        Toast.makeText(this, remoteMessage+"", Toast.LENGTH_SHORT).show();
    }

   }