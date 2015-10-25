package prog_mobile.uqac.com.scanmonsters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * @author Jerome
 */
public class TessOCR {
	private TessBaseAPI mTess;
	
	public TessOCR()
	{
		mTess = new TessBaseAPI();
		//TODO Recupere la bibliotheque venant du res et non sur la memoire du device
		String datapath = Environment.getExternalStorageDirectory() + "/tesseract/"; //Definition du lieu ou se trouve la bibliotheque
		String language = "eng"; //Definition de la langue de la bibliotheque
		File dir = new File(datapath + "tessdata/");
		if (!dir.exists())
			dir.mkdirs();
		mTess.init(datapath, language); //Initialisation de la bibliotheque avec la bonne langue
	}

	/**
	 * Recupere le texte present sur l image
	 * @param bitmap image a analyser
	 * @return texte de l image
	 */
	public String getOCRResult(Bitmap bitmap)
	{
		mTess.setImage(bitmap);
		return mTess.getUTF8Text();
    }
	
	public void onDestroy() {
		if (mTess != null)
			mTess.end();
	}
}
