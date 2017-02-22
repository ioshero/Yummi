package com.almabranding.yummi;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.adapter.LeftSideArrayAdapter;
import com.almabranding.yummi.fragments.ChatFragment;
import com.almabranding.yummi.fragments.ChatListFragment;
import com.almabranding.yummi.fragments.CreditCardDialogFragment;
import com.almabranding.yummi.fragments.EventFragment;
import com.almabranding.yummi.fragments.EventStaticFragment;
import com.almabranding.yummi.fragments.EventsListFragment;
import com.almabranding.yummi.fragments.FavouritesListFragment;
import com.almabranding.yummi.fragments.GalleryFragment;
import com.almabranding.yummi.fragments.NearbyFragment;
import com.almabranding.yummi.fragments.NotificationsFragment;
import com.almabranding.yummi.fragments.PaymentsListFragment;
import com.almabranding.yummi.fragments.PerformerFilterFragment;
import com.almabranding.yummi.fragments.PerformerFragment;
import com.almabranding.yummi.fragments.PerformerListFragment;
import com.almabranding.yummi.fragments.ProfileFragment;
import com.almabranding.yummi.fragments.SettingsFragment;
import com.almabranding.yummi.models.BankModel;
import com.almabranding.yummi.models.CardModel;
import com.almabranding.yummi.models.CreateEventmodel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.Logout_model;
import com.almabranding.yummi.models.PaymentTokenModel;
import com.almabranding.yummi.models.PerformerListModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.RegistrationEightResponseModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;
import com.almabranding.yummi.services.LocationUpdateService;
import com.almabranding.yummi.utils.MapDrawer;
import com.almabranding.yummi.utils.YummiUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ioshero on 18/04/16.
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;

    public GoogleMap map;

    private SupportMapFragment mf;

    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    public static final String BASE_URL = WelcomeActivity.BASE_STATIC_URL + "/api/";

    public static Retrofit retrofit;

    public static CreateEventmodel createEventModel = null;

    public static EventModel activeModel = null;

    public static String token = "";
    public static String userId = "";

    public static int where_go_back = -1;


    @Override
    public void onBackPressed() {

        try {
            removeView();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }

        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }

        if (fullScreenIW != null)
            if (fullScreenIW.getVisibility() == View.VISIBLE) {
                fullScreenIW.setVisibility(View.GONE);
                return;
            }

        if (where_go_back == 66) {
            setUpFirstFragment();
            where_go_back = -1;
        }

        if (where_go_back == 4) {

            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
            Call<EventModel> call = service.getEvent(YummiUtils.currentlyOpenedEvent, MainActivity.token);

            call.enqueue(new Callback<EventModel>() {
                @Override
                public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                    if (response.body() != null) {

                        EventStaticFragment event = new EventStaticFragment();
                        event.setEventModel(response.body());
                        MainActivity.where_go_back = 3;
                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, event)
                                .commit();

                    }
                }

                @Override
                public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());

                }
            });
            return;

        }

        if (where_go_back == 3) {
            MainActivity.createEventModel = null;
            MainActivity.activeModel = null;
            setUpEventsListFragment();
            where_go_back = -1;
            return;
        }


//
//        if (where_go_back == -1)
        super.onBackPressed();

//        if (where_go_back == 0) {
//            setUpFirstFragment();
//            where_go_back = -1;
//        }
//
//        if (where_go_back == 2) {
//            setUpFavourites();
//            where_go_back = -1;
//        }
//


    }


    public com.github.nkzawa.socketio.client.Socket mSocket;

    public Socket getmSocket() {
        return mSocket;
    }

    public void connectLogin() {
        if (mSocket != null) {
            if (!mSocket.connected())
                mSocket.connect();


            mSocket.off();


            mSocket.off("youHaveToLogIn");

            mSocket.on("youHaveToLogIn", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("CONNECTIONS", "youHaveToLogIn");
                    mSocket.emit("logIn", MainActivity.userId, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.e("CONNECTIONS", "done");
                            unreadedNotif();
                            unreadedMessage();

                        }
                    });
                }
            });

            mSocket.on("eventStatus", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        String updatedEventId = (String) args[0];
                        int statusValue = (int) args[1];
                        if (YummiUtils.currentlyOpenedEvent.equalsIgnoreCase(updatedEventId)) {
                            startStaticEvent(updatedEventId);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });




