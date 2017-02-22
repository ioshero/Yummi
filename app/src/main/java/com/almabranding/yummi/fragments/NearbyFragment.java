package com.almabranding.yummi.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.location.LocationPushModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 26/07/16.
 */
public class NearbyFragment extends Fragment {


    private int mInterval = 300000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);

        mHandler = new Handler();

        try {
            final View view = inflater.inflate(R.layout.fragment_nearby, container, false);


            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getActivity() != null) {
            ((ImageView) getActivity().findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icon);
            ((TextView) getActivity().findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("black"));

            NearbyFragment performerListFragment = new NearbyFragment();
            MainActivity.where_go_back = 0;
            // Insert the fragment by replacing any existing fragment

            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, performerListFragment)
                    .commit();

        }
        return inflater.inflate(R.layout.fragment_nearby_no_frag, container, false);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getPerformers(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void setUpActionBar() {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        if (getActivity() != null)
            ((MainActivity) getActivity()).setUpMap((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        startRepeatingTask();//getPerformers();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    @Override
    public void onResume() {
        super.onResume();
//        setUpActionBar();
        ((MainActivity) getActivity()).setMapEventToNearby();

    }


    @Override
    public void onDetach() {
        super.onDetach();


    }

    ArrayList<PerformerListModel> result = new ArrayList<PerformerListModel>();

    public ArrayList<PerformerListModel> getPerformers() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<List<PerformerListModel>> call;
        if (YummiUtils.getLocation() != null) {
            call = service.getPerformersNearer(MainActivity.token, new LocationPushModel(YummiUtils.getLocation().getLatitude(), YummiUtils.getLocation().getLongitude(), MainActivity.userId));
            call.enqueue(new Callback<List<PerformerListModel>>() {
                             @Override
                             public void onResponse(Call<List<PerformerListModel>> call, Response<List<PerformerListModel>> response) {

                                 if (response.body() != null) {
                                     result.clear();
                                     for (PerformerListModel e : response.body()) {
                                         if (e.getName() != null)
                                             if (!e.getName().isEmpty())
                                                 result.add(e);
                                     }
                                     ((MainActivity) getActivity()).setMapEventToNearby(result);


                                 }

                             }

                             @Override
                             public void onFailure(Call<List<PerformerListModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                             }
                         }

            );
        }
        return result;
    }
}
