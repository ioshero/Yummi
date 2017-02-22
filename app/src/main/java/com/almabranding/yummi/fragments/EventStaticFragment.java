package com.almabranding.yummi.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.GuestAdapter;
import com.almabranding.yummi.adapter.PerformersAdapter;
import com.almabranding.yummi.adapter.PerformersLEAdapter;
import com.almabranding.yummi.adapter.ServiceAdapter;
import com.almabranding.yummi.adapter.ServiceShowAdapter;
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
import com.almabranding.yummi.utils.CounterClass;
import com.almabranding.yummi.utils.InputFilterMinMax;
import com.almabranding.yummi.utils.YummiUtils;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import org.json.JSONArray;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ioshero on 27/04/16.
 */
public class EventStaticFragment extends Fragment {


    private static final int PLACE_PICKER_REQUEST = 188;

    TextView location_text_view;
    TextView duration_text_view;
    TextView date_text_view;
    EditText title_text;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelativeLayout;

    EventModel eventModel = null;

    public void setEventModel(EventModel eventModel) {
        this.eventModel = eventModel;
    }


    private boolean isStarted() {
        if (eventModel == null)
            return false;

        return eventModel.getStatus() == 505;
    }

    SwipeMenuListView listView;


    @Override
    public void onStart() {
        super.onStart();
//        mDrawerLayout = ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout));
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.navList_ce));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        getActivity().findViewById(R.id.navList).setVisibility(View.GONE);
        getActivity().findViewById(R.id.event_create).setVisibility(View.VISIBLE);
        try {
            if (isStarted()) {
                final View view = inflater.inflate(R.layout.fragment_live_event, container, false);

                mDrawerLayout = ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout));

                if (YummiUtils.isPreformer(getActivity()))
                    ((LinearLayout) view.findViewById(R.id.layout_two_buttons)).setWeightSum(1.0f);
                else
                    ((LinearLayout) view.findViewById(R.id.layout_two_buttons)).setWeightSum(1.9f);

                view.findViewById(R.id.problem_button).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {


                            getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                            getActivity().findViewById(R.id.problem_layout).setVisibility(View.VISIBLE);
                            getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                                @Override
                                public void run() {
                                    View view_instance = getActivity().findViewById(R.id.add_image_view);
                                    android.view.ViewGroup.LayoutParams params = view_instance
                                            .getLayoutParams();
                                    params.height = 0;
                                    view_instance.setLayoutParams(params);
                                }
                            });


                            getActivity().findViewById(R.id.problem_button_rep).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.addToBackStack(null);
                                    // Create and show the dialog.
                                    ConflictResolveDialogFragment newFragment = new ConflictResolveDialogFragment();
//                                    newFragment.setwAct(WelcomeActivity.this);
//                                    newFragment.setEventModel(null);
                                    newFragment.show(ft, "dialog");
                                }
                            });


                            getActivity().findViewById(R.id.problem_button_canc).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ((MainActivity) getActivity()).cancelEvent(eventModel.getId());
                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("NO", null);

                                    alertDialogBuilder.setTitle("Are you sure you want to cancel the event?");

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });

                            mDrawerLayout.openDrawer(Gravity.RIGHT);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });


//                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, getActivity().findViewById(R.id.navList_ce));

                listView = (SwipeMenuListView) view.findViewById(R.id.performer_list);

//        View emptyView = inflater.inflate(R.layout.empty_list, null, false);
//        getActivity().addContentView(emptyView, listView.getLayoutParams()); // You're using the same params as listView
//        listView.setEmptyView(emptyView);

                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {

                        switch (menu.getViewType()) {
                            case 0: {
                                SwipeMenuItem openItem = new SwipeMenuItem(
                                        getActivity().getApplicationContext());
                                // set item background
                                openItem.setBackground(new ColorDrawable(Color.rgb(0xC7, 0xC7,
                                        0xCC)));
                                // set item width
                                openItem.setWidth((int) (90 * EventStaticFragment.this.getContext().getResources().getDisplayMetrics().density));
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

                        }


                    }
                };

