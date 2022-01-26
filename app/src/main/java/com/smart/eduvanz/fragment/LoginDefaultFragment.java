package com.smart.eduvanz.fragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.smart.eduvanz.R;
import com.smart.eduvanz.db.Customer;
import com.smart.eduvanz.db.DatabaseHandler;
import com.smart.eduvanz.helper.EduvanzUtility;
import com.smart.eduvanz.helper.PhoneNumberVerifier;
import com.smart.eduvanz.helper.PrefsHelper;
import com.smart.eduvanz.session.AppStorage;
import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;

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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginDefaultFragment} factory method to
 * create an instance of this fragment.
 */
public class LoginDefaultFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    FragmentActivity fragmentActivity;
    EditText editText;
    CheckBox cbWhats;
    View popView;
    private GoogleApiClient mCredentialsApiClient;
    public EditText phoneField1;
    public Button submit;
    public Button popBioBtn;
    public TextView popLoginTxt;
    public TextView popErrorTxt;
    EditText[] otpET = new EditText[6];
    public TextView popTitleText;
    View view;
    Bundle objBundle;
    JSONObject jsonObject;
    ProgressDialog dialog;


    private static final int NOTIFICATION_ID = 1001;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int RC_HINT = 1000;
    private static boolean isVerifying = false;
    private static boolean isRunning;
    boolean uType;
    boolean mPinStatus = false;
    boolean isUp;

    private SmsRetrieverClient smsRetrieverClient;
    private NotificationCompat.Builder notification;
    public AppStorage appStorage;
    DatabaseHandler db;

    private static final String KEY_NAME = "KeyName";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String FORWARD_SLASH = "/";
    String msg;
    String status;
    String deviceId;
    String verificationType;

    int resStatus;
    int onBorard;
    int isMPinSet;
    int isFaceSet;
    int numRows;

    public LoginDefaultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity) context;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mCredentialsApiClient.stopAutoManage(fragmentActivity);
        mCredentialsApiClient.disconnect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login_default, container, false);
        appStorage = AppStorage.getInstance();
        objBundle = new Bundle();
        db = new DatabaseHandler(getContext());

        appStorage.setOtp("1234");
        popView = view.findViewById(R.id.pop_pinview);
        popView.setVisibility(View.INVISIBLE);
        isUp = false;

        cbWhats = view.findViewById(R.id.cbWhats);
        String txtWhats = "Get OTP on WhatsApp";
        String boldText = "WhatsApp";
        SpannableString str = new SpannableString(txtWhats);
        str.setSpan(new StyleSpan(Typeface.BOLD), txtWhats.length() - boldText.length(), txtWhats.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        cbWhats.setText(str);

        popBioBtn = view.findViewById(R.id.pop_pinbutton);
        popLoginTxt = view.findViewById(R.id.pop_pinlogintext);
        popTitleText = view.findViewById(R.id.pop_pintitle);
        popErrorTxt = view.findViewById(R.id.pop_otp_error);
        otpET[0] = view.findViewById(R.id.pop_otpET1);
        otpET[1] = view.findViewById(R.id.pop_otpET2);
        otpET[2] = view.findViewById(R.id.pop_otpET3);
        otpET[3] = view.findViewById(R.id.pop_otpET4);
        setMobileNumberOtpEditTextHandler();

        Button btn = view.findViewById(R.id.logindefaultcontinue);
        editText = view.findViewById(R.id.editTextPhone);


        popBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUp) {
                    slideDown(popView);
                } else {
                    slideUp(popView);
                }
                isUp = !isUp;
                getBiometricPromptHandler().authenticate(getBiometricPrompt());
            }
        });
        popLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUp) {
                    slideDown(popView);
                } else {
                    slideUp(popView);
                }
                isUp = !isUp;
                new SendOTPTask().execute();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = editText.getText().toString();
                if(mob.equals("")) {
                    Toast.makeText(getContext(), "Enter your mobile no", Toast.LENGTH_LONG).show();
                }else if(mob.length() != 10){
                    Toast.makeText(getContext(), "Mobile number should be 10 digits", Toast.LENGTH_LONG).show();
                }else if(!isNetworkAvailable()){
                    Toast.makeText(getContext(), "Enable mobile data", Toast.LENGTH_LONG).show();
                }else{
                    appStorage.setMob(mob);
                    new LoginTask().execute(mob);
                }
                //appStorage.setOtp("1234");
               //Navigation.findNavController(v).navigate(R.id.action_mobileLoginFragment_to_otpLoginFragment, null);
            }
        });



        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {

                }
            }
        });
        if(appStorage.getIsEditMob() == 0){

            mCredentialsApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();

            TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(getContext(), sdkCallback)
                    .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
                    // .buttonColor(Color.parseColor(colorSpinner.getSelectedItem().toString()))
                    // .buttonTextColor(Color.parseColor(colorTextSpinner.getSelectedItem().toString()))
                    .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
                    .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
                    .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
                    .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
                    // .privacyPolicyUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                    // .termsOfServiceUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                    .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                    .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
                    .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                    .build();

            TruecallerSDK.init(trueScope);
            numRows = db.getEduserCount();
            Log.d("num rows:",""+numRows);
            displayBiometricButton();
            if(numRows > 0)
            {
                Customer objCust = db.getEdUser();
                appStorage.setCust_id(objCust.getAccId());
                isMPinSet = objCust.getIsMPinEnabled();
                isFaceSet = objCust.getIsFaceEnabled();
                if(isFaceSet == 1)
                {
                    getBiometricPromptHandler().authenticate(getBiometricPrompt());
                }else{
                    if(TruecallerSDK.getInstance().isUsable()){
                      // TruecallerSDK.getInstance().getUserProfile( fragmentActivity);
                    }else{
                        showHint();
                    }
                }
            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            deviceId = getDeviceIMEI();
        }


        return view;
    }
    //hint popup

    // Mobile number picker
    private void showHint() {
        // ui.clearKeyboard();
        HintRequest hintRequest = new HintRequest.Builder()
                        .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                        .setPhoneNumberIdentifierSupported(true)
                        .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            fragmentActivity.startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e("", "Could not start hint picker Intent", e);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == RC_HINT)
        {
            switch (resultCode) {
                case 1001:
                    break;
                case -1:
                    Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                    String pickNo = cred.getId();
                    String onlyNo = pickNo.substring(pickNo.length() - 10);
                    editText.setText(onlyNo);
                    appStorage.setMob(pickNo);
                    new LoginTask().execute(editText.getText().toString());
                    break;
            }
        }
        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {

            TruecallerSDK.getInstance().onActivityResultObtained(fragmentActivity, requestCode, resultCode, data);
        }

    }

    //Bio metric code start here
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displayBiometricButton() {
       // generateSecretKey();
        /*if (isBiometricCompatibleDevice()) {
            touchButton.setEnabled(false);
        } else {
            touchButton.setEnabled(true);
            generateSecretKey();
        }*/
    }

    // Biometric Section
    private BiometricManager getBiometricManager() {
        return BiometricManager.from(getContext());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void generateSecretKey() {
        KeyGenerator keyGenerator = null;
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(false)
                .build();
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        if (keyGenerator != null) {
            try {
                keyGenerator.init(keyGenParameterSpec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            keyGenerator.generateKey();
        }
    }

    private SecretKey getSecretKey() {
        KeyStore keyStore = null;
        Key secretKey = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        if (keyStore != null) {
            try {
                keyStore.load(null);
            } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                secretKey = keyStore.getKey(KEY_NAME, null);
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }
        }
        return (SecretKey) secretKey;
    }
    private BiometricPrompt.PromptInfo getBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Enter your phone lock pattern,")
                .setSubtitle("PIN password or fingerprint")
                .setNegativeButtonText("Use PIN")
                .setConfirmationRequired(false)
                .build();
    }

    private void onBiometricSuccess() {
        //Call the respective API on biometric success
        // callLoginApi("userName", "password");
        new AccountProfile().execute();
    }

    private BiometricPrompt getBiometricPromptHandler() {
        return new BiometricPrompt(this, ContextCompat.getMainExecutor(getContext()),
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Log.d("error code",""+errorCode);
                        if(errorCode == 10) {
                            showHint();
                        }else{
                            if (isUp) {
                                slideDown(popView);
                            } else {
                                if(numRows > 0)
                                {
                                    Customer objCust = db.getEdUser();
                                    Log.d("Bio", objCust.getMobile());
                                    new LoginViaPin().execute(objCust.getMobile());

                                }
                                slideUp(popView);
                            }
                            isUp = !isUp;
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        onBiometricSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                }
        );
    }
    //Bio metric code end here

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        checkReadNumberPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkReadNumberPermission()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            {
                int permissioncheck =getActivity().checkCallingPermission("Manifest.permission.READ_PHONE_NUMBERS");
                permissioncheck +=getActivity().checkCallingPermission(Manifest.permission.READ_PHONE_STATE);
                if(permissioncheck != 0)
                {
                    getActivity().requestPermissions(new String[]{Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE}, 1001);
                }
            }
        }
    }


