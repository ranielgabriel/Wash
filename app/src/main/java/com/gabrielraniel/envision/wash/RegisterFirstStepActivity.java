package com.gabrielraniel.envision.wash;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFirstStepActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private TextView tvStepOne, tvSignUp;
    private RadioGroup rgUserType;
    private RadioButton rbBusinessOwner, rbRegularUser;
    private ImageButton ibBack, ibNext, ibBusinessOwnerHelp, ibRegularUserHelp;
    private Typeface typeface_normal, typeface_bold;
    private Bundle bundle;
    private String bundle_email = "";
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first_step);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //CONNECTORS
        tvStepOne = (TextView) findViewById(R.id.textViewStepOne);
        tvSignUp = (TextView) findViewById(R.id.textViewSignUp);
        rgUserType = (RadioGroup) findViewById(R.id.radioGroupUserType);
        rbBusinessOwner = (RadioButton) findViewById(R.id.radioButtonBusinessOwner);
        rbRegularUser = (RadioButton) findViewById(R.id.radioButtonRegularUser);
        ibBack = (ImageButton) findViewById(R.id.imageButtonBack);
        ibNext = (ImageButton) findViewById(R.id.imageButtonNext);
        ibBusinessOwnerHelp = (ImageButton) findViewById(R.id.imageButtonBusinessOwnerHelp);
        ibRegularUserHelp = (ImageButton) findViewById(R.id.imageButtonRegularUserHelp);

        //INITIALIZATION OF VIEW
        tvStepOne.setTypeface(typeface_bold);
        tvSignUp.setTypeface(typeface_bold);
        rbRegularUser.setTypeface(typeface_normal);
        rbBusinessOwner.setTypeface(typeface_normal);

        //INITIALIZATION
        ibBack.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibBusinessOwnerHelp.setOnClickListener(this);
        ibRegularUserHelp.setOnClickListener(this);
        rgUserType.setOnCheckedChangeListener(this);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            String gmail = bundle.getString("GMAIL");
            String fb = bundle.getString("FACEBOOK");
            if (gmail.equals("yes") || fb.equals("yes")) {
                bundle_email = bundle.getString("USER_EMAIL");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonBack:
                finish();
                onBackPressed();
                break;
            case R.id.imageButtonNext:
                if (rgUserType.getCheckedRadioButtonId() == R.id.radioButtonRegularUser) {
                    Intent i = new Intent(this, RegisterActivity.class);
                    i.putExtra("USER_TYPE", "regular user");
                    i.putExtra("USER_EMAIL", bundle_email);
                    startActivity(i);
                } else if (rgUserType.getCheckedRadioButtonId() == R.id.radioButtonBusinessOwner) {
                    Intent i = new Intent(this, RegisterActivity.class);
                    i.putExtra("USER_TYPE", "business owner");
                    i.putExtra("USER_EMAIL", bundle_email);
                    startActivity(i);
                } else {
                    chooseAUserType();
                }
                break;
            case R.id.imageButtonBusinessOwnerHelp:
                showBusinessOwnerHelp();
                break;
            case R.id.imageButtonRegularUserHelp:
                showRegularUserHelp();
                break;

        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    //WARNING AND PROMPTS

    private void showBusinessOwnerHelp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_business_owner_help, null);

        dialogBuilder.setView(dialogView);

        TextView tvBusinessOwner = (TextView) dialogView.findViewById(R.id.textViewBusinessEstablishmentOwner);
        TextView tvBusinessOwnerDescription = (TextView) dialogView.findViewById(R.id.textViewBusinessEstablishmentOwnerDescription);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayBusinessOwner);
        tvBusinessOwner.setTypeface(typeface_bold);
        tvBusinessOwnerDescription.setTypeface(typeface_normal);

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

    private void showRegularUserHelp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_regular_user_help, null);

        dialogBuilder.setView(dialogView);

        TextView tvBusinessOwner = (TextView) dialogView.findViewById(R.id.textViewRegularUser);
        TextView tvBusinessOwnerDescription = (TextView) dialogView.findViewById(R.id.textViewRegularUserDescription);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayRegularUser);
        tvBusinessOwner.setTypeface(typeface_bold);
        tvBusinessOwnerDescription.setTypeface(typeface_normal);

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

    private void chooseAUserType() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_choose_a_user_type, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewChooseAUserType);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayChooseAUserType);
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
