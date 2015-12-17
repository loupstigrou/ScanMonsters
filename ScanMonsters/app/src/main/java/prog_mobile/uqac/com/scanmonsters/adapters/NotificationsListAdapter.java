package prog_mobile.uqac.com.scanmonsters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.database.Notification;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

public class NotificationsListAdapter extends BaseAdapter {

    private List<Notification> currentArray;

    private List<Notification> receivedNotifs;
    private List<Notification> sendedNotifs;


    private Context mContext;

    //mécanisme pour gérer l'affichage graphique depuis un layout XML
    private LayoutInflater mInflater;

    private SessionManager mSession;
    private User user;
    private Comparator<Notification> comparator;

    private int currentFilter = 0;

    public static final int FILTER_RECEIVED = 0;
    public static final int FILTER_SENDED = 1;


    public NotificationsListAdapter(Context context, SessionManager session) {
        mContext = context;
        receivedNotifs = new ArrayList<>();
        sendedNotifs = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
        mSession = session;
        user = mSession.getUser();
        filterBy(FILTER_RECEIVED);


        comparator = new Comparator<Notification>() {
            @Override
            public int compare(Notification lhs, Notification rhs) {
                if (lhs.dateLastUpdate < rhs.dateLastUpdate)
                    return 1;
                else if (lhs.dateLastUpdate == rhs.dateLastUpdate)
                    return 0;
                else
                    return -1;
            }
        };
    }

    public void filterBy(int type) {
        currentFilter = type;
        if(type == FILTER_RECEIVED) currentArray = receivedNotifs;
        else if(type == FILTER_SENDED) currentArray = sendedNotifs;
        notifyDataSetInvalidated();
    }



    public void clear() {
        receivedNotifs.clear();
        sendedNotifs.clear();
    }
    public int getCount() {
        return currentArray.size();
    }

    public Object getItem(int position) {
        return currentArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public void add(Notification notification) {
        if(notification.isReceived())
            receivedNotifs.add(notification);
        else
            sendedNotifs.add(notification);
        //this.notifyDataSetInvalidated();
    }
    public void remove(Notification notification) {
        receivedNotifs.remove(notification);
        sendedNotifs.remove(notification);
    }

    public void sortArrays() {
        Collections.sort(receivedNotifs, comparator);
        Collections.sort(sendedNotifs, comparator);
        notifyDataSetInvalidated();
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
        if (convertView == null) {
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.activity_friend_list_first_row_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView tv_Nom = (TextView)layoutItem.findViewById(R.id.TV_Nom);
        TextView tv_Prenom = (TextView)layoutItem.findViewById(R.id.TV_Prenom);

        tv_Nom.setText(currentArray.get(position).getDisplayableNotif());
        tv_Prenom.setText("");

        tv_Nom.setTag(position);

        tv_Nom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Lorsque l'on clique sur le nom, on récupère la position du "friend"
                Integer position = (Integer)v.getTag();

                //On prévient les listeners qu'il y a eu un clic sur le TextView "TV_Nom".
                sendListener(currentArray.get(position), position);

            }

        });

        return layoutItem;
    }


// -------------------------------------------------



    //Contient la liste des listeners
    private ArrayList<NotificationsAdapterListener> mListListener = new ArrayList<>();
    /**
     * Pour ajouter un listener sur notre adapter
     */
    public void addListener(NotificationsAdapterListener aListener) {
        mListListener.add(aListener);
    }
    private void sendListener(Notification notification, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickNom(notification, position);
        }
    }



    public interface NotificationsAdapterListener {
        void onClickNom(Notification notification, int position);
    }
}



