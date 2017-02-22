package com.almabranding.yummi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.almabranding.yummi.adapter.PricesArrayAdapter;
import com.almabranding.yummi.adapter.ServiceArrayAdapter;
import com.almabranding.yummi.fragments.CreditCardDialogFragment;
import com.almabranding.yummi.models.BankModel;
import com.almabranding.yummi.models.LoginModel;
import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.models.LoginResponseModel;
import com.almabranding.yummi.models.Logout_model;
import com.almabranding.yummi.models.PerformerLoginResponseModel;
import com.almabranding.yummi.models.RegFifthModel;
import com.almabranding.yummi.models.RegFourthModel;
import com.almabranding.yummi.models.RegSecondModel;
import com.almabranding.yummi.models.RegThirdModel;
import com.almabranding.yummi.models.RegistrationEightModel;
import com.almabranding.yummi.models.RegistrationEightPriceModel;
import com.almabranding.yummi.models.RegistrationEightResponseModel;
import com.almabranding.yummi.models.RegistrationFirstResponseModel;
import com.almabranding.yummi.models.RegistrationFirstResponsePerformerModel;
import com.almabranding.yummi.models.RegistrationSecondResponseModel;
import com.almabranding.yummi.models.RegistrationSecondResponsePerformerModel;
import com.almabranding.yummi.models.RegistrationThirdResponseModel;
import com.almabranding.yummi.models.RegistrationThirdResponsePerformerModel;
import com.almabranding.yummi.models.ResetPassModel;
import com.almabranding.yummi.models.first.RegFirstClient;
import com.almabranding.yummi.models.services.ServiceModel;
import com.almabranding.yummi.models.services.ServicePushModel;
import com.almabranding.yummi.models.services.ServicePushPriceModel;
import com.almabranding.yummi.models.third.GenderModel;
import com.almabranding.yummi.services.RegistrationIntentService;
import com.almabranding.yummi.utils.RealPathUtil;
import com.almabranding.yummi.utils.YummiUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private SharedPreferences sharedPreferences;
    //live//            http://ec2-54-206-80-79.ap-southeast-2.compute.amazonaws.com:3000


    //test//            http://ec2-52-34-30-140.us-west-2.compute.amazonaws.com:3000
    //ditribution//     http://ec2-54-206-73-236.ap-southeast-2.compute.amazonaws.com:3000

    public static final String BASE_STATIC_URL = "http://ec2-54-206-73-236.ap-southeast-2.compute.amazonaws.com:3000";

    public static final String BASE_URL = BASE_STATIC_URL + "/api/";
    Retrofit retrofit;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case YummiUtils.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    showAllert("succes", "permission granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    showAllert("fail", "permission denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();


    }

    @Override
    protected void onPause() {
        YummiUtils.isInForeground = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter("registrationComplete"));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }


        Fabric.with(this, new Crashlytics());


        YummiUtils.verifyLocationPermissions(this);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(WelcomeActivity.this)
                .addOnConnectionFailedListener(WelcomeActivity.this)
                .build();

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        if (!sharedPreferences.getString("token_acces", "").isEmpty()) {
            setContentView(R.layout.activity_welcome_loggin_in);


            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

            Call<PerformerLoginResponseModel> call = service.myProfile(sharedPreferences.getString("token_acces", ""));

            call.enqueue(new Callback<PerformerLoginResponseModel>() {
                @Override
                public void onResponse(Call<PerformerLoginResponseModel> call, Response<PerformerLoginResponseModel> response) {
                    if (response.body() != null) {

                        YummiUtils.token = sharedPreferences.getString("token_acces", "");

                        getAllLists();
                        if (serviceListDO == null) {
                            getServiceList();
                        }
                        if (response.body().getPerformer() != null) {


                            gender = response.body().getPerformer().getGenderId();
                            first_response_per = new RegistrationFirstResponsePerformerModel(new RegFirstClient("", sharedPreferences.getString("token_acces", ""), "1", Integer.valueOf(response.body().getPerformer().getSignUpStep()), null));

                            switch (response.body().getPerformer().getSignUpStep()) {
                                case 0: {
                                    registrationStepTwo(true);
                                    break;
                                }
                                case 1: {
                                    registrationStepThreeClient(true);
                                    break;
                                }
                                case 2: {
                                    registrationStepFour();
                                    break;
                                }
                                case 3: {
                                    registrationStepFifth();
                                    break;
                                }
                                case 4: {
                                    registrationStepSixth();
                                    break;
                                }
                                case 5: {
                                    registrationStepSeventh();
                                    break;
                                }
                                case 6: {
                                    registrationBankAccount();
                                    break;
                                }

                                case 8: {
                                    registrationStepEighth();
                                    break;
                                }
                                case 7: {
                                    registrationStepEighth();
                                    break;
                                }

                                case 9: {
                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    intent.putExtra("token", sharedPreferences.getString("token_acces", ""));
                                    intent.putExtra("userId", response.body().getPerformer().getId());
                                    YummiUtils.performer = response.body().getPerformer();
                                    startActivity(intent);
//                        removeView();
                                    finish();

                                    break;
                                }
                                default: {

                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    intent.putExtra("token", sharedPreferences.getString("token_acces", ""));
                                    intent.putExtra("userId", response.body().getPerformer().getId());
                                    YummiUtils.performer = response.body().getPerformer();
                                    startActivity(intent);
//                        removeView();
                                    finish();

                                    break;
                                }

                            }
                        } else {
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("token", sharedPreferences.getString("token_acces", ""));
                            intent.putExtra("userId", response.body().getClient().getId());
                            YummiUtils.client = response.body().getClient();
                            YummiUtils.token = sharedPreferences.getString("token_acces", "");
                            getAllLists();
                            startActivity(intent);
//                        removeView();
                            finish();
                        }

                    } else {
                        removeView();
                        oldWayWelcome();
                        if (response.errorBody() != null) {
                            try {
                                final String error = response.errorBody().string();
                                Log.e("FastSingUpError", error);
                                showResponseError(error);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<PerformerLoginResponseModel> call, Throwable t) {
                    oldWayWelcome();
                    Log.e("FastSingUpError", t.toString());
                }
            });

        } else {
            oldWayWelcome();

        }
    }

    private void oldWayWelcome() {
        setContentView(R.layout.activity_welcome);

        //        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean("device_token", false);
                String toke = sharedPreferences
                        .getString("device_token_string", "");

                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        findViewById(R.id.welcome_client_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginScreen(false);
                ip = false;

            }
        });

        findViewById(R.id.welcome_performer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginScreen(true);
                ip = true;
            }
        });

        findViewById(R.id.terms_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.webView_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.GONE);
            }
        });

        ((WebView) findViewById(R.id.webView)).loadUrl("http://doyouyummi.com/app_conditions.php");
    }


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("LOCATION", "Connected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(600000);//600000
        mLocationRequest.setFastestInterval(30000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            YummiUtils.verifyLocationPermissions(this);

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOCATION", "onConnectionFailed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LOCATION", "onConnectionSuspended");
    }


    @Override
    public void onLocationChanged(Location location) {
//        Double lat1 = location.getLatitude();
//        Double lng1 = location.getLongitude();

        YummiUtils.setLocation(location);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public void removeView() {
        try {
            rl.removeView(overlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOverlay() {
        rl = (RelativeLayout) findViewById(R.id.login);

        overlay = (LinearLayout) getLayoutInflater().inflate(R.layout.refresh_overlay, null);
        LinearLayout.LayoutParams ppp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        overlay.setLayoutParams(ppp);
        overlay.findViewById(R.id.coach_marks_image).startAnimation(AnimationUtils.loadAnimation(this, R.anim.infinite_rotation));

        rl.addView(overlay);
    }


    public void addOverlay(int layID) {
        rl = (RelativeLayout) findViewById(layID);

        overlay = (LinearLayout) getLayoutInflater().inflate(R.layout.refresh_overlay, null);
        LinearLayout.LayoutParams ppp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        overlay.setLayoutParams(ppp);
        overlay.findViewById(R.id.coach_marks_image).startAnimation(AnimationUtils.loadAnimation(this, R.anim.infinite_rotation));

        rl.addView(overlay);
    }

    RelativeLayout rl;
    LinearLayout overlay;


    private void getAllLists() {
        if (genderModelList == null) {
            getGenderList();
        }

        if (bustModelList == null) {
            getBustList();
        }

        if (hairModelList == null) {
            getHairList();
        }

        if (bodyModelList == null) {
            getBodyList();
        }


    }

    private void setWelcomeScreen() {
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.welcome_client_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginScreen(false);
                ip = false;
                YummiUtils.isClient = true;
            }
        });

        findViewById(R.id.welcome_performer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginScreen(true);
                ip = true;
                YummiUtils.isClient = false;
            }
        });

        findViewById(R.id.terms_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.webView_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.GONE);
            }
        });

        ((WebView) findViewById(R.id.webView)).loadUrl("http://doyouyummi.com/app_conditions.php");

    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.welcome) != null) {
            super.onBackPressed();
        }

        if (findViewById(R.id.first) != null) {
            setLoginScreen(ip);
        }
        if (findViewById(R.id.second) != null) {
            setLoginScreen(ip);
        }
        if (findViewById(R.id.forgot) != null) {
            setLoginScreen(ip);
        }

        if (findViewById(R.id.third) != null) {
            registrationStepTwo(ip);
        }
        if (findViewById(R.id.fourth) != null) {
            registrationStepThreeClient(ip);
        }
        if (findViewById(R.id.fifth) != null) {
            registrationStepFour();
        }
        if (findViewById(R.id.sixth) != null) {
            registrationStepFifth();
        }
        if (findViewById(R.id.seventh) != null) {
            registrationStepSixth();
        }
        if (findViewById(R.id.second_a) != null) {
            registrationStepSeventh();
        }
        if (findViewById(R.id.eight) != null) {
            registrationBankAccount();
        }

        if (findViewById(R.id.login) != null) {
            setWelcomeScreen();
        }
    }

    private boolean ip = false;

    EditText username = null;
    EditText password = null;

    private void setLoginScreen(final boolean isPreformer) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isPreformer", isPreformer);
        editor.apply();
        //set Login Screen
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
//        final Switch rememberme = ((Switch) findViewById(R.id.switch1));
//        rememberme.setChecked(sharedPreferences.getBoolean("switch", false));

        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_forgot_password);

                final EditText forgot_text = ((EditText) findViewById(R.id.reg_sec_stagename));
                final Button submit = (Button) findViewById(R.id.reg_sec_button);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (validField(forgot_text.getText().toString(), emailRegex)) {
                            addOverlay(R.id.forgot);

                            if (ip) {
                                //performer
                                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                                Call<ResponseBody> call = service.performerReset(new ResetPassModel(forgot_text.getText().toString().replace(" ", "")));

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.body() != null)
                                            showAllert("Password Reset Email Sent", "An email has been sent to your email address.\\n Follow the directions in the email to reset your password.");
                                        else
                                            showAllert("Error", "An error has occured");
                                        removeView();

                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        removeView();
                                    }
                                });
                            } else {
                                //client
                                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                                Call<ResponseBody> call = service.clientReset(new ResetPassModel(forgot_text.getText().toString().replace(" ", "")));

                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.body() != null)
                                            showAllert("Password Reset Email Sent", "An email has been sent to your email address.\\n Follow the directions in the email to reset your password.");
                                        else
                                            showAllert("Error", "An error has occured");
                                        removeView();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        removeView();
                                    }
                                });

                            }
                        }

                    }
                });


                forgot_text.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        if (validField(forgot_text.getText().toString(), emailRegex))
                            if (ip) {
                                submit.setBackgroundResource(R.drawable.main_gold_button);
                            } else {
                                submit.setBackgroundResource(R.drawable.main_black_button);
                            }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


            }
        });

