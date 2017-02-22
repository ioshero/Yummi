package com.almabranding.yummi.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.adapter.PricesArrayAdapter;
import com.almabranding.yummi.adapter.ServiceArrayAdapter;
import com.almabranding.yummi.models.BankModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.PerformerUpdateModel;
import com.almabranding.yummi.models.PriceModel;
import com.almabranding.yummi.models.RegistrationEightModel;
import com.almabranding.yummi.models.RegistrationEightPriceModel;
import com.almabranding.yummi.models.RegistrationEightResponseModel;
import com.almabranding.yummi.models.SecurityContactModel;
import com.almabranding.yummi.models.Service;
import com.almabranding.yummi.models.UpdateMailModel;
import com.almabranding.yummi.models.UpdatePassModel;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;
import com.almabranding.yummi.models.services.ServicePushPriceModel;
import com.almabranding.yummi.models.third.GenderModel;
import com.almabranding.yummi.utils.PictureCroop;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ioshero on 13/05/16.
 */
public class ProfileFragment extends Fragment {


    private String selectedImagePath_one = "";
    private String selectedImagePath_two = "";
    private String selectedImagePath_three = "";
    private String selectedImagePath_four = "";

    String bsbRegex = "^\\d{3}-\\d{3}$";
    String accountRegex = "^\\d{8,10}$";
    String idRegex = "^\\d{8}$";

    private boolean validField(final String fieldText, final String regularExpression) {
        Pattern pattern = Pattern.compile(regularExpression);
        return pattern.matcher(fieldText).matches();
    }

    private void setUpActionBar() {
        ((MainActivity) getActivity()).setNone();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.getCustomView().findViewById(R.id.actionbar_titleimageview).setVisibility(View.INVISIBLE);
        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_titleview);
        title.setVisibility(View.VISIBLE);
        title.setText("Profile");

        actionBar.getCustomView().findViewById(R.id.drawer_textview_done).setVisibility(View.INVISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_imageview_done).setVisibility(View.INVISIBLE);

        actionBar.getCustomView().findViewById(R.id.drawer_imageview).setVisibility(View.VISIBLE);
        actionBar.getCustomView().findViewById(R.id.drawer_textview).setVisibility(View.INVISIBLE);

        final TextView update = (TextView) actionBar.getCustomView().findViewById(R.id.drawer_textview_done);
        update.setVisibility(View.VISIBLE);
        update.setText("UPDATE");
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ((MainActivity) getActivity()).addOverlay();
                update.setClickable(false);
                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

