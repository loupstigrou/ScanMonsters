package prog_mobile.uqac.com.scanmonsters.playerboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import prog_mobile.uqac.com.scanmonsters.R;

/**
 * Created by Major on 21/11/2015.
 */
public class UsersInUqacFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_users_in_uqac, container, false);
        return rootView;
    }
}
