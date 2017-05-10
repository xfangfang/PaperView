package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import java.util.ArrayList;

class AutoTextView extends AppCompatTextView {
    private int mLineY;
    private int mViewWidth;
    private int mViewHeight;
    private int mSpaceHeight;
    private int wordHeight;
    private int mLines = 15;
    private float startX = 0.0F;
    private float startY = 0.0F;
    private ArrayList<String> lineModeText;
    private int textColor = 0x8a000000;
    private static final String TAG = "AutoTextView";

    public AutoTextView(Context c) {
        super(c);
    }

    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        TextPaint paint = this.getPaint();
        paint.drawableState = this.getDrawableState();
        this.mViewWidth = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        this.mViewHeight = this.getHeight();
        this.wordHeight = (int) (paint.getFontMetrics().descent - paint.getFontMetrics().ascent);
        this.mSpaceHeight = (this.mViewHeight - this.mLines * this.wordHeight) / (this.mLines - 1);
        this.startX = (float) this.getPaddingLeft();
        if(lineModeText != null) {
            this.drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        TextPaint paint = this.getPaint();
        paint.setColor(this.textColor);
        paint.drawableState = this.getDrawableState();
        this.mLineY = (int) ((double) this.startY + 0.5D);
        this.mLineY = (int) ((float) this.mLineY + this.getTextSize());
        int i;
        for (i = 0; i < this.lineModeText.size(); ++i) {
            String lineStart =  this.lineModeText.get(i);
            float lineEnd = StaticLayout.getDesiredWidth(lineStart, 0, lineStart.length(), this.getPaint());
            if (this.needScale(lineStart)) {
                this.drawScaledText(canvas, lineStart, lineEnd);
            } else {
                canvas.drawText(lineStart, this.startX, (float) this.mLineY, paint);
            }

            this.mLineY += this.mSpaceHeight + this.wordHeight;
        }

    }

    private void drawScaledText(Canvas canvas, String line, float lineWidth) {
        float x = this.startX;
        float d = ((float) this.mViewWidth - lineWidth) / (float) (line.length() - 1);

        for (int i = 0; i < line.length(); ++i) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, this.getPaint());
            canvas.drawText(c, x, (float) this.mLineY, this.getPaint());
            x += cw + d;
        }

    }

    private boolean needScale(String line) {
        return line.length() == 0 ? false : line.charAt(line.length() - 1) != 10;
    }

    public void setLines(int lines) {
        this.mLines = lines;
        this.postInvalidate();
    }

    public void setLineModeText(ArrayList<String> t) {
        if (t != null) {
            this.lineModeText = t;
        } else {
            this.lineModeText = new ArrayList();
        }

        this.postInvalidate();
    }

    public void setTextColor(int color) {
        this.textColor = color;
        this.postInvalidate();
    }
}
