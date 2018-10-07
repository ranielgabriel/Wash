package com.gabrielraniel.envision.wash;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.adapters.ViewPagerFullscreenAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageFullscreenActivity extends AppCompatActivity {

    private ViewPager viewPagerFullscreen;
    private LinearLayout sliderDotsFullscreen;
    private int dotsCounts;
    private ImageView[] dots;
    private List<String> imagesEncodedList;
    private ViewPagerFullscreenAdapter viewPagerFullscreenAdapter;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFullscreenImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        imagesEncodedList = new ArrayList<>();

        bundle = getIntent().getExtras();
        if (bundle != null) {
            imagesEncodedList = getIntent().getStringArrayListExtra("IMAGE_ENCODED_LIST");
        }

        viewPagerFullscreen = (ViewPager) findViewById(R.id.viewPagerFullscreen);
        sliderDotsFullscreen = (LinearLayout) findViewById(R.id.sliderDotsFullscreen);

        viewPagerFullscreenAdapter = new ViewPagerFullscreenAdapter(imagesEncodedList, this);
        viewPagerFullscreen.setAdapter(viewPagerFullscreenAdapter);

        dotsCounts = viewPagerFullscreenAdapter.getCount();
        dots = new ImageView[dotsCounts];

        for (int i = 0; i < dotsCounts; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            try {
                sliderDotsFullscreen.addView(dots[i], params);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));

        viewPagerFullscreen.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotsCounts; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
