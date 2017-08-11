package com.example.sarthak.firebasesignin.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sarthak.firebasesignin.R;
import com.example.sarthak.firebasesignin.managers.FirebaseAuthorisation;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult> {

    private static final int RC_SIGN_IN = 123;

    private Button mRegButton, mLoginButton, mForgotButton;
    private SignInButton mGoogleButton;
    private LoginButton mFacebookButton;
    private EditText mEmailInput, mPassInput;

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialise all view components
        setUpView();

        // configure Google Sign In to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        mFacebookButton.setReadPermissions("email", "public_profile");
        mFacebookButton.registerCallback(callbackManager, this);

        //-------------------------------------------------------------------
        // button onClick listeners
        //-------------------------------------------------------------------
        mRegButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mForgotButton.setOnClickListener(this);
        mGoogleButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
    }

    /**
     * Initialise all view components
     */
    private void setUpView() {

        mRegButton = (Button) findViewById(R.id.login_register_btn);
        mLoginButton = (Button) findViewById(R.id.login_login_btn);
        mForgotButton = (Button) findViewById(R.id.login_forgot_pass_btn);
        mGoogleButton = (SignInButton) findViewById(R.id.google_sign_in);
        mFacebookButton = (LoginButton) findViewById(R.id.fb_sign_in);

        mEmailInput = (EditText) findViewById(R.id.login_email);
        mPassInput = (EditText) findViewById(R.id.login_password);
    }

    //-------------------------------------------------------------------------
    // button onClick listener
    //-------------------------------------------------------------------------
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.login_register_btn :
                launchRegisterActivity();
                break;
            case R.id.login_login_btn :
                loginUser();
                break;
            case R.id.google_sign_in :
                googleLoginUser();
                break;
            case R.id.login_forgot_pass_btn :
                launchForgotPassActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                new FirebaseAuthorisation(LoginActivity.this).firebaseAuthWithGoogle(account);
            } else {

                Toast.makeText(LoginActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
            }
        } else {

            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSuccess(LoginResult loginResult) {

        Log.d("TAG", "facebook:onSuccess:" + loginResult);
        new FirebaseAuthorisation(LoginActivity.this).handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {

        Log.d("TAG", "facebook:onCancel");
    }

    @Override
    public void onError(FacebookException error) {

        Log.d("TAG", "facebook:onError", error);
    }

    /**
     * Launch register activity
     */
    private void launchRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    /**
     * Login registered user using firebase authentication
     */
    private void loginUser() {

        // setup progress dialog
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setTitle(getString(R.string.login_progress_dialog_title));
        mProgressDialog.setMessage(getString(R.string.login_progress_dialog_message));
        mProgressDialog.setCanceledOnTouchOutside(false);

        String email = mEmailInput.getText().toString();
        String password = mPassInput.getText().toString();

        if (!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))) {

            mProgressDialog.show();
            // login registered user
            FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(LoginActivity.this);
            firebaseAuthorisation.loginUser(email, password, mProgressDialog);
        } else {

            Toast.makeText(LoginActivity.this, R.string.login_error_message, Toast.LENGTH_LONG).show();
        }
    }

    private void googleLoginUser() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void launchForgotPassActivity() {
    }
}
