package com.almabranding.yummi.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import java.net.URISyntaxException;
import java.text.DateFormatSymbols;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.Manifest;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.GuestAdapter;
import com.almabranding.yummi.adapter.PerformersAdapter;
import com.almabranding.yummi.adapter.ServiceAdapter;
import com.almabranding.yummi.models.CreateEventmodel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.EventPerformerModel;
import com.almabranding.yummi.models.GuestModel;
import com.almabranding.yummi.models.LocationModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.PriceModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.ServiceRespondModel;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 27/04/16.
 */
public class EventFragment extends Fragment {


    private static final int PLACE_PICKER_REQUEST = 188;

    TextView location_text_view;
    TextView duration_text_view;
    TextView date_text_view;
    EditText title_text;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelativeLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        final TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview_done);
        title.setVisibility(View.VISIBLE);
        if (MainActivity.activeModel != null) {
            title.setText("UPDATE");
        } else
            title.setText("CREATE");

        TextView title_view = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);

        title_view.setVisibility(View.VISIBLE);

        if (MainActivity.activeModel != null)
            title_view.setText(MainActivity.activeModel.getName());
        else
            title_view.setText("New Event");

        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        ((MainActivity) getActivity()).setEvents();


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location_text_view.getText().toString().isEmpty() || title_text.getText().toString().isEmpty() || date_text_view.getText().toString().isEmpty()) {
                    MainActivity.showAllert("Error", "Please fill Title,date and location");
                    return;
                }

                ((MainActivity) getActivity()).addOverlay();
                if (MainActivity.createEventModel != null) {
                    MainActivity.createEventModel.setName(title_text.getText().toString());
                    String str = getDateAsString();

                    MainActivity.createEventModel.setStartAt(str);
                    NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

                    Call<EventModel> call = service.createEvent(MainActivity.createEventModel, MainActivity.token);

                    call.enqueue(new Callback<EventModel>() {
                        @Override
                        public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                            ((MainActivity) getActivity()).removeView();
                            if (response.body() != null) {
                                MainActivity.activeModel = response.body();
                                YummiUtils.currentlyOpenedEvent = MainActivity.activeModel.getId();
                                //model id: 5721f9e641c69aaa49c6109a
                                ((MainActivity) getActivity()).connectLogin();

                                try {


                                    JSONArray performersUpd = new JSONArray();

                                    if (MainActivity.createEventModel != null) {
                                        if (MainActivity.createEventModel.getOffline_performers() != null) {
                                            for (final EventPerformerModel p : MainActivity.createEventModel.getOffline_performers()) {
                                                JSONObject perfor = new JSONObject();
                                                perfor.put("performerId", p.getPerformerId());
                                                JSONArray performerServ = new JSONArray();
                                                if (MainActivity.createEventModel.getOffline_services() != null) {
                                                    for (final ServiceRespondModel q : MainActivity.createEventModel.getOffline_services()) {
                                                        if (q.getPerformerId().equalsIgnoreCase(p.getPerformerId())) {
                                                            JSONObject servic = new JSONObject();
                                                            servic.put("serviceId", q.getService().getId());
                                                            servic.put("duration", q.getDuration());
                                                            performerServ.put(servic);
                                                        }

                                                    }

                                                }
                                                perfor.put("services", performerServ);

                                                performersUpd.put(perfor);

                                            }
                                        }
                                    }

                                    ((MainActivity) getActivity()).addEventPerformersWithServices(MainActivity.activeModel.getId(), performersUpd, EventFragment.this);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (MainActivity.createEventModel.getOffline_guests() != null)
                                    for (final String p : MainActivity.createEventModel.getOffline_guests()) {
                                        if (p.contains("@")) {
                                            ((MainActivity) getActivity()).inviteGuest(p, MainActivity.activeModel.getId());
                                        } else {
                                            ((MainActivity) getActivity()).addGuest(p, MainActivity.activeModel.getId());
                                        }
                                    }

                                MainActivity.createEventModel.setOffline_performers(null);
                                MainActivity.createEventModel.setOffline_guests(null);
                                MainActivity.createEventModel.setOffline_services(null);

                                MainActivity.createEventModel = null;
                                MainActivity.activeModel = null;

//                                reload(response.body().getId());
//                                EventsListFragment performerListFragment = new EventsListFragment();
//                                // Insert the fragment by replacing any existing fragment
//                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                fragmentManager.beginTransaction()
//                                        .replace(R.id.content_frame, performerListFragment)
//                                        .commit();
                                MainActivity.showAllert("Succes", "Event created");

                            } else {
                                MainActivity.showAllert("Error", "Please fill all the fileds");
                            }
                        }

                        @Override
                        public void onFailure(Call<EventModel> call, Throwable t) {
                            call.toString();
                            ((MainActivity) getActivity()).removeView();
                        }
                    });
                } else {
                    if (MainActivity.activeModel != null) {
                        ((MainActivity) getActivity()).connectLogin();

//                        if (MainActivity.activeModel.getOffline_performers() != null) {
//                            for (final EventPerformerModel p : MainActivity.activeModel.getOffline_performers()) {
//                                ((MainActivity) getActivity()).addPerformerLink(p.getPerformerId(), MainActivity.activeModel.getId());
//                            }
//                            MainActivity.activeModel.getOffline_performers().clear();
//                        }
//                        if (MainActivity.activeModel.getOffline_services() != null) {
//                            for (final ServiceRespondModel p : MainActivity.activeModel.getOffline_services()) {
//                                ((MainActivity) getActivity()).addEventService(p.getService().getId(), p.getPerformerId(), MainActivity.activeModel.getId(), p.getDuration());
//                            }
//                            MainActivity.activeModel.getOffline_services().clear();
//                        }

                        try {


                            JSONArray performersUpd = new JSONArray();

                            if (MainActivity.activeModel != null) {
                                if (MainActivity.activeModel.getPerformers() != null) {
                                    for (final EventPerformerModel p : MainActivity.activeModel.getPerformers()) {
                                        JSONObject perfor = new JSONObject();
                                        perfor.put("performerId", p.getPerformerId());
                                        JSONArray performerServ = new JSONArray();
                                        if (MainActivity.activeModel.getOffline_services() != null) {
                                            for (final ServiceRespondModel q : MainActivity.activeModel.getOffline_services()) {
                                                if (q.getPerformerId().equalsIgnoreCase(p.getPerformerId())) {
                                                    JSONObject servic = new JSONObject();
                                                    servic.put("serviceId", q.getService().getId());
                                                    servic.put("duration", q.getDuration());
                                                    performerServ.put(servic);
                                                }

                                            }

                                        }

                                        for (final ServiceRespondModel q : p.getServices()) {

                                            JSONObject servic = new JSONObject();
                                            servic.put("serviceId", q.getService().getId());
                                            servic.put("duration", q.getDuration());
                                            performerServ.put(servic);


                                        }

                                        perfor.put("services", performerServ);

                                        performersUpd.put(perfor);

                                    }
                                }

                                if (MainActivity.activeModel.getOffline_performers() != null) {
                                    for (final EventPerformerModel p : MainActivity.activeModel.getOffline_performers()) {
                                        JSONObject perfor = new JSONObject();
                                        perfor.put("performerId", p.getPerformerId());
                                        JSONArray performerServ = new JSONArray();
                                        if (MainActivity.activeModel.getOffline_services() != null) {
                                            for (final ServiceRespondModel q : MainActivity.activeModel.getOffline_services()) {
                                                if (q.getPerformerId().equalsIgnoreCase(p.getPerformerId())) {
                                                    JSONObject servic = new JSONObject();
                                                    servic.put("serviceId", q.getService().getId());
                                                    servic.put("duration", q.getDuration());
                                                    performerServ.put(servic);
                                                }

                                            }

                                        }
                                        perfor.put("services", performerServ);

                                        performersUpd.put(perfor);

                                    }
                                }

                            }

                            ((MainActivity) getActivity()).addEventPerformersWithServices(MainActivity.activeModel.getId(), performersUpd);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if (MainActivity.activeModel.getOffline_guests() != null)
                            for (final String p : MainActivity.activeModel.getOffline_guests()) {
                                if (p.contains("@")) {
                                    ((MainActivity) getActivity()).inviteGuest(p, MainActivity.activeModel.getId());
                                } else {
                                    ((MainActivity) getActivity()).addGuest(p, MainActivity.activeModel.getId());
                                }
                            }

                        MainActivity.activeModel.setOffline_performers(null);
                        MainActivity.activeModel.setOffline_guests(null);
                        MainActivity.activeModel.setOffline_services(null);

                        MainActivity.activeModel.setName(title_text.getText().toString());
                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                        Call<EventModel> call = service.updateEvent(MainActivity.activeModel, MainActivity.token);
                        call.enqueue(new Callback<EventModel>() {
                            @Override
                            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                ((MainActivity) getActivity()).removeView();
                                if (response.body() != null) {
                                    //model id: 5721f9e641c69aaa49c6109a
//                                    if (MainActivity.activeModel != null)
//                                        if (MainActivity.activeModel.getOffline_performers() != null)
//                                            for (final EventPerformerModel p : MainActivity.activeModel.getOffline_performers()) {
//                                                ((MainActivity) getActivity()).addPerformerLink(p.getPerformerId(), MainActivity.activeModel.getId());
//                                            }


                                    MainActivity.createEventModel = null;
                                    MainActivity.activeModel = null;
                                    reload(response.body().getId());
//                                    EventsListFragment performerListFragment = new EventsListFragment();
//                                    // Insert the fragment by replacing any existing fragment
//                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                    fragmentManager.beginTransaction()
//                                            .replace(R.id.content_frame, performerListFragment)
//                                            .commit();

                                    MainActivity.showAllert("Succes", "Event updated");

                                }
                            }

                            @Override
                            public void onFailure(Call<EventModel> call, Throwable t) {
                                call.toString();
                                ((MainActivity) getActivity()).removeView();
                            }
                        });

                    }
                }
            }
        });


        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }

    public void reload(final String id) {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<EventModel> call = service.getEvent(id, MainActivity.token);

        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {

                if (response.body() != null) {

                    MainActivity.activeModel = response.body();
                    MainActivity.activeModel.setOffline_performers(null);
                    MainActivity.activeModel.setOffline_guests(null);
                    MainActivity.activeModel.setOffline_services(null);

                    setUpActionBar();

                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
            }

        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        try {
            final View view = inflater.inflate(R.layout.fragment_create_event, container, false);


            location_text_view = ((TextView) view.findViewById(R.id.location_text_view));
            duration_text_view = ((TextView) view.findViewById(R.id.duration_text_view));
            date_text_view = ((TextView) view.findViewById(R.id.date_text_view));

            mDrawerList = ((ListView) getActivity().findViewById(R.id.list_view));
            mDrawerLayout = ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout));
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerRelativeLayout = ((RelativeLayout) getActivity().findViewById(R.id.navList_ce));
            getActivity().findViewById(R.id.navList).setVisibility(View.GONE);
            getActivity().findViewById(R.id.event_create).setVisibility(View.VISIBLE);

            view.findViewById(R.id.detail_layout).setBackgroundResource(YummiUtils.getStatusColor(0));
            ((TextView) view.findViewById(R.id.textView8)).setText(YummiUtils.getStatusDescription(0));

            if (MainActivity.activeModel != null) {
                view.findViewById(R.id.detail_layout).setBackgroundResource(YummiUtils.getStatusColor(MainActivity.activeModel.getStatus()));
                ((TextView) view.findViewById(R.id.textView8)).setText(YummiUtils.getStatusDescription(MainActivity.activeModel.getStatus()));
            }

//        if (MainActivity.activeModel != null) {
//            ((MainActivity) getActivity()).inviteGuest("adam89@rap.sk", MainActivity.activeModel.getId());
//            ((MainActivity) getActivity()).addGuest("adam89", MainActivity.activeModel.getId());
//        }
            mDrawerRelativeLayout.findViewById(R.id.add_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pressed == 0) {
                        PerformerListFragment performerListFragment = new PerformerListFragment();
                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, performerListFragment)
                                .commit();

                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    }
                    if (pressed == 1) {
                        EditText add_et = ((EditText) mDrawerRelativeLayout.findViewById(R.id.add_edit_text));
                        if (add_et != null) {
                            if (getActivity() != null) {
                                if (!add_et.getText().toString().isEmpty()) {
                                    if (MainActivity.activeModel != null) {
                                        if (!MainActivity.activeModel.getOffline_guests().contains(add_et.getText().toString()))
                                            MainActivity.activeModel.getOffline_guests().add(add_et.getText().toString());
                                    }
                                    if (MainActivity.createEventModel != null) {
                                        if (!MainActivity.createEventModel.getOffline_guests().contains(add_et.getText().toString()))
                                            MainActivity.createEventModel.getOffline_guests().add(add_et.getText().toString());
                                    }
                                    add_et.setText("");
                                    MainActivity.showAllert("Success", "Guest invited");
                                    ArrayList<GuestModel> result = new ArrayList<GuestModel>();
//                                    result.add(new GuestModel());

                                    if (MainActivity.activeModel != null) {
                                        for (GuestModel g : MainActivity.activeModel.getClients()) {
                                            result.add(g);
                                        }
                                        for (String str : MainActivity.activeModel.getOffline_guests()) {
                                            boolean in = false;
                                            for (GuestModel mg : result) {
                                                if (mg.getClient() != null)
                                                    if (mg.getClient().getStageName().equalsIgnoreCase(str))
                                                        in = true;
                                            }
                                            if (!in)
                                                result.add(new GuestModel("i", str));
                                        }
                                    }
                                    if (MainActivity.createEventModel != null) {
                                        for (String str : MainActivity.createEventModel.getOffline_guests()) {
                                            result.add(new GuestModel("i", str));
                                        }
                                    }

                                    GuestAdapter adapter = new GuestAdapter(getContext(), result);
                                    mDrawerList.setAdapter(adapter);
                                    mDrawerList.setOnItemClickListener(null);

                                }
                            }


                        }

                    }
                }
            });


            title_text = ((EditText) view.findViewById(R.id.editText));
            title_text.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (MainActivity.activeModel != null)
                        MainActivity.activeModel.setName(title_text.getText().toString());

                    if (MainActivity.createEventModel != null)
                        MainActivity.createEventModel.setName(title_text.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            return view;
        } catch (Exception e) {
            e.printStackTrace();

            EventFragment event = new EventFragment();
            MainActivity.where_go_back = 3;

            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, event)
                    .commit();

        }
        return inflater.inflate(R.layout.fragment_create_event_no_frag, container, false);
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        YummiUtils.currentlyOpenedEvent = "";
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpActionBar();
        MainActivity.where_go_back = 3;

        if (MainActivity.activeModel != null)
            YummiUtils.currentlyOpenedEvent = MainActivity.activeModel.getId();


        ((MainActivity) getActivity()).setMapEvent();

    }

    private int pressed = 0;

    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() == null) {
            EventsListFragment performerListFragment = new EventsListFragment();
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            MainActivity.where_go_back = 66;
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, performerListFragment)
                    .commit();

            return;
        }


        setUpActionBar();

        if (((MainActivity) getActivity()) != null)
            ((MainActivity) getActivity()).setUpMap((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        getView().findViewById(R.id.guests_button).post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((MainActivity) getActivity()).setMapEvent();

            }
        });

        final EditText edit_text_searh = (EditText) getActivity().findViewById(R.id.add_edit_text);
        edit_text_searh.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit_text_searh.getWindowToken(), 0);
                return true;

            }
        });

        getView().findViewById(R.id.guests_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressed = 1;

                getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.GONE);
                getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.GONE);
                getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_image_view).setVisibility(View.VISIBLE);

                getActivity().findViewById(R.id.add_edit_text).setVisibility(View.VISIBLE);
                ArrayList<GuestModel> result = new ArrayList<GuestModel>();
                result.add(new GuestModel());
                if (MainActivity.activeModel != null) {
                    for (GuestModel g : MainActivity.activeModel.getClients()) {
                        result.add(g);
                    }
                    for (String str : MainActivity.activeModel.getOffline_guests()) {
                        boolean in = false;
                        for (GuestModel mg : result) {
                            if (mg.getClient().getStageName().equalsIgnoreCase(str))
                                in = true;
                        }
                        if (!in)
                            result.add(new GuestModel("i", str));
                    }
                }
                if (MainActivity.createEventModel != null) {
                    for (String str : MainActivity.createEventModel.getOffline_guests()) {
                        result.add(new GuestModel("i", str));
                    }
                }


                GuestAdapter adapter = new GuestAdapter(getContext(), result);
                mDrawerList.setAdapter(adapter);
                mDrawerList.setOnItemClickListener(null);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });


        duration_text_view.setText(getDurationHours());

        getView().findViewById(R.id.notifications_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        getView().findViewById(R.id.performers_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressed = 0;

                getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.GONE);
                getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_image_view).setVisibility(View.VISIBLE);

                getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                    @Override
                    public void run() {
                        View view_instance = getActivity().findViewById(R.id.add_image_view);
                        android.view.ViewGroup.LayoutParams params = view_instance
                                .getLayoutParams();
                        if (getActivity() != null) {
                            params.height = (int) getActivity().getResources().getDimension(R.dimen.add_image_view_height);
                        } else {
                            params.height = 30;
                        }
                        view_instance.setLayoutParams(params);
                    }
                });

                getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.GONE);

                getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                ArrayList<PerformerModel> res = new ArrayList<PerformerModel>();
                res.add(new PerformerModel("Performers"));

                if (MainActivity.activeModel != null) {
                    for (EventPerformerModel p : MainActivity.activeModel.getPerformers())
                        if (p != null)
                            res.add(p.getPerformer());
                    if (MainActivity.activeModel.getOffline_performers() != null) {
                        for (EventPerformerModel p : MainActivity.activeModel.getOffline_performers()) {
                            boolean in = false;
                            for (PerformerModel pr : res)
                                if (pr != null)
                                    if (p.getPerformer().getId().equalsIgnoreCase(pr.getId()))
                                        in = true;

                            if (!in)
                                if (p != null)
                                    res.add(p.getPerformer());
                        }
                    }
                }

                if (MainActivity.createEventModel != null)
                    if (MainActivity.createEventModel.getOffline_performers() != null)
                        for (EventPerformerModel p : MainActivity.createEventModel.getOffline_performers())
                            res.add(p.getPerformer());

                PerformersAdapter adapter = new PerformersAdapter(EventFragment.this.getContext(), res);
                mDrawerList.setAdapter(adapter);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });


        getView().findViewById(R.id.services_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressed = 0;

                getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.VISIBLE);
                ((TextView) getActivity().findViewById(R.id.duration_summ_text_view)).setText(getDurationHours());
                getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                getActivity().findViewById(R.id.add_image_view).setVisibility(View.GONE);
                getStringFromServiceList();

            }
        });


        if (date_text_view.getText().toString().isEmpty())
            date_text_view.setText(getDateAsString());

        getView().findViewById(R.id.ladies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformerListFragment performerListFragment = new PerformerListFragment();

                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, performerListFragment)
                        .commit();
            }
        });


        date_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setFrg(EventFragment.this);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");


            }
        });


        getView().findViewById(R.id.location_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        if (MainActivity.activeModel == null) {
            if (MainActivity.createEventModel == null) {
                MainActivity.createEventModel = new CreateEventmodel();
                MainActivity.createEventModel.setOwnerId(MainActivity.userId);
            } else {
                location_text_view.setText(MainActivity.createEventModel.getAddress());
                date_text_view.setText(MainActivity.createEventModel.getStartAt());
                title_text.setText(MainActivity.createEventModel.getName());
            }
        } else {
            location_text_view.setText(MainActivity.activeModel.getAddress());
            date_text_view.setText(MainActivity.activeModel.getStartAt());
            title_text.setText(MainActivity.activeModel.getName());
        }


        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.next,  /* "open drawer" description */
                R.string.next  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                duration_text_view.setText(getDurationHours());
                if (getActivity() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.add_edit_text).getWindowToken(), 0);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);


    }


    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        if (monthOfYear == 0)
            monthOfYear = 1;
        date_text_view.setText(String.valueOf(dayOfMonth) + " " + getMonth(monthOfYear) + " " + String.valueOf(year));
        String month;
        String day;

        if (monthOfYear < 10)
            month = "-0" + String.valueOf(monthOfYear);
        else
            month = "-" + String.valueOf(monthOfYear);

        if (dayOfMonth < 10)
            day = "-0" + String.valueOf(dayOfMonth);
        else
            day = "-" + String.valueOf(dayOfMonth);

        if (MainActivity.createEventModel != null)
            MainActivity.createEventModel.setStartAt(String.valueOf(year) + month + day);

        if (MainActivity.activeModel != null)
            MainActivity.activeModel.setStartAt(String.valueOf(year) + month + day);

    }

    public void onTimeChanged(int hours, int minutes) {

        String min = "";
        if (minutes < 10)
            min = "0" + String.valueOf(minutes);
        else
            min = String.valueOf(minutes);


        String ho = "";
        if (hours < 10)
            ho = "0" + String.valueOf(hours);
        else
            ho = String.valueOf(hours);

        date_text_view.setText(date_text_view.getText() + ", " + ho + ":" + min);


        if (MainActivity.createEventModel != null)
            MainActivity.createEventModel.setStartAt(MainActivity.createEventModel.getStartAt() + " " + ho + ":" + min);


        if (MainActivity.activeModel != null)
            MainActivity.activeModel.setStartAt(MainActivity.activeModel.getStartAt() + " " + ho + ":" + min);

    }


    private String getDateAsString() {
        String Datetime;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Datetime = dateformat.format(c.getTime());

        if (MainActivity.createEventModel != null)
            MainActivity.createEventModel.setStartAt(Datetime);

        return Datetime;
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(getActivity(), data);
            final CharSequence name = place.getName();
//            final CharSequence address = place.getAddress();
//            String attributions = (String) place.getAttributions();
//            if (attributions == null) {
//                attributions = "";
//            }

            location_text_view.setText(place.getAddress());
            if (MainActivity.createEventModel != null) {
                MainActivity.createEventModel.setAddress(place.getAddress().toString());
                MainActivity.createEventModel.setLocation(new LocationModel(place.getLatLng().latitude, place.getLatLng().longitude));
            }

            if (MainActivity.activeModel != null) {
                MainActivity.activeModel.setAddress(place.getAddress().toString());
                MainActivity.activeModel.setLocation(new LocationModel(place.getLatLng().latitude, place.getLatLng().longitude));
            }


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(place.getLatLng()).zoom(20).build();

            if (((MainActivity) getActivity()).map != null) {
                ((MainActivity) getActivity()).map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                ((MainActivity) getActivity()).map.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .draggable(false));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        EventFragment frg;

        public EventFragment getFrg() {
            return frg;
        }

        public void setFrg(EventFragment frg) {
            this.frg = frg;
        }


        public String getMonth(int month) {
            return new DateFormatSymbols().getMonths()[month - 1];
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            int yearq = c.get(Calendar.YEAR);
            int monthq = c.get(Calendar.MONTH);
            int dayq = c.get(Calendar.DAY_OF_MONTH);

            if (year < yearq) {
                year = yearq;
            }
            if (year == yearq && month < monthq) {
                month = monthq;
            }
            if (year == yearq && month == monthq && day < dayq) {
                day = dayq;
            }

            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

//                            txtTime.setText(hourOfDay + ":" + minute);

                            frg.onTimeChanged(hourOfDay, minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
            frg.onDateChanged(view, year, month, day);

        }
    }

    private boolean isInTheList(String id, ArrayList<Service> result) {
        boolean isIn = false;
        for (Service serv : result) {
            if (serv.getService() != null)
                if (serv.getService().getId().equalsIgnoreCase(id))
                    isIn = true;

        }
        return isIn;
    }

    private boolean isInTheTypeList(String id, ArrayList<Service> result) {
        boolean isIn = false;
        for (Service serv : result) {
            if (serv.getService().getServiceType() != null)
                if (serv.getService().getServiceType().getId().equalsIgnoreCase(id))
                    isIn = true;

        }
        return isIn;
    }


    public void setUpSum() {
        if (getActivity().findViewById(R.id.duration_summ_text_view) != null)
            ((TextView) getActivity().findViewById(R.id.duration_summ_text_view)).setText(getDurationHours());

    }


    private ArrayList<PerformerModel> getCurrentPerformers() {
        ArrayList<PerformerModel> res = new ArrayList<PerformerModel>();

        if (MainActivity.activeModel != null) {
            for (EventPerformerModel p : MainActivity.activeModel.getPerformers())
                if (!res.contains(p.getPerformer()))
                    res.add(p.getPerformer());

            if (MainActivity.activeModel.getOffline_performers() != null)
                for (EventPerformerModel p : MainActivity.activeModel.getOffline_performers())
                    if (!res.contains(p.getPerformer()))
                        res.add(p.getPerformer());
        }

        if (MainActivity.createEventModel != null)
            if (MainActivity.createEventModel.getOffline_performers() != null)
                for (EventPerformerModel p : MainActivity.createEventModel.getOffline_performers())
                    if (!res.contains(p.getPerformer()))
                        res.add(p.getPerformer());

        return res;
    }

    private ArrayList<Service> getStringFromServiceList() {


        ArrayList<PerformerModel> res = getCurrentPerformers();


        final LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.performers_tab_bar);

        linearLayout.removeAllViews();

        linearLayout.setWeightSum(res.size());

        for (final PerformerModel p : res) {
            final TextView valueTV = new TextView(getActivity());
            valueTV.setText(p.getName());
            if (res.indexOf(p) == 0) {
                valueTV.setBackgroundResource(R.drawable.gold_performer_selector);
                valueTV.setTextColor(Color.BLACK);
            } else {
                valueTV.setBackgroundResource(R.drawable.grey_performer_selector);
                valueTV.setTextColor(Color.WHITE);
            }


            valueTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int count = linearLayout.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View v = linearLayout.getChildAt(i);
                        v.setBackgroundResource(R.drawable.grey_performer_selector);
                        ((TextView) v).setTextColor(Color.WHITE);
                    }

                    view.setBackgroundResource(R.drawable.gold_performer_selector);
                    ((TextView) view).setTextColor(Color.BLACK);
                    setUpServiceListView(getServiceAdapter(new ArrayList<Service>(), p));
                }
            });

            valueTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            valueTV.setGravity(Gravity.CENTER);

            linearLayout.addView(valueTV);
        }


        ArrayList<Service> result = new ArrayList<Service>();
        if (res.size() > 0)
            setUpServiceListView(getServiceAdapter(result, res.get(0)));

        return result;
    }

    private void setUpServiceListView(final ServiceAdapter adapter) {
        mDrawerList.setAdapter(adapter);
        mDrawerLayout.openDrawer(Gravity.RIGHT);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Service service = adapter.getItem(position);

                if (MainActivity.createEventModel != null) {
                    if (MainActivity.createEventModel.getOffline_services().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                        int ind = MainActivity.createEventModel.getOffline_services().indexOf(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                        if (MainActivity.createEventModel.getOffline_services().get(ind).getPerformerId().equals(service.getUid()))
                            return;
                    }
                }

                if (MainActivity.activeModel != null) {
                    if (MainActivity.activeModel.getOffline_services().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                        int ind = MainActivity.activeModel.getOffline_services().indexOf(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                        if (MainActivity.activeModel.getOffline_services().get(ind).getPerformerId().equals(service.getUid()))
                            return;
                    }
                }

                if (MainActivity.activeModel != null) {
                    for (EventPerformerModel prq : MainActivity.activeModel.getPerformers())
                        if (prq.getServices().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                            int ind = prq.getServices().indexOf(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                            if (prq.getServices().get(ind).getPerformerId().equals(service.getUid()))
                                return;
                        }
                }


//                if (MainActivity.activeModel != null && getActivity() != null)
//                    ((MainActivity) getActivity()).addService(s.getService().getId(), MainActivity.activeModel.getId(), s.getDuration());

                service.setSelected(true);
                service.setDuration(1.0);
                adapter.getItem(position).setDuration(1.0);
                adapter.getItem(position).setSelected(true);

                adapter.notifyDataSetChanged();

                if (!service.getService().getId().isEmpty()) {

                    if (MainActivity.activeModel != null) {

                        if (MainActivity.activeModel.getOffline_services().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                            MainActivity.activeModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                        }
                        ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                        added.setPrice(service.getPrice());
                        MainActivity.activeModel.getOffline_services().add(added);


                    } else if (MainActivity.createEventModel != null) {


                        if (MainActivity.createEventModel.getOffline_services().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                            MainActivity.createEventModel.getOffline_services().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                        }
                        ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                        added.setPrice(service.getPrice());
                        MainActivity.createEventModel.getOffline_services().add(added);
                    }

                    final TextView duartion = (TextView) view.findViewById(R.id.textView20);
                    final TextView time_unit = (TextView) view.findViewById(R.id.textView21);
                    final ImageView minus = (ImageView) view.findViewById(R.id.imageView7);
                    final ImageView plus = (ImageView) view.findViewById(R.id.imageView6);
                    final ImageView mark = (ImageView) view.findViewById(R.id.imageView5);

                    duartion.setVisibility(View.VISIBLE);
                    time_unit.setVisibility(View.VISIBLE);
                    minus.setVisibility(View.VISIBLE);
                    plus.setVisibility(View.VISIBLE);
                    mark.setVisibility(View.VISIBLE);


//                    if (duartion != null)
//                        duartion.setText("0.5");

//                    if (MainActivity.activeModel != null)
//                        MainActivity.activeModel.getOffline_services().add(new ServiceRespondModel(service.getService(), service.getDuration()));
//
//                    if (MainActivity.createEventModel != null)
//                        MainActivity.createEventModel.getOffline_services().add(new ServiceRespondModel(service.getService(), service.getDuration()));


                    setUpSum();
                }
            }
        });
    }

    @NonNull
    private ServiceAdapter getServiceAdapter(ArrayList<Service> result, PerformerModel performer) {
        for (Service serv : performer.getServices()) {
            if (serv.getService().getServiceType() != null)
                if (!isInTheTypeList(serv.getService().getServiceType().getId(), result)) {

                    boolean in = false;
                    for (Service s : result)
                        if (s.getService().getName().equalsIgnoreCase(serv.getService().getServiceType().getName()))
                            in = true;
                    int rr = 0;
                    for (Service s : performer.getServices()) {
                        if (s.getService().getServiceType().getId().equalsIgnoreCase(serv.getService().getServiceType().getId())) {
                            if (!isInTheList(s.getService().getId(), result)) {
                                if (s.getAvailability() == 2)
                                    rr++;
                            }
                        }
                    }

                    if (!in && rr > 0) {
                        Service f = new Service(2, new ServiceModel(serv.getService().getServiceType().getName(), "", ""));
                        for (PriceModel priceModel : performer.getPrices())
                            if (priceModel.getServiceTypeId().equals(serv.getService().getServiceTypeId()))
                                f.setPrice(priceModel.getAmount());
                        f.setUid(performer.getId());
                        result.add(f);
                    }
                }
            for (Service s : performer.getServices()) {
                if (s.getService().getServiceType() != null)
                    if (s.getService().getServiceType().getId().equalsIgnoreCase(serv.getService().getServiceType().getId())) {
                        if (!isInTheList(s.getService().getId(), result)) {
                            if (s.getAvailability() == 2) {
                                if (MainActivity.activeModel != null) {
                                    for (EventPerformerModel prq : MainActivity.activeModel.getPerformers())
                                        for (ServiceRespondModel srm : prq.getServices()) {
                                            if (srm.getService().getId().equalsIgnoreCase(s.getService().getId())) {
                                                if (prq.getPerformerId().equalsIgnoreCase(performer.getId())) {
                                                    s.setSelected(true);
                                                    s.setDuration(srm.getDuration());
                                                }
                                            }
                                        }
                                    for (ServiceRespondModel srm : MainActivity.activeModel.getOffline_services()) {
                                        if (srm.getService().getId().equalsIgnoreCase(s.getService().getId())) {
                                            if (srm.getPerformerId().equalsIgnoreCase(performer.getId())) {
                                                s.setSelected(true);
                                                s.setDuration(srm.getDuration());
                                            }
                                        }
                                    }
                                }
                                if (MainActivity.createEventModel != null) {
                                    for (ServiceRespondModel srm : MainActivity.createEventModel.getOffline_services()) {
                                        if (srm.getService().getId().equalsIgnoreCase(s.getService().getId())) {
                                            if (srm.getPerformerId().equalsIgnoreCase(performer.getId())) {
                                                s.setSelected(true);
                                                s.setDuration(srm.getDuration());
                                            }
                                        }
                                    }
                                }

                                s.setUid(performer.getId());
                                for (PriceModel priceModel : performer.getPrices())
                                    if (priceModel.getServiceTypeId().equals(serv.getService().getServiceTypeId()))
                                        s.setPrice(priceModel.getAmount());
                                result.add(s);
                            }
                        }
                    }
            }
//            }

        }

        final ServiceAdapter adapter = new ServiceAdapter((MainActivity) getActivity(), result);
        adapter.setFragment(this);
        return adapter;
    }


    private String getDurationHours() {

        double sum = 0.0;
        int price = 0;

        ArrayList<Service> result = new ArrayList<Service>();
        int i = 0;
        if (MainActivity.activeModel != null) {
            for (EventPerformerModel prq : MainActivity.activeModel.getPerformers()) {
                for (ServiceRespondModel s : prq.getServices()) {
                    sum += s.getDuration();
                    int ServPrice = 0;

                    for (PriceModel sr : prq.getPerformer().getPrices())
                        if (sr.getServiceTypeId().equalsIgnoreCase(s.getService().getServiceTypeId()))
                            ServPrice = sr.getAmount();

                    price += (s.getDuration() * ServPrice);
                    i++;
                }
            }
            for (ServiceRespondModel s : MainActivity.activeModel.getOffline_services()) {
                sum += s.getDuration();
                price += (s.getDuration() * s.getPrice());
                i++;
            }
            if (getActivity() != null)
                if (getActivity().findViewById(R.id.service_count_text_view) != null) {
                    int size = 0;
                    for (EventPerformerModel prq : MainActivity.activeModel.getPerformers())
                        size += prq.getServices().size();

                    ((TextView) getActivity().findViewById(R.id.service_count_text_view)).setText(String.valueOf(size + MainActivity.activeModel.getOffline_services().size()));
                }

        }
        if (MainActivity.createEventModel != null) {
            for (ServiceRespondModel s : MainActivity.createEventModel.getOffline_services()) {
                sum += s.getDuration();
                price += (s.getDuration() * s.getPrice());
                i++;
            }

            if (getActivity() != null)
                if (getActivity().findViewById(R.id.service_count_text_view) != null) {
                    ((TextView) getActivity().findViewById(R.id.service_count_text_view)).setText(String.valueOf(MainActivity.createEventModel.getOffline_services().size()));
                }
        }
        if (getActivity() != null)
            if (getActivity().findViewById(R.id.price_text_view) != null) {
                ((TextView) getActivity().findViewById(R.id.price_text_view)).setText(
                        String.valueOf(price) + " $"
                );
            }

        return String.valueOf(sum) + " h";
    }

}
