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

import java.util.List;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class AboutList extends ArrayAdapter<String>{

    private Activity context;
    private List<String> aboutList;

    public AboutList(Activity context, List<String> settingList){
        super(context, R.layout.list_about_layout, settingList);
        this.context = context;
        this.aboutList = settingList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_about_layout, null, true);
        TextView tvAboutName = (TextView) listViewItem.findViewById(R.id.textViewAboutName);

        tvAboutName.setText(aboutList.get(position));

        return listViewItem;
    }
}
