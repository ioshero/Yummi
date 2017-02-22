package com.almabranding.yummi.services;

/**
 * Created by ioshero on 24/06/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.almabranding.yummi.MainActivity;
import com.almabranding.yummi.R;
import com.almabranding.yummi.utils.YummiUtils;
import com.google.android.gms.gcm.GcmListenerService;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyGcmListenerService extends GcmListenerService {


    final String imageRequest = "IMAGE_REQUEST";
    final String newNotification = "NEW_NOTIFICATION";
    final String newMessage = "NEW_MESSAGE";
    final String chatRequest = "CHAT_REQUEST";
    final String imageResponse = "IMAGE_RESPONSE";

    private static final String TAG = "MyGcmListenerService";


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //  String message = data.getString("message");
        String title = data.getString("title");
        String category = data.getString("category");
        try {
            ShortcutBadger.applyCount(getBaseContext(), Integer.valueOf(data.getString("badge_count")));
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.e(TAG, "From: " + from);

        if (!YummiUtils.isInForeground)
            switch (category) {
                case imageRequest: {
                    String clientName = data.getString("clientName");
                    YummiUtils.push_action_id =  data.getString("requestId");
                    YummiUtils.push_action_param_1 =  data.getString("clientName");
                    YummiUtils.push_action = 1;
                    sendNotification("", title, category);
                    break;
                }

                case newNotification: {
                    YummiUtils.push_action_id =  data.getString("eventId");
                    YummiUtils.push_action_param_1 =  data.getString("eventName");
                    YummiUtils.push_action = 4;
                    sendNotification("", title, category);
                    break;
                }

                case newMessage: {
                    String clientName = data.getString("clientName");
                    YummiUtils.push_action_id = data.getString("chatId");
                    YummiUtils.push_action = 3;
                    sendNotification("", title, category);
                    break;
                }

                case chatRequest: {
                    YummiUtils.push_action_id = data.getString("chatId");
                    YummiUtils.push_action = 2;
                    sendNotification("", title, category);
                    break;
                }


                case imageResponse: {

                    break;
                }


                default: {
                    break;
                }
            }

//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */


        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title, final String category) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                0);
        Notification notification = new Notification();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSmallIcon(R.mipmap.push2ico)
                .setColor( 0xffa38e31)
//                .setLargeIcon(R.mipmap.push2ico)
                .setContentTitle(title)
                .setContentText("")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(defaultSoundUri)

                .setContentIntent(pendingIntent);


        switch (category){
            case imageRequest:{
                notificationBuilder.addAction(R.mipmap.photo_icon,"Take a shot", pendingIntent);
                break;
            }

            case chatRequest:{
                notificationBuilder.addAction(R.mipmap.button_icon_chatg,"Start a chat", pendingIntent);
                break;
            }

            case newMessage:{
                notificationBuilder.addAction(R.mipmap.button_icon_chatg,"Reply", pendingIntent);
                break;
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