// set creator
                listView.setMenuCreator(creator);


                return view;
            } else {

                final View view = inflater.inflate(R.layout.fragment_create_event, container, false);
                location_text_view = ((TextView) view.findViewById(R.id.location_text_view));
                duration_text_view = ((TextView) view.findViewById(R.id.duration_text_view));

                date_text_view = ((TextView) view.findViewById(R.id.date_text_view));

                mDrawerList = ((ListView) getActivity().findViewById(R.id.list_view));
                mDrawerLayout = ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout));
//                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,getActivity().findViewById(R.id.navList_ce));
                mDrawerRelativeLayout = ((RelativeLayout) getActivity().findViewById(R.id.navList_ce));

                title_text = ((EditText) view.findViewById(R.id.editText));

                view.findViewById(R.id.detail_layout).setBackgroundResource(YummiUtils.getStatusColor(0));
                ((TextView) view.findViewById(R.id.textView8)).setText(YummiUtils.getStatusDescription(0));
                if (eventModel != null) {
                    view.findViewById(R.id.detail_layout).setBackgroundResource(YummiUtils.getStatusColor(eventModel.getStatus()));
                    ((TextView) view.findViewById(R.id.textView8)).setText(YummiUtils.getStatusDescription(eventModel.getStatus()));
                }

                title_text.setEnabled(false);


                return view;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventsListFragment fragment = new EventsListFragment();
            fragment.setMainActivity((MainActivity) getActivity());
