package prog_mobile.uqac.com.scanmonsters.playerboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import prog_mobile.uqac.com.scanmonsters.R;

/**
 * Created by Major on 21/11/2015.
 */

/**
 * Fragment qui contient la vue pour le Leader Board
 */
public class LeaderBoardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);
        return rootView;
    }

}
