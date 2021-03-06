package prog_mobile.uqac.com.scanmonsters.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité avec menu de base
 */
public class InGameActivity extends AppCompatActivity {

    public SessionManager session;
    public User user;
    public static final String webserviceURL = "http://miralud.com/progMobile/webservice.php";

    protected View _currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        if (!session.checkLogin()) {
            finish();
        }
        user = session.getUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!session.checkLogin()) {
            finish();
        }
    }




    /**
     * Animation de progression durant la connexion
     * @param show
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View progressView, final View currentView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            currentView.setVisibility(show ? View.GONE : View.VISIBLE);
            currentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            currentView.setVisibility(show ? View.GONE : View.VISIBLE);
            _currentView = currentView;
        }
    }

    /**
     * Menu with a logout option if the user is logged in
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (this.session.isLoggedIn())
            getMenuInflater().inflate(R.menu.menu_logged_in, menu);

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
        if (id == R.id.menu_logout) {

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getText(R.string.warning).toString());
            alertDialog.setMessage(getText(R.string.confirm_logout).toString());
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.no).toString(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.yes).toString(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.logoutUser();
                }
            });
            alertDialog.show();
            return super.onOptionsItemSelected(item);

        } else if (id == R.id.menu_infos) {
            Intent intent = new Intent(getApplicationContext(), PlayersBoardActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_friends) {
            Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
            startActivity(intent);
        }else if (id == R.id.menu_notifications) {
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_creatures) {
            Intent intent = new Intent(getApplicationContext(), CreaturesListActivity.class);
            startActivity(intent);
        }
        if(!(this instanceof ScanMonsterActivity))
            finish();

        return super.onOptionsItemSelected(item);
    }
}