//        Bundle args = new Bundle();
////        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }

        return inflater.inflate(R.layout.fragment_create_event_no_frag, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        YummiUtils.currentlyOpenedEvent = "";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.where_go_back != 4)
            MainActivity.where_go_back = 3;
        ((MainActivity) getActivity()).setMapEvent(eventModel);
        if (eventModel != null)
            YummiUtils.currentlyOpenedEvent = eventModel.getId();
        if (isStarted())
            startTimer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isStarted())
            stoptimertask(null);
    }

    CounterClass counterClass;

    public void startTimer() {

        counterClass = new CounterClass(getDurationInMillis(), 1000, ((TextView) getView().findViewById(R.id.textViewTime)));
        counterClass.setActivity((MainActivity) getActivity());
        counterClass.start();

//        timer = new Timer();
//
//        initializeTimerTask();
//
//        timer.schedule(timerTask, 500, 1000);

    }


    public void stoptimertask(View v) {


        if (timer != null) {

            timer.cancel();

            timer = null;

        }

    }


    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                handler.post(new Runnable() {

                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        final String strDate = simpleDateFormat.format(calendar.getTime());
                        if (getView() != null)
                            ((TextView) getView().findViewById(R.id.textViewTime)).setText(strDate);
                        else
                            stoptimertask(null);

                    }
                });
            }
        };
    }


    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setUpActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview_done);

        TextView title_view = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);

        title.setVisibility(View.INVISIBLE);
        title_view.setVisibility(View.VISIBLE);


        if (eventModel == null) {
            ((MainActivity) getActivity()).setUpEventsListFragment();
            return;
        }

        title_view.setText(eventModel.getName());
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);
        TextView rej = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview);
        rej.setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);


        switch (eventModel.getStatus()) {
            case 0: { // pending
                actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                title.setText("ACCEPT");
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).acceptEvent(eventModel.getId(), EventStaticFragment.this);

                    }
                });
                rej.setVisibility(View.VISIBLE);
                rej.setText("REJECT");
                rej.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).rejectEvent(eventModel.getId(), EventStaticFragment.this);

                    }
                });
                break;
            }

            case 1: {

                if (!YummiUtils.isPreformer(getContext()) && eventModel.getOwnerId().equalsIgnoreCase(MainActivity.userId)) {
                    title.setVisibility(View.VISIBLE);
                    title.setText("PAY");
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (YummiUtils.client.getCards().size() > 0) {
                                ((MainActivity) getActivity()).payEvent(eventModel.getId(), null, 1000, EventStaticFragment.this);
                                return;
                            }

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.addToBackStack(null);
                            // Create and show the dialog.
                            CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
                            newFragment.setEventModel(eventModel);
                            newFragment.show(ft, "dialog");


                        }
                    });
                }

                break;
            }

            case 2: {

                if (!eventModel.getOwnerId().equalsIgnoreCase(MainActivity.userId)) {
                    title.setVisibility(View.VISIBLE);
                    title.setText("CHECK IN");
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ((MainActivity) getActivity()).checkInEvent(eventModel.getId(), EventStaticFragment.this);

                        }
                    });
                }

                break;
            }

            case 5: {


                title.setVisibility(View.VISIBLE);
                title.setText("LIVE");
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventStaticFragment event = new EventStaticFragment();
                        EventModel ne = eventModel;
                        ne.setStatus(505);
                        MainActivity.where_go_back = 4;
                        event.setEventModel(ne);
                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .hide(EventStaticFragment.this)
                                .replace(R.id.content_frame, event)
                                .commit();


                    }
                });


                break;
            }

            case 3: {
                if (!YummiUtils.isPreformer(getContext()) && eventModel.getOwnerId().equalsIgnoreCase(MainActivity.userId)) {
                    title.setVisibility(View.VISIBLE);
                    title.setText("START");
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).startEvent(eventModel.getId(), EventStaticFragment.this);
                        }
                    });
                }

                break;
            }

        }


        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }


    public void restartFragment() {
        EventStaticFragment event = new EventStaticFragment();
        event.setEventModel(eventModel);
        MainActivity.where_go_back = 3;

        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, event)
                .commit();
    }


    Timer timer;

    TimerTask timerTask;

    final Handler handler = new Handler();
    JSONArray tips = new JSONArray();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isStarted()) {

            ((MainActivity) getActivity()).mSocket.on("tipLeaderBoard", new Emitter.Listener() {
                        @Override
                        public void call(final Object... args) {
                            //576a3476824a19ef071b911a
                            //[{"total":150,"id":"576952a93ca9627604a67889"}]
                            try {
                                String str = (String) args[0];
                                if (str.equalsIgnoreCase(YummiUtils.currentlyOpenedEvent)) {
                                    tips = (JSONArray) args[1];
                                    setUpLEPerformerList();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
            );


            listView.setCloseInterpolator(new BounceInterpolator());
            listView.setOpenInterpolator(new BounceInterpolator());
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0: {
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.tip_change_dialog);
                            dialog.setTitle("Change tip amount");

                            final EditText et = (EditText) dialog.findViewById(R.id.editTextcardnumber);
                            et.setFilters(new InputFilter[]{new InputFilterMinMax("1", "500")});

                            Button dialogButton = (Button) dialog.findViewById(R.id.button_pay_card);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    performersLEAdapter.getItem(position).setTip(Integer.parseInt(et.getText().toString()));
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((SwipeMenuListView) getActivity().findViewById(R.id.performer_list)).setAdapter(performersLEAdapter);
                                            performersLEAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });
                            dialog.show();
                            break;
                        }

                    }
                    return false;
                }
            });

            ((MainActivity) getActivity()).mSocket.emit("tipLeaderBoard", eventModel.getId(), new Ack() {
                @Override
                public void call(Object... args) {
                    try {
                        tips = (JSONArray) args[0];
                        setUpLEPerformerList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            setUpActionBar();
            getView().findViewById(R.id.services_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.VISIBLE);
                    ((TextView) getActivity().findViewById(R.id.duration_summ_text_view)).setText(getDurationHours());
                    ((TextView) getActivity().findViewById(R.id.price_text_view)).setText(getPriceHours());


                    getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                        @Override
                        public void run() {
                            View view_instance = getActivity().findViewById(R.id.add_image_view);
                            android.view.ViewGroup.LayoutParams params = view_instance
                                    .getLayoutParams();
                            params.height = 0;
                            view_instance.setLayoutParams(params);
                        }
                    });

                    getStringFromServiceListLE();

                }
            });

