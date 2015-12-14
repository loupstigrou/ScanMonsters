package prog_mobile.uqac.com.scanmonsters.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.database.MySQLiteHelper;
import prog_mobile.uqac.com.scanmonsters.asynctasks.ScoreAndCreatureService;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Created by Major on 07/12/2015.
 */
public class CreaturesGridAdapter extends BaseAdapter {

    private Context context;
    private ScoreAndCreatureService getCreatures;
    private ArrayList<Integer> creaturesNb;

    public CreaturesGridAdapter(Context context, SessionManager sessionManager) {
        this.context = context;

        this.getCreatures = new ScoreAndCreatureService(context, sessionManager, null, null);
        getCreatures.execute((Void) null);

        // Attend que l'async task soit finit
        try {
            getCreatures.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // List des créatures possédées par l'utilisateur
        this.creaturesNb = new ArrayList<>();
        if (!getCreatures.getServerResponse().equals("NOK")) {
            String[] dataCreatures = getCreatures.getServerResponse()
                    .split("=");

            if (!dataCreatures[2].equals("OK")) {
                dataCreatures = dataCreatures[2].split(",");

                for (int i = 0; i < dataCreatures.length; i++) {
                    String creature = dataCreatures[i].split("-")[0];
                    int creatureNb = Integer.parseInt(creature) - 1;
                    this.creaturesNb.add(creatureNb);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return creatureImages.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Création d'une vue pour chaque utilisateur
        LinearLayout creatureView;

        int viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, this.context.getResources().getDisplayMetrics());
        int btnDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, this.context.getResources().getDisplayMetrics());

        creatureView = new LinearLayout(this.context);
        creatureView.setLayoutParams(new GridView.LayoutParams(viewWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        creatureView.setOrientation(LinearLayout.VERTICAL);
        creatureView.setBackground(ContextCompat.getDrawable(this.context, R.drawable.roundedlayout));

        ImageView imageView = new ImageView(this.context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (this.creaturesNb.contains(position))
            imageView.setImageResource(creatureImages[position]);
        else
            imageView.setImageResource(shadowImages[position]);
        creatureView.addView(imageView);

        // Separator
        View divider = new View(this.context);
        divider.setBackgroundColor(ContextCompat.getColor(this.context, R.color.divider));
        divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        creatureView.addView(divider);

        Button button = new Button(this.context);
        button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, btnDim));
        button.setText(R.string.offer_creature);
        button.setBackground(ContextCompat.getDrawable(this.context, R.drawable.roundedbottombutton));
        if (this.creaturesNb.contains(position)) {
            button.setTextColor(ContextCompat.getColor(this.context, R.color.accent));
            // On click => Ouvre un dialog pour choisir à qui l'envoyer
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("A qui ? ");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.select_dialog_singlechoice
                    );

                    MySQLiteHelper datasource = new MySQLiteHelper(context);
                    datasource.open();

                    List<Friend> allFriends = datasource.getAllFriends();
                    int nbFriends = allFriends.size();
                    if(nbFriends == 0)
                    {
                        builder.setMessage("Vous n'avez pas encore d'amis :/");
                    }
                    else {
                        for (Friend tmpFriend : allFriends) {
                            arrayAdapter.add(tmpFriend.name);
                        }
                    }

                    builder.setNegativeButton(
                            "Annuler",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setAdapter(
                                    arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            Toast.makeText(context, "Friend : " + strName, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .show();
                }
            });
        } else {
            button.setTextColor(ContextCompat.getColor(this.context, R.color.primary_text));
        }
        creatureView.addView(button);

        return creatureView;
    }

    private Integer[] creatureImages = {
            R.drawable.crea_1, R.drawable.crea_2, R.drawable.crea_3, R.drawable.crea_4,
            R.drawable.crea_5, R.drawable.crea_6, R.drawable.crea_7, R.drawable.crea_8,
            R.drawable.crea_9, R.drawable.crea_10, R.drawable.crea_11, R.drawable.crea_12,
            R.drawable.crea_13, R.drawable.crea_14, R.drawable.crea_15, R.drawable.crea_16,
            R.drawable.crea_17, R.drawable.crea_18, R.drawable.crea_19, R.drawable.crea_20,
            R.drawable.crea_21, R.drawable.crea_22, R.drawable.crea_23, R.drawable.crea_24,
            R.drawable.crea_25, R.drawable.crea_26, R.drawable.crea_27, R.drawable.crea_28,
            R.drawable.crea_29, R.drawable.crea_30, R.drawable.crea_31, R.drawable.crea_32,
            R.drawable.crea_33, R.drawable.crea_34, R.drawable.crea_35, R.drawable.crea_36,
            R.drawable.crea_37, R.drawable.crea_38, R.drawable.crea_39, R.drawable.crea_40,
            R.drawable.crea_41, R.drawable.crea_42, R.drawable.crea_43, R.drawable.crea_44,
            R.drawable.crea_45, R.drawable.crea_46, R.drawable.crea_47, R.drawable.crea_48,
            R.drawable.crea_49, R.drawable.crea_50
    };

    private Integer[] shadowImages = {
        R.drawable.crea_1_shadow, R.drawable.crea_2_shadow, R.drawable.crea_3_shadow, R.drawable.crea_4_shadow,
                R.drawable.crea_5_shadow, R.drawable.crea_6_shadow, R.drawable.crea_7_shadow, R.drawable.crea_8_shadow,
                R.drawable.crea_9_shadow, R.drawable.crea_10_shadow, R.drawable.crea_11_shadow, R.drawable.crea_12_shadow,
                R.drawable.crea_13_shadow, R.drawable.crea_14_shadow, R.drawable.crea_15_shadow, R.drawable.crea_16_shadow,
                R.drawable.crea_17_shadow, R.drawable.crea_18_shadow, R.drawable.crea_19_shadow, R.drawable.crea_20_shadow,
                R.drawable.crea_21_shadow, R.drawable.crea_22_shadow, R.drawable.crea_23_shadow, R.drawable.crea_24_shadow,
                R.drawable.crea_25_shadow, R.drawable.crea_26_shadow, R.drawable.crea_27_shadow, R.drawable.crea_28_shadow,
                R.drawable.crea_29_shadow, R.drawable.crea_30_shadow, R.drawable.crea_31_shadow, R.drawable.crea_32_shadow,
                R.drawable.crea_33_shadow, R.drawable.crea_34_shadow, R.drawable.crea_35_shadow, R.drawable.crea_36_shadow,
                R.drawable.crea_37_shadow, R.drawable.crea_38_shadow, R.drawable.crea_39_shadow, R.drawable.crea_40_shadow,
                R.drawable.crea_41_shadow, R.drawable.crea_42_shadow, R.drawable.crea_43_shadow, R.drawable.crea_44_shadow,
                R.drawable.crea_45_shadow, R.drawable.crea_46_shadow, R.drawable.crea_47_shadow, R.drawable.crea_48_shadow,
                R.drawable.crea_49_shadow, R.drawable.crea_50_shadow
    };
}
