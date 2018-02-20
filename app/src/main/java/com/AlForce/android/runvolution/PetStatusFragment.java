package com.AlForce.android.runvolution;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.AlForce.android.runvolution.R;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class PetStatusFragment extends Fragment {

    String petName;
    int petLevel;
    int petXP;
    private TextView petNameView;
    private TextView petLevelView;
    private TextView petXPView;

    public PetStatusFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String petData = getArguments().getString("petData");
            try {
                JSONObject rawPetData = new JSONObject(petData);
                petName = rawPetData.getString("name");
                petLevel = rawPetData.getInt("level");
                petXP = rawPetData.getInt("xp");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        petNameView = (TextView) view.findViewById(R.id.pet_name);
        petLevelView = (TextView) view.findViewById(R.id.pet_level);
        petXPView = (TextView) view.findViewById(R.id.pet_xp);
        petNameView.setText(petNameView.getText().toString() + " " + petName);
        petLevelView.setText(petLevelView.getText().toString() + " " +  String.format("%d", petLevel));
        petXPView.setText(petXPView.getText().toString() +  " " +  String.format("%d",petXP));

    }

}
