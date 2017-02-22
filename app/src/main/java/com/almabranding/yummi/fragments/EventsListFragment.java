package com.almabranding.yummi.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
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
import com.almabranding.yummi.models.third.GenderModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.nkzawa.emitter.Emitter;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 19/04/16.
 */
public class EventsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    SwipeMenuListView listView;
    MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_events, container, false);
        MainActivity.where_go_back = -1;

        listView = (SwipeMenuListView) view.findViewById(R.id.listView);


        if (!result.isEmpty())
            if (EventsListFragment.this != null) {
                if (EventsListFragment.this.getContext() != null) {
                    adapter = new EventsAdapter(EventsListFragment.this.getContext(), result);
                    listView.setAdapter(adapter);
                    listView.setEmptyView(view.findViewById(R.id.emptyElement));
                }
            }

        events = getEvents();

//        View emptyView = inflater.inflate(R.layout.empty_list, null, false);
//        getActivity().addContentView(emptyView, listView.getLayoutParams()); // You're using the same params as listView
//        listView.setEmptyView(emptyView);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                switch (menu.getViewType()) {
                    case 0: {

                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getActivity().getApplicationContext());
                        // set item background
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00,
                                0x00)));
                        // set item width
                        deleteItem.setWidth((int) (90 * EventsListFragment.this.getContext().getResources().getDisplayMetrics().density));
                        // set item title
                        deleteItem.setTitle("Delete");
                        // set item title fontsize
                        deleteItem.setTitleSize(18);
                        // set item title font color
                        deleteItem.setTitleColor(Color.WHITE);
                        // add to menu
                        menu.addMenuItem(deleteItem);


                        SwipeMenuItem openItem = new SwipeMenuItem(
                                getActivity().getApplicationContext());
                        // set item background
                        openItem.setBackground(new ColorDrawable(Color.rgb(0xC7, 0xC7,
                                0xCC)));
                        // set item width
                        openItem.setWidth((int) (90 * EventsListFragment.this.getContext().getResources().getDisplayMetrics().density));
                        // set item title
                        openItem.setTitle("Edit");
                        // set item title fontsize
                        openItem.setTitleSize(18);
                        // set item title font color
                        openItem.setTitleColor(Color.WHITE);
                        // add to menu
                        menu.addMenuItem(openItem);




                        break;
                    }
                    case 1: {

                        break;
                    }

                    case 2: {

                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getActivity().getApplicationContext());
                        // set item background
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00,
                                0x00)));
                        // set item width
                        deleteItem.setWidth((int) (90 * EventsListFragment.this.getContext().getResources().getDisplayMetrics().density));
                        // set item title
                        deleteItem.setTitle("Delete");
                        // set item title fontsize
                        deleteItem.setTitleSize(18);
                        // set item title font color
                        deleteItem.setTitleColor(Color.WHITE);
                        // add to menu
                        menu.addMenuItem(deleteItem);
                        break;
                    }

                }


            }
        };

