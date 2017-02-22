package com.almabranding.yummi.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.adapter.SettingsAdapter;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.SettingsModel;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 29/07/16.
 */
public class SettingsFragment extends ListFragment implements AdapterView.OnItemClickListener {


    @Override
    public void onPause() {
/*
 {
    "name": "Male",
    "id": "56d8533b083e3e9e40254035"
  },
  {
    "name": "Female",
    "id": "56d8534d083e3e9e40254036"
  },
  {
    "name": "Fluid",
    "id": "56d85351083e3e9e40254037"
  }
 */

        String gender = "56d85351083e3e9e40254037";
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Woman", false)) {
            gender = "56d8534d083e3e9e40254036";
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Fluid", false)) {
            gender = "56d85351083e3e9e40254037";
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Man", false)) {
            gender = "56d8533b083e3e9e40254035";
        }

        if (YummiUtils.isPreformer(getActivity())) {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);


            Call<ResponseBody> call = service.updateInterestedInPerformer(MainActivity.userId, gender, MainActivity.token);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((MainActivity) getActivity()).removeView();
                }
            });
        } else {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
            Call<ResponseBody> call = service.updateInterestedIn(MainActivity.userId, gender, MainActivity.token);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((MainActivity) getActivity()).removeView();
                }
            });
        }

        super.onPause();
    }

    public void setUpActionBar() {

        ((MainActivity) getActivity()).setNone();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("Settings");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);


        setSettingsList();
        return view;
    }

    public void setSettingsList() {
        ArrayList<SettingsModel> settings = new ArrayList<>();
        settings.add(new SettingsModel("Measurments", "", "", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Measurments", false)));
        settings.add(new SettingsModel("Weight", "lbs", "kg", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Weight", true)));
        settings.add(new SettingsModel("Dimensions", "inch", "cm", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Dimensions", true)));
        settings.add(new SettingsModel("Tempretures", "ºF", "ºC", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Tempretures", true)));
        settings.add(new SettingsModel("Distance", "mile", "km", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Distance", true)));

        settings.add(new SettingsModel("Interested in", "", "", false));
        settings.add(new SettingsModel("Woman", "No", "Yes", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Woman", false)));
        settings.add(new SettingsModel("Man", "No", "Yes", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Man", false)));
        settings.add(new SettingsModel("Fluid", "No", "Yes", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Fluid", false)));

        SettingsAdapter adapter = new SettingsAdapter(getActivity(), settings, this);
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();

    }


}
