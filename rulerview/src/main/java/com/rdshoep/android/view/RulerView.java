package com.rdshoep.android.view;
/*
 * @description
 *   Please write the RulerView module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/13/2016)
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.rdshoep.android.rulerView.R;
import com.rdshoep.android.utils.BitmapUtil;
import com.rdshoep.android.utils.CanvasHelper;
import com.rdshoep.android.utils.PixelUtil;

public class RulerView extends View implements GestureDetector.OnGestureListener, Runnable {

    private static final String TAG = "RulerView";
    //滑动时，刷新图像时间间隔
    private static final long FILLING_REFRESH_INTERVAL = 33; // 30Hz

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;

    private static final int START = 1;
    private static final int END = 2;

    private static final int ASC = 1;
    private static final int DESC = 2;

    //最小值、最大值、当前值
    float minValue = 0, maxValue = 100, curValue = 0;
    //最小显示精度
    float tickValue = 1f, unitStartValue = 0f;
    //单位刻度间距(px)
    int interval = 0;
    //默认刻度宽度，默认刻度颜色
    int defaultTickWidth, defaultTickColor, unitHeight;
    int displayValueLevel = -1;

    float labelFontSize;
    int labelFontColor;
    //偏移量
    int labelOffset;

    //左、上、右、下的边距
    int contentPaddingLeft = 0, contentPaddingTop = 0, contentPaddingRight = 0, contentPaddingBottom = 0;
    //最上层的遮罩
    Bitmap frontShade;
    //指针
    Bitmap pointer;

    //方向，默认水平  1--水平  2--垂直
    private int orientation = HORIZONTAL;
    //默认向上对齐   1--start  2--end
    //orientation为水平时，align为上、下有效（默认为上）
    //orientation为垂直时，align为左、右有效（默认为左）
    private int align = START;
    private int order = ASC;

    private OnValueChangedListener mListener;
    private RulerValueFormater mFormater = new RulerValueFormater() {
        @Override
        public String format(float value) {
            return String.valueOf(Math.round(value));
        }
    };

    //足够滑动的可显示Bitmap
    Bitmap fullTickContentMap;
    //    int[] tickInjectMap = {4, 1, 2, 1, 2, 1, 2, 1, 2, 1, 3, 1, 2, 1, 2, 1, 2, 1, 2, 1};
    int[] tickInjectMap = {3, 1, 1, 1, 1, 2, 1, 1, 1, 1};
    Bitmap[] tickBitmapArray = new Bitmap[5];

    GestureDetector gestureDetector;
    Scroller mScroller;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        gestureDetector = new GestureDetector(context, this);
        mScroller = new Scroller(context);
        mScroller.setFriction(ViewConfiguration.getScrollFriction() * 2);

        //设置刻度间隔
        interval = PixelUtil.dpToPx(context, 8);
        //刻度的单位高度,跟format中的类别相互影响刻度的高度
        unitHeight = PixelUtil.dpToPx(context, 5);
        //设置默认刻度宽度
        defaultTickWidth = PixelUtil.dpToPx(context, 4);
        //默认刻度颜色
        defaultTickColor = Color.RED;
        //刻度值颜色
        labelFontColor = Color.BLACK;
        //刻度描述偏移量
        labelOffset = unitHeight * 6;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RulerView, 0, 0);

            //初始化最大值、最小值和当前值
            minValue = a.getFloat(R.styleable.RulerView_minValue, minValue);
            maxValue = a.getFloat(R.styleable.RulerView_maxValue, maxValue);
            curValue = a.getFloat(R.styleable.RulerView_initValue, curValue);

            //初始值检测，必须保证在最大值和最小值之间
            if (curValue > maxValue || curValue < minValue) {
                throw new RuntimeException("InitValue is invalid, it's not in the range[minValue ~ maxValue]!");
            }

            //TODO 需要思考如果开始和结束都不是整数倍值，如何画刻度的问题
//            unitStartValue = a.getFloat(R.styleable.RulerView_unitStartValue, unitStartValue);

            //初始化刻度相关设置
            //单位刻度的差值
            tickValue = a.getFloat(R.styleable.RulerView_tickValue, tickValue);
            //刻度间的距离
            interval = a.getDimensionPixelSize(R.styleable.RulerView_interval, interval);
            //刻度的默认宽度
            defaultTickWidth = a.getDimensionPixelSize(R.styleable.RulerView_tickWidth, defaultTickWidth);
            //单位刻度高度
            unitHeight = a.getDimensionPixelSize(R.styleable.RulerView_unitHeight, unitHeight);
            //刻度的默认颜色
            defaultTickColor = a.getColor(R.styleable.RulerView_tickColor, defaultTickColor);
            //默认显示数值的Level
            displayValueLevel = a.getInteger(R.styleable.RulerView_displayValueLevel, displayValueLevel);

            orientation = a.getInt(R.styleable.RulerView_direction, orientation);
            align = a.getInt(R.styleable.RulerView_align, align);
            //根据水平/垂直方向设置数值方向
            order = a.getInt(R.styleable.RulerView_order, orientation == HORIZONTAL ? ASC : DESC);

            labelFontSize = a.getDimensionPixelSize(R.styleable.RulerView_labelFontSize, 12);
            labelFontColor = a.getColor(R.styleable.RulerView_labelFontColor, labelFontColor);
            labelOffset = a.getDimensionPixelSize(R.styleable.RulerView_labelOffset, labelOffset);

            contentPaddingLeft = getPaddingLeft();
            contentPaddingTop = getPaddingTop();
            contentPaddingRight = getPaddingRight();
            contentPaddingBottom = getPaddingBottom();

            Drawable pointerDrawable = a.getDrawable(R.styleable.RulerView_pointerDrawable);
            if (pointerDrawable != null) {
                pointer = BitmapUtil.drawableToBitmap(pointerDrawable);
            }
            Drawable frontDrawable = a.getDrawable(R.styleable.RulerView_frontDrawable);
            if (frontDrawable != null) {
                frontShade = BitmapUtil.drawableToBitmap(frontDrawable);
            }

            String tickFormat = a.getString(R.styleable.RulerView_tickFormat);
            if (!TextUtils.isEmpty(tickFormat)) {
                char[] formats = tickFormat.toCharArray();
                int[] injectMap = new int[formats.length];
                for (int i = 0; i < formats.length; i++) {
                    char c = formats[i];
                    if (c >= '0' && c <= '4') {
                        injectMap[i] = Integer.parseInt(String.valueOf(c));
                    } else {
                        throw new RuntimeException("TickFormat value must be in the range [0~4] !");
                    }
                }

                tickInjectMap = injectMap;
            }

            Drawable tick0 = a.getDrawable(R.styleable.RulerView_tick0);
            Drawable tick1 = a.getDrawable(R.styleable.RulerView_tick1);
            Drawable tick2 = a.getDrawable(R.styleable.RulerView_tick2);
            Drawable tick3 = a.getDrawable(R.styleable.RulerView_tick3);
            Drawable tick4 = a.getDrawable(R.styleable.RulerView_tick4);

            tickBitmapArray[0] = BitmapUtil.drawableToBitmap(tick0);
            tickBitmapArray[1] = BitmapUtil.drawableToBitmap(tick1);
            tickBitmapArray[2] = BitmapUtil.drawableToBitmap(tick2);
            tickBitmapArray[3] = BitmapUtil.drawableToBitmap(tick3);
            tickBitmapArray[4] = BitmapUtil.drawableToBitmap(tick4);

            a.recycle();
        }

        //如果设置的值为0，则默认选择最大Level显示数值
        if (displayValueLevel < 0) {
            for (int i : tickInjectMap) {
                displayValueLevel = Math.max(i, displayValueLevel);
            }
        }
    }

    /**
     * 获取内容区域宽度
     */
    public int getContentAreaWidth() {
        return getWidth() - contentPaddingLeft - contentPaddingRight;
    }

    /**
     * 获取内容区域高度
     */
    public int getContentAreaHeight() {
        return getHeight() - contentPaddingTop - contentPaddingBottom;
    }

    private int getContentLength() {
        return orientation == HORIZONTAL ? getContentAreaWidth() : getContentAreaHeight();
    }

    private int getContentHeight() {
        return orientation == HORIZONTAL ? getContentAreaHeight() : getContentAreaWidth();
    }

    private int getBitmapLength(Bitmap bitmap) {
        return orientation == HORIZONTAL ? bitmap.getWidth() : bitmap.getHeight();
    }

    private int getBitmapHeight(Bitmap bitmap) {
        return orientation == HORIZONTAL ? bitmap.getHeight() : bitmap.getWidth();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Bitmap getEachTickMapByType(int type) {
        Bitmap simpleTickBitmap = tickBitmapArray[type];
        if (simpleTickBitmap == null) {
            int tickHeight = (type + 1) * unitHeight;
            if (orientation == HORIZONTAL) {
                simpleTickBitmap = Bitmap.createBitmap(defaultTickWidth, tickHeight, Bitmap.Config.RGB_565);
            } else if (orientation == VERTICAL) {
                simpleTickBitmap = Bitmap.createBitmap(tickHeight, defaultTickWidth, Bitmap.Config.RGB_565);
            }

            //noinspection ConstantConditions
            Canvas canvas = new Canvas(simpleTickBitmap);
            canvas.drawColor(defaultTickColor);

            tickBitmapArray[type] = simpleTickBitmap;
        }
        return simpleTickBitmap;
    }

    private Bitmap getSrcTickBitmap() {
        if (fullTickContentMap == null) {

            int contentLength = getContentLength();
            int contentHeight = getContentHeight();

            //内容区显示值区间
            float contentSpanValue = contentLength / interval * tickValue;

            float groupValueSpan = tickValue * tickInjectMap.length;
            int startIndex = (int) Math.floor(minValue / groupValueSpan);
            int endIndex = (int) Math.ceil(maxValue / groupValueSpan);
            int displayScale = (int) Math.ceil(contentSpanValue / groupValueSpan);

            int tickUnitGroupLength = tickInjectMap.length;
            int tickUnitGroupWidth = interval * tickUnitGroupLength;

            int tickDisplayTime = Math.min(endIndex - startIndex, displayScale + 1);
            int tickMapLength = tickDisplayTime * tickUnitGroupWidth + contentLength;

            if (orientation == HORIZONTAL) {
                fullTickContentMap = Bitmap.createBitmap(tickMapLength, contentHeight
                        , Bitmap.Config.ARGB_8888);
            } else if (orientation == VERTICAL) {
                //noinspection SuspiciousNameCombination
                fullTickContentMap = Bitmap.createBitmap(contentHeight, tickMapLength
                        , Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(fullTickContentMap);
            Paint p = new Paint();

            int startOffset = contentLength / 2;
            for (int i = 0; i < tickDisplayTime; i++) {
                for (int j = 0; j < tickUnitGroupLength; j++) {
                    //根据数值变化顺序调整描画刻度的顺序
                    Bitmap tMap = getEachTickMapByType(tickInjectMap[j]);

                    int offset = startOffset + i * tickUnitGroupWidth + j * interval;
                    drawTick(fullTickContentMap, canvas, p, offset, tMap);
                }
            }

            //画结尾的刻度线
            Bitmap tMap = getEachTickMapByType(tickInjectMap[0]);
            int offset = startOffset + tickDisplayTime * tickUnitGroupWidth;
            drawTick(fullTickContentMap, canvas, p, offset, tMap);
        }
        return fullTickContentMap;
    }

    /**
     * 根据布局和对齐方式画刻度线
     *
     * @param offset  相对开始的偏移量
     * @param tickMap 刻度线的Bitmap对象
     */
    private void drawTick(Bitmap containerMap, Canvas canvas, Paint p, int offset, Bitmap tickMap) {
        int start = 0;
        int top = 0;

        if (order == ASC) {
            start = offset - getBitmapLength(tickMap) / 2;
        } else if (order == DESC) {
            start = getBitmapLength(containerMap) - offset - getBitmapLength(tickMap) / 2;
        }

        if (align == START) {
            top = 0;
        } else if (align == END) {
            top = getContentHeight() - getBitmapHeight(tickMap);
        }

        if (orientation == HORIZONTAL) {
            canvas.drawBitmap(tickMap, start, top, p);
        } else if (orientation == VERTICAL) {
            //noinspection SuspiciousNameCombination
            canvas.drawBitmap(tickMap, top, start, p);
        }
    }

    public void setValue(float value) {
        if (value < minValue || value > maxValue) {
            throw new RuntimeException("value is invalid, it must be in the range(minValue ~ maxValue)");
        }

        this.curValue = value;
        invalidate();
    }

    public RulerView setListener(OnValueChangedListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: curValue-----start" + curValue);

        super.onDraw(canvas);

        int width = getWidth(), height = getHeight();
        Rect rectView = new Rect(0, 0, width, height);
        Paint paint = new Paint();

        //显示数值区域长度
        int contentLength = getContentLength();
        int contentHeight = getContentHeight();

        //可见范围内的标尺数值差值
        float displaySpanValue = contentLength / (interval / tickValue);

        //画刻度尺
        drawTickMark(canvas, paint, contentLength, contentHeight, displaySpanValue);

        //画数
        drawTickNumber(canvas, paint, contentLength, displaySpanValue);

        //画指针
        if (pointer != null) {
            if (orientation == HORIZONTAL) {
                canvas.drawBitmap(pointer, contentPaddingLeft + (contentLength - pointer.getWidth()) / 2
                        , align == END ? (contentPaddingTop + getContentHeight() - getBitmapHeight(pointer)) : contentPaddingTop
                        , paint);
            } else if (orientation == VERTICAL) {
                canvas.drawBitmap(pointer, align == END ? (contentPaddingLeft + getContentHeight() - getBitmapHeight(pointer)) : contentPaddingLeft, contentPaddingTop + (contentLength - pointer.getHeight()) / 2, paint);
            }
        }

        //画最上层遮罩
        if (frontShade != null) {
            canvas.drawBitmap(frontShade, null, rectView, paint);
        }

        Log.d(TAG, "onDraw: curValue-----end" + curValue);
    }

    /**
     * 画刻度尺
     *
     * @param canvas
     * @param paint
     * @param contentLength
     * @param contentHeight
     * @param displaySpanValue
     */
    private void drawTickMark(Canvas canvas, Paint paint, int contentLength, int contentHeight, float displaySpanValue) {
        float minSpanValue = curValue - minValue;
        float maxSpanValue = maxValue - curValue;

        float offsetMin = -1, offsetMax = -1;
        if (maxSpanValue <= displaySpanValue / 2) {
            offsetMax = maxSpanValue * interval / tickValue;
        } else {
            offsetMin = minSpanValue * interval / tickValue;

            if (minSpanValue > displaySpanValue / 2) {
                float unitValue = tickValue * tickInjectMap.length;
                float spanValue = minSpanValue - displaySpanValue / 2;

                while (spanValue > unitValue) {
                    spanValue -= unitValue;
                }

                offsetMin = (spanValue + displaySpanValue / 2) * interval / tickValue;
            }
        }

        //画刻度
        Bitmap tickBitmap = getSrcTickBitmap();
        int pixelOffsetStart = 0;
        if (order == ASC) {
            if (offsetMax >= 0) {
                pixelOffsetStart = getBitmapLength(tickBitmap) - Math.round(offsetMax) - contentLength;
            } else {
                pixelOffsetStart = Math.round(offsetMin);
            }
        } else if (order == DESC) {
            if (offsetMax >= 0) {
                pixelOffsetStart = Math.round(offsetMax);
            } else {
                pixelOffsetStart = getBitmapLength(tickBitmap) - Math.round(offsetMin) - contentLength;
            }
        }

        Rect src;
        if (orientation == HORIZONTAL) {
            src = new Rect(pixelOffsetStart, 0, pixelOffsetStart + contentLength, contentHeight);
        } else {
            src = new Rect(0, pixelOffsetStart, contentHeight, pixelOffsetStart + contentLength);
        }

        Rect dest = new Rect(contentPaddingLeft, contentPaddingTop
                , contentPaddingLeft + getContentAreaWidth(), contentPaddingTop + getContentAreaHeight());
        canvas.drawBitmap(tickBitmap, src, dest, paint);
    }

    private void drawTickNumber(Canvas canvas, Paint paint, int contentLength, float displaySpanValue) {
        float startValue = curValue - displaySpanValue / 2;
        float endValue = curValue + displaySpanValue / 2;
        float groupValueSpan = tickValue * tickInjectMap.length;

        float startDrawValue = (float) Math.floor(startValue / groupValueSpan) * groupValueSpan;

        paint.setColor(labelFontColor);
        paint.setTextSize(labelFontSize);

        Log.d(TAG, String.format("onDraw: startDrawValue:%f, endValue:%f, groupValueSpan: %f, displayValueLevel:%d"
                , startDrawValue, endValue, groupValueSpan, displayValueLevel));

        for (float v = startDrawValue; v < endValue; v += groupValueSpan) {
            float startOffset = (v - startValue) / tickValue * interval;

            for (int i = 0; i < tickInjectMap.length; i++) {
                int curLevel = tickInjectMap[i];
                if (curLevel < displayValueLevel) {
                    continue;
                }

                float value = v + i * tickValue;

                if (minValue > value || maxValue < value) {
                    continue;
                }

                float offset = startOffset + i * interval;
                String labelText = mFormater.format(value);

                if (orientation == HORIZONTAL) {
                    float labelTop = contentPaddingTop + labelOffset;
                    if (align == END) {
                        labelTop = contentPaddingTop + getContentHeight() - labelOffset;
                    }

                    if (order == ASC) {
                        CanvasHelper.drawCenterText(canvas, paint, labelText, offset + contentPaddingLeft, labelTop);
                    } else if (order == DESC) {
                        CanvasHelper.drawCenterText(canvas, paint, labelText, contentPaddingLeft + (contentLength - offset), labelTop);
                    }
                } else if (orientation == VERTICAL) {
                    float labelLeft = contentPaddingLeft + labelOffset;
                    if (align == END) {
                        labelLeft = contentPaddingLeft + getContentHeight() - labelOffset;
                    }

                    if (order == ASC) {
                        CanvasHelper.drawCenterText(canvas, paint, labelText, labelLeft, contentPaddingTop + offset);
                    } else if (order == DESC) {
                        CanvasHelper.drawCenterText(canvas, paint, labelText, labelLeft, contentPaddingTop + (contentLength - offset));
                    }
                }
            }
        }
    }

    float preDownValue;
    float moveDistance;

    boolean isKeyDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, String.format("onTouchEvent: %d", event.getAction()));
        boolean isUpEvent = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preDownValue = curValue;
                moveDistance = 0;
                isKeyDown = true;
                break;
            case MotionEvent.ACTION_UP:
                isUpEvent = true;
                isKeyDown = false;
                break;
        }

        gestureDetector.onTouchEvent(event);

        //解决滑动没有触发onFilling事件时，数值不为tickValue整数倍的问题
        if (isUpEvent && !isFilling) {
            curValue = Math.round(curValue / tickValue) * tickValue;
            triggerValueChanged();
        }

        return true;
    }

    private boolean moveRuler(int pixel) {
        return moveRuler(pixel, curValue);
    }

    private boolean moveRuler(int pixel, float lastValue) {
        Log.d(TAG, String.format("moveRuler------pixel:%d, lastValue: %f", pixel, lastValue));

        if (pixel != 0) {
            if ((order == DESC && orientation == HORIZONTAL)
                    || (order == DESC && orientation == VERTICAL)) {
                pixel = -pixel;
            }

            if (pixel < 0 && curValue <= minValue
                    || pixel > 0 && curValue >= maxValue) {
                return false;
            }

            float pixelValue = pixel / (interval / tickValue);
            curValue = lastValue + pixelValue;

            curValue = Math.max(minValue, Math.min(curValue, maxValue));

            triggerValueChanged();

            return true;
        }
        return false;
    }

    private void triggerValueChanged() {
        postInvalidate();

        if (mListener != null) {
            mListener.onValueChanged(curValue, !(isFilling || isKeyDown));
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (orientation == HORIZONTAL) {
            moveDistance += distanceX;
        } else if (orientation == VERTICAL) {
            moveDistance += distanceY;
        }
        return moveRuler(Math.round(moveDistance), preDownValue);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    boolean isFilling = false;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float scale = interval / tickValue;
        int min = Math.round((curValue - minValue) * scale);
        int max = Math.round((maxValue - curValue) * scale);

        mScroller.fling(Math.round(e2.getX()), Math.round(e2.getY()), Math.round(velocityX), Math.round(velocityY)
                , Math.round(e2.getX()) - min, Math.round(e2.getX()) + max
                , Math.round(e2.getY()) - min, Math.round(e2.getY()) + max);

        Log.d(TAG, String.format("onFling----velocityX:%f, finalX:%d", velocityX, mScroller.getFinalX()));

        postDelayed(this, FILLING_REFRESH_INTERVAL);
        isFilling = true;
        return false;
    }

    @Override
    public void run() {
        if (isFilling) {
            boolean computeResult = mScroller.computeScrollOffset();
            Log.d(TAG, String.format("run----computeResult:%b", computeResult));
            if (!computeResult) return;

            boolean isFinished = mScroller.isFinished();
            int fillingDistance = orientation == HORIZONTAL ? (mScroller.getStartX() - mScroller.getCurrX()) : (mScroller.getStartY() - mScroller.getCurrY());
            moveRuler(fillingDistance + Math.round(moveDistance), preDownValue);
            Log.d(TAG, String.format("run----isFinished:%b, finalX:%d, start:%d, cur: %d"
                    , isFinished, mScroller.getFinalX(), mScroller.getStartX(), mScroller.getCurrX()));

            if (isFinished) {
                curValue = Math.round(curValue / tickValue) * tickValue;
                isFilling = false;

                triggerValueChanged();
            }
            //如果当前值为极值，则完成滑动事件
            else if (curValue == maxValue || curValue == minValue) {
                isFilling = false;

                triggerValueChanged();
            }

            if (isFilling) {
                postDelayed(this, FILLING_REFRESH_INTERVAL);
            }
        }
    }

    /**
     * 滑动触发的值修改事件监听
     */
    public interface OnValueChangedListener {
        /**
         * Ruler值变化回调方法
         *
         * @param value        当前值
         * @param isFinalValue 是否是最终结果（用于排除滑动过程中的中间值）
         */
        void onValueChanged(float value, boolean isFinalValue);
    }

    public interface RulerValueFormater {
        /**
         * 将尺子的值转换为相应的字符串显示
         *
         * @param value
         * @return
         */
        String format(float value);
    }
}