// set creator
        listView.setMenuCreator(creator);

        return view;
    }

    public void setUpActionBar() {
        ((MainActivity) getActivity()).setEvents();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        ImageView title = (ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);
        if (YummiUtils.isPreformer(getActivity())) {
            title.setVisibility(View.GONE);
        } else
            title.setVisibility(View.VISIBLE);
        title.setImageResource(R.mipmap.add);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    EventFragment event = new EventFragment();
                    MainActivity.where_go_back = 3;

                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .hide(EventsListFragment.this)
                            .replace(R.id.content_frame, event)
                            .commit();
                }
            }
        });

        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();

        listView.setOnItemClickListener(this);
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 1: {
                        // edit
                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                        Call<EventModel> call = service.getEvent(result.get(position).getId(), MainActivity.token);

                        call.enqueue(new Callback<EventModel>() {
                            @Override
                            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                MainActivity.createEventModel = null;
                                MainActivity.activeModel = response.body();
                                MainActivity.activeModel.getOffline_services().clear();
                                MainActivity.activeModel.getOffline_performers().clear();

                                EventFragment event = new EventFragment();
                                MainActivity.where_go_back = 3;

                                // Insert the fragment by replacing any existing fragment
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .addToBackStack(null)
                                        .hide(EventsListFragment.this)
                                        .replace(R.id.content_frame, event)
                                        .commit();

                            }

                            @Override
                            public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                            }
                        });

                        break;
                    }
                    case 0: {
                        // delete
                        if (getActivity()!= null)
                            ((MainActivity)getActivity()).addOverlay();
                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                        Call<ResponseBody> call = service.deleteEvent(result.get(position).getId(), MainActivity.token);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    bustModelList = response.body();

                                result.remove(position);
                                adapter.notifyDataSetChanged();

                                if (getActivity()!= null)
                                    ((MainActivity)getActivity()).removeView();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());

                                if (getActivity()!= null)
                                    ((MainActivity)getActivity()).removeView();
                            }
                        });

                        break;
                    }
                }
                // false : close the menu; true : not close the menu
                return true;
            }
        });


        getView().findViewById(R.id.ladies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformerListFragment performerListFragment = new PerformerListFragment();
                MainActivity.where_go_back = 3;
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .hide(EventsListFragment.this)
                        .replace(R.id.content_frame, performerListFragment)
                        .commit();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        listView.setOnItemClickListener(this);
        setUpActionBar();

        ((MainActivity) getActivity()).mSocket.on("eventStatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String updatedEventId = (String) args[0];
                    int statusValue = (int) args[1];

                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).getId().equalsIgnoreCase(updatedEventId)) {
                            result.get(j).setStatus(statusValue);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                        }
                    });
            }
        });

    }

    @Override
    public void onPause() {
        ((MainActivity) getActivity()).mSocket.off("eventStatus");
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<EventModel> call = service.getEvent(result.get(position).getId(), MainActivity.token);

        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if (response.body() != null) {
                    if (response.body().getOwnerId() == null) {
                        MainActivity.showAllert("Live event", "comming soon");
                        return;
                    }

                    MainActivity.createEventModel = null;
                    MainActivity.activeModel = null;


                    if (YummiUtils.isPreformer(getContext())) {
                        EventStaticFragment event = new EventStaticFragment();
                        event.setEventModel(response.body());
                        MainActivity.where_go_back = 3;

                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(EventsListFragment.this)
                                .replace(R.id.content_frame, event)
                                .commit();
                    } else if (response.body().getOwnerId().equalsIgnoreCase(MainActivity.userId)) {


                        if (response.body().getStatus() == 0) {
                            MainActivity.activeModel = response.body();
                            MainActivity.activeModel.getOffline_services().clear();
                            MainActivity.activeModel.getOffline_performers().clear();
                            EventFragment event = new EventFragment();
                            MainActivity.where_go_back = 3;

                            // Insert the fragment by replacing any existing fragment
                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .hide(EventsListFragment.this)
                                    .replace(R.id.content_frame, event)
                                    .commit();
                        } else {
                            EventStaticFragment event = new EventStaticFragment();
                            event.setEventModel(response.body());
                            MainActivity.where_go_back = 3;

                            // Insert the fragment by replacing any existing fragment
                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .hide(EventsListFragment.this)
                                    .replace(R.id.content_frame, event)
                                    .commit();

                        }
                    } else {
                        EventStaticFragment event = new EventStaticFragment();
                        event.setEventModel(response.body());
                        MainActivity.where_go_back = 3;

                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(EventsListFragment.this)
                                .replace(R.id.content_frame, event)
                                .commit();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
            }
        });

    }

    EventsAdapter adapter;
    static final ArrayList<EventListModel> result = new ArrayList<EventListModel>();
    ArrayList<EventListModel> events = new ArrayList<EventListModel>();

    private ArrayList<EventListModel> getEvents() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<EventListModel>> call;

        if (!YummiUtils.isPreformer(getContext()))
            call = service.getClientEvents(MainActivity.userId, MainActivity.token);
        else
            call = service.getClientEventsPer(MainActivity.userId, MainActivity.token);

//        Log.e("GET EVENTS", "started");
        call.enqueue(new Callback<List<EventListModel>>() {
                         @Override
                         public void onResponse(Call<List<EventListModel>> call, Response<List<EventListModel>> response) {
//                    bustModelList = response.body();

                             if (response.body() != null) {
//                                 Log.e("GET EVENTS", "received");
                                 result.clear();
                                 for (EventListModel e : response.body()) {
                                     result.add(e);
                                 }
                                 if (EventsListFragment.this != null) {
                                     if (EventsListFragment.this.getContext() != null) {
                                         adapter = new EventsAdapter(EventsListFragment.this.getContext(), result);
                                         listView.setAdapter(adapter);
                                         if (getView() != null)
                                             listView.setEmptyView(getView().findViewById(R.id.emptyElement));
                                     }
                                 }
                             }

                         }

                         @Override
                         public void onFailure(Call<List<EventListModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                         }
                     }

        );

        return result;
    }


}
