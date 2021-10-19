package com.heweather.owp.view.horizonview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.heweather.owp.R;
import com.heweather.owp.utils.ContentUtil;
import com.heweather.owp.utils.DisplayUtil;
import com.heweather.owp.utils.IconUtils;
import com.heweather.owp.utils.TransUnitUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherHourlyBean;

/**
 * Created by niu on 18/11/22.
 */

public class HourlyForecastView extends View implements ScrollWatcher {


    private Context mContext;
    //折线
    private Paint foldLinePaint;
    private Paint backPaint;
    //底线
    private Paint baseLinePaint;
    //虚线
    private Paint dashPaint;
    //文字
    private Paint textPaint;
    //图片
    private Paint bitmapPaint;

    //文本的大小
    private int textSize;

    //数据
    private List<WeatherHourlyBean.HourlyBean> hourlyWeatherList;

    //画虚线的点的index
    private List<Integer> dashLineList;


    private int screenWidth;
    //每个item的宽度
    private int itemWidth;
    //温度基准高度
    private int lowestTempHeight;
    //温度基准高度
    private int highestTempHeight;
    //最低温
    private int lowestTemp;
    //最高温
    private int highestTemp;


    //默认图片绘制位置
    float bitmapHeight;
    //默认图片宽高
    float bitmapXY;


    //View宽高
    private int mWidth;
    private int mHeight;
    //默认高
    private int defHeightPixel = 0;
    private int defWidthPixel = 0;

    private int paddingL = 0;
    private int paddingT = 0;
    private int paddingR = 0;
    private int paddingB = 0;

    private int mScrollX = 0;
    private float baseLineHeight;
    private Paint paint1;
    private boolean isDark = false;


    public HourlyForecastView(Context context) {
        this(context, null);
    }

