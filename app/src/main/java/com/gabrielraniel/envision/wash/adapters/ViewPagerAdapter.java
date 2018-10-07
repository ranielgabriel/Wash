package com.gabrielraniel.envision.wash.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.ImageFullscreenActivity;
import com.gabrielraniel.envision.wash.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabrielraniel on 05/11/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<String> images;
    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(List<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.viewpager_images_layout, container, false);
        ImageView imageView = (ImageView) item_view.findViewById(R.id.imageViewRestroomImage);
        final ProgressBar progressBar = (ProgressBar) item_view.findViewById(R.id.progressBarCircular);

        try{
            Picasso.with(context)
                    .load(images.get(position))
                    .error(R.mipmap.ic_launcher)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        container.addView(item_view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ImageFullscreenActivity.class);
                i.putStringArrayListExtra("IMAGE_ENCODED_LIST", (ArrayList<String>) images);
                context.startActivity(i);
            }
        });

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
