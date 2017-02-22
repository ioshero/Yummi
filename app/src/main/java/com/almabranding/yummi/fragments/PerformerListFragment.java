package com.almabranding.yummi.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.EventsAdapter;
import com.almabranding.yummi.adapter.PerformersArrayAdapter;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.location.LocationPushModel;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 19/04/16.
 */
public class PerformerListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    //    ListView performesrListView;
    AppCompatActivity mainActivity;

    public void setMainActivity(AppCompatActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_performers, container, false);
        if (!result.isEmpty())
            if (PerformerListFragment.this != null) {
                if (PerformerListFragment.this.getContext() != null) {
                    adapter = new PerformersArrayAdapter(PerformerListFragment.this.getContext(), result);
                    setListAdapter(adapter);
                }
            }

        getPerformers();
        MainActivity.where_go_back = -1;
        return view;
    }

    private void setUpActionBar() {
        ((MainActivity) getActivity()).setLadies();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.VISIBLE);


        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("filtering", false)) {
            ((ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done)).setImageResource(R.mipmap.top_search_cancel_icon);

            actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("filtering", false).commit();
                    getPerformers();
                }
            });
        } else {
            ((ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done)).setImageResource(R.mipmap.menu_icon_search);


            actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerformerFilterFragment perf = new PerformerFilterFragment();
                    perf.setFragment(PerformerListFragment.this);
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    if (getActivity() != null) {
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(PerformerListFragment.this)
                                .replace(R.id.content_frame, perf)
                                .commit();
                    }
                }
            });
        }

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);

    }

    PerformersArrayAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        getListView().setOnItemClickListener(this);

        getView().findViewById(R.id.events).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getListView().setOnItemClickListener(this);
        setUpActionBar();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<PerformerModel> call = service.getPerformer(result.get(position).getId(), MainActivity.token);
        call.enqueue(new Callback<PerformerModel>() {
            @Override
            public void onResponse(Call<PerformerModel> call, Response<PerformerModel> response) {
                if (response.body() != null) {
                    PerformerFragment perf = new PerformerFragment();
                    perf.setPerformer(response.body());
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    if (getActivity() != null) {
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(PerformerListFragment.this)
                                .replace(R.id.content_frame, perf)
                                .commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<PerformerModel> call, Throwable t) {
            }
        });
    }

   static ArrayList<PerformerListModel> result = new ArrayList<PerformerListModel>();

    public ArrayList<PerformerListModel> getPerformers() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("filtering", false)) {
            ((ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done)).setImageResource(R.mipmap.top_search_cancel_icon);

            actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("filtering", false).commit();
                    getPerformers();
                }
            });
        } else {
            ((ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done)).setImageResource(R.mipmap.menu_icon_search);

//

            actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PerformerFilterFragment perf = new PerformerFilterFragment();
                    perf.setFragment(PerformerListFragment.this);
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    if (getActivity() != null) {
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(PerformerListFragment.this)
                                .replace(R.id.content_frame, perf)
                                .commit();
                    }
                }
            });
        }

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<List<PerformerListModel>> call;
        if (YummiUtils.getLocation() != null) {

            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("filtering", false)) {
                call = service.getPreformerswithFilter(MainActivity.token, YummiUtils.setUpFilter(PreferenceManager.getDefaultSharedPreferences(getActivity())));
            } else
                call = service.getPerformersNearer(MainActivity.token, new LocationPushModel(YummiUtils.getLocation().getLatitude(), YummiUtils.getLocation().getLongitude(), MainActivity.userId));
            Log.e("Nearer", "lat:" + String.valueOf(YummiUtils.getLocation().getLatitude()) + " long:" + String.valueOf(YummiUtils.getLocation().getLongitude()));
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("filtering", false)) {
                call = service.getPreformerswithFilter(MainActivity.token, YummiUtils.setUpFilter(PreferenceManager.getDefaultSharedPreferences(getActivity())));
            } else
                call = service.getPreformers(MainActivity.token);
        }

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
                                 if (PerformerListFragment.this != null) {
                                     if (PerformerListFragment.this.getContext() != null) {
                                         adapter = new PerformersArrayAdapter(PerformerListFragment.this.getContext(), result);
                                         setListAdapter(adapter);
                                     }
                                 }
                             }

                         }

                         @Override
                         public void onFailure(Call<List<PerformerListModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                         }
                     }

        );

        return result;
    }
}
