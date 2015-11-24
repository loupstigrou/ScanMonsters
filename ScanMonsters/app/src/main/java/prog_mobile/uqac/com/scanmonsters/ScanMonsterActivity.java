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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité principale après connexion
 */
public class ScanMonsterActivity extends InGameActivity implements View.OnClickListener  {

    Intent serviceIntent;

    private Button cheatButton;
    private Button tesseractButton;
    private Button locationButton;
    private Button miniGameButton;
    private TextView connectedText;

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


        this.cheatButton = (Button) findViewById(R.id.cheat_button);
        this.tesseractButton = (Button) findViewById(R.id.tesseract_button);
        this.locationButton = (Button) findViewById(R.id.location_button);
        this.miniGameButton = (Button) findViewById(R.id.mini_game_button);
        this.connectedText = (TextView) findViewById(R.id.scan_monster_description);

        this.cheatButton.setOnClickListener(this);
        this.tesseractButton.setOnClickListener(this);
        this.locationButton.setOnClickListener(this);
        this.miniGameButton.setOnClickListener(this);

        String msgConnected = String.format(getResources().getString(R.string.scan_monster_description), session.getUser().getLogin());
        connectedText.setText(msgConnected);

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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cheat_button:
                this.goToWhateverActivity();
                break;

            case R.id.tesseract_button:
                this.goToTesseractActivity();
                break;

            case R.id.location_button:
                this.goToLocationActivity();
                break;

            case R.id.mini_game_button:
                this.goToMiniGameActivity();
                break;

            default:
                break;
        }
    }

    private void goToWhateverActivity() {
        Intent intent = new Intent(ScanMonsterActivity.this, PlayersBoardActivity.class);
        startActivity(intent);
    }

    private void goToTesseractActivity(){
        Intent intent = new Intent(ScanMonsterActivity.this, OCRActivity.class);
        startActivity(intent);
    }

    private void goToLocationActivity() {
        Intent intent = new Intent(ScanMonsterActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    private void goToMiniGameActivity() {
        Intent intent = new Intent(ScanMonsterActivity.this, MiniGameActivity.class);
        startActivity(intent);
    }


}
