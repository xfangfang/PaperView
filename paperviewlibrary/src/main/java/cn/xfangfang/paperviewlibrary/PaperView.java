package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class PaperView extends FrameLayout {

    private String paperView_text = "";
    private int paperView_textColor = 0x8A000000;
    private int paperView_textSize = 16;
    private int paperView_textLine = 15;
    private Paint mPaint;
    private int currentPage = 0;
    private int wholePage = 0;
    private int contentPadding = 16;
    private ArrayList<ArrayList<String>> lineText;


    private TextView paperView_name, paperView_position, paperView_extraInfo;
    private BatteryAndClockView paperView_batteryAndClock;
    private PaperLayout paperLayout;
    private Context context;

    public PaperView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PaperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        this.context = context;

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PaperView, defStyle, 0);

        if(a.hasValue(R.styleable.PaperView_textLine)){
            paperView_textLine = a.getInt(R.styleable.PaperView_textLine,15);
        }
        if(a.hasValue(R.styleable.PaperView_text)){
            paperView_text = a.getString(R.styleable.PaperView_text);
        }
        if(a.hasValue(R.styleable.PaperView_textColor)){
            paperView_textColor = a.getColor(R.styleable.PaperView_textColor, Color.BLACK);
        }
        if(a.hasValue(R.styleable.PaperView_textSize)){
            paperView_textSize = a.getDimensionPixelSize(R.styleable.PaperView_textSize, (int) (16 * context.getResources().getDisplayMetrics().scaledDensity + 0.5));
            paperView_textSize /= context.getResources().getDisplayMetrics().scaledDensity;
        }


        a.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_paper_view, this);
        paperView_name = (TextView) view.findViewById(R.id.paper_view_name);
        paperView_position = (TextView) view.findViewById(R.id.paper_view_position);
        paperView_extraInfo = (TextView) view.findViewById(R.id.paper_view_extra_info);
        paperView_batteryAndClock = (BatteryAndClockView) view.findViewById(R.id.paper_view_battery_clock);
        paperLayout = (PaperLayout) view.findViewById(R.id.paper_layout);


        paperView_batteryAndClock.registerBatteryReceiver();
        paperLayout.setTextSize(paperView_textSize);
        if (paperView_textLine < 2) paperView_textLine = 2;
        paperLayout.setTextLine(paperView_textLine);
        paperLayout.setText(paperView_text);
        paperLayout.setContentPadding((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, contentPadding));
        paperLayout.setTextColor(paperView_textColor);

        paperLayout.setOnPageChangeListener(new PaperLayout.onPageChangeListener() {
            @Override
            public ArrayList<String> toPrePage() {


                Log.e(TAG, "当前页码 " + (currentPage + 1));
                if (currentPage == 0) {
                    Log.e(TAG, "1上一条错了 当前页码 " + (currentPage + 1));
                    updatePosition();
                    return null;
                }
                currentPage--;
                if (currentPage == 0) {
                    Log.e(TAG, "2上一条错了 当前页码 " + (currentPage + 1));
                    updatePosition();
                    return new ArrayList<>();
                }


                updatePosition();
                return lineText.get(currentPage - 1);
            }

            @Override
            public ArrayList<String> toNextPage() {


                Log.e(TAG, "当前页码 " + (currentPage + 1));
                if (currentPage == lineText.size() - 1) {
                    Log.e(TAG, "3上一条错了 当前页码 " + (currentPage + 1));
                    updatePosition();
                    return null;
                }
                currentPage++;
                if (currentPage == lineText.size() - 1) {
                    Log.e(TAG, "4上一条错了 当前页码 " + (currentPage + 1));
                    updatePosition();
                    return new ArrayList<>();
                }

                updatePosition();
                return lineText.get(currentPage + 1);
            }
        });

        mPaint = new Paint();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paperView_batteryAndClock.unregisterBatteryReceiver();
    }

    private static final String TAG = "PaperView";

    private void updatePosition() {
        paperView_position.setText((currentPage + 1) + "/" + (lineText.size()));
    }

    private ArrayList<ArrayList<String>> splitArticle(String article) {
        ArrayList<ArrayList<String>> ans = new ArrayList<>();

        ArrayList<String> res = new ArrayList<>();//一页上的每行组成的列表
        mPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_DIP, paperView_textSize));
        int MAX_LINE = paperView_textLine;
        int len;//每句话长度
        int start = 0;
        int breakTextNumber;
        int line = 0;
        boolean isAdded = false;//当前页面是否加入最终页面

        String[] sec = article.split("\n");//按段进行分割
        for (int p = 0; p < sec.length; p++) {
            String i = sec[p] + "\n";
            len = i.length();
            do {
                int width = getWidth();
                if (width == 0) width = 1080;
                breakTextNumber = mPaint.breakText(i, start, len, true, width - contentPadding * 2, null);

                isAdded = false;
                if (!i.substring(start, start + breakTextNumber).isEmpty()) {
                    res.add(i.substring(start, start + breakTextNumber));//添加一行内容
                    line++;
                    if (line >= MAX_LINE) {
                        isAdded = true;
                        ans.add(new ArrayList<>(res));
                        res.clear();
                        line = 0;
                    }
                }
                start += breakTextNumber;
            } while (start < len);
            start = 0;
            if (line >= MAX_LINE) {
                isAdded = true;
                ans.add(new ArrayList<>(res));
                res.clear();
                line = 0;
            }
        }
        if (!isAdded) ans.add(res);
        wholePage = ans.size();
        Log.e(TAG, "splitArticle: 分割完毕 页数 " + wholePage);
        return ans;
    }


    /**
     * 获取指定单位对应的原始大小（根据设备信息）
     * px,dip,sp -> px
     * <p>
     * Paint.setTextSize()单位为px
     * <p>
     * <p>
     * <p>
     * 代码摘自：TextView.setTextSize()
     *
     * @param unit TypedValue.COMPLEX_UNIT_*
     * @param size
     * @return
     */
    public float getRawSize(int unit, float size) {
        Context c = getContext();
        Resources r;

        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();

        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public void setContentPadding(int contentPadding) {
        this.contentPadding = contentPadding;
        paperLayout.setContentPadding((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, contentPadding));
    }

    public void setExtraInfo(String info) {
        this.paperView_extraInfo.setText(info);
    }

    public void setTextColor(String color) {
        this.paperView_textColor = Color.parseColor(color);
        this.paperLayout.setTextColor(paperView_textColor);
    }

    public void setTextSize(float textSize) {

        paperView_textSize = (int) textSize;
        paperLayout.setTextSize(textSize);
        setText(paperView_text);

    }

    public void setTextLine(int line) {
        if (line < 2) {
            line = 2;
        }

        paperView_textLine = line;
        paperLayout.setTextLine(line);
        setText(paperView_text);
    }

    public void setText(String text) {
        this.paperView_text = text;
        this.paperLayout.setVisibility(VISIBLE);
        lineText = splitArticle(paperView_text);
        updatePosition();
        if (lineText != null) {
            if (lineText.size() == 1) {
                paperLayout.initViewText(null, lineText.get(0), null);
            } else {
                paperLayout.initViewText(null, lineText.get(0), lineText.get(1));
            }
        }

    }

    public void setPage(int pageNum) {
        if (lineText == null) {
            Log.e(TAG, "setPage: 还没设置文字呢");
            return;
        }
        this.currentPage = pageNum - 1;
        if (currentPage < 0) {
            currentPage = 0;
        } else if (currentPage >= lineText.size()) {
            currentPage = lineText.size() - 1;
        }
        updatePosition();

        if(currentPage > 0 && currentPage + 1 <= lineText.size() - 1) {
            paperLayout.initViewText(
                    lineText.get(currentPage - 1),
                    lineText.get(currentPage),
                    lineText.get(currentPage + 1)
            );
        }
        else if(currentPage == 0 && lineText.size() >= 2) {
            paperLayout.initViewText(
                    null,
                    lineText.get(currentPage),
                    lineText.get(currentPage + 1)
            );
        }
        else if(currentPage == lineText.size()-1 &&  lineText.size() >= 2 ) {
            paperLayout.initViewText(
                    lineText.get(currentPage - 1),
                    lineText.get(currentPage),
                    null
            );
        }else {
            paperLayout.initViewText(
                    null,
                    lineText.get(currentPage),
                    null
            );
        }

    }

    /**
     * 设置字体
     *
     * @param fontName : 位于 app/src/main/assets 下的字体文件名称
     *                 例 : 若当前文件结构为 app/src/main/assets/font.ttf
     *                 则 fontName 为 "font.ttf"
     */
    public void setFont(String fontName) {
        paperLayout.setFont(fontName);
    }

    public void setChapterName(String chapterName) {
        paperView_name.setText(chapterName);
    }

    public void setOnPaperViewStateListener(PaperLayout.StateListener l) {
        paperLayout.setOnStateListener(l);
    }


}
