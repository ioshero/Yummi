package com.almabranding.yummi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.fragments.EventFragment;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.ServiceRespondModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class ServiceNMAdapter extends ArrayAdapter<Service> {


    MainActivity c;

    EventFragment fragment;

    public void setFragment(EventFragment fragment) {
        this.fragment = fragment;
    }


    public ServiceNMAdapter(MainActivity context, ArrayList<Service> service) {
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
            tvName.setText(service.getService().getName());

            if (service.getService().getId().isEmpty()) {
                tvName.setText(tvName.getText());
            }

            final TextView duartion = (TextView) convertView.findViewById(R.id.textView20);


            final TextView time_unit = (TextView) convertView.findViewById(R.id.textView21);
            final ImageView minus = (ImageView) convertView.findViewById(R.id.imageView7);
            final ImageView plus = (ImageView) convertView.findViewById(R.id.imageView6);
            final ImageView mark = (ImageView) convertView.findViewById(R.id.imageView5);

            if (duartion != null) {
                duartion.setText(service.getStringDuration());
                if (service.isSelected()) {
                    duartion.setVisibility(View.VISIBLE);
                    time_unit.setVisibility(View.VISIBLE);
                    plus.setVisibility(View.VISIBLE);
                    minus.setVisibility(View.VISIBLE);
                    mark.setVisibility(View.VISIBLE);
                    duartion.setText(service.getStringDuration());
                }
            }

            if (mark != null)
                mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        duartion.setVisibility(View.GONE);
                        time_unit.setVisibility(View.GONE);
                        plus.setVisibility(View.GONE);
                        minus.setVisibility(View.GONE);
                        mark.setVisibility(View.GONE);
                        service.setSelected(false);
                        String eventId = "";
                        if (MainActivity.activeModel != null) {
                            eventId = MainActivity.activeModel.getId();
                            c.deleteEventService(service.getService().getId(), eventId, service.getUid());
                            MainActivity.activeModel.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                            MainActivity.activeModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                        }

                        if (MainActivity.createEventModel != null)
                            MainActivity.createEventModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));

                        if (fragment != null)
                            fragment.setUpSum();
                    }
                });

            if (plus != null)
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setSelected(true);
                        service.setDuration(Double.parseDouble(duartion.getText().toString()) + 0.5);
                        duartion.setText(service.getStringDuration());
                        if (MainActivity.activeModel != null) {


                            if (MainActivity.activeModel.getServices().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                                MainActivity.activeModel.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                            }

                            int isIn = -1;
                            int i = -1;
                            for (ServiceRespondModel srm : MainActivity.activeModel.getOffline_services()) {
                                i++;
                                if (srm.getService().getId().equalsIgnoreCase(service.getService().getId())) {
                                    isIn = i;
                                }
                            }
                            if (isIn == -1) {
                                ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                added.setPrice(service.getPrice());
                                if (MainActivity.activeModel.getOffline_services().contains(added))
                                    MainActivity.activeModel.getOffline_services().remove(added);
                                MainActivity.activeModel.getOffline_services().add(added);
                            } else {
                                MainActivity.activeModel.getOffline_services().remove(isIn);
                                ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                added.setPrice(service.getPrice());
                                if (MainActivity.activeModel.getOffline_services().contains(added))
                                    MainActivity.activeModel.getOffline_services().remove(added);
                                MainActivity.activeModel.getOffline_services().add(added);
                                ;
                            }
                        } else if (MainActivity.createEventModel != null) {
                            int isIn = -1;
                            int i = -1;
                            for (ServiceRespondModel srm : MainActivity.createEventModel.getOffline_services()) {
                                i++;
                                if (srm.getService().getId().equalsIgnoreCase(service.getService().getId())) {
                                    isIn = i;
                                }
                            }
                            if (isIn == -1) {
                                ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                added.setPrice(service.getPrice());
                                if (MainActivity.createEventModel.getOffline_services().contains(added))
                                    MainActivity.createEventModel.getOffline_services().remove(added);
                                MainActivity.createEventModel.getOffline_services().add(added);
                            } else {
                                MainActivity.createEventModel.getOffline_services().remove(isIn);
                                ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                added.setPrice(service.getPrice());
                                if (MainActivity.createEventModel.getOffline_services().contains(added))
                                    MainActivity.createEventModel.getOffline_services().remove(added);
                                MainActivity.createEventModel.getOffline_services().add(added);

                            }
                        }

                        if (fragment != null)
                            fragment.setUpSum();
                    }
                });

            if (minus != null)
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        service.setDuration(Double.parseDouble(duartion.getText().toString()) - 0.5);
                        service.setSelected(true);
                        if (service.getDuration() <= 0.0) {
                            service.setSelected(false);
                            duartion.setVisibility(View.GONE);
                            time_unit.setVisibility(View.GONE);
                            plus.setVisibility(View.GONE);
                            minus.setVisibility(View.GONE);
                            mark.setVisibility(View.GONE);
                            String eventId = "";
                            if (MainActivity.activeModel != null) {
                                if (MainActivity.activeModel.getServices().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                                    MainActivity.activeModel.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                                }

                                eventId = MainActivity.activeModel.getId();
                                c.deleteEventService(service.getService().getId(), eventId, service.getUid());
                                MainActivity.activeModel.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                                MainActivity.activeModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                            }
                            if (MainActivity.createEventModel != null)
                                MainActivity.createEventModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));


                        } else {
                            duartion.setText(service.getStringDuration());
                            if (MainActivity.activeModel != null) {
                                if (MainActivity.activeModel.getServices().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                                    MainActivity.activeModel.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                                }

                                int isIn = -1;
                                int i = -1;
                                for (ServiceRespondModel srm : MainActivity.activeModel.getOffline_services()) {
                                    i++;
                                    if (srm.getService().getId().equalsIgnoreCase(service.getService().getId())) {
                                        isIn = i;
                                    }
                                }

                                if (isIn == -1) {
                                    ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                    added.setPrice(service.getPrice());
                                    if (MainActivity.activeModel.getOffline_services().contains(added))
                                        MainActivity.activeModel.getOffline_services().remove(added);
                                    MainActivity.activeModel.getOffline_services().add(added);
                                } else {
                                    MainActivity.activeModel.getOffline_services().remove(isIn);
                                    ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                    added.setPrice(service.getPrice());
                                    if (MainActivity.activeModel.getOffline_services().contains(added))
                                        MainActivity.activeModel.getOffline_services().remove(added);
                                    MainActivity.activeModel.getOffline_services().add(added);

                                }
                            } else if (MainActivity.createEventModel != null) {
                                int isIn = -1;
                                int i = -1;
                                for (ServiceRespondModel srm : MainActivity.createEventModel.getOffline_services()) {
                                    i++;
                                    if (srm.getService().getId().equalsIgnoreCase(service.getService().getId())) {
                                        isIn = i;
                                    }
                                }
                                if (isIn == -1) {
                                    ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                    added.setPrice(service.getPrice());
                                    if (MainActivity.createEventModel.getOffline_services().contains(added))
                                        MainActivity.createEventModel.getOffline_services().remove(added);
                                    MainActivity.createEventModel.getOffline_services().add(added);
                                } else {
                                    MainActivity.createEventModel.getOffline_services().remove(isIn);
                                    ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                                    added.setPrice(service.getPrice());
                                    if (MainActivity.createEventModel.getOffline_services().contains(added))
                                        MainActivity.createEventModel.getOffline_services().remove(added);
                                    MainActivity.createEventModel.getOffline_services().add(added);
                                }
                            }
                        }
                        if (fragment != null)
                            fragment.setUpSum();
                    }
                });


        }


        return convertView;
    }
}
