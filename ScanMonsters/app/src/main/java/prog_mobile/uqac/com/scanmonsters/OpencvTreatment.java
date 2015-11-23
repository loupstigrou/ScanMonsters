package prog_mobile.uqac.com.scanmonsters;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jerome & Nicolas on 15/11/2015.
 */
public class OpencvTreatment {

    public Mat opencvTreatment(Bitmap basePicture,Rect roi){
        Mat imageSource,imageROI;
        Bitmap briffod;
        imageSource = new Mat(basePicture.getWidth(), basePicture.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(basePicture, imageSource);
        Imgproc.cvtColor(imageSource, imageSource, Imgproc.COLOR_RGB2GRAY);

        if(imageSource.empty()){
            Log.d("Chargement OpenCV", "Image Vide");
        }
        imageROI = imageSource.submat(roi);
        Imgproc.threshold(imageROI, imageROI, 60, 255, Imgproc.THRESH_BINARY);
        //basePicture.setHeight(imageROI.height());
        //basePicture.setWidth(imageSource.width());
        //briffod = Bitmap.createBitmap(imageROI.cols(), imageROI.rows(), Bitmap.Config.ARGB_8888);
        //String text2 = String.valueOf(briffod.getHeight());
        //String text3 = String.valueOf(briffod.getWidth());
        //Log.i("Bri Height", text2);
        //Log.i("Bri Width", text3);
        //Utils.matToBitmap(imageROI,briffod);
        return imageROI;
    }

    public int opencvTreatment(Bitmap basePicture){
        Mat imageSource;
        imageSource = new Mat(basePicture.getWidth(), basePicture.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(basePicture, imageSource);
        Imgproc.cvtColor(imageSource, imageSource, Imgproc.COLOR_RGB2GRAY);

        if(imageSource.empty()){
            Log.d("Chargement OpenCV", "Image Vide");
            return 1;
        }
        Imgproc.threshold(imageSource,imageSource,60,255,Imgproc.THRESH_BINARY);
        Utils.matToBitmap(imageSource,basePicture);
        return 0;
    }


}
