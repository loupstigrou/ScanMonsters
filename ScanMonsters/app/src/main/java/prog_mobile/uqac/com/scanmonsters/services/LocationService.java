package prog_mobile.uqac.com.scanmonsters.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.activities.PlayersBoardActivity;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

public class LocationService extends Service implements LocationListener {

    private LocationManager lm;
    private double latitude;
    private double longitude;

    //48.420048, -71.052508 : latitude et longitude du centre du batiment principal
    private static double LATITUDE_CENTER = 48.420048;
    private static double LONGITUDE_CENTER = -71.052508;
    private static int DISTANCE_ACCURACY = 300; // distance in meters acceptable between the center and the user to assume the user is in the zone

    private static final String webserviceURL = "http://miralud.com/progMobile/webservice.php";
    private int notificationID = 1;
    private NotificationManager notificationManager;

    private SessionManager session;
    private User user;

    private boolean uiz;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        this.session = new SessionManager(getApplicationContext());
        this.user = this.session.getUser();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (this.uiz != userInZone(latitude, longitude)) {
            this.uiz = userInZone(latitude, longitude);
            UserLocationTask ult = new UserLocationTask(this.user, uiz);
            ult.execute((Void) null);
        }

        this.uiz = userInZone(latitude, longitude);

    }

    private boolean userInZone(double latitude, double longitude) {
        Location location1 = new Location("");
        location1.setLatitude(latitude);
        location1.setLongitude(longitude);
        Location location2 = new Location("");
        location2.setLatitude(LATITUDE_CENTER);
        location2.setLongitude(LONGITUDE_CENTER);

        double d = location1.distanceTo(location2);

        if (d <= DISTANCE_ACCURACY)
            return true;
        else
            return false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private class UserLocationTask extends AsyncTask<Void, Void, Boolean> {

        private User user;
        private String serverResponse;
        private String serverResponse2;
        private boolean isInUQAC;
        private ArrayList<String> usersInUQAC;

        public UserLocationTask(User user, boolean isInUQAC) {
            this.user = user;
            this.serverResponse = "";
            this.serverResponse2 = "";
            this.isInUQAC = isInUQAC;
            this.usersInUQAC = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                urlParameters =
                        "requestType=isInUQAC"+
                                "&login=" + URLEncoder.encode(user.getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(user.getPassword(), "UTF-8") +
                                "&value=" + URLEncoder.encode(String.valueOf(this.isInUQAC), "UTF-8");

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(urlParameters);
                out.close();

                Scanner inStream = new Scanner(connection.getInputStream());

                while (inStream.hasNextLine())
                    this.serverResponse += inStream.nextLine();

                inStream.close();

                if (!this.serverResponse.equals("OK"))
                    return false;

                connection.disconnect();

                urlParameters = "getIsInUQAC=";

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                out = new PrintWriter(connection.getOutputStream());
                out.print(urlParameters);
                out.close();

                inStream = new Scanner(connection.getInputStream());

                while (inStream.hasNextLine())
                    this.serverResponse2 += inStream.nextLine();

                connection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                String[] tabUsers = this.serverResponse2.split(",");
                for (int i=0; i<tabUsers.length; i++)
                    usersInUQAC.add(tabUsers[i].split("-")[0]);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_pets_white_24dp)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                        .setContentTitle(getString(R.string.location_notif_title))
                        .setContentText(String.format(getString(R.string.location_notif_content), usersInUQAC.size() - 1))
                        .setLights(0x0FFFFFF, 500, 500)
                        .setAutoCancel(true);
                Intent resultIntent = new Intent(getApplicationContext(), PlayersBoardActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(PlayersBoardActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);

                notificationManager.notify(notificationID, builder.build());
            }
        }
    }
}
