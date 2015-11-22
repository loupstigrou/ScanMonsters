package prog_mobile.uqac.com.scanmonsters.playerboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Major on 21/11/2015.
 */

/**
 * Classe qui va s'occuper de la gestion des Tabs de
 * l'activité PlayersBoardActivity
 * Elle retourne le fragment correspondant à la tab
 */
public class TabsPagerAdapter extends FragmentPagerAdapter{

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UsersInUqacFragment();
            case 1:
                return new LeaderBoardFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
