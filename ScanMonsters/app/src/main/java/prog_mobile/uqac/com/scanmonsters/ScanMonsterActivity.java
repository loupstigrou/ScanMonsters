package prog_mobile.uqac.com.scanmonsters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;

public class ScanMonsterActivity extends Activity {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_monster);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();
        HashMap<String, String> user = this.session.getUserDetails();

        String login = user.get(SessionManager.KEY_LOGIN);
        Toast.makeText(getApplicationContext(), "User : "+login, Toast.LENGTH_LONG).show();
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
            this.session.logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }
}
