package prog_mobile.uqac.com.scanmonsters.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.FriendListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.asynctasks.GetFriendsService;
import prog_mobile.uqac.com.scanmonsters.asynctasks.IServiceCallback;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

public class FriendsListActivity extends InGameActivity implements FriendListAdapter.FriendAdapterListener, IServiceCallback {


    private MySQLiteHelper datasource;
    private FriendListAdapter adapter;

    private View progressView;
    private View friendListView;
    private View groupListView;

    private GetFriendsService getFriendsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();

        this.progressView   = (View) findViewById(R.id.wait_search_info);
        this.friendListView = (View) findViewById(R.id.friendsList);
        this.groupListView = (View) findViewById(R.id.listGroup);

        datasource = new MySQLiteHelper(this);
        datasource.open();

        List<Friend> allFriends = datasource.getAllFriends();
        int nbFriends = allFriends.size();
        if(nbFriends == 0) reloadFriends(); // ou si cookie needToReloadFriends

        adapter = new FriendListAdapter(this, allFriends);
        adapter.addListener(this);
        ListView list = (ListView)findViewById(R.id.friendsList);

        list.setAdapter(adapter);
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {


            case R.id.refresh:
                reloadFriends();
                break;

            case R.id.search:
                intent = new Intent(this, SearchFriendsActivity.class);
                startActivity(intent);
                break;

            /*case R.id.localise:
                intent = new Intent(this, LocateGoogleMapActivity.class);
                startActivity(intent);
                break;*/
        }
        adapter.notifyDataSetChanged();
    }

    private void reloadFriends() {
        if(getFriendsService == null || getFriendsService.finished())
        {
            getFriendsService = new GetFriendsService(this, session, this);
            getFriendsService.execute();
            showProgress(true, progressView, groupListView);
        }
        else
        {
            Toast.makeText(this, "Erreur : Patiente un peu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveData(boolean success, String data) {

        showProgress(false, progressView, groupListView);
        if(!success)
        {
            // Erreur
        }
        else if(data.equals("NO_FRIENDS"))
        {
            Toast.makeText(this, "Pas encore d'amis", Toast.LENGTH_SHORT).show();
            datasource.deleteAllFriends();
            adapter.setmListP(new ArrayList<Friend>());
            adapter.notifyDataSetInvalidated();
        }
        else if(!data.equals(""))
        {
            datasource.deleteAllFriends();
            String allPlayersData[];
            int lg;

            allPlayersData = data.split(",");
            lg = allPlayersData.length;
            Friend tmpFriend;
            for (int i=0; i<lg; i++) {
                tmpFriend = new Friend();
                tmpFriend.fromRawData(allPlayersData[i]);
                datasource.addFriend(tmpFriend);
            }

            adapter.setmListP(datasource.getAllFriends());
            adapter.notifyDataSetInvalidated();
        }
    }

    public void onClickNom(Friend item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.name);

        builder.setMessage("Vous avez cliqué sur : " + item.name);
        builder.show();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        datasource.close();
        super.onDestroy();
    }


}