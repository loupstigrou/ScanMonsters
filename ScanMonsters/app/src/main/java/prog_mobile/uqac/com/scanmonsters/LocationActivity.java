package prog_mobile.uqac.com.scanmonsters;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité temporaire pour gérer la localisation de l'utilisateur
 */
public class LocationActivity extends AppCompatActivity implements LocationListener {

    SessionManager session;

    private LocationManager lm;
    private double latitude;
    private double longitude;

    //48.420048, -71.052508 : latitude et longitude du centre du batiment principal
    private static double LATITUDE_CENTER = 48.420048;
    private static double LONGITUDE_CENTER = -71.052508;
    private static int DISTANCE_ACCURACY = 300; // distance in meters acceptable between the center and the user to assume the user is in the zone

    private User user;
    private static final String webserviceURL = "http://miralud.com/progMobile/webservice.php";
    private int notificationID = 1;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();
        this.user = this.session.getUser(); // Get the current user
        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        UserLocationTask getIsInUQAC = new UserLocationTask(this.user, "getIsInUQAC", false); // False or true does not matter when request is getIsInUQAC
        getIsInUQAC.execute((Void) null);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.session.checkLogin();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }

    /**
     * Menu with a logout option if the user is logged in
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (this.session.isLoggedIn())
            getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        else
            getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    /**
     * Logout functionnality
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_logout) {
            this.session.logoutUser();
        } else if (id == R.id.menu_infos) {
            Intent intent = new Intent(getApplicationContext(), PlayersBoardActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        String msg = "";

//        userLoginTask = new UserLoginTask(this, login, password);
//        userLoginTask.execute((Void) null);
        boolean uiz = userInZone(latitude, longitude);

        UserLocationTask ult = new UserLocationTask(this.user, "isInUQAC", uiz);
        ult.execute((Void) null);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "TEMPORARILY_UNAVAILABLE";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "AVAILABLE";
                break;
        }
        String msg = String.format(
                getResources().getString(R.string.provider_new_status), provider,
                newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        String msg = String.format(
                getResources().getString(R.string.provider_disabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        String msg = String.format(
                getResources().getString(R.string.provider_enabled), provider);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean userInZone(double latitude, double longitude) {
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

    public class UserLocationTask extends AsyncTask<Void, Void, Boolean> {

        private User user;
        private String request;
        private String serverResponse;
        private boolean isInUQAC;
        private ArrayList<String> usersInUQAC;

        public UserLocationTask(User user, String request, boolean isInUQAC) {
            this.user = user;
            this.request = request;
            this.serverResponse = "";
            this.isInUQAC = isInUQAC;
            this.usersInUQAC = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            if (request == "isInUQAC") {

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

                    if (this.serverResponse.equals("OK"))
                        return true;
                    else
                        return false;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (request == "getIsInUQAC") {
                try {
                    url = new URL(webserviceURL);
                    urlParameters = "getIsInUQAC=";

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

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                if (this.serverResponse.equals("OK")) {
                    //Toast.makeText(getApplicationContext(), "Location registered", Toast.LENGTH_SHORT).show();
                } else {
                    String[] tabUsers = this.serverResponse.split(",");
                    for (int i=0; i<tabUsers.length; i++)
                        usersInUQAC.add(tabUsers[i].split("-")[0]);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_explore_white_24dp)
                            .setColor(Color.argb(1, 41, 128, 185))
                            .setContentTitle("ScanMonster : Users in your area")
                            .setContentText(String.format("There are %d users in your area !", usersInUQAC.size() - 1))
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
}
