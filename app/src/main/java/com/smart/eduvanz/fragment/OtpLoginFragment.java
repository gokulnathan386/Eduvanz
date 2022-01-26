package com.smart.eduvanz.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smart.eduvanz.DrawableClickListener;
import com.smart.eduvanz.R;
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
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtpLoginFragment#} factory method to
 * create an instance of this fragment.
 */
public class OtpLoginFragment extends Fragment {

    FragmentActivity fragmentActivity;
    EditText[] otpET = new EditText[6];
    int resStatus = 0;
    AppStorage appStorage;
    TextView txtError;
    CountDownTimer objCount;
    View view;
    String msg="";
    String status;
    String onlyNo;
    int onBorard = 0;
    boolean uType = false;
    public OtpLoginFragment() {
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
        view = inflater.inflate(R.layout.fragment_otp_login, container, false);
        appStorage = AppStorage.getInstance();

        Button button= view.findViewById(R.id.otplogindefaultcontinue);
        txtError = view.findViewById(R.id.otp_error);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button)v;
                String buttonText = b.getText().toString();
                if(buttonText.equals("Resend OTP"))
                {
                    otpET[0].setText("");
                    otpET[1].setText("");
                    otpET[2].setText("");
                    otpET[3].setText("");
                    objCount.start();
                    new ResenOTPTask().execute();
                }
                //Toast.makeText(getContext(), "", Toast.LENGTH_LONG).show();
                //Navigation.findNavController(v).navigate(R.id.action_default_loginotpFragment);
            }
        });

        TextView mobileOTPMessageText = (TextView) view.findViewById(R.id.otp_mobiletextview);
        String normalText = "Enter the OTP Sent to "+appStorage.getMob();
        String boldText = appStorage.getMob();
        SpannableString str = new SpannableString(normalText);
        str.setSpan(new StyleSpan(Typeface.BOLD), normalText.length() - boldText.length(), normalText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        onlyNo = boldText.substring(boldText.length() - 10);
        mobileOTPMessageText.setText(str);

        mobileOTPMessageText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                       if(event.getRawX() >= (mobileOTPMessageText.getRight() - mobileOTPMessageText.getTotalPaddingRight())) {
                        // your action for drawable click event
                           appStorage.setIsEditMob(1);
                           Navigation.findNavController(view).navigate(R.id.action_otplogin_to_loginscreen);
                           return true;
                    }
                }
                return true;
            }
        });

        objCount = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                button.setText("Resend in "+String.format("%02d:%02ds",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }
            public void onFinish() {
                button.setText("Resend OTP");
            }
        }.start();

        otpET[0] = view.findViewById(R.id.otpET1);
        otpET[1] = view.findViewById(R.id.otpET2);
        otpET[2] = view.findViewById(R.id.otpET3);
        otpET[3] = view.findViewById(R.id.otpET4);

        setMobileNumberOtpEditTextHandler();

        otpET[0].requestFocus();
        final InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        return view;
    }

    private void checkIsPressedBackButton()
    {
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
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
                        if(appStorage.getOtp().equals(otpCon))
                        {
                           // Navigation.findNavController(view).navigate(R.id.action_default_loginotpFragment);
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
            //dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("otp", appStorage.getOtp()));
            list.add(new BasicNameValuePair("logId",""+appStorage.getLogId()));
            EduvanzUtility.apiPostCall("verify_otp",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(appStorage.apiCallStatus == 1) {
                try{
                    txtError.setVisibility(View.INVISIBLE);
                    JSONObject json = appStorage.getJsonObject();
                    appStorage.setCust_id(json.getString("id"));
                    appStorage.setCust_name(json.getString("first_name"));
                    onBorard = json.getInt("onBoard");
                    EduvanzUtility.hideKeyboardFrom(fragmentActivity);

                    if(json.getInt("mpin") == 0)
                    {
                        Navigation.findNavController(view).navigate(R.id.action_default_loginotpFragment);
                    }else{
                        onBoardNavication(onBorard);
                    }

                }catch (Exception e){}
            }else if(appStorage.apiCallStatus == 2) {
                txtError.setVisibility(View.VISIBLE);
            }
        }

    }

    class ResenOTPTask extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
            dialog.setMessage("Loading, please wait");
            //dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("mobile_no", onlyNo));
            list.add(new BasicNameValuePair("logId",""+appStorage.getLogId()));
            EduvanzUtility.apiPostCall("send_otp",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(appStorage.apiCallStatus == 1) {
                try{
                    txtError.setVisibility(View.INVISIBLE);
                    JSONObject json = appStorage.getJsonObject();
                    appStorage.setCust_id(json.getString("id"));
                    EduvanzUtility.hideKeyboardFrom(fragmentActivity);
                }catch (Exception e){}
            }else if(appStorage.apiCallStatus == 2) {
                txtError.setVisibility(View.VISIBLE);
            }
        }

    }
    private void onBoardNavication(int onBorard)
    {
        switch (onBorard)
        {
            case 0:
                Navigation.findNavController(view).navigate(R.id.action_otplogin_to_yourdetails);
                break;
            case 1:
                Navigation.findNavController(view).navigate(R.id.action_otplogin_to_yourdetails);
                break;
            case 2:
                Navigation.findNavController(view).navigate(R.id.action_otplogin_to_limitfetch);
                break;
        }
    }

}