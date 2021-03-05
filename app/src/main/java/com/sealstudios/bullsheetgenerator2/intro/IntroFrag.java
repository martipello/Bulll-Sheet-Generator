package com.sealstudios.bullsheetgenerator2.intro;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.sealstudios.bullsheetgenerator2.R;

public class IntroFrag extends Fragment {
public static boolean accepted;

    public IntroFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_frag, container, false);
        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        final String accepted = sharedPreferences.getString("TERMS","NOT ACCEPTED");
        CheckBox checkboxvariable = (CheckBox)rootView.findViewById(R.id.tandc);
        checkboxvariable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString("TERMS", "ACCEPTED");
                e.apply();
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


}
