package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.models.chat.ChatMessageModel;
import com.almabranding.yummi.models.chat.ChatModel;
import com.almabranding.yummi.utils.CircleTransform;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessageModel> {


    Context c;

    public ChatAdapter(Context context, ArrayList<ChatMessageModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatMessageModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (service.getOwnerId().equalsIgnoreCase(MainActivity.userId))
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_me, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_other, parent, false);


        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        tvName.setText(service.getMessage());


        return convertView;
    }
}
