package com.almabranding.yummi.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.FullScreenViewActivity;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.adapter.GridViewAdapter;
import com.almabranding.yummi.adapter.NotificationsAdapter;
import com.almabranding.yummi.models.EventModel;
import com.almabranding.yummi.models.MediaModel;
import com.almabranding.yummi.models.NotificationModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 03/05/16.
 */
public class GalleryFragment extends Fragment {
    GridView gridView;

    public static ArrayList<String> images = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        return view;
    }

    public void setUpActionBar() {

        ((MainActivity) getActivity()).setNone();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("Gallery");


        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                intent.putExtra("order", i);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });

        getGallery();

    }


    //    ImageGridAdapter adapter;
    final ArrayList<NotificationModel> result = new ArrayList<NotificationModel>();
    ArrayList<NotificationModel> events = new ArrayList<NotificationModel>();

    private void getGallery() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

        Call<List<MediaModel>> call = service.mediaGallery(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<List<MediaModel>>() {
                         @Override
                         public void onResponse(Call<List<MediaModel>> call, final Response<List<MediaModel>> response) {
                             if (response.body() != null) {
                                 if (getView() != null) {
                                     final LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.performers_tab_bar);

                                     linearLayout.removeAllViews();

                                     linearLayout.setWeightSum(response.body().size());

                                     for (final MediaModel p : response.body()) {
                                         final TextView valueTV = new TextView(getActivity());
                                         valueTV.setText(p.getPerformerName());
                                         if (response.body().indexOf(p) == 0) {
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
//                                             setUpServiceListView(getServiceAdapter(new ArrayList<Service>(), p));
                                             }
                                         });

                                         valueTV.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
                                         valueTV.setGravity(Gravity.CENTER);

                                         linearLayout.addView(valueTV);
                                     }
                                     if (response.body().size() > 0) {
                                         images = new ArrayList(response.body().get(0).getImages());
                                         GridViewAdapter gridAdapter = new GridViewAdapter(getActivity(), new ArrayList(response.body().get(0).getImages()));
                                         gridView.setAdapter(gridAdapter);
                                         gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                 Intent intent = new Intent(getActivity(), FullScreenViewActivity.class);
                                                 intent.putExtra("order", i);
                                                 intent.putExtra("type", 1);
                                                 startActivity(intent);
                                             }
                                         });
                                     }

                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<List<MediaModel>> call, Throwable t) {
//                    showAllert("Network Error", t.getMessage());
                         }
                     }

        );

    }

}
