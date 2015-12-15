package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class SetGCMTokenService extends BasicService {


    public SetGCMTokenService(Context context, SessionManager session, String tokenGCM, IServiceCallback callback) {
        super(context, session,
                "setGCMToken",
                "&tk=" + tokenGCM
        );
        _callback = callback;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Toast.makeText(context, "GCM TOKEN : ok", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }
}
