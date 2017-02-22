package com.almabranding.yummi.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.fragments.SettingsFragment;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.SettingsModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class SettingsAdapter extends ArrayAdapter<SettingsModel> {


    Context c;

    SettingsFragment fragment;

    public SettingsAdapter(Context context, ArrayList<SettingsModel> service, SettingsFragment fragment) {
        super(context, 0, service);
        c = context;
        this.fragment = fragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final SettingsModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

//        if (convertView == null) {
        if (service.getValue1().isEmpty())
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_cell, parent, false);
//        }


        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        // Populate the data into the template view using the data object
        tvName.setText(service.getName());
        if (!service.getValue1().isEmpty()) {
            // Lookup view for data population


            final TextView textv1 = (TextView) convertView.findViewById(R.id.choice_a);
            // Populate the data into the template view using the data object
            textv1.setText(service.getValue1());

            final TextView textv2 = (TextView) convertView.findViewById(R.id.choice_c);
            // Populate the data into the template view using the data object
            textv2.setText(service.getValue2());


            if (service.isSelected2()) {
                textv1.setBackgroundResource(R.drawable.left_off);
                textv1.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));   //(R.color.radioCorrnerGray));

                textv2.setBackgroundResource(R.drawable.right_on);
                textv2.setTextColor(c.getResources().getColor(R.color.whiteColor));

            } else {

                textv1.setBackgroundResource(R.drawable.left_on);
                textv1.setTextColor(c.getResources().getColor(R.color.whiteColor));
                textv2.setBackgroundResource(R.drawable.right_off);
                textv2.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));
            }

            if (textv1.getText().toString().contains("No")) {
                textv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setSelected2(false);
                        textv1.setBackgroundResource(R.drawable.left_on);
                        textv1.setTextColor(c.getResources().getColor(R.color.whiteColor));
                        textv2.setBackgroundResource(R.drawable.right_off);
                        textv2.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));
                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(service.getName(), false).commit();


                        if (service.getName().contains("Fluid")){
                            if ( PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Woman", false) ||   PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Man", false)){

                            }else{
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Woman", true).commit();
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Man", false).commit();
                            }

                        }

                        if (service.getName().contains("Man")){
                            if ( PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Woman", false) ||   PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Fluid", false)){

                            }else{
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Woman", true).commit();
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Fluid", false).commit();
                            }
                        }

                        if (service.getName().contains("Woman")){
                            if ( PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Man", false) ||   PreferenceManager.getDefaultSharedPreferences(c).getBoolean("Fluid", false)){

                            }else{
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Man", true).commit();
                                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Fluid", false).commit();
                            }
                        }

                        fragment.setSettingsList();


                    }
                });
            } else
                textv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setSelected2(false);
                        textv1.setBackgroundResource(R.drawable.left_on);
                        textv1.setTextColor(c.getResources().getColor(R.color.whiteColor));
                        textv2.setBackgroundResource(R.drawable.right_off);
                        textv2.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));

                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(service.getName(), false).commit();
                    }
                });




            if (textv2.getText().toString().contains("Yes")) {
                textv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setSelected2(true);

                        textv1.setBackgroundResource(R.drawable.left_off);
                        textv1.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));   //(R.color.radioCorrnerGray));

                        textv2.setBackgroundResource(R.drawable.right_on);
                        textv2.setTextColor(c.getResources().getColor(R.color.whiteColor));
                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(service.getName(), true).commit();


                        if (service.getName().contains("Fluid")){
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Woman", false).commit();
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Man", false).commit();
                        }

                        if (service.getName().contains("Man")){
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Fluid", false).commit();
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Woman", false).commit();
                        }

                        if (service.getName().contains("Woman")){
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Fluid", false).commit();
                            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("Man", false).commit();
                        }

                        fragment.setSettingsList();


                    }
                });
            } else
                textv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setSelected2(true);

                        textv1.setBackgroundResource(R.drawable.left_off);
                        textv1.setTextColor(c.getResources().getColor(R.color.radioCorrnerGray));   //(R.color.radioCorrnerGray));

                        textv2.setBackgroundResource(R.drawable.right_on);
                        textv2.setTextColor(c.getResources().getColor(R.color.whiteColor));
                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(service.getName(), true).commit();
                    }
                });


            // Return the completed view to render on screen
        }


        return convertView;
    }
}
