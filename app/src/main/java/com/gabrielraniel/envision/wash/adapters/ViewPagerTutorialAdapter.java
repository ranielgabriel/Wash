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

public class ViewPagerTutorialAdapter extends PagerAdapter {

    private int[] images;
    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerTutorialAdapter(int[] images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length+1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.viewpager_tutorial_images_layout, container, false);
        final ImageView imageView = (ImageView) item_view.findViewById(R.id.imageViewTutorial);

//        imageView.setImageResource(images[position]);
        try{
            Picasso.with(context)
                    .load(images[position])
                    .error(R.mipmap.ic_launcher)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
//                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
