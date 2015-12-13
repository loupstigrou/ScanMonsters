package prog_mobile.uqac.com.scanmonsters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import prog_mobile.uqac.com.scanmonsters.user.SessionManager;
import prog_mobile.uqac.com.scanmonsters.user.User;

/**
 * @author Jerome
 * @author Nico
 */
public class OCRActivity extends AppCompatActivity{
	private TessOCR mTessOCR;
	private TextView mResult;
	private ProgressDialog mProgressDialog;
	private ImageView mImage;
	private String mCurrentPhotoPath;
	private User myUser;
	private static final int REQUEST_TAKE_PHOTO = 1;

	private SessionManager session;

	private Point p1,p2;
	private boolean picTake = false;
	private org.opencv.core.Rect roi;
	private Bitmap bitmap;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tesseract);

		// Current user session
		this.session = new SessionManager(getApplicationContext());
		this.session.checkLogin();
		myUser = this.session.getUser();

		mResult = (TextView) findViewById(R.id.tv_result);
		mImage = (ImageView) findViewById(R.id.image);

		new Thread(new Runnable() { //Creation d'un thread annexe  pour realiser la detection
            public void run() {
                mTessOCR = new TessOCR(getApplicationContext());
            }
        }).start();

		mImage.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
            if(picTake)
            {
                if(event.getPointerCount() == 1) {
                    double x1 = (double) event.getX(0);
                    double y1 = (double) event.getY(0);
                    p1 = new Point(x1, y1);

                }
                if(event.getPointerCount() == 2) {
                    double x2 = (double) event.getX(1);
                    double y2 = (double) event.getY(1);
                    p2 = new Point(x2, y2);
                    roi = new org.opencv.core.Rect(p1, p2);

                    String text2 = String.valueOf(roi.height);
                    String text3 = String.valueOf(roi.width);
                    Log.i("Rect Height", text2);
                    Log.i("Rect Width", text3);
                    String text4 = String.valueOf(bitmap.getHeight());
                    String text5 = String.valueOf(bitmap.getWidth());
                    Log.i("Bitmap Height", text4);
                    Log.i("Bitmap Width", text5);
                    OpencvTreatment myTreat = new OpencvTreatment();

                    Mat briffod = myTreat.opencvTreatment(bitmap, roi);
                    Bitmap tmp =  Bitmap.createBitmap(briffod.cols(), briffod.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(briffod,tmp);

                    mImage.setImageBitmap(tmp);

                    String text6 = String.valueOf(mImage.getHeight());
                    String text7 = String.valueOf(mImage.getWidth());
                    Log.i("IMG Height", text6);
                    Log.i("IMG Width", text7);

                    picTake =false;
                    doOCR(tmp);

                    //Release des images
                    //tmp.recycle();
                    //bitmap.recycle();
                }
            }
            return true;
			}

		});
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
    	OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);

		//If not logged in => return to first activity
		this.session.checkLogin();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/**
	 * Menu with a logout option if the user is logged in
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (this.session.isLoggedIn())
			getMenuInflater().inflate(R.menu.menu_logged_in, menu);
		else
			getMenuInflater().inflate(R.menu.menu, menu);

		return true;
	}

	/**
	 * Logout functionnality
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.menu_logout) {
			this.session.logoutUser();
		} else if (id == R.id.menu_infos) {
			Intent intent = new Intent(getApplicationContext(), PlayersBoardActivity.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(mTessOCR!=null) {
            mTessOCR.onDestroy();
        }
	}

	/**
	 * Creer une image JPEG
	 * @return File
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss") //Horodatage
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		String storageDir = Environment.getExternalStorageDirectory()
				+ "/TessOCR";
		File dir = new File(storageDir);
		if (!dir.exists())
			dir.mkdir();

		File image = new File(storageDir + "/" + imageFileName + ".jpg");

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_TAKE_PHOTO
				&& resultCode == Activity.RESULT_OK) {
			setPic();
		}
	}

	/**
	 * Traitement sur l image obtenue pour l ajuster a l ecran
	 */
	private void setPic() {
		// Get the dimensions of the View
		int targetW = mImage.getWidth();
		int targetH = mImage.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor << 1;
		bmOptions.inPurgeable = true;

		bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		mImage.setImageBitmap(bitmap);
		picTake = true;
	}

	/**
	 * Lance le processus pour recuperer l image a partir de l appareil photo
	 */
	public void takePhoto(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	private void checkRoom(String detectedRoom){
		if(detectedRoom.equals(this.myUser.getRoom())){
			Intent intent = new Intent(this,MiniGameActivity.class);
			startActivity(intent);
			finish();
		}
		else{
			Toast.makeText(this,"Vous n'êtes pas devant la bonne salle !!",Toast.LENGTH_LONG).show();
		}
	}

	public void byPass(View v){
		Intent intent = new Intent(this,MiniGameActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Lance le processus de detection de texte
	 * @param bitmap
	 */
	private void doOCR(final Bitmap bitmap) {
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, "Processing",
					"Doing OCR...", true);
		}
		else {
			mProgressDialog.show();
		}
		
		new Thread(new Runnable() { //Creation d'un thread annexe  pour realiser la detection
			public void run() {
			if(mTessOCR!=null){
				final String result = mTessOCR.getOCRResult(bitmap);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
					// TODO Auto-generated method stub
					if (result != null && !result.equals("")) {
						mResult.setText(result);
						checkRoom(result);
					}
					mProgressDialog.dismiss();
					}
				});
			}
			}
		}).start();
	}
}
