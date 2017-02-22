package com.almabranding.yummi.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.almabranding.yummi.Apis.NetworkCallApiInterface;
import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.WelcomeActivity;
import com.almabranding.yummi.models.location.LocationPushModel;
import com.almabranding.yummi.utils.YummiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ioshero on 26/05/16.
 */
public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private boolean isRunning = false;


    @Override
    public void onCreate() {

        super.onCreate();
        isRunning = true;
        Log.e("LOCATION", "Running");
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(LocationUpdateService.this)
                .addOnConnectionFailedListener(LocationUpdateService.this)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(WelcomeActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String userId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle b = intent.getExtras();
        userId = b.getString("id");
        if (userId.isEmpty()) {
            userId = YummiUtils.getUserId(getBaseContext());
        }

        Log.e("LOCATION", "START COMM");


        mGoogleApiClient.connect();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.e("LOCATION", "Destroy");
        isRunning = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 600000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 5;


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("LOCATION", "Connected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);//600000
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
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
    public void onConnectionSuspended(int i) {
        Log.e("LOCATION", "onConnectionSuspended");
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void onLocationChanged(Location location) {
//        Double lat1 = location.getLatitude();
//        Double lng1 = location.getLongitude();
        Log.e("LOCATION", "lat:" + String.valueOf(location.getLatitude()) + " long:" + String.valueOf(location.getLongitude()));
        if (location != null)
            sendLocation(location.getLatitude(), location.getLongitude());

    }

    public Retrofit retrofit;


    private void sendLocation(double lat, double lng) {

        NetworkCallApiInterface service = retrofit.create(NetworkCallApiInterface.class);
        Log.e("LOCATION", "lat: " + String.valueOf(lat) + " long: " + String.valueOf(lng) + " id: " + userId);

        if (userId == null) {
            userId = YummiUtils.getUserId(getBaseContext());
        }

        if (userId != null)
            if (userId.isEmpty()) {
                userId = YummiUtils.getUserId(getBaseContext());
            } else {
                Call<ResponseBody> call = null;
                YummiUtils.token = YummiUtils.getAccesToken(getBaseContext());
                if (!YummiUtils.token.isEmpty())
                    if (YummiUtils.isPreformer(getBaseContext())) {
                        call = service.postLocationPer(userId, new LocationPushModel(lat, lng, userId), YummiUtils.token);

                        if (call != null)
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    Log.e("LOCATION", "succes");
                                    try {
                                        if (response.body() != null) {
                                            Log.e("LOCATION", response.body().string());
                                        }

                                        if (response.errorBody() != null) {
                                            Log.e("LOCATION", response.errorBody().string());
                                            userId = YummiUtils.getUserId(getBaseContext());

                                        }
                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("LOCATION", "failure" + t.toString());
                                }
                            });
                    }
            }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOCATION", "onConnectionFailed");
    }
}
