package cn.xfangfang.paperviewdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by FANGs on 2017/3/22.
 */

public class BatteryAndClockView extends View {
    private BroadcastReceiver batteryReceiver;
    private TimeReceiver timeReceiver;
    private Context context;
    private IntentFilter filter, timeFilter;
    private Paint mTitlePaint;
    private Paint paint;
    private int power=50;
    private Rect rect;
    private String time;
    private int timeSize = 13;

    public BatteryAndClockView(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public BatteryAndClockView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public BatteryAndClockView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void drawRect(Canvas paramCanvas, int top, int paramInt1, int paramInt2) {//高，宽
        int center = top + (int) ((float)paramInt1/2+0.5);

        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
        RectF rectf=new RectF();
        rectf.left = 5;
        rectf.top = top;
        rectf.right = paramInt2+5;
        rectf.bottom = paramInt1 + top;
        int r = (int) getResources().getDisplayMetrics().density;
        paramCanvas.drawRoundRect(rectf, r, r, paint);

        paint.setStyle(Paint.Style.FILL);
        //头
        rectf.left = paramInt2+6;
        rectf.right = paramInt2 + 9;
        rectf.top = center-3;
        rectf.bottom = center+3;
        paramCanvas.drawRoundRect(rectf,3, 3, this.paint);
    }

    public class BatteryReceiver extends BroadcastReceiver {
        BatteryAndClockView a;

        public BatteryReceiver(BatteryAndClockView paramViewPhoneBattery) {
            this.a = paramViewPhoneBattery;
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            int i = paramIntent.getIntExtra("level", -1);
            int j = paramIntent.getIntExtra("scale", -1);
            this.a.update(i * 100 / j);
        }
    }

    public class TimeReceiver extends BroadcastReceiver {
        BatteryAndClockView a;

        public TimeReceiver(BatteryAndClockView paramViewPhoneBattery) {
            this.a = paramViewPhoneBattery;
        }

        public void onReceive(Context paramContext, Intent paramIntent) {
            this.a.update();
        }
    }


    private void init(Context paramContext) {
        this.context = paramContext;
        this.rect = new Rect();
        this.paint = new Paint();
        this.paint.setColor(Color.parseColor("#8A000000"));
        this.paint.setAntiAlias(true);
        this.batteryReceiver = new BatteryReceiver(this);
        this.timeReceiver = new TimeReceiver(this);
        this.filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        this.mTitlePaint = new Paint(1);
        this.mTitlePaint.setAntiAlias(true);
        this.mTitlePaint.setTextAlign(Paint.Align.LEFT);
        float textSize = getResources().getDisplayMetrics().scaledDensity * timeSize + 0.5f;
        this.mTitlePaint.setTextSize(textSize);
        this.mTitlePaint.setColor(Color.parseColor("#8A000000"));
        this.time = new SimpleDateFormat("HH:mm").format(new Date());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float textSize = getResources().getDisplayMetrics().scaledDensity * 12 + 0.5f;
        int r = (int) getResources().getDisplayMetrics().density;
        int strwidth = (int) (this.mTitlePaint.measureText(time) * 1.5) +10+ 10*r;

        int h = MeasureSpec.getSize(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        if (hMode == MeasureSpec.AT_MOST && wMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(strwidth, (int)( textSize*1.5));
        } else if (wMode == MeasureSpec.AT_MOST && hMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(strwidth, h);
        } else if (wMode == MeasureSpec.EXACTLY && hMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(w, (int)( textSize*1.5));
        } else {//match_parent
            setMeasuredDimension(w, h);
        }

    }

    protected void onDraw(Canvas paramCanvas) {

        this.mTitlePaint.getTextBounds(time, 0, 5, this.rect);
        int textHeight = this.rect.height();

        //画外框
        super.onDraw(paramCanvas);
        int i = getMeasuredHeight();
        int j = getMeasuredWidth() / 3 - 3;
        int top = (int) ( i * 0.2 + 0.5);
        drawRect(paramCanvas, top, (int) (i * 0.6), j);

        int r = (int) getResources().getDisplayMetrics().density;

        //画时间
        paramCanvas.drawText(time, j + 10*r, (int) ((i + textHeight) * 0.5 + 0.5), this.mTitlePaint);


        //画电池电量
        this.rect.left = 7;
        this.rect.right = (this.power * (j - 6) / 100 + 9);
        this.rect.top = 2 + top;
        this.rect.bottom = (int) (i * 0.6) + top - 2 ;
        paramCanvas.drawRect(this.rect, this.paint);

    }

    public void registerBatteryReceiver() {
        this.context.registerReceiver(this.batteryReceiver, this.filter);
        this.context.registerReceiver(this.timeReceiver, this.timeFilter);
    }


    public void setPaintColor(int color) {
        this.paint.setColor(color);
        this.mTitlePaint.setColor(color);
        invalidate();
    }

    public void unregisterBatteryReceiver() {
        try {
            this.context.unregisterReceiver(this.batteryReceiver);
            this.context.unregisterReceiver(this.timeReceiver);
            return;
        } catch (Exception localException) {
            for (; ; ) {
                localException.printStackTrace();
            }
        }
    }

    public void update(int paramInt) {
        time = new SimpleDateFormat("HH:mm").format(new Date());
        this.power = paramInt;
        invalidate();
    }

    public void update() {
        time = new SimpleDateFormat("HH:mm").format(new Date());
        invalidate();
    }
}