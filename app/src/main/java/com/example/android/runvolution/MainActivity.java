package com.example.android.runvolution;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.runvolution.sensor.ShakeDetector;
import com.example.android.runvolution.utils.DatabaseOpenHelper;
import com.example.android.runvolution.utils.FragmentFactory;


public class MainActivity extends AppCompatActivity {

    public static final String TAG =
            MainActivity.class.getSimpleName();
    public static final String TAB_HOME = FragmentFactory.TAG_FRAGMENT_HOME;
    public static final String TAB_HISTORY = FragmentFactory.TAG_FRAGMENT_HISTORY;
    public static final String TAB_STATUS = FragmentFactory.TAG_FRAGMENT_STATUS;
    public static final String[] TABS = {
            TAB_HOME, TAB_HISTORY, TAB_STATUS
    };

    private DatabaseOpenHelper dbHelper;
    private FragmentManager fragmentManager;

    /* Shake Detection Variables*/
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseOpenHelper(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        loadFragment(TAB_HOME);

        initializeShakeDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(
                mShakeDetector,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

    private void initializeShakeDetector() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        ShakeDetector.OnShakeListener shakeListener = createShakeListener();
        mShakeDetector.setOnShakeListener(shakeListener);
    }

    private ShakeDetector.OnShakeListener createShakeListener() {
        ShakeDetector.OnShakeListener listener =
                new ShakeDetector.OnShakeListener() {
                    @Override
                    public void onShake(int count) {
                        handleShakeEvent(count);
                    }

                    private void handleShakeEvent(int count) {
                        moveTaskToBack(true);
                    }
                };

        return listener;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(TAB_HOME);
                    return true;
                case R.id.navigation_history:
                    loadFragment(TAB_HISTORY);
                    return true;
                case R.id.navigation_notifications:
                    loadFragment(TAB_STATUS);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(String fragmentTag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fr : fragmentManager.getFragments()) {
            if (!fr.getTag().equals(fragmentTag)) {
                fragmentTransaction.detach(fr);
            }
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragment == null) {
            fragment = FragmentFactory.createFragment(fragmentTag);
            fragmentTransaction.add(R.id.container, fragment, fragmentTag);
        } else {
            fragmentTransaction.attach(fragment);
        }

        fragmentTransaction.commit();
    }

}
