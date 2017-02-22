package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class EventsAdapter extends ArrayAdapter<EventListModel> {


    Context c;

    public EventsAdapter(Context context, ArrayList<EventListModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 3;
    }


    @Override
    public int getItemViewType(int position) {

        if (YummiUtils.isPreformer(getContext()))
            return 1;

        EventListModel service = getItem(position);

        if (service.getStatus() == 0) {
            return 0;
        } else if (service.getStatus() == 6 || service.getStatus() == 9)
            return 2;
        else
            return 1;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        EventListModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.events_cell, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        tvName.setText(service.getName());

        TextView tvperformers = (TextView) convertView.findViewById(R.id.textView19);
        tvperformers.setText("Performers: " + String.valueOf(service.getNumPerformers()));

        TextView tvnotifications = (TextView) convertView.findViewById(R.id.textView18);
        tvnotifications.setText("Services: " + String.valueOf(service.getNumServices()));

        TextView tvguests = (TextView) convertView.findViewById(R.id.textView17);
        tvguests.setText("Guests: " + String.valueOf(service.getNumClients()));

        TextView tvstart = (TextView) convertView.findViewById(R.id.textView16);
        tvstart.setText("Start: " + service.getstartAtDate());


        if (service.getCover() != null)
            Picasso.with(getContext()).load(service.getCover().getFile().getSrc()).into((ImageView) convertView.findViewById(R.id.imageView4));
        else {
            ((ImageView) convertView.findViewById(R.id.imageView4)).setImageBitmap(null);
        }


        ((TextView) convertView.findViewById(R.id.message_status)).setText(YummiUtils.getStatusDescription(service.getStatus()));
        ((TextView) convertView.findViewById(R.id.message_status)).setBackgroundResource(YummiUtils.getStatusBackground(service.getStatus()));

        return convertView;
    }
}
