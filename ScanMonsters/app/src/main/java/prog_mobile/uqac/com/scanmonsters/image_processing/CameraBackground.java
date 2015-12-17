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

	// this constructor used when requested as an XML resource
	public CameraBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraBackground(Context context) {
		super(context);
		init();
	}

	public void init() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		SurfaceHolder mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

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

	private void updateBufferSize() {
		mBuffer = null;
		System.gc();
		// prepare a buffer for copying preview data to
		int h = mCamera.getParameters().getPreviewSize().height;
		int w = mCamera.getParameters().getPreviewSize().width;
		int bitsPerPixel = ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat());
		mBuffer = new byte[w * h * bitsPerPixel / 8];
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where to draw.
		try {
			mCamera = Camera.open();
		}
		catch (RuntimeException exception) {
			Toast.makeText(getContext(), "Camera broken, quitting :(",Toast.LENGTH_LONG).show();
		}

		try {
			mCamera.setPreviewDisplay(holder);
			updateBufferSize();
			mCamera.addCallbackBuffer(mBuffer); // where we'll store the image data
			mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
				public synchronized void onPreviewFrame(byte[] data, Camera c) {

					if (mCamera != null) { // there was a race condition when onStop() was called..
						mCamera.addCallbackBuffer(mBuffer); // it was consumed by the call, add it back
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
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	// FYI: not called for each frame of the camera preview
	// gets called on my phone when keyboard is slid out
	// requesting landscape orientation prevents this from being called as camera tilts
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		try {
			mParameters = mCamera.getParameters();
			mParameters.set("orientation","landscape");
			mCamera.setParameters(mParameters); // apply the changes
		} catch (Exception e) {
			// older phone - doesn't support these calls
		}
		updateBufferSize(); // then use them to calculate
		mCamera.startPreview();
	}

	public Parameters getCameraParameters(){
		return mCamera.getParameters();
	}
}