//            setUpLEPerformerList();

        } else {
            setUpActionBar();

            if (eventModel == null) {
                ((MainActivity) getActivity()).setUpEventsListFragment();
                return;
            }

            ((MainActivity) getActivity()).setUpMap((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

            duration_text_view.setText(getDurationHours());
            ((TextView) getActivity().findViewById(R.id.price_text_view)).setText(getPriceHours());

            getView().findViewById(R.id.guests_button).post(new Runnable() {
                @Override
                public void run() {
                    if (eventModel != null)
                        if (((MainActivity) getActivity()) != null)
                            ((MainActivity) getActivity()).setMapEvent(eventModel);
                }
            });

            getView().findViewById(R.id.performers_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.GONE);

                    getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
//                getView().findViewById(R.id.add_image_view).setVisibility(View.INVISIBLE);
                    getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                        @Override
                        public void run() {
                            View view_instance = getActivity().findViewById(R.id.add_image_view);
                            android.view.ViewGroup.LayoutParams params = view_instance
                                    .getLayoutParams();
                            params.height = 0;
                            view_instance.setLayoutParams(params);
                        }
                    });

                    getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.GONE);

                    ArrayList<PerformerModel> res = new ArrayList<PerformerModel>();
                    res.add(new PerformerModel("Performers"));

                    if (eventModel != null) {
                        for (EventPerformerModel p : eventModel.getPerformers())
                            res.add(p.getPerformer());

                    }

                    PerformersAdapter adapter = new PerformersAdapter(EventStaticFragment.this.getContext(), res);
                    mDrawerList.setAdapter(adapter);
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            });

            getView().findViewById(R.id.services_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                    ((TextView) getActivity().findViewById(R.id.duration_summ_text_view)).setText(getDurationHours());
                    ((TextView) getActivity().findViewById(R.id.price_text_view)).setText(getPriceHours());

                    getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                        @Override
                        public void run() {
                            View view_instance = getActivity().findViewById(R.id.add_image_view);
                            android.view.ViewGroup.LayoutParams params = view_instance
                                    .getLayoutParams();
                            params.height = 0;
                            view_instance.setLayoutParams(params);
                        }
                    });
                    getStringFromServiceList();

                }
            });

            getView().findViewById(R.id.guests_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().findViewById(R.id.relative_summ_layput).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.performers_tab_bar).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.add_edit_text).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.problem_layout).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.add_image_view).post(new Runnable() {
                        @Override
                        public void run() {
                            View view_instance = getActivity().findViewById(R.id.add_image_view);
                            android.view.ViewGroup.LayoutParams params = view_instance
                                    .getLayoutParams();
                            params.height = 0;
                            view_instance.setLayoutParams(params);
                        }
                    });

                    ArrayList<GuestModel> result = new ArrayList<GuestModel>();

                    if (eventModel != null)
                        for (GuestModel g : eventModel.getClients()) {
                            result.add(g);
                        }


                    GuestAdapter adapter = new GuestAdapter(getContext(), result);
                    mDrawerList.setAdapter(adapter);
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            });


            if (date_text_view.getText().toString().isEmpty())
                date_text_view.setText(getDateAsString());


            location_text_view.setText(eventModel.getAddress());
            date_text_view.setText(eventModel.getStartAtString());
            title_text.setText(eventModel.getName());

        }
    }

    PerformersLEAdapter performersLEAdapter;

    public void setUpLEPerformerList() {

        if (!YummiUtils.isPreformer(getActivity())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PerformerModel> res = new ArrayList<PerformerModel>();
//            res.add(new PerformerModel("Performers"));

                    if (eventModel != null) {
                        for (EventPerformerModel p : eventModel.getPerformers()) {
                            PerformerModel per = p.getPerformer();
                            if (per.getTip() == 0)
                                per.setTip(10);
                            try {
                                for (int i = 0; i < tips.length(); i++) {

                                    if (tips.getJSONObject(i).getString("id").equalsIgnoreCase(per.getId())) {
                                        per.setTipPrice(tips.getJSONObject(i).getInt("total"));
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            res.add(per);
                        }
                    }

                    Collections.sort(res, new Comparator<PerformerModel>() {
                        @Override
                        public int compare(PerformerModel pr1, PerformerModel performerModel) {

                            if (pr1.getTipPrice() == 0 && performerModel.getTipPrice() == 0) {
                                return pr1.getName().compareTo(performerModel.getName());
                            } else {
                                if (pr1.getTipPrice() > performerModel.getTipPrice()) {
                                    return 1;
                                }

                                if (pr1.getTipPrice() < performerModel.getTipPrice()) {
                                    return -1;
                                }

                                return pr1.getName().compareTo(performerModel.getName());
                            }
                        }
                    });

                    performersLEAdapter = new PerformersLEAdapter(getContext(), res, EventStaticFragment.this);
                    ((SwipeMenuListView) getActivity().findViewById(R.id.performer_list)).setAdapter(performersLEAdapter);
                    performersLEAdapter.notifyDataSetChanged();
                }
            });
        } else {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<PerformerModel> res = new ArrayList<PerformerModel>();
                    res.add(new PerformerModel(eventModel.getOwner().getStageName()));

                    performersLEAdapter = new PerformersLEAdapter(getContext(), res, EventStaticFragment.this);
                    ((ListView) getActivity().findViewById(R.id.performer_list)).setAdapter(performersLEAdapter);
                    performersLEAdapter.notifyDataSetChanged();

                }
            });


        }
    }


    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
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

    }


    private String getDateAsString() {
        String Datetime;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Datetime = dateformat.format(c.getTime());

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


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(place.getLatLng()).zoom(20).build();
            ((MainActivity) getActivity()).map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            ((MainActivity) getActivity()).map.getUiSettings().setScrollGesturesEnabled(false);
            ((MainActivity) getActivity()).map.setMyLocationEnabled(false);
            ((MainActivity) getActivity()).map.getUiSettings().setZoomControlsEnabled(false);
            ((MainActivity) getActivity()).map.getUiSettings().setZoomControlsEnabled(false);

            ((MainActivity) getActivity()).map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

        EventStaticFragment frg;

        public EventStaticFragment getFrg() {
            return frg;
        }

        public void setFrg(EventStaticFragment frg) {
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


    private long getDurationInMillis() {


        long millis = eventModel.getEndAtDate().getTime() - new Date().getTime();
//        for (EventPerformerModel pr : eventModel.getPerformers())
//            for (ServiceRespondModel s : pr.getServices()) {
//                millis += (long) (3600000 * s.getDuration());
//            }


        return millis;

    }

    private String getDurationHours() {

        double sum = 0.0;

        ArrayList<Service> result = new ArrayList<Service>();


        for (EventPerformerModel pr : eventModel.getPerformers())
            for (ServiceRespondModel s : pr.getServices()) {
                sum += s.getDuration();

            }

        return String.valueOf(sum) + " h";
    }


    private String getPriceHours() {

        int sum = 0;

        ArrayList<Service> result = new ArrayList<Service>();


        for (EventPerformerModel prq : eventModel.getPerformers())
            for (ServiceRespondModel s : prq.getServices()) {
                int ServPrice = 0;
                for (EventPerformerModel pr : eventModel.getPerformers())
                    if (pr.getPerformer().getId().equalsIgnoreCase(s.getPerformerId()))
                        for (PriceModel sr : pr.getPerformer().getPrices())
                            if (sr.getServiceTypeId().equalsIgnoreCase(s.getService().getServiceTypeId()))
                                ServPrice = sr.getAmount();

                sum += (s.getDuration() * ServPrice);

            }

        return String.valueOf(sum) + " $";
    }

    private ArrayList<Service> getStringFromServiceList() {

        ArrayList<Service> result = new ArrayList<Service>();

        ArrayList<EventPerformerModel> resz = new ArrayList<>();
        for (EventPerformerModel p : eventModel.getPerformers())
            if (!resz.contains(p.getPerformer()))
                resz.add(p);


        final LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.performers_tab_bar);
        if (linearLayout != null) {
            linearLayout.removeAllViews();

            linearLayout.setWeightSum(resz.size());

            for (final EventPerformerModel p : resz) {
                final TextView valueTV = new TextView(getActivity());
                valueTV.setText(p.getPerformer().getName());
                if (resz.indexOf(p) == 0) {
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

                        int i = 0;

                        ArrayList<Service> resul = new ArrayList<Service>();

                        for (Service serv : p.getPerformer().getServices()) {
                            if (serv.getService().getServiceType() != null)
                                if (!isInTheTypeList(serv.getService().getServiceType().getId(), resul)) {
                                    Service f = new Service(2, new ServiceModel(serv.getService().getServiceType().getName(), "", ""));
                                    boolean in = false;
                                    for (Service s : resul)
                                        if (s.getService().getName().equalsIgnoreCase(serv.getService().getServiceType().getName()))
                                            in = true;
                                    int rr = 0;
                                    for (EventPerformerModel prq : eventModel.getPerformers())
                                        for (ServiceRespondModel s : prq.getServices()) {
                                            if (s.getService().getServiceTypeId().equalsIgnoreCase(serv.getService().getServiceTypeId())) {
                                                if (!isInTheList(s.getService().getId(), resul)) {
                                                    if (s.getPerformerId().equalsIgnoreCase(p.getId()))
                                                        rr++;
                                                }
                                            }
                                        }

                                    if (!in && rr > 0) {
                                        //here
                                        for (PriceModel priceModel : p.getPerformer().getPrices())
                                            if (priceModel.getServiceTypeId().equals(serv.getService().getServiceType().getId()))
                                                f.setPrice(priceModel.getAmount());
                                        f.setUid(p.getId());
                                        resul.add(f);
                                    }
                                }


                            for (EventPerformerModel prq : eventModel.getPerformers())
                                for (ServiceRespondModel s : prq.getServices()) {
                                    if (s.getService().getServiceTypeId().equalsIgnoreCase(serv.getService().getServiceType().getId()))
                                        if (p.getId().equalsIgnoreCase(s.getPerformerId())) {
                                            Service res = new Service(2, s.getService());
                                            res.setDuration(s.getDuration());
                                            if (!resul.contains(res))
                                                resul.add(res);
                                        }
                                    i++;
                                }
                        }

                        int size = 0;
                        for (EventPerformerModel prq : eventModel.getPerformers())
                            size += prq.getServices().size();

                        if (getView().findViewById(R.id.service_count_text_view) != null) {
                            ((TextView) getView().findViewById(R.id.service_count_text_view)).setText(String.valueOf(size));
                        }

                        final ServiceShowAdapter adapter = new ServiceShowAdapter((MainActivity) getActivity(), resul);
                        mDrawerList.setAdapter(adapter);
                    }
                });

                valueTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                valueTV.setGravity(Gravity.CENTER);

                linearLayout.addView(valueTV);
            }
        }

        if (resz.size() > 0) {
            EventPerformerModel first = resz.get(0);


            for (Service serv : first.getPerformer().getServices()) {
                if (serv.getService().getServiceType() != null)
                    if (!isInTheTypeList(serv.getService().getServiceType().getId(), result)) {
                        Service f = new Service(2, new ServiceModel(serv.getService().getServiceType().getName(), "", ""));
                        boolean in = false;
                        for (Service s : result)
                            if (s.getService().getName().equalsIgnoreCase(serv.getService().getServiceType().getName()))
                                in = true;
                        int rr = 0;
                        for (EventPerformerModel prq : eventModel.getPerformers())
                            for (ServiceRespondModel s : prq.getServices()) {
                                if (s.getService().getServiceTypeId().equalsIgnoreCase(serv.getService().getServiceTypeId())) {
                                    if (!isInTheList(s.getService().getId(), result)) {
                                        if (s.getPerformerId().equalsIgnoreCase(eventModel.getPerformers().get(0).getId()))
                                            rr++;
                                    }
                                }
                            }

                        if (!in && rr > 0) {
                            //here
                            for (PriceModel priceModel : first.getPerformer().getPrices())
                                if (priceModel.getServiceTypeId().equals(serv.getService().getServiceType().getId()))
                                    f.setPrice(priceModel.getAmount());
                            f.setUid(first.getId());
                            result.add(f);
                        }
                    }

                for (EventPerformerModel prq : eventModel.getPerformers())
                    for (ServiceRespondModel s : prq.getServices()) {
                        if (s.getService().getServiceTypeId().equalsIgnoreCase(serv.getService().getServiceType().getId()))
                            if (eventModel.getPerformers().get(0).getId().equalsIgnoreCase(s.getPerformerId())) {
                                Service res = new Service(2, s.getService());
                                res.setDuration(s.getDuration());
                                if (!result.contains(res))
                                    result.add(res);
                            }
                    }
            }

            int size = 0;
            for (EventPerformerModel prq : eventModel.getPerformers())
                size += prq.getServices().size();

            if (getView().findViewById(R.id.service_count_text_view) != null) {
                ((TextView) getView().findViewById(R.id.service_count_text_view)).setText(String.valueOf(size));
            }

            final ServiceShowAdapter adapter = new ServiceShowAdapter((MainActivity) getActivity(), result);
            mDrawerList.setAdapter(adapter);
        } else {
//            mDrawerList.setAdapter(null);
        }
        mDrawerLayout.openDrawer(Gravity.RIGHT);


        return result;
    }


    private ArrayList<Service> getStringFromServiceListLE() {
        ArrayList<EventPerformerModel> res = new ArrayList<>();
        for (EventPerformerModel pr : eventModel.getPerformers())
            res.add(pr);


        final LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.performers_tab_bar);

        linearLayout.removeAllViews();

        linearLayout.setWeightSum(res.size());

        for (final EventPerformerModel p : res) {
            final TextView valueTV = new TextView(getActivity());
            valueTV.setText(p.getPerformer().getName());
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


                if (eventModel != null) {
                    for (EventPerformerModel prq : eventModel.getPerformers())
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

                    if (eventModel != null) {
                        for (EventPerformerModel prq : eventModel.getPerformers()) {
                            if (prq.getServices().contains(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()))) {
                                prq.getServices().remove(new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid()));
                            }
                            ServiceRespondModel added = new ServiceRespondModel(service.getService(), service.getDuration(), service.getUid());
                            added.setPrice(service.getPrice());
                            prq.getServices().add(added);
                        }


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

    public void setUpSum() {
        if (getView().findViewById(R.id.duration_summ_text_view) != null)
            ((TextView) getView().findViewById(R.id.duration_summ_text_view)).setText(getDurationHours());

    }


    @NonNull
    private ServiceAdapter getServiceAdapter(ArrayList<Service> result, EventPerformerModel performer) {
        for (Service serv : performer.getPerformer().getServices()) {
            if (serv.getService().getServiceType() != null)
                if (!isInTheTypeList(serv.getService().getServiceType().getId(), result)) {

                    boolean in = false;
                    for (Service s : result)
                        if (s.getService().getName().equalsIgnoreCase(serv.getService().getServiceType().getName()))
                            in = true;
                    int rr = 0;
                    for (Service s : performer.getPerformer().getServices()) {
                        if (s.getService().getServiceType().getId().equalsIgnoreCase(serv.getService().getServiceType().getId())) {
                            if (!isInTheList(s.getService().getId(), result)) {
                                if (s.getAvailability() == 2)
                                    rr++;
                            }
                        }
                    }

                    if (!in && rr > 0) {
                        Service f = new Service(2, new ServiceModel(serv.getService().getServiceType().getName(), "", ""));
                        for (PriceModel priceModel : performer.getPerformer().getPrices())
                            if (priceModel.getServiceTypeId().equals(serv.getService().getServiceType().getId()))
                                f.setPrice(priceModel.getAmount());
                        f.setUid(performer.getPerformer().getId());
                        result.add(f);
                    }
                }
            for (Service s : performer.getPerformer().getServices()) {
                if (s.getService().getServiceType() != null)
                    if (s.getService().getServiceType().getId().equalsIgnoreCase(serv.getService().getServiceType().getId())) {
                        if (!isInTheList(s.getService().getId(), result)) {
                            if (s.getAvailability() == 2) {
                                if (eventModel != null) {
                                    for (ServiceRespondModel srm : eventModel.getServices()) {
                                        if (srm.getService().getId().equalsIgnoreCase(s.getService().getId())) {
                                            if (srm.getPerformerId().equalsIgnoreCase(performer.getId())) {
                                                s.setSelected(true);
                                                s.setDuration(srm.getDuration());
                                            }
                                        }
                                    }
                                    for (ServiceRespondModel srm : eventModel.getOffline_services()) {
                                        if (srm.getService().getId().equalsIgnoreCase(s.getService().getId())) {
                                            if (srm.getPerformerId().equalsIgnoreCase(performer.getId())) {
                                                s.setSelected(true);
                                                s.setDuration(srm.getDuration());
                                            }
                                        }
                                    }
                                }

                                s.setUid(performer.getId());
                                for (PriceModel priceModel : performer.getPerformer().getPrices())
                                    if (priceModel.getServiceTypeId().equals(serv.getService().getServiceType().getId()))
                                        s.setPrice(priceModel.getAmount());
                                result.add(s);
                            }
                        }
                    }
            }
//            }

        }

        final ServiceAdapter adapter = new ServiceAdapter((MainActivity) getActivity(), result);
//        adapter.setFragment(this);
        return adapter;
    }

}
