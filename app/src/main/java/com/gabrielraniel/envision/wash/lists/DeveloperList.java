package com.gabrielraniel.envision.wash.lists;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabrielraniel.envision.wash.R;

import java.util.List;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class DeveloperList extends ArrayAdapter<String> {

    private Activity context;
    private List<String> developerList;
    private String[] roleList, linkedIn, facebook, googlePlus;
    private int[] developerImages;

    public DeveloperList(Activity context, List<String> developerList, String[] roleList, int[] developerImages, String[] linkedIn, String[] facebook, String[] googlePlus) {
        super(context, R.layout.list_settings_layout, developerList);
        this.context = context;
        this.developerList = developerList;
        this.roleList = roleList;
        this.developerImages = developerImages;
        this.linkedIn = linkedIn;
        this.googlePlus = googlePlus;
        this.facebook = facebook;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        Typeface typeface_normal = Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_regular.otf");
        Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_bold.otf");

        View listViewItem = inflater.inflate(R.layout.list_developers_layout, null, true);
        TextView tvDeveloperName = (TextView) listViewItem.findViewById(R.id.textViewDeveloperName);
        TextView tvRoleName = (TextView) listViewItem.findViewById(R.id.textViewRoleName);
        tvDeveloperName.setTypeface(typeface_bold);
        tvRoleName.setTypeface(typeface_normal);
        ImageView ivDeveloper = (ImageView) listViewItem.findViewById(R.id.imageViewDeveloper);
        ImageView ivLinkedIn = (ImageView) listViewItem.findViewById(R.id.imageViewLinkedIn);
        ivLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = linkedIn[position];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        ImageView ivFacebook = (ImageView) listViewItem.findViewById(R.id.imageViewFacebook);
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = facebook[position];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        ImageView ivGooglePlus = (ImageView) listViewItem.findViewById(R.id.imageViewGooglePlus);
        ivGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = googlePlus[position];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        ivDeveloper.setImageResource(developerImages[position]);

        tvDeveloperName.setText(developerList.get(position));
        tvRoleName.setText(roleList[position]);
        return listViewItem;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
