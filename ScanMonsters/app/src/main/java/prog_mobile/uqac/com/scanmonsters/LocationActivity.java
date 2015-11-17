package prog_mobile.uqac.com.scanmonsters;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

public class LocationActivity extends Activity implements LocationListener {

    SessionManager session;

    private LocationManager lm;
    private double latitude;
    private double longitude;

    //48.420048, -71.052508 : latitude et longitude du centre du batiment principal
    private static double LATITUDE_CENTER = 48.420048;
    private static double LONGITUDE_CENTER = -71.052508;
    private static int DISTANCE_ACCURACY = 300; // distance in meters acceptable between the center and the user to assume the user is in the zone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        this.session = new SessionManager(getApplicationContext());
//        this.session.checkLogin();
//        HashMap<String, String> user = this.session.getUserDetails();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        String msg = "";

        // If the user is in the University => ok
        if (userInZone(latitude, longitude))
            msg = "ok";
        else
            msg = "nok";
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

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
}
