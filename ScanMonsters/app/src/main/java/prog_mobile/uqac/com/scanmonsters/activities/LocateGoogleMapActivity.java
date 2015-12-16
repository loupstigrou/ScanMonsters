package prog_mobile.uqac.com.scanmonsters.activities;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.asynctasks.GetUsersAroundService;
import prog_mobile.uqac.com.scanmonsters.asynctasks.IServiceCallback;
import prog_mobile.uqac.com.scanmonsters.asynctasks.SetRecordLocationService;

public class LocateGoogleMapActivity extends InGameActivity implements OnMapReadyCallback, IServiceCallback {

    private MapFragment mapFragment;
    private boolean isMapReady;
    private GoogleMap _map;

    private LatLng UQAC_LOCATION;


    private SetRecordLocationService setRecordLocationService;
    private GetUsersAroundService getUsersAroundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_google_map);

        UQAC_LOCATION = new LatLng(48.4199035, -71.0543764);


        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        isMapReady = true;
        _map = map;
        initDataOnMap();
        getUsersAround();
    }

    private void initDataOnMap()
    {
        _map.setMyLocationEnabled(true);
        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(UQAC_LOCATION, 13));
        _map.addMarker(new MarkerOptions()
                .title("UQAC")
                .snippet("Lieu regorgeant de créatures.")
                .position(UQAC_LOCATION));
    }


    public void onClick(View view) {
        if(!isMapReady) return;

        switch (view.getId()) {

            case R.id.btn_share_location:
                confirmRecordLocation();
                break;

            case R.id.btn_hide_location:
                stopLocation();
                break;
            case R.id.btn_reload_others_location:
                getUsersAround();
                break;
        }
    }

    private void confirmRecordLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partage de position");
        builder.setMessage("Es-tu sûr de vouloir partager ta position avec les autres joueurs de ScanMonsters ?");

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                recordMyLocation();
            }
        });
        builder.setNegativeButton("Non", null);
        builder.show();
    }

    private void recordMyLocation() {
        Location myLocation;
        try
        {
            myLocation = _map.getMyLocation();
            Log.i("MAP", myLocation.getLatitude() + " " + myLocation.getLongitude() + " " + myLocation.getAltitude());
        }
        catch(Exception e)
        {
            Toast.makeText(this,"Localisation impossible :/", Toast.LENGTH_LONG).show();
            return;
        }


        if(setRecordLocationService == null || setRecordLocationService.finished())
        {
            setRecordLocationService = new SetRecordLocationService(this, session, true, myLocation.getLatitude(), myLocation.getLongitude());
            setRecordLocationService.execute();
        }
    }
    private void stopLocation() {
        if(setRecordLocationService == null || setRecordLocationService.finished())
        {
            setRecordLocationService = new SetRecordLocationService(this, session, false, 0.0, 0.0);
            setRecordLocationService.execute();
        }
    }

    private void getUsersAround() {

        Location myLocation;
        double latitude = UQAC_LOCATION.latitude;
        double longitude = UQAC_LOCATION.longitude;
        try
        {
            myLocation = _map.getMyLocation();
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
            Log.i("MAP", myLocation.getLatitude() + " " + myLocation.getLongitude() + " " + myLocation.getAltitude());
        }
        catch(Exception e)
        {
            Toast.makeText(this,"Localisation par rapport à l'UQAC", Toast.LENGTH_LONG).show();
        }

        if(getUsersAroundService == null || getUsersAroundService.finished())
        {
            getUsersAroundService = new GetUsersAroundService(this, session, this, latitude, longitude);
            getUsersAroundService.execute();
        }
    }


    @Override
    public void onReceiveData(boolean success, String data) {
        //Toast.makeText(this,"Reponse : "+data, Toast.LENGTH_LONG).show();

        if(data.equals("EMPTY"))
        {
            Toast.makeText(this, "Personne n'est dans cette zone", Toast.LENGTH_SHORT).show();
        }
        else if(!data.equals(""))
        {
            _map.clear();
            initDataOnMap();

            String allPlayersData[];
            int lg;
            allPlayersData = data.split(",");
            lg = allPlayersData.length;

            String playerData[];

            for (int i=0; i<lg; i++) {
                playerData = allPlayersData[i].split(":");
                _map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_patte))
                        .title(playerData[0])
                        .snippet("Score : " + playerData[1])
                        .position(new LatLng(Double.parseDouble(playerData[2]), Double.parseDouble(playerData[3]))));
            }
        }
    }
}