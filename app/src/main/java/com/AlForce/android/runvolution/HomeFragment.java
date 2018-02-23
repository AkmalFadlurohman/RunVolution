package com.AlForce.android.runvolution;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.AlForce.android.runvolution.history.HistoryDAO;
import com.AlForce.android.runvolution.history.HistoryItem;
import com.AlForce.android.runvolution.history.HistoryStatistics;
import com.AlForce.android.runvolution.timer.Timer;
import com.AlForce.android.runvolution.utils.DatabaseOpenHelper;
import com.AlForce.android.runvolution.utils.DatabaseUpdateListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private TextView nameView;
    private TextView welcomeView;
    public Button timerButton;
    public TextView timerTextView;
    private TextView distanceTextView;
    private TextView stepTextView;
    private TextView totalDistanceTextView;
    private Timer timer;

    private DatabaseOpenHelper dbHelper;
    private DatabaseUpdateListener updateListener;
    private HistoryStatistics statistics;
    private HistoryDAO historyDAO;

    private float totalDistance;
    private float currentDistance;
    private int currentSteps;

    public HomeFragment() {
        // Required empty public constructor
    }

    public void setDbHelper(DatabaseOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollView scrollView = (ScrollView) getView().findViewById(R.id.home_scroll_view);
        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) + 10, getResources().getDisplayMetrics());
        scrollView.setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);
        welcomeView = (TextView) getView().findViewById(R.id.welcomeMessage);
        nameView = (TextView) getView().findViewById(R.id.welcomeMessage_user);
        timerButton = (Button) getView().findViewById(R.id.timerButton);
        timerTextView = (TextView) getView().findViewById(R.id.timerView);
        distanceTextView = (TextView) getView().findViewById(R.id.distanceView);
        stepTextView = (TextView) getView().findViewById(R.id.stepCounterView);
        totalDistanceTextView = (TextView) getView().findViewById(R.id.totalDistanceView);

        initializeHistoryAccess();

        timer = new Timer(timerTextView);
        timerButton.setText("START");
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (button.getText().equals("STOP")) {
                    startRecording();
                    button.setText("START");
                } else {
                    stopRecording();
                    button.setText("STOP");
                }
            }
        });
        SharedPreferences preferences = this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
        String name = preferences.getString("name",null);
        if (name != null) {
            if(name.contains(" ")) {
                String nickName = name.substring(0,name.indexOf(" "));
                nameView.setText(nickName);
            }
        }
    }

    private void initializeHistoryAccess() {
        historyDAO = new HistoryDAO(dbHelper);
        statistics = new HistoryStatistics(historyDAO);
        updateListener = new DatabaseUpdateListener() {
            @Override
            public void onDatabaseUpdate() {
                totalDistance = statistics.getTotalDistance();
                totalDistanceTextView.setText(Float.toString(totalDistance));
                SharedPreferences preferences = HomeFragment.this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
                String email = preferences.getString("email",null);
                //new RecordUpdaterTask(email,totalDistance).execute((Void) null)
            }
        };
        historyDAO.setListener(updateListener);

        totalDistance = statistics.getTotalDistance();
        totalDistanceTextView.setText(Float.toString(totalDistance) + " m");
    }

    private void startRecording() {
        currentDistance = 0;
        currentSteps = 0;
        timer.timerHandler.removeCallbacks(timer.timerRunnable);
    }

    private void stopRecording() {
        timer.startTime = System.currentTimeMillis();
        timer.timerHandler.postDelayed(timer.timerRunnable, 0);
        saveCurrentRecord();
    }

    private void saveCurrentRecord() {
        HistoryItem record = new HistoryItem();
        record.setDate(new Date(new Date().getTime()));
        record.setDistance(currentDistance);
        record.setSteps(currentSteps);
        long newId = historyDAO.insert(record);
        Log.d(TAG, "saveCurrentRecord: Created item with id="+newId);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.timerHandler.removeCallbacks(timer.timerRunnable);
        Button button = (Button) getView().findViewById(R.id.timerButton);
        button.setText("START");
    }

    public static class RecordUpdaterTask extends AsyncTask<Void, Void, Boolean> {
        @NonNull
        private final String email;
        private final float record;

        RecordUpdaterTask(String email, float record) {
            this.email = email;
            this.record = record;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String address = "https://runvolution.herokuapp.com/updaterecord";
            String param = "?email=" + email + "&record=" + record;
            HttpsURLConnection httpsPatch = null;
            BufferedReader reader = null;
            String msg = null;

            try {
                URL urlAddress = new URL(address + param);
                httpsPatch = (HttpsURLConnection) urlAddress.openConnection();
                httpsPatch.setRequestMethod("PATCH");
                httpsPatch.connect();
                reader = new BufferedReader(new InputStreamReader(httpsPatch.getInputStream()));
                String inputLine;
                StringBuilder buffer = new StringBuilder();
                int respCode = httpsPatch.getResponseCode();
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine);
                }
                msg = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (httpsPatch != null) {
                    httpsPatch.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (msg == null) {
                    Log.d("The server responded with : ", "Message is null");
                } else {
                    Log.d("The server responded with : ", msg);
                }

            }
            if (msg != null) {
                return "OK".equals(msg);
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "Updated user record on server database");
                //Toast.makeText(HomeFragment.this.getActivity().getApplicationContext(), "Congratulations, You just achieved a new record", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG,"Failed to update record");
                //Toast.makeText(HomeFragment.this.getActivity().getApplicationContext(), "Update record failed", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            //Empty
        }
    }
}