/*
socket.on("chatStatus") {data, ack in
            if let updatedChatId = data.first as? String,
                let statusValue = data.last as? Int,
                let chatDelegate = self.chatUpdateDelegate {
                if let status = ChatStatus(rawValue: statusValue) {
                    dispatch_async(dispatch_get_main_queue()) {
                        chatDelegate.chatStatusWasChanged(updatedChatId, chatStatus: status)
                    }
                }
            }
        }
 */


            mSocket.on("eventLive", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        String eventId = (String) args[0];
                        String eventEndDate = (String) args[1];

                        if (!YummiUtils.currentlyOpenedEvent.equalsIgnoreCase(eventId)) {
//                        startStaticEvent(updatedEventId);
                            showLiveEvent(eventId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            mSocket.on("newChatMessage", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
//                        String updatedEventId = (String) args[0];

                        unreadedMessage();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            mSocket.on("notification", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        //{"categoryType":"imageRequest","type":"newImageRequest","read":false,"createdAt":"2016-06-02T08:16:59.543Z","createdAtTime":1464855419544,"id":"574feb7b658e410838351ca4","ownerType":"performer","ownerId":"57447746759e2d2e3ee33f3a","clientName":"Test User Stage Name","requestId":"574feb7b658e410838351ca2"}
//{"categoryType":"imageRequest","type":"imageRequestReplayed","read":false,"createdAt":"2016-06-02T13:36:33.794Z","createdAtTime":1464874593796,"id":"57503661658e410838351d2a","performerId":"57447746759e2d2e3ee33f3a","ownerType":"client","ownerId":"574c027bf7b28bc50bdffb62","performerName":"Natalie","requestId":"57501e68658e410838351d04","imageId":"57501e68658e410838351d04","imageUrl":"https:\/\/s3-us-west-2.amazonaws.com\/yummi-app\/clients\/574c027bf7b28bc50bdffb62\/images\/testImage57501e68658e410838351d04.jpeg"}
                        final JSONObject notification = (JSONObject) args[0];

                        unreadedNotif();
                        unreadedMessage();

                        if (notification != null) {

                            if (notification.getString("type").toLowerCase().contains("chat")) {

                                NotificationCompat.Builder b = new NotificationCompat.Builder(getBaseContext());
                                b.setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.mipmap.push2ico)
                                        .setTicker(notification.getString("clientName"))
                                        .setContentTitle("New chat request from %@".replace("%@", notification.getString("clientName")))
                                        .setContentText(notification.getString("type"))
                                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                        .setContentInfo("Info");
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                                notificationManager.notify(1, b.build());

                            }


                            if (YummiUtils.isPreformer(MainActivity.this)) {


                                NotificationCompat.Builder b = new NotificationCompat.Builder(getBaseContext());
                                if (notification.getString("categoryType").equalsIgnoreCase("imageRequest")) {
                                    b.setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.push2ico)
                                            .setTicker(notification.getString("clientName"))
//                                            .setContentTitle(YummiUtils.getNotification(notification.getString("type")).replace("%@", notification.getString("clientName")))
                                            .setContentText(notification.getString("type"))
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                            .setContentInfo("Info");

                                }


                                if (notification.getString("categoryType").equalsIgnoreCase("tip")) {
                                    b.setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.push2ico)
                                            .setContentTitle(YummiUtils.getNotification(notification.getString("type")).replace("%@", notification.getString("clientName")))
                                            .setContentText(notification.getString("type"))
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                            .setContentInfo("Info");
                                }


                                if (notification.getString("categoryType").equalsIgnoreCase("event")) {
                                    b.setAutoCancel(true)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.push2ico)
                                            .setContentTitle(YummiUtils.getNotification(notification.getString("type")).replace("%@", notification.getString("eventName")))
                                            .setContentText(notification.getString("type"))
                                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                            .setContentInfo("Info");
                                }

                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                                notificationManager.notify(1, b.build());

                            } else {

                                NotificationCompat.Builder b = new NotificationCompat.Builder(getBaseContext());

                                b.setAutoCancel(true)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setWhen(System.currentTimeMillis())
                                        .setSmallIcon(R.mipmap.push2ico)
                                        .setContentTitle(YummiUtils.getNotification(notification.getString("type")))
                                        .setContentText(notification.getString("type"))
                                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                                        .setContentInfo("Info");


                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                                notificationManager.notify(1, b.build());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (notification.getString("categoryType").equalsIgnoreCase("imageRequest")) {
                                            if (YummiUtils.isPreformer(MainActivity.this)) {
                                                showPerformerImageRequestNotif(notification.getString("clientName"), notification.getString("requestId"));
                                            }
                                        }

                                        if (notification.getString("type").equalsIgnoreCase("imageRequestReplayed")) {
                                            if (!YummiUtils.isPreformer(MainActivity.this)) {
                                                showClientImageRequestNotif();
                                            }
                                        }

//{"categoryType":"chat","type":"newChat","read":false,"createdAt":"2016-08-09T09:20:46.888Z","createdAtTime":1470734446889,"id":"57a9a06e8c3ca65141e3ac52","ownerType":"performer","ownerId":"572dd4f4fcd7fcd338aa94d7","clientId":"578cd45abdc225b97f8353cc","clientName":"Adamecsek","chatId":"57a9a06e8c3ca65141e3ac50"}
                                        if (notification.getString("type").equalsIgnoreCase("newChat")) {
                                            if (YummiUtils.isPreformer(MainActivity.this)) {
                                                showChat(notification.getString("clientName"), notification.getString("chatId"));
                                            }
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    private void showPerformerImageRequestNotif(final String name, final String requestId) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setPositiveButton("Take a shot", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            NotificationsFragment fragment = new NotificationsFragment();
                            // Insert the fragment by replacing any existing fragment
                            fragment.setRequestId(requestId);
                            fragment.setClientName(name);

                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.content_frame, fragment)
                                    .commit();


                            setNone();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("OK", null);
                    alertDialogBuilder.setTitle("You recived image request from " + name);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception e) {
                    //exception if it wants to show an message when the screen is dissmissed
                    e.printStackTrace();
                }
            }
        });
    }


    private void showLiveEvent(final String eventID) {


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setPositiveButton("Show", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                            Call<EventModel> call = service.getEvent(eventID, MainActivity.token);

                            call.enqueue(new Callback<EventModel>() {
                                @Override
                                public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                    if (response.body() != null) {

                                        EventStaticFragment event = new EventStaticFragment();
                                        event.setEventModel(response.body());
                                        MainActivity.where_go_back = 3;
                                        // Insert the fragment by replacing any existing fragment
                                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .addToBackStack(null)
                                                .replace(R.id.content_frame, event)
                                                .commit();

                                    }
                                }

                                @Override
                                public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());

                                }
                            });

                        }
                    });
                    alertDialogBuilder.setNegativeButton("OK", null);

                    alertDialogBuilder.setTitle("Active event");
                    alertDialogBuilder.setMessage("You have an active event");


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception e) {
                    //exception if it wants to show an message when the screen is dissmissed
                    e.printStackTrace();
                }
            }
        });
    }

    private void showClientImageRequestNotif() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setPositiveButton("Show", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    GalleryFragment fragment = new GalleryFragment();
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    setNone();
                }
            });
            alertDialogBuilder.setNegativeButton("OK", null);
            alertDialogBuilder.setTitle("You recived new image");

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            //exception if it wants to show an message when the screen is dissmissed
            e.printStackTrace();
        }
    }


    private void showChat(final String name, final String ChatId) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setPositiveButton("Start chat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setUpChatFragment(ChatId);
                }
            });
            alertDialogBuilder.setNegativeButton("OK", null);
            alertDialogBuilder.setTitle("Chat Request");
            alertDialogBuilder.setMessage("You received chat request from " + name);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            //exception if it wants to show an message when the screen is dissmissed
            e.printStackTrace();
        }
    }

    //[{"performerId":"57447746759e2d2e3ee33f3a","services":[{"serviceId":"56d71d434cfa5f4e31a5c4e7","duration":1},{"serviceId":"56d71cbf4cfa5f4e31a5c4e0","duration":0.5},{"serviceId":"56d71b984cfa5f4e31a5c4d5","duration":0.5}]},{"performerId":"5755382992c438747d370dc0","services":[{"serviceId":"56d71d434cfa5f4e31a5c4e7","duration":1},{"serviceId":"56d71cbf4cfa5f4e31a5c4e0","duration":0.5},{"serviceId":"56d71b984cfa5f4e31a5c4d5","duration":0.5}]}]
    //575e833af09a969b6cdb6d39
    public void addEventPerformersWithServices(final String eventId, final JSONArray performersWithServices, final EventFragment efrg) {
        if (mSocket != null)
            mSocket.emit("addEventPerformersWithServices", eventId, performersWithServices, new Ack() {
                @Override
                public void call(Object... args) {
                    boolean result = (boolean) args[0];

                    if (!result) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                showAllert("Error", "There was a problem, please try again later");
                            }
                        });

                        return;
                    } else {
                        efrg.reload(eventId);
                    }


                }
            });
    }

    public void addEventPerformersWithServices(final String eventId, final JSONArray performersWithServices) {
        if (mSocket != null)
            mSocket.emit("addEventPerformersWithServices", eventId, performersWithServices, new Ack() {
                @Override
                public void call(Object... args) {
                    boolean result = (boolean) args[0];

                    if (!result) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                showAllert("Error", "There was a problem, please try again later");
                            }
                        });

                        return;
                    }


                }
            });
    }

    public void addPerformerLink(final String perforId, final String eventId) {

        if (mSocket != null)
            mSocket.emit("addEventPerformer", eventId, perforId, new Ack() {
                @Override
                public void call(Object... args) {
                    boolean result = (boolean) args[0];

                }
            });


    }

    public void addGuest(final String stageName, final String eventId) {
        if (mSocket != null)
            if (stageName != null && eventId != null) {
                mSocket.emit("addEventGuest", eventId, stageName, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];

                    }
                });
            }

    }

    public void giveTip(final String eventId, final String performerId, final int amount, final String token) {
        if (mSocket != null)
            if (token == null) {
                mSocket.emit("giveATip", eventId, performerId, amount, "", new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with adding a tip", "There was a problem, please try again later");
                                }
                            });

                            return;
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Success", "Thanks for Tip");
                                }
                            });

                            return;
                        }


                    }
                });
            } else {
                mSocket.emit("giveATip", eventId, performerId, amount, token, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with adding a tip", "There was a problem, please try again later");
                                }
                            });

                            return;
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Success", "Thanks for Tip");
                                }
                            });

                            return;
                        }

                    }
                });
            }

    }


    public void newChat(final String performerId) {
        if (mSocket != null)
            if (performerId != null) {
                mSocket.emit("newChat", performerId, "", new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with Chat", "There was a problem, please try again later");
                                }
                            });

                            return;
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Success", "Your request has been sent successfully");
                                }
                            });


                        }

                    }
                });
            }

    }

    public void newChat(final String performerId, final String token) {
        if (mSocket != null)
            if (performerId != null) {
                mSocket.emit("newChat", performerId, token, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });

                            return;
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Success", "");
                                }
                            });


                        }

                    }
                });
            }

    }


    public void newChatMessage(final ChatFragment frg, final String chatId, final String message) {
        if (mSocket != null)
            if (chatId != null && message != null) {
                mSocket.emit("newChatMessage", chatId, message, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {

//                            callback(success: false, errorMessage: data[1] as? String)


                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showResponseError((String) args[1]);
                                }
                            });

                            return;
                        } else {
                            frg.assToChat(message);
                        }

                    }
                });
            }

    }

    public void readChat(final String chatId) {
        if (mSocket != null)
            if (mSocket.connected())
                if (chatId != null) {
                    mSocket.emit("readChat", chatId, new Ack() {
                        @Override
                        public void call(Object... args) {


                        }
                    });
                    unreadedMessage();
                }

    }

    public void notificationRead(final String notificationId) {
        if (mSocket != null)
            if (notificationId != null) {
                mSocket.emit("notificationRead", notificationId, new Ack() {
                    @Override
                    public void call(Object... args) {


                    }
                });
                unreadedNotif();
            }

    }

    public void chatList(final ChatListFragment chFr) {
        if (mSocket != null)
            mSocket.emit("chatList", new Ack() {
                @Override
                public void call(Object... args) {
                    chFr.updateResult((JSONArray) args[0]);


                }
            });
    }


    public void unreadedNotif() {
        if (mSocket != null)
            mSocket.emit("unreadNotificationsCount", new Ack() {
                @Override
                public void call(Object... args) {
                    try {
                        YummiUtils.unreaded_notif_count = (int) args[0];
                        reValidate();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }

                }
            });
    }

    public void unreadedMessage() {
        if (mSocket != null)
            mSocket.emit("unreadChatMessagesCount", new Ack() {
                @Override
                public void call(Object... args) {
                    try {
                        YummiUtils.unreaded_message_count = (int) args[0];
                        reValidate();
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            });
    }


    private void reValidate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (YummiUtils.unreaded_notif_count > 0) {
                    if (findViewById(R.id.notif_count) != null) {
                        findViewById(R.id.notif_count).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.notif_count)).setText(String.valueOf(YummiUtils.unreaded_notif_count));
                    }
                } else {
                    if (findViewById(R.id.notif_count) != null) {
                        findViewById(R.id.notif_count).setVisibility(View.GONE);
                    }
                }

                if (YummiUtils.unreaded_message_count > 0) {
                    if (findViewById(R.id.message_count) != null) {
                        findViewById(R.id.message_count).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.message_count)).setText(String.valueOf(YummiUtils.unreaded_message_count));
                    }
                } else {
                    if (findViewById(R.id.message_count) != null) {
                        findViewById(R.id.message_count).setVisibility(View.GONE);
                    }
                }
            }
        });


    }


    public void startChat(final String chatId) {
        if (mSocket != null)
            if (chatId != null) {
                mSocket.emit("startChat", chatId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });

                            return;
                        }

                    }
                });
            }

    }

    public void addEventService(final String servicId, final String performerId, final String eventId, double duration) {
        if (mSocket != null)
            if (servicId != null && eventId != null && performerId != null) {
                mSocket.emit("addEventPerformerService", eventId, performerId, servicId, duration, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem with adding service, please try again later");
                                }
                            });
                            return;
                        }

                    }
                });
            }

    }

    public void checkInEvent(final String eventId, final EventStaticFragment frg) {
        if (mSocket != null)
            if (eventId != null) {
                mSocket.emit("checkInEvent", eventId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });
                            return;
                        }
                        if (frg != null)
                            startStaticEvent(eventId);
                    }
                });
            }
    }

    public void payEvent(final String eventId, final String token, final int ammount, final Fragment frg) {
        if (mSocket != null)
            if (eventId != null) {
                if (token == null)
                    mSocket.emit("payEvent", eventId, false, ammount, new Ack() {
                        @Override
                        public void call(Object... args) {
                            boolean result = (boolean) args[0];
                            if (!result) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Error", "There was a problem, please try again later");
                                    }
                                });
                                return;
                            }

                            if (frg != null)
                                startStaticEvent(eventId);
                        }
                    });
                else
                    mSocket.emit("payEvent", eventId, token, ammount, new Ack() {
                        @Override
                        public void call(Object... args) {
                            boolean result = (boolean) args[0];
                            if (!result) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Error", "There was a problem, please try again later");
                                    }
                                });
                                return;
                            }

                            if (frg != null)
                                startStaticEvent(eventId);
                        }
                    });
            }
    }


    public void addCardToken(final String tok, final String eventId, final int ammount, final CreditCardDialogFragment dialog, final PerformerFragment frg) {//, String digit, int month, int year

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<CardModel> call = service.postCards(MainActivity.userId, MainActivity.token, new PaymentTokenModel(tok));

        call.enqueue(new Callback<CardModel>() {
            @Override
            public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                if (response.body() != null) {
                    YummiUtils.client.getCards().add(response.body());

                    if (eventId != null) {
                        payEvent(eventId, null, ammount, dialog);
                    }

                    if (frg != null) {
                        if (ammount == 0)
                            frg.reqImage(null);
                        if (ammount == -1)
                            frg.reqChat(null);
                    }

                } else {
                    if (eventId != null) {
                        payEvent(eventId, tok, ammount, dialog);
                    }

                    if (frg != null)
                        frg.reqImage(tok);


                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAllert("Error", "There was an error with adding the card");
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<CardModel> call, Throwable t) {
                showAllert("Error", "Network failure. Please try again!");
            }
        });


    }

    public void startEvent(final String eventId, final EventStaticFragment frg) {
        if (mSocket != null)
            if (eventId != null) {
                mSocket.emit("startEvent", eventId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });
                            return;
                        }
                        if (frg != null) {
                            startStaticEvent(eventId);
                        }
                    }
                });
            }
    }

    private void startStaticEvent(String eventId) {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<EventModel> call = service.getEvent(eventId, MainActivity.token);

        call.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if (response.body() != null) {
                    try {

                        EventStaticFragment event = new EventStaticFragment();
                        event.setEventModel(response.body());
                        MainActivity.where_go_back = 3;
                        // Insert the fragment by replacing any existing fragment
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, event)
                                .commitAllowingStateLoss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
            }
        });
    }

    public void deleteEventService(final String serviceID, final String eventId, final String performerId) {
        if (mSocket != null)
            if (serviceID != null && eventId != null && performerId != null) {
                mSocket.emit("deleteEventPerformerService", eventId, performerId, serviceID, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];

                    }
                });
            }

    }

    public void inviteGuest(final String mail, final String eventId) {
        if (mSocket != null)
            if (mail != null && eventId != null) {
                mSocket.emit("inviteEventGuest", eventId, mail, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];

                    }
                });
            }

    }


    public void cancelEvent(final String eventId) {
        if (mSocket != null)
            if (eventId != null) {
                mSocket.emit("cancelEvent", eventId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with Event cancel", "Please try again later");
                                }
                            });

                            return;
                        } else {
                            startStaticEvent(eventId);
                        }
                    }
                });
            }

    }

    public void requestImage(final String performerId, final String token) {
        if (mSocket != null)
            if (performerId != null) {
                if (token == null)
                    mSocket.emit("imageRequest", performerId, false, new Ack() {
                        @Override
                        public void call(Object... args) {
                            boolean result = (boolean) args[0];
                            if (!result) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Error with Image Request", "There was a problem, please try again later");
                                    }
                                });

                                return;
                            } else {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Success", "Your request hass been sent succesfully");
                                    }
                                });


                            }

                        }
                    });
                else
                    mSocket.emit("imageRequest", performerId, token, new Ack() {
                        @Override
                        public void call(Object... args) {
                            boolean result = (boolean) args[0];
                            if (!result) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Error with Image Request", "There was a problem, please try again later");
                                    }
                                });

                                return;
                            } else {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        showAllert("Success", "Your request hass been sent succesfully");
                                    }
                                });


                            }

                        }
                    });
            }

    }


    public void deleteCard(final String cardId) {
        if (mSocket != null)
            if (cardId != null) {
                mSocket.emit("deleteCard", cardId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with Card deletion", "There was a problem, please try again later");
                                }
                            });

                            return;
                        }

                    }
                });
            }

    }

    public void defaultCard(final String cardId) {
        if (mSocket != null)
            if (cardId != null) {
                mSocket.emit("setDefaultCard", cardId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error with Card setting", "There was a problem, please try again later");
                                }
                            });
                            return;
                        }

                    }
                });
            }

    }


    public void acceptEvent(final String eventId, final EventStaticFragment frg) {
        if (mSocket != null)
            if (eventId != null) {
                mSocket.emit("acceptEvent", eventId, new Ack() {//MainActivity.userId,
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });
                            return;
                        }
                        if (frg != null)
                            startStaticEvent(eventId);

                    }
                });
            }

    }

    public void rejectEvent(final String eventId, final EventStaticFragment frg) {
        if (mSocket != null)
            if (eventId != null) {
                mSocket.emit("rejectEvent", eventId, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean result = (boolean) args[0];
                        if (!result) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    showAllert("Error", "There was a problem, please try again later");
                                }
                            });
                            return;
                        }
                        if (frg != null)
                            startStaticEvent(eventId);
                    }
                });
            }

    }

    private void logOut() {

        MainActivity.activeModel = null;
        MainActivity.createEventModel = null;

        YummiUtils.client = null;

        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

        Call<ResponseBody> call = service.logout(MainActivity.token, new Logout_model(YummiUtils.deviceToken));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("token_acces", "");
                editor.apply();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private void logOutPer() {

        MainActivity.activeModel = null;
        MainActivity.createEventModel = null;

        YummiUtils.performer = null;

        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

        Call<ResponseBody> call = service.perLogout(MainActivity.token, new Logout_model(YummiUtils.deviceToken));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("token_acces", "");
                editor.apply();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private void rlogOut() {
        logOut();
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("token_acces", "");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void rlogOutPer() {
        logOutPer();
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("token_acces", "");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
//        if (YummiUtils.isPreformer(this))
//            logOutPer();
//        else
//            logOut();

        mSocket.off();
        mSocket.disconnect();
        mSocket.close();
        mSocket = null;

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectLogin();
        YummiUtils.isInForeground = true;

        setUpMap();
        if (YummiUtils.push_action > -1) {

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);

            switch (YummiUtils.push_action) {
                case 1: {
                    if (YummiUtils.isPreformer(MainActivity.this)) {
                        NotificationsFragment fragment = new NotificationsFragment();
                        // Insert the fragment by replacing any existing fragment
                        fragment.setRequestId(YummiUtils.push_action_id);
                        fragment.setClientName(YummiUtils.push_action_param_1);

                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, fragment)
                                .commit();


                        setNone();
                    }

                    break;
                }
                case 2: {
                    setUpChatFragment();
                    setChats();

                    break;
                }

                case 3: {
                    setUpChatFragment(YummiUtils.push_action_id);
                    setChats();

                    break;
                }

                case 4: {
                    startStaticEvent(YummiUtils.push_action_id);
                    setEvents();

                    break;
                }


            }


//            try {
//                if (mSocket != null) {
//                    mSocket.off();
//                    mSocket.disconnect();
//                    mSocket.close();
//
//                    mSocket.open();
//                }else {
//
//                    if (YummiUtils.isPreformer(getApplicationContext())) {
//                        mSocket = IO.socket(WelcomeActivity.BASE_STATIC_URL + "/performers");
//                    } else
//                        mSocket = IO.socket(WelcomeActivity.BASE_STATIC_URL + "/clients");//
//
//                    mSocket.open();
//                }
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//                Log.e("SOCKET!!!!!", e.toString());
//            }

            YummiUtils.push_action = -1;

        }


    }


    public void setUpMap(final SupportMapFragment smf) {
        mf = smf;
        if (mf != null)
            mf.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    map.getUiSettings().setScrollGesturesEnabled(false);
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);

                    if (MainActivity.activeModel != null) {
                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(new LatLng(MainActivity.activeModel.getLocation().getLat(),
                                        MainActivity.activeModel.getLocation().getLng()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                        map.moveCamera(center);
                        map.animateCamera(zoom);
                    } else if (MainActivity.createEventModel != null) {
                        if (MainActivity.createEventModel.getLocation() != null) {
                            CameraUpdate center =
                                    CameraUpdateFactory.newLatLng(new LatLng(MainActivity.createEventModel.getLocation().getLat(),
                                            MainActivity.createEventModel.getLocation().getLng()));
                            CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                            map.moveCamera(center);
                            map.animateCamera(zoom);
                        }
                    } else {
                        setMapEventToNearby();
                    }

                }
            });
    }


    public void setUpMap() {
        mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mf != null)
            mf.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        YummiUtils.isInForeground = false;
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        if (mSocket != null)
            if (mSocket.connected()) {
                mSocket.off();
                mSocket.disconnect();
            }
        super.onPause();

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        ((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.navList_ce));
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        if (mSocket != null)
            if (mSocket.connected()) {
                mSocket.off();
                mSocket.disconnect();
            }
        super.onStop();
    }

    public static GoogleApiClient mGoogleApiClient;


    public ImageView fullScreenIW;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case YummiUtils.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    showAllert("", "permission granted");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    showAllert("", "permission denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (YummiUtils.isPreformer(this)) {
            setContentView(R.layout.activity_main_performer);
        } else
            setContentView(R.layout.activity_main);

        try {
            if (YummiUtils.isPreformer(getApplicationContext())) {
                mSocket = IO.socket(WelcomeActivity.BASE_STATIC_URL + "/performers");
            } else
                mSocket = IO.socket(WelcomeActivity.BASE_STATIC_URL + "/clients");//
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("SOCKET!!!!!", e.toString());
        }

        if (YummiUtils.isPreformer(this))
            YummiUtils.verifyStoragePermissions(this);
        alertDialogBuilder = new AlertDialog.Builder(this);


        if (findViewById(R.id.ful_screen_iw) != null) {
            fullScreenIW = (ImageView) findViewById(R.id.ful_screen_iw);
            findViewById(R.id.ful_screen_iw).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.ful_screen_iw).setVisibility(View.GONE);
                }
            });
        }


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            token = bundle.getString("token");
            userId = bundle.getString("userId");
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("aaa_token", token).commit();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("aaa_userId", userId).commit();


        } else {
            token = PreferenceManager.getDefaultSharedPreferences(this).getString("aaa_token", "");
            userId = PreferenceManager.getDefaultSharedPreferences(this).getString("aaa_userId", "");
        }

        if (YummiUtils.isPreformer(this)) {
            Intent mServiceIntent = new Intent(this, LocationUpdateService.class);
            mServiceIntent.putExtra("id", userId);
            startService(mServiceIntent);
        }

        Log.e("token + userid", token + " " + userId);


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        if (YummiUtils.isPreformer(this)) {
            mPlanetTitles = getResources().getStringArray(R.array.menu_array_performer);
            if (YummiUtils.performer == null) {
                getActualPerformer();
            } else {
                getServiceList();
            }
            YummiUtils.bankModel = null;
            getActualBankAccount();

        } else
            mPlanetTitles = getResources().getStringArray(R.array.menu_array);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setUpFirstFragmentWithoutBackstack();

        if (!YummiUtils.isPreformer(this))
            findViewById(R.id.ladies).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icon);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("black"));
                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
                    ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
                    ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
                    PerformerListFragment performerListFragment = new PerformerListFragment();
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, performerListFragment)
                            .commit();
                }
            });

        if (!YummiUtils.isPreformer(this))
            findViewById(R.id.nearby).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
                    ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icon);
                    ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("black"));


                    NearbyFragment performerListFragment = new NearbyFragment();
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, performerListFragment)
                            .commit();
                }
            });


        if (YummiUtils.isPreformer(this))
            findViewById(R.id.notifications).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notificationsw);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("black"));

                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

                    setUpFirstFragment();
                }
            });


        if (YummiUtils.isPreformer(this))
            findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatgw);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("black"));

                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notifications);
                    ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("#ffacabab"));

                    setUpChatFragment();
                }
            });


        if (!YummiUtils.isPreformer(this))
            findViewById(R.id.favourites).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

                    ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icon);
                    ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("black"));
                    ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
                    ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));

                    FavouritesListFragment performerListFragment = new FavouritesListFragment();
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, performerListFragment)
                            .commit();
                }
            });


        if (!YummiUtils.isPreformer(this))
            findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatgw);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("black"));

                    ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
                    ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
                    ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
                    setUpChatFragment();
                }
            });

        if (!YummiUtils.isPreformer(this))
            findViewById(R.id.events).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icon);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("black"));
                    ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
                    ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
                    ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
                    if (MainActivity.activeModel != null) {
                        EventFragment event = new EventFragment();
                        MainActivity.where_go_back = 3;

                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.content_frame, event)
                                .commit();
                    } else {

                        if (MainActivity.createEventModel != null) {
                            EventFragment event = new EventFragment();
                            MainActivity.where_go_back = 3;

                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.content_frame, event)
                                    .commit();
                        } else {
                            EventsListFragment events = new EventsListFragment();
                            MainActivity.where_go_back = 0;
                            // Insert the fragment by replacing any existing fragment
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.content_frame, events)
                                    .commit();
                        }
                    }


                }
            });
        else
            findViewById(R.id.events).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notifications);
                    ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icon);
                    ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("black"));
                    ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("#ffacabab"));
                    ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
                    ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

                    EventsListFragment events = new EventsListFragment();
                    MainActivity.where_go_back = 0;
                    // Insert the fragment by replacing any existing fragment
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.content_frame, events)
                            .commit();


                }
            });


        ArrayList<String> list = new ArrayList<String>();

        for (String s : mPlanetTitles) {
            list.add(s);
        }

        // Set the adapter for the list view
        LeftSideArrayAdapter adap = new LeftSideArrayAdapter(this, list);

        mDrawerList.setAdapter(adap);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.next,  /* "open drawer" description */
                R.string.next  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setTitle("");
