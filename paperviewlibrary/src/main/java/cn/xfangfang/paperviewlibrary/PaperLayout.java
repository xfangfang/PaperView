package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;


public class PaperLayout extends ViewGroup{

    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;

    private AutoTextView leftView,centerView,rightView;

    private VelocityTracker mVelocityTracker;

    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mXMove;

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mXLastMove;

    private String text;

    private int textLine;

    private float textSize;

    private Context context;



    public PaperLayout(Context context) {
        super(context);
        init(context,null, 0);
    }

    public PaperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs, 0);
    }

    public PaperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.context = context;

        mScroller = new Scroller(context);

        leftView = new AutoTextView(context,attrs);
        centerView = new AutoTextView(context,attrs);
        rightView = new AutoTextView(context,attrs);


        addView(leftView);
        addView(centerView);
        addView(rightView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                //left top right bottom
                childView.layout((i-1)*getMeasuredWidth() , 0, i*getMeasuredWidth(), getMeasuredHeight());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());

                //mXMove 为当前手指触碰位置
                mXMove = event.getX();

                //scrolledX 为上次x坐标和本次触摸X的坐标差（相对位移）
                int scrolledX = (int) (mXLastMove - mXMove);

                centerView.offsetLeftAndRight(-scrolledX);
                leftView.offsetLeftAndRight(-scrolledX);
                rightView.offsetLeftAndRight(-scrolledX);
                invalidate();
                mXLastMove = mXMove;
                break;
            case MotionEvent.ACTION_UP:
                float mxUp = event.getX();
                float myUp = event.getY();
                if(mxUp == mXDown){
//                     _______
//                    |  _|_  |
//                    | |   | |
//                    | |___| |
//                    |___|___|
//
//                    Log.e(TAG, "onTouchEvent: 单击事件" );
                    if (mxUp > getMeasuredWidth()*0.25
                                && mxUp < getMeasuredWidth()*0.75
                                && myUp > getMeasuredHeight() * 0.25
                                && myUp < getMeasuredHeight() *0.75) {
                            //点击在中部区域
//                        Log.e(TAG, "onTouchEvent: 中部点击" );
                            if(stateListener != null) {
                                stateListener.centerClicked();
                            }
                    }else if(mxUp < 0.5 * getMeasuredWidth()){
//                        Log.e(TAG, "onTouchEvent: 左部点击" );
                        if(pageListener != null){
                            ArrayList<String> lineText = pageListener.toPrePage();
                            if(lineText == null){
                                if(stateListener != null){
                                    stateListener.toStart();
                                }
//                                Log.e(TAG, "onTouchEvent: 到头了" );
                                leftView.setText("");
                            }else {
                                AutoTextView temp = rightView;
                                rightView = centerView;
                                centerView = leftView;
                                leftView = temp;
                                leftView.setLineModeText(lineText);
                                leftView.offsetLeftAndRight(-getWidth()*3);

                                leftView.offsetLeftAndRight(getWidth());
                                centerView.offsetLeftAndRight(getWidth());
                                rightView.offsetLeftAndRight(getWidth());

                            }
                        }
                    }else{
                        Log.e(TAG, "onTouchEvent: 右部点击" );
                        if(pageListener != null){
                            ArrayList<String> lineText = pageListener.toNextPage();
                            if(lineText == null){
                                if(stateListener != null){
                                    stateListener.toEnd();
                                }
                                Log.e(TAG, "onTouchEvent: 触礁了"  );
                                rightView.setText("");
                            }else {
                                AutoTextView temp = leftView;
                                leftView = centerView;
                                centerView = rightView;
                                rightView = temp;
                                rightView.setLineModeText(lineText);
                                rightView.offsetLeftAndRight(getWidth()*3);

                                leftView.offsetLeftAndRight(-getWidth());
                                centerView.offsetLeftAndRight(-getWidth());
                                rightView.offsetLeftAndRight(-getWidth());

                            }
                        }

                    }
                    return super.onTouchEvent(event);
                }
                int mVelocityValue = (int) mVelocityTracker.getXVelocity();
                int time = 3000;
                int left= centerView.getLeft();
                if(left > getWidth()/2 || mVelocityValue > time){
                    //向右滑动 页码--

                    if(pageListener != null){
                        ArrayList<String> lineText = pageListener.toPrePage();
                        if(lineText == null){
                            if(stateListener != null){
                                stateListener.toStart();
                            }
//                            Log.e(TAG, "onTouchEvent: 到头了" );
                            leftView.setText("");
                        }else {
                            AutoTextView temp = rightView;
                            rightView = centerView;
                            centerView = leftView;
                            leftView = temp;
                            leftView.setLineModeText(lineText);
                            leftView.offsetLeftAndRight(-getWidth()*3);
                        }
                    }

                }else if(left<-getWidth()/2 || mVelocityValue < -time){
                    //向左滑动 页码++

                    if(pageListener != null){
                        ArrayList<String> lineText = pageListener.toNextPage();
                        if(lineText == null){
                            if(stateListener != null){
                                stateListener.toEnd();
                            }
//                            Log.e(TAG, "onTouchEvent: 触礁了"  );
                            rightView.setText("");
                        }else {
                            AutoTextView temp = leftView;
                            leftView = centerView;
                            centerView = rightView;
                            rightView = temp;
                            rightView.setLineModeText(lineText);
                            rightView.offsetLeftAndRight(getWidth()*3);
                        }
                    }
                }
                mScroller.startScroll(-centerView.getLeft(), 0, centerView.getLeft(), 0);

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getX();
                mXLastMove = mXDown;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private static final String TAG = "PaperLayout";

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int left = centerView.getLeft();
            centerView.offsetLeftAndRight(-left - mScroller.getCurrX());
            leftView.offsetLeftAndRight(-left - mScroller.getCurrX());
            rightView.offsetLeftAndRight(-left - mScroller.getCurrX());
            invalidate();
        }
    }

    interface onPageChangeListener{
        ArrayList<String> toPrePage();
        ArrayList<String> toNextPage();
    }

    private onPageChangeListener pageListener;

    public void setOnPageChangeListener(onPageChangeListener l){
        this.pageListener = l;
    }


    public void setText(String text){
        this.text = text;
        centerView.setText(text);
    }

    public void setTextSize(float a){
        leftView.setTextSize(a);
        centerView.setTextSize(a);
        rightView.setTextSize(a);

        this.textSize = a;
    }

    public void setTextLine(int line){
        leftView.setLines(line);
        centerView.setLines(line);
        rightView.setLines(line);

        this.textLine = line;
    }

    public void setTextColor(int color){
        leftView.setTextColor(color);
        centerView.setTextColor(color);
        rightView.setTextColor(color);
    }

    public void initViewText(ArrayList<String> left,ArrayList<String> center,ArrayList<String> right){
        rightView.setLineModeText(right);
        leftView.setLineModeText(left);
        centerView.setLineModeText(center);
    }

    public void setFont(String fontName){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        centerView.setTypeface(typeface);
        leftView.setTypeface(typeface);
        rightView.setTypeface(typeface);
    }

    public void setContentPadding(int padding){
        leftView.setPadding(padding,0,padding,0);
        centerView.setPadding(padding,0,padding,0);
        rightView.setPadding(padding,0,padding,0);
    }

    public interface StateListener{
        void toStart();
        void toEnd();
        void centerClicked();
    }
    private StateListener stateListener;

    public void setOnStateListener(StateListener l){
        this.stateListener = l;
    }
}
