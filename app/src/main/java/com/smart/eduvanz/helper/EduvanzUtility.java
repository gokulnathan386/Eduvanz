package com.smart.eduvanz.helper;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;
import com.smart.eduvanz.session.AppStorage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import com.smart.eduvanz.R;


import java.util.ArrayList;
import java.util.List;

public class EduvanzUtility {

    public static void hideKeyboardFrom(FragmentActivity fragmentActivity) {
        InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void showKeyboardFrom(FragmentActivity fragmentActivity) {
        InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 1);
    }
    public static void apiPostCall(String url, List<NameValuePair> para, AppStorage appStorage)
    {

        try
        {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(appStorage.getApiUrl()+url);
            httpPost.setEntity(new UrlEncodedFormEntity(para));
            HttpResponse response = httpClient.execute(httpPost);

            int respons = response.getStatusLine().getStatusCode();
            Log.d("response status",""+respons);
            if(respons == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                JSONObject jsono = new JSONObject(data);
                Log.d("response data",""+data);
                appStorage.setJsonObject(jsono);
                appStorage.setApiCallStatus(1);
            }else{
                appStorage.setApiCallStatus(2);
            }
        }
        catch(Exception e)
        {
            appStorage.setApiCallStatus(0);
            Log.d("response status",""+e.getMessage());
            System.out.println(e);
        }
    }
}
