package com.AlForce.android.runvolution;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.AlForce.android.runvolution.timer.Timer;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public TextView timerTextView;
    public Button timerButton;
    private Timer timer;
    public HomeFragment() {
        // Required empty public constructor
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
        timer = new Timer(timerTextView);
        timerButton.setText("START");
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (button.getText().equals("STOP")) {
                    timer.timerHandler.removeCallbacks(timer.timerRunnable);
                    button.setText("START");
                } else {
                    timer.startTime = System.currentTimeMillis();
                    timer.timerHandler.postDelayed(timer.timerRunnable, 0);
                    button.setText("STOP");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.timerHandler.removeCallbacks(timer.timerRunnable);
        Button button = (Button) getView().findViewById(R.id.timerButton);
        button.setText("START");
    }
}
