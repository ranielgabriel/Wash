package com.gabrielraniel.envision.wash;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.adapters.ViewPagerTutorialAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPagerTutorial;
    private LinearLayout sliderDotsTutorial;
    private int dotsCounts;
    private ImageView[] dots;
    private int imagesOwner[] = {R.drawable.guide_owner_1, R.drawable.guide_owner_2, R.drawable.guide_owner_3, R.drawable.guide_owner_4, R.drawable.guide_owner_5};
    private int imagesRegularUser[] = {R.drawable.guide_user_1, R.drawable.guide_user_2, R.drawable.guide_user_3, R.drawable.guide_user_4};
    private ViewPagerTutorialAdapter viewPagerTutorialAdapter;
    private Bundle bundle;
    private FloatingActionButton fabSkip, fabNext;
    private CoordinatorLayout rootLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //CONNECTORS
        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_tutorial);
        viewPagerTutorial = (ViewPager) findViewById(R.id.viewPagerTutorial);
        fabSkip = (FloatingActionButton) findViewById(R.id.fabSkip);
        fabNext = (FloatingActionButton) findViewById(R.id.fabNext);
        sliderDotsTutorial = (LinearLayout) findViewById(R.id.sliderDotsTutorial);

        //INITIALIZATION
        fabSkip.setOnClickListener(this);
        fabNext.setOnClickListener(this);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            String type = bundle.getString("TYPE");
            if (type.equals("regular user")) {
                viewPagerTutorialAdapter = new ViewPagerTutorialAdapter(imagesRegularUser, this);
                viewPagerTutorial.setAdapter(viewPagerTutorialAdapter);
            } else if (type.equals("business owner")) {
                viewPagerTutorialAdapter = new ViewPagerTutorialAdapter(imagesOwner, this);
                viewPagerTutorial.setAdapter(viewPagerTutorialAdapter);
            }
        }

        dotsCounts = viewPagerTutorialAdapter.getCount() - 1;
        dots = new ImageView[dotsCounts];

        for (int i = 0; i < dotsCounts; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            try {
                sliderDotsTutorial.addView(dots[i], params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));

        viewPagerTutorial.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == viewPagerTutorial.getAdapter().getCount() - 2) {
                    fabNext.setVisibility(View.VISIBLE);
                    fabSkip.setVisibility(View.GONE);

                } else {
                    fabSkip.setVisibility(View.VISIBLE);
                    fabNext.setVisibility(View.GONE);
                }

                if (position == viewPagerTutorial.getAdapter().getCount() - 1) {
                    fabSkip.setVisibility(View.GONE);
                    onBackPressed();
                }
            }

            @Override
            public void onPageSelected(int position) {

                try {
                    for (int i = 0; i < dotsCounts; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == fabSkip) {
            //SKIP FAB
            Snackbar snackbar_skip = Snackbar.make(rootLayout, "Skip this guide?", Snackbar.LENGTH_SHORT);
            snackbar_skip.setAction("SKIP", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            View snackBarViewSkip = snackbar_skip.getView();
            snackBarViewSkip.setBackgroundColor(Color.parseColor("#387889")); // snackbar background color
            snackbar_skip.setActionTextColor(Color.parseColor("#ffffff")); // snackbar action text color
            snackbar_skip.show();
        } else if (view == fabNext) {
            onBackPressed();
        }
    }
}
