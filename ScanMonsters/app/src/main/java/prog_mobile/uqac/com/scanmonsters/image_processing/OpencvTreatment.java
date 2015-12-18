package prog_mobile.uqac.com.scanmonsters.image_processing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jerome & Nicolas on 15/11/2015.
 */
public class OpencvTreatment {

    public Mat opencvTreatment(Bitmap basePicture){
        Mat imageSource;
        imageSource = new Mat(basePicture.getWidth(), basePicture.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(basePicture, imageSource);
        Imgproc.cvtColor(imageSource, imageSource, Imgproc.COLOR_RGB2GRAY); //passage en nuance de gris

        if(imageSource.empty()){
            Log.d("Chargement OpenCV", "Image Vide");
            return null;
        }
        Imgproc.threshold(imageSource,imageSource,60,255,Imgproc.THRESH_BINARY); //binarisation de l'image
        imageSource = eraseLine(imageSource);
        return imageSource;
    }

    // Erase the first black line on the frame
    private Mat eraseLine(Mat _frame)
    {
        int row;
        int col;
        boolean lineFound;
        boolean onBlackLine;
        Mat tmp = _frame.clone();

        _frame.convertTo(tmp,CvType.CV_8UC1);

        // for each column of the frame
        for(col = 10 ; col <tmp.cols() - 10 ; col++)
        {
            row = tmp.rows() - 10;
            lineFound = false;
            onBlackLine = false;

            while(!lineFound && row > 8*tmp.rows() / 10)
            {
                // if there's a black point
                double[] value = tmp.get(row, col);
                if (value[0] == 0)
                {
                    // add them to the map
                    // Erase lines that are already stored in the map from the reference Frame
                    value[0]=255;
                    tmp.put(row,col,value);
                    // set the boolean to true (we're on a line)
                    onBlackLine = true;
                }
                // if there's a white point
               /* else if (value[0] > 20 && onBlackLine)
                {
                    // set the boolean to true (we found the entire line)
                    lineFound = true;
                }*/
                row--;
            }
        }
        return tmp;
    }
}
