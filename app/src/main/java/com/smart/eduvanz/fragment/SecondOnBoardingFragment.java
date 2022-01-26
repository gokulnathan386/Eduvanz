package com.smart.eduvanz.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.eduvanz.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondOnBoardingFragment#} factory method to
 * create an instance of this fragment.
 */
public class SecondOnBoardingFragment extends Fragment {


    public SecondOnBoardingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_secondonboarding, container, false);

        Button button = view.findViewById(R.id.secondcontinue);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Navigation.findNavController(v).navigate(R.id.action_defaultSecondFragment);
            }
        });

        TextView skipTextView= view.findViewById(R.id.second_skip_text_id);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_defaultSecondSkipFragment);
            }
        });


        return view;
    }
}