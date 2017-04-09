package cn.xfangfang.paperviewdemo;

/**
 * Created by FANGs on 2017/3/23.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;


public class HelloScrollerView extends View {

    private Paint paint;
    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private float lastX,lastY;
    private float currentX = 200,currentY = 200;

    public HelloScrollerView(Context context) {
        super(context);
        scroller = new Scroller(context);
        // TODO Auto-generated constructor stub
    }

    public HelloScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        scroller = new Scroller(context);
    }

    public HelloScrollerView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (paint == null) {
            paint = new Paint();
            paint.setTextSize(24*3);
        }
        canvas.drawText("测试", currentX, currentY, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (currentX + (event.getX() - lastX));
                currentY += event.getY() - lastY;
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(500);
                scroller.fling(
                        (int) currentX,
                        (int) currentY,
                        (int) velocityTracker.getXVelocity(),
                        (int) velocityTracker.getYVelocity(),
                        0, getWidth()-100,
                        30, getHeight()
                );
                velocityTracker.recycle();
                velocityTracker = null;
                if (!scroller.computeScrollOffset()) {
                }
                break;
        }
        invalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            currentX = scroller.getCurrX();
            currentY = scroller.getCurrY();
            invalidate();
        } else {
        }
    }
}