//                    getSupportActionBar().setDisplayShowHomeEnabled(true);
//                    getSupportActionBar().setIcon(R.mipmap.title_icon);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }


                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setTitle("");
//                    getSupportActionBar().setDisplayShowHomeEnabled(true);
//                    getSupportActionBar().setIcon(R.mipmap.title_icon);
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setUpActionBar();
    }


    public void setNone() {
        if (YummiUtils.isPreformer(this)) {
            ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notifications);
            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
            ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("#ffacabab"));

            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

        } else {
            ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
            ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
            ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
            ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));

            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));
        }
    }

    public void setLadies() {
        if (YummiUtils.isPreformer(this)) {
            ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notificationsw);
            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
            ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("black"));
            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

        } else {
            ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icon);
            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
            ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("black"));
            ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
            ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
            ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));
        }
    }


    public void setChats() {
        if (YummiUtils.isPreformer(this)) {

            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatgw);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("black"));

            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));

            ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notifications);
            ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("#ffacabab"));

        } else {
            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatgw);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("black"));
            ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
            ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));
            ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
            ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));
        }
    }

    public void setEvents() {
        if (YummiUtils.isPreformer(this)) {
            ((ImageView) findViewById(R.id.notificationsiw)).setImageResource(R.mipmap.tab_bar_notifications);
            ((TextView) findViewById(R.id.notificationstw)).setTextColor(Color.parseColor("#ffacabab"));

            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icon);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("black"));

            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));

        } else {
            ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
            ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));

            ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icon);
            ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("black"));

            ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icong);
            ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
            ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
            ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);
            ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));
        }
    }

    public void setFavourites() {
        ((ImageView) findViewById(R.id.ladiesiw)).setImageResource(R.mipmap.tab_bar_female_icong);
        ((TextView) findViewById(R.id.ladiestw)).setTextColor(Color.parseColor("#ffacabab"));

        ((ImageView) findViewById(R.id.eventsiw)).setImageResource(R.mipmap.tab_bar_events_icong);
        ((TextView) findViewById(R.id.eventstw)).setTextColor(Color.parseColor("#ffacabab"));

        ((ImageView) findViewById(R.id.favouritesiw)).setImageResource(R.mipmap.tab_bar_favorites_icon);
        ((TextView) findViewById(R.id.favouritestw)).setTextColor(Color.parseColor("black"));
        ((ImageView) findViewById(R.id.nearbyiw)).setImageResource(R.mipmap.tab_bar_nearby_icong);
        ((TextView) findViewById(R.id.nearbytw)).setTextColor(Color.parseColor("#ffacabab"));
        ((ImageView) findViewById(R.id.chatiw)).setImageResource(R.mipmap.button_icon_chatg);

        ((TextView) findViewById(R.id.chattw)).setTextColor(Color.parseColor("#ffacabab"));
    }

    public RelativeLayout actionBarLayout;

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        actionBarLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.action_bar_layout, null);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        ImageView drawerImageView = (ImageView) actionBarLayout.findViewById(R.id.drawer_imageview);

        drawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else
                    mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });


        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }


    private void setUpFavourites() {
        FavouritesListFragment fragment = new FavouritesListFragment();
        fragment.setMainActivity(this);

        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();


    }


    private void setUpFirstFragmentWithoutBackstack() {

        if (YummiUtils.isPreformer(this)) {
            NotificationsFragment fragment = new NotificationsFragment();
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else {
            PerformerListFragment fragment = new PerformerListFragment();
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    private void setUpFirstFragment() {

        if (YummiUtils.isPreformer(this)) {
            NotificationsFragment fragment = new NotificationsFragment();
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else {
            PerformerListFragment fragment = new PerformerListFragment();
            fragment.setMainActivity(this);
            Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }


    private void setUpChatFragment(String chatID) {

        ChatListFragment fragment = new ChatListFragment();
        fragment.setChatId(chatID);
        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    private void setUpChatFragment() {

        ChatListFragment fragment = new ChatListFragment();
        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

    }


    public void setUpEventsListFragment() {
        EventsListFragment fragment = new EventsListFragment();
        fragment.setMainActivity(this);
//        Bundle args = new Bundle();
////        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(0, true);
        setTitle(mPlanetTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
//            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapToEvent();

    }


    public void setMapEvent(EventModel event) {
        if (event != null) {
            if (event.getLocation() != null)
                if (map != null) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    map.getUiSettings().setScrollGesturesEnabled(false);
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(event.getLocation().getLat(),
                                    event.getLocation().getLng())).zoom(20).build();


                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(event.getLocation().getLat(),
                                    event.getLocation().getLng()))
                            .draggable(false));
                }
        }
    }


    public void setMapEventToNearby(final ArrayList<PerformerListModel> list) {

        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setScrollGesturesEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(true);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(YummiUtils.getLocation().getLatitude(),
                            YummiUtils.getLocation().getLongitude())).zoom(17).build();


            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    addOverlay();

                    NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                    Call<PerformerModel> call = service.getPerformer(marker.getSnippet(), MainActivity.token);
                    call.enqueue(new Callback<PerformerModel>() {
                        @Override
                        public void onResponse(Call<PerformerModel> call, Response<PerformerModel> response) {
                            removeView();
                            if (response.body() != null) {
                                PerformerFragment perf = new PerformerFragment();
                                perf.setPerformer(response.body());
                                MainActivity.where_go_back = 0;
                                // Insert the fragment by replacing any existing fragment

                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .addToBackStack(null)
                                        .replace(R.id.content_frame, perf)
                                        .commit();

                            }
                        }

                        @Override
                        public void onFailure(Call<PerformerModel> call, Throwable t) {
                            removeView();
                        }
                    });

                }
            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    // Getting reference to the TextView to set latitude
                    TextView tvLat = (TextView) v.findViewById(R.id.textView27);


                    // Setting the latitude
                    tvLat.setText(arg0.getTitle());

                    // Setting the longitude

                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

//            MapDrawer md = new MapDrawer(map);
            map.clear();

            for (PerformerListModel plm : list) {
                try {
//                    md.drawCircle(plm.getLatLng(), 10);
                    map.addCircle(new CircleOptions()
                            .center(plm.getLatLng())
                            .radius(500)
                            .strokeWidth(1f)
                            .strokeColor(0x70a38e31)
                            .fillColor(0x55a38e31));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            for (PerformerListModel plm : list) {
                try {
                    map.addMarker(new MarkerOptions()
                            .position(plm.getLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.yummi_pin))
                            .title(plm.getName())
                            .snippet(plm.getId())
                            .draggable(false));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void setMapEventToNearby() {
        try {
            if (map != null) {
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setMyLocationEnabled(true);
                map.getUiSettings().setScrollGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(YummiUtils.getLocation().getLatitude(),
                                YummiUtils.getLocation().getLongitude())).zoom(17).build();


                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setMapEvent() {
        if (MainActivity.activeModel != null) {
            if (map != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(MainActivity.activeModel.getLocation().getLat(),
                                MainActivity.activeModel.getLocation().getLng())).zoom(20).build();


                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(MainActivity.activeModel.getLocation().getLat(),
                                MainActivity.activeModel.getLocation().getLng()))
                        .draggable(false));
            }
        }


        if (MainActivity.createEventModel != null) {
            if (MainActivity.createEventModel.getLocation() != null)
                if (map != null) {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(MainActivity.createEventModel.getLocation().getLat(),
                                    MainActivity.createEventModel.getLocation().getLng())).zoom(20).build();


                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
        }
    }

    public void setMapToEvent() {
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            map.setMyLocationEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(false);

            if (MainActivity.activeModel != null) {
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(MainActivity.activeModel.getLocation().getLat(),
                                MainActivity.activeModel.getLocation().getLng()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                map.moveCamera(center);
                map.animateCamera(zoom);
            } else {
                if (MainActivity.createEventModel != null) {
                    if (MainActivity.createEventModel.getLocation() != null) {
                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(new LatLng(MainActivity.createEventModel.getLocation().getLat(),
                                        MainActivity.createEventModel.getLocation().getLng()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                        map.moveCamera(center);
                        map.animateCamera(zoom);
                    }
                } else {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            MainActivity.mGoogleApiClient);
                    if (mLastLocation != null) {
                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                                        mLastLocation.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);

                        map.moveCamera(center);
                        map.animateCamera(zoom);
                    }
                }
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(final int position) {
//TODO: MEMORY menagement on stacked fragments


        if (YummiUtils.isPreformer(this)) {
            if (position == 0) {
                ProfileFragment fr = new ProfileFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fr)
                        .commit();
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }

            if (position == 1) {
                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                Call<PerformerModel> call = service.getPerformer(MainActivity.userId, MainActivity.token);
                call.enqueue(new Callback<PerformerModel>() {
                    @Override
                    public void onResponse(Call<PerformerModel> call, Response<PerformerModel> response) {
                        if (response.body() != null) {
                            PerformerFragment perf = new PerformerFragment();
                            perf.setPerformer(response.body());
                            // Insert the fragment by replacing any existing fragment
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.content_frame, perf)
                                    .commit();

                            mDrawerList.setItemChecked(position, true);
                            setTitle(mPlanetTitles[position]);
                            mDrawerLayout.closeDrawer(mDrawerList);

                            setNone();

                        }
                    }

                    @Override
                    public void onFailure(Call<PerformerModel> call, Throwable t) {
                    }
                });

                return;
            }

            if (position == 2) {
                NotificationsFragment fragment = new NotificationsFragment();
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                setNone();
                return;
            }

            if (position == 5) {
                SettingsFragment perf = new SettingsFragment();
                MainActivity.where_go_back = 0;
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, perf)
                        .commit();


//                showAllert("Under development", "comming soon");


                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            }
            if (position == 7) {
                YummiUtils.verifyOtherPermissions(this);

                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);

                    if (YummiUtils.performer.getSecurityNumber().isEmpty())
                        phoneIntent.setData(Uri.parse("tel:+37495462828"));
                    else
                        phoneIntent.setData(Uri.parse("tel:" + YummiUtils.client.getSecurityNumber()));

                    startActivity(phoneIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (position == 8)
                rlogOutPer();

            if (position == 3) {
                setUpEventsListFragment();
            }

        } else {

            if (position == 0) {
                ProfileFragment fr = new ProfileFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fr)
                        .commit();
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            }


            if (position == 1) {
                NotificationsFragment fragment = new NotificationsFragment();
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                setNone();
                return;
            }


            if (position == 2) {
                PerformerFilterFragment perf = new PerformerFilterFragment();
                perf.setFragment(null);
                MainActivity.where_go_back = 0;
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, perf)
                        .commit();


                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            }

            if (position == 7) {
                SettingsFragment perf = new SettingsFragment();
                MainActivity.where_go_back = 0;
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, perf)
                        .commit();


//                showAllert("Under development", "comming soon");


                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            }


            if (position == 9) {
                YummiUtils.verifyOtherPermissions(this);
                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);

                    if (YummiUtils.client.getSecurityNumber().isEmpty())
                        phoneIntent.setData(Uri.parse("tel:+37495462828"));
                    else
                        phoneIntent.setData(Uri.parse("tel:" + YummiUtils.client.getSecurityNumber()));

                    startActivity(phoneIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (position == 10)
                rlogOut();

            if (position == 4) {
                setUpEventsListFragment();
            }

            if (position == 5) {

                PaymentsListFragment fragment = new PaymentsListFragment();
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                return;
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//                ft.addToBackStack(null);
//                // Create and show the dialog.
//                CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
//
//                newFragment.show(ft, "dialog");
//
//                mDrawerList.setItemChecked(position, true);
//                setTitle(mPlanetTitles[position]);
//                mDrawerLayout.closeDrawer(mDrawerList);


            }


            if (position == 3) {
                GalleryFragment fragment = new GalleryFragment();
                // Insert the fragment by replacing any existing fragment
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.content_frame, fragment)
                        .commit();

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mPlanetTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                setNone();
                return;
            }
//
//            // Create a new fragment and specify the planet to show based on position
//            PerformerListFragment fragment = new PerformerListFragment();
//            fragment.setMainActivity(this);
//            Bundle args = new Bundle();
////        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//            fragment.setArguments(args);
//
//            // Insert the fragment by replacing any existing fragment
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.content_frame, fragment)
//                    .commit();
//
//            // Highlight the selected item, update the title, and close the drawer
//            mDrawerList.setItemChecked(position, true);
//            setTitle(mPlanetTitles[position]);
//            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }


    public void setPerformer(PerformerModel perf) {
        PerformerFragment fragment = new PerformerFragment();
        fragment.setPerformer(perf);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    @Override
    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getActionBar().setTitle(mTitle);
    }

    static AlertDialog.Builder alertDialogBuilder;


    final static private String clientExist = "CLIENT_EMAIL_UNIQUENESS";
    final static private String clientBlockerd = "CLIENT_BLOCKED";
    final static private String clientDosntExist = "EMAIL_DOESNT_EXIST";
    final static private String chatFinished = "CHAT_FINISHED";

    final static private String pExist = "PERFORMER_EMAIL_UNIQUENESS";
    final static private String pBlockerd = "PERFORMER_BLOCKED";


    final static private String wrongPassword = "WRONG_PASSWORD";
    final static private String currentEmailChange = "CURRENT_EMAIL";

//
//
//                case .clientExist:
//                return "This e-mail address already exists"
//                case .clientBlockerd:
//                return "Your account has been disabled. please contact the administrator"
//                case .clientDosntExist:
//                return "Client does not exist"
//


    public static void showResponseError(final String error) {

        if (error.contains(clientExist)) {
            showAllert("Error", "This e-mail address already exists");
        }

        if (error.contains(wrongPassword)) {
            showAllert("Error", "Password not matched");
            return;
        }

        if (error.contains(currentEmailChange)) {
            showAllert("Error", "This is your current email address");
            return;
        }

        if (error.contains(clientBlockerd)) {
            showAllert("Error", "Your account has been disabled. please contact the administrator");
        }

        if (error.contains(pExist)) {
            showAllert("Error", "This e-mail address already exists");
        }

        if (error.contains(pBlockerd)) {
            showAllert("Error", "Your account has been disabled. please contact the administrator");
        }

        if (error.contains(clientDosntExist)) {
            showAllert("Error", "Client does not exist");
        }

        if (error.contains(chatFinished)) {
            showAllert("Error", "Active period for this chat is finished");
        }


    }

    public static void showAllert(final String title, final String msg) {

        alertDialogBuilder.setPositiveButton("OK", null);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void removeView() {
        try {
            rl.removeView(overlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOverlay() {
        rl = (DrawerLayout) findViewById(R.id.drawer_layout);

        overlay = (LinearLayout) getLayoutInflater().inflate(R.layout.refresh_overlay, null);
        LinearLayout.LayoutParams ppp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        overlay.setLayoutParams(ppp);
        overlay.findViewById(R.id.coach_marks_image).startAnimation(AnimationUtils.loadAnimation(this, R.anim.infinite_rotation));

        rl.addView(overlay);
    }


    DrawerLayout rl;
    LinearLayout overlay;


    public ArrayList<ServiceModel> headers = new ArrayList<ServiceModel>();

    private void getServiceList() {

        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

        Call<List<RegistrationEightResponseModel>> call = service.getServiceListByGender(YummiUtils.performer.getGenderId(), MainActivity.token);

        call.enqueue(new Callback<List<RegistrationEightResponseModel>>() {
            @Override
            public void onResponse(Call<List<RegistrationEightResponseModel>> call, Response<List<RegistrationEightResponseModel>> response) {
                if (response.body() != null) {
                    WelcomeActivity.serviceListDO = response.body();
                    WelcomeActivity.services = new ArrayList<ServicePushModel>();


                    for (RegistrationEightResponseModel g : WelcomeActivity.serviceListDO) {
                        for (ServiceModel m : g.getServiceList())
                            WelcomeActivity.services.add(new ServicePushModel(0, m.getId()));
                    }

                    for (Service perModel : YummiUtils.performer.getServices()) {
                        int i = 0;
                        for (ServicePushModel rModel : WelcomeActivity.services) {
                            if (perModel.getService().getId().equalsIgnoreCase(rModel.getServiceId())) {
                                WelcomeActivity.services.get(i).setAvailability(perModel.getAvailability());
                            }
                            i++;
                        }
                    }


                    headers.clear();
                    ArrayList<ServiceModel> result = new ArrayList<ServiceModel>();
                    if (WelcomeActivity.serviceListDO != null)
                        for (RegistrationEightResponseModel g : WelcomeActivity.serviceListDO) {
                            result.add(new ServiceModel(g.getName(), ""));
                            headers.add(new ServiceModel(g.getName(), g.getId()));
                            for (ServiceModel m : g.getServiceList())
                                result.add(m);
                        }
                }
                removeView();
            }

            @Override
            public void onFailure(Call<List<RegistrationEightResponseModel>> call, Throwable t) {
                call.toString();
                removeView();
            }
        });
    }


    private void getActualBankAccount() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<BankModel> call = service.getBankAccount(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<BankModel>() {
            @Override
            public void onResponse(Call<BankModel> call, Response<BankModel> response) {
                if (response.body() != null) {
                    YummiUtils.bankModel = response.body();
                } else {
                    YummiUtils.bankModel = null;
                }
            }

            @Override
            public void onFailure(Call<BankModel> call, Throwable t) {
                removeView();
            }
        });
    }

    private void getActualPerformer() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<PerformerModel> call = service.getPerformer(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<PerformerModel>() {
            @Override
            public void onResponse(Call<PerformerModel> call, Response<PerformerModel> response) {

                if (response.body() != null) {
                    YummiUtils.performer = response.body();
                    getServiceList();

                }
            }

            @Override
            public void onFailure(Call<PerformerModel> call, Throwable t) {
                removeView();
            }
        });
    }

}
