package com.smart.eduvanz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.smart.eduvanz.fragment.FragmentPermissionheper;
import com.smart.eduvanz.fragment.FramgentPermissionInterface;
import com.smart.eduvanz.fragment.LoginDefaultFragment;

import java.util.List;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1001 :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            List<Fragment> lsActiveFragments = getSupportFragmentManager().getFragments();
            for (Fragment fragmentActive : lsActiveFragments) {

                if (fragmentActive instanceof NavHostFragment) {

                    List<Fragment> lsActiveSubFragments = fragmentActive.getChildFragmentManager().getFragments();
                    for (Fragment fragmentActiveSub : lsActiveSubFragments) {

                        if (fragmentActiveSub instanceof LoginDefaultFragment) {
                            fragmentActiveSub.onActivityResult(requestCode, resultCode, data);
                        }

                    }

                }


        }
    }

}