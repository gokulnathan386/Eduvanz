package com.smart.eduvanz.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart.eduvanz.R;
import com.smart.eduvanz.db.Customer;
import com.smart.eduvanz.db.DatabaseHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashSuccessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashSuccessFragment extends Fragment {
     DatabaseHandler db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplashSuccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashSuccessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashSuccessFragment newInstance(String param1, String param2) {
        SplashSuccessFragment fragment = new SplashSuccessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_splash_success, container, false);
        db = new DatabaseHandler(getContext());
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Navigation.findNavController(view).navigate(R.id.action_limitfetch_to_successfully);
            }
        }, 1000);

        return  view;
    }
}