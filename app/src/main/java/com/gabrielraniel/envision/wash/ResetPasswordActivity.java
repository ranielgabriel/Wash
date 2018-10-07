package com.gabrielraniel.envision.wash;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    //FOR FONTS
    private Typeface typeface_normal;
    private Typeface typeface_bold;

    private TextView tvResetPassword;
    private TextInputEditText etEmail;
    private TextInputLayout tilEmail;
    private ImageButton ibResetPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarResetPassword);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        tvResetPassword = (TextView) findViewById(R.id.textViewResetPassword);
        etEmail = (TextInputEditText) findViewById(R.id.editTextEmailResetPassword);
        tilEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmailResetPassword);
        ibResetPassword = (ImageButton) findViewById(R.id.imageButtonResetPassword);

        tilEmail.setTypeface(typeface_normal);
        etEmail.setTypeface(typeface_normal);
        tvResetPassword.setTypeface(typeface_bold);

        ibResetPassword.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == ibResetPassword) {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
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
                tilEmail.setError(Html.fromHtml("<font color='#ffffff'>Please enter your email.</font>"));
                return;
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

                return;
            } else {
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showResetPasswordEmailHasSent();
                    }
                });
            }
        }
    }


    private void showResetPasswordEmailHasSent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_password_reset_email_sent, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewPasswordResetEmail);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayPasswordResetEmail);
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

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onBackPressed();
            }
        });
    }
}
