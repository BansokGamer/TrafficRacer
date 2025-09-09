package com.example.trafficracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bullet {
    private float x, y;
    private float speedX, speedY;
    private float radius = 10;
    private boolean active = true;
    private RectF rect = new RectF();

    public Bullet(float startX, float startY, float directionX, float directionY, float speed) {
        this.x = startX;
        this.y = startY;

        float length = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        if (length > 0) {
            this.speedX = (directionX / length) * speed;
            this.speedY = (directionY / length) * speed;
        } else {
            this.speedX = 0;
            this.speedY = -speed;
        }

        updateRect();
    }

    public void update() {
        x += speedX;
        y += speedY;
        updateRect();
    }

    private void updateRect() {
        rect.set(x - radius, y - radius, x + radius, y + radius);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, radius, paint);
    }

    public boolean isOffScreen(int screenWidth, int screenHeight) {
        return x < 0 || x > screenWidth || y < 0 || y > screenHeight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public RectF getRect() {
        return rect;
    }

    public String getInfo() {
        return "Bullet at (" + x + ", " + y + ") with speed (" + speedX + ", " + speedY + ")";
    }
}