package prog_mobile.uqac.com.scanmonsters.asynctasks;

import android.content.Context;
import android.widget.Toast;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class SetRecordLocationService extends BasicService {

    private boolean _recordLocation;
    private double _latitude;
    private double _longitude;

    public SetRecordLocationService(Context context, SessionManager session, boolean recordLocation, double latitude, double longitude) {
        super(context, session,
                "setLocation",
                "&showLocation=" + ((recordLocation)?1:0) +
                        "&lat="+latitude+
                        "&lng="+longitude
        );

        _recordLocation = recordLocation;
        _latitude = latitude;
        _longitude = longitude;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        if (success) {
            if(_recordLocation)
                Toast.makeText(context, "Position partagée : "+_latitude+" , "+_longitude, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Partage de position arrêté", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erreur " + serverResponse, Toast.LENGTH_SHORT).show();
        }

    }
}
