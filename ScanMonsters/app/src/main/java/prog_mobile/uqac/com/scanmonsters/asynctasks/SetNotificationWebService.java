package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;
import android.widget.Toast;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class SetNotificationWebService extends BasicService {


    public SetNotificationWebService(Context context, SessionManager session, String recepteur, int typeNotification, String data) {
        super(context, session,
                "addNotification",
                "&recepteur=" + recepteur +
                        "&typeNotif="+typeNotification+
                        "&dataNotif="+data
        );
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            if(serverResponse.equals("ALREADY_FRIENDS"))
                Toast.makeText(context, "Vous êtes déjà amis !", Toast.LENGTH_SHORT).show();
            else if(serverResponse.equals("ALREADY_NOTIF"))
                Toast.makeText(context, "Demande d'amitié déjà en cours.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Requête envoyée !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }


    public static final void sendInviteFriend(SessionManager session, String loginInvitation) {

    }
}
