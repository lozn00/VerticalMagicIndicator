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
    public static final float MATCH_PARENT = -1;

    private int mMode = MODE_MATCH_EDGE;  // 默认为MODE_MATCH_EDGE模式

    public interface  RectIntercept{
        boolean onIntercept(boolean vertical, RectF rectF, PositionData current, PositionData next, int width, int height, float lineHeight, float lineWidth);
    }

    public RectIntercept get_rectIntercept() {
        return _rectIntercept;
    }

    public void set_rectIntercept(RectIntercept _rectIntercept) {
        this._rectIntercept = _rectIntercept;
    }

    RectIntercept _rectIntercept;

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
        mLineHeight = vertical ? UIUtil.dip2px(context, 10) : UIUtil.dip2px(context, 3);// UIUtil.dip2px(context, vertical ? 10 : 3);
        mLineWidth = vertical ? UIUtil.dip2px(context, 1) : UIUtil.dip2px(context, 10);// UIUtil.dip2px(context, vertical ? 3 : 10);
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
        if (mLineWidth == LinePagerIndicator.MATCH_PARENT) {
            mLineWidth = getWidth();
        }
        if (mLineHeight == LinePagerIndicator.MATCH_PARENT) {
            mLineWidth = getHeight();
        }
        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position, vertical);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1, vertical);


        if(_rectIntercept!=null){
            if(_rectIntercept.onIntercept(vertical,mLineRect,current,next,getWidth(),getHeight(),getLineHeight(),getLineWidth())){
                return;
            }
        }

        float leftX;
        float nextLeftX;
        float rightX;
        float nextRightX;

        float topY = 0;
        float nextTopY = 0;
        float bottomY = 0;
        float nextBottomY = 0;
        if (mMode == MODE_MATCH_EDGE) {
            /*
                public static final int MODE_MATCH_EDGE = 0;   // 直线宽度 == title宽度 - 2 * mXOffset
    public static final int MODE_WRAP_CONTENT = 1;    // 直线宽度 == title内容宽度 - 2 * mXOffset
    public static final int MODE_EXACTLY = 2;  // 直线宽度 == mLineWidth
             */
            if (isVertical()) { // 直线宽度 == title宽度 - 2 * mXOffset
                topY = current.mTop + mYOffset;
                nextTopY = next.mTop + mYOffset;
                bottomY = current.mBottom - mYOffset;
                nextBottomY = next.mBottom - mYOffset;

                leftX = current.mLeft + mXOffset;
                nextLeftX = next.mLeft + mXOffset;
                rightX = current.mRight - mXOffset;
                nextRightX = next.mRight - mXOffset;
            } else {

                leftX = current.mLeft + mXOffset;
                nextLeftX = next.mLeft + mXOffset;
                rightX = current.mRight - mXOffset;
                nextRightX = next.mRight - mXOffset;

            }
        } else if (mMode == MODE_WRAP_CONTENT) {// 直线宽度 == title内容宽度 - 2 * mXOffset
            topY = current.mContentTop + mYOffset;
            nextTopY = next.mContentTop + mYOffset;
            bottomY = current.mContentBottom - mYOffset;
            nextBottomY = next.mContentBottom - mYOffset;

            leftX = current.mContentLeft + mXOffset;
            nextLeftX = next.mContentLeft + mXOffset;
            rightX = current.mContentRight - mXOffset;
            nextRightX = next.mContentRight - mXOffset;
        } else if (mMode == MODE_EXACTLY || true) {   // 直线宽度 == mLineWidth
            if (isVertical()) {
                topY = current.mTop + (current.height() - mLineHeight) / 2;
                bottomY = current.mTop + (current.height() + mLineHeight) / 2;
                nextTopY = next.mTop + (next.height() - mLineHeight) / 2;
                nextBottomY = next.mTop + (next.height() + mLineHeight) / 2;


                leftX = current.mLeft + (current.width() - mLineWidth) / 2;
                rightX = current.mLeft + (current.width() + mLineWidth) / 2;
                nextLeftX = next.mLeft + (next.width() - mLineWidth) / 2;
                nextRightX = next.mLeft + (next.width() + mLineWidth) / 2;


            } else {
                leftX = current.mLeft + (current.width() - mLineWidth) / 2;
                rightX = current.mLeft + (current.width() + mLineWidth) / 2;
                nextLeftX = next.mLeft + (next.width() - mLineWidth) / 2;
                nextRightX = next.mLeft + (next.width() + mLineWidth) / 2;

            }
        }

  /*      if (mMode != MODE_MATCH_EDGE && vertical) {
            mLineRect.top = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
            mLineRect.bottom = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
            mLineRect.left = getWidth() - mLineWidth - mXOffset;//这句话在右边显示竖线的时候很重要
            mLineRect.right = getWidth() - mXOffset;
        } else {*/
        if (vertical) {

            mLineRect.left = getWidth() - mLineWidth - mXOffset;
            mLineRect.right = getWidth() - mXOffset;
            if (mMode == MODE_MATCH_EDGE) {
            } else if (mMode == MODE_WRAP_CONTENT) {


            } else {
            }
                mLineRect.top = topY + (nextTopY - topY) * mStartInterpolator.getInterpolation(positionOffset);
                mLineRect.bottom = bottomY + (nextBottomY - bottomY) * mEndInterpolator.getInterpolation(positionOffset);

        } else {
            mLineRect.left = leftX + (nextLeftX - leftX) * mStartInterpolator.getInterpolation(positionOffset);
            mLineRect.right = rightX + (nextRightX - rightX) * mEndInterpolator.getInterpolation(positionOffset);
            mLineRect.top = getHeight() - mLineHeight - mYOffset;
            mLineRect.bottom = getHeight() - mYOffset;

        }

//        }

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
