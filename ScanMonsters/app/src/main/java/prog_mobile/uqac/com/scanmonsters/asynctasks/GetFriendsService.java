package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Async Task qui va récupérer les amis que possède l'utilisateur
 */
public class GetFriendsService extends BasicService {

    public GetFriendsService(Context context, SessionManager session, IServiceCallback callback) {
        super(context, session,
                "getFriends",
                ""
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
