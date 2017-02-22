package com.almabranding.yummi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.models.ConflictResolutionAnswerModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 29/07/16.
 */
public class PerformersConflictResolutionArrayAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private ArrayList data;
    ConflictResolutionAnswerModel tempValues = null;
    LayoutInflater inflater;

    /*************
     * CustomAdapter Constructor
     *****************/
    public PerformersConflictResolutionArrayAdapter(
            MainActivity activitySpinner,
            int textViewResourceId,
            ArrayList objects
    ) {
        super(activitySpinner, textViewResourceId, objects);

        /********** Take passed values **********/
        activity = activitySpinner;
        data = objects;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomViewClosed(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_item, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (ConflictResolutionAnswerModel) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.company);
        TextView sub = (TextView) row.findViewById(R.id.sub);


        // Set values for spinner each row
        label.setText(tempValues.getTitle());
        sub.setText(tempValues.getDescription());


        return row;
    }

    public View getCustomViewClosed(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_item_smaller, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (ConflictResolutionAnswerModel) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.company);


        // Set values for spinner each row
        label.setText(tempValues.getTitle());


        return row;
    }
}
