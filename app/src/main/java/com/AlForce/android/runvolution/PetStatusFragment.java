package com.AlForce.android.runvolution;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.AlForce.android.runvolution.R;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PetStatusFragment extends Fragment {

    String petName;
    int petLevel;
    int petXP;

    public PetStatusFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = this.getActivity().getSharedPreferences(getString(R.string.sharedpref_file), MODE_PRIVATE);
        petName = preferences.getString("petName", "Bobby");
        petLevel = preferences.getInt("petLevel", 1);
        petXP = preferences.getInt("petXP",0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_status, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView petNameView = (TextView) view.findViewById(R.id.pet_name);
        TextView petLevelView = (TextView) view.findViewById(R.id.pet_level);
        TextView petXPView = (TextView) view.findViewById(R.id.pet_xp);
        petNameView.setText(petNameView.getText().toString() + " " + petName);
        petLevelView.setText(petLevelView.getText().toString() + " " +  String.format("%d", petLevel));
        petXPView.setText(petXPView.getText().toString() +  " " +  String.format("%d",petXP));

    }
}