//        username.setText(sharedPreferences.getString("email", ""));
//        password.setText(sharedPreferences.getString("password", ""));


//        rememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                ;
//            }
//        });


        final ImageView logo = ((ImageView) findViewById(R.id.login_logo));
        if (isPreformer) {
            logo.setImageResource(R.mipmap.logo_gold);
            (findViewById(R.id.log_in_button)).setBackgroundResource(R.drawable.main_gold_button);
        } else {
            logo.setImageResource(R.mipmap.logo_black);
            (findViewById(R.id.log_in_button)).setBackgroundResource(R.drawable.main_black_button);
        }

        (findViewById(R.id.log_in_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (rememberme.isChecked()) {
//                    editor.putBoolean("switch", true);
//                    editor.putString("email", username.getText().toString().replace(" ", ""));
//                    editor.putString("password", password.getText().toString());
//                    editor.apply();
//                } else {
//                    editor.putBoolean("switch", false);
//                    editor.putString("email", "");
//                    editor.putString("password", "");
//                    editor.apply();
//                }
                addOverlay();

                logIn(isPreformer);
            }
        });

        (findViewById(R.id.login_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   registrationStepSixth();
                registrationStepOne(isPreformer);
            }
        });

    }

    private RegistrationFirstResponseModel first_response = null;
    private RegistrationFirstResponsePerformerModel first_response_per = null;
    private LoginModel user;

    private void registrationStepOne(final boolean isPerformer) {
        setContentView(R.layout.activity_registration_first);


        final Switch switch_reg = (Switch) findViewById(R.id.switch_terms);

        findViewById(R.id.terms_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.webView_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.webView_layout).setVisibility(View.GONE);
            }
        });

        ((WebView) findViewById(R.id.webView)).loadUrl("http://doyouyummi.com/app_conditions.php");
        findViewById(R.id.webView_layout).setVisibility(View.VISIBLE);

        final EditText email_filed = (EditText) findViewById(R.id.reg_firs_email);
        final EditText password_field = (EditText) findViewById(R.id.reg_firs_password);
        final EditText password_confirm_field = (EditText) findViewById(R.id.reg_first_confirm_password);
        final ImageView logo = ((ImageView) findViewById(R.id.login_logo));
        final TextView promo = ((TextView) findViewById(R.id.promo_texview));


        switch_reg.setChecked(false);
        switch_reg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (validField(email_filed.getText().toString().toLowerCase(), emailRegex))
                    if (validPassword(password_field.getText().toString(), password_confirm_field.getText().toString())) {
                        if (switch_reg.isChecked())
                            if (isPerformer) {
                                (findViewById(R.id.reg_first_button)).setBackgroundResource(R.drawable.main_gold_button);
                            } else {
                                (findViewById(R.id.reg_first_button)).setBackgroundResource(R.drawable.main_black_button);
                            }
                    }
            }
        });

        password_confirm_field.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (validField(email_filed.getText().toString(), emailRegex))
                    if (validPassword(password_field.getText().toString(), password_confirm_field.getText().toString())) {
                        if (switch_reg.isChecked())
                            if (isPerformer) {
                                (findViewById(R.id.reg_first_button)).setBackgroundResource(R.drawable.main_gold_button);
                            } else {
                                (findViewById(R.id.reg_first_button)).setBackgroundResource(R.drawable.main_black_button);
                            }
                    }

            }
        });

        if (!isPerformer) {
            (findViewById(R.id.reg_first_button)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (switch_reg.isChecked())
                        addOverlay(R.id.first);

                    if (validField(email_filed.getText().toString(), emailRegex)) {
                        if (validPassword(password_field.getText().toString(), password_confirm_field.getText().toString())) {
                            if (switch_reg.isChecked()) {
                                SharedPreferences sharedPreferences =
                                        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                YummiUtils.deviceToken = sharedPreferences.getString("device_token_string", "");


                                user = new LoginModel(email_filed.getText().toString().replace(" ", "").toLowerCase(), password_field.getText().toString(), sharedPreferences.getString("device_token_string", ""));
                                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                                Call<RegistrationFirstResponseModel> call = service.signUpClient(user);

                                call.enqueue(new Callback<RegistrationFirstResponseModel>() {
                                    @Override
                                    public void onResponse(Call<RegistrationFirstResponseModel> call, Response<RegistrationFirstResponseModel> response) {
                                        if (response.body() != null) {
                                            first_response = response.body();
                                            if (first_response.getClient() != null)
                                                YummiUtils.token = first_response.getClient().getUserToken().getUserId();
                                            getAllLists();
                                            removeView();
                                            registrationStepTwo(isPerformer);
                                        } else {
                                            removeView();
                                            if (response.errorBody() != null) {
                                                try {
                                                    showResponseError(response.errorBody().string());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else
                                                showAllert("Error", "Error during registration");
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<RegistrationFirstResponseModel> call, Throwable t) {
                                        removeView();
                                        showAllert("Network Error", "Could not connect to the server");
                                    }
                                });
                            } else {
                                showAllert("Error", "Please accept terms and conditions");
                            }
                        } else {
                            removeView();
                            showAllert(getString(R.string.error), getString(R.string.wrong_password));
                        }
                    } else {
                        removeView();
                        showAllert(getString(R.string.error), getString(R.string.wrong_email));
                    }
                }

            });

        } else {
            logo.setImageResource(R.mipmap.logo_gold);
            promo.setText(R.string.performer_promo_text);

            (findViewById(R.id.reg_first_button)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    addOverlay(R.id.first);

                    if (validField(email_filed.getText().toString(), emailRegex)) {
                        if (validPassword(password_field.getText().toString(), password_confirm_field.getText().toString())) {
                            SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            YummiUtils.deviceToken = sharedPreferences.getString("device_token_string", "");
                            user = new LoginModel(email_filed.getText().toString().replace(" ", "").toLowerCase(), password_field.getText().toString(), sharedPreferences.getString("device_token_string", ""));
                            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                            Call<RegistrationFirstResponsePerformerModel> call = service.perSignUpClient(user);

                            call.enqueue(new Callback<RegistrationFirstResponsePerformerModel>() {
                                @Override
                                public void onResponse(Call<RegistrationFirstResponsePerformerModel> call, Response<RegistrationFirstResponsePerformerModel> response) {
                                    if (response.body() != null) {
                                        first_response_per = response.body();
                                        YummiUtils.token = first_response_per.getClient().getUserToken().getUserId();
                                        getAllLists();
                                        removeView();
                                        registrationStepTwo(isPerformer);
                                    } else {
                                        removeView();
                                        if (response.errorBody() != null) {
                                            try {
                                                showResponseError(response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else
                                            showAllert("Error", "Error during registration");
                                    }
                                }

                                @Override
                                public void onFailure(Call<RegistrationFirstResponsePerformerModel> call, Throwable t) {
                                    removeView();
                                    showAllert("Network Error", "Could not connect to the server");
                                }
                            });
                        } else {
                            removeView();
                            showAllert(getString(R.string.error), getString(R.string.wrong_password));
                        }
                    } else {
                        removeView();
                        showAllert(getString(R.string.error), getString(R.string.wrong_email));
                    }
                }

            });

        }
    }


    private RegistrationSecondResponseModel second_response = null;
    private RegistrationSecondResponsePerformerModel second_response_per = null;

    private void registrationStepTwo(final boolean isPerformer) {
        setContentView(R.layout.activity_registration_second);
        final EditText real_name_field = (EditText) findViewById(R.id.reg_sec_realname);
        final EditText stage_name_field = (EditText) findViewById(R.id.reg_sec_stagename);
        final ImageView logo = ((ImageView) findViewById(R.id.login_logo));
        final TextView promo = ((TextView) findViewById(R.id.promo_texview));


        real_name_field.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!real_name_field.getText().toString().isEmpty() && !stage_name_field.getText().toString().isEmpty()) {
                    if (real_name_field.getText().toString().length() > 3)
                        if (findViewById(R.id.reg_sec_button) != null)
                            if (isPerformer) {
                                (findViewById(R.id.reg_sec_button)).setBackgroundResource(R.drawable.main_gold_button);
                            } else {
                                (findViewById(R.id.reg_sec_button)).setBackgroundResource(R.drawable.main_black_button);
                            }
                }

            }
        });

        if (!isPerformer) {
            stage_name_field.setHint("Username");
            (findViewById(R.id.reg_sec_button)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (!real_name_field.getText().toString().isEmpty() && !stage_name_field.getText().toString().isEmpty()) {
                        addOverlay(R.id.second);
                        RegSecondModel model = new RegSecondModel(
                                stage_name_field.getText().toString(),
                                real_name_field.getText().toString());

                        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                        Call<RegistrationSecondResponseModel> call = service.signUpClientSecond(first_response.getClient().getUserId(), model, getValidToken());

                        call.enqueue(new Callback<RegistrationSecondResponseModel>() {
                            @Override
                            public void onResponse(Call<RegistrationSecondResponseModel> call, Response<RegistrationSecondResponseModel> response) {
                                second_response = response.body();
                                removeView();
                                if (second_response != null) {

                                    if (genderModelList == null) {
                                        getGenderList();
                                        removeView();
                                        registrationStepThreeClient(isPerformer);
                                    } else {
                                        removeView();
                                        registrationStepThreeClient(isPerformer);

                                    }
                                }
                                if (response.body() == null) {
                                    if (response.errorBody() != null) {
                                        removeView();
                                        try {
                                            showResponseError(response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        showAllert("Error", "Error during registration");
                                        removeView();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<RegistrationSecondResponseModel> call, Throwable t) {
                                removeView();
                                showAllert("Network Error", "Could not connect to the server");
                            }
                        });

                    }
                }

            });
        } else {
            logo.setImageResource(R.mipmap.logo_gold);
            promo.setText(R.string.performer_promo_text);

            (findViewById(R.id.reg_sec_button)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    addOverlay(R.id.second);
                    if (!real_name_field.getText().toString().isEmpty() && !stage_name_field.getText().toString().isEmpty()) {

                        if (!validField(stage_name_field.getText().toString(), usernameRegex)) {
                            removeView();
                            if (stage_name_field.getText().toString().length() >= 3) {
                                showAllert("Error", "Your stage name cannot have special characters or spaces");
                            } else {
                                showAllert("Error", "Stage name needs to be minimum 3 characters long");
                            }
                            return;

                        }

                        if (!validField(real_name_field.getText().toString(), nameRegex)) {
                            removeView();
                            showAllert("Error", "Your real name is invalid");
                            return;
                        }

                        RegSecondModel model = new RegSecondModel(
                                stage_name_field.getText().toString(),
                                real_name_field.getText().toString());

                        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                        Call<RegistrationSecondResponsePerformerModel> call = service.perSignUpClientSecond(first_response_per.getClient().getUserId(), model, getValidToken());

                        call.enqueue(new Callback<RegistrationSecondResponsePerformerModel>() {
                            @Override
                            public void onResponse(Call<RegistrationSecondResponsePerformerModel> call, Response<RegistrationSecondResponsePerformerModel> response) {
                                second_response_per = response.body();
                                if (second_response_per != null) {
                                    if (genderModelList == null) {
                                        getGenderList();
                                        removeView();
                                        registrationStepThreeClient(isPerformer);
                                    } else {
                                        removeView();
                                        registrationStepThreeClient(isPerformer);
                                    }
                                }
                                if (response.body() == null) {
                                    if (response.errorBody() != null) {
                                        try {
                                            showResponseError(response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else
                                        showAllert("Error", "Error during registration");
                                }
                            }

                            @Override
                            public void onFailure(Call<RegistrationSecondResponsePerformerModel> call, Throwable t) {
                                removeView();
                                showAllert("Network Error", "Could not connect to the server");
                            }
                        });

                    }
                }

            });
        }
    }

    String gender = "";
    String interestedIn = "";

    private void registrationStepThreeClient(final boolean isPerformer) {
        setContentView(R.layout.activity_registration_third_client);
        final TextView gender_a = ((TextView) findViewById(R.id.choice_a));
        final TextView gender_b = ((TextView) findViewById(R.id.choice_b));
        final TextView gender_c = ((TextView) findViewById(R.id.choice_c));

        final TextView int_a = ((TextView) findViewById(R.id.choice_aa));
        final TextView int_b = ((TextView) findViewById(R.id.choice_ab));
        final TextView int_c = ((TextView) findViewById(R.id.choice_ac));

        final ImageView logo = ((ImageView) findViewById(R.id.login_logo));
        final TextView promo = ((TextView) findViewById(R.id.promo_texview));

        if (isPerformer) {
            (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
        } else {
            (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_black_button);
        }


        if (genderModelList != null) {

            gender = genderModelList.get(0).getId();
            interestedIn = genderModelList.get(0).getId();

            gender_a.setText(genderModelList.get(0).getName());
            int_a.setText(genderModelList.get(0).getName());

            gender_a.setBackgroundResource(R.drawable.left_on);
            gender_a.setTextColor(getResources().getColor(R.color.bgColor));

            int_a.setBackgroundResource(R.drawable.left_on);
            int_a.setTextColor(getResources().getColor(R.color.bgColor));


            gender_b.setText(genderModelList.get(1).getName());
            int_b.setText(genderModelList.get(1).getName());

            gender_c.setText(genderModelList.get(2).getName());
            int_c.setText(genderModelList.get(2).getName());

        }

        gender_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = genderModelList.get(0).getId();

                gender_a.setBackgroundResource(R.drawable.left_on);
                gender_a.setTextColor(getResources().getColor(R.color.bgColor));

                gender_b.setBackgroundResource(R.drawable.mid_off);
                gender_b.setTextColor(getResources().getColor(R.color.blackColor));

                gender_c.setBackgroundResource(R.drawable.right_off);
                gender_c.setTextColor(getResources().getColor(R.color.blackColor));
            }
        });

        gender_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = genderModelList.get(1).getId();

                gender_a.setBackgroundResource(R.drawable.left_off);
                gender_a.setTextColor(getResources().getColor(R.color.blackColor));

                gender_b.setBackgroundResource(R.drawable.mid_on);
                gender_b.setTextColor(getResources().getColor(R.color.bgColor));

                gender_c.setBackgroundResource(R.drawable.right_off);
                gender_c.setTextColor(getResources().getColor(R.color.blackColor));
            }
        });

        gender_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = genderModelList.get(2).getId();

                gender_a.setBackgroundResource(R.drawable.left_off);
                gender_a.setTextColor(getResources().getColor(R.color.blackColor));

                gender_b.setBackgroundResource(R.drawable.mid_off);
                gender_b.setTextColor(getResources().getColor(R.color.blackColor));

                gender_c.setBackgroundResource(R.drawable.right_on);
                gender_c.setTextColor(getResources().getColor(R.color.bgColor));
            }
        });


        int_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestedIn = genderModelList.get(0).getId();

                int_a.setBackgroundResource(R.drawable.left_on);
                int_a.setTextColor(getResources().getColor(R.color.bgColor));

                int_b.setBackgroundResource(R.drawable.mid_off);
                int_b.setTextColor(getResources().getColor(R.color.blackColor));

                int_c.setBackgroundResource(R.drawable.right_off);
                int_c.setTextColor(getResources().getColor(R.color.blackColor));
            }
        });

        int_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestedIn = genderModelList.get(1).getId();

                int_a.setBackgroundResource(R.drawable.left_off);
                int_a.setTextColor(getResources().getColor(R.color.blackColor));

                int_b.setBackgroundResource(R.drawable.mid_on);
                int_b.setTextColor(getResources().getColor(R.color.bgColor));

                int_c.setBackgroundResource(R.drawable.right_off);
                int_c.setTextColor(getResources().getColor(R.color.blackColor));
            }
        });

        int_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestedIn = genderModelList.get(2).getId();

                int_a.setBackgroundResource(R.drawable.left_off);
                int_a.setTextColor(getResources().getColor(R.color.blackColor));

                int_b.setBackgroundResource(R.drawable.mid_off);
                int_b.setTextColor(getResources().getColor(R.color.blackColor));

                int_c.setBackgroundResource(R.drawable.right_on);
                int_c.setTextColor(getResources().getColor(R.color.bgColor));
            }
        });


        if (!isPerformer) {
            findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RegThirdModel model = new RegThirdModel(
                            gender,
                            interestedIn);

                    NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                    Call<RegistrationThirdResponseModel> call = service.signUpClientThird(first_response.getClient().getUserId(), model, getValidToken());

                    call.enqueue(new Callback<RegistrationThirdResponseModel>() {
                        @Override
                        public void onResponse(Call<RegistrationThirdResponseModel> call, Response<RegistrationThirdResponseModel> response) {
//                            setLoginScreen(isPerformer);
//                            showAllert("Succes", "now you can log in " + response.body().getClient().getStageName());


                            logInUser(isPerformer);
                        }

                        @Override
                        public void onFailure(Call<RegistrationThirdResponseModel> call, Throwable t) {
                            showAllert("Network Error", "Could not connect to the server");
                        }
                    });
                }
            });
        } else {
            findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RegThirdModel model = new RegThirdModel(
                            gender,
                            interestedIn);
                    if (serviceListDO == null) {
                        getServiceList();
                    }

                    NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                    Call<RegistrationThirdResponsePerformerModel> call = service.perSignUpClientThird(first_response_per.getClient().getUserId(), model, getValidToken());

                    call.enqueue(new Callback<RegistrationThirdResponsePerformerModel>() {
                        @Override
                        public void onResponse(Call<RegistrationThirdResponsePerformerModel> call, Response<RegistrationThirdResponsePerformerModel> response) {
                            registrationStepFour();
                        }

                        @Override
                        public void onFailure(Call<RegistrationThirdResponsePerformerModel> call, Throwable t) {
                            showAllert("Network Error", "Could not connect to the server");
                        }
                    });
                }
            });
        }

    }


    private void registrationStepFour() {
        setContentView(R.layout.activity_registration_fourth);

        final TextView age_tv = ((TextView) findViewById(R.id.age_text));
        final TextView height_tv = ((TextView) findViewById(R.id.height_text));

        final SeekBar age_seek = ((SeekBar) findViewById(R.id.seek_bar_age));
        final SeekBar height_seek = ((SeekBar) findViewById(R.id.seek_bar_height));

        height_tv.setText(getString(R.string.height) + " " + String.valueOf(round(1.20 + 0.80 * height_seek.getProgress() / height_seek.getMax(), 2)) + " m");
        age_tv.setText(getString(R.string.age) + " " + String.valueOf((int) round(19 + 30 * age_seek.getProgress() / age_seek.getMax(), 2)));

        age_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                age_tv.setText(getString(R.string.age) + " " + String.valueOf((int) round(19 + 30 * age_seek.getProgress() / age_seek.getMax(), 2)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        height_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                height_tv.setText(getString(R.string.height) + " " + String.valueOf(round(1.20 + 0.80 * height_seek.getProgress() / height_seek.getMax(), 2)) + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegFourthModel model = new RegFourthModel(
                        round(1.20 + 0.80 * height_seek.getProgress() / height_seek.getMax(), 2),
                        round(19 + 30 * age_seek.getProgress() / age_seek.getMax(), 2));

                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                Call<RegistrationThirdResponsePerformerModel> call = service.perSignUpClientFourth(first_response_per.getClient().getUserId(), model, getValidToken());

                call.enqueue(new Callback<RegistrationThirdResponsePerformerModel>() {
                    @Override
                    public void onResponse(Call<RegistrationThirdResponsePerformerModel> call, Response<RegistrationThirdResponsePerformerModel> response) {
                        registrationStepFifth();
                    }

                    @Override
                    public void onFailure(Call<RegistrationThirdResponsePerformerModel> call, Throwable t) {
                        showAllert("Network Error", "Could not connect to the server");
                    }
                });
            }
        });
    }

    Spinner spinner_hair;
    Spinner spinner_bust;
    Spinner spinner_body;

    private void registrationStepFifth() {
        setContentView(R.layout.activity_registration_fifth);

        spinner_hair = ((Spinner) findViewById(R.id.spinner_hair));
        spinner_bust = ((Spinner) findViewById(R.id.spinner_Bust));
        spinner_body = ((Spinner) findViewById(R.id.spinner_Body));


        if (genderModelList != null)
            for (GenderModel g : genderModelList) {
                if (gender.equalsIgnoreCase(g.getId())) {
                    if (g.getName().equalsIgnoreCase("male")) {
                        spinner_body.setVisibility(View.GONE);
                        ArrayList<String> body = getStringListFromModeList(bodyModelList);
                        body.add(0, getResources().getString(R.string.body));
                        ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, body);
                        spinner_bust.setAdapter(adapter_body);
                    } else {
                        spinner_body.setVisibility(View.VISIBLE);

                        ArrayList<String> bust = getStringListFromModeList(bustModelList);
                        bust.add(0, getResources().getString(R.string.bust));
                        ArrayAdapter<String> adapter_bust = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, bust);
                        spinner_bust.setAdapter(adapter_bust);


                        ArrayList<String> body = getStringListFromModeList(bodyModelList);
                        body.add(0, getResources().getString(R.string.body));
                        ArrayAdapter<String> adapter_body = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, body);
                        spinner_body.setAdapter(adapter_body);
                    }
                }
            }

        ArrayList<String> hair = getStringListFromModeList(hairModelList);
        hair.add(0, getResources().getString(R.string.hair));

        ArrayAdapter<String> adapter_hair = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, hair);
        spinner_hair.setAdapter(adapter_hair);

        findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinner_body.getSelectedItemPosition() == 0
                        || spinner_hair.getSelectedItemPosition() == 0
                        || spinner_bust.getSelectedItemPosition() == 0) {
                    showAllert("Error", "Please select all the categories");
                } else {

                    RegFifthModel model;

                    if (spinner_body.getVisibility() == View.GONE) {
                        model = new RegFifthModel(
                                hairModelList.get(spinner_hair.getSelectedItemPosition() - 1).getId(),
                                bodyModelList.get(spinner_bust.getSelectedItemPosition() - 1).getId(),
                                bustModelList.get(0).getId());
                    } else {
                        model = new RegFifthModel(
                                hairModelList.get(spinner_hair.getSelectedItemPosition() - 1).getId(),
                                bodyModelList.get(spinner_body.getSelectedItemPosition() - 1).getId(),
                                bustModelList.get(spinner_bust.getSelectedItemPosition() - 1).getId());
                    }

                    NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                    Call<RegistrationThirdResponsePerformerModel> call = service.perSignUpClientFifth(first_response_per.getClient().getUserId(), model, getValidToken());

                    call.enqueue(new Callback<RegistrationThirdResponsePerformerModel>() {
                        @Override
                        public void onResponse(Call<RegistrationThirdResponsePerformerModel> call, Response<RegistrationThirdResponsePerformerModel> response) {
//                        showAllert("Succes", "");
                            registrationStepSixth();
                        }

                        @Override
                        public void onFailure(Call<RegistrationThirdResponsePerformerModel> call, Throwable t) {
                            showAllert("Network Error", "Could not connect to the server");
                        }
                    });
                }
            }
        });


    }

    public static final int SELECT_IMAGE_FIRST = 41;
    public static final int SELECT_IMAGE_SECOND = 42;
    public static final int SELECT_IMAGE_THIRD = 43;
    public static final int SELECT_IMAGE_FOURTH = 44;

    private void registrationStepSixth() {
        setContentView(R.layout.activity_registration_sixth);

        YummiUtils.verifyStoragePermissions(this);

        selectedImagePath_one = "";
        selectedImagePath_two = "";
        selectedImagePath_three = "";
        selectedImagePath_four = "";
        fr1 = false;
        fr2 = false;
        fr3 = false;
        fr4 = false;

        file1 = null;
        file2 = null;
        file3 = null;
        file4 = null;

        ImageButton one = ((ImageButton) findViewById(R.id.button_one));
        ImageButton two = ((ImageButton) findViewById(R.id.button_two));
        ImageButton three = ((ImageButton) findViewById(R.id.button_three));
        ImageButton four = ((ImageButton) findViewById(R.id.button_four));

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setAction(Intent.ACTION_GET_CONTENT);//
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 1000);
                startActivityForResult(intent, SELECT_IMAGE_FIRST);
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
                startActivityForResult(intent, SELECT_IMAGE_SECOND);
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
                startActivityForResult(intent, SELECT_IMAGE_THIRD);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);//
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 1000);
                startActivityForResult(intent, SELECT_IMAGE_FOURTH);
            }
        });

        findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (selectedImagePath_four.isEmpty() || selectedImagePath_three.isEmpty() || selectedImagePath_one.isEmpty() || selectedImagePath_two.isEmpty()) {
                    showAllert("Error", "Please upload 4 pictures");
                } else {
//                    if (!selectedImagePath_one.isEmpty())
//                        uploadImage(selectedImagePath_one, "1");
//
//                    if (!selectedImagePath_two.isEmpty())
//                        uploadImage(selectedImagePath_two, "2");
//
//                    if (!selectedImagePath_three.isEmpty())
//                        uploadImage(selectedImagePath_three, "3");
//
//                    if (!selectedImagePath_four.isEmpty())
//                        uploadImage(selectedImagePath_four, "4");

                    if (file1 != null) {
                        addOverlay(R.id.sixth);
                        uploadImage(file1, "1");
                    } else
                        showAllert("Pleas wait", "One of the pictures is uploading");
                }


            }
        });

    }


    private void uploadImage(final File file, final String order) {
        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);


        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ResponseBody> call = service.upload(first_response_per.getClient().getUserId(),
//                MultipartBody.Part.createFormData("id", first_response_per.getClient().getUserId()),
                MultipartBody.Part.createFormData("order", order),
                body, getValidToken());
        Log.e("IMAGE", "file: " + order + " started toupload");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    Log.e("IMAGE", "file: " + order + " started succes");
                    switch (order) {
                        case "1": {
                            fr1 = true;
                            file1 = null;
                            if (file2 != null) {
                                uploadImage(file2, "2");
                            }
                            break;
                        }
                        case "2": {
                            fr2 = true;
                            file2 = null;
                            if (file3 != null) {
                                uploadImage(file3, "3");
                            }
                            break;
                        }
                        case "3": {
                            fr3 = true;
                            file3 = null;
                            if (file4 != null) {
                                uploadImage(file4, "4");
                            }
                            break;
                        }
                        case "4": {
                            fr4 = true;
                            file4 = null;

                            break;
                        }
                    }
                } else {
                    Log.e("IMAGE", "file: " + order + " started failed " + response.errorBody().toString());
                    switch (order) {
                        case "1": {
                            fr1 = false;
                            showAllert("Error", "Failed to upload first picture");
                            file1 = null;
                            removeView();
                            break;
                        }
                        case "2": {
                            fr2 = false;
                            showAllert("Error", "Failed to upload second picture");
                            file2 = null;
                            removeView();
                            break;
                        }
                        case "3": {
                            fr3 = false;
                            showAllert("Error", "Failed to upload third picture");
                            file3 = null;
                            removeView();
                            break;
                        }
                        case "4": {
                            fr4 = false;
                            showAllert("Error", "Failed to upload fourth picture");
                            file4 = null;
                            removeView();
                            break;
                        }

                        default: {
                            removeView();
                            break;
                        }
                    }
                }

                if (isSucces()) {
                    (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
                    removeView();
                    registrationStepSeventh();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("IMAGE", "file: " + order + " started failed Network");

                switch (order) {
                    case "1": {
                        fr1 = false;
                        showAllert("Network Error", "Failed to upload first picture");
                        file1 = null;
                        removeView();
                        break;
                    }
                    case "2": {
                        fr2 = false;
                        showAllert("Network Error", "Failed to upload second picture");
                        file2 = null;
                        removeView();
                        break;
                    }
                    case "3": {
                        fr3 = false;
                        showAllert("Network Error", "Failed to upload third picture");
                        file3 = null;
                        removeView();
                        break;
                    }
                    case "4": {
                        fr4 = false;
                        showAllert("Network Error", "Failed to upload fourth picture");
                        file4 = null;
                        removeView();
                        break;
                    }
                    default: {
                        removeView();
                        break;
                    }
                }
            }
        });
    }

    private void uploadIdImage(final File file, final String order) {
        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);


        final RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ResponseBody> call = service.uploadidImages(first_response_per.getClient().getUserId(),
//                MultipartBody.Part.createFormData("id", first_response_per.getClient().getUserId()),
                MultipartBody.Part.createFormData("order", order),
                body, getValidToken());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    switch (order) {
                        case "1": {
                            fr1 = true;
                            file1 = null;
                            if (file2 != null) {
                                uploadIdImage(file2, "2");
                            }
                            break;
                        }
                        case "2": {
                            fr2 = true;
                            file2 = null;
                            if (file3 != null) {
                                uploadIdImage(file3, "3");
                            }
                            break;
                        }
                        case "3": {
                            fr3 = true;
                            file3 = null;
                            if (file4 != null) {
                                uploadIdImage(file4, "4");
                            }
                            break;
                        }
                        case "4": {
                            fr4 = true;
                            file4 = null;

                            break;
                        }
                    }
                } else {
                    switch (order) {
                        case "1": {
                            fr1 = false;
                            showAllert("Error", "Failed to upload first picture");
                            file1 = null;
                            removeView();
                            break;
                        }
                        case "2": {
                            fr2 = false;
                            showAllert("Error", "Failed to upload second picture");
                            file2 = null;
                            removeView();
                            break;
                        }
                        case "3": {
                            fr3 = false;
                            showAllert("Error", "Failed to upload third picture");
                            file3 = null;
                            removeView();
                            break;
                        }
                        case "4": {
                            fr4 = false;
                            showAllert("Error", "Failed to upload fourth picture");
                            file4 = null;
                            removeView();
                            break;
                        }

                        default: {
                            removeView();
                            break;
                        }
                    }
                }


                if (fr1 || fr2 || fr3 || fr4) {
                    if ((findViewById(R.id.reg_third_button)) != null)
                        (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
                    removeView();
                    registrationBankAccount();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                switch (order) {
                    case "1": {
                        fr1 = false;
                        showAllert("Network Error", "Failed to upload first picture");
                        file1 = null;
                        removeView();
                        break;
                    }
                    case "2": {
                        fr2 = false;
                        showAllert("Network Error", "Failed to upload second picture");
                        file2 = null;
                        removeView();
                        break;
                    }
                    case "3": {
                        fr3 = false;
                        showAllert("Network Error", "Failed to upload third picture");
                        file3 = null;
                        removeView();
                        break;
                    }
                    case "4": {
                        fr4 = false;
                        showAllert("Network Error", "Failed to upload fourth picture");
                        file4 = null;
                        removeView();
                        break;
                    }
                    default: {
                        removeView();
                        break;
                    }
                }
            }
        });
    }


    public boolean credCard = false;


    public void goldenButton() {
        if (credCard) {
            if (findViewById(R.id.reg_third_button) != null) {
                if (selectedImagePath_four.isEmpty() && selectedImagePath_three.isEmpty() && selectedImagePath_one.isEmpty() && selectedImagePath_two.isEmpty()) {
                    ;
                } else {
                    ((Button) findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
                }
            }
        }
    }

    private void registrationStepSeventh() {
        setContentView(R.layout.activity_registration_seventh);

        selectedImagePath_one = "";
        selectedImagePath_two = "";
        selectedImagePath_three = "";
        selectedImagePath_four = "";
        fr1 = false;
        fr2 = false;
        fr3 = false;
        fr4 = false;
        credCard = false;

        file1 = null;
        file2 = null;

        ImageButton one = ((ImageButton) findViewById(R.id.button_one));
        ImageButton two = ((ImageButton) findViewById(R.id.button_two));

        findViewById(R.id.choice_cred_card).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                // Create and show the dialog.
                CreditCardDialogFragment newFragment = new CreditCardDialogFragment();
                newFragment.setwAct(WelcomeActivity.this);
                newFragment.setEventModel(null);
                newFragment.show(ft, "dialog");
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra("scale", true);
                intent.putExtra("outputX", 1000);
                startActivityForResult(intent, SELECT_IMAGE_FIRST);
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
                startActivityForResult(intent, SELECT_IMAGE_SECOND);
            }
        });

        findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!credCard) {
                    showAllert("Error", "Please verify your credit card");
                } else if (selectedImagePath_four.isEmpty() && selectedImagePath_three.isEmpty() && selectedImagePath_one.isEmpty() && selectedImagePath_two.isEmpty()) {
                    showAllert("Error", "Please upload at least 1 picture");
                } else {
//                    if (!selectedImagePath_one.isEmpty())
//                        uploadIdImage(selectedImagePath_one, "1");
//                    if (!selectedImagePath_two.isEmpty())
//                        uploadIdImage(selectedImagePath_two, "2");
//                    if (!selectedImagePath_three.isEmpty())
//                        uploadIdImage(selectedImagePath_three, "3");
//                    if (!selectedImagePath_four.isEmpty())
//                        uploadIdImage(selectedImagePath_four, "4");

                    if (file1 != null) {
                        addOverlay(R.id.seventh);
                        uploadIdImage(file1, "1");
                    } else
                        showAllert("Please Wait", "Failed to upload at least 1 picture yet");
                }
            }
        });
    }

    String bsbRegex = "^\\d{3}-\\d{3}$";
    String accountRegex = "^\\d{8,10}$";
    String idRegex = "^\\d{8}$";


    private void registrationBankAccount() {
        setContentView(R.layout.activity_registration_eight_a);

        final EditText idNumber = (EditText) findViewById(R.id.reg_sec_stagename);
        final EditText bsb = (EditText) findViewById(R.id.reg_sec_realname);
        final EditText account_number = (EditText) findViewById(R.id.reg_sec_acount_number);

        TextWatcher textw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validField(bsb.getText().toString(), bsbRegex) && validField(idNumber.getText().toString(), idRegex) && validField(account_number.getText().toString(), accountRegex)) {
                    (findViewById(R.id.reg_sec_button)).setBackgroundResource(R.drawable.main_gold_button);
                } else {
                    (findViewById(R.id.reg_sec_button)).setBackgroundResource(R.drawable.main_grey_button);
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

        findViewById(R.id.reg_sec_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validField(bsb.getText().toString(), bsbRegex) && validField(idNumber.getText().toString(), idRegex) && validField(account_number.getText().toString(), accountRegex)) {
                    addOverlay(R.id.second_a);
                    NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

                    Call<BankModel> call = service.postBankAccount(first_response_per.getClient().getUserId(), new BankModel(bsb.getText().toString().substring(0, 3), account_number.getText().toString(), idNumber.getText().toString(), bsb.getText().toString().substring(4, 7)), getValidToken());

                    call.enqueue(new Callback<BankModel>() {
                        @Override
                        public void onResponse(Call<BankModel> call, Response<BankModel> response) {
                            if (response.body() != null) {
                                removeView();
                                registrationStepEighth();
                            } else {
                                removeView();

                                showAllert("Error", "Error during registration");
                            }

                        }

                        @Override
                        public void onFailure(Call<BankModel> call, Throwable t) {
                            removeView();
                            showAllert("Network Error", "Could not connect to the server");
                        }
                    });

                } else {
                    showAllert("Error", "Please fill all the fields");
                }
            }
        });
    }


    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    public static ArrayList<ServicePushModel> services = new ArrayList<>();
    public static ArrayList<ServicePushPriceModel> prices = new ArrayList<>();


    boolean isService = false;

    private void registrationStepEighth() {
        getStringFromServiceList();
        services.clear();
        prices.clear();

        YummiUtils.chatPrice = 0;
        YummiUtils.imagePrice = 0;

        if (services.isEmpty()) {
            if (serviceListDO == null) {
                getServiceList();
            } else
                for (RegistrationEightResponseModel g : serviceListDO) {
                    for (ServiceModel m : g.getServiceList())
                        services.add(new ServicePushModel(0, m.getId()));
                }
        }

        if (prices.isEmpty()) {
            for (ServiceModel sm : headers) {
                prices.add(new ServicePushPriceModel(1, sm.getId()));
            }
        }


        setContentView(R.layout.activity_registration_eight);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle mDrawerToggle;
        Toolbar tb = new Toolbar(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                tb, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here

                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
                if (isService) {
                    boolean hasAtLeastService = false;
                    for (ServicePushModel spm : services) {
                        if (spm.getAvailability() == 2)
                            hasAtLeastService = true;
                    }


                    if (hasAtLeastService) {
                        RegistrationEightModel model = new RegistrationEightModel(services);

                        Call<ResponseBody> call = service.registrationServices(first_response_per.getClient().getUserId(), model, getValidToken());

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                response.body();

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showAllert("Network Error", "Could not connect to the server");
//                        services.clear();
                            }
                        });
                    } else {
                        showAllert("Service Error", "Please set at least one service to 'Yes' value");
                    }
                } else {


                    if (WelcomeActivity.prices.isEmpty()) {
                        for (ServiceModel sm : headers) {
                            prices.add(new ServicePushPriceModel(0, sm.getId()));
                        }
                    }

                    ArrayList<ServicePushPriceModel> sen = new ArrayList<>();
                    for (ServicePushPriceModel sppm : WelcomeActivity.prices) {
                        if (sppm.getServiceId().equalsIgnoreCase("66")) {
                            if (YummiUtils.chatPrice < 1)
                                YummiUtils.chatPrice = sppm.getPrice();
                        } else if (sppm.getServiceId().equalsIgnoreCase("68")) {
                            if (YummiUtils.imagePrice < 1)
                                YummiUtils.imagePrice = sppm.getPrice();
                        } else {
                            sen.add(sppm);
                        }
                    }


                    RegistrationEightPriceModel m = new RegistrationEightPriceModel(sen, YummiUtils.imagePrice, YummiUtils.chatPrice);

                    Call<ResponseBody> c = service.registrationPrices(first_response_per.getClient().getUserId(), m, getValidToken());

                    c.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            response.body();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            showAllert("Network Error", "Could not connect to the server");
//                        services.clear();
                        }
                    });

                }

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getStringFromServiceList();

        findViewById(R.id.triple_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isService = true;
                if (services.isEmpty()) {
                    if (serviceListDO == null) {
                        getServiceList();
                    } else
                        for (RegistrationEightResponseModel g : serviceListDO) {
                            for (ServiceModel m : g.getServiceList())
                                services.add(new ServicePushModel(0, m.getId()));
                        }
                }

                ServiceArrayAdapter adapter = new ServiceArrayAdapter(WelcomeActivity.this, getStringFromServiceList());
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


        findViewById(R.id.tr_choice_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isService = false;
                if (prices.isEmpty()) {
                    for (ServiceModel sm : headers) {
                        prices.add(new ServicePushPriceModel(0, sm.getId()));
                    }
                }

                YummiUtils.imagePrice = 0;
                YummiUtils.chatPrice = 0;

                PricesArrayAdapter adapter = new PricesArrayAdapter(WelcomeActivity.this, headers);
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


        findViewById(R.id.reg_third_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOverlay(R.id.eight);

                logInUser(true);
                removeView();
                services.clear();
                prices.clear();


            }
        });

    }


    private String selectedImagePath_one = "";
    private String selectedImagePath_two = "";
    private String selectedImagePath_three = "";
    private String selectedImagePath_four = "";


    boolean fr1 = false;
    boolean fr4 = false;
    boolean fr3 = false;
    boolean fr2 = false;


    File file1;
    File file2;
    File file3;
    File file4;


    private boolean isSucces() {
        return fr1 && fr2 && fr3 && fr4;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_FIRST) {
            if (resultCode == RESULT_OK) {
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
                        showAllert("Error", "This gallery is not supported, please use other.");

//                        showAllert("Error", e.toString());
                    }


                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == SELECT_IMAGE_FIRST) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                ((ImageButton) findViewById(R.id.button_one)).setImageBitmap(yourSelectedImage);
                if (selectedImagePath_one == null || selectedImagePath_one.isEmpty()) {
//                    showAllert("Error", "Could not get the picture, Uri:"+data.getData().toString()+" first_path:"+getPath(data.getData())+" second:" +getRealpath(data));
                    return;
                }

