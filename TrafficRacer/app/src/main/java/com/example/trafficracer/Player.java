package com.example.trafficracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player {
    public float x, y;
    public float width = 150;
    public float height = 250;
    private float laneStep = 100;

    public Player(float startX, float startY) {
        x = startX;
        y = startY;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLUE);
        canvas.drawRect(x - width/2, y - height/2, x + width/2, y + height/2, paint);
    }

    public void moveLeft(int screenWidth) {
        x -= laneStep;
        if (x - width/2 < 0) x = width/2;
    }

    public void moveRight(int screenWidth) {
        x += laneStep;
        if (x + width/2 > screenWidth) x = screenWidth - width/2;
    }

    public RectF getRect() {
        return new RectF(x - width/2, y - height/2, x + width/2, y + height/2);
    }
}