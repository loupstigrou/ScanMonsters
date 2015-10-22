package prog_mobile.uqac.com.scanmonsters;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * @author Jerome
 */
public class TessOCR {
	private TessBaseAPI mTess;
	
	public TessOCR()
	{
		mTess = new TessBaseAPI();
		String datapath = Environment.getExternalStorageDirectory() + "/tesseract/";
		String language = "eng";
		File dir = new File(datapath + "tessdata/");
		if (!dir.exists()) 
			dir.mkdirs();
		mTess.init(datapath, language);
	}
	
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
