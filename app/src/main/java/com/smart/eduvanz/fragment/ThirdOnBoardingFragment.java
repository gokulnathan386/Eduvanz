package com.smart.eduvanz.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.eduvanz.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdOnBoardingFragment#} factory method to
 * create an instance of this fragment.
 */
public class ThirdOnBoardingFragment extends Fragment {


    public ThirdOnBoardingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thirdonboarding_screen, container, false);

        Button button = view.findViewById(R.id.thirdcontinue);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_defaultThirdFragment);
            }
        });

        TextView skipTextView= view.findViewById(R.id.third_skip_text_id);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_defaultThirdFragment);

            }
        });


        return view;
    }
}