                Call<ResponseBody> call = null;
                if (getActivity() != null)
                    if (YummiUtils.isPreformer(getActivity())) {
                        if (securityNumber.getText().toString().length() <8)
                            MainActivity.showAllert("Error", "Emergency call number needs to be at least 8 character long");

                        if (getView().findViewById(R.id.bust_layout).getVisibility() == View.GONE) {
                            call = service.updatePerformer(MainActivity.userId, MainActivity.token, new PerformerUpdateModel(stagename.getText().toString(),
                                    WelcomeActivity.hairModelList.get(spinner_hair.getSelectedItemPosition()).getId(),
                                    WelcomeActivity.bodyModelList.get(spinner_body.getSelectedItemPosition()).getId(),
                                    WelcomeActivity.bustModelList.get(0).getId(), securityNumber.getText().toString()));
                        } else
                            call = service.updatePerformer(MainActivity.userId, MainActivity.token, new PerformerUpdateModel(stagename.getText().toString(),
                                    WelcomeActivity.hairModelList.get(spinner_hair.getSelectedItemPosition()).getId(),
                                    WelcomeActivity.bodyModelList.get(spinner_body.getSelectedItemPosition()).getId(),
                                    WelcomeActivity.bustModelList.get(spinner_bust.getSelectedItemPosition()).getId(), securityNumber.getText().toString()));

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                MainActivity.showAllert("Success", "");
                                getActualPerformer();
                                update.setClickable(true);
                                ((MainActivity) getActivity()).removeView();

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                update.setClickable(true);
                                ((MainActivity) getActivity()).removeView();
                            }
                        });
                    } else {
                        if (securityNumber.getText().toString().length() <8){
                            MainActivity.showAllert("Error", "Emergency call number needs to be at least 8 character long");
                        }else {

                            call = service.postSecurityContact(YummiUtils.getUserId(getActivity()), new SecurityContactModel(securityNumber.getText().toString()), YummiUtils.getAccesToken(getActivity()));
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    MainActivity.showAllert("Success", "");
                                    YummiUtils.client.setSecurityNumber(securityNumber.getText().toString());
                                    update.setClickable(true);
                                    ((MainActivity) getActivity()).removeView();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    update.setClickable(true);
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


    private EditText securityNumber;
    private EditText stagename;
    private ListView mDrawerList;


    private LinearLayout mDrawerLinear;
    private LinearLayout mDrawerPassLinear;
    private LinearLayout mDrawerBankLinear;


    private DrawerLayout mDrawerLayout;

    public void setPerformer(PerformerModel performer) {
        if (!securityNumber.getText().toString().isEmpty())
            performer.setSecurityNumber(securityNumber.getText().toString());
        YummiUtils.performer = performer;

        if (stagename != null) {
            stagename.setText(performer.getName());
        }

        if (securityNumber != null)
            securityNumber.setText(performer.getSecurityNumber());

        getBodyList();
        getBustList();
        getHairList();

        for (Service serv : performer.getServices()) {
            for (ServicePushModel s : WelcomeActivity.services) {
                if (s.getServiceId().equalsIgnoreCase(serv.getService().getId())) {
                    s.setAvailability(serv.getAvailability());
                }
            }
        }

        if (WelcomeActivity.prices.isEmpty()) {
            for (PriceModel price : performer.getPrices())
                WelcomeActivity.prices.add(new ServicePushPriceModel(price.getAmount(), price.getServiceTypeId()));

            if (WelcomeActivity.prices.isEmpty())
                for (ServiceModel sm : ((MainActivity) getActivity()).headers) {
                    WelcomeActivity.prices.add(new ServicePushPriceModel(1, sm.getId()));
                }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (YummiUtils.performer == null) {
            getActualPerformer();
        }
    }

    File file1;
    File file2;
    File file3;
    File file4;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WelcomeActivity.SELECT_IMAGE_FIRST) {
            if (resultCode == WelcomeActivity.RESULT_OK) {
                if (data != null) {


//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    try {


                        selectedImagePath_one = getRealpath(data);
                        if (selectedImagePath_one == null) {
                            Uri selectedImageUri = data.getData();
                            selectedImagePath_one = getPath(selectedImageUri);
                        }
//                        PictureCroop.doCrop(data.getData(), this, PictureCroop.CROP_FROM_CAMERA1);
//                        ((ImageButton) findViewById(R.id.button_one)).setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (selectedImagePath_one == null)
                            selectedImagePath_one = "";
                        MainActivity.showAllert("Error", "This gallery is not supported, please use other.");
                    }


                } else if (resultCode == WelcomeActivity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_FIRST) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                ((ImageButton) getView().findViewById(R.id.button_one)).setImageBitmap(yourSelectedImage);

                file1 = new File(selectedImagePath_one);
                FileOutputStream fOut = new FileOutputStream(file1);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_SECOND) {
            if (resultCode == WelcomeActivity.RESULT_OK) {
                if (data != null) {


//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    try {

                        selectedImagePath_two = getRealpath(data);
                        if (selectedImagePath_two == null) {
                            Uri selectedImageUri = data.getData();
                            selectedImagePath_two = getPath(selectedImageUri);
                        }

//                        PictureCroop.doCrop(data.getData(), this, PictureCroop.CROP_FROM_CAMERA2);
//                      ((ImageButton) findViewById(R.id.button_two)).setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (selectedImagePath_two == null)
                            selectedImagePath_two = "";
                        MainActivity.showAllert("Error", "This gallery is not supported, please use other.");
                    }

                } else if (resultCode == WelcomeActivity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_SECOND) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                ((ImageButton) getView().findViewById(R.id.button_two)).setImageBitmap(yourSelectedImage);

                file2 = new File(selectedImagePath_two);
                FileOutputStream fOut = new FileOutputStream(file2);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_THIRD) {
            if (resultCode == WelcomeActivity.RESULT_OK) {
                if (data != null) {


//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    try {
                        selectedImagePath_three = getRealpath(data);
                        if (selectedImagePath_three == null) {
                            Uri selectedImageUri = data.getData();
                            selectedImagePath_three = getPath(selectedImageUri);
                        }

//                        PictureCroop.doCrop(data.getData(), this, PictureCroop.CROP_FROM_CAMERA3);
//                      ((ImageButton) findViewById(R.id.button_three)).setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (selectedImagePath_three == null)
                            selectedImagePath_three = "";
                        MainActivity.showAllert("Error", "This gallery is not supported, please use other.");
                    }
                } else if (resultCode == WelcomeActivity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_THIRD) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                ((ImageButton) getView().findViewById(R.id.button_three)).setImageBitmap(yourSelectedImage);

                file3 = new File(selectedImagePath_three);
                FileOutputStream fOut = new FileOutputStream(file3);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_FOURTH) {
            if (resultCode == WelcomeActivity.RESULT_OK) {
                if (data != null) {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    try {
                        selectedImagePath_four = getRealpath(data);
                        if (selectedImagePath_four == null) {
                            Uri selectedImageUri = data.getData();
                            selectedImagePath_four = getPath(selectedImageUri);
                        }

//                        PictureCroop.doCrop(data.getData(), this, PictureCroop.CROP_FROM_CAMERA4);
//                      ((ImageButton) findViewById(R.id.button_four)).setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (selectedImagePath_four == null)
                            selectedImagePath_four = "";
                        MainActivity.showAllert("Error", "This gallery is not supported, please use other.");
                    }

                } else if (resultCode == WelcomeActivity.RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == WelcomeActivity.SELECT_IMAGE_FOURTH) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                ((ImageButton) getView().findViewById(R.id.button_four)).setImageBitmap(yourSelectedImage);

                file4 = new File(selectedImagePath_four);
                FileOutputStream fOut = new FileOutputStream(file4);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (!isUploading) {
            if (file1 != null) {
                uploadImage(file1, "1");
            } else if (file2 != null) {
                uploadImage(file2, "2");
            } else if (file3 != null) {
                uploadImage(file3, "3");
            } else if (file4 != null) {
                uploadImage(file4, "4");
            }
        }

    }

    boolean isOpened = false;
    private String emailRegex = "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)";

    public boolean isService() {
        return isService && isOpened;
    }

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setHasOptionsMenu(true);
        if (!YummiUtils.isPreformer(getActivity())) {
            final View view = inflater.inflate(R.layout.profile_fragment_client, container, false);
            mDrawerList = (ListView) view.findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
            mDrawerLinear = (LinearLayout) view.findViewById(R.id.triple_choice_layout_c);
            mDrawerPassLinear = (LinearLayout) view.findViewById(R.id.triple_choice_layout_cd);

            view.findViewById(R.id.choice_password_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerPassLinear.setVisibility(View.VISIBLE);
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.GONE);
                    isOpened = false;

                    final EditText current = (EditText) getView().findViewById(R.id.current_password);
                    final EditText pass = (EditText) getView().findViewById(R.id.new_password);
                    final EditText pass_conf = (EditText) getView().findViewById(R.id.confirm_password);
                    final Button submmit_button = (Button) getView().findViewById(R.id.reg_sec_button);


                    final TextView tcurrent = (TextView) getView().findViewById(R.id.current_passwordt);
                    final TextView tpass = (TextView) getView().findViewById(R.id.new_passwordt);
                    final TextView tpass_conf = (TextView) getView().findViewById(R.id.confirm_passwordt);

                    current.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());


                    pass.setText("");
                    current.setText("");
                    pass_conf.setText("");

                    tcurrent.setText("Current Password");

                    tpass.setText("New Password");

                    tpass_conf.setText("New Password Confirmation");

                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (!current.getText().toString().isEmpty() &&
                                    validPassword(pass.getText().toString(), pass_conf.getText().toString())) {

                                if (YummiUtils.isPreformer(getActivity())) {
                                    submmit_button.setBackgroundResource(R.drawable.main_gold_button);
                                } else {
                                    submmit_button.setBackgroundResource(R.drawable.main_black_button);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    };

                    current.addTextChangedListener(watcher);
                    pass.addTextChangedListener(watcher);
                    pass_conf.addTextChangedListener(watcher);


                    submmit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).addOverlay();
                            if (!current.getText().toString().isEmpty() &&
                                    validPassword(pass.getText().toString(), pass_conf.getText().toString())) {

                                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                                Call<ResponseBody> call = null;


                                if (YummiUtils.isPreformer(getActivity())) {
                                    call = service.updatePasswordPassword(MainActivity.userId, MainActivity.token, new UpdatePassModel(current.getText().toString(), pass.getText().toString()));
                                } else {
                                    call = service.updatePasswordClient(MainActivity.userId, MainActivity.token, new UpdatePassModel(current.getText().toString(), pass.getText().toString()));
                                }

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.body() != null) {
                                            MainActivity.showAllert("Succes", "Your password has been changed");
                                        } else {
                                            MainActivity.showResponseError(response.errorBody().toString());
                                        }

                                        ((MainActivity) getActivity()).removeView();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        ((MainActivity) getActivity()).removeView();
                                    }
                                });


                            } else {
                                MainActivity.showAllert("Error", "Please correct the passwords");
                                ((MainActivity) getActivity()).removeView();
                            }
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }

            });


            view.findViewById(R.id.choice_email_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mDrawerPassLinear.setVisibility(View.VISIBLE);
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.GONE);
                    isOpened = false;

                    final EditText current = (EditText) getView().findViewById(R.id.current_password);
                    final EditText pass = (EditText) getView().findViewById(R.id.new_password);
                    final EditText pass_conf = (EditText) getView().findViewById(R.id.confirm_password);

                    final TextView tcurrent = (TextView) getView().findViewById(R.id.current_passwordt);
                    final TextView tpass = (TextView) getView().findViewById(R.id.new_passwordt);
                    final TextView tpass_conf = (TextView) getView().findViewById(R.id.confirm_passwordt);

                    current.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    tcurrent.setText("New Email");

                    tpass.setText("New Email Confirmation");

                    tpass_conf.setText("Password");


                    pass.setText("");
                    current.setText("");
                    pass_conf.setText("");


                    final Button submmit_button = (Button) getView().findViewById(R.id.reg_sec_button);

                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (!current.getText().toString().isEmpty() && validField(current.getText().toString(), emailRegex) && current.getText().toString().equalsIgnoreCase(pass.getText().toString()) && pass_conf.getText().toString().length() > 7) {

                                if (YummiUtils.isPreformer(getActivity())) {
                                    submmit_button.setBackgroundResource(R.drawable.main_gold_button);
                                } else {
                                    submmit_button.setBackgroundResource(R.drawable.main_black_button);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    };

                    current.addTextChangedListener(watcher);
                    pass.addTextChangedListener(watcher);
                    pass_conf.addTextChangedListener(watcher);


                    submmit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).addOverlay();

                            if (!current.getText().toString().isEmpty() && validField(current.getText().toString(), emailRegex) && current.getText().toString().equalsIgnoreCase(pass.getText().toString()) && pass_conf.getText().toString().length() > 7) {

                                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                                Call<ResponseBody> call = null;


                                if (YummiUtils.isPreformer(getActivity())) {
                                    call = service.updatePerEmail(MainActivity.userId, MainActivity.token, new UpdateMailModel(current.getText().toString(), pass_conf.getText().toString()));
                                } else {
                                    call = service.updateEmail(MainActivity.userId, MainActivity.token, new UpdateMailModel(current.getText().toString(), pass_conf.getText().toString()));
                                }

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.body() != null) {
                                            MainActivity.showAllert("Succes", "Your email has been changed");
                                        } else {
                                            MainActivity.showResponseError(response.errorBody().toString());
                                        }

                                        ((MainActivity) getActivity()).removeView();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        ((MainActivity) getActivity()).removeView();
                                    }
                                });


                            } else {
                                MainActivity.showAllert("Error", "Please correct the fields");
                                ((MainActivity) getActivity()).removeView();
                            }
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }

            });

            securityNumber = (EditText) view.findViewById(R.id.security_number_edit);
            if (securityNumber != null)
                securityNumber.setText(YummiUtils.client.getSecurityNumber());

            return view;


        } else {

            final View view = inflater.inflate(R.layout.profile_fragment, container, false);


            stagename = (EditText) view.findViewById(R.id.editTextStageName);
            securityNumber = (EditText) view.findViewById(R.id.security_number_edit);
            mDrawerList = (ListView) view.findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
            mDrawerLinear = (LinearLayout) view.findViewById(R.id.triple_choice_layout_c);
            mDrawerPassLinear = (LinearLayout) view.findViewById(R.id.triple_choice_layout_cd);
            mDrawerBankLinear = (LinearLayout) view.findViewById(R.id.triple_choice_layout_banks);


            Toolbar tb = new Toolbar(getActivity());
            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                    tb, R.string.drawer_open, R.string.drawer_close) {

                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    // Do whatever you want here

                    NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                    if (isService()) {
                        boolean hasAtLeastService = false;
                        for (ServicePushModel spm : WelcomeActivity.services) {
                            if (spm.getAvailability() == 2)
                                hasAtLeastService = true;
                        }
                        if (hasAtLeastService) {
                            RegistrationEightModel model = new RegistrationEightModel(WelcomeActivity.services);
                            Call<ResponseBody> c = service.registrationServices(MainActivity.userId, model, MainActivity.token);

                            c.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    WelcomeActivity.serviceListDO = null;
                                    getActualPerformer();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        } else {
                            MainActivity.showAllert("Service Error", "Please set at least one service to 'Yes' value");
                        }
                    } else {
                        if (isOpened) {
                            RegistrationEightPriceModel m = new RegistrationEightPriceModel(WelcomeActivity.prices, YummiUtils.imagePrice, YummiUtils.chatPrice);

                            Call<ResponseBody> ct = service.registrationPrices(MainActivity.userId, m, MainActivity.token);

                            ct.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    WelcomeActivity.prices.clear();
                                    getActualPerformer();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        }
                    }

                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // Do whatever you want here
                }
            };

            mDrawerLayout.addDrawerListener(mDrawerToggle);


            view.findViewById(R.id.choice_password_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerPassLinear.setVisibility(View.VISIBLE);
                    mDrawerBankLinear.setVisibility(View.GONE);
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.GONE);
                    isOpened = false;

                    final EditText current = (EditText) getView().findViewById(R.id.current_password);
                    final EditText pass = (EditText) getView().findViewById(R.id.new_password);
                    final EditText pass_conf = (EditText) getView().findViewById(R.id.confirm_password);
                    final Button submmit_button = (Button) getView().findViewById(R.id.reg_sec_button);


                    final TextView tcurrent = (TextView) getView().findViewById(R.id.current_passwordt);
                    final TextView tpass = (TextView) getView().findViewById(R.id.new_passwordt);
                    final TextView tpass_conf = (TextView) getView().findViewById(R.id.confirm_passwordt);

                    current.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());


                    pass.setText("");
                    current.setText("");
                    pass_conf.setText("");
                    tcurrent.setText("Current Password");

                    tpass.setText("New Password");

                    tpass_conf.setText("New Password Confirmation");

                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (!current.getText().toString().isEmpty() &&
                                    validPassword(pass.getText().toString(), pass_conf.getText().toString())) {

                                if (YummiUtils.isPreformer(getActivity())) {
                                    submmit_button.setBackgroundResource(R.drawable.main_gold_button);
                                } else {
                                    submmit_button.setBackgroundResource(R.drawable.main_black_button);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    };

                    current.addTextChangedListener(watcher);
                    pass.addTextChangedListener(watcher);
                    pass_conf.addTextChangedListener(watcher);


                    submmit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).addOverlay();
                            if (!current.getText().toString().isEmpty() &&
                                    validPassword(pass.getText().toString(), pass_conf.getText().toString())) {

                                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                                Call<ResponseBody> call = null;


                                if (YummiUtils.isPreformer(getActivity())) {
                                    call = service.updatePasswordPassword(MainActivity.userId, MainActivity.token, new UpdatePassModel(current.getText().toString(), pass.getText().toString()));
                                } else {
                                    call = service.updatePasswordClient(MainActivity.userId, MainActivity.token, new UpdatePassModel(current.getText().toString(), pass.getText().toString()));
                                }

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.body() != null) {
                                            MainActivity.showAllert("Succes", "Your password has been changed");
                                        } else {
                                            MainActivity.showResponseError(response.errorBody().toString());
                                        }

                                        ((MainActivity) getActivity()).removeView();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        ((MainActivity) getActivity()).removeView();
                                    }
                                });


                            } else {
                                MainActivity.showAllert("Error", "Please correct the passwords");
                                ((MainActivity) getActivity()).removeView();
                            }
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }

            });


            view.findViewById(R.id.choice_email_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerPassLinear.setVisibility(View.VISIBLE);
                    mDrawerBankLinear.setVisibility(View.GONE);
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.GONE);
                    isOpened = false;

                    final EditText current = (EditText) getView().findViewById(R.id.current_password);
                    final EditText pass = (EditText) getView().findViewById(R.id.new_password);
                    final EditText pass_conf = (EditText) getView().findViewById(R.id.confirm_password);

                    final TextView tcurrent = (TextView) getView().findViewById(R.id.current_passwordt);
                    final TextView tpass = (TextView) getView().findViewById(R.id.new_passwordt);
                    final TextView tpass_conf = (TextView) getView().findViewById(R.id.confirm_passwordt);

                    current.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    tcurrent.setText("New Email");

                    tpass.setText("New Email Confirmation");

                    tpass_conf.setText("Password");

                    pass.setText("");
                    current.setText("");
                    pass_conf.setText("");


                    final Button submmit_button = (Button) getView().findViewById(R.id.reg_sec_button);

                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (!current.getText().toString().isEmpty() && validField(current.getText().toString(), emailRegex) && current.getText().toString().equalsIgnoreCase(pass.getText().toString()) && pass_conf.getText().toString().length() > 7) {

                                if (YummiUtils.isPreformer(getActivity())) {
                                    submmit_button.setBackgroundResource(R.drawable.main_gold_button);
                                } else {
                                    submmit_button.setBackgroundResource(R.drawable.main_black_button);
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    };

                    current.addTextChangedListener(watcher);
                    pass.addTextChangedListener(watcher);
                    pass_conf.addTextChangedListener(watcher);


                    submmit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity) getActivity()).addOverlay();

                            if (!current.getText().toString().isEmpty() && validField(current.getText().toString(), emailRegex) && current.getText().toString().equalsIgnoreCase(pass.getText().toString()) && pass_conf.getText().toString().length() > 7) {

                                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
                                Call<ResponseBody> call = null;


                                if (YummiUtils.isPreformer(getActivity())) {
                                    call = service.updatePerEmail(MainActivity.userId, MainActivity.token, new UpdateMailModel(current.getText().toString(), pass_conf.getText().toString()));
                                } else {
                                    call = service.updateEmail(MainActivity.userId, MainActivity.token, new UpdateMailModel(current.getText().toString(), pass_conf.getText().toString()));
                                }

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.body() != null) {
                                            MainActivity.showAllert("Succes", "Your email has been changed");
                                        } else {
                                            MainActivity.showResponseError(response.errorBody().toString());
                                        }

                                        ((MainActivity) getActivity()).removeView();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        ((MainActivity) getActivity()).removeView();
                                    }
                                });


                            } else {
                                MainActivity.showAllert("Error", "Please correct the fields");
                                ((MainActivity) getActivity()).removeView();
                            }
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }

            });

            view.findViewById(R.id.choice_bank_account).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerBankLinear.setVisibility(View.VISIBLE);
                    mDrawerPassLinear.setVisibility(View.GONE);
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.GONE);
                    isOpened = false;

                    final EditText idNumber = (EditText) getView().findViewById(R.id.reg_sec_stagename);
                    final EditText bsb = (EditText) getView().findViewById(R.id.reg_sec_realname);
                    final EditText account_number = (EditText) getView().findViewById(R.id.reg_sec_acount_number);

                    if (YummiUtils.bankModel != null) {
                        idNumber.setText(YummiUtils.bankModel.getOwnerIdNumber());
                        bsb.setText(YummiUtils.bankModel.getBSB());
                        account_number.setText(YummiUtils.bankModel.getAccountNumber());
                    }

                    TextWatcher textw = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (validField(bsb.getText().toString(), bsbRegex) && validField(idNumber.getText().toString(), idRegex) && validField(account_number.getText().toString(), accountRegex)) {
                                (getView().findViewById(R.id.reg_sec_button_banks)).setBackgroundResource(R.drawable.main_gold_button);
                            } else {
                                (getView().findViewById(R.id.reg_sec_button_banks)).setBackgroundResource(R.drawable.main_grey_button);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (bsb.getText().toString().length() == 3) {
                                bsb.append("-");

                                if (!bsb.getText().toString().contains("-"))
                                    bsb.getText().append("-");

                                if (!bsb.getText().toString().contains("-"))
                                    bsb.setText(bsb.getText() + "-");

                                int position = bsb.length();
                                bsb.setSelection(position);
                            }
                        }
                    };


                    account_number.addTextChangedListener(textw);
                    idNumber.addTextChangedListener(textw);
                    bsb.addTextChangedListener(textw);


                    getView().findViewById(R.id.reg_sec_button_banks).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (validField(bsb.getText().toString(), bsbRegex) && validField(idNumber.getText().toString(), idRegex) && validField(account_number.getText().toString(), accountRegex)) {
                                ((MainActivity) getActivity()).addOverlay();
                                NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);

                                Call<BankModel> call;
                                if (YummiUtils.bankModel == null)
                                    call = service.postBankAccount(MainActivity.userId, new BankModel(bsb.getText().toString().substring(0, 3), account_number.getText().toString(), idNumber.getText().toString(), bsb.getText().toString().substring(4, 7)), MainActivity.token);
                                else
                                    call = service.updateBankAccount(MainActivity.userId, MainActivity.token, new BankModel(bsb.getText().toString().substring(0, 3), account_number.getText().toString(), idNumber.getText().toString(), bsb.getText().toString().substring(4, 7)));

                                call.enqueue(new Callback<BankModel>() {
                                    @Override
                                    public void onResponse(Call<BankModel> call, Response<BankModel> response) {
                                        if (response.body() != null) {
                                            ((MainActivity) getActivity()).removeView();
                                            if (YummiUtils.bankModel != null) {
                                                YummiUtils.bankModel.setAccountNumber(account_number.getText().toString());
                                                YummiUtils.bankModel.setBankNumber(bsb.getText().toString().substring(0, 3));
                                                YummiUtils.bankModel.setBankOfficeNumber(bsb.getText().toString().substring(4, 7));
                                                YummiUtils.bankModel.setOwnerIdNumber(idNumber.getText().toString());
                                            } else {
                                                YummiUtils.bankModel = new BankModel(bsb.getText().toString().substring(0, 3), account_number.getText().toString(), idNumber.getText().toString(), bsb.getText().toString().substring(4, 7));
                                            }

                                            ((MainActivity) getActivity()).showAllert("Succes", "Your bank account was changed");
                                        } else {
                                            ((MainActivity) getActivity()).removeView();

                                            ((MainActivity) getActivity()).showAllert("Error", "Error during registration");
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<BankModel> call, Throwable t) {
                                        ((MainActivity) getActivity()).removeView();
                                        ((MainActivity) getActivity()).showAllert("Network Error", "Could not connect to the server");
                                    }
                                });

                            } else {
                                ((MainActivity) getActivity()).showAllert("Error", "Please fill all the fields");
                            }
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }

                }

            });


            view.findViewById(R.id.t_choice_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isOpened = false;
                    mDrawerLinear.setVisibility(View.VISIBLE);
                    mDrawerList.setVisibility(View.GONE);
                    mDrawerPassLinear.setVisibility(View.GONE);
                    mDrawerBankLinear.setVisibility(View.GONE);
                    try {
                        if (selectedImagePath_one.isEmpty()) {
                            Picasso.with(getContext()).load(YummiUtils.performer.getImages().get(0).getFirstImagePath()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.button_one)));
                            selectedImagePath_one = YummiUtils.performer.getImages().get(0).getFirstImagePath();
                        }

                        if (selectedImagePath_two.isEmpty()) {
                            Picasso.with(getContext()).load(YummiUtils.performer.getImages().get(1).getFirstImagePath()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.button_two)));
                            selectedImagePath_two = YummiUtils.performer.getImages().get(1).getFirstImagePath();
                        }

                        if (selectedImagePath_three.isEmpty()) {
                            Picasso.with(getContext()).load(YummiUtils.performer.getImages().get(2).getFirstImagePath()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.button_three)));
                            selectedImagePath_three = YummiUtils.performer.getImages().get(2).getFirstImagePath();
                        }


                        if (selectedImagePath_four.isEmpty()) {
                            Picasso.with(getContext()).load(YummiUtils.performer.getImages().get(3).getFirstImagePath()).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1024, 768).centerCrop().into(((ImageView) getView().findViewById(R.id.button_four)));
                            selectedImagePath_four = YummiUtils.performer.getImages().get(2).getFirstImagePath();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    ImageButton one = ((ImageButton) getView().findViewById(R.id.button_one));
                    ImageButton two = ((ImageButton) getView().findViewById(R.id.button_two));
                    ImageButton three = ((ImageButton) getView().findViewById(R.id.button_three));
                    ImageButton four = ((ImageButton) getView().findViewById(R.id.button_four));

                    File file1 = null;
                    File file2 = null;
                    File file3 = null;
                    File file4 = null;


                    one.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra("scale", true);
                            intent.putExtra("outputX", 1000);
                            startActivityForResult(intent, WelcomeActivity.SELECT_IMAGE_FIRST);
                        }
                    });

                    two.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra("scale", true);
                            intent.putExtra("outputX", 1000);
                            startActivityForResult(intent, WelcomeActivity.SELECT_IMAGE_SECOND);
                        }
                    });
                    three.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra("scale", true);
                            intent.putExtra("outputX", 1000);
                            startActivityForResult(intent, WelcomeActivity.SELECT_IMAGE_THIRD);
                        }
                    });
                    four.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra("scale", true);
                            intent.putExtra("outputX", 1000);
                            startActivityForResult(intent, WelcomeActivity.SELECT_IMAGE_FOURTH);
                        }
                    });


                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        // close
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    } else {
                        // open
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                }
            });

            view.findViewById(R.id.triple_choice_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.VISIBLE);
                    mDrawerPassLinear.setVisibility(View.GONE);
                    mDrawerBankLinear.setVisibility(View.GONE);
                    isOpened = true;
                    isService = true;
                    ServiceArrayAdapter adapter = new ServiceArrayAdapter(getActivity(), getStringFromServiceList());
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


            view.findViewById(R.id.tr_choice_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mDrawerLinear.setVisibility(View.GONE);
                    mDrawerPassLinear.setVisibility(View.GONE);
                    mDrawerBankLinear.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.VISIBLE);
                    isOpened = true;
                    isService = false;
                    if (WelcomeActivity.prices.isEmpty()) {
                        for (PriceModel price : YummiUtils.performer.getPrices())
                            WelcomeActivity.prices.add(new ServicePushPriceModel(price.getAmount(), price.getServiceTypeId()));

                        if (WelcomeActivity.prices.isEmpty())
                            for (ServiceModel sm : ((MainActivity) getActivity()).headers) {
                                WelcomeActivity.prices.add(new ServicePushPriceModel(0, sm.getId()));
                            }
                    }

                    ArrayList<ServiceModel> arr = new ArrayList<ServiceModel>(((MainActivity) getActivity()).headers);

                    arr.add(arr.size(), new ServiceModel("Image Request", "68"));
                    arr.add(arr.size(), new ServiceModel("Chat Request", "66"));

                    YummiUtils.imagePrice = YummiUtils.performer.getImagePrice();
                    YummiUtils.chatPrice = YummiUtils.performer.getChatPrice();

                    PricesArrayAdapter adapter = new PricesArrayAdapter(getActivity(), arr);


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

            return view;
        }
    }

    boolean isService = true;

    private void getActualPerformer() {
        NetworkCallApiInterface service = MainActivity.retrofit.create(NetworkCallApiInterface.class);
        Call<PerformerModel> call = service.getPerformer(MainActivity.userId, MainActivity.token);

        call.enqueue(new Callback<PerformerModel>() {
            @Override
            public void onResponse(Call<PerformerModel> call, Response<PerformerModel> response) {

                if (response.body() != null) {
                    setPerformer(response.body());
                    if (WelcomeActivity.serviceListDO == null) {
                        getServiceList();
                    }
                }
            }

            @Override
            public void onFailure(Call<PerformerModel> call, Throwable t) {
                ((MainActivity) getActivity()).removeView();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (YummiUtils.isPreformer(getActivity()))
            YummiUtils.verifyStoragePermissions(getActivity());

        setUpActionBar();
        if (YummiUtils.performer == null)
            getActualPerformer();
        else {
            setPerformer(YummiUtils.performer);
        }


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
                            ArrayAdapter<String> adapter_hair = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, hair);
                            spinner_hair.setAdapter(adapter_hair);
                            int i = -1;
                            int pos = 0;
                            for (GenderModel gm : WelcomeActivity.hairModelList) {
                                i++;
                                if (gm.getId().equalsIgnoreCase(YummiUtils.performer.gethairColor().getId())) {
                                    pos = i;
                                }
                            }
                            spinner_hair.setSelection(pos);
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
                    ArrayAdapter<String> adapter_hair = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, hair);
                    spinner_hair.setAdapter(adapter_hair);
                    int i = -1;
                    int pos = 0;
                    for (GenderModel gm : WelcomeActivity.hairModelList) {
                        i++;
                        if (gm.getId().equalsIgnoreCase(YummiUtils.performer.gethairColor().getId())) {
                            pos = i;
                        }
                    }
                    spinner_hair.setSelection(pos);
                }
            }
        }
    }

    Spinner spinner_body;

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
                            ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item, body);
                            spinner_body.setAdapter(adapter_body);
                            int i = -1;
                            int pos = 0;
                            for (GenderModel gm : WelcomeActivity.bodyModelList) {
                                i++;
                                if (gm.getId().equalsIgnoreCase(YummiUtils.performer.getbodyType().getId())) {
                                    pos = i;
                                }
                            }
                            spinner_body.setSelection(pos);
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
                    ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_dropdown_item, body);
                    spinner_body.setAdapter(adapter_body);
                    int i = -1;
                    int pos = 0;
                    for (GenderModel gm : WelcomeActivity.bodyModelList) {
                        i++;
                        if (gm != null)
                            if (gm.getId().equalsIgnoreCase(YummiUtils.performer.getbodyType().getId())) {
                                pos = i;
                            }
                    }
                    spinner_body.setSelection(pos);
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

                        if (spinner_bust != null)
                            if (YummiUtils.performer.getGender().getName().equalsIgnoreCase("male")) {
                                spinner_bust.setVisibility(View.GONE);
                                getView().findViewById(R.id.bust_layout).setVisibility(View.GONE);
                            } else {
                                spinner_bust.setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.bust_layout).setVisibility(View.VISIBLE);

                                ArrayList<String> bust = getStringListFromModeList(WelcomeActivity.bustModelList);
                                ArrayAdapter<String> adapter_bust = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, bust);
                                spinner_bust.setAdapter(adapter_bust);

                                int i = -1;
                                int pos = 0;
                                for (GenderModel gm : WelcomeActivity.bustModelList) {
                                    i++;
                                    if (gm.getId().equalsIgnoreCase(YummiUtils.performer.getBustSize().getId())) {
                                        pos = i;
                                    }
                                }
                                spinner_bust.setSelection(pos);
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
                if (spinner_bust != null)
                    if (YummiUtils.performer.getGender().getName().equalsIgnoreCase("male")) {
                        spinner_bust.setVisibility(View.GONE);
                        getView().findViewById(R.id.bust_layout).setVisibility(View.GONE);
                    } else {
                        spinner_bust.setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.bust_layout).setVisibility(View.VISIBLE);

                        ArrayList<String> bust = getStringListFromModeList(WelcomeActivity.bustModelList);
                        ArrayAdapter<String> adapter_bust = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_dropdown_item, bust);
                        spinner_bust.setAdapter(adapter_bust);

                        int i = -1;
                        int pos = 0;
                        for (GenderModel gm : WelcomeActivity.bustModelList) {
                            i++;
                            if (gm.getId().equalsIgnoreCase(YummiUtils.performer.getBustSize().getId())) {
                                pos = i;
                            }
                        }
                        spinner_bust.setSelection(pos);
                    }
            }
        }
    }

    private void getServiceList() {

        NetworkCallApiInterface service = ((MainActivity) getActivity()).retrofit.create(NetworkCallApiInterface.class);

        Call<List<RegistrationEightResponseModel>> call = service.getServiceListByGender(YummiUtils.performer.getGenderId(), MainActivity.token);

        call.enqueue(new Callback<List<RegistrationEightResponseModel>>() {
            @Override
            public void onResponse(Call<List<RegistrationEightResponseModel>> call, Response<List<RegistrationEightResponseModel>> response) {
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


                getStringFromServiceList();

                ((MainActivity) getActivity()).removeView();
            }

            @Override
            public void onFailure(Call<List<RegistrationEightResponseModel>> call, Throwable t) {
                call.toString();
                ((MainActivity) getActivity()).removeView();
            }
        });
    }


    private ArrayList<ServiceModel> getStringFromServiceList() {
        ArrayList<ServiceModel> result = new ArrayList<ServiceModel>();
        if (WelcomeActivity.serviceListDO != null)
            for (RegistrationEightResponseModel g : WelcomeActivity.serviceListDO) {
                result.add(new ServiceModel(g.getName(), ""));
                for (ServiceModel m : g.getServiceList())
                    result.add(m);
            }
        return result;
    }


    boolean isUploading = false;

    private void uploadImage(final File file, final String order) {
        NetworkCallApiInterface service = ((MainActivity) getActivity()).retrofit.create(NetworkCallApiInterface.class);


        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", String.valueOf((new Date()).getTime()) + "_" + file.getName(), requestFile);

        Call<ResponseBody> call = service.upload(((MainActivity) getActivity()).userId,
//                MultipartBody.Part.createFormData("id", ((MainActivity) getActivity()).userId),
                MultipartBody.Part.createFormData("order", order),
                body, MainActivity.token);
        isUploading = true;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                response.body();
                isUploading = false;

                switch (order) {
                    case "1": {
                        file1 = null;
                        if (file2 != null) {
                            uploadImage(file2, "2");
                        } else if (file3 != null) {
                            uploadImage(file3, "3");
                        } else if (file4 != null) {
                            uploadImage(file4, "4");
                        }
                        break;
                    }
                    case "2": {
                        file2 = null;
                        if (file1 != null) {
                            uploadImage(file1, "1");
                        } else if (file3 != null) {
                            uploadImage(file3, "3");
                        } else if (file4 != null) {
                            uploadImage(file4, "4");
                        }
                        break;
                    }
                    case "3": {
                        file3 = null;
                        if (file2 != null) {
                            uploadImage(file2, "2");
                        } else if (file1 != null) {
                            uploadImage(file1, "1");
                        } else if (file4 != null) {
                            uploadImage(file4, "4");
                        }
                        break;
                    }
                    case "4": {
                        file4 = null;
                        if (file2 != null) {
                            uploadImage(file2, "2");
                        } else if (file3 != null) {
                            uploadImage(file3, "3");
                        } else if (file1 != null) {
                            uploadImage(file1, "1");
                        }
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isUploading = false;

            }
        });
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


    private String passwordRegex = "^(.{0,7}|[^0-9]*|[^A-Z]*)$";

    private boolean validPassword(final String passWord, final String confPassWord) {
        Pattern pattern = Pattern.compile(passwordRegex);
        return passWord.equalsIgnoreCase(confPassWord) && !pattern.matcher(passWord).matches();
    }
}
