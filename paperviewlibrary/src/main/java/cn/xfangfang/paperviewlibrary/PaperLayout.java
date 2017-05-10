package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
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

    /**
     * 用于显示文字的三个View
     */
    private AutoTextView leftView,centerView,rightView;

    /**
     * 用于检测滑动速度
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 手机按下时的屏幕坐标
     */
    private float mXDown;
    private float mXMove;
    private float mTouchSlop=0;
    private float touchSlopRatio=0;
    private float animateTimeRatio=0;
    private float velocityRatio=0;

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mXLastMove;

    private boolean hasMove = false;

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

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)/2;

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

                mXMove = event.getX();
                float diff = Math.abs(mXMove - mXDown);
                if (diff < mTouchSlop+48*touchSlopRatio) {
                    return true;
                }
                hasMove = true;

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
                float differ = Math.abs(mxUp - mXDown);
                if(differ < mTouchSlop+48*touchSlopRatio && !hasMove){
                    hasMove = false;
//                     _______
//                    |  _|_  |
//                    | |   | |
//                    | |___| |
//                    |___|___|
//
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
                               goPre();
                                leftView.setLineModeText(lineText);
                                leftView.offsetLeftAndRight(-getWidth()*3);

                                leftView.offsetLeftAndRight(getWidth());
                                centerView.offsetLeftAndRight(getWidth());
                                rightView.offsetLeftAndRight(getWidth());

                                invalidate();

                            }
                        }
                    }else{
//                        Log.e(TAG, "onTouchEvent: 右部点击" );
                        if(pageListener != null){
                            ArrayList<String> lineText = pageListener.toNextPage();
                            if(lineText == null){
                                if(stateListener != null){
                                    stateListener.toEnd();
                                }
//                                Log.e(TAG, "onTouchEvent: 触礁了"  );
                                rightView.setText("");
                            }else {
                                goNext();
                                rightView.setLineModeText(lineText);
                                rightView.offsetLeftAndRight(getWidth()*3);

                                leftView.offsetLeftAndRight(-getWidth());
                                centerView.offsetLeftAndRight(-getWidth());
                                rightView.offsetLeftAndRight(-getWidth());

                                invalidate();
                            }
                        }

                    }
                    return super.onTouchEvent(event);
                }
                int mVelocityValue = (int) mVelocityTracker.getXVelocity();
                int time = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 333+100*velocityRatio);
                int left= centerView.getLeft();
                if(left > getWidth()/2 || mVelocityValue > time){
                    //向右滑动 页码--

                    goPre();
                    if(pageListener != null){
                        ArrayList<String> lineText = pageListener.toPrePage();
                        if(lineText == null){
                            if(stateListener != null){
                                stateListener.toStart();
                            }
                            goNext();
//                            Log.e(TAG, "onTouchEvent: 到头了" );
                            leftView.setText("");
                        }else {
                            leftView.setLineModeText(lineText);
                            leftView.offsetLeftAndRight(-getWidth()*3);
                        }
                    }

                }else if(left<-getWidth()/2 || mVelocityValue < -time){
                    //向左滑动 页码++

                    if(pageListener != null){
                        goNext();
                        ArrayList<String> lineText = pageListener.toNextPage();
                        if(lineText == null){
                            if(stateListener != null){
                                stateListener.toEnd();
                            }
                           goPre();
//                            Log.e(TAG, "onTouchEvent: 触礁了"  );
                            rightView.setText("");
                        }else {
                            rightView.setLineModeText(lineText);
                            rightView.offsetLeftAndRight(getWidth()*3);
                        }
                    }
                }

                mScroller.startScroll(-centerView.getLeft(), 0, centerView.getLeft(), 0,(int)(350+100*animateTimeRatio));

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getX();
                mXLastMove = mXDown;
                hasMove = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void goNext(){
        AutoTextView temp = leftView;
        leftView = centerView;
        centerView = rightView;
        rightView = temp;
    }

    private void goPre(){
        AutoTextView temp = rightView;
        rightView = centerView;
        centerView = leftView;
        leftView = temp;
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

    public void setTextSize(float a){
        leftView.setTextSize(a);
        centerView.setTextSize(a);
        rightView.setTextSize(a);
    }

    public void setTextLine(int line){
        leftView.setLines(line);
        centerView.setLines(line);
        rightView.setLines(line);
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

    public void initLeftViewText(ArrayList<String> left){
        leftView.setLineModeText(left);
    }

    public void initRightViewText(ArrayList<String> right){
        rightView.setLineModeText(right);
    }

    public void initViewText(ArrayList<String> center){
        centerView.setLineModeText(center);
    }

    public void setContentPadding(int padding){
        leftView.setPadding(padding,0,padding,0);
        centerView.setPadding(padding,0,padding,0);
        rightView.setPadding(padding,0,padding,0);
    }

    interface StateListener{
        void toStart();
        void toEnd();
        void centerClicked();
    }
    private StateListener stateListener;

    private float getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public void setOnStateListener(StateListener l){
        this.stateListener = l;
    }

    /**
     * 设置小说阅读器的字体
     * @param fontName 储存在工程文件 app/src/main/assets 文件夹下的字体资源名
     */
    public void setFont(String fontName){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
        centerView.setTypeface(typeface);
        leftView.setTypeface(typeface);
        rightView.setTypeface(typeface);
    }

    /**
     * 设置触摸灵敏度
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setTouchSlop(float ratio){
        if(ratio < 0) ratio = 0;
        else if (ratio > 1) ratio = 1;
        touchSlopRatio = ratio;
    }

    /**
     * 设置滑动翻页的动画时长
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setAnimateTime(float ratio){
        if(ratio < 0) ratio = 0;
        else if (ratio > 1) ratio = 1;
        animateTimeRatio = ratio;
    }

    /**
     * 设置快速滑动以达到翻页的触发速度
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setVelocityRatio(float ratio){
        if(ratio < 0) ratio = 0;
        else if (ratio > 1) ratio = 1;
        velocityRatio = ratio;
    }

}
