package prog_mobile.uqac.com.scanmonsters.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.NotificationsListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Notification;
import prog_mobile.uqac.com.scanmonsters.asynctasks.BasicService;
import prog_mobile.uqac.com.scanmonsters.asynctasks.UpdateNotificationWebService;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

public class NotificationsActivity extends InGameActivity implements NotificationsListAdapter.NotificationsAdapterListener {

    private GetNotificationsTask getNotificationsTask;
    private UpdateNotificationWebService updateNotificationWebService;

    private View progressView;
    private ListView notificationsListView;

    private Button receivedNotifsButton;
    private Button sendedNotifsButton;

    private NotificationsListAdapter adapter;
    private Notification currentNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        this.session = new SessionManager(getApplicationContext());
        this.session.checkLogin();

        this.progressView =    (View) findViewById(R.id.wait_notifications);
        this.notificationsListView = (ListView) findViewById(R.id.notificationsList);

        this.receivedNotifsButton = (Button) findViewById(R.id.received);
        this.sendedNotifsButton = (Button) findViewById(R.id.sended);

        receivedNotifsButton.setAlpha(0.5f);
        sendedNotifsButton.setAlpha(1f);

        adapter = new NotificationsListAdapter(this, session);
        adapter.addListener(this);
        notificationsListView.setAdapter(adapter);

        proceedSearch();
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loading_notifications:
                proceedSearch();
                break;
            case R.id.received:
                adapter.filterBy(NotificationsListAdapter.FILTER_RECEIVED);
                receivedNotifsButton.setAlpha(0.5f);
                sendedNotifsButton.setAlpha(1f);
                break;
            case R.id.sended:
                adapter.filterBy(NotificationsListAdapter.FILTER_SENDED);
                receivedNotifsButton.setAlpha(1f);
                sendedNotifsButton.setAlpha(0.5f);
                break;
        }
    }
    protected void proceedSearch() {

        if(getNotificationsTask == null)
        {
            getNotificationsTask = new GetNotificationsTask(this, session);
            getNotificationsTask.execute((Void) null);
        }
        else
        {
            Toast.makeText(this, "Erreur : Patiente un peu", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickNom(final Notification notification, int position) {
        currentNotification = notification;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(notification.getDisplayableType());

        if(notification.imHost && (notification.state == 2 || notification.state == 3))
        {
            builder.setMessage("Souhaites tu archiver la requête de " + notification.getLoginOther(session.getUser().getLogin()) + " ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    answerRequest(4);
                }
            });
            builder.setNegativeButton("Non", null);
        }
        else if(!notification.imHost && (notification.state == 0 || notification.state == 1))
        {
            builder.setMessage("Souhaites tu accepter la requête de " + notification.getLoginOther(session.getUser().getLogin()) + " ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    answerRequest(2);
                }
            });
            builder.setNegativeButton("Non",  new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    answerRequest(3);
                }
            });
        }
        else
        {
            builder.setMessage("Requête en attente....");
            builder.setNeutralButton("OK", null);
        }

        builder.show();
    }

    private void answerRequest(int stateWanted) {
        if(updateNotificationWebService == null || updateNotificationWebService.finished())
        {
            updateNotificationWebService = new UpdateNotificationWebService(this, session, currentNotification.id, stateWanted, "Action effectuée");
            updateNotificationWebService.execute();

            adapter.remove(currentNotification);
            currentNotification.state = stateWanted;
            currentNotification.dateLastUpdate = System.currentTimeMillis()/1000;

            if(stateWanted == 2 || stateWanted == 3)
            {
                adapter.add(currentNotification);
            }
            else if(stateWanted == 4){

            }
            adapter.sortArrays();
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
    }@Override
     protected void onDestroy() {
        if(getNotificationsTask != null) getNotificationsTask.onCancelled();
        super.onDestroy();
    }


    /**
     * Tâche asyncrone qui va se connecter au service pour
     * récupérer les dernières notifications
     */
    public class GetNotificationsTask extends BasicService {

        public GetNotificationsTask(Context context, SessionManager session) {
            super(context, session,
                    "getNotifications", "" );
            showProgress(true, progressView, notificationsListView);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getNotificationsTask = null;
            if (success) {

                if(serverResponse.equals(""))
                {
                    showProgress(false, progressView, notificationsListView);
                    Toast.makeText(context, "Pas de notifications",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    String allNotifsData[];
                    int lg;
                    adapter.clear();
                    ArrayList<String> users = new ArrayList<>();
                    allNotifsData = serverResponse.split(",");
                    lg = allNotifsData.length;
                    Notification tmpNotif;
                    for (int i = 0; i < lg; i++) {
                        tmpNotif = new Notification();
                        tmpNotif.fromRawData(allNotifsData[i]);
                        adapter.add(tmpNotif);
                    }
                    adapter.notifyDataSetInvalidated();
                    showProgress(false, progressView, notificationsListView);

                    //Toast.makeText(context, serverResponse,Toast.LENGTH_LONG).show();
                    System.out.println(serverResponse);
                }


            } else {
                Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
            }

            getNotificationsTask = null;
        }




        @Override
        protected void onCancelled() {
            super.onCancelled();
            getNotificationsTask = null;
            showProgress(true, progressView, notificationsListView);
        }
    }
}


