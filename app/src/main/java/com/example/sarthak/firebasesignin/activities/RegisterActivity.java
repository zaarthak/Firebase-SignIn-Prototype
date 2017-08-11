package com.example.sarthak.firebasesignin.activities;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.sarthak.firebasesignin.R;
import com.example.sarthak.firebasesignin.managers.FirebaseAuthorisation;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mName, mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.register_activity_title);
        }

        Button mSignUp = (Button) findViewById(R.id.register_sign_up_btn);

        mName = (TextInputLayout) findViewById(R.id.register_name);
        mEmail = (TextInputLayout) findViewById(R.id.register_email);
        mPassword = (TextInputLayout) findViewById(R.id.register_password);

        mSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.register_sign_up_btn :
                registerUser();
                break;
        }
    }

    /**
     * Register new user to firebase authentication
     */
    private void registerUser() {

        // setup progress dialog
        ProgressDialog mProgressDialog = new ProgressDialog(RegisterActivity.this);

        mProgressDialog.setTitle(getString(R.string.register_progress_dialog_title));
        mProgressDialog.setMessage(getString(R.string.register_progress_dialog_message));
        mProgressDialog.setCanceledOnTouchOutside(false);

        String name = mName.getEditText().getText().toString();
        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        if (!(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))) {

            mProgressDialog.show();

            // register user to firebase authentication
            // store name and email to firebase database
            FirebaseAuthorisation firebaseAuthorisation = new FirebaseAuthorisation(RegisterActivity.this);
            firebaseAuthorisation.registerUser(name, email, password, mProgressDialog);
        }
    }
}
