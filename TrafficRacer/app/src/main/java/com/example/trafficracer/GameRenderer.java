package com.example.trafficracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.ArrayList;

public class GameRenderer {
    private Paint paint = new Paint();
    private int viewWidth, viewHeight;
    private RectF restartButtonRect;
    private boolean showRestartButton = false;

    public void setViewSize(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
        this.restartButtonRect = new RectF(
                width/2f - 150,
                height/2f + 50,
                width/2f + 150,
                height/2f + 150
        );
    }

    public void showRestartButton(boolean show) {
        this.showRestartButton = show;
    }

    public RectF getRestartButtonRect() {
        return restartButtonRect;
    }

    public void render(Canvas canvas, Player player,
                       ArrayList<Enemy> enemies, ArrayList<HomingEnemy> homingEnemies,
                       ArrayList<Bullet> bullets, int score, int bestScore,
                       boolean isGameOver) {

        if (canvas == null) return;

        canvas.drawColor(Color.BLACK);

        if (player != null) {
            player.draw(canvas, paint);
        }

        for (Enemy e : enemies) {
            e.draw(canvas, paint);
        }

        for (HomingEnemy he : homingEnemies) {
            he.draw(canvas, paint);
        }

        for (Bullet bullet : bullets) {
            bullet.draw(canvas, paint);
        }

        drawUI(canvas, score, bestScore, isGameOver);
    }

    private void drawUI(Canvas canvas, int score, int bestScore, boolean isGameOver) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, 50, 100, paint);
        canvas.drawText("Best: " + bestScore, 50, 180, paint);

        if (isGameOver && showRestartButton) {
            paint.setColor(Color.argb(150, 0, 0, 0));
            canvas.drawRect(0, 0, viewWidth, viewHeight, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("GAME OVER", viewWidth/2f, viewHeight/2f - 50, paint);

            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(restartButtonRect, 20, 20, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(40);
            canvas.drawText("RESTART", viewWidth/2f, viewHeight/2f + 120, paint);

            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}