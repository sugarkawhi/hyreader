package me.sugarkawhi.hyreader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import org.w3c.dom.Text;

import me.sugarkawhi.hyreader.R;

/**
 * 阅读器
 * 1.总共两页来支持前后翻页
 *
 * @author zhzy
 * @date 2017/12/12
 */

public abstract class HyReaderView extends View {

    boolean DEBUG = true;

    //标题字体颜色
    private int mTitleColor;
    //标题文字大小
    private int mTitleSize;
    //内容文字颜色
    private int mContentColor;
    //内容文字大小
    private int mContentSize;
    //头部标题高度：章节标题
    private int mHeaderHeight;
    //底部内尔用高度： 电池电量/阅读进度/时间
    private int mFooterHeight;
    //水平方向内边距
    private int mHorizontalPadding;
    //电池宽度
    private int mBatteryWidth;
    //电池高度
    private int mBatteryHeight;
    //电池头尺寸
    private int mBatteryHeadSize;
    //电池边距
    private int mBatteryGap;

    private int mWidth;
    private int mHeight;
    private int mContentWidth;
    private int mContentHeight;
    //标题 时间 电池 进度 Paint
    private Paint mTitlePaint;
    //内容Paint
    private TextPaint mContentPaint;
    //测量时间字符串宽度
    private Rect mTimeRect;
    //Content 文字样式
    private Typeface mTypeface;
    //当前页面画布
    private Canvas mCurrentCanvas;
    //下一页面画布
    private Canvas mNextCavas;
    //当前状态#{ReaderState.SCROLL or ReaderState.STATIC}
    private int mCurrentState;
    //测量标题高度FontMetrics
    private Paint.FontMetrics mTitleFontMetrics;
    //背景
    private Bitmap mBackgroundBitmap;
    //电量
    private float mBatteryElectricity;
    //时间
    private String mTime;
    //当前页章节名
    private String mCurrentChapterName;
    //下一页章节名
    private String mNextChapterName;
    //当前页进度
    private float mCurrentProgress;
    //下一页进度
    private float mNextProgress;
    //当前页内容
    private String mCurrentContent;
    //下一页内容
    private String mNextContent;
    //绘制多行文字
    private StaticLayout mStaticLayout;

    public HyReaderView(Context context) {
        this(context, null);
    }

