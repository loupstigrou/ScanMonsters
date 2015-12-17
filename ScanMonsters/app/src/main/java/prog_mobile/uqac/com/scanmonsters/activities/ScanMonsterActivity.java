package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import prog_mobile.uqac.com.scanmonsters.gcm.QuickstartPreferences;
import prog_mobile.uqac.com.scanmonsters.gcm.RegistrationIntentService;
import prog_mobile.uqac.com.scanmonsters.services.LocationService;
import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


/**
 * Activité principale après connexion
 */
public class ScanMonsterActivity extends InGameActivity {

    Intent locationServiceIntent;

    private TextView connectedText;
    private MySQLiteHelper mySQLiteHelper;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

//    private static final int REQUEST_ENABLE_BT = 1;
//
//    String devices = "";
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            // When discovery finds a device
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                // Add the name and address to an array adapter to show in a ListView
//                devices += device.getName() + device.getAddress() + "\n";
//                Toast.makeText(getApplicationContext(), devices, Toast.LENGTH_LONG).show();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_monster);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();

        this.locationServiceIntent = new Intent(this, LocationService.class);
        this.startService(locationServiceIntent);

        this.connectedText = (TextView) findViewById(R.id.scan_monster_description);

        String msgConnected = String.format(getResources().getString(R.string.scan_monster_description), session.getUser().getLogin());
        connectedText.setText(msgConnected);

        // Service pour s'abonner aux notifications push venant du serveur
        if (checkPlayServices()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            if (!sentToken) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

//        Intent discoverableIntent = new
//                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);

//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter != null) {
//            if (!bluetoothAdapter.isEnabled()) {
//                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            } else {
//                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//                String devices = "";
//                if (pairedDevices.size() > 0) {
//
//                    for (BluetoothDevice device : pairedDevices) {
//                        devices += device.getName() + device.getAddress() + "\n";
//                    }
//
//                } else {
//                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                    registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//                }
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        this.stopService(locationServiceIntent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.session.checkLogin();
    }

    public void goToInformationActivity(View v) {
        Intent intent = new Intent(ScanMonsterActivity.this, PlayersBoardActivity.class);
        startActivity(intent);
    }

    public void goToFriendsActivity(View v){
        Intent intent = new Intent(ScanMonsterActivity.this, FriendsListActivity.class);
        startActivity(intent);
    }

    public void goToCreaturesActivity(View v) {
        Intent intent = new Intent(ScanMonsterActivity.this, CreaturesListActivity.class);
        startActivity(intent);
    }

    public void goToPlayActivity(View v) {
        Intent intent = new Intent(ScanMonsterActivity.this, SearchRoomActivity.class);
        startActivity(intent);
    }



    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("ScanMonstersActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
