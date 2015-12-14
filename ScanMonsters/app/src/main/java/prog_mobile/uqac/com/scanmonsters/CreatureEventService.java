package prog_mobile.uqac.com.scanmonsters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Random;

public class CreatureEventService extends Service {

    private int notificationID = 2;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Runnable creatureAppearance = new Runnable() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_pets_white_24dp)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                        .setContentTitle(getString(R.string.creature_event_notif_title))
                        .setContentText(getString(R.string.creature_event_notif_content))
                        .setLights(0xFFFFFFFF, 500,500)
                        .setAutoCancel(true);
                Intent resultIntent = new Intent(getApplicationContext(), ScanMonsterActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(ScanMonsterActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);

                notificationManager.notify(notificationID, builder.build());
            }
        };

        Handler creatureEvent = new Handler();
        creatureEvent.postDelayed(creatureAppearance, new Random().nextInt(28_800_000)); // 8 heures

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
