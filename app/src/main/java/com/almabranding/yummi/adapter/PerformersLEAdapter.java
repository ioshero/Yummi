package com.almabranding.yummi.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.fragments.PerformerFragment;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ioshero on 15/04/16.
 */
public class PerformersLEAdapter extends ArrayAdapter<PerformerModel> {


    Context c;
    Fragment frgr;

    public PerformersLEAdapter(Context context, ArrayList<PerformerModel> service, Fragment frg) {
        super(context, 0, service);
        frgr = frg;
        c = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final PerformerModel performer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

//        if (convertView == null) {
        if (performer.getId().isEmpty()) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.live_event_owner, parent, false);
            TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
            tvName.setText(performer.getName());

        }else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.performer_cell_live_event, parent, false);
//        }


            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.textView7);
            // Populate the data into the template view using the data object
            tvName.setText(performer.getName());


            TextView tipName = (TextView) convertView.findViewById(R.id.tip_textView);
            if (tipName != null)
                tipName.setText(String.valueOf(performer.getTipPrice()) + " $");


            Button tipButton = (Button) convertView.findViewById(R.id.tip_button);
            tipButton.setText(performer.getTipStr());

            if (tipButton != null) {
                tipButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) frgr.getActivity()).giveTip(YummiUtils.currentlyOpenedEvent, performer.getId(), performer.getTip(), "");
                    }
                });
            }


            // Return the completed view to render on screen
            if (!performer.getId().isEmpty()) {
                if (performer.getImages().size() > 1)
                    Picasso.with(getContext()).load(performer.getImages().get(0).getFirstImagePath()).into((ImageView) convertView.findViewById(R.id.performer_iw));


            }
        }
        return convertView;
    }
}
