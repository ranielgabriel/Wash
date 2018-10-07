package com.gabrielraniel.envision.wash;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gabrielraniel.envision.wash.lists.DeveloperList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DevelopersActivity extends AppCompatActivity {
    private Typeface typeface_normal;
    private Typeface typeface_bold;
    private ListView lvDevelopers;
    private List<String> developerNames;
    private String[] roleNames, linkedIn, facebook, googlePlus;
    private int[] developerImages = {R.drawable.dev_raniel, R.drawable.dev_ian, R.drawable.dev_benneth, R.drawable.dev_alen};
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDevelopers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        lvDevelopers = (ListView) findViewById(R.id.listViewDevelopers);
        developerNames = new ArrayList<>();
        developerNames.add("Raniel R. Gabriel");
        developerNames.add("Christian Laettner E. Garcia");
        developerNames.add("Benneth John A. Fajardo");
        developerNames.add("Alen Paul Madriaga");
        roleNames = getResources().getStringArray(R.array.developer_roles);
        linkedIn = getResources().getStringArray(R.array.linkedin_accounts);
        facebook = getResources().getStringArray(R.array.facebook_accounts);
        googlePlus = getResources().getStringArray(R.array.google_plus);
        DeveloperList adapter = new DeveloperList(this, developerNames, roleNames, developerImages, linkedIn, facebook, googlePlus);
        lvDevelopers.setAdapter(adapter);
        setListViewHeightBasedOnChildren(lvDevelopers);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = ViewGroup.MeasureSpec.makeMeasureSpec(listView.getWidth(), ViewGroup.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight() + 50;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
