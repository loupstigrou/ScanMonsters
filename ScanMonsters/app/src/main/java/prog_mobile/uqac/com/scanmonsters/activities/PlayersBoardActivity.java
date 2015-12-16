package prog_mobile.uqac.com.scanmonsters.activities;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.playerboard.TabsPagerAdapter;

/**
 * Activité qui contient le Leader Board +
 * La liste des joueurs présents dans l'UQAC
 * Séparés en 2 tab
 */
public class PlayersBoardActivity extends InGameActivity implements ActionBar.TabListener{

    private ViewPager viewPager;
    private TabsPagerAdapter adapter;
    private android.support.v7.app.ActionBar actionBar;
    private String[] tabs = {"Users in UQAC", "Leader Board"};

    private GetPlayersTask gpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_board);

        this.session.checkLogin();

        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        adapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        gpt = new GetPlayersTask(this);
        gpt.execute((Void) null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayersBoardActivity.this, ScanMonsterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * Méthode de tri d'une Map par valeur
     * dans l'ordre décroissant pour des valeures entieres dans
     * notre cas
     * @param map => La map à trier
     * @return => La map triée
     */
    private static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    protected void onDestroy() {
        if(gpt != null) gpt.cancel(true);
        super.onDestroy();
    }







    /**
     * Tâche asyncrone qui va envoyer 2 requêtes au webservice
     * afin de récupérer la liste des joueurs de l'UQAC +
     * le Leader Board afin de modifier les Vues en conséquence
     */
    public class GetPlayersTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String playersInUqac;
        private String leaderBoard;

        public GetPlayersTask(Context context) {
            this.context = context;
            this.playersInUqac = "";
            this.leaderBoard = "";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                // First request //
                urlParameters = "getIsInUQAC=";

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
                    this.playersInUqac += inStream.nextLine();

                connection.disconnect();

                // Second request //
                urlParameters = "getLeaderBord=" +
                        "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8");

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                out = new PrintWriter(connection.getOutputStream());
                out.print(urlParameters);
                out.close();

                inStream = new Scanner(connection.getInputStream());

                while (inStream.hasNextLine())
                    this.leaderBoard += inStream.nextLine();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            TableLayout usersInUqacView = (TableLayout) findViewById(R.id.container_section1);
            TableLayout leaderBoardView = (TableLayout) findViewById(R.id.table_leader_board);

            String allPlayersData[];
            int lg;
            String playerData[];

            // User In UQAC Tab //
            ArrayList<String> users = new ArrayList<>();
            allPlayersData = playersInUqac.split(",");
            lg = allPlayersData.length;
            for (int i=0; i<lg; i++) {
                playerData = allPlayersData[i].split("-");
                users.add(playerData[0]);
            }

            for (String userInUqac : users) {
                if (!user.getLogin().equals(userInUqac)) {
                    TextView tv = new TextView(this.context);
                    tv.setText(userInUqac);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                    tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(0, 0, 0, 35);
                    tv.setLayoutParams(lp);
                    usersInUqacView.addView(tv);
                }
            }

            // Leader Board Tab //
            Map<String, Integer> scores = new HashMap<>();
            allPlayersData = this.leaderBoard.split(",");
            lg = allPlayersData.length;



            for (int i=1; i<lg; i++) {
                playerData = allPlayersData[i].split("-");
                scores.put(
                        playerData[0],
                        Integer.valueOf(playerData[3])
                );
            }
            HashMap<String, Integer> scoresSorted = (HashMap) sortByValue(scores);

            for (Map.Entry<String, Integer> entry : scoresSorted.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                TableRow row = new TableRow(this.context);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

                TextView pseudo = new TextView(this.context);
                pseudo.setText(key);
                pseudo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                pseudo.setLayoutParams(new TableRow.LayoutParams(1));
                if (key.equalsIgnoreCase(user.getLogin()))
                    pseudo.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent));
                else
                    pseudo.setTextColor(R.color.secondary_text);
                TextView score = new TextView(this.context);
                score.setText(String.valueOf(value));
                score.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                if (key.equalsIgnoreCase(user.getLogin()))
                    score.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.accent));
                else
                    score.setTextColor(R.color.secondary_text);

                row.addView(pseudo);
                row.addView(score);
                leaderBoardView.addView(row);

                View separationLine = new View(this.context);
                separationLine.setBackgroundColor(R.color.divider);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 5, 0, 5);
                lp.height = 1;
                separationLine.setLayoutParams(lp);

                leaderBoardView.addView(separationLine);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            gpt = null;
        }
    }
}
