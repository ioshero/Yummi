package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.Service;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class SampleAdapter extends ArrayAdapter<Service> {


    Context c;

    public SampleAdapter(Context context, ArrayList<Service> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Service service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

//        if (convertView == null) {
        if (service.getService().getId().isEmpty())
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header_in_app, parent, false);
            else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell_in_app, parent, false);
//        }

        if (service.getAvailability() == 2) {
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            // Populate the data into the template view using the data object
            tvName.setText(service.getService().getName());
            // Return the completed view to render on screen
        }


        return convertView;
    }
}
