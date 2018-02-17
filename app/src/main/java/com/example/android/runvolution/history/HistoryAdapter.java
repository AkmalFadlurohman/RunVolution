package com.example.android.runvolution.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.runvolution.MainActivity;
import com.example.android.runvolution.R;
import com.example.android.runvolution.utils.DatabaseOpenHelper;

import java.io.DataOutput;
import java.util.List;

/**
 * Created by iqbal on 16/02/18.
 */

public class HistoryAdapter extends Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyItems;
    private DatabaseOpenHelper dbHelper;
    private Context context;

    public HistoryAdapter(List<HistoryItem> historyItems, Context context) {
        this.historyItems = historyItems;
        this.context = context;
    }

    public HistoryAdapter(Context context, DatabaseOpenHelper db) {
        this.context = context;
        this.dbHelper = db;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View historyItemView = LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(historyItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryItem currentItem;
        if (dbHelper == null) {
            currentItem = historyItems.get(position);
        } else {
            currentItem = dbHelper.query(position);
        }

        String date = context.getString(R.string.date) + currentItem.getDate().toString();
        String steps = context.getString(R.string.steps) + Integer.toString(currentItem.getSteps());
        String distance = context.getString(R.string.distance) + Float.toString(currentItem.getDistance());
        holder.dateTextView.setText(date);
        holder.stepsTextView.setText(steps);
        holder.distanceTextView.setText(distance);
    }

    @Override
    public int getItemCount() {
        if (dbHelper == null) {
            return historyItems.size();
        } else {
            return (int) dbHelper.getHistoryCount();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView dateTextView;
        public TextView stepsTextView;
        public TextView distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.date);
            stepsTextView = (TextView) itemView.findViewById(R.id.steps);
            distanceTextView = (TextView) itemView.findViewById(R.id.distance);
        }
    }
}
