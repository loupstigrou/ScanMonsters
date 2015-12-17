package prog_mobile.uqac.com.scanmonsters.image_processing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import prog_mobile.uqac.com.scanmonsters.R;

public class RectangleView extends View {

	private Drawable mRightBottomIcon;

	//Position de départ du rectangle

	private float mRightBottomPosX = 500;
	private float mRightBottomPosY = 500;

	private float mLeftTopPosX = mRightBottomPosX - 800;
	private float mLeftTopPosY = mRightBottomPosY - 300;

	private float mRightTopPosX = mRightBottomPosX;
	private float mRightTopPosY = mRightBottomPosY - 300;

	private float mLeftBottomPosX = mRightBottomPosX - 800;
	private float mLeftBottomPosY = mRightBottomPosY;


	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;

	private Paint topLine;
	private Paint bottomLine;
	private Paint leftLine;
	private Paint rightLine;

	private Rect buttonRec;
	
	private int mCenter;

	private static final int INVALID_POINTER_ID = -1;
	private int mActivePointerId = INVALID_POINTER_ID;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	

	public RectangleView(Context context){
		super(context);
		init(context);
	}

	public RectangleView(Context context, AttributeSet attrs){
		super (context,attrs);
		init(context);
	}

	public RectangleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {

		// I need to create lines for the bounding box to connect

		topLine = new Paint();
		bottomLine = new Paint();
		leftLine = new Paint();
		rightLine = new Paint();

		setLineParameters(Color.RED,5);
		
		// Here I grab the image that will work as the corners of the bounding
		// box and set their positions.

		mRightBottomIcon = context.getResources().getDrawable(R.drawable.corners);
		mRightBottomIcon.setBounds((int)mRightBottomPosX, (int)mRightBottomPosY,
				mRightBottomIcon.getIntrinsicWidth()+(int)mRightBottomPosX,
				mRightBottomIcon.getIntrinsicHeight()+(int)mRightBottomPosY);
		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

	}

	private void setLineParameters(int color, float width){

		topLine.setColor(color);
		topLine.setStrokeWidth(width);

		bottomLine.setColor(color);
		bottomLine.setStrokeWidth(width);

		leftLine.setColor(color);
		leftLine.setStrokeWidth(width);

		rightLine.setColor(color);
		rightLine.setStrokeWidth(width);
	
	}

	// Draws the bounding box on the canvas. Every time invalidate() is called
	// this onDraw method is called.
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		
		canvas.drawLine(mLeftTopPosX+mCenter, mLeftTopPosY+mCenter,
				mRightTopPosX+mCenter, mRightTopPosY+mCenter, topLine);
		canvas.drawLine(mLeftBottomPosX+mCenter, mLeftBottomPosY+mCenter,
				mRightBottomPosX+mCenter, mRightBottomPosY+mCenter, bottomLine);
		canvas.drawLine(mLeftTopPosX + mCenter, mLeftTopPosY + mCenter,
				mLeftBottomPosX + mCenter, mLeftBottomPosY + mCenter, leftLine);
		canvas.drawLine(mRightTopPosX + mCenter, mRightTopPosY + mCenter,
				mRightBottomPosX + mCenter, mRightBottomPosY + mCenter, rightLine);

		mRightBottomIcon.setBounds((int) mRightBottomPosX-30, (int) mRightBottomPosY-30,
				mRightBottomIcon.getIntrinsicWidth() + (int) mRightBottomPosX,
				mRightBottomIcon.getIntrinsicHeight() + (int) mRightBottomPosY);

		mRightBottomIcon.draw(canvas);
		canvas.restore();
	}

	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		boolean intercept = true;

		switch (action) {

		case MotionEvent.ACTION_DOWN: {

			final float x = ev.getX();
			final float y = ev.getY();

			// in CameraPreview we have Rect rec. This is passed here to return
			// a false when the camera button is pressed so that this view ignores
			// the touch event.
			if ((x >= buttonRec.left) && (x <=buttonRec.right) && (y>=buttonRec.top) && (y<=buttonRec.bottom)){
				intercept = false;
				break;
			}

			// Remember where we started
			mLastTouchX = x;
			mLastTouchY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_MOVE: {

			final float x = ev.getX();
			final float y = ev.getY();

			// Only move if the ScaleGestureDetector isn't processing a gesture.
			// but we ignore here because we are not using ScaleGestureDetector.
			if (!mScaleDetector.isInProgress()) {
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				mPosX += dx;
				mPosY += dy;

				invalidate();
			}

			// Calculate the distance moved
			final float dx = x - mLastTouchX;
			final float dy = y - mLastTouchY;


			// Move the object
			if (mPosX >= 0 && mPosX <=800){
				mPosX += dx;
			}
			if (mPosY >=0 && mPosY <= 480){
				mPosY += dy;
			}
			if (dx != 0){
				mRightTopPosX = x;
			}
			if (dy != 0){
				mLeftBottomPosY = y;
			}
			mRightBottomPosX = x;
			mRightBottomPosY = y;

			mLeftTopPosX = mRightBottomPosX - 800;
			mLeftTopPosY = mRightBottomPosY - 300;

			mRightTopPosX = mRightBottomPosX;//1000;
			mRightTopPosY = mRightBottomPosY - 300;//700;

			mLeftBottomPosX = mRightBottomPosX - 800;//200;
			mLeftBottomPosY = mRightBottomPosY;//1000;

			// Remember this touch position for the next move event
			mLastTouchX = x;
			mLastTouchY = y;

			// Invalidate to request a redraw
			invalidate();
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			mActivePointerId = INVALID_POINTER_ID;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			// Extract the index of the pointer that left the touch sensor
			final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
			>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			final int pointerId = ev.getPointerId(pointerIndex);
			if (pointerId == mActivePointerId) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				mLastTouchX = ev.getX(newPointerIndex);
				mLastTouchY = ev.getY(newPointerIndex);
				mActivePointerId = ev.getPointerId(newPointerIndex);
			}
			break;
		}
		}
		return intercept;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

			invalidate();
			return true;
		}
	}

	public float getmLeftTopPosX(){
		return mLeftTopPosX;
	}
	public float getmLeftTopPosY(){
		return mLeftTopPosY;
	}
	public float getmRightBottomPosY() {
		return mRightBottomPosY;
	}
	public float getmRightBottomPosX() {
		return mRightBottomPosX;
	}
	public void setRec(Rect rec) {
		this.buttonRec = rec;
	}
}