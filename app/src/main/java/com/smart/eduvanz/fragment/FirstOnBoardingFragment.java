package com.smart.eduvanz.fragment;

import android.content.Context;
import android.content.res.Resources;
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
 * Use the {@link FirstOnBoardingFragment} factory method to
 * create an instance of this fragment.
 */
public class FirstOnBoardingFragment extends Fragment {


    public FirstOnBoardingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view= inflater.inflate(R.layout.fragment_firstonboarding, container, false);

         Button button = view.findViewById(R.id.firstcontinue);

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                     Navigation.findNavController(v).navigate(R.id.action_defaultFirstFragment);
             }
         });


        TextView skipTextView= view.findViewById(R.id.first_skip_text_id);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_defaultFirstSkipFragment);
            }
        });

        return view;
    }


}