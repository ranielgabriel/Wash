package com.gabrielraniel.envision.wash.lists;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabrielraniel.envision.wash.R;
import com.gabrielraniel.envision.wash.models.Rating;

import java.util.List;

/**
 * Created by gabrielraniel on 12/10/2017.
 */

public class RatingList extends ArrayAdapter<Rating> {
    private Activity context;
    private List<Rating> ratings;

    public RatingList(Activity context, List<Rating> ratings) {
        super(context, R.layout.list_rating_layout, ratings);
        this.context = context;
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_rating_layout, null, true);
        TextView textViewUsername = (TextView) listViewItem.findViewById(R.id.textViewUsername);
        TextView textViewReview = (TextView) listViewItem.findViewById(R.id.textViewRating);

        Rating rating = ratings.get(position);
        textViewUsername.setText(rating.getUser());
        textViewReview.setText(String.valueOf(rating.getUser_rating()));

        return listViewItem;
    }
}
