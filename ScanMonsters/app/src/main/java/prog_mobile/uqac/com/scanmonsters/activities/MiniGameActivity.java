package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Activité de capture de la créature
 */
public class MiniGameActivity extends InGameActivity {

    private ProgressBar lifeProgressBar;
    private ImageView canvasCreature;
    private Button captureButton;

    private CaptureCreatureTask captureCreatureTask;
    private Bitmap bitmapCreatureBase;

    private int creatureLifeMax = 100;
    private int creatureLife = 100;


    private ArrayList<EffectImage> effectImages = new ArrayList<>();

    private Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game);

        this.lifeProgressBar = (ProgressBar) findViewById(R.id.progressbar_life);
        this.canvasCreature = (ImageView) findViewById(R.id.canvasImageCreature);

        this.captureButton = (Button) findViewById(R.id.capture_button);

        this.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attack();
            }
        });

        creatureLifeMax = (int) (Math.random()*400+100);
        creatureLife = creatureLifeMax;
        lifeProgressBar.setMax(creatureLifeMax);
        lifeProgressBar.setProgress(creatureLife);


        String uri = "@drawable/crea_"+session.getUser().getCreature(); // where myresource.png is the file, extension removed from the String
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        bitmapCreatureBase = BitmapFactory.decodeResource(getResources(), imageResource);

        canvasCreature.setImageBitmap(bitmapCreatureBase);

        handler.postDelayed(runnable, 100);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshView();
            handler.postDelayed(this, 100); // restart every 100 ms
        }
    };

    private void attack() {
        creatureLife -= 5;
        addEffect();
        if(creatureLife < 1)
        {
            creatureLife = 0;
            updateProgressBar();
            handler.removeCallbacks(runnable);
            proceedCapture();
            return;
        }
        updateProgressBar();
    }

    private void refreshView() {
        Bitmap background = bitmapCreatureBase.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(background);
        Paint p = new Paint();


        for(EffectImage currentEffect : effectImages)
        {

            currentEffect.lifeTime--;

            if(currentEffect.lifeTime > 0 && currentEffect.alpha > 0)
            {
                currentEffect.alpha -= 30;
                if(currentEffect.alpha < 0) currentEffect.alpha = 0; // évite la réapparition du bitmap à la fin de son animation

                p.setAlpha(currentEffect.alpha);
                canvas.drawBitmap(currentEffect.bitmap, currentEffect.posX, currentEffect.posY, p);
            }
            else
            {
                //effectImages.remove(currentEffect);
            }

        }
        canvasCreature.setImageBitmap(background);
    }

    private void addEffect() {
        EffectImage effet = new EffectImage();
        effet.id = (int) (Math.random()*9 + 1);
        effet.alpha = 255;
        effet.lifeTime = 10;
        String uri = "@drawable/anim_"+effet.id;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        effet.bitmap = BitmapFactory.decodeResource(getResources(), imageResource);
        effet.posX = (int) (Math.random()*(bitmapCreatureBase.getWidth() - effet.bitmap.getWidth()));
        effet.posY = (int) (Math.random()*(bitmapCreatureBase.getHeight() - effet.bitmap.getHeight()));
        effectImages.add(effet);
    }


    private void updateProgressBar() {
        lifeProgressBar.setProgress(creatureLife);
    }

    private void proceedCapture() {
        if(captureCreatureTask != null) return;
        //showProgress(true);
        captureCreatureTask = new CaptureCreatureTask(this, session);
        captureCreatureTask.execute((Void) null);
    }

    public class EffectImage {
        public int id;
        public int posX;
        public int posY;
        public int lifeTime;
        public int alpha;
        public Bitmap bitmap;
    }
    /**
     * Tâche asyncrone qui va se connecter au service pour
     * capturer la créature et l'ajouter à la liste
     */
    public class CaptureCreatureTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private String serverResponse;
        private SessionManager session;

        public CaptureCreatureTask(Context context, SessionManager session) {
            this.context = context;
            this.serverResponse = "";
            this.session = session;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url;
            HttpURLConnection connection;
            String urlParameters;

            try {
                url = new URL(webserviceURL);
                urlParameters =
                        "requestType=capture" +
                                "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8") +
                                "&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8"); //

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(urlParameters);
                out.close();

                Scanner inStream = new Scanner(connection.getInputStream());

                while (inStream.hasNextLine())
                    this.serverResponse += (inStream.nextLine());

                connection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return this.serverResponse.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            captureCreatureTask = null;
           // showProgress(false);

            if (success) {
                /*Intent intent = new Intent(this.context, ScanMonsterActivity.class);
                context.startActivity(intent);
                finish();*/
                Toast.makeText(context, "Tu as capturé la créature !", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //loginView.setError(getString(R.string.error_wrong_login_or_password));
                Toast.makeText(context, "Erreur "+serverResponse, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            captureCreatureTask = null;
           // showProgress(false);
        }
    }
}
