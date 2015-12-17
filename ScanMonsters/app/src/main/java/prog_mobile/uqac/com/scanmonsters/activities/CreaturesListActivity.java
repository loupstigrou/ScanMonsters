package prog_mobile.uqac.com.scanmonsters.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.CreaturesGridAdapter;
import prog_mobile.uqac.com.scanmonsters.asynctasks.IServiceCallback;
import prog_mobile.uqac.com.scanmonsters.asynctasks.OfferCreatureService;
import prog_mobile.uqac.com.scanmonsters.asynctasks.ScoreAndCreatureService;
import prog_mobile.uqac.com.scanmonsters.database.Creature;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.database.Notification;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité contenant les créatures possédées
 */
public class CreaturesListActivity extends InGameActivity implements CreaturesGridAdapter.CreatureAdapterListener, IServiceCallback {

    private CreaturesGridAdapter _creaturesGridAdapter;
    private GridView _creaturesGrid;

    private ScoreAndCreatureService getCreatures;
    private OfferCreatureService offerCreatureService;

    private List<Friend> allFriends;


    private View _progressView;
    private TextView _creaturesInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatures_list);

        _creaturesGrid = (GridView) findViewById(R.id.creatures_list);
        _progressView = (View) findViewById(R.id.wait_creature_loading_progress);
        _creaturesInfoText = (TextView) findViewById(R.id.creatures_info);

        _creaturesGridAdapter = new CreaturesGridAdapter(this, session);
        _creaturesGrid.setAdapter(_creaturesGridAdapter);
        _creaturesGridAdapter.addListener(this);

        _creaturesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        _creaturesInfoText.setText("Chargement en cours..");
        // Load creatures from server
        this.getCreatures = new ScoreAndCreatureService(this, session, this);
        getCreatures.execute((Void) null);
    }

    @Override
    public void onReceiveData(boolean success, String data) {
        // List des créatures possédées par l'utilisateur
        if(success == false) return;

        Log.i("CreaturesList", data);

        if (!data.equals("NOK")) {
            String[] dataCreatures = data.split("=");
            String[] currentCreatureData;

            if (!dataCreatures[2].equals("EMPTY")) {
                dataCreatures = dataCreatures[2].split(",");

                Creature tmpCreature;
                for (int i = 0; i < dataCreatures.length; i++) {
                    currentCreatureData = dataCreatures[i].split("-");
                    int creatureId = Integer.parseInt(currentCreatureData[0]);
                    long date = Integer.parseInt(currentCreatureData[1]);
                    int quantity = Integer.parseInt(currentCreatureData[2]);

                    tmpCreature = new Creature(creatureId, "", quantity);
                    tmpCreature.date = date;
                    _creaturesGridAdapter.add(tmpCreature);
                }
                _creaturesGridAdapter.notifyDataSetInvalidated();

                _creaturesInfoText.setText(_creaturesGridAdapter.getNbCreatures()+"/"+_creaturesGridAdapter.getCount()+" créatures différentes possédées.");
                _progressView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClickItem(final Creature currentCreature, int position) {
        if(currentCreature == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String popupTitle = "A qui offrir la créature n°"+currentCreature.id+"?\n";
        if(currentCreature.quantity > 1) popupTitle = popupTitle+"("+currentCreature.quantity+" exemplaires disponibles)";

        builder.setTitle(popupTitle);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.select_dialog_singlechoice
        );

        if(allFriends == null)
        {
            MySQLiteHelper datasource = new MySQLiteHelper(this);
            datasource.open();
            allFriends = datasource.getAllFriends();
            datasource.close();
        }


        int nbFriends = allFriends.size();
        if(nbFriends == 0)
        {
            builder.setMessage("Vous n'avez pas encore d'amis :/");
        }
        else {
            for (Friend tmpFriend : allFriends) {
                arrayAdapter.add(tmpFriend.name);
            }
        }

        builder.setNegativeButton("Annuler",null);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                String message = "Offre de la créature n°" + (currentCreature.id) + " à " + strName;
                Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT).show();
                sendOfferCreatureRequest(strName, (currentCreature.id) + "");
            }
        });
        builder.show();
    }

    private void sendOfferCreatureRequest(String name, String dataCreature) {
        if(offerCreatureService == null || offerCreatureService.finished())
        {

            try {
                name = URLEncoder.encode(name, "UTF-8");
                dataCreature = URLEncoder.encode(dataCreature, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                name = "";
                dataCreature = "";
            }

            offerCreatureService = new OfferCreatureService(this, session, name, Notification.CREATURE_EXCHANGE_REQUEST, dataCreature);
            offerCreatureService.execute();
        }
        else
        {
            Toast.makeText(this, "Erreur : Patiente un peu", Toast.LENGTH_SHORT).show();
        }
    }
}
