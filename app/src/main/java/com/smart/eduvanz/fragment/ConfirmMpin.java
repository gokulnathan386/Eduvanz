package com.smart.eduvanz.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.smart.eduvanz.R;
import com.smart.eduvanz.db.Customer;
import com.smart.eduvanz.db.DatabaseHandler;
import com.smart.eduvanz.helper.EduvanzUtility;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ConfirmMpin extends Fragment {



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentActivity fragmentActivity;
    AppStorage appStorage;
    TextView txtError;
    CountDownTimer objCount;
    View view;
    String msg="";
    String status;
    String mPin;
    CheckBox chBox;
    DatabaseHandler db;
    EditText[] otpET = new EditText[6];
    public ConfirmMpin() {
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
        view = inflater.inflate(R.layout.fragment_confirm_mpin, container, false);
        appStorage = AppStorage.getInstance();
        Log.d("response","sss"+appStorage.getCust_id());
        Button button = view.findViewById(R.id.setupPINcontinue);
        txtError = view.findViewById(R.id.otp_error);
        chBox = (CheckBox) view.findViewById(R.id.checkBox);
        db = new DatabaseHandler(getContext());

        mPin = getArguments().getString("mpin");
        String ischecked = getArguments().getString("ischecked");
        if(ischecked.equals("Yes"))
        {
            chBox.setChecked(true);
        }
        chBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer objCus = new Customer();
                objCus.setAccId(appStorage.cust_id);
                //Navigation.findNavController(v).navigate(R.id.action_default_to_setuppinFragment);
                if(chBox.isChecked()) {
                    objCus.setIsFaceEnabled(1);
                    db.updateBiometric(objCus);
                }else{
                    objCus.setIsFaceEnabled(0);
                    db.updateBiometric(objCus);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigation.findNavController(v).navigate(R.id.action_default_to_setuppinFragment);
                String otpCon = otpET[0].getText().toString()+otpET[1].getText().toString()+otpET[2].getText().toString()+otpET[3].getText();
                if(otpCon.length() == 4)
                {
                    mPin = otpCon;
                    //Navigation.findNavController(view).navigate(R.id.action_default_loginotpFragment);
                    new ExecuteTask().execute();
                }else{
                    Toast.makeText(getContext(), "Invalid mPin", Toast.LENGTH_LONG).show();
                }
            }
        });

        otpET[0] = view.findViewById(R.id.otpET1);
        otpET[1] = view.findViewById(R.id.otpET2);
        otpET[2] = view.findViewById(R.id.otpET3);
        otpET[3] = view.findViewById(R.id.otpET4);

        setMobileNumberOtpEditTextHandler();
        otpET[0].requestFocus();
        EduvanzUtility.showKeyboardFrom(fragmentActivity);
        TextView textViewSkipForNow = view.findViewById(R.id.skip_for_now_textview);
        textViewSkipForNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EduvanzUtility.hideKeyboardFrom(fragmentActivity);
                Navigation.findNavController(v).navigate(R.id.action_default_to_confirmpinSkipForNowFragment);
            }
        });

        return view;
    }



    private void setMobileNumberOtpEditTextHandler () {
        for (int i = 0;i < 4;i++) {
            final int iVal = i;

            otpET[iVal].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(iVal == 3 && !otpET[iVal].getText().toString().isEmpty()) {
                        otpET[iVal].clearFocus();
                    } else if (!otpET[iVal].getText().toString().isEmpty()) {
                        otpET[iVal+1].requestFocus();
                    }
                    if(iVal == 3)
                    {
                        String otpCon = otpET[0].getText().toString()+otpET[1].getText().toString()+otpET[2].getText().toString()+otpET[3].getText();
                        if(mPin.equals(otpCon))
                        {
                            txtError.setVisibility(View.INVISIBLE);
                            new ExecuteTask().execute();
                        }else{
                            txtError.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            otpET[iVal].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) {
                        return false;
                    }
                    if(keyCode == KeyEvent.KEYCODE_DEL &&
                            otpET[iVal].getText().toString().isEmpty() && iVal != 0) {
                        otpET[iVal-1].setText("");
                        otpET[iVal-1].requestFocus();//and sets the focus on previous digit
                    }
                    return false;
                }
            });
        }
    }
    class ExecuteTask extends AsyncTask<String, Integer, String> {

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
            PostData(strings);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(status.equals("success")) {
                EduvanzUtility.hideKeyboardFrom(fragmentActivity);
                Toast.makeText(getContext(), "mPin updated successfully", Toast.LENGTH_LONG).show();
                Navigation.findNavController(view).navigate(R.id.action_default_to_confirmpinSkipForNowFragment);
            }else{
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void PostData(String[] valuse) {
        try
        {
            Log.d("response otp",""+appStorage.getOtp());
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(appStorage.getApiUrl()+"mpin_update");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("mpin", mPin));
            list.add(new BasicNameValuePair("id",""+appStorage.getCust_id()));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            int respons = response.getStatusLine().getStatusCode();

            Log.d("response status",""+respons);

            if(respons == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                JSONObject jsono = new JSONObject(data);
                status = jsono.getString("status");
                msg = jsono.getString("message");
            }else{
                msg = "Failled to connect with server";
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }
    private void onBoardNavication(int onBorard)
    {
        switch (onBorard)
        {
            case 0:
                Navigation.findNavController(view).navigate(R.id.action_to_basicdetail);
                break;
        }
    }
}
