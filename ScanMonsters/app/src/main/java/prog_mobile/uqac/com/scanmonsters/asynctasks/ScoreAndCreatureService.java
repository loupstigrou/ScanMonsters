package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Async Task qui va récupérer le score et les créatures que possède l'utilisateur
 */
public class ScoreAndCreatureService extends BasicService {

    public ScoreAndCreatureService(Context context, SessionManager session, IServiceCallback callback) {
        super(context, session,
                "getScoreAndCreatures",
                ""
        );

        _callback = callback;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        if (!success) {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }

    public String getServerResponse() {
        return serverResponse;
    }
}
