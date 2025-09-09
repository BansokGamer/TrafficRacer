package com.example.trafficracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Enemy {
    public float x, y;
    public float width = 150;
    public float height = 250;
    private float speed = 15;
    private RectF rect = new RectF();

    public Enemy(float startX, float startY) {
        x = startX;
        y = startY;
        updateRect();
    }

    public void update() {
        y += speed;
        updateRect();
    }

    private void updateRect() {
        rect.set(x - width/2, y - height/2, x + width/2, y + height/2);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawRect(rect, paint);
    }

    public boolean isOffScreen(int screenHeight) {
        return y - height/2 > screenHeight;
    }

    public RectF getRect() {
        return rect;
    }
}