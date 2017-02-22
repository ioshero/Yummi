package com.almabranding.yummi.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.adapter.PerformersConflictResolutionArrayAdapter;
import com.almabranding.yummi.models.ConflictResolutionAnswerModel;
import com.almabranding.yummi.models.ConflictResolutionBaseClass;
import com.almabranding.yummi.models.ConflictResolutionReturnClass;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.PaymentModel;
import com.almabranding.yummi.models.third.GenderModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 10/05/16.
 */
public class ConflictResolveDialogFragment extends DialogFragment {


    public ConflictResolveDialogFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_conflict_resolution, container, false);
        view.findViewById(R.id.drawer_textview_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (questionPos < 0 || andswerPos < 0) {
                    MainActivity.showAllert("Error", "Could not send the report");
                    return;
                }

                sendConflictRes();


            }
        });
        view.findViewById(R.id.drawer_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getCRQandA();

        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
//        setStyle(style, theme);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            ((MainActivity) getActivity()).removeView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<String> getStringListFromModeList(ArrayList<ConflictResolutionBaseClass> modes) {

        ArrayList<String> result = new ArrayList<String>();
        for (ConflictResolutionBaseClass g : modes) {
            result.add(g.getName());
        }
        return result;
    }

    private ArrayList<String> getStringListFromModeList(ArrayList<ConflictResolutionBaseClass> modes, int i) {

        ArrayList<String> result = new ArrayList<String>();
        for (ConflictResolutionAnswerModel g : modes.get(i).getAnswers()) {
            result.add(g.getTitle());
        }
        return result;
    }

    ArrayList<ConflictResolutionBaseClass> crList = new ArrayList<>();


    private void sendConflictRes() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<ResponseBody> call = null;


        if (YummiUtils.isPreformer(getActivity()))
            call = service.postPerformerCR(MainActivity.userId, MainActivity.token, new ConflictResolutionReturnClass(crList.get(questionPos).getAnswers().get(andswerPos).getId(), ((EditText) getView().findViewById(R.id.editText2)).getText().toString()));
        else
            call = service.postClientCR(MainActivity.userId, MainActivity.token, new ConflictResolutionReturnClass(crList.get(questionPos).getAnswers().get(andswerPos).getId(), ((EditText) getView().findViewById(R.id.editText2)).getText().toString()));


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if (response.body() == null){
                    MainActivity.showAllert("Error","An error occured during the problem report sending");
                }else{
                    MainActivity.showAllert("Success","The problem report has been successfully sent");
                    dismiss();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                MainActivity.showAllert("Network Error", "Could not connect to the server");
            }
        });
    }

    int andswerPos = -1;

    int questionPos = -1;

    private void getCRQandA() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<ConflictResolutionBaseClass>> call = null;


        if (!YummiUtils.isPreformer(getActivity()))
            call = service.getCrQuestions(MainActivity.token);
        else
            call = service.getCrQuestionsPrformer(MainActivity.token);


        call.enqueue(new Callback<List<ConflictResolutionBaseClass>>() {
            @Override
            public void onResponse(Call<List<ConflictResolutionBaseClass>> call, Response<List<ConflictResolutionBaseClass>> response) {
                crList = new ArrayList<ConflictResolutionBaseClass>(response.body());

                Spinner questions = (Spinner) getView().findViewById(R.id.spinner);
                ArrayList<String> body = getStringListFromModeList(crList);
                body.add(0, "Category");
                ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, body);
                questions.setAdapter(adapter_body);


                questions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                        questionPos = i - 1;
                        Spinner questions = (Spinner) getView().findViewById(R.id.spinner2);
                        if (i == 0) {
                            ArrayList<String> body = new ArrayList<String>();
                            body.add(0, "Select an option");
                            ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, body);
                            questions.setAdapter(adapter_body);
                        } else {

                            if (YummiUtils.isPreformer(getActivity())){
                                ArrayList<String> body = getStringListFromModeList(crList, i - 1);
                                body.add(0, "Select an option");
                                PerformersConflictResolutionArrayAdapter adapter_body = new PerformersConflictResolutionArrayAdapter(((MainActivity) getActivity()),
                                        android.R.layout.simple_spinner_dropdown_item, crList.get(i-1).getAnswers());

                                questions.setAdapter(adapter_body);


                                questions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }

                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                                        andswerPos = i - 1;

                                    }
                                });
                            }else{
                                ArrayList<String> body = getStringListFromModeList(crList, i - 1);
                                body.add(0, "Select an option");
                                ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, body);
                                questions.setAdapter(adapter_body);
                                questions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }

                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                                        andswerPos = i - 1;

                                    }
                                });
                            }



                        }

                    }
                });

            }

            @Override
            public void onFailure(Call<List<ConflictResolutionBaseClass>> call, Throwable t) {
                MainActivity.showAllert("Network Error", "Could not connect to the server");
            }
        });
    }

}



