package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class OfferCreatureService extends BasicService {


    public OfferCreatureService(Context context, SessionManager session, String recepteur, int typeNotification, String data) {
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
            if(serverResponse.equals("CREATURE_NOT_AVAILABLE"))
                Toast.makeText(context, "Tu n'as pas cette créature :/", Toast.LENGTH_SHORT).show();
            else if(serverResponse.equals("ALREADY_NOTIF"))
                Toast.makeText(context, "Demande déjà en cours.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Créature bien envoyée !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }
}
