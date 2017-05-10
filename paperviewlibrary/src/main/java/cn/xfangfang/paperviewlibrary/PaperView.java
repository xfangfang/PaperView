package cn.xfangfang.paperviewlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class PaperView extends FrameLayout {

    private String paperView_text, paperView_pre_text = null, paperView_next_text = null;
    private int paperView_textColor = 0x8A000000;
    private int paperView_info_textColor = 0x8A000000;
    private int paperView_textSize = 18;
    private int paperView_textLine = 17;
    private Paint mPaint;
    private int currentPage = 0;
    private int wholePage = 0, pre_wholePage = 0x3f3f3f, next_wholePage = 0x3f3f3f, cur_wholePage = 0x3f3f3f;
    private int contentPadding = 16;
    private ArrayList<ArrayList<String>> lineText, preLineText, nextLineText;


    private TextView paperView_name, paperView_position, paperView_extraInfo;
    private BatteryAndClockView paperView_batteryAndClock;
    private PaperLayout paperLayout;
    private Context context;
    private onPageStateLinstener pageStateListener;

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

        if (a.hasValue(R.styleable.PaperView_textLine)) {
            paperView_textLine = a.getInt(R.styleable.PaperView_textLine, 17);
        }
        if (a.hasValue(R.styleable.PaperView_textColor)) {
            paperView_textColor = a.getColor(R.styleable.PaperView_textColor, Color.BLACK);
        }
        if (a.hasValue(R.styleable.PaperView_textSize)) {
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
        if (paperView_textLine < 3) paperView_textLine = 3;
        paperLayout.setTextLine(paperView_textLine);
        paperLayout.setContentPadding((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, contentPadding));
        paperLayout.setTextColor(paperView_textColor);

        paperLayout.setOnPageChangeListener(new PaperLayout.onPageChangeListener() {
            @Override
            public ArrayList<String> toPrePage() {

                if (currentPage == 0) {
                    if (pageStateListener != null) {
                        pageStateListener.decChapter();
                    }
                    if (getPreChapter()) {
                        if (paperView_pre_text == null) {
                            //如果没有加载出来，会显示正在加载上一章
                            pageStateListener.incChapter();
                            pageStateListener.isStartPoint();
                            return null;
                        }
                        next_wholePage = cur_wholePage;
                        nextLineText = lineText;
                        paperView_next_text = paperView_text;
                        currentPage = pre_wholePage - 1;
                        cur_wholePage = pre_wholePage;
                        lineText = preLineText;
                        paperView_text = paperView_pre_text;

                        paperView_pre_text = null;

                        updatePosition();
                        if (lineText.size() == 1) {
                            if (pageStateListener != null) {
                                pageStateListener.getPreChapter();
                                if (preLineText != null) return preLineText.get(pre_wholePage - 1);
                            }
                            return new ArrayList<>();
                        }
                        return lineText.get(currentPage - 1);
                    } else {
                        if (pageStateListener != null) {
                            pageStateListener.isStartPoint();
                        }
                        return null;
                    }
                }
                currentPage--;
                if (currentPage == 0) {
                    updatePosition();
                    if (getPreChapter()) {
//                        Log.e(TAG, "toPrePage: 取前一章");
                        if (paperView_pre_text != null) {
                            return preLineText.get(pre_wholePage - 1);
                        }
                        return new ArrayList<>();
                    } else {
                        if (pageStateListener != null) {
                            pageStateListener.isStartPoint();
                        }
                        return new ArrayList<>();
                    }
                } else {
                    updatePosition();
                    return lineText.get(currentPage - 1);
                }
            }

            @Override
            public ArrayList<String> toNextPage() {
                if (currentPage == lineText.size() - 1) {
                    if (pageStateListener != null) {
                        pageStateListener.incChapter();
                    }
                    if (getNextChapter()) {
                        if (paperView_next_text == null) {
                            pageStateListener.decChapter();
                            pageStateListener.isEndPoint();
                            return null;
                        }
                        pre_wholePage = cur_wholePage;
                        preLineText = lineText;
                        paperView_pre_text = paperView_text;
                        currentPage = 0;
                        cur_wholePage = next_wholePage;
                        lineText = nextLineText;
                        paperView_text = paperView_next_text;

                        paperView_next_text = null;

                        updatePosition();
                        if (lineText.size() == 1) {
                            if (pageStateListener != null) {
                                pageStateListener.getNextChapter();
                                if (nextLineText != null) return nextLineText.get(0);
                            }
                            return new ArrayList<>();
                        } else {
                            return lineText.get(1);
                        }
                    }else if (pageStateListener != null) {
                        pageStateListener.isEndPoint();
                    }
                    return null;
                }
                currentPage++;

                if (currentPage == lineText.size() - 1) {
                    updatePosition();
                    if (getNextChapter()) {
                        if (paperView_next_text != null) {
                            return nextLineText.get(0);
                        }
                        return new ArrayList<>();
                    } else {
                        if (pageStateListener != null) {
                            pageStateListener.isEndPoint();
                        }
                        return new ArrayList<>();
                    }
                } else {
                    updatePosition();
                    return lineText.get(currentPage + 1);
                }
            }
        });
        paperLayout.setOnStateListener(new PaperLayout.StateListener() {
            @Override
            public void toStart() {
                if (pageStateListener != null) {
                    pageStateListener.isStartPoint();
                }
            }

            @Override
            public void toEnd() {
                if (pageStateListener != null) {
                    pageStateListener.isEndPoint();
                }
            }

            @Override
            public void centerClicked() {
                if (pageStateListener != null) {
                    pageStateListener.centerClicked();
                }
            }
        });

        mPaint = new Paint();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (paperView_text == null) paperView_text = "";
                        setText(paperView_text);
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paperView_batteryAndClock.unregisterBatteryReceiver();
    }

    private void updatePosition() {
        if (lineText == null) {

        } else {
            paperView_position.setText(String.valueOf(currentPage + 1) + "/" + String.valueOf(cur_wholePage));
        }

        if(pageStateListener != null){
            pageStateListener.onEveryPageLoad(currentPage+1,cur_wholePage);
        }
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

        String[] sec = article.split("\n");
        for (int p = 0; p < sec.length; p++) {
            String i = sec[p] + "\n";
            len = i.length();
            do {
                int width = getWidth();
                if (width == 0) width = getResources().getDisplayMetrics().widthPixels;
                breakTextNumber = mPaint.breakText(
                        i,
                        start,
                        len,
                        true,
                        width - getRawSize(TypedValue.COMPLEX_UNIT_DIP, contentPadding) * 2,
                        null
                );
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
        return ans;
    }

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
        if (contentPadding > 64) {
            contentPadding = 64;
        }
        this.contentPadding = contentPadding;
        paperLayout.setContentPadding((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, contentPadding));
        updateView();
    }

    public void setExtraInfo(String info) {
        this.paperView_extraInfo.setText(info);
    }

    public void setContentTextColor(String color) {
        this.paperView_textColor = Color.parseColor(color);
        this.paperLayout.setTextColor(paperView_textColor);
    }

    public void setInfoTextColor(String color) {
        this.paperView_info_textColor = Color.parseColor(color);
        this.paperView_batteryAndClock.setPaintColor(paperView_info_textColor);
        this.paperView_extraInfo.setTextColor(paperView_info_textColor);
        this.paperView_position.setTextColor(paperView_info_textColor);
        this.paperView_name.setTextColor(paperView_info_textColor);
    }

    public void setTextSize(float textSize) {

        paperView_textSize = (int) textSize;
        paperLayout.setTextSize(textSize);
        updateView();

    }

    public int getTextSize() {
        return paperView_textSize;
    }

//    private static final String TAG = "PaperView";

    public void setTextLine(int line) {
        if (line < 3) {
            line = 3;
        }

        paperView_textLine = line;
        paperLayout.setTextLine(line);
        updateView();
    }

    public int getTextLine() {
        return paperView_textLine;
    }


    public void updateView() {
        lineText = splitArticle(paperView_text);
        cur_wholePage = wholePage;
        setPage(currentPage + 1);
    }

    public void setPage(int pageNum) {
        if (lineText == null) {
            return;
        }
        this.currentPage = pageNum - 1;
        if (currentPage <= 0) {
            currentPage = 0;
            getPreChapter();
        }
        if (currentPage >= lineText.size() - 1) {
            currentPage = lineText.size() - 1;
            getNextChapter();
        }
        updatePosition();

        if (currentPage > 0 && currentPage + 1 <= lineText.size() - 1) {
            paperLayout.initViewText(
                    lineText.get(currentPage - 1),
                    lineText.get(currentPage),
                    lineText.get(currentPage + 1)
            );
        } else if (currentPage == 0 && lineText.size() >= 2) {
            if (preLineText != null) {
                paperLayout.initViewText(
                        preLineText.get(pre_wholePage - 1),
                        lineText.get(currentPage),
                        lineText.get(currentPage + 1)
                );
            } else {
                paperLayout.initViewText(
                        null,
                        lineText.get(currentPage),
                        lineText.get(currentPage + 1)
                );
            }
        } else if (currentPage == lineText.size() - 1 && lineText.size() >= 2) {
            if (nextLineText == null) {
                paperLayout.initViewText(
                        lineText.get(currentPage - 1),
                        lineText.get(currentPage),
                        null
                );
            } else {
                paperLayout.initViewText(
                        lineText.get(currentPage - 1),
                        lineText.get(currentPage),
                        nextLineText.get(0)
                );
            }
        } else {
            if (preLineText == null){
                if(nextLineText == null) {
                    paperLayout.initViewText(
                            null,
                            lineText.get(currentPage),
                            null
                    );

                }else{
                    paperLayout.initViewText(
                            null,
                            lineText.get(currentPage),
                            nextLineText.get(0)
                    );
                }
            }else{
                if(nextLineText == null) {
                    paperLayout.initViewText(
                            preLineText.get(pre_wholePage - 1),
                            lineText.get(currentPage),
                            null
                    );
                }else{
                    paperLayout.initViewText(
                            preLineText.get(pre_wholePage - 1),
                            lineText.get(currentPage),
                            nextLineText.get(0)
                    );
                }
            }

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

    public interface onPageStateLinstener {

        boolean getNextChapter();

        boolean getPreChapter();

        void isStartPoint();

        void isEndPoint();

        void centerClicked();

        void incChapter();

        void decChapter();

        void onEveryPageLoad(int currentPage,int wholePage);

    }

    public void setOnPageStateListener(onPageStateLinstener l) {
        this.pageStateListener = l;
    }

    private boolean getPreChapter() {
        if (pageStateListener != null) {
            if (paperView_pre_text != null) {
                return true;
            }
            if (pageStateListener.getPreChapter()) {
                return true;
            } else {
                paperView_pre_text = null;
            }
        }
        return false;
    }

    private boolean getNextChapter() {
        if (pageStateListener != null) {
            if (paperView_next_text != null) {
                return true;
            }
            if (pageStateListener.getNextChapter()) {
                return true;
            } else {
                paperView_next_text = null;
            }
        }
        return false;
    }

    public void setText(String text) {
        this.paperView_text = text;
        currentPage = 0;
        updateView();
    }

    public void setNextChapterText(String text) {
        this.paperView_next_text = text;
        if (text == null) {
            if (pageStateListener != null) {
                pageStateListener.isEndPoint();
                return;
            }
        }
        this.nextLineText = splitArticle(text);
        this.next_wholePage = wholePage;

        paperLayout.initRightViewText(this.nextLineText.get(0));
    }

    public void setPreChapterText(String text) {

        this.paperView_pre_text = text;
        if (text == null) {
            if (pageStateListener != null) {
                pageStateListener.isStartPoint();
                return;
            }
        }
        this.preLineText = splitArticle(text);
        this.pre_wholePage = wholePage;

        paperLayout.initLeftViewText(this.preLineText.get(wholePage - 1));

    }

    /**
     * 设置触摸灵敏度
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setTouchSlop(float ratio){
        paperLayout.setTouchSlop(ratio);
    }

    /**
     * 设置滑动翻页的动画时长
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setAnimateTime(float ratio){
        paperLayout.setAnimateTime(ratio);
    }

    /**
     * 设置快速滑动以达到翻页的触发速度
     * 默认值为0
     * @param ratio 系数 0-1
     */
    public void setVelocityRatio(float ratio){
        paperLayout.setVelocityRatio(ratio);
    }

}
