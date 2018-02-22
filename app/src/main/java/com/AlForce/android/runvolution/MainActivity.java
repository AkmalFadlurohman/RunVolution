package com.AlForce.android.runvolution;

import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.AlForce.android.runvolution.location.LocationService;
import com.AlForce.android.runvolution.sensor.ShakeDetector;
import com.AlForce.android.runvolution.utils.DatabaseOpenHelper;
import com.AlForce.android.runvolution.utils.FragmentFactory;

public class MainActivity extends AppCompatActivity {
    Bundle bundle;

    public static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseOpenHelper dbHelper;
    private FragmentManager fragmentManager;

    /* Shake Detection Variables */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    /* Location Service Variables */
    private LocationService mLocationService;
    private float mTotalDistance;
    private Location mCurrentLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseOpenHelper(this);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();
        String userData = intent.getStringExtra("userData");
        String petData = intent.getStringExtra("petData");
        bundle = new Bundle();
        bundle.putString("userData", userData);
        bundle.putString("petData", petData);
        loadFragment(FragmentFactory.TAG_FRAGMENT_HOME, bundle);

        initializeShakeDetector();
        if (LocationService.isGooglePlayServicesAvailable(this)){
            initializeLocationService();
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(
                mShakeDetector,
                mAccelerometer,
                SensorManager.SENSOR_DELAY_UI);

        // TODO: This should be moved to start-running button implementation.
        if (mLocationService != null) {
            mLocationService.buildGoogleClient();
            if (mLocationService.isConnected()) {
                mLocationService.startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // TODO: This should be moved to stop-running button implementation.
        if (mLocationService != null) {
            if (mLocationService.isConnected()) {
                mLocationService.stopLocationUpdates();
            }
        }
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

    private void initializeLocationService() {
        mLocationService = new LocationService(this);
        mLocationService.setLocationServiceListener(
                new LocationService.LocationServiceListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (mCurrentLocation == null) {
                            mTotalDistance = 0;
                        } else {
                            mTotalDistance += mCurrentLocation.distanceTo(location);
                        }
                        mCurrentLocation = location;
                        Log.d(TAG, "onLocationChanged: " + mTotalDistance + " meters.");
                    }
                }
        );
        Log.d(TAG, "initializeLocationService: initialized.");
        Log.d(TAG, "initializeLocationService: " + mLocationService.isConnected());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(FragmentFactory.TAG_FRAGMENT_HOME, bundle);
                    return true;
                case R.id.navigation_history:
                    loadFragment(FragmentFactory.TAG_FRAGMENT_HISTORY, bundle);
                    return true;
                case R.id.navigation_notifications:
                    loadFragment(FragmentFactory.TAG_FRAGMENT_PET, bundle);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(String fragmentTag, Bundle data) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fr : fragmentManager.getFragments()) {
            if (!fr.getTag().equals(fragmentTag)) {
                fragmentTransaction.detach(fr);
            }
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragment == null) {
            fragment = FragmentFactory.createFragment(fragmentTag,data);
            fragmentTransaction.add(R.id.container, fragment, fragmentTag);
        } else {
            fragmentTransaction.attach(fragment);
        }

        fragmentTransaction.commit();
    }

}
