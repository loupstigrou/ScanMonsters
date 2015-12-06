package prog_mobile.uqac.com.scanmonsters.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.database.Friend;

/**
 * Created by Benjamin on 06/12/2015.
 */


public class FriendListAdapter extends BaseAdapter {

    private List<Friend> mListP;

    private Context mContext;

    //mécanisme pour gérer l'affichage graphique depuis un layout XML
    private LayoutInflater mInflater;

    public FriendListAdapter(Context context, List<Friend> aListP) {
        mContext = context;
        mListP = aListP;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setmListP(List<Friend> mListP) {
        this.mListP = mListP;
    }

    public void clear() {
        this.mListP.clear();
    }
    public int getCount() {
        return mListP.size();
    }

    public Object getItem(int position) {
        return mListP.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public void add(Friend friend) {
        mListP.add(friend);
        //this.notifyDataSetInvalidated();
    }
    public void remove(Friend friend) {
        mListP.remove(friend);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.activity_friend_list_list_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView tv_Nom = (TextView)layoutItem.findViewById(R.id.TV_Nom);
        TextView tv_Prenom = (TextView)layoutItem.findViewById(R.id.TV_Prenom);

        tv_Nom.setText(mListP.get(position).name);
        tv_Prenom.setText(mListP.get(position).score+"");

       /* if (mListP.get(position).score > 5) {
            layoutItem.setBackgroundColor(Color.BLUE);
        } else {
            layoutItem.setBackgroundColor(Color.MAGENTA);
        }*/

//On mémorise la position du "friend" dans le composant textview
        tv_Nom.setTag(position);
//On ajoute un listener
        tv_Nom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Lorsque l'on clique sur le nom, on récupère la position du "friend"
                Integer position = (Integer)v.getTag();

                //On prévient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendListener(mListP.get(position), position);

            }

        });

        return layoutItem;
    }


// -------------------------------------------------



    //Contient la liste des listeners
    private ArrayList<FriendAdapterListener> mListListener = new ArrayList<FriendAdapterListener>();
    /**
     * Pour ajouter un listener sur notre adapter
     */
    public void addListener(FriendAdapterListener aListener) {
        mListListener.add(aListener);
    }
    private void sendListener(Friend item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickNom(item, position);
        }
    }



    public interface FriendAdapterListener {
        void onClickNom(Friend item, int position);
    }
}



