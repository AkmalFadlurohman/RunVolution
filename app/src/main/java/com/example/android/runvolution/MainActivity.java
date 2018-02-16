package com.example.android.runvolution;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_ACTIVITY =
            MainActivity.class.getSimpleName();
    public static final String TAG_FRAGMENT_HOME = "home";
    public static final String TAG_FRAGMENT_HISTORY = "history";
    public static final String TAG_FRAGMENT_STATUS = "status";

    private TextView mTextMessage;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_history:
                    mTextMessage.setText("");
                    loadHistoryFragment();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private void loadHistoryFragment() {
        Fragment historyFragment = fragmentManager.findFragmentByTag("history");

        if (historyFragment == null) {
            historyFragment = new HistoryFragment();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, historyFragment, "history")
                    .commit();
        } else {
            fragmentManager
                    .beginTransaction()
                    .attach(historyFragment)
                    .commit();
        }
    }
}
