package prog_mobile.uqac.com.scanmonsters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

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
                        .setContentTitle("A creature has appeared !")
                        .setContentText(String.format("You can find it at the room %s", "P4-2350"))
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
        creatureEvent.postDelayed(creatureAppearance, 2000 + new Random().nextInt(8000));

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
