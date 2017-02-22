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
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.models.chat.ChatModel;
import com.almabranding.yummi.utils.CircleTransform;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class ChatsAdapter extends ArrayAdapter<ChatModel> {


    Context c;

    public ChatsAdapter(Context context, ArrayList<ChatModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }


    @Override
    public int getItemViewType(int position) {

        if ( getItem(position).getStatus() == 6) {
            return 0;
        } else
            return 1;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chats_cell, parent, false);
        }


        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        ImageView ivName = (ImageView) convertView.findViewById(R.id.imageView4);

        TextView message_count = (TextView) convertView.findViewById(R.id.message_count);
        if (service.getUnreaded() > 0) {
            message_count.setVisibility(View.VISIBLE);
            message_count.setText(String.valueOf(service.getUnreaded()));
        }else{
            message_count.setVisibility(View.GONE);
        }

       if (YummiUtils.isPreformer(c))
            tvName.setText(service.getClientName());
        else
           tvName.setText(service.getPerformerName());

        Picasso.with(c).load(service.getCover()).transform(new CircleTransform()).into(ivName);

        if (service.getStatus() == 2 || service.getStatus() == 6){
            ivName.setAlpha(0.5f);
            tvName.setAlpha(0.5f);
        }else{
            ivName.setAlpha(1.0f);
            tvName.setAlpha(1.0f);
        }


        ((TextView)convertView.findViewById(R.id.message_status)).setText(YummiUtils.getStatusDescriptionChats(service.getStatus()));
        ((TextView)convertView.findViewById(R.id.message_status)).setBackgroundResource(YummiUtils.getStatusColorChats(service.getStatus()));
        return convertView;
    }
}
