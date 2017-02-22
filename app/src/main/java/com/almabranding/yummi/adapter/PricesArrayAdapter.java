package com.almabranding.yummi.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.PriceModel;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;
import com.almabranding.yummi.models.services.ServicePushPriceModel;
import com.almabranding.yummi.utils.InputFilterMinMax;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class PricesArrayAdapter extends ArrayAdapter<ServiceModel> {


    Context c;
    ArrayList<ServiceModel> srr;

    public PricesArrayAdapter(Context context, ArrayList<ServiceModel> service) {
        super(context, 0, service);
        c = context;
        srr = service;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        for (ServiceModel smodel : srr) {
            boolean contatins = false;
            for (ServicePushPriceModel mod : WelcomeActivity.prices) {
                if (smodel.getId().equalsIgnoreCase(mod.getServiceId())) {
                    contatins = true;
                }
            }
            if (!contatins)
                WelcomeActivity.prices.add(new ServicePushPriceModel(0, smodel.getId()));
        }

//        ServicePushPriceModel sp = WelcomeActivity.prices.get(position);
        ServiceModel service = srr.get(position);
//        for (ServiceModel q : srr) {
//            if (sp.getServiceId().equalsIgnoreCase(q.getId()))
//                service = q;
//        }

        if (service == null) {
            return LayoutInflater.from(getContext()).inflate(R.layout.price_cell, parent, false);
        }
        final String serviceId = service.getId();
        // Check if an existing view is being reused, otherwise inflate the view

//        if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.price_cell, parent, false);
//        }

        try {
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            // Populate the data into the template view using the data object
            tvName.setText(service.getName());


            final EditText edit = ((EditText) convertView.findViewById(R.id.editTextPrice));
            edit.setFilters(new InputFilter[]{new InputFilterMinMax(1, 100000000)});

            if (YummiUtils.performer != null)
                if (!serviceId.equalsIgnoreCase("68") || !serviceId.equalsIgnoreCase("66"))
                    for (PriceModel spm : YummiUtils.performer.getPrices()) {
                        if (spm.getServiceTypeId().equalsIgnoreCase(service.getId())) {
                            edit.setText(String.valueOf(spm.getAmount()));
                        }

                    }

            // Return the completed view to render on screen


            if (serviceId.equalsIgnoreCase("68")) {
                ((TextView) convertView.findViewById(R.id.textPrice)).setText("amt/img");
                if (YummiUtils.performer != null)
                    edit.setText(String.valueOf(YummiUtils.performer.getImagePrice()));

                edit.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100000000)});
            } else {
                ((TextView) convertView.findViewById(R.id.textPrice)).setText("amt/hr");
            }

            if (serviceId.equalsIgnoreCase("66")) {
                if (YummiUtils.performer != null)
                    edit.setText(String.valueOf(YummiUtils.performer.getChatPrice()));
                edit.setFilters(new InputFilter[]{new InputFilterMinMax(0, 100000000)});
            }

            edit.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {


                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    if (!edit.getText().toString().isEmpty()) {

                        if (serviceId.equalsIgnoreCase("68")) {
                            YummiUtils.imagePrice = Integer.valueOf(edit.getText().toString());
                        } else if (serviceId.equalsIgnoreCase("66")) {
                            YummiUtils.chatPrice = Integer.valueOf(edit.getText().toString());
                        } else {
                            int i = 0;
                            for (ServicePushPriceModel spm : WelcomeActivity.prices) {
                                if (spm.getServiceId().equalsIgnoreCase(serviceId)) {
                                    WelcomeActivity.prices.get(i).setPrice(Integer.valueOf(edit.getText().toString()));
                                }
                                i++;
                            }
                        }
                    }
                }
            });


            return convertView;
        } catch (Exception e) {
            return convertView;
        }


    }
}