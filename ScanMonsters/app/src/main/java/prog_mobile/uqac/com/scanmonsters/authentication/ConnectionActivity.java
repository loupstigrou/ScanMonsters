package prog_mobile.uqac.com.scanmonsters.authentication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Pattern;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.ScanMonsterActivity;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité de Connexion
 * (login + mdp)
 */
public class ConnectionActivity extends AppCompatActivity {

    SessionManager session;

    private EditText loginView;
    private EditText passwordView;
    private Button loginButton;
    private View progressView;
    private View loginFormView;

    private UserLoginTask userLoginTask = null;

    // Les expressions régulières que doivent matcher les différents champs en entrées
    private static final Pattern loginPattern = Pattern.compile("^[a-z0-9_-]{3,15}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern passwordPattern = Pattern.compile("[a-z0-9]{2,19}", Pattern.CASE_INSENSITIVE);

    private static final String webserviceURL = "http://miralud.com/progMobile/webservice.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        this.session = new SessionManager(getApplicationContext());
        if (this.session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), ScanMonsterActivity.class);
            startActivity(intent);
            finish();
        }

        this.loginView = (EditText) findViewById(R.id.connection_login);
        this.passwordView = (EditText) findViewById(R.id.connection_password);

        this.loginButton = (Button) findViewById(R.id.signup_button);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedLogin();
            }
        });

        // Appui sur le bouton Se connecter sur le clavier
        this.passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.connection || actionId == EditorInfo.IME_NULL) {
                    proceedLogin();
                    return true;
                }
                return false;
            }
        });

        this.progressView = findViewById(R.id.connection_login_progress);
        this.loginFormView = findViewById(R.id.connection_login_form);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.session.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), ScanMonsterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Méthode qui va effectuer toute les vérifications
     * (Champs rempli, longueur et regex correct)
     * Et tenter la connexion si c'est bon
     */
    private void proceedLogin() {
        if (userLoginTask != null)
            return;

        this.loginView.setError(null);
        this.passwordView.setError(null);

        String login = this.loginView.getText().toString();
        String password = this.passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Vérification de la validité
        if (TextUtils.isEmpty(login)) {
            loginView.setError(getString(R.string.error_field_required));
            focusView = loginView;
            cancel = true;
        } else if (!isLoginValid(login)) {
            loginView.setError(getString(R.string.error_invalid_login));
            focusView = loginView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            userLoginTask = new UserLoginTask(this, login, password);
            userLoginTask.execute((Void) null);
        }
    }

    private boolean isLoginValid(CharSequence email) {
        return this.loginPattern.matcher(email).matches();
    }

    private boolean isPasswordValid(CharSequence password) {
        return this.passwordPattern.matcher(password).matches() && password.length() < 20;
    }

    /**
     * Animation de progression durant la connexion
     * @param show
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Tâche asyncrone qui va se connecter au service pour
     * vérifier que le login et le password correspondent
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private User user;
        private String serverResponse;

        public UserLoginTask(Context context, String login, String password) {
            this.context = context;
            this.serverResponse = "";
            this.user = new User(login, password);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                urlParameters =
                        "requestType=connexion" +
                                "&login=" + URLEncoder.encode(this.user.getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(this.user.getPassword(), "UTF-8");

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
                    this.serverResponse += (inStream.nextLine());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return this.serverResponse.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userLoginTask = null;
            showProgress(false);

            if (success) {
                session.createLoginSession(user);
                Intent intent = new Intent(this.context, ScanMonsterActivity.class);
                context.startActivity(intent);
                finish();
            } else {
                loginView.setError(getString(R.string.error_wrong_login_or_password));
                passwordView.setText(null);
                loginView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress(false);
        }
    }
}
