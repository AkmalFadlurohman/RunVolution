package com.example.android.runvolution.history;

import com.example.android.runvolution.utils.DatabaseOpenHelper;

/**
 * Created by iqbal on 17/02/18.
 */

public class HistoryStatistics {
    private int avgSteps;
    private float avgDistance;

    private DatabaseOpenHelper mDB;

    public HistoryStatistics(DatabaseOpenHelper mDB) {
        this.mDB = mDB;
    }

    public int getAvgSteps() {
        updateAvgSteps();
        return avgSteps;
    }

    private void updateAvgSteps() {
        long historyCount = mDB.getHistoryCount();
        long stepSum = 0;
        for (int i = 0; i < historyCount; i++) {
            stepSum += mDB.query(i).getSteps();
        }
        avgSteps = (int) (stepSum / historyCount);
    }

    public float getAvgDistance() {
        updateAvgDistance();
        return avgDistance;
    }

    private void updateAvgDistance() {
        long historyCount = mDB.getHistoryCount();
        float distanceSum = 0;
        for (int i = 0; i < historyCount; i++) {
            distanceSum += mDB.query(i).getDistance();
        }
        avgDistance = distanceSum / historyCount;
    }
}
