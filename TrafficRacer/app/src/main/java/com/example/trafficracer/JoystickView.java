package com.example.trafficracer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class JoystickView extends View {
    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private float outerCircleRadius;
    private float innerCircleRadius;
    private float centerX;
    private float centerY;
    private float joystickX;
    private float joystickY;
    private boolean isPressed = false;
    private boolean isActive = false;

    private JoystickListener listener;

    public interface JoystickListener {
        void onJoystickMoved(float xPercent, float yPercent);
    }

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outerCirclePaint.setAlpha(100);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.RED);
        innerCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        outerCircleRadius = Math.min(w, h) / 2f * 0.8f;
        innerCircleRadius = outerCircleRadius * 0.4f;
        joystickX = centerX;
        joystickY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, outerCircleRadius, outerCirclePaint);
        canvas.drawCircle(joystickX, joystickY, innerCircleRadius, innerCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return handleActionDown(event);
            case MotionEvent.ACTION_MOVE:
                return handleActionMove(event);
            case MotionEvent.ACTION_UP:
                return handleActionUp();
        }
        return true;
    }

    private boolean handleActionDown(MotionEvent event) {
        if (isInCircle(event.getX(), event.getY())) {
            isPressed = true;
            isActive = true;
            updateJoystickPosition(event.getX(), event.getY());
            return true;
        }
        return false;
    }

    private boolean handleActionMove(MotionEvent event) {
        if (isPressed) {
            updateJoystickPosition(event.getX(), event.getY());
            return true;
        }
        return false;
    }

    private boolean handleActionUp() {
        isPressed = false;
        isActive = false;
        joystickX = centerX;
        joystickY = centerY;
        invalidate();
        if (listener != null) {
            listener.onJoystickMoved(0, 0);
        }
        return true;
    }

    private boolean isInCircle(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        return dx * dx + dy * dy <= outerCircleRadius * outerCircleRadius;
    }

    private void updateJoystickPosition(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > outerCircleRadius) {
            dx = dx * outerCircleRadius / distance;
            dy = dy * outerCircleRadius / distance;
        }

        joystickX = centerX + dx;
        joystickY = centerY + dy;
        invalidate();

        if (listener != null) {
            float xPercent = dx / outerCircleRadius;
            float yPercent = dy / outerCircleRadius;
            listener.onJoystickMoved(xPercent, yPercent);
        }
    }

    public void setJoystickListener(JoystickListener listener) {
        this.listener = listener;
    }

    public boolean isActive() {
        return isActive;
    }
}