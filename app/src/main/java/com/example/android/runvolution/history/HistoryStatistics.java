package com.example.android.runvolution.history;

import android.util.Log;

import com.example.android.runvolution.utils.DatabaseAccessObject;
import com.example.android.runvolution.utils.DatabaseOpenHelper;
import com.example.android.runvolution.utils.DatabaseUpdateListener;

/**
 * Created by iqbal on 17/02/18.
 */

public class HistoryStatistics implements DatabaseUpdateListener {
    public static final String TAG = HistoryStatistics.class.getSimpleName();

    private int avgSteps;
    private float avgDistance;

    private HistoryDAO historyDAO;

    public HistoryStatistics(HistoryDAO historyDAO) {
        this.historyDAO = historyDAO;
    }

    public int getAvgSteps() {
        updateAvgSteps();
        return avgSteps;
    }

    private void updateAvgSteps() {
        long historyCount = historyDAO.getQueryCount();
        long stepSum = 0;
        for (int i = 0; i < historyCount; i++) {
            stepSum += historyDAO.query(i).getSteps();
        }
        avgSteps = (int) (stepSum / historyCount);
    }

    public float getAvgDistance() {
        updateAvgDistance();
        return avgDistance;
    }

    private void updateAvgDistance() {
        long historyCount = historyDAO.getQueryCount();
        float distanceSum = 0;
        for (int i = 0; i < historyCount; i++) {
            distanceSum += historyDAO.query(i).getDistance();
        }
        avgDistance = distanceSum / historyCount;
    }

    @Override
    public void onDatabaseUpdate() {
        updateAvgDistance();
        updateAvgSteps();
    }
}
