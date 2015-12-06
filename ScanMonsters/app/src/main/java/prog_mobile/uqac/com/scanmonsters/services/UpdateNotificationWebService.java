package prog_mobile.uqac.com.scanmonsters.services;

import android.content.Context;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Created by Benjamin on 06/12/2015.
 */
public class UpdateNotificationWebService extends BasicService {


    public UpdateNotificationWebService(Context context, SessionManager session, int idNotif, int state){
        super(context, session,
                "updateNotification",
                "&id=" + idNotif+
                        "&state="+state
        );
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Toast.makeText(context, "Success " + serverResponse, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }




    public static final void sendInviteFriend(SessionManager session, String loginInvitation)
    {

    }


}
