package com.smart.eduvanz.fragment;

import android.Manifest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import java.util.Map;

public class FragmentPermissionheper {

    FramgentPermissionInterface framgentPermissionInterface;
    public void startPermissionRequest(FragmentActivity fractivty, FramgentPermissionInterface fsInterface, String[] manifest)
    {
        ActivityResultLauncher<String[]> requestPermissionLauncher = fractivty.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Boolean areAllGranted = true;
                for(Boolean b : result.values()) {
                    areAllGranted = areAllGranted && b;
                }
                if(areAllGranted) {
                    fsInterface.onGranted(areAllGranted);
                }
            }
        });

        requestPermissionLauncher.launch(
               manifest);
    }


}
