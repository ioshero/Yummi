package com.almabranding.yummi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.utils.CircleImageView;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class LeftSideArrayAdapter extends ArrayAdapter<String> {


    Context c;

    public LeftSideArrayAdapter(Context context, ArrayList<String> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String title = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_cell, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.text_view_menu);
        CircleImageView ciw = (CircleImageView) convertView.findViewById(R.id.imageView2);
        if (YummiUtils.isPreformer(getContext()))
            switch (position) {
                case 0: {
                    ciw.setImageResource(R.mipmap.menu_icon_profile);
                    break;
                }
                case 1: {
                    ciw.setImageResource(R.mipmap.menu_icon_preview);
                    break;
                }

                case 2: {
                    ciw.setImageResource(R.mipmap.menu_icon_notification);
                    break;
                }

                case 3: {
                    ciw.setImageResource(R.mipmap.menu_icon_events);
                    break;
                }

                case 4: {
                    ciw.setImageResource(R.mipmap.menu_icon_credit);
                    ciw.setFillColor(Color.GRAY);
                    break;
                }

                case 5: {
                    ciw.setImageResource(R.mipmap.menu_icon_settings);
                    break;
                }

                case 6: {
                    ciw.setImageResource(R.mipmap.menu_icon_help);
                    ciw.setFillColor(Color.GRAY);
                    break;
                }

                case 7: {
                    ciw.setImageResource(R.mipmap.menu_icon_call);
                    break;
                }

                case 8: {
                    ciw.setImageResource(R.mipmap.menu_icon_logout);
                    break;
                }
            }
        else
            switch (position) {
                case 0: {
                    ciw.setImageResource(R.mipmap.menu_icon_profile);
                    break;
                }

                case 1: {
                    ciw.setImageResource(R.mipmap.menu_icon_notification);
                    break;
                }

                case 2: {
                    ciw.setImageResource(R.mipmap.menu_icon_search);
                    break;
                }

                case 3: {
                    ciw.setImageResource(R.mipmap.menu_icon_media);
                    break;
                }

                case 4: {
                    ciw.setImageResource(R.mipmap.menu_icon_events);
                    break;
                }

                case 5: {
                    ciw.setImageResource(R.mipmap.menu_icon_credit);
                    break;
                }

                case 6: {
                    ciw.setImageResource(R.mipmap.menu_icon_promo);
                    ciw.setFillColor(Color.GRAY);
                    break;
                }

                case 7: {
                    ciw.setImageResource(R.mipmap.menu_icon_settings);
                    break;
                }


                case 8: {
                    ciw.setImageResource(R.mipmap.menu_icon_help);
                    ciw.setFillColor(Color.GRAY);
                    break;
                }


                case 9: {
                    ciw.setImageResource(R.mipmap.menu_icon_call);
                    break;
                }

                case 10: {
                    ciw.setImageResource(R.mipmap.menu_icon_logout);
                    break;
                }


                default: {

                    break;
                }
            }

        // Populate the data into the template view using the data object
        tvName.setText(title);

        return convertView;

    }
}
