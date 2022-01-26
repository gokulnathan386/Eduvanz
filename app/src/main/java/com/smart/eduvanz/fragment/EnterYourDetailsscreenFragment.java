package com.smart.eduvanz.fragment;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@SuppressWarnings("ALL")
public class EnterYourDetailsscreenFragment extends Fragment {

    FragmentActivity fragmentActivity;
    int resStatus = 0;
    AppStorage appStorage;
    TextView txtError;
    CountDownTimer objCount;
    View view;
    String msg="";
    GoogleSignInAccount acct;
    String status;
    int onBorard = 0;
    EditText objFName;
    EditText objLName;
    EditText objEId;
    SignInButton objGoogleLogin;
    GoogleSignInClient mGoogleSignInClient;
    private static  int RC_SIGN_IN =100;
    public EnterYourDetailsscreenFragment() {
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
        view = inflater.inflate(R.layout.fragment_enter_your_detailsscreen, container, false);
        Button button = view.findViewById(R.id.enterYourdetailscontinueetailscontinue);
        appStorage = AppStorage.getInstance();
        objFName = (EditText) view.findViewById(R.id.editTextTextPersonFirstName);
        objLName = (EditText) view.findViewById(R.id.editTextTextPersonLastName) ;
        objEId = (EditText) view.findViewById(R.id.editTextTextPersonEmailIdDetails);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        objGoogleLogin = view.findViewById(R.id.basic_google);
        objGoogleLogin.setSize(objGoogleLogin.SIZE_STANDARD);
        objGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fn = objFName.getText().toString();
                String ln = objLName.getText().toString();
                String email = objEId.getText().toString();
                if(fn.equals(""))
                {
                    objFName.setError("First name cannot be empty");
                }else if(ln.equals("")){
                    objLName.setError("Last name cannot be empty");
                }else if(email.equals("")){
                    objEId.setError("Email id cannot be empty");
                }else{
                    new ExecuteTask().execute(fn,ln,email);
                }
                //Navigation.findNavController(v).navigate(R.id.action_default_to_enterYourDetailsFragment);
            }
        });
        new AccountProfile().execute();
        return view;
    }

    private void SignIn() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
      GetDataGoogleAccount();

    }

    private void GetDataGoogleAccount() {

        if (acct != null) {
            Toast.makeText(getContext(), "googleaccount", Toast.LENGTH_SHORT).show();
            String personName = acct.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();

            Uri personPhoto = acct.getPhotoUrl();
            objFName.setText(personName);

            //Picasso.get().load(personPhoto).into(pic);
            //Glide
        }
    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {

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
            PostData(strings);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if(status.equals("success")) {
                Navigation.findNavController(view).navigate(R.id.action_default_to_enterYourDetailsFragment);
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
            HttpPost httpPost=new HttpPost(appStorage.getApiUrl()+"account_basic");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("first_name", valuse[0]));
            list.add(new BasicNameValuePair("last_name",valuse[1]));
            list.add(new BasicNameValuePair("email",valuse[2]));
            list.add(new BasicNameValuePair("id",appStorage.getCust_id()));
            list.add(new BasicNameValuePair("google_id","0"));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = httpClient.execute(httpPost);
            int respons = response.getStatusLine().getStatusCode();

            Log.d("response status",""+respons);

            if(respons == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                Log.d("murugan",data);
                JSONObject jsono = new JSONObject(data);
                status = jsono.getString("status");
            }else{
                msg = "Failled to connect with server";
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
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
                        if(!uData.getString("first_name__c").equals("null"))
                        {
                            objFName.setText(uData.getString("first_name__c"));
                        }
                        if(!uData.getString("last_name__c").equals("null"))
                        {
                            objLName.setText(uData.getString("last_name__c"));
                        }
                        if(!uData.getString("email__c").equals("null"))
                        {
                            objEId.setText(uData.getString("email__c"));
                        }
                    }
                }catch (Exception e){}
            }
        }

    }
}