package com.smart.eduvanz.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
 * Use the {@link SetuppinFragment} factory method to
 * create an instance of this fragment.
 */
public class SetuppinFragment extends Fragment {


    FragmentActivity fragmentActivity;
    AppStorage appStorage;
    TextView txtError;
    CountDownTimer objCount;
    View view;
    String msg="";
    String status;
    String mPin;
    boolean isChecked = false;

    CheckBox chbox;
    String cValue = "No";
    EditText[] otpET = new EditText[6];
    DatabaseHandler db;
    public SetuppinFragment() {
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
        view = inflater.inflate(R.layout.fragment_setuppin, container, false);
        appStorage = AppStorage.getInstance();
        db = new DatabaseHandler(getContext());
        Button button = view.findViewById(R.id.setupPINcontinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigation.findNavController(v).navigate(R.id.action_default_to_setuppinFragment);
                String otpCon = otpET[0].getText().toString()+otpET[1].getText().toString()+otpET[2].getText().toString()+otpET[3].getText();
                if(otpCon.length() == 4)
                {
                    Bundle args = new Bundle();
                    args.putString("mpin",otpCon);
                    args.putString("ischecked",cValue);
                    Navigation.findNavController(view).navigate(R.id.action_confirm_mpin,args);

                }else{
                    Toast.makeText(getContext(), "Invalid mPin", Toast.LENGTH_LONG).show();
                }
            }
        });

        chbox = (CheckBox)view.findViewById(R.id.checkBox);

        chbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer objCus = new Customer();
                objCus.setAccId(appStorage.getCust_id());
                //Navigation.findNavController(v).navigate(R.id.action_default_to_setuppinFragment);
                if(chbox.isChecked()) {
                    cValue = "Yes";
                    objCus.setIsFaceEnabled(1);
                    db.updateBiometric(objCus);
                }else{
                    cValue = "No";
                    objCus.setIsFaceEnabled(0);
                    db.updateBiometric(objCus);
                }
            }
        });
        otpET[0] = view.findViewById(R.id.otpET1);
        otpET[1] = view.findViewById(R.id.otpET2);
        otpET[2] = view.findViewById(R.id.otpET3);
        otpET[3] = view.findViewById(R.id.otpET4);

        setMobileNumberOtpEditTextHandler();
        otpET[0].requestFocus();
        EduvanzUtility.hideKeyboardFrom(fragmentActivity);
        TextView textViewSkipForNow = view.findViewById(R.id.skip_for_now_textview);
        textViewSkipForNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_default_to_setuppinSkipForNowFragment);
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
                    String otpCon = otpET[0].getText().toString()+otpET[1].getText().toString()+otpET[2].getText().toString()+otpET[3].getText();
                    if(iVal == 3 && otpCon.length() == 4)
                    {
                        EduvanzUtility.hideKeyboardFrom(fragmentActivity);
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
}