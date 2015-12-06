package prog_mobile.uqac.com.scanmonsters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.adapters.FriendListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

public class SearchFriendsActivity extends InGameActivity implements FriendListAdapter.FriendAdapterListener {

    private SearchFriendTask searchFriendTask;

    private View indexView;
    private View progressView;
    private View friendListView;
    private TextView searchPseudoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();

        // View à l'arrivée sur l'activity
        this.indexView = (View) findViewById(R.id.wait_search_index);
        ((TextView)findViewById(R.id.wait_search_index_login)).setText(this.session.getUser().getLogin());
        _currentView = indexView;


        this.progressView =    (View) findViewById(R.id.wait_search_info);
        this.friendListView = (View) findViewById(R.id.friendsList);


        this.searchPseudoText = (TextView) findViewById(R.id.search_txt);

        searchFriendTask = null;

        // Appui sur le bouton Se connecter sur le clavier
        this.searchPseudoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.btn_search_ok || actionId == EditorInfo.IME_NULL) {
                    proceedSearch();
                    return true;
                }
                return false;
            }
        });

        /*adapter = new FriendListAdapter(this, allFriends);
        adapter.addListener(this);
        ListView list = (ListView)findViewById(R.id.friendsList);

        list.setAdapter(adapter);*/
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        Friend friend = null;
        switch (view.getId()) {
            case R.id.btn_search_ok:
                proceedSearch();
                break;
        }
        //adapter.notifyDataSetChanged();
    }
    protected void proceedSearch() {
        String searchPseudo = searchPseudoText.getText().toString();
        if(searchFriendTask == null && searchPseudo != "" && searchPseudo.length() > 2 && !searchPseudo.contains("%"))
        {
            searchFriendTask = new SearchFriendTask(this, session);
            searchFriendTask.setPseudo(searchPseudo);
            searchFriendTask.execute((Void) null);
        }
        else
        {
            searchPseudoText.setError(getString(R.string.error_invalid_search_login));
        }
    }

    public void onClickNom(Friend item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.name);

        builder.setMessage("Vous avez cliqué sur : " + item.name);
        builder.setPositiveButton("Oui", null);
        builder.setNegativeButton("Non", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }@Override
    protected void onDestroy() {
        if(searchFriendTask != null) searchFriendTask.onCancelled();
        super.onDestroy();
    }


    /**
     * Tâche asyncrone qui va se connecter au service pour
     * récupérer les logins des joueurs demandés
     */
    public class SearchFriendTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String serverResponse;
        private SessionManager session;
        private String pseudo;

        public SearchFriendTask(Context context, SessionManager session) {
            this.context = context;
            this.serverResponse = "";
            this.session = session;
            showProgress(true, progressView, (indexView.getVisibility() == View.VISIBLE) ? indexView : friendListView);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                urlParameters =
                        "requestType=searchByName" +
                                "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8")+
                                "&name=" + URLEncoder.encode(pseudo, "UTF-8"); //

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
            searchFriendTask = null;
            if (success) {

                if(serverResponse.equals(""))
                {
                    searchPseudoText.setError(getString(R.string.error_no_user_to_show));
                    searchPseudoText.requestFocus();
                    showProgress(false, progressView, indexView);
                }
                else
                {

                    String allPlayersData[];
                    int lg;
                    String playerData[];

                    ArrayList<String> users = new ArrayList<>();
                    allPlayersData = serverResponse.split(",");
                    lg = allPlayersData.length;
                    for (int i = 0; i < lg; i++) {
                        playerData = allPlayersData[i].split("-");
                        users.add(playerData[0]);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, users);

                    ((ListView) friendListView).setAdapter(adapter);
                    showProgress(false, progressView, friendListView);
                }


            } else {
                Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
            }

            searchFriendTask = null;
        }




        @Override
        protected void onCancelled() {
            super.onCancelled();
            searchFriendTask = null;
            showProgress(true, progressView, indexView);
        }

        public void setPseudo(String pseudo) {
            this.pseudo = pseudo;
        }
    }
}


