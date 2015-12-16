package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Async Task qui va récupérer les joueurs autour de nous
 */
public class GetUsersAroundService extends BasicService {

    public GetUsersAroundService(Context context, SessionManager session, IServiceCallback callback, double latitude, double longitude) {
        super(context, session,
                "getUsersAround",
                "&lat="+latitude+
                        "&lng="+longitude
        );
        _callback = callback;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
    }

    public String getServerResponse() {
        return serverResponse;
    }
}
