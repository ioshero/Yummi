package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.GuestModel;
import com.almabranding.yummi.models.Service;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class GuestAdapter extends ArrayAdapter<GuestModel> {


    Context c;

    public GuestAdapter(Context context, ArrayList<GuestModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GuestModel service = getItem(position);

        if (service.getId() == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header_in_app, parent, false);
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            tvName.setText("Guests");
        }else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dark_gray_cell, parent, false);
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            tvName.setText(service.getClient().getStageName());

        }


        return convertView;
    }
}
