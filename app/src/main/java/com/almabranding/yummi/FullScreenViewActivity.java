package com.almabranding.yummi;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.almabranding.yummi.adapter.FullScreenImageAdapter;
import com.almabranding.yummi.adapter.FullScreenImageStringAdapter;
import com.almabranding.yummi.fragments.GalleryFragment;
import com.almabranding.yummi.fragments.NotificationsFragment;
import com.almabranding.yummi.fragments.PerformerFragment;
import com.almabranding.yummi.models.ImageModel;
import com.almabranding.yummi.models.PerformerModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 12/05/16.
 */
public class FullScreenViewActivity extends AppCompatActivity {
    int order = 0;

    int type = 0;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Bundle bundle = getIntent().getExtras();
        order = bundle.getInt("order");
        type = bundle.getInt("type");
        setContentView(R.layout.activity_fullscreen_view);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_fullscreen_view);


        Bundle bundle = getIntent().getExtras();

        if (bundle.getInt("type") == 0)
            if (PerformerFragment.performer != null) {

                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                if (pager != null) {
                    FullScreenImageAdapter adp = new FullScreenImageAdapter(this, new ArrayList<ImageModel>(PerformerFragment.performer.getImages()));
                    pager.setAdapter(adp);
                    order = bundle.getInt("order");
                    pager.setCurrentItem(order);
                }
            } else
                finish();

        if (bundle.getInt("type") == 1)
            if (GalleryFragment.images != null) {

                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                if (pager != null) {
                    FullScreenImageStringAdapter adp = new FullScreenImageStringAdapter(this, GalleryFragment.images);
                    pager.setAdapter(adp);
                    order = bundle.getInt("order");
                    pager.setCurrentItem(order);
                }
            } else
                finish();
    }
}
