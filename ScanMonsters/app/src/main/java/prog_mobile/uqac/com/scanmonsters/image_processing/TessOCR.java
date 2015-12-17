package prog_mobile.uqac.com.scanmonsters.image_processing;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import prog_mobile.uqac.com.scanmonsters.R;

/**
 * @author Jerome
 */
public class TessOCR {
	private TessBaseAPI mTess;
	
	public TessOCR(Context context)
	{
		mTess = new TessBaseAPI();
		String datapath = Environment.getExternalStorageDirectory() + "/tesseract/"; //Definition du lieu ou se trouve la bibliotheque
		String language = "eng"; //Definition de la langue de la bibliotheque
		File dir = new File(datapath + "tessdata/");
		if (!dir.exists())
			dir.mkdirs();

		File file = new File(datapath + "tessdata/" + language + ".traineddata");
		try {
			if (file.createNewFile()) {
				InputStream in = context.getResources().openRawResource(R.raw.eng);
				FileOutputStream out = new FileOutputStream(file);
				byte[] buff = new byte[1024];
				int read = 0;

				while ((read = in.read(buff)) > 0) {
					out.write(buff, 0, read);
				}

				in.close();
				out.close();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		mTess.init(datapath, language); //Initialisation de la bibliotheque avec la bonne langue
		mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "P0123456789-"); //Liste des caracteres acceptable
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
