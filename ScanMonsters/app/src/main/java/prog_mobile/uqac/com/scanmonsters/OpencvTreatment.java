package prog_mobile.uqac.com.scanmonsters;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jerome on 15/11/2015.
 */
public class OpencvTreatment {

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
