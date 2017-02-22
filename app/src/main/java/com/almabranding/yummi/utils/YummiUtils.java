package com.almabranding.yummi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.almabranding.yummi.R;
import com.almabranding.yummi.models.BankModel;
import com.almabranding.yummi.models.ClientModel;
import com.almabranding.yummi.models.PerformerModel;
import com.almabranding.yummi.models.chat.ChatMessageModel;
import com.almabranding.yummi.models.chat.ChatModel;

import java.util.ArrayList;

/**
 * Created by ioshero on 06/05/16.
 */
public class YummiUtils {


//    public static void putIsClient(final boolean isClient, Context c){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
//        final SharedPreferences.Editor editor = sharedPreferences.edit();
//    }

    public static PerformerModel performer = null;

    public static BankModel bankModel = null;

    public static String token = null;

    private static Location location = null;

    public static int chatPrice = 0;
    public static int imagePrice = 0;


    //    <uses-permission android:name="com.almabranding.yummi.permission.MAPS_RECEIVE" />
//    <uses-permission android:name="android.permission.INTERNET" />
//    <uses-permission android:name="android.permission.CAMERA" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//    <uses-permission android:name="android.permission.CALL_PHONE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
//    <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
//
//
//
//    <uses-permission android:name="android.permission.ACCESS_GPS" />
//    <uses-permission android:name="android.permission.ACCESS_LOCATION" />
//    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_EXTERNAL_LOC = 2;
    public static final int REQUEST_EXTERNAL_OTH = 3;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    private static String[] PERMISSIONS_OTHERS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE

    };
    public static String deviceToken = "";


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public static int push_action = -1;

    public static String push_action_id = "";
    public static String push_action_param_1 = "";


    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_LOC
            );
        }
    }

    public static void verifyOtherPermissions(Activity activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_OTHERS,
                    REQUEST_EXTERNAL_OTH
            );
        }
    }


    public static int unreaded_notif_count = 0;
    public static int unreaded_message_count = 0;

    public static Location getLocation() {
        return location;
    }

    public static void setLocation(Location location) {
        YummiUtils.location = location;
    }

    public static ClientModel client = null;

    public static boolean isPreformer(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getBoolean("isPreformer", false);
    }


    public static String getUserId(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getString("aaa_userId", "");
    }

    public static String getAccesToken(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getString("aaa_token", "");
    }

    public static String currentlyOpenedEvent = "";

    public static String currentlyOpenedChat = "";

    public static ArrayList<ChatMessageModel> currentlyOpenedChatresult = null;


    public static boolean isClient = true;

    /*
    {
      where: {
        age: {between: [XX, XX]},
        height: {between: [XX, XX]},
        bustSizeId: {inq: [XX,…]},
        bodyTypeId: {inq: [XX,…]},
        genderId: {inq: [XX,…]},
        hairColorId: {inq: [XX,…]}
      },
      location: [XX, XX],
      distance: XX
    }
     */
    public static String setUpFilter(SharedPreferences sp) {
        String filter = "";
        filter += "{\"where\": { \"age\": {\"between\": [";
        filter += String.valueOf(sp.getLong("filter_age_min", 17)) + ", ";
        filter += String.valueOf(sp.getLong("filter_age_max", 50)) + "]},";

        filter += String.valueOf(" \"height\": {\"between\": [" + (double)sp.getLong("filter_height_min", 140)/100) + ", ";
        filter += String.valueOf((double)sp.getLong("filter_height_max", 220)/100) + "]}";


//        averagePrice
        filter += String.valueOf(", \"averagePrice\": {\"between\": [" + sp.getLong("filter_price_min", 1999)) + ", ";
        filter += String.valueOf(sp.getLong("filter_price_max", 100)) + "]}";


        if (!sp.getString("filter_bust", "").isEmpty())
            filter += String.valueOf(", \"bustSizeId\": {\"inq\": [\"" + sp.getString("filter_bust", "")) + "\"]} ";
        if (!sp.getString("filter_body", "").isEmpty())
            filter += String.valueOf(", \"bodyTypeId\": {\"inq\": [\"" + sp.getString("filter_body", "")) + "\"]} ";
        if (!sp.getString("filter_gender", "").isEmpty())
            filter += String.valueOf(", \"genderId\": {\"inq\": [\"" + sp.getString("filter_gender", "")) + "\"]} ";
        if (!sp.getString("filter_hair", "").isEmpty())
            filter += String.valueOf(", \"hairColorId\": {\"inq\": [\"" + sp.getString("filter_hair", "")) + "\"]} ";


        if(YummiUtils.getLocation() == null){
            filter += "}";
        }else {

            filter += "}, \"location\": [" + String.valueOf(YummiUtils.getLocation().getLongitude()) + ", " + String.valueOf(YummiUtils.getLocation().getLatitude() + "],");
            filter += "\"distance\": " + String.valueOf(sp.getLong("filter_dist_max", 1000)) + "} ";
        }
        return filter;
    }

    public static String getStatusDescription(final int status) {
        switch (status) {
            case 0:
                return "Pending";
            case 1:
                return "Waiting for Payment";
            case 2:
                return "Paid";
            case 5:
                return "Started";
            case 6:
                return "Finished";
            case 9:
                return "Cancelled";
            case 3:
                return "Ready to Start";

            default:
                return "Unknown status: " + String.valueOf(status);
        }
    }

    public static Bitmap scaleBitmap(Bitmap mBitmap) {
        try {
            int ScaleSize = 1000;//max Height or width to Scale
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            float excessSizeRatio = width > height ? (float) ((float) width / (float) ScaleSize) : (float) ((float) height / (float) ScaleSize);
            Bitmap bitmap = Bitmap.createScaledBitmap(
                    mBitmap, (int) (width / excessSizeRatio), (int) (height / excessSizeRatio), false);
            //mBitmap.recycle(); if you are not using mBitmap Obj
            return bitmap;


//            Matrix matrix = new Matrix();
//            // RESIZE THE BIT MAP
//            matrix.postScale(excessSizeRatio, excessSizeRatio);
//
//            // "RECREATE" THE NEW BITMAP
//            Bitmap resizedBitmap = Bitmap.createBitmap(
//                    mBitmap, 0, 0, width , height, matrix, false);
//
//            return resizedBitmap;


        } catch (Exception e) {
            e.printStackTrace();
            return mBitmap;
        }


    }


    public static boolean isInForeground = false;

    public static int getStatusColor(final int status) {
        switch (status) {
            case 0:
                return R.color.pendingYellow;
            case 1:
                return R.color.pendingYellow;
            case 2:
                return R.color.acceptedGreen;
            case 5:
                return R.color.blueColor;
            case 6:
                return R.color.lightGrayColor;
            case 9:
                return R.color.redColor;
            case 3:
                return R.color.acceptedGreen;

            default:
                return R.color.pendingYellow;
        }
    }


    public static int getStatusBackground(final int status) {
        switch (status) {
            case 0:
                return R.drawable.pending_oval_background;
            case 1:
                return R.drawable.pending_oval_background;
            case 2:
                return R.drawable.accepted_oval_background;
            case 5:
                return R.drawable.blue_oval_background;
            case 6:
                return R.drawable.finished_oval_background;
            case 9:
                return R.drawable.red_oval_background;
            case 3:
                return R.drawable.accepted_oval_background;

            default:
                return R.drawable.pending_oval_background;
        }
    }

    public static String getNotification(String type) {


        switch (type) {

            case "eventInvitation": {
                return "You have been invited to event %@";
            }

            case "eventIsReadyToBePaid": {
                return "Event is ready to be paid";
            }
            case "performerAdded": {
                return "New performer added to event %@";
            }

            case "cancelEventInvitation": {
                return "%@ event invitation has been canceled";
            }
            case "performerDeleted": {
                return "Performer has been removed from event %@";
            }
            case "performerAccepted": {
                return "Performer accept invitation for event %@";
            }
            case "performerRejected": {
                return "Performer reject invitation for event %@";
            }
            case "performerCheckedIn": {
                return "Performer is in event";
            }
            case "guestAdded": {
                return "You have been invited to event %@";
            }
            case "guestAccepted": {
                return "Guest accept invitation for event %@";
            }
            case "guestDeleted": {
                return "Guest has been removed from event %@";
            }
            case "eventStarted": {
                return "%@ event has been started";
            }
            case "eventCancelled": {
                return "%@ event has been canceled";
            }
            case "tipAdded": {
                return "%@ has added new Tip";
            }
            case "serviceAdded": {
                return "%@ new services added";
            }
            case "serviceDeleted": {
                return "Service was removed from event %@";
            }
            case "guestCheckedIn": {
                return "Guest is in place";
            }
            case "eventIsPaid": {
                return "The event was paid";
            }
            case "guestRejected": {
                return "Guest reject invitation for event %@";
            }
            case "undifined": {
                return "undefined notifiaction type";
            }
            case "newImageRequest": {
                return "You received image request from %@";
            }
            case "imageRequestReplayed": {
                return "You received new image";
            }

            default: {
                return type;
            }

        }


//        return "";
    }



    /*
    public enum ChatStatus: Int {
    case panding = 2
    case active = 5
    case finished = 6

    func getDescription() -> String{
        switch self {
        case .panding:
            return "Pending"
        case .active:
            return "Active"
        case .finished:
            return "Finished"
        }
    }

    func getColor() -> UIColor {
        switch self {
        case .panding:
            return UIColor.pendingYellow()
        case .active:
            return UIColor.acceptedGreen()
        case .finished:
            return UIColor.lightGrayColor()
        }
    }
}
     */


    public static int getStatusColorChats(final int status) {
        switch (status) {

            case 2:
                return R.drawable.pending_oval_background;
            case 5:
                return R.drawable.accepted_oval_background;
            case 6:
                return R.drawable.finished_oval_background;


            default:
                return R.drawable.pending_oval_background;
        }
    }

    public static String getStatusDescriptionChats(final int status) {
        switch (status) {

            case 2:
                return "Pending";
            case 5:
                return "Active";
            case 6:
                return "Finished";

            default:
                return "Unknown status: " + String.valueOf(status);
        }
    }

}

