package prog_mobile.uqac.com.scanmonsters.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.FriendListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.Notification;
import prog_mobile.uqac.com.scanmonsters.asynctasks.BasicService;
import prog_mobile.uqac.com.scanmonsters.asynctasks.SetNotificationWebService;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

public class SearchFriendsActivity extends InGameActivity implements FriendListAdapter.FriendAdapterListener {

    private SearchFriendTask searchFriendTask;
    private SetNotificationWebService setNotificationWebService;

    private View indexView;
    private View progressView;
    private ListView friendListView;
    private View groupListView;

    private TextView searchPseudoText;

    private FriendListAdapter adapter;
    private Friend currentFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        // View à l'arrivée sur l'activity
        this.indexView = (View) findViewById(R.id.wait_search_index);
        ((TextView)findViewById(R.id.wait_search_index_login)).setText(this.session.getUser().getLogin());
        _currentView = indexView;


        this.progressView =    (View) findViewById(R.id.wait_search_info);
        this.friendListView = (ListView) findViewById(R.id.friendsList);
        this.groupListView = (View) findViewById(R.id.listGroup);


        this.searchPseudoText = (TextView) findViewById(R.id.search_txt);

        searchFriendTask = null;

        // Appui sur le bouton Rechercher sur le clavier
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

        adapter = new FriendListAdapter(this);
        adapter.addListener(this);
        friendListView.setAdapter(adapter);
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_search_ok:
                proceedSearch();
                break;
            case R.id.btn_search_by_location:
                intent = new Intent(this, LocateGoogleMapActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_search_by_proximity:
                intent = new Intent(this, NearbyActivity.class);
                startActivity(intent);
                break;
        }
    }
    protected void proceedSearch() {
        String searchPseudo = searchPseudoText.getText().toString();
        try {
            searchPseudo = URLEncoder.encode(searchPseudo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            searchPseudo = "";
        }
        if(searchFriendTask == null && searchPseudo != "" && searchPseudo.length() > 2 && !searchPseudo.contains("%"))
        {

            searchFriendTask = new SearchFriendTask(this, session, searchPseudo);
            searchFriendTask.execute((Void) null);
        }
        else
        {
            searchPseudoText.setError(getString(R.string.error_invalid_search_login));
        }
    }

    public void onClickNom(Friend item, int position) {
        currentFriend = item;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.name);

        builder.setMessage("Envoyer une demande d'amis à " + item.name + " ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                sendFriendRequest(currentFriend.name);
            }
        });
        builder.setNegativeButton("Non", null);
        builder.show();
    }

    private void sendFriendRequest(String name) {
        if(setNotificationWebService == null || setNotificationWebService.finished())
        {

            try {
                name = URLEncoder.encode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                name = "";
            }

            setNotificationWebService = new SetNotificationWebService(this, session, name, Notification.FRIEND_REQUEST, "");
            setNotificationWebService.execute();
        }
        else
        {
            Toast.makeText(this, "Erreur : Patiente un peu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
     protected void onDestroy() {
        if(searchFriendTask != null) searchFriendTask.onCancelled();
        super.onDestroy();
    }


    /**
     * Tâche asyncrone qui va se connecter au service pour
     * récupérer les logins des joueurs demandés
     */
    public class SearchFriendTask extends BasicService {

        protected String pseudo;

        public SearchFriendTask(Context context, SessionManager session, String pseudo) {
            super(context, session,
                    "searchByName", "&name=" + pseudo );
            this.pseudo = pseudo;
            showProgress(true, progressView, (indexView.getVisibility() == View.VISIBLE) ? indexView : groupListView);

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
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
                    adapter.clear();
                    ArrayList<String> users = new ArrayList<>();
                    allPlayersData = serverResponse.split(",");
                    lg = allPlayersData.length;
                    Friend tmpFriend;
                    for (int i = 0; i < lg; i++) {
                        tmpFriend = new Friend();
                        tmpFriend.fromRawData(allPlayersData[i]);
                        adapter.add(tmpFriend);
                    }
                    adapter.notifyDataSetInvalidated();
                    showProgress(false, progressView, groupListView);
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
    }
}


