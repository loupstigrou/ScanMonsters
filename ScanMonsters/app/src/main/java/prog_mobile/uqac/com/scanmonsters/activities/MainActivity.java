package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.authentication.ConnectionActivity;
import prog_mobile.uqac.com.scanmonsters.authentication.InscriptionActivity;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Activité principale
 * Connexion + Inscription depuis ici
 * Redirige automatiquement si l'utilisateur est déjà connecté (Désactivé en développement)
 * Contient des raccourcis pour les autres activités en dev
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SessionManager session;

    private Button inscriptionButton;
    private Button connectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.session = new SessionManager(getApplicationContext());
        // Directly redirect if user is already logged in
        if (this.session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), ScanMonsterActivity.class);
            startActivity(intent);
            finish();
        }


        this.inscriptionButton = (Button) findViewById(R.id.inscription_button);
        this.connectionButton = (Button) findViewById(R.id.connection_button);

        this.inscriptionButton.setOnClickListener(this);
        this.connectionButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
}
