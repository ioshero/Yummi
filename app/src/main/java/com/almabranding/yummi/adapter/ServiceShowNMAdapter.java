package com.almabranding.yummi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.models.Service;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class ServiceShowNMAdapter extends ArrayAdapter<Service> {


    MainActivity c;

    public ServiceShowNMAdapter(MainActivity context, ArrayList<Service> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Service service = getItem(position);


        if (service.getService().getId().isEmpty())
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header_in_app, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell_in_app, parent, false);


        if (service.getAvailability() == 2) {
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            if (service.getService().getId().isEmpty())
                tvName.setText(service.getService().getName());

            final TextView duartion = (TextView) convertView.findViewById(R.id.textView20);
            if (duartion != null) {
                duartion.setText(service.getStringDuration());
                final TextView time_unit = (TextView) convertView.findViewById(R.id.textView21);
                final ImageView minus = (ImageView) convertView.findViewById(R.id.imageView7);
                final ImageView plus = (ImageView) convertView.findViewById(R.id.imageView6);
                final ImageView mark = (ImageView) convertView.findViewById(R.id.imageView5);

                time_unit.setVisibility(View.VISIBLE);
                minus.setVisibility(View.INVISIBLE);
                plus.setVisibility(View.INVISIBLE);
                mark.setVisibility(View.INVISIBLE);
                duartion.setVisibility(View.VISIBLE);
            }


        }


        return convertView;
    }
}
