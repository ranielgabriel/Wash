package com.gabrielraniel.envision.wash.lists;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabrielraniel.envision.wash.R;
import com.gabrielraniel.envision.wash.models.Review;

import java.util.List;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class ReviewList extends ArrayAdapter<Review> {
    private Activity context;
    private List<Review> reviews;

    public ReviewList(Activity context, List<Review> reviews) {
        super(context, R.layout.list_review_layout, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Nullable
    @Override
    public Review getItem(int position) {
        return super.getItem(getCount() - position - 1);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        Typeface typeface_normal = Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_regular.otf");
        Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_bold.otf");

        View listViewItem = inflater.inflate(R.layout.list_review_layout, null, true);
        TextView tvUsername = (TextView) listViewItem.findViewById(R.id.textViewUsername);
        TextView tvReview = (TextView) listViewItem.findViewById(R.id.textViewReviews);
        TextView tvRating = (TextView) listViewItem.findViewById(R.id.textViewReviewRating);
        TextView tvDateTime = (TextView) listViewItem.findViewById(R.id.textViewDateTime);

        tvUsername.setTypeface(typeface_bold);
        tvDateTime.setTypeface(typeface_normal);
        tvReview.setTypeface(typeface_normal);
        tvRating.setTypeface(typeface_bold);

        Review review = getItem(position);
        tvUsername.setText(review.getUser());
        tvReview.setText(review.getReview());
        tvRating.setText(String.valueOf(review.getUser_rating()));
        tvDateTime.setText(review.getDate_time());

        return listViewItem;
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
