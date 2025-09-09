package com.example.trafficracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class HomingEnemy {
    public float x, y;
    public float width = 150;
    public float height = 250;
    private float speed = 8;
    private Player player;
    private RectF rect = new RectF();

    public HomingEnemy(float startX, float startY, Player player) {
        x = startX;
        y = startY;
        this.player = player;
        updateRect();
    }

    public void update() {

        y += speed;

        if (player != null) {
            if (x < player.x) {
                x += speed * 0.7f;
            } else if (x > player.x) {
                x -= speed * 0.7f;
            }
        }

        updateRect();
    }

    private void updateRect() {
        rect.set(x - width/2, y - height/2, x + width/2, y + height/2);
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.MAGENTA);
        canvas.drawRect(rect, paint);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(x, y - height/2 - 20, 15, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    public boolean isOffScreen(int screenHeight) {
        return y - height/2 > screenHeight;
    }

    public RectF getRect() {
        return rect;
    }
}