// Truecaller section
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private final ITrueCallback sdkCallback = new ITrueCallback() {

        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            //Log.i("true1", trueProfile.phoneNumber + " " + trueProfile.lastName+ " " + trueProfile.email);
            String pickNo = trueProfile.phoneNumber;
            String onlyNo = pickNo.substring(pickNo.length() - 10);
            new ExecuteTruecallerTask().execute(onlyNo,trueProfile.firstName,trueProfile.lastName,trueProfile.email);
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            Log.i("true2", trueError.toString());
        }

        @Override
        public void onVerificationRequired(@Nullable final TrueError trueError) {
            Log.i("true3", "onVerificationRequired");
        }

    };

    class ExecuteTruecallerTask extends AsyncTask<String, Integer, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            PostTruecallerData(strings);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();

            if(status.equals("success")) {
                onBoardNavication(onBorard);
            }else{
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        }

    }

    public void PostTruecallerData(String[] valuse) {
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(appStorage.getApiUrl()+"truecaller_login");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("phoneNumber", valuse[0]));
            list.add(new BasicNameValuePair("firstName",valuse[1]));
            list.add(new BasicNameValuePair("lastName",valuse[2]));
            list.add(new BasicNameValuePair("email",valuse[3]));
            list.add(new BasicNameValuePair("device_id","0"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            int respons = response.getStatusLine().getStatusCode();

            Log.d("response status",""+respons);

            if(respons == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                Log.d("murugan",data);
                jsonObject = new JSONObject(data);
                status = jsonObject.getString("status");
                if(status.equals("success")) {
                    appStorage.setCust_id(jsonObject.getString("id"));
                    appStorage.setCust_name(jsonObject.getString("first_name"));
                    onBorard = jsonObject.getInt("onBoard");
                    if(jsonObject.getString("isNewUser").equals("Yes"))
                    {
                        uType = true;
                        mPinStatus = true;
                    }else{
                        if(jsonObject.getInt("mpin") == 0) {
                            onBorard = 0;
                        }else if(jsonObject.getInt("mpin") > 0 && onBorard == 0){
                        }else{
                            onBorard = onBorard + 1;
                        }

                        uType = false;
                    }
                }
            }else{
                msg = "Failled to connect with server";
            }
        }
        catch(Exception e)
        {
            Log.d("true caller error",e.getMessage());
        }

    }

    //trucaller ends

    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // OTP or PIN input event lisener
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
                            //new MPINLoginFragment.ExecuteTask().execute();
                            new VerifyMPIN().execute();
                        }else{
                            popErrorTxt.setVisibility(View.VISIBLE);
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

    @SuppressLint("MissingPermission")
    public String getDeviceIMEI() {
        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    fragmentActivity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) fragmentActivity.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        fragmentActivity.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    class LoginTask extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
           // dialog.setMessage("Connecting, please wait");
            // dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("mobile_no", strings[0]));
            list.add(new BasicNameValuePair("device_id",deviceId));
            EduvanzUtility.apiPostCall("mobile_login",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            Log.d("status","after execute");

            if(appStorage.apiCallStatus == 1) {
                try {
                    JSONObject jsono = appStorage.getJsonObject();
                    status = jsono.getString("status");
                    if (status.equals("success")) {
                        appStorage.setLogId(jsono.getInt("log_id"));
                        appStorage.setCust_id(jsono.getString("id"));
                        appStorage.setOtp(jsono.getString("otp"));
                        uType = jsono.getBoolean("isNewUser");
                        onBorard = jsono.getInt("onBoard");
                        appStorage.setMob(jsono.getString("mobile_no"));
                        verificationType = jsono.getString("verificationType");
                        if(numRows > 0)
                        {
                            Customer objCust = new Customer();
                            objCust.setMobile(appStorage.getMob());
                            objCust.setAccId(appStorage.getCust_id());
                            db.updateMobile(objCust);

                        }else{
                            Customer objCust = new Customer();
                            objCust.setMobile(appStorage.getMob());
                            objCust.setAccId(appStorage.getCust_id());
                            db.addEdUser(objCust);
                        }
                        if(verificationType.equals("mPin")){
                            Navigation.findNavController(view).navigate(R.id.action_mobileLoginFragment_to_minLoginFragment, objBundle);
                        }else {
                            Navigation.findNavController(view).navigate(R.id.action_mobileLoginFragment_to_otpLoginFragment, objBundle);
                        }
                    }else{
                        Toast.makeText(getContext(), "Failed to cnnect", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                }catch (Exception e){}
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    class AccountProfile extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
            // dialog.setMessage("Connecting, please wait");
            //dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("cust_id", appStorage.getCust_id()));
            EduvanzUtility.apiPostCall("account_profile",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            Log.d("Bio",""+appStorage.apiCallStatus);
            if(appStorage.apiCallStatus == 1) {
                try{
                    JSONObject json = appStorage.getJsonObject();
                    if(json.getString("status").equals("success"))
                    {
                        JSONObject uAcc = json.getJSONObject("accountDet");
                        JSONObject uPro = uAcc.getJSONObject("account_profile");
                        onBorard = uPro.getInt("onbording");
                        if(uPro.getInt("mpin") > 0 && onBorard == 0)
                        {
                                onBorard = 1;
                        }

                        onBoardNavication(onBorard);
                    }
                }catch (Exception e){}
            }

        }

    }
    class LoginViaPin extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fragmentActivity);
            // dialog.setMessage("Connecting, please wait");
            // dialog.setTitle("Connecting...");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("mobile_no", strings[0]));
            list.add(new BasicNameValuePair("device_id",deviceId));
            EduvanzUtility.apiPostCall("mobile_login",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            Log.d("status","after execute");

            if(appStorage.apiCallStatus == 1) {
                try {
                    JSONObject jsono = appStorage.getJsonObject();
                    status = jsono.getString("status");
                    if (status.equals("success")) {
                        appStorage.setLogId(jsono.getInt("log_id"));
                        appStorage.setCust_id(jsono.getString("id"));
                        appStorage.setOtp(jsono.getString("otp"));
                        uType = jsono.getBoolean("isNewUser");
                        onBorard = jsono.getInt("onBoard");
                        appStorage.setMob(jsono.getString("mobile_no"));
                        verificationType = jsono.getString("verificationType");
                    }else{
                        Toast.makeText(getContext(), "Failed to cnnect", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                }catch (Exception e){}
            }
        }

    }
    class SendOTPTask extends AsyncTask<String, Integer, String> {

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
            list.add(new BasicNameValuePair("mobile_no", appStorage.getMob()));
            list.add(new BasicNameValuePair("log_id",""+appStorage.getLogId()));
            EduvanzUtility.apiPostCall("send_otp",list,appStorage);
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

                        appStorage.setLogId(json.getInt("id"));
                        appStorage.setOtp(json.getString("otp"));
                        EduvanzUtility.hideKeyboardFrom(fragmentActivity);
                        Navigation.findNavController(view).navigate(R.id.action_mobileLoginFragment_to_otpLoginFragment);
                    }
                }catch (Exception e){}
            }else if(appStorage.apiCallStatus == 2) {

            }
        }

    }
    class VerifyMPIN extends AsyncTask<String, Integer, String> {

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
            list.add(new BasicNameValuePair("mpin", appStorage.getOtp()));
            list.add(new BasicNameValuePair("account_profile_id", appStorage.getCust_id()));
            list.add(new BasicNameValuePair("logId",""+appStorage.getLogId()));
            EduvanzUtility.apiPostCall("verify_mpin",list,appStorage);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(appStorage.apiCallStatus == 1) {
                try{
                    popErrorTxt.setVisibility(View.INVISIBLE);
                    JSONObject json = appStorage.getJsonObject();
                    Log.d("response",json.getString("id"));
                    appStorage.setCust_id(json.getString("id"));
                    appStorage.setCust_name(json.getString("first_name"));
                    onBorard = json.getInt("onBoard");
                    EduvanzUtility.hideKeyboardFrom(fragmentActivity);
                    onBoardNavication(onBorard);
                }catch (Exception e){}
            }else if(appStorage.apiCallStatus == 2) {
                popErrorTxt.setVisibility(View.VISIBLE);
            }
        }

    }
    private void onBoardNavication(int onBorard)
    {
        switch (onBorard)
        {
            case 0:
                Navigation.findNavController(view).navigate(R.id.action_to_setmpin);
                break;
            case 1:
                Navigation.findNavController(view).navigate(R.id.action_to_basicdetail);
                break;
            case 2:
                Navigation.findNavController(view).navigate(R.id.action_to_showpan);
                break;
            case 3:
                Navigation.findNavController(view).navigate(R.id.action_login_to_limitfetch);
                break;

        }
    }

}