package prog_mobile.uqac.com.scanmonsters.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.CreaturesGridAdapter;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité temporaire pour gérer la localisation de l'utilisateur
 */
public class CreaturesListActivity extends InGameActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatures_list);

        final GridView creaturesGrid = (GridView) findViewById(R.id.creatures_list);
        creaturesGrid.setAdapter(new CreaturesGridAdapter(this, session));

        creaturesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
