package com.tech.ac.sys.myInterface;

import android.support.v4.app.Fragment;

/**
 * Created by Vicky on 10-Sep-17.
 */

public interface AddReplaceFragment {
    void addFragment(Fragment fragment, boolean addToBackStack, int containerId);
    void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId);
}
