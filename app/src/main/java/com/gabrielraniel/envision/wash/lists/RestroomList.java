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
import com.gabrielraniel.envision.wash.models.Restroom;

import java.util.List;

/**
 * Created by gabrielraniel on 10/10/2017.
 */

public class RestroomList extends ArrayAdapter<Restroom>{

    private Activity context;
    private List<Restroom> restroomList;

    public RestroomList(Activity context, List<Restroom> restroomList){
        super(context, R.layout.list_layout, restroomList);
        this.context = context;
        this.restroomList = restroomList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewRestroomName = (TextView) listViewItem.findViewById(R.id.textViewRestroomName);

        Restroom restroom = restroomList.get(position);
        textViewRestroomName.setText(restroom.getRestroom_name());

        return listViewItem;
    }
}
