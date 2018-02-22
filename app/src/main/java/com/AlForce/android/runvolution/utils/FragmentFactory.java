package com.AlForce.android.runvolution.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.AlForce.android.runvolution.HistoryFragment;
import com.AlForce.android.runvolution.HomeFragment;
import com.AlForce.android.runvolution.PetStatusFragment;
/**
 * Created by iqbal on 16/02/18.
 */

public class FragmentFactory {

    public static final String TAG_FRAGMENT_HOME = "home";
    public static final String TAG_FRAGMENT_HISTORY = "history";
    public static final String TAG_FRAGMENT_PET = "pet";

    public static Fragment createFragment(String fragmentTag) {
        switch (fragmentTag) {
            case TAG_FRAGMENT_HISTORY:
                return new HistoryFragment();
            case TAG_FRAGMENT_HOME:
                return new HomeFragment();
            case TAG_FRAGMENT_PET:
                return new PetStatusFragment();
        }

        return new Fragment();
    }
}
