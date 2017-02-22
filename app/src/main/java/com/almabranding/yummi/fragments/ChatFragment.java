package com.almabranding.yummi.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.ChatAdapter;
import com.almabranding.yummi.adapter.ChatsAdapter;
import com.almabranding.yummi.models.chat.ChatMessageModel;
import com.almabranding.yummi.models.chat.ChatModel;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiUtils;
import com.github.nkzawa.emitter.Emitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class ChatFragment extends ListFragment {


    private String requestId = "";

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    private String clientName = "";

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);


//        if (YummiUtils.currentlyOpenedChat.isEmpty()){
//            YummiUtils.currentlyOpenedChat
//        }

        if (chatId.isEmpty()) {

            ChatListFragment fragment = new ChatListFragment();
            // Insert the fragment by replacing any existing fragment
            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, fragment)
                    .commit();


            return view;


        }

        if (getActivity().findViewById(R.id.send_image_close) != null)
            getActivity().findViewById(R.id.send_image_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    getActivity().findViewById(R.id.send_picture_layout).setVisibility(View.GONE);
                    ((ImageButton) getActivity().findViewById(R.id.send_image_select)).setImageResource(R.mipmap.photo_icon);
                    getActivity().findViewById(R.id.send_image_button).setOnClickListener(null);
                    requestId = "";
                    clientName = "";
                }
            });

        ((EditText) view.findViewById(R.id.send_edit_text)).setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);


        ((EditText) view.findViewById(R.id.send_edit_text)).setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER: {
                                if (getView().findViewById(R.id.send_edit_text) != null) {
                                    if (!((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().isEmpty())
                                        ((MainActivity) getActivity()).newChatMessage(ChatFragment.this, chatId, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString());

                                    ((EditText) getView().findViewById(R.id.send_edit_text)).setText("");
                                }
                                return true;
                            }
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        view.findViewById(R.id.send_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getView().findViewById(R.id.send_edit_text) != null) {
                    if (!((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().isEmpty())
                        ((MainActivity) getActivity()).newChatMessage(ChatFragment.this, chatId, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().replace('\n', ' '));//substring(0, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().length() - 1)

                    ((EditText) getView().findViewById(R.id.send_edit_text)).setText("");
                }
            }
        });

        ((EditText) view.findViewById(R.id.send_edit_text)).addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0) {
                        if (s.charAt(s.length() - 1) == '\n') {
                            if (getView().findViewById(R.id.send_edit_text) != null) {
                                if (!((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().isEmpty())
                                    ((MainActivity) getActivity()).newChatMessage(ChatFragment.this, chatId, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().replace('\n', ' '));//substring(0, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().length() - 1)

                                ((EditText) getView().findViewById(R.id.send_edit_text)).setText("");
                            }
                        }

                        if (start < s.length()) {
                            if (s.charAt(start) == '\n') {
                                if (getView().findViewById(R.id.send_edit_text) != null) {
                                    if (!((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().isEmpty())
                                        ((MainActivity) getActivity()).newChatMessage(ChatFragment.this, chatId, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().replace('\n', ' '));//substring(0, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().length() - 1)

                                    ((EditText) getView().findViewById(R.id.send_edit_text)).setText("");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ((EditText) view.findViewById(R.id.send_edit_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == 12323) {

                if (getView().findViewById(R.id.send_edit_text) != null) {
                    if (!((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString().isEmpty())
                        ((MainActivity) getActivity()).newChatMessage(ChatFragment.this, chatId, ((EditText) getView().findViewById(R.id.send_edit_text)).getText().toString());

                    ((EditText) getView().findViewById(R.id.send_edit_text)).setText("");
                }


                return true;
//                }
//                return false;
            }
        });


//        setEmptyText("No Notifications");


        view.findViewById(R.id.parent).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getView() != null)
                    if (getListView() != null) {
                        View q = view.findViewById(R.id.parent);

                        int heightDiff = q.getRootView().getHeight() / q.getHeight();

//                        Log.e("heightDiff", String.valueOf(heightDiff));
//                        Log.e("heightDiff2", String.valueOf((float) q.getRootView().getHeight() / q.getHeight()));
//                        Log.e("q.getHeight()", String.valueOf(q.getHeight()));
//                        Log.e("q.getRootView()", String.valueOf(q.getRootView().getHeight()));
//
//                        Log.e("getListView()", String.valueOf(getListView().getHeight()));


                        if (!isOpen)
                            if (heightDiff == 2) {

                                if (getListAdapter() != null)
                                    getListView().smoothScrollToPosition(getListAdapter().getCount());

                                Log.e("MyActivity", "keyboard opened");
                                ((MainActivity) getActivity()).findViewById(R.id.tab_bar_container).setVisibility(View.GONE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
                                ((MainActivity) getActivity()).findViewById(R.id.content_frame).setLayoutParams(params);
                                isOpen = true;


                            }
                        if (isOpen) {
                            if (heightDiff == 1) {
                                Log.e("MyActivity", "keyboard closed");


                                AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
                                animation1.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        ((MainActivity) getActivity()).findViewById(R.id.tab_bar_container).setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        if (!isOpen)
                                            if (getListAdapter() != null)
                                                getListView().smoothScrollToPosition(getListAdapter().getCount());
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                animation1.setDuration(400);
                                ((MainActivity) getActivity()).findViewById(R.id.tab_bar_container).setAlpha(1.0f);
                                ((MainActivity) getActivity()).findViewById(R.id.tab_bar_container).startAnimation(animation1);

                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.87f);
                                ((MainActivity) getActivity()).findViewById(R.id.content_frame).setLayoutParams(params);
                                isOpen = false;
                            }

                        }

                    }
            }
        });


        return view;
    }

    boolean isOpen = false;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }


    public void setUpActionBar() {

        ((MainActivity) getActivity()).setChats();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("Chat");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (YummiUtils.currentlyOpenedChatresult != null) {
            if (ChatFragment.this != null) {
                if (ChatFragment.this.getContext() != null) {
                    adapter = new ChatAdapter(getContext(), YummiUtils.currentlyOpenedChatresult);
                    setListAdapter(adapter);

                }
            }
        }


        setUpActionBar();
        getNotifications();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).getmSocket() != null)
            ((MainActivity) getActivity()).getmSocket().on("newChatMessage", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String updatedEventId = (String) args[0];
                                String message = (String) args[1];
                                String date = (String) args[2];
                                final ChatMessageModel chatMessageModel = new ChatMessageModel();
                                chatMessageModel.setId(updatedEventId);
                                chatMessageModel.setOwnerId(updatedEventId);
                                chatMessageModel.setMessage(message);
                                chatMessageModel.setCreatedAt(date);

                                try {
                                    if (updatedEventId.equalsIgnoreCase(YummiUtils.currentlyOpenedChat)) {
                                        result.add(chatMessageModel);

                                        YummiUtils.currentlyOpenedChatresult = new ArrayList<ChatMessageModel>(result);
                                        if (ChatFragment.this != null) {
                                            if (ChatFragment.this.getContext() != null) {
                                                adapter = new ChatAdapter(ChatFragment.this.getContext(), result);
                                                setListAdapter(adapter);

                                            }
                                        }


                                        try {

                                            ((MainActivity) getActivity()).readChat(YummiUtils.currentlyOpenedChat);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        ((MainActivity) getActivity()).unreadedMessage();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        YummiUtils.currentlyOpenedChat = chatId;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onPause() {
        ((MainActivity) getActivity()).getmSocket().off("newChatMessage");

        super.onPause();
        YummiUtils.currentlyOpenedChat = "";
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((MainActivity) getActivity()).findViewById(R.id.tab_bar_container).setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.87f);
        ((MainActivity) getActivity()).findViewById(R.id.content_frame).setLayoutParams(params);
    }


    public String chatId = "";

    ChatAdapter adapter;
    final ArrayList<ChatMessageModel> result = new ArrayList<ChatMessageModel>();

    public void assToChat(final String chat) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatMessageModel c = new ChatMessageModel();
                c.setMessage(chat);
                c.setOwnerId(MainActivity.userId);
                result.add(c);

                YummiUtils.currentlyOpenedChatresult = new ArrayList<ChatMessageModel>(result);
                if (ChatFragment.this != null) {
                    if (ChatFragment.this.getContext() != null) {
                        adapter = new ChatAdapter(ChatFragment.this.getContext(), result);
                        setListAdapter(adapter);

                    }
                }
            }
        });


    }

    public ArrayList<ChatMessageModel> getNotifications() {

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<ChatMessageModel>> call;

        call = service.getChatMessages(chatId, MainActivity.token);

        call.enqueue(new Callback<List<ChatMessageModel>>() {
                         @Override
                         public void onResponse(Call<List<ChatMessageModel>> call, final Response<List<ChatMessageModel>> response) {
//                    bustModelList = response.body();

                             if (response.body() != null) {
                                 result.clear();

                                 YummiUtils.currentlyOpenedChatresult = null;


                                 for (int i = response.body().size() - 1; i >= 0; i--) {
                                     result.add(response.body().get(i));
                                 }
//                                 for (ChatMessageModel e : response.body()) {
//                                     result.add(e);
//                                 }
                                 YummiUtils.currentlyOpenedChatresult = new ArrayList<ChatMessageModel>(result);
                                 if (ChatFragment.this != null) {
                                     if (ChatFragment.this.getContext() != null) {
                                         adapter = new ChatAdapter(ChatFragment.this.getContext(), result);
                                         setListAdapter(adapter);

                                     }
                                 }
                             }

                         }

                         @Override
                         public void onFailure(Call<List<ChatMessageModel>> call, Throwable t) {
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
