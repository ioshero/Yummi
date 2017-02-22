package com.almabranding.yummi.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.FullScreenViewActivity;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.PerformersArrayAdapter;
import com.almabranding.yummi.adapter.ServiceAdapter;
import com.almabranding.yummi.adapter.ServiceNMAdapter;
import com.almabranding.yummi.adapter.ServiceShowNMAdapter;
import com.almabranding.yummi.models.CreateEventmodel;
import com.almabranding.yummi.models.EventPerformerModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.PriceModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 20/04/16.
 */
public class PerformerFragment extends Fragment {

    public static PerformerModel performer;
    boolean favourite = false;

    //coralline 56fba7aad524e2933d19b353
    public void setPerformer(PerformerModel performer) {
        this.performer = performer;
        favourite = performer.isMyFavourite();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_performer, container, false);


        return view;
    }

    private int current = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        getView().findViewById(R.id.arrow_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (performer != null) {
                    if (current == 0) {
                        current = performer.getImages().size() - 1;
                    } else
                        current--;
                    if (performer.getImages().size() > current)
                        Picasso.with(getContext()).load(performer.getImages().get(current).getFirstImagePath()).into(((ImageView) getView().findViewById(R.id.performer_iw)));
                }
            }
        });

        getView().findViewById(R.id.arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (performer != null) {
                    if (current == performer.getImages().size() - 1) {
                        current = 0;
                    } else
                        current++;

                    if (performer.getImages().size() > current)
                        Picasso.with(getContext()).load(performer.getImages().get(current).getFirstImagePath()).into(((ImageView) getView().findViewById(R.id.performer_iw)));
                }
            }
        });


        if (YummiUtils.isPreformer(getActivity())) {
            getView().findViewById(R.id.favourite_iw).setVisibility(View.GONE);
        } else
            getView().findViewById(R.id.favourite_iw).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favourite = !favourite;
                    if (favourite) {
                        ((ImageView) getView().findViewById(R.id.favourite_iw)).setImageResource(R.mipmap.tab_bar_favorites_icon_gold);


                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

                        Call<ResponseBody> call = service.putFavourite(MainActivity.userId, performer.getId(), MainActivity.token);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                call.toString();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.toString();
                            }
                        });

                    } else {
                        ((ImageView) getView().findViewById(R.id.favourite_iw)).setImageResource(R.mipmap.tab_bar_favorites_icon_gold_rounded);
                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

                        Call<ResponseBody> call = service.deleteFavourite(MainActivity.userId, performer.getId(), MainActivity.token);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                call.toString();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.toString();
                            }
                        });
                    }
                }
            });


        if (YummiUtils.isPreformer(getActivity())) {
            getView().findViewById(R.id.big_ri_button).setBackgroundResource(R.drawable.light_grey_rounded_corners);
        } else
            getView().findViewById(R.id.big_ri_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!YummiUtils.isPreformer(getActivity()))
                        if (performer.getImagePrice() <= 0) {
                            MainActivity.showAllert("Warning", "This Performer has not activated this feature yet");
                        } else if (YummiUtils.client != null) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (YummiUtils.client.getCards().size() > 0) {
                                        reqImage(null);
                                    } else {
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.addToBackStack(null);
                                        // Create and show the dialog.
                                        CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
                                        newFragment.setEventModel(null);
                                        newFragment.setFrag(PerformerFragment.this, true);
                                        newFragment.show(ft, "dialog");
                                    }
                                }
                            });
                            alertDialogBuilder.setNegativeButton("Cancel", null);

                            alertDialogBuilder.setTitle("Payment");
                            alertDialogBuilder.setMessage("your account will be charged $"+String.valueOf(performer.getImagePrice())+" for this transaction, tap confirm to proceed.");

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }


                }
            });

        if (YummiUtils.isPreformer(getActivity())) {
            getView().findViewById(R.id.big_rchat_button).setBackgroundResource(R.drawable.light_grey_rounded_corners);
        } else
            getView().findViewById(R.id.big_rchat_button).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (!YummiUtils.isPreformer(getActivity()))
                        if (performer.getChatPrice() <= 0) {
                            MainActivity.showAllert("Warning", "This Performer has not activated this feature yet");
                        } else if (YummiUtils.client != null) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (YummiUtils.client.getCards().size() > 0) {
                                        ((MainActivity) getActivity()).newChat(performer.getId());
                                    } else {
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.addToBackStack(null);
                                        // Create and show the dialog.
                                        CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
                                        newFragment.setEventModel(null);
                                        newFragment.setFrag(PerformerFragment.this, false);
                                        newFragment.show(ft, "dialog");
                                    }
                                }
                            });
                            alertDialogBuilder.setNegativeButton("Cancel", null);

                            alertDialogBuilder.setTitle("Payment");
                            alertDialogBuilder.setMessage("your account will be charged $"+String.valueOf(performer.getChatPrice())+" for this transaction, tap confirm to proceed.");

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }

                }
            });


        if (YummiUtils.isPreformer(getActivity())) {
            getView().findViewById(R.id.big_book_button).setBackgroundResource(R.drawable.light_grey_rounded_corners);
        } else
            getView().findViewById(R.id.big_book_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventFragment event = new EventFragment();
                    MainActivity.where_go_back = 3;

                    if (MainActivity.activeModel != null) {
                        if (MainActivity.activeModel.getOffline_performers() == null)
                            MainActivity.activeModel.setOffline_performers(new ArrayList<EventPerformerModel>());
                        MainActivity.activeModel.getOffline_performers().add(new EventPerformerModel(performer.getId(), MainActivity.activeModel.getId(), performer));
                    } else {
                        if (MainActivity.createEventModel != null) {
                            if (MainActivity.createEventModel.getOffline_performers() == null)
                                MainActivity.createEventModel.setOffline_performers(new ArrayList<EventPerformerModel>());
                            MainActivity.createEventModel.getOffline_performers().add(new EventPerformerModel(performer.getId(), "", performer));
                        } else {
                            MainActivity.createEventModel = new CreateEventmodel();
                            MainActivity.createEventModel.setOwnerId(MainActivity.userId);
                            if (MainActivity.createEventModel.getOffline_performers() == null)
                                MainActivity.createEventModel.setOffline_performers(new ArrayList<EventPerformerModel>());
                            MainActivity.createEventModel.getOffline_performers().add(new EventPerformerModel(performer.getId(), "", performer));
                        }
                    }

                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, event)
                            .commit();
                }
            });
    }

    public void reqImage(final String token) {
        ((MainActivity) getActivity()).requestImage(performer.getId(), token);
    }


    public void reqChat(final String token) {
        if (token == null)
            ((MainActivity) getActivity()).newChat(performer.getId());
        else
            ((MainActivity) getActivity()).newChat(performer.getId(), token);
    }


    public void setUpActionBar() {
        if (YummiUtils.isPreformer(getContext()))
            ((MainActivity) getActivity()).setLadies();
        else
            ((MainActivity) getActivity()).setNone();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.INVISIBLE);
        title.setText("Notifications");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.where_go_back = 0;


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final View bu = getView().findViewById(R.id.services_button);
        bu.post(new Runnable() {
            @Override
            public void run() {
                getView().findViewById(R.id.performer_iw1).getLayoutParams().width = bu.getWidth() - 6;//metrics.widthPixels/2 - (metrics.widthPixels/20);
                getView().findViewById(R.id.performer_iw1).requestLayout();
                getView().findViewById(R.id.performer_iw2).getLayoutParams().width = bu.getWidth() - 6;//metrics.widthPixels/2 - (metrics.widthPixels/20);
                getView().findViewById(R.id.performer_iw2).requestLayout();
                getView().findViewById(R.id.performer_iw3).getLayoutParams().width = bu.getWidth() - 6;// metrics.widthPixels/2 - (metrics.widthPixels/20);
                getView().findViewById(R.id.performer_iw3).requestLayout();
                getView().findViewById(R.id.performer_iw4).getLayoutParams().width = bu.getWidth() - 6;// metrics.widthPixels/2 - (metrics.widthPixels/20);
                getView().findViewById(R.id.performer_iw4).requestLayout();
            }
        });

        bu.requestLayout();


        if (performer != null) {
            if (performer.getImages().size() > 0) {
                Picasso.with(getContext()).load(performer.getImages().get(0).getFirstImagePath()).into(((ImageView) getView().findViewById(R.id.performer_iw)));
                current = 0;
            }

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);


            if (performer.getImages().size() > 1) {
                Picasso.with(getContext()).load(performer.getImages().get(1).getFirstImagePath()).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.performer_iw1)));
                getView().findViewById(R.id.performer_iw1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ((MainActivity)getActivity()).fullScreenIW.setVisibility(View.VISIBLE);
//                        fullScreen();
//                        Picasso.with(getContext()).load(performer.getImages().get(1).getFirstImagePath()).resize(size.y+600,size.x+300).rotate(90).centerCrop().into(((MainActivity)getActivity()).fullScreenIW);

                        Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                        intent.putExtra("order", 1);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    }
                });
            }
            if (performer.getImages().size() > 2) {
                Picasso.with(getContext()).load(performer.getImages().get(2).getFirstImagePath()).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.performer_iw2)));
                getView().findViewById(R.id.performer_iw2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).fullScreenIW.setVisibility(View.VISIBLE);
