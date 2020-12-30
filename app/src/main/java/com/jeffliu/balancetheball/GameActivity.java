package com.jeffliu.balancetheball;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameActivity extends Activity implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private CountDownTimer mTimer = null;
    private boolean mFinished = false;
    private int mTickCount;
    MediaPlayer mTickPlayer;
    MediaPlayer mDingPlayer;

    private static class Ball
    {
        public float px = 100;
        public float vx = 0;
        public float py = 100;
        public float vy = 0;
        public final float r = 20;
    }

    private final Ball mBall = new Ball();

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViewById(R.id.game_layout).setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert mSensorManager != null;
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mTickPlayer = MediaPlayer.create(this, R.raw.tick);
        mDingPlayer = MediaPlayer.create(this, R.raw.ding);
    }

    @Override protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override public void onSensorChanged(SensorEvent event)
    {
        GameView gv = findViewById(R.id.game_view);

        float maxX = gv.getWidth() - mBall.r;
        float maxY = gv.getHeight() - mBall.r;

        float centerX = gv.getWidth()/2f;
        float centerY = gv.getHeight()/2f;
        float centerR = gv.getWidth()/8f;

        mBall.vx -= event.values[0] / 2;
        mBall.vy += event.values[1] / 2;
        mBall.px += mBall.vx;
        mBall.py += mBall.vy;

        if (mBall.px < mBall.r) {
            mBall.px = mBall.r;
            mBall.vx = 0;
        }

        if (mBall.px > maxX) {
            mBall.px = maxX;
            mBall.vx = 0;
        }

        if (mBall.py < mBall.r) {
            mBall.py = mBall.r;
            mBall.vy = 0;
        }

        if (mBall.py > maxY) {
            mBall.py = maxY;
            mBall.vy = 0;
        }

        double distance = Math.sqrt(Math.pow(mBall.px - centerX, 2) + Math.pow(mBall.py - centerY, 2));
        boolean in = distance < centerR;
        if (!in && mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (in &&  mTimer == null) {
            mTimer = new CountDownTimer(3000, 50) {
                @Override public void onTick(long millisUntilFinished) {
                    mTickCount++;
                    if (mTickCount == 20 || mTickCount == 40) {
                        mTickPlayer.start();
                    }
                }
                @Override public void onFinish() {
                    mFinished = true;
                    mDingPlayer.start();
                }
            };
            mTickCount = -1;
            mTimer.start();
        }

        Bundle ballBundle = new Bundle();
        ballBundle.putFloat("cx", mBall.px);
        ballBundle.putFloat("cy", mBall.py);
        ballBundle.putBoolean("in", in);
        if (in) {
            ballBundle.putFloat("inr", (float) Math.sqrt(Math.pow(centerR, 2f) * mTickCount / 60.0));
        }
        ballBundle.putBoolean("finished", mFinished);
        gv.setData(ballBundle);
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mFinished = false;
            mBall.px = 30;
            mBall.vx = 0;
            mBall.py = 30;
            mBall.vy = 0;
            return true;
        }
        return false;
    }
}
