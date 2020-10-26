package com.jeffliu.balancetheball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View
{
    private final Paint mBallPaint;
    private final Paint mTargetPaint;
    private final Paint mInPaint;
    private final Paint mFinishedPaint;

    private float mCenterX;
    private float mCenterY;

    private Bundle mBundle;

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mBallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBallPaint.setStyle(Paint.Style.FILL);
        mBallPaint.setColor(getResources().getColor(R.color.white));

        mTargetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTargetPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mInPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mInPaint.setColor(getResources().getColor(R.color.yellow));

        mFinishedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFinishedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFinishedPaint.setColor(getResources().getColor(R.color.green));

        mBundle = new Bundle();
    }

    public void setData(Bundle data)
    {
        mBundle = data;
        invalidate();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minWidth, widthMeasureSpec, 1);
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);
        setMeasuredDimension(w, h);

        mCenterX = getWidth() / 2f;
        mCenterY = getHeight() / 2f;
    }

    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX, mCenterY, getWidth()/8f, mTargetPaint);

        if (mBundle.getBoolean("finished", false)) {
            canvas.drawCircle(mCenterX, mCenterY, getWidth()/8f, mFinishedPaint);
        } else if (mBundle.getBoolean("in", false)) {
            canvas.drawCircle(mCenterX, mCenterY, mBundle.getFloat("inr", getWidth()/8f), mInPaint);
        }

        canvas.drawCircle(mBundle.getFloat("cx", 20), mBundle.getFloat("cy", 20), mBundle.getFloat("r", 20), mBallPaint);
    }
}
