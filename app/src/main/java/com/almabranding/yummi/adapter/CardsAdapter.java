package com.almabranding.yummi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.CardModel;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ioshero on 15/04/16.
 */
public class CardsAdapter extends ArrayAdapter<CardModel> {


    Context c;

    public CardsAdapter(Context context, ArrayList<CardModel> service) {
        super(context, 0, service);
        c = context;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }


    @Override
    public int getItemViewType(int position) {

        if (YummiUtils.isPreformer(getContext()))
            return 1;
        return 0;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CardModel service = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cards_cell, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.textView4);
        tvName.setText("••••••••••••" + service.getDigits());


        ImageView cardType = (ImageView) convertView.findViewById(R.id.imageView4);

        ImageView mark = (ImageView) convertView.findViewById(R.id.imageViewMark);
        if (service.isDefault()) {
            mark.setVisibility(View.VISIBLE);
        } else
            mark.setVisibility(View.GONE);

        TextView tvDate = (TextView) convertView.findViewById(R.id.textView5);
        if (String.valueOf(service.getExpirationYear()).length() > 2)
            tvDate.setText(String.valueOf(service.getExpirationMonth()) + "/" + String.valueOf(service.getExpirationYear()).substring(2));


        switch (service.getType()) {
            case "Visa": {
                cardType.setImageResource(R.mipmap.stp_card_visa);
                break;
            }

            case "American Express": {
                cardType.setImageResource(R.mipmap.stp_card_amex);
                break;
            }


            case "MasterCard": {
                cardType.setImageResource(R.mipmap.stp_card_mastercard);
                break;
            }


            case "Discover": {
                cardType.setImageResource(R.mipmap.stp_card_discover);
                break;
            }

            case "JCB": {
                cardType.setImageResource(R.mipmap.stp_card_jcb);
                break;
            }


            case "Diners Club": {
                cardType.setImageResource(R.mipmap.stp_card_diners);
                break;
            }

            case "Unknown": {
                cardType.setImageResource(R.mipmap.stp_card_placeholder_template);
                break;
            }


            case "Maestro": {
                cardType.setImageResource(R.mipmap.stp_card_maestro);
                break;
            }


            case "Laser": {
                cardType.setImageResource(R.mipmap.stp_card_laser);
                break;
            }

            case "Solo": {
                cardType.setImageResource(R.mipmap.stp_card_solo);
                break;
            }

            case "Forbru": {
                cardType.setImageResource(R.mipmap.stp_card_forbru);
                break;
            }

            case "Paypal": {
                cardType.setImageResource(R.mipmap.stp_card_paypal);
                break;
            }

            case "Google": {
                cardType.setImageResource(R.mipmap.stp_card_google);
                break;
            }

            case "Shopify": {
                cardType.setImageResource(R.mipmap.stp_card_shopify);
                break;
            }

            case "Dankort": {
                cardType.setImageResource(R.mipmap.stp_card_dankort);
                break;
            }

            case "Money": {
                cardType.setImageResource(R.mipmap.stp_card_money);
                break;
            }

            default: {
                cardType.setImageResource(R.mipmap.stp_card_placeholder_template);
                break;
            }
        }

        return convertView;
    }
}
