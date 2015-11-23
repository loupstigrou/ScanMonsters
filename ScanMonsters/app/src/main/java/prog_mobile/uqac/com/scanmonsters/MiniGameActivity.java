package prog_mobile.uqac.com.scanmonsters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Activité de capture de la créature
 */
public class MiniGameActivity extends InGameActivity {

    private ProgressBar lifeProgressBar;
    private View canvasCreature;
    private Button captureButton;

    private CaptureCreatureTask captureCreatureTask;

    private int creatureLifeMax = 100;
    private int creatureLife = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game);

        this.lifeProgressBar = (ProgressBar) findViewById(R.id.progressbar_life);
        this.canvasCreature = (View) findViewById(R.id.canvasImageCreature);

        this.captureButton = (Button) findViewById(R.id.capture_button);

        this.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attack();
            }
        });
    }

    private void attack() {
        creatureLife -= 20;
        if(creatureLife < 1)
        {
            creatureLife = 0;
            updateProgressBar();
            proceedCapture();
            return;
        }
        updateProgressBar();
    }

    private void updateProgressBar() {
        int percent = creatureLife*100/creatureLifeMax;
        lifeProgressBar.setMax(creatureLifeMax);
        lifeProgressBar.setProgress(percent);
    }

    private void proceedCapture() {
        if(captureCreatureTask != null) return;
        showProgress(true);
        captureCreatureTask = new CaptureCreatureTask(this, session);
        captureCreatureTask.execute((Void) null);
    }

    /**
     * Tâche asyncrone qui va se connecter au service pour
     * capturer la créature et l'ajouter à la liste
     */
    public class CaptureCreatureTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String serverResponse;
        private SessionManager session;

        public CaptureCreatureTask(Context context, SessionManager session) {
            this.context = context;
            this.serverResponse = "";
            this.session = session;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                urlParameters =
                        "requestType=addScore" +
                                "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8"); //

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
            captureCreatureTask = null;
            showProgress(false);

            if (success) {
                /*Intent intent = new Intent(this.context, ScanMonsterActivity.class);
                context.startActivity(intent);
                finish();*/
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            } else {
                //loginView.setError(getString(R.string.error_wrong_login_or_password));
                Toast.makeText(context, "Erreur", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            captureCreatureTask = null;
            showProgress(false);
        }
    }
}
