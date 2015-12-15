package prog_mobile.uqac.com.scanmonsters.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.activities.NotificationsActivity;
import prog_mobile.uqac.com.scanmonsters.activities.PlayersBoardActivity;
import prog_mobile.uqac.com.scanmonsters.activities.ScanMonsterActivity;

public class MyGcmListenerService extends GcmListenerService {

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
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Test: " + data.getString("test"));

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        sendNotification(message);

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
        //sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

        String[] receiveNotifData = message.split("\\|");

        int iconId;

        switch (receiveNotifData[0])
        {
            case "1": // ami
                iconId = R.mipmap.ic_new_friend;
                break;
            case "2": // cadeau
                iconId = R.mipmap.cadeau_blanc;
                break;
            case "0": // patte
            default:
                iconId = R.mipmap.ic_pets_white_24dp;
        }


        Class activityToOpen;

        switch (receiveNotifData[1])
        {
            case "1":
                activityToOpen = NotificationsActivity.class;
                break;
            case "2":
                activityToOpen = PlayersBoardActivity.class;
                break;
            case "0":
            default:
                activityToOpen = ScanMonsterActivity.class;
        }

        String titleToDisplay = receiveNotifData[2];
        String messageToDisplay = receiveNotifData[3];


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(iconId)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                .setContentTitle(titleToDisplay)
                .setContentText(messageToDisplay)
                .setLights(0x0009688, 1500, 500)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(getApplicationContext(), activityToOpen);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(activityToOpen);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}