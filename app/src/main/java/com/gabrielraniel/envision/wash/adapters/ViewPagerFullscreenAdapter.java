package com.gabrielraniel.envision.wash.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by gabrielraniel on 02/12/2017.
 */

public class ViewPagerFullscreenAdapter extends PagerAdapter {

    private List<String> images;
    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerFullscreenAdapter(List<String> images, Context context) {
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
        View item_view = layoutInflater.inflate(R.layout.viewpager_images_fullscreen_layout, container, false);
        final ImageView imageView = (ImageView) item_view.findViewById(R.id.imageViewRestroomImageFullscreen);
        final ProgressBar progressBar = (ProgressBar) item_view.findViewById(R.id.progressBarCircularFullscreen);
        progressBar.setVisibility(View.VISIBLE);

        try{
            Picasso.with(context)
                    .load(images.get(position))
                    .error(R.mipmap.ic_launcher)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);

                            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
                            photoViewAttacher.update();

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
