package com.tech.ac.sys.util;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

/**
 * Created by Vicky on 19-Jun-17.
 */

public class MySnackBar {
  public static CoordinatorLayout coordinatorLayout;
    public static void show(String msg){
        if(coordinatorLayout != null){
            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).setAction("ok",null).show();

        }
    }


}
