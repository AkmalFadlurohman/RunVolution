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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.AlForce.android.runvolution.location.LocationService;
import com.AlForce.android.runvolution.sensor.ShakeDetector;
import com.AlForce.android.runvolution.utils.DatabaseOpenHelper;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_FRAGMENT_HOME = "home";
    public static final String TAG_FRAGMENT_HISTORY = "history";
    public static final String TAG_FRAGMENT_PET = "pet";

    private FragmentManager fragmentManager;
    private DatabaseOpenHelper dbHelper;

    /* Fragments */
    private HomeFragment mHomeFragment;
    private HistoryFragment mHistoryFragment;
    private PetStatusFragment mPetFragment;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        dbHelper = new DatabaseOpenHelper(this);
        loadAllFragments();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            showFragment(TAG_FRAGMENT_HOME);
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
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
                    showFragment(TAG_FRAGMENT_HOME);
                    return true;
                case R.id.navigation_history:
                    showFragment(TAG_FRAGMENT_HISTORY);
                    return true;
                case R.id.navigation_notifications:
                    showFragment(TAG_FRAGMENT_PET);
                    return true;
            }
            return false;
        }
    };

    private void loadAllFragments() {
        fragmentManager = getSupportFragmentManager();
        mHomeFragment = new HomeFragment();
        mHomeFragment.setDbHelper(dbHelper);
        mHistoryFragment = new HistoryFragment();
        mHistoryFragment.setDbHelper(dbHelper);
        mPetFragment = new PetStatusFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, mPetFragment, TAG_FRAGMENT_PET);
        transaction.hide(mPetFragment);
        transaction.add(R.id.container, mHistoryFragment, TAG_FRAGMENT_HISTORY);
        transaction.hide(mHistoryFragment);
        transaction.add(R.id.container, mHomeFragment, TAG_FRAGMENT_HOME);
        transaction.hide(mHomeFragment);
        transaction.commitNow();
    }

    private void showFragment(String fragmentTag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fr : fragmentManager.getFragments()) {
            if (!fr.getTag().equals(fragmentTag)) {
                fragmentTransaction.hide(fr);
            }
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

}
