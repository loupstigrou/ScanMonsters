package prog_mobile.uqac.com.scanmonsters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button inscriptionButton;
    private Button connectionButton;
    private Button cheatButton;
    private Button tesseractButton;
    private Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.inscriptionButton = (Button) findViewById(R.id.inscription_button);
        this.connectionButton = (Button) findViewById(R.id.connection_button);
        this.cheatButton = (Button) findViewById(R.id.cheat_button);
        this.tesseractButton = (Button) findViewById(R.id.tesseract_button);
        this.locationButton = (Button) findViewById(R.id.location_button);

        this.inscriptionButton.setOnClickListener(this);
        this.connectionButton.setOnClickListener(this);
        this.cheatButton.setOnClickListener(this);
        this.tesseractButton.setOnClickListener(this);
        this.locationButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inscription_button:
                this.goToInscriptionActivity();
                break;

            case R.id.connection_button:
                this.goToConnectionActivity();
                break;

            case R.id.cheat_button:
                this.goToWhateverActivity();
                break;

            case R.id.tesseract_button:
                this.goToTesseractActivity();
                break;

            case R.id.location_button:
                this.goToLocationActivity();
                break;

            default:
                break;
        }
    }

    private void goToInscriptionActivity() {
        Intent intent = new Intent(MainActivity.this, InscriptionActivity.class);
        startActivity(intent);
    }

    private void goToConnectionActivity() {
        Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
        startActivity(intent);
    }

    private void goToWhateverActivity() {
        Intent intent = new Intent(MainActivity.this, ScanMonsterActivity.class);
        startActivity(intent);
    }

    private void goToTesseractActivity(){
        Intent intent = new Intent(MainActivity.this, OCRActivity.class);
        startActivity(intent);
    }

    private void goToLocationActivity() {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
    }
}
