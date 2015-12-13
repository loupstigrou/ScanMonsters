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
        imageSource = new Mat(basePicture.getWidth(), basePicture.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(basePicture, imageSource);
        Imgproc.cvtColor(imageSource, imageSource, Imgproc.COLOR_RGB2GRAY);

        if(imageSource.empty()){
            Log.d("Chargement OpenCV", "Image Vide");
        }
        Log.d("mat col",String.valueOf(imageSource.cols()));
        Log.d("mat height", String.valueOf(imageSource.height()));
        Log.d("mat row", String.valueOf(imageSource.rows()));
        Log.d("mat width",String.valueOf(imageSource.width()));
        imageROI = imageSource.submat(roi);
        Imgproc.threshold(imageROI, imageROI, 60, 255, Imgproc.THRESH_BINARY);
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

/*
    // Erase the first black line on the frame
    Mat eraseLine(Mat _frame)
    {
        int row = 0;
        int col =10;
        boolean lineFound = false;
        boolean onBlackLine = false;

        // for each column of the frame
        for(col = 10 ; col <_frame.cols() - 10 ; col++)
        {
            row = _frame.rows() - 10;
            lineFound = false;
            onBlackLine = false;

            while(lineFound != true && row > _frame.rows() / 4)
            {
                // if there's a black point
                double[] value = _frame.get(row, col);
                if (value[0] == 0) //Changer la condition => trouver le bon accesseur
                {
                    String val = String.valueOf(value[0]);
                    Log.i("Value", val);
                    // add them to the map
                    // Erase lines that are already stored in the map from the reference Frame
                    value[0]=255;
                    value[1]=255;
                    value[2]=255;
                    _frame.put(row,col,value);
                    // set the boolean to true (we're on a line)
                    onBlackLine = true;
                }
                // if there's a white point
                else if (value[0] > 20 && onBlackLine == true)
                {
                    // set the boolean to true (we found the entire line)
                    lineFound = true;

                }

                row--;
            }
        }
        return _frame;
    }*/
}
