package com.gabrielraniel.envision.wash;

import android.content.Intent;
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

public class AboutActivity extends AppCompatActivity implements ListView.OnItemClickListener {


    private ListView lvAbout;
    private List<String> aboutList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //FIREBASE AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        lvAbout = (ListView) findViewById(R.id.listViewAbout);
        aboutList = new ArrayList<>();
        aboutList.add("Wash Application");
        aboutList.add("Developers");

        SettingList adapter = new SettingList(this, aboutList);
        lvAbout.setAdapter(adapter);
        lvAbout.setOnItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0: {
                startActivity(new Intent(this, WashAppActivity.class));
                break;
            }
            case 1: {
                startActivity(new Intent(this, DevelopersActivity.class));
                break;
            }

        }
    }
}
