package com.gabrielraniel.envision.wash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseUsers, databaseBusinesses, databaseReports, databaseRestrooms, databaseReviews, databaseRatings, databaseImages;
    private ValueEventListener vel_user;
    private String _USER_EMAIL, _USER_TYPE, _USER_ID, _USER_PASSWORD;
    private ProgressDialog progressDialog;

    //FOR FONTS
    private Typeface typeface_normal;
    private Typeface typeface_bold;

    private TextView tvChangePassword, tvAccountSettings;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etNewPassword, etConfirmNewPassword, etCurrentPassword;
    private ImageButton ibSaveChanges, ibDeleteAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChangePassword);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        _USER_EMAIL = user.getEmail();
        _USER_ID = user.getUid();

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseBusinesses = FirebaseDatabase.getInstance().getReference("businesses");
        databaseRatings = FirebaseDatabase.getInstance().getReference("ratings");
        databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        databaseReviews = FirebaseDatabase.getInstance().getReference("reviews");
        databaseRestrooms = FirebaseDatabase.getInstance().getReference("restrooms");
        databaseImages = FirebaseDatabase.getInstance().getReference("images");

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        tvChangePassword = (TextView) findViewById(R.id.textViewChangePassword);
        tvAccountSettings = (TextView) findViewById(R.id.textViewAccountSettings);
        etCurrentPassword = (TextInputEditText) findViewById(R.id.editTextCurrentPassword);
        etNewPassword = (TextInputEditText) findViewById(R.id.editTextNewPassword);
        etConfirmNewPassword = (TextInputEditText) findViewById(R.id.editTextNewConfirmPassword);
        tilCurrentPassword = (TextInputLayout) findViewById(R.id.textInputLayoutCurrentPassword);
        tilConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        tilNewPassword = (TextInputLayout) findViewById(R.id.textInputLayoutNewPassword);
        ibSaveChanges = (ImageButton) findViewById(R.id.imageButtonSaveChanges);
        ibDeleteAccount = (ImageButton) findViewById(R.id.imageButtonDeleteAccount);

        tvChangePassword.setTypeface(typeface_bold);
        tvAccountSettings.setTypeface(typeface_bold);
        etConfirmNewPassword.setTypeface(typeface_normal);
        etCurrentPassword.setTypeface(typeface_normal);
        etNewPassword.setTypeface(typeface_normal);
        tilNewPassword.setTypeface(typeface_normal);
        tilConfirmPassword.setTypeface(typeface_normal);
        tilCurrentPassword.setTypeface(typeface_normal);
        ibDeleteAccount.setOnClickListener(this);
        ibSaveChanges.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        getCurrentPassword(_USER_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.imageButtonSaveChanges):
                if (isNetworkAvailable() && isLocationAvailable()) {
                    String password = etNewPassword.getText().toString();
                    String confirmPassword = etConfirmNewPassword.getText().toString();
                    String currentPassword = etCurrentPassword.getText().toString();
                    if (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword) || !TextUtils.isEmpty(currentPassword)) {
                        updateUserPassword(password, confirmPassword, currentPassword);
                    } else {
                        showFillAllTheFields();
                        tilCurrentPassword.setErrorEnabled(true);
                        tilCurrentPassword.setError(Html.fromHtml("<font color='#ffffff'>Please enter your current password</font>"));
                        etCurrentPassword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                tilCurrentPassword.setError(null);
                                tilCurrentPassword.setErrorEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        tilNewPassword.setErrorEnabled(true);
                        tilNewPassword.setError(Html.fromHtml("<font color='#ffffff'>Please enter your new desired password</font>"));
                        etNewPassword.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                tilNewPassword.setError(null);
                                tilNewPassword.setErrorEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        tilConfirmPassword.setErrorEnabled(true);
                        tilConfirmPassword.setError(Html.fromHtml("<font color='#ffffff'>Please confirm your new password</font>"));
                        etConfirmNewPassword.addTextChangedListener(new TextWatcher() {
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
                    }
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
            case (R.id.imageButtonDeleteAccount):
                if (isNetworkAvailable() && isLocationAvailable()) {
                    showAreYouSureDeleteAccount(_USER_ID, _USER_PASSWORD);
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateUserPassword(final String password, String confirmPassword, final String currentPassword) {
        Log.d("PASSWORD", password);
        Log.d("CONFIRM_PASSWORD", confirmPassword);
        Log.d("CURR_PASSWORD", currentPassword);

        if (password.length() <= 5) {
            showPasswordMustBeLonger();
            tilCurrentPassword.setErrorEnabled(true);
            tilNewPassword.setError(Html.fromHtml("<font color='#ffffff'>Password must be 6 characters or longer.</font>"));
            etNewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tilNewPassword.setError(null);
                    tilNewPassword.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            return;
        }

        final String encryptedCurrentPassword = convertPasswordMd5(currentPassword);

        if (!encryptedCurrentPassword.equals(_USER_PASSWORD)) {
            showCurrentPasswordIsWrong();
            tilCurrentPassword.setErrorEnabled(true);
            tilCurrentPassword.setError(Html.fromHtml("<font color='#ffffff'>Current password is wrong</font>"));
            etCurrentPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tilCurrentPassword.setError(null);
                    tilCurrentPassword.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            return;
        }

        if (password.equals(confirmPassword) && _USER_PASSWORD.equals(encryptedCurrentPassword)) {
            progressDialog.setMessage("Updating password...");
            progressDialog.show();
            progressDialog.setIndeterminate(true);
            final String newPassword = convertPasswordMd5(password);
            user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    databaseUsers.child(_USER_ID).child("password").setValue(newPassword);
                    showPasswordChanged();
                    _USER_PASSWORD = newPassword;
                }
            });
        } else {
            showPasswordDoesNotMatch();
            tilNewPassword.setErrorEnabled(true);
            tilNewPassword.setError(Html.fromHtml("<font color='#ffffff'>Password does not match</font>"));
            etNewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    tilNewPassword.setError(null);
                    tilNewPassword.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError(Html.fromHtml("<font color='#ffffff'>Password does not match</font>"));
            etConfirmNewPassword.addTextChangedListener(new TextWatcher() {
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
            return;
        }
    }

    private void getCurrentPassword(final String UID) {

        try {
            vel_user = databaseUsers.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    _USER_PASSWORD = dataSnapshot.child("password").getValue().toString();
                    Log.d("CURRENT_PASSWORD", _USER_PASSWORD);
                    databaseUsers.child(UID).removeEventListener(vel_user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ChangePasswordActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAccount(final String UID) {

        databaseBusinesses.child(UID).removeValue();
        databaseRatings.child(UID).removeValue();
        databaseReviews.child(UID).removeValue();
        databaseRestrooms.child(UID).removeValue();
        databaseReports.child(UID).removeValue();
        databaseUsers.child(UID).removeValue();
        databaseImages.child(UID).removeValue();

//        storageRestroomImage.child("images").child("restrooms").child(UID).child();
        user.delete();
        mAuth.signOut();
        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class).putExtra("ACCOUNT_DELETED", "yes").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
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

    //WARNINGS AND PROMPTS

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

    private void showPasswordChanged() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_password_changed, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewPasswordChanged);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayPasswordChanged);
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

    private void showCurrentPasswordIsWrong() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_current_password_is_wrong, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewCurrentPasswordWrong);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayCurrentPasswordWrong);
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

    private void showAreYouSureDeleteAccount(final String UID, final String currentPassword) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_are_you_sure_delete_account, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAreYouSureDeleteAccount);
        ImageButton ibNo = (ImageButton) dialogView.findViewById(R.id.imageButtonNoAreYouSureDeleteAccount);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonYesAreYouSureDeleteAccount);
        final EditText etPassword = (EditText) dialogView.findViewById(R.id.editTextPasswordDeleteAccount);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ibYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    Log.d("DELETE_CURRENT_PASS", etPassword.getText().toString());
                    String encryptedCurrentPassword = convertPasswordMd5(etPassword.getText().toString());
                    if (encryptedCurrentPassword.equals(currentPassword)) {
                        deleteAccount(UID);
                        alertDialog.dismiss();
                    } else {
                        alertDialog.dismiss();
                        showCurrentPasswordIsWrong();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
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

}
