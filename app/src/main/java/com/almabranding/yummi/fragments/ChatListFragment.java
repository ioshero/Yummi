package com.almabranding.yummi.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.ChatsAdapter;
import com.almabranding.yummi.adapter.EventsAdapter;
import com.almabranding.yummi.adapter.NotificationsAdapter;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.models.chat.ChatModel;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiUtils;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 08/06/16.
 */
public class ChatListFragment extends Fragment {


    private String chatId = "";

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).mSocket.on("chatStatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String updatedChatId = (String) args[0];
                    int statusValue = (int) args[1];

                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).getId().equalsIgnoreCase(updatedChatId)) {
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


        if (((MainActivity) getActivity()).getmSocket() != null)
            ((MainActivity) getActivity()).getmSocket().on("newChatMessage", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
//                        String updatedEventId = (String) args[0];

                        ((MainActivity) getActivity()).chatList(ChatListFragment.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

    }

    @Override
    public void onPause() {
        ((MainActivity) getActivity()).getmSocket().off("newChatMessage");
        ((MainActivity) getActivity()).mSocket.off("chatStatus");
        super.onPause();
    }


    boolean started = false;
    SwipeMenuListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);
        started = false;
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);

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
                        deleteItem.setWidth((int) (90 * ChatListFragment.this.getContext().getResources().getDisplayMetrics().density));
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
                    case 1: {

                        break;
                    }

                }


            }
        };

// set creator
        listView.setMenuCreator(creator);

        if (!chatId.isEmpty()) {
            ChatFragment fragment = new ChatFragment();
            // Insert the fragment by replacing any existing fragment
            fragment.chatId = chatId;
            try {
                ((MainActivity) getActivity()).readChat(fragment.chatId);

            } catch (Exception e) {
                e.printStackTrace();
            }


            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .hide(ChatListFragment.this)
                    .replace(R.id.content_frame, fragment)
                    .commit();


            chatId = "";
            started = true;
            return view;
        }


//        setEmptyText("No Notifications");

        return view;
    }


    public void updateResult(JSONArray chats) {
        try {
            for (int i = 0; i < chats.length(); i++) {
                JSONObject chat = chats.getJSONObject(i);
                for (int j = 0; j < result.size(); j++) {
                    if (result.get(j).getId().equalsIgnoreCase(chat.getString("id"))) {
                        result.get(j).setUnreaded(chat.getInt("unread"));


                    }
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


    public void setUpActionBar() {

        ((MainActivity) getActivity()).setChats();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.INVISIBLE);
        title.setText("Chats");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();

        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 0: {
                        // delete
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).addOverlay();

                        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                        if (getActivity() != null) {
                            Call<ResponseBody> call;


                            if (YummiUtils.isPreformer(getActivity())) {
                                call = service.deleteChatPer(YummiUtils.getUserId(getActivity()), result.get(position).getId(), MainActivity.token);
                            } else {
                                call = service.deleteChat(YummiUtils.getUserId(getActivity()), result.get(position).getId(), MainActivity.token);
                            }

                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    bustModelList = response.body();
                                    result.remove(position);
                                    adapter.notifyDataSetChanged();
                                    if (getActivity() != null)
                                        ((MainActivity) getActivity()).removeView();

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                                    if (getActivity() != null)
                                        ((MainActivity) getActivity()).removeView();
                                }
                            });
                        }
                        break;
                    }
                }
                // false : close the menu; true : not close the menu
                return true;
            }
        });


        if (!started) {
            if (!result.isEmpty())
                if (ChatListFragment.this != null) {
                    if (ChatListFragment.this.getContext() != null) {
                        Collections.sort(result);
                        adapter = new ChatsAdapter(ChatListFragment.this.getContext(), result);
                        listView.setAdapter(adapter);
                        listView.setEmptyView(getView().findViewById(R.id.emptyElement));


                        if (((MainActivity) getActivity()).mSocket.connected())
                            ((MainActivity) getActivity()).chatList(ChatListFragment.this);

                        if (getView() != null)
                            listView.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                    ChatFragment fragment = new ChatFragment();
                                    // Insert the fragment by replacing any existing fragment
                                    fragment.chatId = result.get(position).getId();
                                    try {
                                        ((MainActivity) getActivity()).readChat(fragment.chatId);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    YummiUtils.currentlyOpenedChatresult = null;
                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .addToBackStack(null)
                                            .hide(ChatListFragment.this)
                                            .replace(R.id.content_frame, fragment)
                                            .commit();
                                }
                            });
//
                    }
                }

            getNotifications();
        }

    }


    ChatsAdapter adapter;
    static final ArrayList<ChatModel> result = new ArrayList<ChatModel>();


    private ArrayList<ChatModel> getNotifications() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<ChatModel>> call;
        if (!YummiUtils.isPreformer(getContext()))
            call = service.getChats(MainActivity.userId, MainActivity.token);
        else
            call = service.getChatsPer(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<List<ChatModel>>() {
                         @Override
                         public void onResponse(Call<List<ChatModel>> call, final Response<List<ChatModel>> response) {
//                    bustModelList = response.body();

                             if (response.body() != null) {
                                 result.clear();
                                 for (ChatModel e : response.body()) {
                                     result.add(e);
                                 }
                                 if (ChatListFragment.this != null) {
                                     if (ChatListFragment.this.getContext() != null) {
                                         Collections.sort(result);
                                         adapter = new ChatsAdapter(ChatListFragment.this.getContext(), result);
                                         listView.setAdapter(adapter);
                                         if (getView() != null)
                                             listView.setEmptyView(getView().findViewById(R.id.emptyElement));


                                         if (((MainActivity) getActivity()).mSocket.connected())
                                             ((MainActivity) getActivity()).chatList(ChatListFragment.this);

                                         if (getView() != null)
                                             listView.setOnItemClickListener(new OnItemClickListener() {
                                                 @Override
                                                 public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                                                     ChatFragment fragment = new ChatFragment();
                                                     // Insert the fragment by replacing any existing fragment
                                                     fragment.chatId = result.get(position).getId();
                                                     try {
                                                         ((MainActivity) getActivity()).readChat(fragment.chatId);

                                                     } catch (Exception e) {
                                                         e.printStackTrace();
                                                     }
                                                     YummiUtils.currentlyOpenedChatresult = null;
                                                     android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                     fragmentManager.beginTransaction()
                                                             .addToBackStack(null)
                                                             .hide(ChatListFragment.this)
                                                             .replace(R.id.content_frame, fragment)
                                                             .commit();
                                                 }
                                             });
//
                                     }
                                 }
                             }

                         }

                         @Override
                         public void onFailure(Call<List<ChatModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                         }
                     }

        );

        return result;
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return "";
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    private String getRealpath(final Intent data) {
        String realPath = "";
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getActivity(), data.getData());

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), data.getData());

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), data.getData());

        return realPath;
    }

}
