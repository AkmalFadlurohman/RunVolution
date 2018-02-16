package com.example.android.runvolution.utils;

import android.support.v4.app.Fragment;

import com.example.android.runvolution.HistoryFragment;
import com.example.android.runvolution.HomeFragment;
import com.example.android.runvolution.PetStatusFragment;

/**
 * Created by iqbal on 16/02/18.
 */

public class FragmentFactory {

    public static final String TAG_FRAGMENT_HOME = "home";
    public static final String TAG_FRAGMENT_HISTORY = "history";
    public static final String TAG_FRAGMENT_STATUS = "status";

    public static Fragment createFragment(String fragmentTag) {
        switch (fragmentTag) {
            case TAG_FRAGMENT_HISTORY:
                return new HistoryFragment();
            case TAG_FRAGMENT_HOME:
                return new HomeFragment();
            case TAG_FRAGMENT_STATUS:
                return new PetStatusFragment();
        }

        return new Fragment();
    }
}