    public HourlyForecastView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HourlyForecastView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HourlyForecastView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        if (ContentUtil.APP_SETTING_THEME.equals("深色")) {
            isDark = true;
        } else {
            isDark = false;
        }
        initDefValue();
        initPaint();
    }

    private static int ITEM_SIZE = 24;


    public void initData(List<WeatherHourlyBean.HourlyBean> weatherData) {

        hourlyWeatherList = weatherData;
        int size = weatherData.size();
        ITEM_SIZE = size;

        dashLineList = new ArrayList<>();

        Iterator iterator = weatherData.iterator();
        WeatherHourlyBean.HourlyBean hourlyBase;
        String lastText = "";


        int idx = 0;
        while (iterator.hasNext()) {
            hourlyBase = (WeatherHourlyBean.HourlyBean) iterator.next();
            if (!hourlyBase.getIcon().equals(lastText)) {
                if (idx != size - 1) {
                    dashLineList.add(idx);//从0开始添加虚线位置的索引值idx
                    lastText = hourlyBase.getIcon();
                }
            }
            idx++;
        }
        dashLineList.add(size - 1);//添加最后一条虚线位置的索引值idx
        invalidate();
    }


    private void initDefValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        itemWidth = DisplayUtil.dp2px(mContext, 30);

        defWidthPixel = itemWidth * (ITEM_SIZE - 1);
        defHeightPixel = DisplayUtil.dp2px(mContext, 80);

        lowestTempHeight = DisplayUtil.dp2px(mContext, 40);//长度  非y轴值
        highestTempHeight = DisplayUtil.dp2px(mContext, 70);
        //defPadding
        paddingT = DisplayUtil.dp2px(mContext, 20);
        paddingL = DisplayUtil.dp2px(mContext, 10);
        paddingR = DisplayUtil.dp2px(mContext, 15);

        textSize = DisplayUtil.sp2px(mContext, 12);


        bitmapHeight = 1 / 2f * (2 * defHeightPixel - lowestTempHeight) + DisplayUtil.dp2px(mContext, 2);//- 给文字留地方
        bitmapXY = 18;

    }

    private TextPaint textLinePaint;

    private void initPaint() {
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(mContext.getResources().getColor(R.color.line_back_dark));
        paint1.setStyle(Paint.Style.FILL);

        foldLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foldLinePaint.setStyle(Paint.Style.STROKE);
        foldLinePaint.setStrokeWidth(5);
        foldLinePaint.setColor(mContext.getResources().getColor(R.color.line_color));

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setStrokeWidth(2);
        backPaint.setAntiAlias(true);

        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(mContext.getResources().getColor(R.color.back_white));
        DashPathEffect pathEffect = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        dashPaint.setPathEffect(pathEffect);
        dashPaint.setStrokeWidth(3);
        dashPaint.setAntiAlias(true);
        dashPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);

        textLinePaint = new TextPaint();
        textLinePaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
        textLinePaint.setAntiAlias(true);

        textLinePaint.setColor(mContext.getResources().getColor(R.color.black));
        textPaint.setColor(mContext.getResources().getColor(R.color.search_light_un_color));

        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePaint.setStrokeWidth(3);
        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setColor(mContext.getResources().getColor(R.color.slategray));


        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);//图像滤波处理
        bitmapPaint.setDither(true);//防抖动
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //当设置的padding值小于默认值是设置为默认值
        paddingT = DisplayUtil.dp2px(mContext, 20);
        paddingL = DisplayUtil.dp2px(mContext, 10);
        paddingR = DisplayUtil.dp2px(mContext, 15);
        paddingB = Math.max(paddingB, getPaddingBottom());

        //获取测量模式
        //注意 HorizontalScrollView的子View 在没有明确指定dp值的情况下 widthMode总是MeasureSpec.UNSPECIFIED
        //同理 ScrollView的子View的heightMode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize + paddingL + paddingR;
            mHeight = heightSize;
        }

        //如果为wrap_content 那么View大小为默认值
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.AT_MOST) {
            mWidth = defWidthPixel + paddingL + paddingR;
            mHeight = defHeightPixel + paddingT + paddingB;
        }

        //设置视图的大小
        setMeasuredDimension(mWidth, mHeight);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDefValue();
        initPaint();
        if (hourlyWeatherList != null && hourlyWeatherList.size() != 0) {
            drawLines(canvas);
            drawBitmaps(canvas);
            drawTemp(canvas);
        }
    }

    private void drawTemp(Canvas canvas) {
        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            if (currentItemIndex == i) {
                //计算提示文字的运动轨迹
//                int Y = getTempBarY(i);
                String tmp = hourlyWeatherList.get(i).getTemp();
                float temp = Integer.parseInt(tmp);
                int Y = (int) (tempHeightPixel(temp) + paddingT);
                //画出温度提示
                int offset = itemWidth / 4;
                Rect targetRect = new Rect(getScrollBarX(), Y - DisplayUtil.dip2px(getContext(), 24)
                        , getScrollBarX() + offset, Y - DisplayUtil.dip2px(getContext(), 4));
                Paint.FontMetricsInt fontMetrics = textLinePaint.getFontMetricsInt();
                int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                textLinePaint.setTextAlign(Paint.Align.LEFT);
                if (ContentUtil.APP_SETTING_UNIT.equals("hua")) {
                    tmp = String.valueOf(TransUnitUtil.getF(tmp));
                }
                canvas.drawText(tmp + "°", targetRect.centerX(), baseline, textLinePaint);
            }
        }
    }

    private void drawBitmaps(Canvas canvas) {

        int scrollX = mScrollX;
        boolean leftHide;
        boolean rightHide;
        for (int i = 0; i < dashLineList.size() - 1; i++) {
            leftHide = true;
            rightHide = true;

            int left = itemWidth * dashLineList.get(i) + paddingL;
            int right = itemWidth * dashLineList.get(i + 1) + paddingL;
            //图的中间位置  drawBitmap是左边开始画
            float drawPoint = 0;
            if (left > scrollX && left < scrollX + screenWidth) {
                leftHide = false;//左边缘显示
            }
            if (right > scrollX && right < scrollX + screenWidth) {
                rightHide = false;
            }


            if (!leftHide && !rightHide) {//左右边缘都显示
                drawPoint = (left + right) / 2f;

            } else if (leftHide && !rightHide) {//右边缘与屏幕左边

                drawPoint = (scrollX + right) / 2f;
            } else if (!leftHide) {//左边缘与屏幕右边
                //rightHide is True when reach this statement
                drawPoint = (left + screenWidth + scrollX) / 2f;

            } else {//左右边缘都不显示
                if (right < scrollX + screenWidth) { //左右边缘都在屏幕左边
                    continue;
                } else if (left > scrollX + screenWidth) {//左右边缘都在屏幕右边
                    continue;
                } else {
                    drawPoint = (screenWidth) / 2f + scrollX;
                }
            }


            String code = hourlyWeatherList.get(dashLineList.get(i)).getIcon();
            BitmapDrawable bd;


            if (code.contains("d")) {
                bd = (BitmapDrawable) mContext.getResources().getDrawable(IconUtils.getDayIconDark(code.replace("d", "")));
            } else {
                bd = (BitmapDrawable) mContext.getResources().getDrawable(IconUtils.getNightIconDark(code.replace("n", "")));
            }

            assert bd != null;
            Bitmap bitmap = DisplayUtil.bitmapResize(bd.getBitmap(),
                    DisplayUtil.dp2px(mContext, bitmapXY), DisplayUtil.dp2px(mContext, bitmapXY));

            //越界判断
            if (drawPoint >= right - bitmap.getWidth() / 2f) {
                drawPoint = right - bitmap.getWidth() / 2f;
            }
            if (drawPoint <= left + bitmap.getWidth() / 2f) {
                drawPoint = left + bitmap.getWidth() / 2f;
            }

            drawBitmap(canvas, bitmap, drawPoint, bitmapHeight);
//            String text = hourlyWeatherList.get(dashLineList.get(i)).getCond_txt();
//            textPaint.setTextSize(DisplayUtil.sp2px(mContext, 8));
//            canvas.drawText(text, drawPoint, bitmapHeight + bitmap.getHeight() + 100 / 3f, textPaint);

        }

    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
        canvas.save();
        canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top, bitmapPaint);
        canvas.restore();
    }


    private void drawLines(Canvas canvas) {
        //底部的线的高度 高度为控件高度减去text高度的1.5倍
        baseLineHeight = mHeight - 1.5f * textSize;
        Path path = new Path();
        List<Float> dashWidth = new ArrayList<>();
        List<Float> dashHeight = new ArrayList<>();

        List<Point> mPointList = new ArrayList<>();

        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            float temp = Integer.parseInt(hourlyWeatherList.get(i).getTemp());

            float w = itemWidth * i + paddingL;
            float h = tempHeightPixel(temp) + paddingT;
            Point point = new Point((int) w, (int) h);
            mPointList.add(point);
            //画虚线
            if (dashLineList.contains(i)) {
                dashWidth.add(w);
                dashHeight.add(h);
            }
        }

        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;

        for (int valueIndex = 0; valueIndex < hourlyWeatherList.size(); ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = mPointList.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = mPointList.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = mPointList.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < hourlyWeatherList.size() - 1) {
                Point point = mPointList.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                path.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (0.2F * firstDiffX);
                final float firstControlPointY = previousPointY + (0.2F * firstDiffY);
                final float secondControlPointX = currentPointX - (0.2F * secondDiffX);
                final float secondControlPointY = currentPointY - (0.2F * secondDiffY);
                //画出曲线
                path.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
            }

            // 更新值,
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }

        //画折线
        canvas.drawPath(path, foldLinePaint);

        path.lineTo(mWidth - paddingR, baseLineHeight);
        path.lineTo(paddingL, baseLineHeight);
        //画阴影
        int[] shadeColors = new int[]{
                Color.argb(100, 145, 145, 145), Color.argb(30, 145, 145, 145),
                Color.argb(18, 237, 238, 240)};

        Shader mShader = new LinearGradient(0, 0, 0, getHeight(), shadeColors, null, Shader.TileMode.CLAMP);

        backPaint.setShader(mShader);
        canvas.drawPath(path, backPaint);

        //画虚线
        drawDashLine(dashWidth, dashHeight, canvas);

        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            float temp = Integer.parseInt(hourlyWeatherList.get(i).getTemp());

            float w = itemWidth * i + paddingL;
            float h = tempHeightPixel(temp) + paddingT;

            //画时间
            String time = hourlyWeatherList.get(i).getFxTime();
            //画时间
            if (ITEM_SIZE > 8) {
                if (i % 2 == 0) {
                    if (i == 0) {
                        textPaint.setTextAlign(Paint.Align.LEFT);
                    } else {
                        textPaint.setTextAlign(Paint.Align.CENTER);
                    }
                    canvas.drawText(time.substring(time.length() - 11, time.length() - 6), w, baseLineHeight + textSize + DisplayUtil.dip2px(mContext, 3), textPaint);
                }
            } else {
                textPaint.setTextAlign(Paint.Align.CENTER);
                if (i == 0) {
                    canvas.drawText(mContext.getString(R.string.now), w, baseLineHeight + textSize + DisplayUtil.dip2px(mContext, 3), textPaint);
                } else {
                    canvas.drawText(time.substring(time.length() - 11, time.length() - 6), w, baseLineHeight + textSize + DisplayUtil.dip2px(mContext, 3), textPaint);
                }
            }
        }

    }

    //画虚线
    private void drawDashLine(List<Float> dashWidth, List<Float> dashHeight, Canvas canvas) {
        if (dashHeight != null && dashHeight.size() > 1) {
            for (int i = 1; i < dashHeight.size() - 1; i++) {
                canvas.drawLine(dashWidth.get(i), dashHeight.get(i) + 3, dashWidth.get(i), baseLineHeight, dashPaint);
            }
        }
    }

    public float tempHeightPixel(float tmp) {
        float res = ((tmp - lowestTemp) / (highestTemp - lowestTemp)) * (highestTempHeight - lowestTempHeight) + lowestTempHeight;
        return defHeightPixel - res;//y从上到下
    }

    @Override
    public void update(int scrollX) {
        mScrollX = scrollX;
    }


    public void setLowestTemp(int lowestTemp) {
        this.lowestTemp = lowestTemp;
    }

    public void setHighestTemp(int highestTemp) {
        this.highestTemp = highestTemp;
    }

    private int maxScrollOffset = 0;//滚动条最长滚动距离
    private int scrollOffset = 0; //滚动条偏移量
    private int currentItemIndex = 0; //当前滚动的位置所对应的item下标

    //设置scrollerView的滚动条的位置，通过位置计算当前的时段
    public void setScrollOffset(int offset, int maxScrollOffset) {
        this.maxScrollOffset = maxScrollOffset + DisplayUtil.dp2px(mContext, 50);
        scrollOffset = offset;
        currentItemIndex = calculateItemIndex();
        invalidate();
    }

    //通过滚动条偏移量计算当前选择的时刻
    private int calculateItemIndex() {
        int x = getScrollBarX();
        int sum = paddingL - itemWidth / 2;
        for (int i = 0; i < ITEM_SIZE - 1; i++) {
            sum += itemWidth;
            if (x < sum)
                return i;
        }
        return ITEM_SIZE - 1;
    }

    private int getScrollBarX() {
        int x = (ITEM_SIZE - 1) * itemWidth * scrollOffset / maxScrollOffset;
        x = x - DisplayUtil.dp2px(mContext, 3);
        return x;
    }

}
