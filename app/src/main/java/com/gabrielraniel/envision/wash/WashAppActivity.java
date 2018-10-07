package com.gabrielraniel.envision.wash;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WashAppActivity extends AppCompatActivity {
    private TextView tvAboutWash;

    private Typeface typeface_normal;
    private Typeface typeface_bold;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_app);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");
        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWashApp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tvAboutWash = (TextView) findViewById(R.id.textViewAboutWash);
        tvAboutWash.setTypeface(typeface_normal);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
