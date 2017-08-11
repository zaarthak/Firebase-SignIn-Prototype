package com.example.sarthak.firebasesignin.managers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.sarthak.firebasesignin.R;
import com.example.sarthak.firebasesignin.activities.HomeActivity;
import com.example.sarthak.firebasesignin.activities.LoginActivity;
import com.example.sarthak.firebasesignin.models.User;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthorisation {

    private Context mContext;

    private FirebaseAuth mAuth;

    // call to firebase authentication
    public FirebaseAuthorisation(Context context) {

        this.mContext = context;

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Registers new user to firebase authentication and adds details to firebase database
     *
     * @param name is the name of the user
     * @param email is the email ID of the user
     * @param password is the password of the user
     * @param mProgressDialog is a progress dialog which is dismissed when the user registration is complete
     */
    public void registerUser(final String name, final String email, String password, final ProgressDialog mProgressDialog) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {

                    mProgressDialog.hide();
                    Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                } else {

                    String UID = getCurrentUser();

                    DatabaseReference mDatabase;

                    // create a database reference for the user
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                    User user = new User(name, email);

                    // store user details to firebase database
                    mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                // dismiss progress dialog
                                mProgressDialog.dismiss();

                                // launch HomeScreenActivity when user registration is complete
                                Intent mainActivity = new Intent(mContext, HomeActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mContext.startActivity(mainActivity);

                                // finish current activity
                                ((Activity) mContext).finish();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Logs in registered user with firebase authentication
     *
     * @param email is the email ID of the user
     * @param password is the password of the user
     * @param mProgressDialog is a progress dialog which is dismissed when the user registration is complete
     */
    public void loginUser(String email, String password, final ProgressDialog mProgressDialog) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    // dismiss progress dialog
                    mProgressDialog.dismiss();

                    // launch HomeScreenActivity when user registration is complete
                    Intent mainIntent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(mainIntent);

                    // finish current activity
                    ((Activity) mContext).finish();
                } else {

                    mProgressDialog.hide();
                    Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Registers user logged in through Google account to firebase authentication
     *
     * @param account is the GoogleSignInAccount of the user
     */
    public void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) mContext), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // launch HomeScreenActivity when user registration is complete
                            Intent mainIntent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(mainIntent);
                        } else {

                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Registers user logged in through Facebook account to firebase authentication
     *
     * @param token is the AccessToken for facebook login
     */
    public void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) mContext), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // launch HomeScreenActivity when user registration is complete
                            Intent mainIntent = new Intent(mContext, HomeActivity.class);
                            mContext.startActivity(mainIntent);

                        } else {

                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // check if current user is logged in
    public void checkFirebaseLogin() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            logoutUser();
        }
    }

    // get current user UID
    public String getCurrentUser() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

    // log out current user
    public void logoutUser() {

        // sign out from firebase authentication
        mAuth.signOut();

        // launch start activity
        Intent startIntent = new Intent(mContext, LoginActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(startIntent);

        // finish current activity
        ((Activity) mContext).finish();
    }
}
