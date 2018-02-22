package com.AlForce.android.runvolution;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.AlForce.android.runvolution.history.HistoryDAO;
import com.AlForce.android.runvolution.history.HistoryItem;
import com.AlForce.android.runvolution.history.HistoryStatistics;
import com.AlForce.android.runvolution.timer.Timer;
import com.AlForce.android.runvolution.utils.DatabaseOpenHelper;
import com.AlForce.android.runvolution.utils.DatabaseUpdateListener;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
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
    }

    private void initializeHistoryAccess() {
        historyDAO = new HistoryDAO(dbHelper);
        statistics = new HistoryStatistics(historyDAO);
        updateListener = new DatabaseUpdateListener() {
            @Override
            public void onDatabaseUpdate() {
                totalDistance = statistics.getTotalDistance();
                totalDistanceTextView.setText(Float.toString(totalDistance));
            }
        };
        historyDAO.setListener(updateListener);

        totalDistance = statistics.getTotalDistance();
        totalDistanceTextView.setText(Float.toString(totalDistance));
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
        record.setDate(new Date());
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
}
