package com.gabrielraniel.envision.wash;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gabrielraniel.envision.wash.lists.HelpList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    private ListView lvHelp;
    private TextView tvLabelHowTo;
    private List<String> listHelp;
    private Typeface typeface_normal;
    private Typeface typeface_bold;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHelp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //CONNECTOR
        tvLabelHowTo = (TextView) findViewById(R.id.textViewLabelHowTo);

        //INITIALIZATION
        tvLabelHowTo.setTypeface(typeface_bold);

        //ARRAY LIST
        lvHelp = (ListView) findViewById(R.id.listViewHelp);
        listHelp = new ArrayList<>();
        listHelp.clear();
        listHelp.add("Sign Up/Register an Account");
        listHelp.add("Locate a Restroom");
        listHelp.add("Rate and Review a Restroom");
        listHelp.add("Report a Restroom Location");
        listHelp.add("Add a Restroom Location");
        listHelp.add("Enable/Disable a Restroom Location");
        listHelp.add("Delete a Restroom Location");
        listHelp.add("Change the Account Password");
        listHelp.add("Change the Business Name");
        listHelp.add("Delete an Account");

        HelpList adapter = new HelpList(this, listHelp);
        lvHelp.setAdapter(adapter);
        lvHelp.setOnItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //CLICKING ON LIST ITEM
        switch (i) {
            case 0:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "SIGN_UP"));
                break;
            case 1:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "LOCATE"));
                break;
            case 2:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "RATE_REVIEW"));

                break;
            case 3:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "REPORT"));

                break;
            case 4:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "ADD_RESTROOM"));

                break;
            case 5:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "ENABLE_DISABLE"));

                break;
            case 6:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "DELETE_RESTROOM"));

                break;
            case 7:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "CHANGE_PASSWORD"));

                break;
            case 8:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "CHANGE_BUSINESS_NAME"));

                break;
            case 9:
                startActivity(new Intent(this, HowToActivity.class).putExtra("HOW_TO", "DELETE_ACCOUNT"));

                break;
        }
    }
}