    public HyReaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyReaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HyReaderView);
        mTitleColor = array.getColor(R.styleable.HyReaderView_hyReader_titleColor, Color.BLACK);
        mTitleSize = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_titleSize, 18);
        mContentColor = array.getColor(R.styleable.HyReaderView_hyReader_contentColor, Color.BLACK);
        mContentSize = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_contentSize, 25);
        mHorizontalPadding = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_horizontalPadding, 40);
        mHeaderHeight = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_headerHeight, 100);
        mFooterHeight = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_footerHeight, 100);
        mBatteryWidth = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_batteryWidth, 20);
        mBatteryHeight = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_batteryHeight, 10);
        mBatteryHeadSize = array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_batteryHeadSize, 5);
        mBatteryGap = Math.min(array.getDimensionPixelSize(R.styleable.HyReaderView_hyReader_batteryGap, 2), mBatteryHeight / 4);
        array.recycle();
        init();
    }

    private void init() {
        //内容
        mContentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mContentPaint.setColor(mContentColor);
        mContentPaint.setTextSize(mContentSize);
        mContentPaint.setTypeface(Typeface.DEFAULT);
        //标题
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitleFontMetrics = mTitlePaint.getFontMetrics();
        //画布
        mCurrentCanvas = new Canvas();
        mNextCavas = new Canvas();

        mTimeRect = new Rect();
    }

    /**
     * set reader text color
     */
    public void setContentColor(int contentColor) {
        mContentColor = contentColor;
        invalidate();
    }

    /**
     * set reader text size
     */
    public void setContentSize(int contentSize) {
        mContentSize = contentSize;
        invalidate();
    }

    /**
     * set text Typeface.DEFAULT
     */
    public void setDefaultTypeface() {
        this.mTypeface = Typeface.DEFAULT;
        invalidate();
    }

    /**
     * setTextTypeface
     */
    public void setTextTypeface(Typeface typeface) {
        this.mTypeface = typeface;
        invalidate();
    }

    /**
     * set title color
     */
    public void setTitleColor(int titleColor) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mContentWidth = w - mHorizontalPadding * 2;
        mContentHeight = h - mHeaderHeight - mFooterHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPage(canvas);
    }


    private void drawPage(Canvas canvas) {
        //画背景
        if (mBackgroundBitmap != null) {
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
        } else {
            canvas.drawColor(Color.parseColor("#D1CEC5"));
        }
        //画头部 标题
        drawHeader(canvas, mCurrentChapterName);
        //画底部 电池、进度、时间
        drawFooter(canvas, mCurrentProgress);
        //画文字内容
        drawContent(canvas, mCurrentContent);
    }

    /**
     * 画头部
     */
    private void drawHeader(Canvas canvas, String chapterName) {
        if (DEBUG) {
            mTitlePaint.setColor(Color.YELLOW);
            canvas.drawRect(0, 0, mWidth, mHeaderHeight, mTitlePaint);
            mTitlePaint.setColor(Color.BLACK);
        }
        if (TextUtils.isEmpty(chapterName)) return;
        float titleHeight = Math.abs(mTitleFontMetrics.descent + mTitleFontMetrics.ascent);
        float left = mHorizontalPadding;
        float top = mHeaderHeight / 2 + titleHeight / 2;
        canvas.drawText(chapterName, left, top, mTitlePaint);
    }

    /**
     * 画底部
     */
    private void drawFooter(Canvas canvas, float progress) {
        if (DEBUG) {
            mTitlePaint.setColor(Color.YELLOW);
            canvas.drawRect(0, mHeight - mFooterHeight, mWidth, mHeight, mTitlePaint);
            mTitlePaint.setColor(Color.BLACK);
        }
        //画进度
        float titleHeight = Math.abs(mTitleFontMetrics.descent + mTitleFontMetrics.ascent);
        float titleY = mHeight - mFooterHeight / 2 + titleHeight / 2;
        canvas.drawText(progress + "%", mHorizontalPadding, titleY, mTitlePaint);
        //画电池 STEP1: 电池头
        canvas.drawRect(mWidth - mHorizontalPadding - mBatteryHeadSize,
                mHeight - mFooterHeight / 2 - mBatteryHeadSize / 2,
                mWidth - mHorizontalPadding,
                mHeight - mFooterHeight / 2 + mBatteryHeadSize / 2,
                mTitlePaint);
        //画电池 STEP2: 电池外壳
        mTitlePaint.setStyle(Paint.Style.STROKE);
        mTitlePaint.setStrokeWidth(2);
        canvas.drawRect(mWidth - mHorizontalPadding - mBatteryHeadSize - mBatteryWidth,
                mHeight - mFooterHeight / 2 - mBatteryHeight / 2,
                mWidth - mHorizontalPadding - mBatteryHeadSize,
                mHeight - mFooterHeight / 2 + mBatteryHeight / 2,
                mTitlePaint);
        mTitlePaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mWidth - mHorizontalPadding - mBatteryHeadSize - mBatteryWidth + mBatteryGap,
                mHeight - mFooterHeight / 2 - mBatteryHeight / 2 + mBatteryGap,
                mWidth - mHorizontalPadding - mBatteryHeadSize - mBatteryGap - (mBatteryWidth * (1 - mBatteryElectricity)),
                mHeight - mFooterHeight / 2 + mBatteryHeight / 2 - mBatteryGap,
                mTitlePaint);
        //画时间
        if (!TextUtils.isEmpty(mTime)) {
            float timeWidth = mTitlePaint.measureText(mTime, 0, mTime.length());
            float timeMargin = 20; //与右边电池的偏移量
            float x = mWidth - mHorizontalPadding - timeWidth - mBatteryElectricity - mBatteryWidth - timeMargin;
            canvas.drawText(mTime, x, titleY, mTitlePaint);
        }
    }

    /**
     * 画当前内容
     */
    private void drawContent(Canvas canvas, String content) {
        if (TextUtils.isEmpty(content)) return;
        canvas.save();
        mStaticLayout = new StaticLayout(content, mContentPaint, mWidth - mHorizontalPadding * 2, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        mStaticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * 设置背景Bitmap
     */
    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.mBackgroundBitmap = backgroundBitmap;
        postInvalidate();
    }

    /**
     * 设置电量
     * TODO STATE_SCROLL时不设置
     */
    public void setBatteryElectricity(float batteryElectricity) {
        mBatteryElectricity = batteryElectricity;
        postInvalidate();
    }

    /**
     * 当前页章节名
     */
    public void setCurrentChapterName(String currentChapterName) {
        mCurrentChapterName = currentChapterName;
        postInvalidate();
    }

    /**
     * 下一页章节名
     */
    public void setNextChapterName(String nextChapterName) {
        mCurrentChapterName = nextChapterName;
    }

    /**
     * 当前时间
     */
    public void setTime(String time) {
        if (TextUtils.isEmpty(time)) return;
        mTime = time;
        postInvalidate();
    }

    /**
     * 设置当前页内容
     */
    public void setCurrentContent(String currentContent) {
        this.mCurrentContent = currentContent;
        postInvalidate();
    }

    /**
     * 设置下一页页内容
     */
    public void setNextContent(String nextContent) {
        this.mNextContent = nextContent;
        postInvalidate();
    }

}
