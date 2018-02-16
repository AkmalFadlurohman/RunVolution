package com.example.android.runvolution.utils;

import android.support.v4.app.Fragment;

import com.example.android.runvolution.HistoryFragment;

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
        }

        return new Fragment();
    }
}
