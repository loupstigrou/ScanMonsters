package prog_mobile.uqac.com.scanmonsters;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Random;

import prog_mobile.uqac.com.scanmonsters.adapters.FriendListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

public class FriendsListActivity extends InGameActivity implements FriendListAdapter.FriendAdapterListener {


    private MySQLiteHelper datasource;
    private FriendListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();

        datasource = new MySQLiteHelper(this);
        datasource.open();

        List<Friend> allFriends = datasource.getAllFriends();

        adapter = new FriendListAdapter(this, allFriends);
        adapter.addListener(this);
        ListView list = (ListView)findViewById(R.id.friendsList);

        list.setAdapter(adapter);
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        Friend friend = null;
        switch (view.getId()) {
            case R.id.add:
                String[] names = new String[] { "Jerome", "Wolfy", "Yolo" };
                int nextInt = new Random().nextInt(3);
                // enregistrer le nouveau commentaire dans la base de données
                friend = datasource.addFriend(new Friend(names[nextInt], 5));
                adapter.add(friend);
                break;
            case R.id.delete:
                if (adapter.getCount() > 0) {
                    friend = (Friend) adapter.getItem(0);
                    datasource.deleteFriend(friend);
                    adapter.remove(friend);
                }
                break;
            case R.id.search:
                Intent intent = new Intent(this, SearchFriendsActivity.class);
                startActivity(intent);
                break;
        }
        adapter.notifyDataSetChanged();
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
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}


