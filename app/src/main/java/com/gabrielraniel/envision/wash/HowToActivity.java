package com.gabrielraniel.envision.wash;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HowToActivity extends AppCompatActivity {

    private TextView tvHowTo, tvTitle;
    private Typeface typeface_normal;
    private Typeface typeface_bold;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        //FOR FONTS
        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHowTo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //CONNECTOR
        tvHowTo = (TextView) findViewById(R.id.textViewHowTo);
        tvTitle = (TextView) findViewById(R.id.textViewHowToTitle);

        //INITIALIZATION
        tvHowTo.setTypeface(typeface_normal);
        tvTitle.setTypeface(typeface_bold);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            switch (bundle.getString("HOW_TO")) {
                case "SIGN_UP":
                    tvHowTo.setText(getResources().getString(R.string.how_to_sign_up_an_account));
                    this.setTitle(null);
                    tvTitle.setText("Sign Up/Register an Account");
                    break;
                case "LOCATE":
                    tvHowTo.setText(getResources().getString(R.string.how_to_locate_a_restroom));
                    this.setTitle(null);
                    tvTitle.setText("Locate a Restroom");
                    break;
                case "RATE_REVIEW":
                    tvHowTo.setText(getResources().getString(R.string.how_to_rate_review));
                    this.setTitle(null);
                    tvTitle.setText("Rate and Review a Restroom");
                    break;
                case "REPORT":
                    tvHowTo.setText(getResources().getString(R.string.how_to_report));
                    this.setTitle(null);
                    tvTitle.setText("Report a Restroom");
                    break;
                case "ADD_RESTROOM":
                    tvHowTo.setText(getResources().getString(R.string.how_to_add_restroom));
                    this.setTitle(null);
                    tvTitle.setText("Add a Restroom Location");
                    break;
                case "ENABLE_DISABLE":
                    tvHowTo.setText(getResources().getString(R.string.how_to_enable_disable_restroom));
                    this.setTitle(null);
                    tvTitle.setText("Enable/Disable a Restroom");
                    break;
                case "DELETE_RESTROOM":
                    tvHowTo.setText(getResources().getString(R.string.how_to_delete_restroom));
                    this.setTitle(null);
                    tvTitle.setText("Delete a Restroom");
                    break;
                case "CHANGE_PASSWORD":
                    tvHowTo.setText(getResources().getString(R.string.how_to_change_password));
                    this.setTitle(null);
                    tvTitle.setText("Change the Account Password");
                    break;
                case "CHANGE_BUSINESS_NAME":
                    tvHowTo.setText(getResources().getString(R.string.how_to_change_business_name));
                    this.setTitle(null);
                    tvTitle.setText("Change the Business Name");
                    break;
                case "DELETE_ACCOUNT":
                    tvHowTo.setText(getResources().getString(R.string.how_delete_an_account));
                    this.setTitle(null);
                    tvTitle.setText("Delete an Account");
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
