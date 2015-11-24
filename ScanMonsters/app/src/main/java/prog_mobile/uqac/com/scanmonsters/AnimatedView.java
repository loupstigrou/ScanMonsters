package prog_mobile.uqac.com.scanmonsters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by Benjamin on 24/11/2015.
 */
public class AnimatedView extends View {

    protected int framesPerSecond = 60;
    protected long animationDuration = 10000; // 10 seconds

    protected Matrix matrix = new Matrix(); // transformation matrix

    protected Path path = new Path();       // your path
    protected Paint paint = new Paint();    // your paint

    protected long startTime;

    public AnimatedView(Context context) {
        super(context);

        // start the animation:
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long elapsedTime = System.currentTimeMillis() - startTime;

        //matrix.postRotate(30 * elapsedTime / 1000);        // rotate 30° every second
       // matrix.postTranslate(100 * elapsedTime / 1000, 0); // move 100 pixels to the right
        // other transformations...

        canvas.concat(matrix);        // call this before drawing on the canvas!
        canvas.drawPath(path, paint); // draw on canvas

        if (elapsedTime < animationDuration)
            this.postInvalidateDelayed(1000 / framesPerSecond);
    }

}
