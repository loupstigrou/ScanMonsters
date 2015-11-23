package prog_mobile.uqac.com.scanmonsters;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Set;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité principale après connexion
 */
public class ScanMonsterActivity extends AppCompatActivity {

    SessionManager session;
    Intent serviceIntent;

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

        this.serviceIntent = new Intent(this, CreatureEventService.class);
        this.startService(serviceIntent);

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
        this.stopService(serviceIntent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.session.checkLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (this.session.isLoggedIn())
            getMenuInflater().inflate(R.menu.menu_logged_in, menu);
        else
            getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

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

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning !");
            alertDialog.setMessage("Are you sure you want to Log Out ?");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Log Out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.logoutUser();
                }
            });
            alertDialog.show();

        } else if (id == R.id.menu_infos) {
            Intent intent = new Intent(getApplicationContext(), PlayersBoardActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
