package com.almabranding.yummi.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.third.GenderModel;
import com.almabranding.yummi.utils.DistanceRangeSeek;
import com.almabranding.yummi.utils.HeightRangeSeek;
import com.almabranding.yummi.utils.PriceRangeSeek;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiRangeSeek;
import com.almabranding.yummi.utils.YummiUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 13/05/16.
 */
public class PerformerFilterFragment extends DialogFragment {

    private PerformerListFragment fragment;

    private void setUpActionBar() {
        ((MainActivity) getActivity()).setNone();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("");

        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.INVISIBLE);
        final TextView cancel = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview);
        cancel.setVisibility(View.VISIBLE);
        cancel.setText("CANCEL");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    getActivity().onBackPressed();

            }
        });

        final TextView update = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview_done);
        update.setVisibility(View.VISIBLE);
        update.setText("SEARCH");
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.showAllert("under development","");
                sp.edit().putBoolean("filtering", true).commit();
                if (fragment != null) {
                    fragment.getPerformers();
                }

                getActivity().onBackPressed();

            }
        });


        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.actionbar_titleview).setVisibility(View.INVISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getGenderList();
        final View view = inflater.inflate(R.layout.fragment_filter, container, false);


        return view;


    }

    TextView gender_a = null;
    TextView gender_b = null;
    TextView gender_c = null;
    TextView gender_d = null;
    SharedPreferences sp;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getHairList();
        getBodyList();
        getBustList();
        setUpActionBar();


        gender_a = ((TextView) getView().findViewById(R.id.choice_a));
        gender_b = ((TextView) getView().findViewById(R.id.choice_b));
        gender_c = ((TextView) getView().findViewById(R.id.choice_c));
        gender_d = ((TextView) getView().findViewById(R.id.choice_d));

        final PriceRangeSeek priceRangeSeek = ((PriceRangeSeek) getView().findViewById(R.id.priceRangeSeekBar));
        priceRangeSeek.setSelectedMaxValue(sp.getLong("filter_price_max", 1999));
        priceRangeSeek.setSelectedMinValue(sp.getLong("filter_price_min", 100));
        priceRangeSeek.setOnRangeSeekBarChangeListener(new PriceRangeSeek.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(PriceRangeSeek bar, Object minValue, Object maxValue) {
                sp.edit().putLong("filter_price_min", bar.getSelectedMinValue().longValue()).commit();
                sp.edit().putLong("filter_price_max", bar.getSelectedMaxValue().longValue()).commit();
            }
        });


        final HeightRangeSeek heightRangeSeek = ((HeightRangeSeek) getView().findViewById(R.id.heightRangeSeekBar));

        heightRangeSeek.setSelectedMaxValue(sp.getLong("filter_height_max", 219));
        heightRangeSeek.setSelectedMinValue(sp.getLong("filter_height_min", 140));
        heightRangeSeek.setOnRangeSeekBarChangeListener(new HeightRangeSeek.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(HeightRangeSeek bar, Object minValue, Object maxValue) {
                sp.edit().putLong("filter_height_min", bar.getSelectedMinValue().longValue()).commit();
                sp.edit().putLong("filter_height_max", bar.getSelectedMaxValue().longValue()).commit();
            }
        });


        final YummiRangeSeek ageRangeSeek = ((YummiRangeSeek) getView().findViewById(R.id.ageRangeSeekBar));

        ageRangeSeek.setSelectedMaxValue(sp.getLong("filter_age_max", 49));
        ageRangeSeek.setSelectedMinValue(sp.getLong("filter_age_min", 18));

        ageRangeSeek.setOnRangeSeekBarChangeListener(new YummiRangeSeek.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(YummiRangeSeek bar, Object minValue, Object maxValue) {
                sp.edit().putLong("filter_age_min", bar.getSelectedMinValue().longValue()).commit();
                sp.edit().putLong("filter_age_max", bar.getSelectedMaxValue().longValue()).commit();
            }
        });

        final DistanceRangeSeek distanceRangeSeekBar = ((DistanceRangeSeek) getView().findViewById(R.id.distanceRangeSeekBar));
