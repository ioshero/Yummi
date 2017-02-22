package com.almabranding.yummi.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.EventsAdapter;
import com.almabranding.yummi.adapter.PerformersArrayAdapter;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.PerformerModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 19/04/16.
 */
public class FavouritesListFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView performesrListView;
    AppCompatActivity mainActivity;

    public void setMainActivity(AppCompatActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        MainActivity.where_go_back = -1;
        listView = (ListView) view.findViewById(R.id.list);
        if (!result.isEmpty())
            if (FavouritesListFragment.this != null) {
                adapter = new PerformersArrayAdapter(FavouritesListFragment.this.getContext(), result);
                listView.setAdapter(adapter);
                if (getView() != null)
                    listView.setEmptyView(getView().findViewById(R.id.empty));
//                        getListView().setEmptyView(getView().findViewById(R.id.emptyElement));
//                        setEmptyText("EMPTY TEST");

            }
        getFavourites();
        return view;
    }

    private void setUpActionBar() {

        ((MainActivity) getActivity()).setFavourites();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setOnClickListener(null);
        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
//        ImageView title = (ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done);
//        title.setVisibility(View.VISIBLE);
//        title.setImageResource(R.mipmap.menu_icon_search);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);


        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }


    PerformersArrayAdapter adapter;
    ListView listView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        if (result != null)
            adapter = new PerformersArrayAdapter(this.getContext(), result);

        if (listView == null)
            listView = (ListView) getView().findViewById(R.id.list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.setEmptyView(getView().findViewById(R.id.empty));
//        setEmptyText("EMPTY TEST");


    }

    static final ArrayList<PerformerListModel> result = new ArrayList<PerformerListModel>();

    private ArrayList<PerformerListModel> getFavourites() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<PerformerListModel>> call = service.getFavouritesList(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<List<PerformerListModel>>() {
            @Override
            public void onResponse(Call<List<PerformerListModel>> call, Response<List<PerformerListModel>> response) {
//                    bustModelList = response.body();

                if (response.body() != null) {
                    result.clear();
                    for (PerformerListModel e : response.body()) {
                        result.add(e);
                    }
                    if (FavouritesListFragment.this != null) {
                        adapter = new PerformersArrayAdapter(FavouritesListFragment.this.getContext(), result);
                        listView.setAdapter(adapter);
                        if (getView() != null)
                            listView.setEmptyView(getView().findViewById(R.id.empty));
//                        getListView().setEmptyView(getView().findViewById(R.id.emptyElement));
//                        setEmptyText("EMPTY TEST");

                    }
                }

            }

            @Override
            public void onFailure(Call<List<PerformerListModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
            }
        });

        return result;
    }


    @Override
    public void onResume() {
        super.onResume();
        listView.setOnItemClickListener(this);
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
                    MainActivity.where_go_back = 2;
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .hide(FavouritesListFragment.this)
                            .replace(R.id.content_frame, perf)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<PerformerModel> call, Throwable t) {
            }
        });

    }


}
