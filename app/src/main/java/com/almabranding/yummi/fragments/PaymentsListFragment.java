package com.almabranding.yummi.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.adapter.CardsAdapter;
import com.almabranding.yummi.adapter.EventsAdapter;
import com.almabranding.yummi.models.CardModel;
import com.almabranding.yummi.models.EventListModel;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.PaymentTokenModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 19/04/16.
 */
public class PaymentsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    SwipeMenuListView listView;
    MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_cards, container, false);
        MainActivity.where_go_back = -1;


        listView = (SwipeMenuListView) view.findViewById(R.id.listView);

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
                        deleteItem.setWidth((int) (90 * PaymentsListFragment.this.getContext().getResources().getDisplayMetrics().density));
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

        return view;
    }


    public void addCardToken(final String tok) {//, String digit, int month, int year

        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<CardModel> call = service.postCards(MainActivity.userId, MainActivity.token, new PaymentTokenModel(tok));

        call.enqueue(new Callback<CardModel>() {
            @Override
            public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                if (response.body() != null) {
//{"token":"card_18FlH1GXxxfrcBAAAtMMp9Gz","digits":"4242","default":true,"type":"Visa","expirationMonth":12,"expirationYear":2023,"id":"57484f68ee11a2bf6f4eb1c0","clientId":"572b6bc1c8f110182b5f2f22"}
                    YummiUtils.client.getCards().add(response.body());
                    result.add(response.body());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });


                }

            }

            @Override
            public void onFailure(Call<CardModel> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
            }
        });


    }


    public void setUpActionBar() {
        ((MainActivity) getActivity()).setNone();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        ImageView title = (ImageView) actionBar.getCustomView().findViewById(R.id.drawer_imageview_done);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setImageResource(R.mipmap.add);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                // Create and show the dialog.
                CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
                newFragment.setEventModel(null);
                newFragment.setFrag(PaymentsListFragment.this);
                newFragment.show(ft, "dialog");

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

        if (!result.isEmpty())
        if (getActivity() != null)
            adapter = new CardsAdapter(getActivity(), result);

        events = getEvents();

        listView.setOnItemClickListener(this);
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: {
                        //delete

                        ((MainActivity) getActivity()).deleteCard(YummiUtils.client.getCards().get(position).getId());
                        YummiUtils.client.getCards().remove(position);
                        result.remove(position);


                        if (!setDefaultIfCan(0))
                            adapter.notifyDataSetChanged();

                        break;
                    }

                }
                // false : close the menu; true : not close the menu
                return false;
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
                        .hide(PaymentsListFragment.this)
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
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        setDefaultIfCan(position);

    }

    private boolean setDefaultIfCan(int position) {

        if (YummiUtils.client.getCards().size() > position) {

            ((MainActivity) getActivity()).defaultCard(YummiUtils.client.getCards().get(position).getId());
            int i = 0;
            for (CardModel cm : YummiUtils.client.getCards()) {
                YummiUtils.client.getCards().get(i).setDef(false);
                result.get(i).setDef(false);
                i++;
            }

            YummiUtils.client.getCards().get(position).setDef(true);
            result.get(position).setDef(true);

            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    CardsAdapter adapter;
    static ArrayList<CardModel> result = new ArrayList<CardModel>();
    ArrayList<CardModel> events = new ArrayList<CardModel>();

    private ArrayList<CardModel> getEvents() {
        result = new ArrayList<CardModel>(YummiUtils.client.getCards());


        if (getActivity() != null)
            adapter = new CardsAdapter(getActivity(), result);
        if (listView != null) {
            listView.setAdapter(adapter);
            if (getView() != null)
                listView.setEmptyView(getView().findViewById(R.id.emptyElement));
        }

        return result;
    }


}
