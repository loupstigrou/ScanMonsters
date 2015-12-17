package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.image_processing.OpencvTreatment;
import prog_mobile.uqac.com.scanmonsters.image_processing.CameraBackground;
import prog_mobile.uqac.com.scanmonsters.image_processing.TessOCR;
import prog_mobile.uqac.com.scanmonsters.image_processing.RectangleView;

public class CameraActivity extends InGameActivity{

    private CameraBackground mCamera;
    private RectangleView mRectangle;

    private Rect rec = new Rect();

    private File mLocation = new File(Environment.
            getExternalStorageDirectory(),"result.jpg");

    private boolean bAlreadyRun;
    private boolean bBob;

    private TessOCR mTessOCR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bAlreadyRun = false;
        bBob = false;
        setContentView(R.layout.activity_camera_frame);

        mCamera = (CameraBackground) findViewById(R.id.Camera);
        mRectangle = (RectangleView) findViewById(R.id.ROIrectangle);
        mRectangle.setRec(rec);

        //Chargement de Tesseract
        new Thread(new Runnable() { //Creation d'un thread annexe  pour realiser la detection
            public void run() {
                mTessOCR = new TessOCR(getApplicationContext());
            }
        }).start();

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Load", "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    /**
     * Obtenir le ratio pour fit la photo
     * @return
     */
    public Double[] getRatio(){
        Camera.Size s = mCamera.getCameraParameters().getPreviewSize();
        double heightRatio = (double)s.height/(double) mCamera.getHeight();
        double widthRatio = (double)s.width/(double) mCamera.getWidth();
        Double[] ratio = {heightRatio,widthRatio};
        return ratio;
    }

    /**
     * Appui sur le bouton prendre photo
     * @param v
     */
    public void takePhoto(View v){
        if(!bAlreadyRun){
            bAlreadyRun = true;
            new Thread( new Runnable() {
                public void run() {
                    Double[] ratio = getRatio();
                    int left = (int) (ratio[1]*(double) mRectangle.getmLeftTopPosX());
                    // 0 is height
                    int top = (int) (ratio[0]*(double) mRectangle.getmLeftTopPosY());

                    int right = (int)(ratio[1]*(double) mRectangle.getmRightBottomPosX());

                    int bottom = (int)(ratio[0]*(double) mRectangle.getmRightBottomPosY());

                    imageProcessing(mCamera.getPic(left, top, right, bottom));
                }
            }).start();
        }
        else{
            Toast.makeText(getApplicationContext(),"Traitement déjà en cours",Toast.LENGTH_LONG);
        }
    }

    /**
     * Appui sur bouton Passer
     * @param v
     */
    public void byPass(View v){
        Intent intent = new Intent(this,MiniGameActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Appui sur bouton Bob
     * @param v
     */
    public void bob(View v){
        bBob = true;
        takePhoto(v);
    }

    /**
     * Libere les resources en cas de retour en arrière
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Sauvegarde de l'image au format JPEG
     * @param bm
     * @return
     */
    private boolean savePhoto(Bitmap bm) {
        FileOutputStream image = null;
        try {
            image = new FileOutputStream(mLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.JPEG, 100, image);
        if (bm != null) {
            int h = bm.getHeight();
            int w = bm.getWidth();
        } else {
            return false;
        }
        return true;
    }

    /**
     * Verifie si la salle est la bonne
     * @param detectedRoom
     */
    private void checkRoom(String detectedRoom){
       Log.i("Room", detectedRoom);
        if(detectedRoom.equals(this.user.getRoom())){
            Intent intent = new Intent(this,MiniGameActivity.class);
            startActivity(intent);
            finish();
        }
        else if(bBob && detectedRoom.equals("P4-5268")){
            Intent intent = new Intent(this,MiniGameActivity.class);
            startActivity(intent);
            finish();
            bBob = false;
        }
        else{
            Toast.makeText(this, "Vous n'êtes pas devant la bonne salle !!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTessOCR!=null) {
            mTessOCR.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);

        //If not logged in => return to first activity
        this.session.checkLogin();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Lance le processus de detection de texte
     * @param bitmap
     */
    private void doOCR(final Bitmap bitmap) {
        new Thread(new Runnable() { //Creation d'un thread annexe  pour realiser la detection
            public void run() {
                if(mTessOCR!=null){
                    final String result = mTessOCR.getOCRResult(bitmap);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (result != null && !result.equals("")) {
                                checkRoom(result);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Chaine vide",Toast.LENGTH_LONG).show();
                            }
                            bAlreadyRun = false;
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Effectue les traitments sur l'image
     * @param source
     */
    public void imageProcessing(Bitmap source){

        Log.i("Info","Start treatment");
        OpencvTreatment myTreat = new OpencvTreatment();

        Mat briffod = myTreat.opencvTreatment(source);
        Bitmap tmp =  Bitmap.createBitmap(briffod.cols(), briffod.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(briffod, tmp);
        doOCR(tmp);
        savePhoto(tmp);
    }
}