package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class NotificationsAdapter extends ArrayAdapter<NotificationModel> {


    Context c;

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NotificationModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notifications_cell, parent, false);
        }


        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);

        TextView date = (TextView) convertView.findViewById(R.id.textView1);

        date.setText(service.getCreatedAtString());
        // Populate the data into the template view using the data object
        if (service.getType().equalsIgnoreCase("eventInvitation")) {
            tvName.setText("You has been invited to event " + service.getEventName());
        } else if (service.getType().equalsIgnoreCase("newChat")) {
            tvName.setText("You received chat request from " + service.getClientName());
        } else if (service.getType().equalsIgnoreCase("imageRequest")) {
            tvName.setText("You received image request from " + service.getClientName());
        }else if (service.getType().equalsIgnoreCase("newImageRequest")) {
            tvName.setText("You received image request from " + service.getClientName());
        }else if (service.getType().equalsIgnoreCase("eventIsReadyToStart")) {
            tvName.setText(service.getEventName() + " event is ready to start");
        }else if (service.getType().equalsIgnoreCase("eventPaid")) {
            tvName.setText("The event was paid");
        }else if (service.getType().equalsIgnoreCase("performerAccepted")) {
            tvName.setText("Performer accept invitation for event " + service.getEventName());
        }else if (service.getType().contains("tip")) {
            tvName.setText(YummiUtils.getNotification(service.getType()).replace("%@", service.getClientName()));
        }else if (service.getType().contains("event")) {
            tvName.setText(YummiUtils.getNotification(service.getType()).replace("%@", service.getEventName()));
        }else{
            tvName.setText(YummiUtils.getNotification(service.getType()));
        }

//        tvName.setText(service.getType());

        // Return the completed view to render on screen


        return convertView;
    }
}
