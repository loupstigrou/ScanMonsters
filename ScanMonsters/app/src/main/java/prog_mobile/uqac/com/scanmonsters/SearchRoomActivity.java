package prog_mobile.uqac.com.scanmonsters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
 * Activité de recherche du prochain objectif (salle et créature)
 */
public class SearchRoomActivity extends InGameActivity {


    private View progressView;
    private View roomFoundedView;

    private TextView roomValueText;
    private Button arriveAtRoomButton;

    private SearchRoomTask searchRoomTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        this.progressView =    (View) findViewById(R.id.wait_room_info);
        this.roomFoundedView = (View) findViewById(R.id.search_room_info);

        this.roomValueText = (TextView) findViewById(R.id.room_value_description);
        this.arriveAtRoomButton = (Button) findViewById(R.id.arrive_at_room_button);

        this.arriveAtRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arriveAtRoom();
            }
        });

        searchRoomTask = new SearchRoomTask(this, session);
        searchRoomTask.execute((Void) null);
    }


    private void arriveAtRoom()
    {
        Intent intent = new Intent(SearchRoomActivity.this, MiniGameActivity.class);
        startActivity(intent);
    }

    /**
     * Tâche asyncrone qui va se connecter au service pour
     * capturer la créature et l'ajouter à la liste
     */
    public class SearchRoomTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String serverResponse;
        private SessionManager session;

        public SearchRoomTask(Context context, SessionManager session) {
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
                        "requestType=getRoom" +
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

            return !this.serverResponse.equals("NOK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            searchRoomTask = null;
            if (success) {
                String infos[] = serverResponse.split("=");
                String roomId = infos[0];
                String creatureId = infos[1];
                session.setObjective(roomId, creatureId);

                showProgress(false, progressView, roomFoundedView);
                roomValueText.setText(roomId);
            } else {
                //loginView.setError(getString(R.string.error_wrong_login_or_password));
                Toast.makeText(context, "Erreur "+serverResponse, Toast.LENGTH_SHORT).show();
            }
        }




        @Override
        protected void onCancelled() {
            searchRoomTask = null;
            showProgress(true, progressView, roomFoundedView);
        }
    }
}
