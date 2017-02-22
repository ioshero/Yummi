package com.almabranding.yummi.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.fragments.PerformerListFragment;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.services.ServiceModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioshero on 19/04/16.
 */
public class PerformersArrayAdapter extends ArrayAdapter<PerformerListModel>

{


    public PerformersArrayAdapter(Context context, List<PerformerListModel> service) {
        super(context, 0, service);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PerformerListModel performerModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.performers_cell, parent, false);


        TextView tvName = (TextView) convertView.findViewById(R.id.textView7);
        TextView location = (TextView) convertView.findViewById(R.id.textView888location);

        if (performerModel.getDistance() > 0) {

            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("Distance", false)) {
                location.setText(String.format("less than %.1f km", performerModel.getDistance() / 1000.0));
            } else
                location.setText(String.format("less than %.1f mile", (performerModel.getDistance() / 1000.0)*0.621371));
        } else
            location.setVisibility(View.INVISIBLE);


        // Populate the data into the template view using the data object
        tvName.setText(performerModel.getName());
        if (performerModel.getCover() != null)
            Picasso.with(getContext()).load(performerModel.getCover().getFirstImagePath()).into((ImageView) convertView.findViewById(R.id.performer_iw));


        return convertView;
    }
}
