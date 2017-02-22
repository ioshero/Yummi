package com.almabranding.yummi.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ioshero on 03/06/16.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<String> data = new ArrayList();

    public GridViewAdapter(Context context, ArrayList data) {
        super(context, R.layout.media_gallery_item, data);
        this.layoutResourceId = R.layout.media_gallery_item;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.medai_gallery_iw);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        String item = data.get(position);

        Picasso.with(getContext()).load(item).into(holder.image);

        return row;
    }

    static class ViewHolder {
        ImageView image;
    }


}