//        distanceRangeSeekBar.setSelectedMaxValue(1000);
        distanceRangeSeekBar.setSelectedMaxValue(sp.getLong("filter_dist_max", 1000));
        distanceRangeSeekBar.setOnRangeSeekBarChangeListener(new DistanceRangeSeek.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(DistanceRangeSeek bar, Object minValue, Object maxValue) {
                sp.edit().putLong("filter_dist_max", bar.getSelectedMaxValue().longValue()).commit();
            }
        });


        gender_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender_a.setBackgroundResource(R.drawable.left_on);
                gender_a.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));

                gender_b.setBackgroundResource(R.drawable.mid_off);
                gender_b.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_c.setBackgroundResource(R.drawable.mid_off);
                gender_c.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_d.setBackgroundResource(R.drawable.right_off);
                gender_d.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                sp.edit().putString("filter_gender", "").commit();

            }
        });
        gender_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender_a.setBackgroundResource(R.drawable.left_off);
                gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_b.setBackgroundResource(R.drawable.mid_on);
                gender_b.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));

                gender_c.setBackgroundResource(R.drawable.mid_off);
                gender_c.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_d.setBackgroundResource(R.drawable.right_off);
                gender_d.setTextColor(getActivity().getResources().getColor(R.color.blackColor));
                if (genderModelList != null)
                    for (GenderModel gm : genderModelList) {
                        if (gm.getName().equalsIgnoreCase("male"))
                            sp.edit().putString("filter_gender", gm.getId()).commit();
                    }

            }
        });
        gender_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender_a.setBackgroundResource(R.drawable.left_off);
                gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_b.setBackgroundResource(R.drawable.mid_off);
                gender_b.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_c.setBackgroundResource(R.drawable.mid_on);
                gender_c.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));

                gender_d.setBackgroundResource(R.drawable.right_off);
                gender_d.setTextColor(getActivity().getResources().getColor(R.color.blackColor));
                if (genderModelList != null)
                    for (GenderModel gm : genderModelList) {
                        if (gm.getName().equalsIgnoreCase("female"))
                            sp.edit().putString("filter_gender", gm.getId()).commit();
                    }
            }
        });
        gender_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender_a.setBackgroundResource(R.drawable.left_off);
                gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_b.setBackgroundResource(R.drawable.mid_off);
                gender_b.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_c.setBackgroundResource(R.drawable.mid_off);
                gender_c.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                gender_d.setBackgroundResource(R.drawable.right_on);
                gender_d.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));
                if (genderModelList != null)
                    for (GenderModel gm : genderModelList) {
                        if (gm.getName().equalsIgnoreCase("fluid"))
                            sp.edit().putString("filter_gender", gm.getId()).commit();
                    }
            }
        });


    }


    private ArrayList<String> getStringListFromModeList(List<GenderModel> modes) {

        ArrayList<String> result = new ArrayList<String>();
        for (GenderModel g : modes) {
            result.add(g.getName());
        }
        return result;
    }

    Spinner spinner_hair;

    private void getHairList() {
        if (WelcomeActivity.hairModelList == null) {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = service.getHairColor(MainActivity.token);

            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    WelcomeActivity.hairModelList = response.body();

                    if (getView() != null) {
                        spinner_hair = ((Spinner) getView().findViewById(R.id.spinner_hair));
                        if (spinner_hair != null) {
                            ArrayList<String> hair = getStringListFromModeList(WelcomeActivity.hairModelList);
                            hair.add(0, "none");
                            ArrayAdapter<String> adapter_hair = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, hair);
                            spinner_hair.setAdapter(adapter_hair);
                            spinner_hair.setSelection(sp.getInt("filter_hair_pos", 0));
                            spinner_hair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                           int i, long arg3) {
                                    // TODO Auto-generated method stub
                                    if (i > 0) {
                                        sp.edit().putString("filter_hair", WelcomeActivity.hairModelList.get(i - 1).getId()).commit();
                                        sp.edit().putInt("filter_hair_pos", i).commit();
                                    } else {
                                        sp.edit().putString("filter_hair", "").commit();
                                        sp.edit().putInt("filter_hair_pos", 0).commit();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {

                }
            });
        } else {
            if (getView() != null) {
                spinner_hair = ((Spinner) getView().findViewById(R.id.spinner_hair));
                if (spinner_hair != null) {
                    ArrayList<String> hair = getStringListFromModeList(WelcomeActivity.hairModelList);
                    hair.add(0, "none");
                    ArrayAdapter<String> adapter_hair = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, hair);
                    spinner_hair.setAdapter(adapter_hair);
                    spinner_hair.setSelection(sp.getInt("filter_hair_pos", 0));
                    spinner_hair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int i, long arg3) {
                            // TODO Auto-generated method stub
                            if (i > 0) {
                                sp.edit().putString("filter_hair", WelcomeActivity.hairModelList.get(i - 1).getId()).commit();
                                sp.edit().putInt("filter_hair_pos", i).commit();
                            } else {
                                sp.edit().putString("filter_hair", "").commit();
                                sp.edit().putInt("filter_hair_pos", 0).commit();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });


                }
            }
        }
    }

    Spinner spinner_body;
    List<GenderModel> genderModelList = null;

    private void getGenderList() {
        if (genderModelList == null) {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
            Call<List<GenderModel>> call = null;

            call = service.getGenderList(MainActivity.token);


            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    genderModelList = response.body();


                    if (!sp.getString("filter_gender", "").isEmpty()) {
                        for (GenderModel gm : genderModelList) {
                            if (sp.getString("filter_gender", "").equalsIgnoreCase(gm.getId())) {
                                if (gm.getName().equalsIgnoreCase("male")) {
                                    gender_a.setBackgroundResource(R.drawable.left_off);
                                    gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_b.setBackgroundResource(R.drawable.mid_on);
                                    gender_b.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));

                                    gender_c.setBackgroundResource(R.drawable.mid_off);
                                    gender_c.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_d.setBackgroundResource(R.drawable.right_off);
                                    gender_d.setTextColor(getActivity().getResources().getColor(R.color.blackColor));
                                }

                                if (gm.getName().equalsIgnoreCase("female")) {
                                    gender_a.setBackgroundResource(R.drawable.left_off);
                                    gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_b.setBackgroundResource(R.drawable.mid_off);
                                    gender_b.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_c.setBackgroundResource(R.drawable.mid_on);
                                    gender_c.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));

                                    gender_d.setBackgroundResource(R.drawable.right_off);
                                    gender_d.setTextColor(getActivity().getResources().getColor(R.color.blackColor));
                                }

                                if (gm.getName().equalsIgnoreCase("fluid")) {
                                    gender_a.setBackgroundResource(R.drawable.left_off);
                                    gender_a.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_b.setBackgroundResource(R.drawable.mid_off);
                                    gender_b.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_c.setBackgroundResource(R.drawable.mid_off);
                                    gender_c.setTextColor(getActivity().getResources().getColor(R.color.blackColor));

                                    gender_d.setBackgroundResource(R.drawable.right_on);
                                    gender_d.setTextColor(getActivity().getResources().getColor(R.color.whiteColor));
                                }
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
//                    showAllert("Network Error", "");
                }
            });
        }
    }


    private void getBodyList() {
        if (WelcomeActivity.bodyModelList == null) {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = service.getBodyType(MainActivity.token);

            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    WelcomeActivity.bodyModelList = response.body();
                    if (getView() != null) {
                        spinner_body = ((Spinner) getView().findViewById(R.id.spinner_Body));
                        if (spinner_body != null) {
                            ArrayList<String> body = getStringListFromModeList(WelcomeActivity.bodyModelList);
                            body.add(0, "none");
                            ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, body);
                            spinner_body.setAdapter(adapter_body);
                            spinner_body.setSelection(sp.getInt("filter_body_pos", 0));
                            spinner_body.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                           int i, long arg3) {
                                    // TODO Auto-generated method stub
                                    if (i > 0) {
                                        sp.edit().putString("filter_body", WelcomeActivity.bodyModelList.get(i - 1).getId()).commit();
                                        sp.edit().putInt("filter_body_pos", i).commit();
                                    } else {
                                        sp.edit().putString("filter_body", "").commit();
                                        sp.edit().putInt("filter_body_pos", 0).commit();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                }
            });
        } else {
            if (getView() != null) {
                spinner_body = ((Spinner) getView().findViewById(R.id.spinner_Body));
                if (spinner_body != null) {
                    ArrayList<String> body = getStringListFromModeList(WelcomeActivity.bodyModelList);
                    body.add(0, "none");
                    ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, body);
                    spinner_body.setAdapter(adapter_body);
                    spinner_body.setSelection(sp.getInt("filter_body_pos", 0));
                    spinner_body.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int i, long arg3) {
                            // TODO Auto-generated method stub
                            if (i > 0) {
                                sp.edit().putString("filter_body", WelcomeActivity.bodyModelList.get(i - 1).getId()).commit();
                                sp.edit().putInt("filter_body_pos", i).commit();
                            } else {
                                sp.edit().putString("filter_body", "").commit();
                                sp.edit().putInt("filter_body_pos", 0).commit();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                }
            }
        }
    }

    Spinner spinner_bust;

    private void getBustList() {
        if (WelcomeActivity.bustModelList == null) {
            NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = service.getBustSize(MainActivity.token);

            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    WelcomeActivity.bustModelList = response.body();
                    if (getView() != null) {
                        spinner_bust = ((Spinner) getView().findViewById(R.id.spinner_Bust));

                        if (spinner_bust != null) {

                            spinner_bust.setVisibility(View.VISIBLE);
                            getView().findViewById(R.id.bust_layout).setVisibility(View.VISIBLE);

                            ArrayList<String> bust = getStringListFromModeList(WelcomeActivity.bustModelList);
                            bust.add(0, "none");
                            ArrayAdapter<String> adapter_bust = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, bust);
                            spinner_bust.setAdapter(adapter_bust);
                            spinner_bust.setSelection(sp.getInt("filter_bust_pos", 0));
                            spinner_bust.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                           int i, long arg3) {
                                    // TODO Auto-generated method stub
                                    if (i > 0) {
                                        sp.edit().putString("filter_bust", WelcomeActivity.bustModelList.get(i - 1).getId()).commit();
                                        sp.edit().putInt("filter_bust_pos", i).commit();
                                    } else {
                                        sp.edit().putString("filter_bust", "").commit();
                                        sp.edit().putInt("filter_bust_pos", 0).commit();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });
                        }

                    }
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                }
            });
        } else {
            if (getView() != null) {

                spinner_bust = ((Spinner) getView().findViewById(R.id.spinner_Bust));
                if (spinner_bust != null) {

                    spinner_bust.setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.bust_layout).setVisibility(View.VISIBLE);

                    ArrayList<String> bust = getStringListFromModeList(WelcomeActivity.bustModelList);
                    bust.add(0, "none");
                    ArrayAdapter<String> adapter_bust = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, bust);
                    spinner_bust.setAdapter(adapter_bust);
                    spinner_bust.setSelection(sp.getInt("filter_bust_pos", 0));
                    spinner_bust.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                   int i, long arg3) {
                            // TODO Auto-generated method stub
                            if (i > 0) {
                                sp.edit().putString("filter_bust", WelcomeActivity.bustModelList.get(i - 1).getId()).commit();
                                sp.edit().putInt("filter_bust_pos", i).commit();
                            } else {
                                sp.edit().putString("filter_bust", "").commit();
                                sp.edit().putInt("filter_bust_pos", 0).commit();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                }

            }
        }
    }


    private String passwordRegex = "^(.{0,7}|[^0-9]*|[^A-Z]*)$";

    private boolean validPassword(final String passWord, final String confPassWord) {
        Pattern pattern = Pattern.compile(passwordRegex);
        return passWord.equalsIgnoreCase(confPassWord) && !pattern.matcher(passWord).matches();
    }

    public void setFragment(PerformerListFragment fragment) {
        this.fragment = fragment;
    }
}
