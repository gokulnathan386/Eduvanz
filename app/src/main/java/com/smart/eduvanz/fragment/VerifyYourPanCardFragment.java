package com.smart.eduvanz.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smart.eduvanz.R;
import com.smart.eduvanz.helper.EduvanzUtility;
import com.smart.eduvanz.session.AppStorage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyYourPanCardFragment#} factory method to
 * create an instance of this fragment.
 */
public class VerifyYourPanCardFragment extends Fragment {

    FragmentActivity fragmentActivity;
    int resStatus = 0;
    AppStorage appStorage;
    View view;
    String pan;
    EditText editText;
    public VerifyYourPanCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_verify_your_pan_card, container, false);
        appStorage = AppStorage.getInstance();
        Button btn = view.findViewById(R.id.btnConfrimAPN);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_panview_to_fetchlimit);
            }
        });
        TextView txtNot = view.findViewById(R.id.txtNotMyPAN);
        txtNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_default_to_VerifyPanCarFragment);
            }
        });
        editText = view.findViewById(R.id.editVerifyPanCardText);
        editText.setBackground(null);
        new AccountProfile().execute();
        return view;
    }
    class AccountProfile extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
            dialog.setMessage("Loading, please wait");
            // dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("cust_id",""+appStorage.getCust_id()));
            EduvanzUtility.apiPostCall("account_profile",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(appStorage.apiCallStatus == 1) {
                try{
                    JSONObject json = appStorage.getJsonObject();
                    if(json.getString("status").equals("success"))
                    {
                        JSONObject uData = json.getJSONObject("accountDet");
                        Log.d("error",uData.getString("pan_number__c"));

                        if(uData.getString("pan_number__c").equals("null"))
                        {
                            Log.d("error","sss");
                            Navigation.findNavController(view).navigate(R.id.action_default_to_VerifyPanCarFragment);
                        }else{
                            editText.setText(uData.getString("pan_number__c"));
                        }
                    }
                }catch (Exception e){}
            }
        }

    }
    private void onBoardNavication(int onBorard)
    {
        switch (onBorard)
        {
            case 2:
                Navigation.findNavController(view).navigate(R.id.action_panview_to_fetchlimit);
                break;
            case 1:
                Navigation.findNavController(view).navigate(R.id.action_mpinlogin_to_showpan);
                break;
        }
    }
}