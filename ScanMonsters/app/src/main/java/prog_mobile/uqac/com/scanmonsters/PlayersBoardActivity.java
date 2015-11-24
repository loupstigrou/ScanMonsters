package prog_mobile.uqac.com.scanmonsters;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import prog_mobile.uqac.com.scanmonsters.playerboard.TabsPagerAdapter;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * Activité qui contient le Leader Board +
 * La liste des joueurs présents dans l'UQAC
 * Séparés en 2 tab
 */
public class PlayersBoardActivity extends AppCompatActivity implements ActionBar.TabListener{

    SessionManager session;

    private ViewPager viewPager;
    private TabsPagerAdapter adapter;
    private android.support.v7.app.ActionBar actionBar;
    private String[] tabs = {"Users in UQAC", "Leader Board", "Score"};

    private static final String webserviceURL = "http://miralud.com/progMobile/webservice.php";

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_board);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();
        this.user = this.session.getUser();

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

        GetPlayersTask gpt = new GetPlayersTask(this);
        gpt.execute((Void) null);
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

    /**
     * Tâche asyncrone qui va envoyer 2 requêtes au webservice
     * afin de récupérer la liste des joueurs de l'UQAC +
     * le Leader Board afin de modifier les Vues en conséquence
     */
    public class GetPlayersTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String playersInUqac;
        private String leaderBoard;
        private String scoreData;

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

                // Third request
                /*urlParameters =
                        "requestType=getScoreAndCreatures" +
                                "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8"); //

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
                    this.scoreData += inStream.nextLine();*/

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

            for (String user : users) {
                TextView tv = new TextView(this.context);
                tv.setText(user);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                tv.setTextColor(Color.DKGRAY);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, 0, 0, 35);
                tv.setLayoutParams(lp);

                usersInUqacView.addView(tv);
            }

            // Leader Board Tab //
            Map<String, Integer> scores = new HashMap<>();
            allPlayersData = this.leaderBoard.split(",");
            lg = allPlayersData.length;



            for (int i=0; i<lg; i++) {
                playerData = allPlayersData[i].split("-");
                if(i == 0) // Le premier score retourné par le webservice est le mien
                {
                    // Own Score //
                    int ownScore = Integer.valueOf(playerData[3]);
                    TextView os = (TextView) findViewById(R.id.own_score);
                    //os.setText(String.valueOf(ownScore));
                }
                else
                {
                    scores.put(
                            playerData[0],
                            Integer.valueOf(playerData[3])
                    );
                }
            }
            HashMap<String, Integer> scoresSorted = (HashMap) sortByValue(scores);

            for (Map.Entry<String, Integer> entry : scoresSorted.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                TableRow row = new TableRow(this.context);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

                TextView pseudo = new TextView(this.context);
                pseudo.setText(key);
                pseudo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                pseudo.setLayoutParams(new TableRow.LayoutParams(1));
                pseudo.setTextColor(Color.BLACK);
                TextView score = new TextView(this.context);
                score.setText(String.valueOf(value));
                score.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                score.setTextColor(Color.BLACK);

                row.addView(pseudo);
                row.addView(score);
                leaderBoardView.addView(row);

                View separationLine = new View(this.context);
                separationLine.setBackgroundColor(Color.BLACK);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 5, 0, 5);
                lp.height = 1;
                separationLine.setLayoutParams(lp);

                leaderBoardView.addView(separationLine);
            }
        }
    }
}
