package com.gabrielraniel.envision.wash;

import android.content.Context;
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

public class ChangeBusinessEstablishmentNameActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference databaseUsers, databaseBusinesses, databaseReports, databaseRestrooms, databaseReviews, databaseRatings, databaseImages;
    private StorageReference storageRestroomImage;
    private ValueEventListener vel_business, vel_users, vel_reports, vel_restrooms, vel_reviews, vel_ratings;

    private String _USER_ID, _USER_BUSINESS_NAME, _USER_PASSWORD;

    //FOR FONTS
    private Typeface typeface_normal;
    private Typeface typeface_bold;

    private TextInputEditText etNewBusinessName, etCurrentPassword;
    private TextInputLayout tilNewBusinessName, tilCurrentPassword;
    private ImageButton ibSaveChanges;
    private TextView tvChangeBusinessName, tvBusinessSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_business_establishment_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChangeBusinessName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseBusinesses = FirebaseDatabase.getInstance().getReference("businesses");
        databaseRatings = FirebaseDatabase.getInstance().getReference("ratings");
        databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        databaseReviews = FirebaseDatabase.getInstance().getReference("reviews");
        databaseRestrooms = FirebaseDatabase.getInstance().getReference("restrooms");
        databaseImages = FirebaseDatabase.getInstance().getReference("images");
        //STORAGE REFERENCE
        storageRestroomImage = FirebaseStorage.getInstance().getReference();

        etNewBusinessName = (TextInputEditText) findViewById(R.id.editTextNewBusinessname);
        etCurrentPassword = (TextInputEditText) findViewById(R.id.editTextCurrentPassword);
        tilCurrentPassword = (TextInputLayout) findViewById(R.id.textInputLayoutCurrentPassword);
        tilNewBusinessName = (TextInputLayout) findViewById(R.id.textInputLayoutNewBusinessname);
        ibSaveChanges = (ImageButton) findViewById(R.id.imageButtonSaveChangesChangeBusinessName);
        ibSaveChanges.setOnClickListener(this);
        tvChangeBusinessName = (TextView) findViewById(R.id.textViewChangeBusinessName);
        tvBusinessSettings = (TextView) findViewById(R.id.textViewBusinessSettings);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        etCurrentPassword.setTypeface(typeface_normal);
        etNewBusinessName.setTypeface(typeface_normal);
        tilNewBusinessName.setTypeface(typeface_normal);
        tilCurrentPassword.setTypeface(typeface_normal);
        tvChangeBusinessName.setTypeface(typeface_bold);
        tvBusinessSettings.setTypeface(typeface_bold);

        _USER_ID = user.getUid();
        getBusinessName(_USER_ID);
        getCurrentPassword(_USER_ID);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.imageButtonSaveChangesChangeBusinessName):
                if (isNetworkAvailable() && isLocationAvailable()) {
                    String currentPassword = etCurrentPassword.getText().toString().trim();
                    String newBusinessName = etNewBusinessName.getText().toString().trim();
                    if (!TextUtils.isEmpty(newBusinessName) && !TextUtils.isEmpty(currentPassword)) {
                        updateUserCompanyName(_USER_ID, newBusinessName, currentPassword);
                    } else {
                        showFillAllTheFields();
                        tilNewBusinessName.setErrorEnabled(true);
                        tilNewBusinessName.setError(Html.fromHtml("<font color='#ffffff'>You need to enter your business establishment name.</font>"));
                        etNewBusinessName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                tilNewBusinessName.setError(null);
                                tilNewBusinessName.setErrorEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        tilCurrentPassword.setErrorEnabled(true);
                        tilCurrentPassword.setError(Html.fromHtml("<font color='#ffffff'>Please enter your current password.</font>"));
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

                    }
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateUserCompanyName(final String UID, final String newBusinessName, final String currentPassword) {
        String encryptedCurrentPassword = convertPasswordMd5(currentPassword);

        if (!_USER_PASSWORD.equals(encryptedCurrentPassword)) {
            showCurrentPasswordIsWrong();
            tilCurrentPassword.setErrorEnabled(true);
            tilCurrentPassword.setError(Html.fromHtml("<font color='#ffffff'>Current password is wrong.</font>"));
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

        if (!newBusinessName.equals(_USER_BUSINESS_NAME) && _USER_PASSWORD.equals(encryptedCurrentPassword)) {

            vel_restrooms = databaseRestrooms.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot restroomSnapshot : dataSnapshot.getChildren()) {
                        String _KEY = restroomSnapshot.getKey();
//                        Log.d("SETTINGS_KEY_RESTROOM",databaseRestrooms.child(UID).child(_KEY)+"");
                        databaseRestrooms.child(UID).child(_KEY).child("restroom_name").setValue(newBusinessName);

                    }
                    databaseRestrooms.child(UID).removeEventListener(vel_restrooms);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            vel_reviews = databaseReviews.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        final String _KEY_FIRST = reviewSnapshot.getKey();
                        databaseReviews.child(UID).child(_KEY_FIRST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                    String _KEY_SECOND = reviewSnapshot.getKey();
//                                    Log.d("SETTINGS_KEY_REVIEW",databaseReviews.child(UID).child(_KEY_FIRST).child(_KEY_SECOND)+"");
                                    databaseReviews.child(UID).child(_KEY_FIRST).child(_KEY_SECOND).child("restroom_name").setValue(newBusinessName);

                                }
                                databaseReviews.child(UID).removeEventListener(vel_reviews);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            vel_ratings = databaseRatings.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        final String _KEY_FIRST = reviewSnapshot.getKey();
                        databaseRatings.child(UID).child(_KEY_FIRST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                    String _KEY_SECOND = reviewSnapshot.getKey();
//                                    Log.d("SETTINGS_KEY_RATINGS",databaseRatings.child(UID).child(_KEY_FIRST).child(_KEY_SECOND)+"");
                                    databaseRatings.child(UID).child(_KEY_FIRST).child(_KEY_SECOND).child("restroom_name").setValue(newBusinessName);
                                }
                                databaseRatings.child(UID).removeEventListener(vel_ratings);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            vel_reports = databaseReports.child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        final String _KEY_FIRST = reviewSnapshot.getKey();
                        databaseReports.child(UID).child(_KEY_FIRST).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                                    String _KEY_SECOND = reviewSnapshot.getKey();
//                                    Log.d("SETTINGS_KEY_REPORT",databaseReports.child(UID).child(_KEY_FIRST).child(_KEY_SECOND)+"");
                                    databaseReports.child(UID).child(_KEY_FIRST).child(_KEY_SECOND).child("restroom_name").setValue(newBusinessName);
                                }
                                databaseReports.child(UID).removeEventListener(vel_reports);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseBusinesses.child(UID).child("business_name").setValue(newBusinessName);
            _USER_BUSINESS_NAME = newBusinessName;
            showBusinessNameChanged();
        }
    }

    private void getBusinessName(final String UID) {
        vel_business = databaseBusinesses.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _USER_BUSINESS_NAME = dataSnapshot.child("business_name").getValue().toString();
                etNewBusinessName.setText(_USER_BUSINESS_NAME);
                databaseBusinesses.child(UID).removeEventListener(vel_business);
                Log.d("SETTINGS_BUSINESS_NAME", _USER_BUSINESS_NAME);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showBusinessNameChanged() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_business_name_changed, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewBusinessNameChanged);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayBusinessNameChanged);
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

    private void getCurrentPassword(final String UID) {
        vel_users = databaseUsers.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _USER_PASSWORD = dataSnapshot.child("password").getValue().toString();
                Log.d("CURRENT_PASSWORD", _USER_PASSWORD);
                databaseUsers.child(UID).removeEventListener(vel_users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
