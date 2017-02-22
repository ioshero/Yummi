package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class ServiceArrayAdapter extends ArrayAdapter<ServiceModel> {


    Context c;

    public ServiceArrayAdapter(Context context, ArrayList<ServiceModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final ServiceModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (service.getId().isEmpty()) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header, parent, false);
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            // Populate the data into the template view using the data object
            tvName.setText(service.getName());

            return convertView;
        } else {
            final int servicePosition = WelcomeActivity.getServicePosition(service.getId());
            try {
                if (position > 1) {
                    if (!getItem(position - 1).getId().isEmpty()) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell, parent, false);
                    } else
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell, parent, false);
                } else
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell, parent, false);

            } catch (Exception e) {
            }

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_cell, parent, false);
            }

            try {
                // Lookup view for data population
                TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
                // Populate the data into the template view using the data object
                tvName.setText(service.getName());
                // Return the completed view to render on screen


                final TextView gender_a = ((TextView) convertView.findViewById(R.id.choice_a));
                final TextView gender_b = ((TextView) convertView.findViewById(R.id.choice_b));
                final TextView gender_c = ((TextView) convertView.findViewById(R.id.choice_c));

                switch (WelcomeActivity.services.get(servicePosition).getAvailability()) {
                    case 0: {
                        gender_a.setBackgroundResource(R.drawable.left_on_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.whiteColor));

                        gender_b.setBackgroundResource(R.drawable.mid_off_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_c.setBackgroundResource(R.drawable.right_off_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.blackColor));
                        break;
                    }
                    case 1: {
                        gender_a.setBackgroundResource(R.drawable.left_off_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_b.setBackgroundResource(R.drawable.mid_on_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.whiteColor));

                        gender_c.setBackgroundResource(R.drawable.right_off_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.blackColor));
                        break;
                    }
                    case 2: {
                        gender_a.setBackgroundResource(R.drawable.left_off_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_b.setBackgroundResource(R.drawable.mid_off_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_c.setBackgroundResource(R.drawable.right_on_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.whiteColor));
                        break;
                    }
                    default: {
                        gender_a.setBackgroundResource(R.drawable.left_on_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.whiteColor));

                        gender_b.setBackgroundResource(R.drawable.mid_off_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_c.setBackgroundResource(R.drawable.right_off_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.blackColor));
                        break;
                    }

                }


                gender_a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        WelcomeActivity.services.get(servicePosition).setAvailability(0);
                        int i = 0;
                        for (ServicePushModel spm : WelcomeActivity.services) {
                            if (spm.getServiceId().equalsIgnoreCase(service.getId())) {
                                WelcomeActivity.services.get(i).setAvailability(0);
                            }
                            i++;
                        }
                        gender_a.setBackgroundResource(R.drawable.left_on_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.whiteColor));

                        gender_b.setBackgroundResource(R.drawable.mid_off_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_c.setBackgroundResource(R.drawable.right_off_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.blackColor));
                    }
                });

                gender_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //                        WelcomeActivity.services.get(servicePosition).setAvailability(1);
                        int i = 0;
                        for (ServicePushModel spm : WelcomeActivity.services) {
                            if (spm.getServiceId().equalsIgnoreCase(service.getId())) {
                                WelcomeActivity.services.get(i).setAvailability(1);
                            }
                            i++;
                        }
                        gender_a.setBackgroundResource(R.drawable.left_off_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_b.setBackgroundResource(R.drawable.mid_on_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.whiteColor));

                        gender_c.setBackgroundResource(R.drawable.right_off_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.blackColor));
                    }
                });

                gender_c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //                        WelcomeActivity.services.get(servicePosition).setAvailability(2);
                        int i = 0;
                        for (ServicePushModel spm : WelcomeActivity.services) {
                            if (spm.getServiceId().equalsIgnoreCase(service.getId())) {
                                WelcomeActivity.services.get(i).setAvailability(2);
                            }
                            i++;
                        }
                        gender_a.setBackgroundResource(R.drawable.left_off_black);
                        gender_a.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_b.setBackgroundResource(R.drawable.mid_off_black);
                        gender_b.setTextColor(c.getResources().getColor(R.color.blackColor));

                        gender_c.setBackgroundResource(R.drawable.right_on_black);
                        gender_c.setTextColor(c.getResources().getColor(R.color.whiteColor));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}