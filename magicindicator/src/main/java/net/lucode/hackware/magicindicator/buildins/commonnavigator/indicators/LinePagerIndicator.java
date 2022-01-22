package net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.Arrays;
import java.util.List;

/**
 * 直线viewpager指示器，带颜色渐变
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class LinePagerIndicator extends View implements IPagerIndicator {
    public static final int MODE_MATCH_EDGE = 0;   // 直线宽度 == title宽度 - 2 * mXOffset
    public static final int MODE_WRAP_CONTENT = 1;    // 直线宽度 == title内容宽度 - 2 * mXOffset
    public static final int MODE_EXACTLY = 2;  // 直线宽度 == mLineWidth

    private int mMode;  // 默认为MODE_MATCH_EDGE模式

    // 控制动画
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    private float mYOffset;   // 相对于底部的偏移量，如果你想让直线位于title上方，设置它即可
    private float mLineHeight;
    private float mXOffset;
    private float mLineWidth;
    private float mRoundRadius;

    private Paint mPaint;
    private List<PositionData> mPositionDataList;
    private List<Integer> mColors;

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    boolean vertical;

    public int getVerticalMode() {
        return verticalMode;
    }

    public void setVerticalMode(int verticalMode) {
        this.verticalMode = verticalMode;
    }

    int verticalMode;
    private RectF mLineRect = new RectF();

    public LinePagerIndicator(Context context) {
        super(context);
        init(context);
    }

    public LinePagerIndicator(Context context, boolean vertical) {
        super(context);
        this.vertical = vertical;
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mLineHeight = UIUtil.dip2px(context,3);// UIUtil.dip2px(context, vertical ? 10 : 3);
        mLineWidth = UIUtil.dip2px(context,10);// UIUtil.dip2px(context, vertical ? 3 : 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(mLineRect, mRoundRadius, mRoundRadius, mPaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算颜色
        if (mColors != null && mColors.size() > 0) {
            int currentColor = mColors.get(Math.abs(position) % mColors.size());
            int nextColor = mColors.get(Math.abs(position + 1) % mColors.size());
            int color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor);
            mPaint.setColor(color);

        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position, vertical);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1, vertical);

        float leftX;
        float nextLeftX;
        float rightX;
        float nextRightX;
        if (mMode == MODE_MATCH_EDGE) {
            if (isVertical()) {
                leftX = current.mTop + mXOffset;
                nextLeftX = next.mTop + mXOffset;
                rightX = current.mBottom - mXOffset;
                nextRightX = next.mBottom - mXOffset;
            } else {

                leftX = current.mLeft + mXOffset;
                nextLeftX = next.mLeft + mXOffset;
                rightX = current.mRight - mXOffset;
                nextRightX = next.mRight - mXOffset;
            }
        } else if (mMode == MODE_WRAP_CONTENT) {
            if (isVertical()) {
                leftX = current.mContentTop + mXOffset;
                nextLeftX = next.mContentTop + mXOffset;
                rightX = current.mContentBottom - mXOffset;
                nextRightX = next.mContentBottom - mXOffset;
            } else {

                leftX = current.mContentLeft + mXOffset;
                nextLeftX = next.mContentLeft + mXOffset;
                rightX = current.mContentRight - mXOffset;
                nextRightX = next.mContentRight - mXOffset;
            }
        } else {    // MODE_EXACTLY
            if (isVertical()) {
                leftX = current.mTop + (current.height() - mLineWidth) / 2;
                rightX = current.mTop + (current.height() + mLineWidth) / 2;

                nextLeftX = next.mTop + (next.height() - mLineWidth) / 2;
                nextRightX = next.mTop + (next.height() + mLineWidth) / 2;
            } else {
                leftX = current.mLeft + (current.width() - mLineWidth) / 2;
                rightX = current.mLeft + (current.width() + mLineWidth) / 2;
                nextLeftX = next.mLeft + (next.width() - mLineWidth) / 2;
                nextRightX = next.mLeft + (next.width() + mLineWidth) / 2;

            }
        }

        if (mMode != MODE_MATCH_EDGE && vertical) {
            mLineRect.top = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
            mLineRect.bottom = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
            mLineRect.left = getWidth() - mLineHeight - mXOffset;//这句话在右边显示竖线的时候很重要
            mLineRect.right = getWidth() - mYOffset;
        } else {
            if (vertical) {
                mLineRect.top = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
                mLineRect.bottom = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
//            mLineRect.left = getWidth() - mLineHeight - mYOffset;
                mLineRect.right = getWidth() - mYOffset;

            } else {
                mLineRect.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
                mLineRect.right = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
                mLineRect.top = getHeight() - mLineHeight - mYOffset;
                mLineRect.bottom = getHeight() - mYOffset;

            }

        }

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public void setYOffset(float yOffset) {
        mYOffset = yOffset;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public void setXOffset(float xOffset) {
        mXOffset = xOffset;
    }

    public float getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(float lineHeight) {
        mLineHeight = lineHeight;
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    public float getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(float roundRadius) {
        mRoundRadius = roundRadius;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        if (mode == MODE_EXACTLY || mode == MODE_MATCH_EDGE || mode == MODE_WRAP_CONTENT) {
            mMode = mode;
        } else {
            throw new IllegalArgumentException("mode " + mode + " not supported.");
        }
    }

    public Paint getPaint() {
        return mPaint;
    }

    public List<Integer> getColors() {
        return mColors;
    }

    public void setColors(Integer... colors) {
        mColors = Arrays.asList(colors);
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public Interpolator getEndInterpolator() {
        return mEndInterpolator;
    }

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new LinearInterpolator();
        }
    }
}
