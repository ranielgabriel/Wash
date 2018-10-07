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

public class HelpList extends ArrayAdapter<String>{

    private Activity context;
    private List<String> helpList;

    public HelpList(Activity context, List<String> helpList){
        super(context, R.layout.list_help_layout, helpList);
        this.context = context;
        this.helpList = helpList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_help_layout, null, true);
        TextView tvHelpName = (TextView) listViewItem.findViewById(R.id.textViewHelpName);

        tvHelpName.setText(helpList.get(position));

        return listViewItem;
    }
}