//                        fullScreen();
//                        Picasso.with(getContext()).load(performer.getImages().get(2).getFirstImagePath()).resize(size.y+300,size.x+300).rotate(90).centerCrop().into(((MainActivity)getActivity()).fullScreenIW);
//

                        Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                        intent.putExtra("order", 2);
                        intent.putExtra("type", 0);
                        startActivity(intent);

                    }
                });
            }
            if (performer.getImages().size() > 3) {
                Picasso.with(getContext()).load(performer.getImages().get(3).getFirstImagePath()).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.performer_iw3)));
                getView().findViewById(R.id.performer_iw3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).fullScreenIW.setVisibility(View.VISIBLE);
//                        fullScreen();
//                        Picasso.with(getContext()).load(performer.getImages().get(3).getFirstImagePath()).resize(size.y+300,size.x+300).rotate(90).centerCrop().into(((MainActivity)getActivity()).fullScreenIW);
                        Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                        intent.putExtra("order", 3);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    }
                });
            }
            if (performer.getImages().size() > 0) {
                Picasso.with(getContext()).load(performer.getImages().get(0).getFirstImagePath()).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.performer_iw4)));
                getView().findViewById(R.id.performer_iw4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ((MainActivity)getActivity()).fullScreenIW.setVisibility(View.VISIBLE);
//                        Picasso.with(getContext()).load(performer.getImages().get(0).getFirstImagePath()).resize(size.y+300,size.x+300).rotate(90).centerCrop().into(((MainActivity)getActivity()).fullScreenIW);
                        Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                        intent.putExtra("order", 0);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    }
                });
            }


            ((TextView) getView().findViewById(R.id.textView7)).setText(performer.getName());

            if (favourite)
                ((ImageView) getView().findViewById(R.id.favourite_iw)).setImageResource(R.mipmap.tab_bar_favorites_icon_gold);
            else
                ((ImageView) getView().findViewById(R.id.favourite_iw)).setImageResource(R.mipmap.tab_bar_favorites_icon_gold_rounded);

            if (performer.getHeight() == 0) {
                ((TextView) getView().findViewById(R.id.textView9)).setText("");
            } else if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("Dimensions", false)) {
                ((TextView) getView().findViewById(R.id.textView9)).setText(Html.fromHtml("Height: <b>" + String.valueOf(+(int) (performer.getHeight() * 100)) + " cm</b>"));
            } else
                ((TextView) getView().findViewById(R.id.textView9)).setText(Html.fromHtml("Height: <b>" + String.valueOf(+(int) ((performer.getHeight() / 2.54) * 100)) + " inch</b>"));

            if (performer.getBustSize() != null)
                ((TextView) getView().findViewById(R.id.textView10)).setText(Html.fromHtml("Bust: <b>" + performer.getBustSize().getName() + " Cups</b>"));
            else
                ((TextView) getView().findViewById(R.id.textView10)).setText("");

            if (performer.getbodyType() != null)
                ((TextView) getView().findViewById(R.id.textView11)).setText(Html.fromHtml("Body: <b>" + performer.getbodyType().getName() + "</b>"));
            else
                ((TextView) getView().findViewById(R.id.textView11)).setText("");


            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(performer.getBirthDate());

                    ((TextView) getView().findViewById(R.id.textView8)).setText(Html.fromHtml("Age: <b>" + String.valueOf(getDiffYears(convertedDate, new Date())) + "</b>"));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (NullPointerException n) {

            }
            mDrawerList = (ListView) getActivity().findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            getActivity().findViewById(R.id.event_create).setVisibility(View.GONE);
            getActivity().findViewById(R.id.navList).setVisibility(View.VISIBLE);

            getView().findViewById(R.id.services_button).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ArrayList<Service> res = getStringFromServiceList();

                    res.add(0, new Service(2, new ServiceModel("Chat Request Price", ""), performer.getChatPrice()));
