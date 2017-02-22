package com.almabranding.yummi.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.PaymentModel;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 10/05/16.
 */
public class CreditCardDialogFragment extends DialogFragment {


    public CreditCardDialogFragment() {

    }

    TextView cardNumber;
    TextView month;
    TextView year;
    TextView cvc;

    Switch save_card;
    ImageView close;
    TextView summ;

    View pay;
    private SharedPreferences sharedPreferences;

    private EventModel eventModel = null;
    private PaymentsListFragment frag = null;
    private PerformerFragment pFrag = null;
    private WelcomeActivity wAct = null;

    public void setwAct(WelcomeActivity wAct) {
        this.wAct = wAct;
    }

    String summ_str;

    public void setSumm_str(String summ_str) {
        this.summ_str = summ_str;
    }


    public PaymentsListFragment getFrag() {
        return frag;
    }

    public void setFrag(PaymentsListFragment frag) {
        this.frag = frag;
    }


    public boolean imgReq = false;

    public void setFrag(PerformerFragment frag, final boolean which) {
        this.pFrag = frag;
        imgReq = which;
    }

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        if (wAct != null)
            view = inflater.inflate(R.layout.credit_card, container, false);
        else if (frag == null) {
            view = inflater.inflate(R.layout.credit_card_advanced, container, false);
            close = (ImageView) view.findViewById(R.id.pay_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            save_card = (Switch) view.findViewById(R.id.pay_switch);
            summ = (TextView) view.findViewById(R.id.pay_text);
            if (summ_str != null) {
                summ.setText("$ " + summ_str);
            }
        } else
            view = inflater.inflate(R.layout.credit_card, container, false);


        cardNumber = ((TextView) view.findViewById(R.id.editTextcardnumber));
        month = ((TextView) view.findViewById(R.id.editTextmonth));
        year = ((TextView) view.findViewById(R.id.editTextyear));
        cvc = ((TextView) view.findViewById(R.id.editTextcvc));
        pay = view.findViewById(R.id.button_pay_card);

        if (eventModel != null) {
//            cardNumber.setText(sharedPreferences.getString("cardNumber", ""));
//            month.setText(sharedPreferences.getString("month", ""));
//            year.setText(sharedPreferences.getString("year", ""));
//            cvc.setText(sharedPreferences.getString("cvc", ""));
        }

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() >= 2)
                    switch (charSequence.toString().substring(0, 2)) {
                        case "37": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_amex);
                            break;
                        }

                        case "34": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_amex);
                            break;
                        }

                        case "30": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_diners);
                            break;
                        }

                        case "36": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_diners);
                            break;
                        }

                        case "35": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_jcb);
                            break;
                        }
                        case "38": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_diners);
                            break;
                        }

                        case "64": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_discover);
                            break;
                        }
                        case "62": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_discover);
                            break;
                        }

                        case "60": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_discover);
                            break;
                        }

                        default: {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_placeholder_template);
                            break;
                        }

                    }

                if (charSequence.toString().length() >= 1)
                    switch (charSequence.toString().substring(0, 1)) {
                        case "4": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_visa);
                            break;
                        }

                        case "5": {
                            ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_mastercard);
                            break;
                        }
                    }
                else
                    ((ImageView) getView().findViewById(R.id.imageView4)).setImageResource(R.mipmap.stp_card_placeholder_template);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (eventModel == null) {
            ((Button) view.findViewById(R.id.button_pay_card)).setText("Save");
        } else {
            ((Button) view.findViewById(R.id.button_pay_card)).setText("Pay");
        }

        if (wAct != null)
            ((Button) view.findViewById(R.id.button_pay_card)).setText("Verify");
        view.findViewById(R.id.button_pay_card).setClickable(true);

        view.findViewById(R.id.button_pay_card).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Card card;
                try {
                    try {

                        if (MainActivity.retrofit != null)
                            if (getActivity() instanceof MainActivity)
                                ((MainActivity) getActivity()).addOverlay();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Please fill", e.toString());
                    }

                    view.findViewById(R.id.button_pay_card).setBackgroundResource(R.drawable.grey_button_selector);
                    view.findViewById(R.id.button_pay_card).setClickable(false);

                    card = new Card(cardNumber.getText().toString(), Integer.valueOf(month.getText().toString()), 2000 + Integer.valueOf(year.getText().toString()), cvc.getText().toString());


//                    final SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("cardNumber", cardNumber.getText().toString());
//
//                    editor.putString("month", month.getText().toString());
//
//                    editor.putString("year", year.getText().toString());
//
//
//                    editor.putString("cvc", cvc.getText().toString());
//                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Please fill", e.toString());

                    try {
                        getView().findViewById(R.id.button_pay_card).setClickable(true);
                        MainActivity.showAllert("Error", "Please fill all the fields");

                    } catch (Exception er) {
                        er.printStackTrace();
                    }
                    if (wAct != null)
                        wAct.showAllert("Error", "Please fill all the fields");


                    return;
                }
                try {
                    final Stripe stripe = new Stripe("pk_test_ReUpo6NguO6frCAVfSDS4qi5");
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(final Token token) {
                                    // Send token to your server
                                    com.stripe.Stripe.apiKey = "sk_test_cDy3ksRPbBXXI1t1d97SmedK";

                                    if (wAct != null) {
                                        wAct.credCard = true;
                                        wAct.goldenButton();
                                        CreditCardDialogFragment.this.dismiss();
                                        pay.setBackgroundResource(R.drawable.gold_button_selector);
                                        pay.setClickable(true);
                                        return;
                                    }

                                    final String tokenStr = token.getId();

                                    final int ammount = 1000;//cents

                                    if (eventModel != null) {
                                        if (save_card != null)
                                            if (save_card.isChecked())
                                                ((MainActivity) getActivity()).addCardToken(tokenStr, eventModel.getId(), ammount, CreditCardDialogFragment.this, null);
                                            else
                                                ((MainActivity) getActivity()).payEvent(eventModel.getId(), tokenStr, ammount, CreditCardDialogFragment.this);
                                    } else {
                                        if (frag != null) {
                                            frag.addCardToken(tokenStr);
                                        }

                                        if (pFrag != null) {
                                            if (save_card != null)
                                                if (save_card.isChecked()) {
                                                    if (imgReq)
                                                        ((MainActivity) getActivity()).addCardToken(tokenStr, null, 0, null, pFrag);
                                                    else
                                                        ((MainActivity) getActivity()).addCardToken(tokenStr, null, -1, null, pFrag);
                                                }else if (imgReq)
                                                    pFrag.reqImage(tokenStr);
                                                else
                                                    pFrag.reqChat(tokenStr);
                                        }
                                    }


                                    succes();

                                }

                                public void onError(Exception error) {
                                    if (MainActivity.retrofit != null) {
                                        try {

                                            ((MainActivity) getActivity()).showAllert("Error", error.toString());
                                            ((MainActivity) getActivity()).removeView();
                                        } catch (Exception er) {
                                            er.printStackTrace();
                                        }

                                    }

                                    getView().findViewById(R.id.button_pay_card).setClickable(true);
                                    pay.setBackgroundResource(R.drawable.gold_button_selector);
                                    pay.setClickable(true);

                                    if (wAct != null) {
                                        wAct.credCard = false;
                                        wAct.showAllert("Error", error.toString());
                                    }
                                }
                            }
                    );
                } catch (Exception e) {
                    if (MainActivity.retrofit != null) {
                        ((MainActivity) getActivity()).removeView();
                        view.findViewById(R.id.button_pay_card).setBackgroundResource(R.drawable.gold_button_selector);
                        getView().findViewById(R.id.button_pay_card).setClickable(true);
                        e.printStackTrace();


                        try {
                            ((MainActivity) getActivity()).showAllert("Error", "invalid credit card");
                        } catch (Exception er) {
                            er.printStackTrace();
                        }

                    }
                    if (wAct != null) {
                        wAct.credCard = false;
                        wAct.showAllert("Error", "invalid credit card");
                    }


                }
            }

        });


        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    private void sendTokenToServer(final Token token) {

        NetworkCallApiInterface service = ((MainActivity) getActivity()).retrofit.create(NetworkCallApiInterface.class);

        Call<ResponseBody> call = service.addChargeToken(MainActivity.userId, MainActivity.token, new PaymentModel(token.getId(), "test"));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ((MainActivity) getActivity()).removeView();
                pay.setBackgroundResource(R.drawable.gold_button_selector);
                pay.setClickable(true);

            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            ((MainActivity) getActivity()).removeView();
            ((MainActivity) getActivity()).removeView();
            ((MainActivity) getActivity()).removeView();
            ((MainActivity) getActivity()).removeView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void succes() {


        getActivity().runOnUiThread(new Runnable() {
            public void run() {
//                if (eventModel != null)
//                    ((MainActivity) getActivity()).showAllert("Charge Paid", "Succes");
                CreditCardDialogFragment.this.dismiss();
                ((MainActivity) getActivity()).removeView();
                pay.setBackgroundResource(R.drawable.gold_button_selector);
                pay.setClickable(true);
            }
        });


    }


}



