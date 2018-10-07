package com.gabrielraniel.envision.wash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.ConnectionService;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private TextView tvDontHaveAccountYet, tvForgotPassword;

    private ImageButton bLogin, bGuestLogin, bSignup, sibGoogle;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseUsers;
    private ValueEventListener vel_users;

    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLE";

    public static String _USER_EMAIL = "";
    public static String _USER_TYPE;

    private Typeface typeface_normal;
    private Typeface typeface_bold;

    private Bundle bundle;

    private LinearLayout llLoginLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FOR FONTS
        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        //FOR FIREBASE INITIALIZATION
        //DATABASE REFERENCE
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        //DATABASE AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();

        //CONNECTOR
        llLoginLayout = (LinearLayout) findViewById(R.id.linearLayoutLoginLayout);
        tilEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        tilPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        etEmail = (TextInputEditText) findViewById(R.id.editTextEmail);
        etPassword = (TextInputEditText) findViewById(R.id.editTextPassword);
        tvDontHaveAccountYet = (TextView) findViewById(R.id.textViewDontHaveAccount);
        tvForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        bLogin = (ImageButton) findViewById(R.id.buttonLogin);
        bGuestLogin = (ImageButton) findViewById(R.id.buttonGuestLogin);
        bSignup = (ImageButton) findViewById(R.id.buttonSignUp);
        sibGoogle = (ImageButton) findViewById(R.id.signInButtonGoogle);

        llLoginLayout.setVisibility(View.VISIBLE);

        //INITIAL VIEW SETUP
        tilEmail.setTypeface(typeface_normal);
        tilPassword.setTypeface(typeface_normal);
        etEmail.setTypeface(typeface_normal);
        etPassword.setTypeface(typeface_normal);
        tvForgotPassword.setTypeface(typeface_bold);
        tvDontHaveAccountYet.setTypeface(typeface_bold);

        //INITIALIZATION OF VIEW
        progressDialog = new ProgressDialog(this);
        bLogin.setOnClickListener(this);
        bSignup.setOnClickListener(this);
        bGuestLogin.setOnClickListener(this);
        sibGoogle.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);

        //CONFIGURE GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "You got an error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        bundle = getIntent().getExtras();

        try {
            if (bundle != null) {
                if (bundle.getString("EMAIL_SENT").equals("yes")) {
                    showEmailVerificationHasBeenSent();
                }

                if (bundle.getString("ACCOUNT_DELETED").equals("yes")) {
                    showAccountDeleted();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
//                if (isNetworkAvailable() && isLocationAvailable())
                    login();
//                else
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.buttonSignUp:
                if (isNetworkAvailable() && isLocationAvailable()) {
                    Intent intent = new Intent(this, RegisterFirstStepActivity.class);
                    intent.putExtra("GMAIL", "no");
                    intent.putExtra("FACEBOOK", "no");
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();

                }
                break;

            case R.id.buttonGuestLogin:
//                if (isNetworkAvailable() && isLocationAvailable())
                    loginAnonymously();
//                else
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();

                break;

            case R.id.signInButtonGoogle:
                if (isNetworkAvailable() && isLocationAvailable())
                    signInGoogle();
                else
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.textViewForgotPassword:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void login() {
        //START
        String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        //CHECK IF ALL FIELDS IS NOT EMPTY
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            //IF FIELDS ARE NOT EMPTY
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            //LOGIN
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()) {
                        showEmailPasswordIncorrect();
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        _USER_EMAIL = user.getEmail();

                        if (checkIfEmailVerified()) {
                            databaseUsers.child(user.getUid()).child("password").setValue(convertPasswordMd5(password));

                            //IF EMAIL IS VERIFIED
                            //SUCCESS LOGIN
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            i.putExtra("USER_EMAIL", _USER_EMAIL);
                            startActivity(i);
                            finish();
                        } else {
                            showVerifyEmailFirst();
                        }
                    }
                }
            });
        } else {
            showFillAllTheFields();
        }
        //END
    }

    private void loginAnonymously() {
        //START
        progressDialog.setMessage("Logging in as guest...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Failed to login, please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                _USER_EMAIL = "";
                Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                i.putExtra("USER_EMAIL", _USER_EMAIL);
                finish();
                startActivity(i);
                progressDialog.dismiss();
            }
        });
        //END
    }

    private void signInGoogle() {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean checkIfEmailVerified() {
        //CHECK IF USER EMAIL IS VERIFIED
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailIsVerified = user.isEmailVerified();
        if (!emailIsVerified) {
            mAuth.signOut();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            // CHECK IF THE USER IS ALREADY IN THE DATABASE
                            final String _UID = task.getResult().getUser().getUid();
                            Log.d("UID", _UID);

                            vel_users = databaseUsers.child(task.getResult().getUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean _doesExist = false;

                                    if (String.valueOf(dataSnapshot.child("uid").getValue()).equals(_UID)) {
                                        _doesExist = true;
                                    }

                                    if (!_doesExist) {
                                        //KAPAG WALA PA SA DATABASE MAG REREGISTER MUNA
                                        Intent intent = new Intent(LoginActivity.this, RegisterFirstStepActivity.class);
                                        intent.putExtra("USER_EMAIL", account.getEmail());
                                        intent.putExtra("GMAIL", "yes");
                                        intent.putExtra("FACEBOOK", "no");
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //KAPAG NASA DATABASE NA ILOLOGIN NA
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        _USER_EMAIL = user.getEmail();
                                        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                                        i.putExtra("USER_EMAIL", _USER_EMAIL);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    databaseUsers.child(task.getResult().getUser().getUid()).removeEventListener(vel_users);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return true;
    }

    private boolean isLocationAvailable() {
        return true;
//        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//
//        }
//
//        try
//
//        {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//
//        }
//
//        if (gps_enabled && network_enabled) {
//            return true;
//        }
//        return false;
    }

    private String convertPasswordMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    //WARNING AND PROMPTS
    private void showFillAllTheFields() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_fill_all_the_fields, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewFillAllTheFields);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayFillAllTheFields);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showEmailPasswordIncorrect() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_email_password_incorrect, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewEmailPasswordIncorrect);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayEmailPasswordIncorrect);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showVerifyEmailFirst() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_verify_email, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewVerifyEmail);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayVerifyEmail);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showEmailVerificationHasBeenSent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_email_verification_has_been_sent, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewEmailVerificationHasBeenSent);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayEmailVerificationHasBeenSent);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showAccountDeleted() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_account_deleted, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAccountDeleted);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayAccountDeleted);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

}