//                showAllert("Print", "Could not get the picture, Uri:"+data.getData().toString()+" first_path:"+getPath(data.getData())+" second:" +getRealpath(data));

                file1 = new File(selectedImagePath_one);
                FileOutputStream fOut = new FileOutputStream(file1);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();


            } catch (Exception e) {
                e.printStackTrace();

                file1 = null;
                selectedImagePath_one = "";
                fr1 = false;

                showAllert("Error", "Image was not selected");
            }
        }

        if (requestCode == SELECT_IMAGE_SECOND) {
            if (resultCode == RESULT_OK) {
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
                        showAllert("Error", "This gallery is not supported, please use other.");
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == SELECT_IMAGE_SECOND) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                ((ImageButton) findViewById(R.id.button_two)).setImageBitmap(yourSelectedImage);
                if (selectedImagePath_two == null || selectedImagePath_two.isEmpty()) {
                    showAllert("Error", "Could not get the picture");
                    return;
                }
                file2 = new File(selectedImagePath_two);
                FileOutputStream fOut = new FileOutputStream(file2);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();


            } catch (Exception e) {
                e.printStackTrace();
                file2 = null;
                selectedImagePath_two = "";
                fr2 = false;

                showAllert("Error", "Image was not selected");
            }
        }

        if (requestCode == SELECT_IMAGE_THIRD) {
            if (resultCode == RESULT_OK) {
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
                        showAllert("Error", "This gallery is not supported, please use other.");
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == SELECT_IMAGE_THIRD) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                ((ImageButton) findViewById(R.id.button_three)).setImageBitmap(yourSelectedImage);
                if (selectedImagePath_three == null || selectedImagePath_three.isEmpty()) {
                    showAllert("Error", "Could not get the picture");
                    return;
                }
                file3 = new File(selectedImagePath_three);
                FileOutputStream fOut = new FileOutputStream(file3);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();

            } catch (Exception e) {
                e.printStackTrace();
                file3 = null;
                selectedImagePath_three = "";
                fr3 = false;

                showAllert("Error", "Image was not selected");
            }
        }

        if (requestCode == SELECT_IMAGE_FOURTH) {
            if (resultCode == RESULT_OK) {
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
                        showAllert("Error", "This gallery is not supported, please use other.");
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == SELECT_IMAGE_FOURTH) {
            try {
                Bitmap yourSelectedImage = YummiUtils.scaleBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                ((ImageButton) findViewById(R.id.button_four)).setImageBitmap(yourSelectedImage);

                if (selectedImagePath_four == null || selectedImagePath_four.isEmpty()) {
                    showAllert("Error", "Could not get the picture");
                    return;
                }

                file4 = new File(selectedImagePath_four);
                FileOutputStream fOut = new FileOutputStream(file4);
                yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush();
                fOut.close();

            } catch (Exception e) {
                e.printStackTrace();
                file4 = null;
                selectedImagePath_four = "";
                fr4 = false;

                showAllert("Error", "Image was not selected");
            }
        }


        if (selectedImagePath_four.isEmpty() || selectedImagePath_three.isEmpty() || selectedImagePath_one.isEmpty() || selectedImagePath_two.isEmpty()) {
            ;
        } else {
            (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
        }


        if (findViewById(R.id.seventh) != null) {
            if (!credCard) {
                ;
            } else if (selectedImagePath_four.isEmpty() && selectedImagePath_three.isEmpty() && selectedImagePath_one.isEmpty() && selectedImagePath_two.isEmpty()) {
                ;
            } else {
                (findViewById(R.id.reg_third_button)).setBackgroundResource(R.drawable.main_gold_button);
            }
        }

    }


    List<GenderModel> genderModelList = null;
    public static List<GenderModel> bustModelList = null;
    public static List<GenderModel> hairModelList = null;
    public static List<GenderModel> bodyModelList = null;
    public static List<RegistrationEightResponseModel> serviceListDO = null;

    private ArrayList<String> getStringListFromModeList(List<GenderModel> modes) {

        ArrayList<String> result = new ArrayList<String>();
        for (GenderModel g : modes) {
            result.add(g.getName());
        }
        return result;
    }

    ArrayList<ServiceModel> headers = new ArrayList<ServiceModel>();

    private ArrayList<ServiceModel> getStringFromServiceList() {
        headers.clear();
        ArrayList<ServiceModel> result = new ArrayList<ServiceModel>();
        if (serviceListDO == null) {
            getServiceList();
        } else
            for (RegistrationEightResponseModel g : serviceListDO) {
                result.add(new ServiceModel(g.getName(), ""));
                headers.add(new ServiceModel(g.getName(), g.getId()));
                for (ServiceModel m : g.getServiceList())
                    result.add(new ServiceModel(m.getName(), m.getId()));
            }
        headers.add(new ServiceModel("Image Request", "68"));
        headers.add(new ServiceModel("Chat Request", "66"));
        return result;
    }


    private String getValidToken() {
        if (YummiUtils.token != null)
            return YummiUtils.token;

        if (first_response_per != null)
            return first_response_per.getClient().getUserToken().getUserId();

        if (first_response != null)
            return first_response.getClient().getUserToken().getUserId();

        return "";
    }

    private void getServiceList() {

        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
        Call<List<RegistrationEightResponseModel>> call = null;

        call = service.getServiceListByGender(gender, getValidToken());

        call.enqueue(new Callback<List<RegistrationEightResponseModel>>() {
            @Override
            public void onResponse(Call<List<RegistrationEightResponseModel>> call, Response<List<RegistrationEightResponseModel>> response) {
                serviceListDO = response.body();
                if (response.body() != null)
                    for (RegistrationEightResponseModel g : serviceListDO) {
                        for (ServiceModel m : g.getServiceList())
                            services.add(new ServicePushModel(0, m.getId()));
                    }
            }

            @Override
            public void onFailure(Call<List<RegistrationEightResponseModel>> call, Throwable t) {
                showAllert("Network Error", "Could not connect to the server");
            }
        });
    }

    private void getGenderList() {
        if (genderModelList == null) {
            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
            Call<List<GenderModel>> call = null;

            call = service.getGenderList(getValidToken());


            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    genderModelList = response.body();
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                    showAllert("Network Error", "Could not connect to the server");
                }
            });
        }
    }

    private void getHairList() {
        if (hairModelList == null) {
            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = null;


            call = service.getHairColor(getValidToken());

            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    hairModelList = response.body();
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                    showAllert("Network Error", "Could not connect to the server");
                }
            });
        }
    }


    private void getBodyList() {
        if (bodyModelList == null) {
            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = null;


            call = service.getBodyType(getValidToken());


            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    bodyModelList = response.body();
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                    showAllert("Network Error", "Could not connect to the server");
                }
            });
        }
    }

    private void getBustList() {
        if (bustModelList == null) {
            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);

            Call<List<GenderModel>> call = null;


            call = service.getBustSize(getValidToken());


            call.enqueue(new Callback<List<GenderModel>>() {
                @Override
                public void onResponse(Call<List<GenderModel>> call, Response<List<GenderModel>> response) {
                    bustModelList = response.body();
                }

                @Override
                public void onFailure(Call<List<GenderModel>> call, Throwable t) {
                    showAllert("Network Error", "Could not connect to the server");
                }
            });
        }
    }

    final private String stageNameUniq = "STAGENAME_UNIQUENESS";
    final private String clientExist = "CLIENT_EMAIL_UNIQUENESS";
    final private String clientBlockerd = "CLIENT_BLOCKED";
    final private String clientDosntExist = "EMAIL_DOESNT_EXIST";

    final private String pExist = "PERFORMER_EMAIL_UNIQUENESS";
    final private String pBlockerd = "PERFORMER_BLOCKED";
