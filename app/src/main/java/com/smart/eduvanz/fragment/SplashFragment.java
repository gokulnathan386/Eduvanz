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
 * Use the {@link SplashFragment} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment extends Fragment {

    DatabaseHandler db;
    public SplashFragment() {
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
        View view= inflater.inflate(R.layout.fragment_splash, container, false);
        db = new DatabaseHandler(getContext());
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(db.getEduserCount() > 0)
                {
                    Customer objCus = db.getEdUser();
                    Log.d("Bio",""+objCus.getAccId());
                    Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_mobileLoginFragment);
                }else{
                    Customer objCus = new Customer();
                    objCus.setAccId("");
                    objCus.setIsFaceEnabled(0);
                    objCus.setIsMPinEnabled(0);
                    objCus.setIsFingerEnabled(0);
                    objCus.setLoginDate("");
                    objCus.setLoginDate("");
                    objCus.setLoginStatus(0);
                    db.addEdUser(objCus);
                    Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_firstFragment);

                }
            }
        }, 2000);
        return  view;
    }

}