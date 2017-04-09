package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by FANGs on 2017/3/22.
 */

class AutoTextView extends android.support.v7.widget.AppCompatTextView {

    private int mLineY;
    private int mViewWidth;
    private int mViewHeight;
    private int mSpaceHeight;
    private int wordHeight;
    private Layout layout;
    private int mLines = 15;
    private float startX = 0, startY = 0;
    private ArrayList<String> lineModeText;
    private boolean isLineMode = false;
    private int textColor = 0x8a000000;


    public AutoTextView(Context c){
        super(c);
    }

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private static final String TAG = "AutoTextView";

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.drawableState = getDrawableState();
        mViewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mViewHeight = getHeight();
        wordHeight = (int) (paint.getFontMetrics().descent - paint.getFontMetrics().ascent);
        mSpaceHeight = (mViewHeight - mLines * wordHeight) / (mLines - 1);
        Log.e(TAG, "onDraw: padding"+getPaddingLeft()+" "+getPaddingRight()+" width "+mViewWidth );
        startX = getPaddingLeft();

        drawText(canvas);

    }

    private void drawText(Canvas canvas) {
        TextPaint paint = getPaint();
        Log.e(TAG, "drawText: 颜色"+textColor );
        paint.setColor(textColor);
        paint.drawableState = getDrawableState();
        String text = (String) getText();
        mLineY = (int) (startY + 0.5);
        mLineY += getTextSize();
        if(isLineMode){
            //使用行模式绘制文字
            for (int i=0; i<lineModeText.size(); i++) {
                String line = lineModeText.get(i);
//                Log.e(TAG, "drawText: 绘制-->"+line );
                float width = StaticLayout.getDesiredWidth(line, 0, line.length(), getPaint());
//                Log.e(TAG, "drawText: line Width "+width );
                if (needScale(line)) {
                    drawScaledText(canvas, line, width);
                } else {
                    canvas.drawText(line, startX, mLineY, paint);
                }
                mLineY += mSpaceHeight + wordHeight;
            }
        }else {
            //使用TextView默认模式绘制文字
            layout = getLayout();
            for (int i = 0; i < layout.getLineCount() && i < mLines; i++) {
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                String line = text.substring(lineStart, lineEnd);
                float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                if (needScale(line) && i < layout.getLineCount() - 1) {
                    drawScaledText(canvas, line, width);
                } else {
                    canvas.drawText(line, startX, mLineY, paint);
                }
                mLineY += mSpaceHeight + wordHeight;
            }
        }
    }

    private void drawScaledText(Canvas canvas, String line, float lineWidth) {
        float x = startX;
        float d = (mViewWidth - lineWidth) / (line.length() - 1);
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

    public void setLines(int lines) {
        mLines = lines;
        postInvalidate();
    }



    /**
     * 行模式设置显示内容
     * @param t 中元素为每行显示的文字
     */
    public void setLineModeText(ArrayList<String> t){
        isLineMode = true;
        if(t != null) {
            this.lineModeText = t;
        }else{
            this.lineModeText = new ArrayList<>();
        }
        postInvalidate();
    }

    public void setTextColor(int color){
        this.textColor = color;
        postInvalidate();
    }
}