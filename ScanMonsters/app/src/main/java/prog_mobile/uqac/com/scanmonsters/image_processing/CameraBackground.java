package prog_mobile.uqac.com.scanmonsters.image_processing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraBackground extends SurfaceView implements SurfaceHolder.Callback {

	private Camera mCamera;
	private Camera.Parameters mParameters;
	private byte[] mBuffer;

	public CameraBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraBackground(Context context) {
		super(context);
		init();
	}

	public void init() {
		SurfaceHolder mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * Récupère l'image à l'intérieur du rectangle
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getPic(int x, int y, int width, int height){
		System.gc(); 
		Bitmap b = null;
		Size s = mParameters.getPreviewSize();

		YuvImage yuvimage = new YuvImage(mBuffer, ImageFormat.NV21, s.width, s.height, null);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		yuvimage.compressToJpeg(new Rect(x, y, width, height), 100, outStream); // make JPG
		b = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size()); // decode JPG
		yuvimage = null;
		try {
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.gc();
		return b;
	}

	/**
	 * Mise à jour du buffer
	 */
	private void updateBufferSize() {
		mBuffer = null;
		System.gc();
		int h = mCamera.getParameters().getPreviewSize().height;
		int w = mCamera.getParameters().getPreviewSize().width;
		int bitsPerPixel = ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat());
		mBuffer = new byte[w * h * bitsPerPixel / 8];
	}

	public void surfaceCreated(SurfaceHolder holder){
		try {
			mCamera = Camera.open();
		}
		catch (RuntimeException exception) {
			Toast.makeText(getContext(), "Camera broken, quitting :(",Toast.LENGTH_LONG).show();
		}

		try {
			mCamera.setPreviewDisplay(holder); //Définition de la prévisualisation
			updateBufferSize();
			mCamera.addCallbackBuffer(mBuffer); //Endroit ou l'on stocke l'image
			mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
				public synchronized void onPreviewFrame(byte[] data, Camera c){
					if (mCamera != null) {
						mCamera.addCallbackBuffer(mBuffer);
					}
				}
			});
		} catch (Exception exception) {
			if (mCamera != null){
				mCamera.release();
				mCamera = null;
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release(); //On libere les resources
		mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		try {
			mParameters = mCamera.getParameters();
			mParameters.set("orientation","landscape");
			mCamera.setParameters(mParameters); //Changement des paramètres
		} catch (Exception e) {}
		updateBufferSize();
		mCamera.startPreview();
	}

	public Parameters getCameraParameters(){
		return mCamera.getParameters();
	}
}