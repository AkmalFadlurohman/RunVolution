package com.example.android.runvolution;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.runvolution.history.HistoryAdapter;
import com.example.android.runvolution.history.HistoryItem;
import com.example.android.runvolution.utils.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView historyView;
    private RecyclerView.Adapter historyViewAdapter;
    private DatabaseOpenHelper dbHelper;

    private List<HistoryItem> historyItems = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHelper = new DatabaseOpenHelper(getContext());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyView = (RecyclerView) getView().findViewById(R.id.historyView);
        historyView.setHasFixedSize(true);
        historyView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO: Replace harcoded list with fetch from server.
        for (int i = 0; i < 100; i++) {
            HistoryItem item = new HistoryItem(i,
                    new Date(),
                    (i*100),
                    0);
            historyItems.add(item);
        }

        historyViewAdapter = new HistoryAdapter(getContext(), dbHelper);
        historyView.setAdapter(historyViewAdapter);
    }
}
