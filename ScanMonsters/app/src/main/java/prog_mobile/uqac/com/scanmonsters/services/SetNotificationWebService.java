package prog_mobile.uqac.com.scanmonsters.services;

import android.content.Context;
import android.widget.Toast;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class SetNotificationWebService extends BasicService {


        public SetNotificationWebService(Context context, SessionManager session, String recepteur){
            super(context, session,
                    "addNotification",
                    "&recepteur=" + recepteur+
                    "&typeNotif=0"+
                    "&dataNotif=0"
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
