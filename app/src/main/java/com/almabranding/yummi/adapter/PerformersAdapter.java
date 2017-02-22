package com.almabranding.yummi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.fragments.PerformerFragment;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.Service;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ioshero on 15/04/16.
 */
public class PerformersAdapter extends ArrayAdapter<PerformerModel> {


    Context c;

    public PerformersAdapter(Context context, ArrayList<PerformerModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PerformerModel performer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

//        if (convertView == null) {
        if (performer == null){
            return LayoutInflater.from(getContext()).inflate(R.layout.performer_cell_in_app, parent, false);
        }


        if (performer.getId().isEmpty())
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.services_header_in_app, parent, false);
        else
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.performer_cell_in_app, parent, false);
//        }


        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        // Populate the data into the template view using the data object
        tvName.setText(performer.getName());
        // Return the completed view to render on screen
        if (!performer.getId().isEmpty()){
            TextView age = (TextView) convertView.findViewById(R.id.textView19);
            TextView body = (TextView) convertView.findViewById(R.id.textView18);
            TextView bust = (TextView) convertView.findViewById(R.id.textView17);
            if (performer.getImages().size() > 1)
                Picasso.with(getContext()).load(performer.getImages().get(0).getFirstImagePath()).into((ImageView) convertView.findViewById(R.id.imageView4));

            if (performer.getBustSize() != null)
                bust.setText(Html.fromHtml("Bust size: " + performer.getBustSize().getName() + ""));
            if (performer.getbodyType() != null)
                body.setText(Html.fromHtml("Body type: " + performer.getbodyType().getName() + ""));
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(performer.getBirthDate());

                    age.setText(Html.fromHtml("Age: <b>" + String.valueOf(PerformerFragment.getDiffYears(convertedDate, new Date())) + "</b>"));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (NullPointerException n) {

            }
        }

        return convertView;
    }
}