//
//
//                case .clientExist:
//                return "This e-mail address already exists"
//                case .clientBlockerd:
//                return "Your account has been disabled. please contact the administrator"
//                case .clientDosntExist:
//                return "Client does not exist"
//


    public void showResponseError(final String error) {

        if (error.contains(clientExist)) {
            showAllert("Error", "This e-mail address already exists");
        }

        if (error.contains(stageNameUniq)) {
            showAllert("Error", "This Stage name already exists");
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


    }


    private void logIn(final boolean isPreformer) {

        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);
        if (validField(username.getText().toString(), emailRegex) && password.getText().toString().length() > 6) {
            YummiUtils.deviceToken = sharedPreferences.getString("device_token_string", "");
            user = new LoginModel(username.getText().toString().replace(" ", "").toLowerCase(), password.getText().toString(), sharedPreferences.getString("device_token_string", ""));
            LoginModel user = new LoginModel(username.getText().toString().replace(" ", "").toLowerCase(), password.getText().toString(), sharedPreferences.getString("device_token_string", ""));
            if (!isPreformer) {
                NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
                Call<LoginResponseModel> call = service.createUser(user);
                call.enqueue(new Callback<LoginResponseModel>() {
                    @Override
                    public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
//                    showAllert("Success", "Hello id:" + response.body().getUserId());
                        if (response.body() != null) {
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("token", response.body().getId());
                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token_acces", response.body().getId());
                            editor.apply();

                            intent.putExtra("userId", response.body().getUserId());
                            YummiUtils.client = response.body().getClient();
                            YummiUtils.token = response.body().getId();
                            getAllLists();
                            startActivity(intent);
//                        removeView();
                            finish();
                        } else {
                            removeView();

                            if (response.errorBody() != null) {
                                try {
                                    showResponseError(response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else
                                showAllert("Error", "Error during Logging in");
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                        showAllert("Network Error", "Could not connect to the server");
                        removeView();
                    }
                });
            } else {
                loginPerformer(user);
            }
        } else {
            showAllert("Error", "Please correct username or password");
            removeView();
        }
    }


    private void loginPerformer(LoginModel user) {
        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
        Call<PerformerLoginResponseModel> call = service.perCreateUser(user);
        call.enqueue(new Callback<PerformerLoginResponseModel>() {
            @Override
            public void onResponse(Call<PerformerLoginResponseModel> call, Response<PerformerLoginResponseModel> response) {
                if (response.body() != null) {
                    first_response_per = new RegistrationFirstResponsePerformerModel(new RegFirstClient("", response.body().getUserId(), "1", Integer.valueOf(response.body().getPerformer().getSignUpStep()), null));
                    gender = response.body().getPerformer().getGenderId();


                    YummiUtils.token = response.body().getId();
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token_acces", response.body().getId());
                    editor.apply();
                    getAllLists();
                    if (serviceListDO == null) {
                        getServiceList();
                    }
                    switch (response.body().getPerformer().getSignUpStep()) {
                        case 0: {
                            registrationStepTwo(true);
                            break;
                        }
                        case 1: {
                            registrationStepThreeClient(true);
                            break;
                        }
                        case 2: {
                            registrationStepFour();
                            break;
                        }
                        case 3: {
                            registrationStepFifth();
                            break;
                        }
                        case 4: {
                            registrationStepSixth();
                            break;
                        }
                        case 5: {
                            registrationStepSeventh();
                            break;
                        }
                        case 6: {
                            registrationBankAccount();
                            break;
                        }

                        case 8: {
                            registrationStepEighth();
                            break;
                        }
                        case 7: {
                            registrationStepEighth();
                            break;
                        }

                        case 9: {
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("token", response.body().getId());
                            intent.putExtra("userId", response.body().getUserId());
                            YummiUtils.performer = response.body().getPerformer();
                            startActivity(intent);
//                        removeView();
                            finish();

                            break;
                        }
                        default: {

                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            intent.putExtra("token", response.body().getId());
                            intent.putExtra("userId", response.body().getUserId());
                            YummiUtils.performer = response.body().getPerformer();
                            startActivity(intent);
//                        removeView();
                            finish();

                            break;
                        }

                    }


                } else {
                    removeView();
                    if (response.errorBody() != null) {
                        try {
                            showResponseError(response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else
                        showAllert("Error", "Error during Logging in");
                }
            }

            @Override
            public void onFailure(Call<PerformerLoginResponseModel> call, Throwable t) {
                showAllert("Network Error", "Could not connect to the server");
                removeView();
            }
        });
    }


    private void
    logInUser(final boolean isPreformer) {
        if (!isPreformer) {
            NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
            Call<LoginResponseModel> call = service.createUser(user);
            call.enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
//                    showAllert("Success", "Hello id:" + response.body().getUserId());
                    if (response.body() != null) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        intent.putExtra("token", response.body().getId());
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token_acces", response.body().getId());
                        editor.apply();
                        intent.putExtra("userId", response.body().getUserId());
                        YummiUtils.client = response.body().getClient();
                        YummiUtils.token = response.body().getId();
                        getAllLists();
                        startActivity(intent);
//                        removeView();
                        finish();
                    } else {
                        removeView();

                        if (response.errorBody() != null) {
                            try {
                                showResponseError(response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            showAllert("Error", "Error during Logging in");
                    }

                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    showAllert("Network Error", "Could not connect to the server");
                    removeView();
                }
            });
        } else {
            loginPerformer(user);
        }
    }

    public static int getServicePosition(String id) {


        //services.get(position).getAvailability()
        int i = 0;
        for (ServicePushModel spm : WelcomeActivity.services) {
            if (spm.getServiceId().equalsIgnoreCase(id))
                return i;
            i++;
        }


        return i;
    }


    Pattern ptr = Pattern.compile("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

    //    private String emailRegex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private String emailRegex = "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)";
    private String usernameRegex = "^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
//"^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$"

    private String nameRegex = "[/^[a-zA-Z ,.'-]+$/i]*";

    private String passwordRegex = "^(.{0,7}|[^0-9]*|[^A-Z]*)$";


    public boolean validateEmail(String emailStr) {
        Matcher matcher = ptr.matcher(emailStr);
        return matcher.find();
    }

    private boolean validField(final String fieldText, final String regularExpression) {
        Pattern pattern = Pattern.compile(regularExpression);
        return pattern.matcher(fieldText).matches();
    }

    private boolean validPassword(final String passWord, final String confPassWord) {
        Pattern pattern = Pattern.compile(passwordRegex);
        return passWord.equalsIgnoreCase(confPassWord) && !pattern.matcher(passWord).matches();
    }

    public void showAllert(final String title, final String msg) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setPositiveButton("OK", null);

            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(msg);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            //exception if it wants to show an message when the screen is dissmissed
            e.printStackTrace();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
        Cursor cursor = managedQuery(uri, projection, null, null, null);
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
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

            // SDK > 19 (Android 4.4)
        else
            realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

        return realPath;
    }

}
