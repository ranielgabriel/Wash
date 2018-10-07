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
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.models.Business;
import com.gabrielraniel.envision.wash.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers, databaseBusinesses;

    private TextInputEditText etPassword, etConfirmPassword, etBusinessName, etEmail;
    private TextInputLayout tilBusinessName, tilPassword, tilConfirmPassword, tilEmail;
    private TextView tvTapToSwitchUser;
    private ImageButton ibDone, ibBack;
    private ProgressDialog progressDialog;

    private Bundle bundle;
    private String bundle_user_type, bundle_email;

    private Typeface typeface_normal, typeface_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //FOR FONTS
        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        //FOR FIREBASE INITIALIZATION
        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseBusinesses = FirebaseDatabase.getInstance().getReference("businesses");

        //CONNECTORS
        tvTapToSwitchUser = (TextView) findViewById(R.id.textViewStepTwo);
        etBusinessName = (TextInputEditText) findViewById(R.id.editTextBusinessName);
        etPassword = (TextInputEditText) findViewById(R.id.editTextPassword);
        etConfirmPassword = (TextInputEditText) findViewById(R.id.editTextConfirmPassword);
        etEmail = (TextInputEditText) findViewById(R.id.editTextEmail);
        tilBusinessName = (TextInputLayout) findViewById(R.id.textInputLayoutBusinessName);
        tilEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        tilPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        ibDone = (ImageButton) findViewById(R.id.imageButtonDone);
        ibBack = (ImageButton) findViewById(R.id.imageButtonBackStepTwo);

        //INITIAL VIEW SETUP
        etBusinessName.setVisibility(View.GONE);
        etEmail.setEnabled(true);
        tvTapToSwitchUser.setTypeface(typeface_bold);
        etEmail.setTypeface(typeface_normal);
        etBusinessName.setTypeface(typeface_normal);
        etPassword.setTypeface(typeface_normal);
        etConfirmPassword.setTypeface(typeface_normal);
        tilEmail.setTypeface(typeface_normal);
        tilPassword.setTypeface(typeface_normal);
        tilBusinessName.setTypeface(typeface_normal);
        tilConfirmPassword.setTypeface(typeface_normal);

        //INITIALIZATION OF VIEW
        progressDialog = new ProgressDialog(this);
        ibDone.setOnClickListener(this);
        ibBack.setOnClickListener(this);

        //PANG KUHA NG EMAIL AT ID NG NAGLOGIN SA GOOGLE
        bundle = getIntent().getExtras();
        if (bundle != null) {

            bundle_user_type = bundle.getString("USER_TYPE");
            bundle_email = bundle.getString("USER_EMAIL");

            //IF THE REGISTERING USER IS BUSINESS OWNER
            if (bundle_user_type.equals("business owner")) {
                etBusinessName.setVisibility(View.VISIBLE);
            } else {
                etBusinessName.setVisibility(View.GONE);
            }

            if (!bundle_email.equals("")) {
                etEmail.setText(bundle_email);
                etEmail.setEnabled(false);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == ibDone) {
            if (isNetworkAvailable() && isLocationAvailable()) {
                register();
            } else {
                Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
            }
        } else if (v == ibBack) {
            onBackPressed();
        }
    }

    private void register() {
        //START
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        final String businessName = etBusinessName.getText().toString().trim();

        if (bundle_user_type.equals("business owner")) {
            etBusinessName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tilBusinessName.setErrorEnabled(false);
                    tilBusinessName.setError(null);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //TO CHECK IF THE BUSINESS NAME FIELD IS NOT EMPTY
            if (TextUtils.isEmpty(businessName)) {
                tilBusinessName.setErrorEnabled(true);
                tilBusinessName.setError(Html.fromHtml("<font color='#ffffff'>Please enter your business establishment name.</font>"));
            }
        } else {
            tilBusinessName.setErrorEnabled(false);
            tilBusinessName.setError(null);
        }

        if (!email.toLowerCase().contains("@") || !email.toLowerCase().contains(".")) {
            //TO CHECK IF THE EMAIL IS VALID
            etEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tilEmail.setError(null);
                    tilEmail.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            tilEmail.setErrorEnabled(true);
            tilEmail.setError(Html.fromHtml("<font color='#ffffff'>Please use a valid e-mail address.</font>"));
        } else {
            tilEmail.setError(null);
            tilEmail.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            //PANG CHECK KUNG MAY LAMAN LAHAT NG FIELDS
            showFillAllTheFields();
            if (TextUtils.isEmpty(email)) {
                //IF EMAIL IS EMPTY
                etEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        tilEmail.setError(null);
                        tilEmail.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                tilEmail.setErrorEnabled(true);
                tilEmail.setError(Html.fromHtml("<font color='#ffffff'>Please enter your e-mail address.</font>"));
            } else {
                tilEmail.setError(null);
                tilEmail.setErrorEnabled(false);
            }

            if (TextUtils.isEmpty(password)) {
                //IF PASSWORD IS EMPTY
                etPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        tilPassword.setError(null);
                        tilPassword.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                tilPassword.setErrorEnabled(true);
                tilPassword.setError("Please enter your desired password.");
                tilPassword.setError(Html.fromHtml("<font color='#ffffff'>Please enter your desired password.</font>"));
            } else {
                tilPassword.setError(null);
                tilPassword.setErrorEnabled(false);
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                //IF CONFIRM PASSWORD IS EMPTY
                etConfirmPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        tilConfirmPassword.setError(null);
                        tilConfirmPassword.setErrorEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                tilConfirmPassword.setErrorEnabled(true);
                tilConfirmPassword.setError(Html.fromHtml("<font color='#ffffff'>Please confirm your password.</font>"));
            } else {
                tilConfirmPassword.setError(null);
                tilConfirmPassword.setErrorEnabled(false);
            }
            return;
        } else {

            if (password.equals(confirmPassword)) {
                //IF PASSWORD AND CONFIRM PASSWORD MATCHES
                //REGISTER A USER
                if (password.length() <= 5) {
                    //PASSWORD IS LESS THE 6 LETTERS
                    showPasswordMustBeLonger();
                    etPassword.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tilPassword.setError(null);
                            tilPassword.setErrorEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    tilPassword.setErrorEnabled(true);
                    tilPassword.setError(Html.fromHtml("<font color='#ffffff'>Password must be 6 characters or longer.</font>"));
                    return;
                } else {
                    tilPassword.setError(null);
                    tilPassword.setErrorEnabled(false);
                }
                if (bundle_email.equals("")) {
                    progressDialog.setMessage("Registering user...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();

                    //REGISTER WITH THE EMAIL AND PASSWORD PROVIDED
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                //SEND EMAIL VERIFICATION IF SUCCESS
                                sendEmailVerification();

                                //ADD USER TO DATABASE
                                String _UID = mAuth.getCurrentUser().getUid();
                                addUserToDatabase(_UID, email, bundle_user_type, businessName, password);
                                addBusinessToDatabase(_UID, email, businessName);

                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class).putExtra("EMAIL_SENT", "yes").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


                            } else {
                                progressDialog.dismiss();
                                //FAILED REGISTERING
                                showEmailAlreadyRegistered();
                            }
                        }
                    });
                } else {
                    progressDialog.setMessage("Registering user...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();

                    //METHOD WHEN CREATING AN ACCOUNT WITH GMAIL
                    mAuth.getCurrentUser().updatePassword(password);
                    String _UID = mAuth.getCurrentUser().getUid();

                    //ADD USER TO DATABASE
                    addUserToDatabase(_UID, email, bundle_user_type, businessName, password);
                    addBusinessToDatabase(_UID, email, businessName);

                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterActivity.this, MapsActivity.class).putExtra("USER_EMAIL", email).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            } else {
                //PASSWORD DOES NOT MATCH
                showPasswordDoesNotMatch();
            }
        }
        //END
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

    private void addUserToDatabase(String UID, String email, String userType, String businessName, String password) {
        //START
        if (!bundle_user_type.equals("business owner")) {
            businessName = "";
        }
        String guide = "no";
        String encryptPassword = convertPasswordMd5(password);
        User user = new User(UID, userType, email, encryptPassword, guide);

        databaseUsers.child(UID).setValue(user);

        Log.d("ADD_USER_UID", UID);
        Log.d("ADD_USER_EMAIL", email);
        Log.d("ADD_USER_USER_TYPE", userType);
        Log.d("ADD_USER_BUSINESS_NAME", businessName);
        Log.d("ADD_USER_GUIDE", guide);
        //END
    }

    private void addBusinessToDatabase(String UID, String businessOwner, String businessName) {
        //START
        if (!bundle_user_type.equals("business owner")) {
            businessName = "";
        }
        Business business = new Business(UID, businessOwner, businessName);

        databaseBusinesses.child(UID).setValue(business);

        Log.d("ADD_BUSINESS_UID", UID);
        Log.d("ADD_BUSINESS_OWNER", businessOwner);
        Log.d("ADD_BUSINESS_NAME", businessName);
        //END
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
    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mAuth.signOut();
                }
            });
        }
    }

    private void showPasswordDoesNotMatch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_password_does_not_match, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewPasswordDoesNotMatch);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayPasswordDoesNotMatch);
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

    private void showPasswordMustBeLonger() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_password_must_be_longer, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewPasswordMustBeLonger);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayPasswordMustBeLonger);
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

    private void showEmailAlreadyRegistered() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_email_already_registered, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewEmailAlreadyRegistered);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayEmailAlreadyRegistered);
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