//                    res.add(0, new Service(2, new ServiceModel("Chat Price","bdanledk")));
//
                    res.add(0, new Service(2, new ServiceModel("Image Request Price", ""), performer.getImagePrice()));
//                    res.add(0, new Service(2, new ServiceModel("Image Price","bdanledk")));

                    ServiceAdapter adapter = new ServiceAdapter((MainActivity) getActivity(), res);

                    mDrawerList.setAdapter(adapter);

                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                }
            });


            getView().findViewById(R.id.bio_button).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    ServiceNMAdapter adapter = new ServiceNMAdapter((MainActivity) getActivity(), getStringFromBio());
                    mDrawerList.setAdapter(adapter);

                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                }
            });

        }
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
//        if (isImmersiveModeEnabled) {
//            Log.i(TAG, "Turning immersive mode mode off. ");
//        } else {
//            Log.i(TAG, "Turning immersive mode mode on.");
//        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }


    private ArrayList<Service> getStringFromBio() {

        ArrayList<Service> result = new ArrayList<Service>();


        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date convertedDate = new Date();
            try {
                convertedDate = format.parse(performer.getBirthDate());

                result.add(new Service(2, new ServiceModel("Age", "", "")));
                result.add(new Service(2, new ServiceModel(String.valueOf(getDiffYears(convertedDate, new Date())), "wefs", "")));

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException n) {

        }


        result.add(new Service(2, new ServiceModel("Height", "", "")));
        result.add(new Service(2, new ServiceModel(String.valueOf(+(int) (performer.getHeight() * 100)) + " cm", "wefs", "")));

        result.add(new Service(2, new ServiceModel("Bust", "", "")));
        if (performer.getBustSize() != null)
            result.add(new Service(2, new ServiceModel(performer.getBustSize().getName(), "wefs", "")));


        result.add(new Service(2, new ServiceModel("Body", "", "")));
        if (performer.getbodyType() != null)
            result.add(new Service(2, new ServiceModel(performer.getbodyType().getName(), "wefs", "")));


        return result;
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

    private ArrayList<Service> getStringFromServiceList() {

        ArrayList<Service> result = new ArrayList<Service>();

        for (Service serv : performer.getServices()) {
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
                    Service s = new Service(2, new ServiceModel(serv.getService().getServiceType().getName(), "", ""), 0);

                    for (PriceModel priceModel : performer.getPrices())
                        if (priceModel.getServiceTypeId().equals(serv.getService().getServiceType().getId()))
                            s.setPrice(priceModel.getAmount());
                    result.add(s);
                }


            }
            for (Service s : performer.getServices()) {
                if (s.getService().getServiceType().getId().equalsIgnoreCase(serv.getService().getServiceType().getId())) {
                    if (!isInTheList(s.getService().getId(), result)) {
                        if (s.getAvailability() == 2)
                            result.add(s);
                    }
                }
            }


        }
        return result;
    }


    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        a.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar b = getCalendar(last);
        b.setTimeZone(TimeZone.getTimeZone("UTC"));
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return cal;
    }


    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar today = Calendar.getInstance();
        today.setTimeZone(TimeZone.getTimeZone("UTC"));

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
