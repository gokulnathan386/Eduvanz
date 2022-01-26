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
import android.widget.Toast;

import com.smart.eduvanz.R;
import com.smart.eduvanz.helper.EduvanzUtility;
import com.smart.eduvanz.session.AppStorage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PanCardInfoFragment#} factory method to
 * create an instance of this fragment.
 */
public class PanCardInfoFragment extends Fragment {

    FragmentActivity fragmentActivity;
    int resStatus = 0;
    AppStorage appStorage;
    View view;
    String pan;
    EditText txtPan;
    public PanCardInfoFragment() {
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
        view= inflater.inflate(R.layout.fragment_pan_card_info, container, false);
        appStorage = AppStorage.getInstance();
        txtPan = view.findViewById(R.id.editVerifyPanCardText);
        Button button = view.findViewById(R.id.btnSubmitPAN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pan = txtPan.getText().toString();
                Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                Matcher matcher = pattern.matcher(pan);
                if(pan.equals(""))
                {
                    Toast.makeText(getContext(), "Enter your PAN number", Toast.LENGTH_SHORT).show();
                }else if(!matcher.matches()) {
                    Toast.makeText(getContext(), "Invalid PAN number", Toast.LENGTH_SHORT).show();
                }else{
                   new UpdatePan().execute();
                }
                //Navigation.findNavController(v).navigate(R.id.action_default_to_enterYourPanCardInfDetailsFragment);
            }
        });
        return view;
    }
    class UpdatePan extends AsyncTask<String, Integer, String> {

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
            list.add(new BasicNameValuePair("id",""+appStorage.getCust_id()));
            list.add(new BasicNameValuePair("pan",pan));
            EduvanzUtility.apiPostCall("check_pan",list,appStorage);
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
                        Navigation.findNavController(view).navigate(R.id.action_panenter_to_limitfetch);
                    }else{
                        Navigation.findNavController(view).navigate(R.id.action_panenter_to_limitfetch);
                    }
                }catch (Exception e){}
            }
        }

    }
}