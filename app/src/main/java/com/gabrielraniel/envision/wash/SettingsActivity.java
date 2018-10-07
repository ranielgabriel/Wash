package com.gabrielraniel.envision.wash;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gabrielraniel.envision.wash.lists.SettingList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private FirebaseAuth mAuth;

    private ListView lvSettings;
    private List<String> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();

        //CONNECTORS
        lvSettings = (ListView) findViewById(R.id.listViewSettings);

        //ARRAY LIST
        settingsList = new ArrayList<>();
        if (mAuth.getCurrentUser().isAnonymous()) {
            settingsList.clear();
            settingsList.add("Application Settings");
        } else {
            settingsList.clear();
            settingsList.add("Account Settings");
            settingsList.add("Business Establishment Settings");
            settingsList.add("Application Settings");
        }

        SettingList adapter = new SettingList(this, settingsList);
        lvSettings.setAdapter(adapter);
        lvSettings.setOnItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //CLICKING A LIST ITEM
        switch (adapterView.getItemAtPosition(i).toString()) {
            case "Account Settings":
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case "Business Establishment Settings":
                startActivity(new Intent(this, ChangeBusinessEstablishmentNameActivity.class));
                break;
            case "Application Settings":
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                break;
        }
    }
}
