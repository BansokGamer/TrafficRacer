package com.example.trafficracer;

import static kotlin.text.Typography.bullet;

import java.util.ArrayList;
import android.graphics.RectF;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String TAG = "GameView";
    private static final long SHOT_COOLDOWN = 1000;

    private ArrayList<HomingEnemy> homingEnemies = new ArrayList<>();
    private long lastHomingSpawnTime = 0;
    private static final long HOMING_SPAWN_INTERVAL = 5000;
    private int score = 0;
    private int bestScore = 0;
    private SharedPreferences prefs;

    private Thread gameThread;
    private volatile boolean running = false;
    private boolean isGameOver = false;

    private SurfaceHolder holder;
    private Paint paint;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private long lastSpawnTime = 0;
    private Random random = new Random();

    private int viewWidth, viewHeight;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private JoystickView joystick;
    private float joystickDirectionX = 0;
    private float joystickDirectionY = 0;
    private long lastShotTime = 0;
    private float lastJoystickX = 0;
    private float lastJoystickY = -1;



    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        prefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        bestScore = prefs.getInt("BestScore", 0);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        viewWidth = getWidth();
        viewHeight = getHeight();

        if (player == null && viewWidth > 0 && viewHeight > 0) {
            player = new Player(viewWidth / 2f, viewHeight - 300);
        }

        startGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        viewWidth = width;
        viewHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pauseGame();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1000000000.0 / 60.0; // 60 updates per second
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            render();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (player == null || viewWidth == 0 || viewHeight == 0) return;

        long currentTime = System.currentTimeMillis();

        checkAutoShooting();

        if (currentTime - lastSpawnTime > 1500) {
            float laneX = (float) (100 + random.nextFloat() * (viewWidth - 200));
            enemies.add(new Enemy(laneX, -200));
            lastSpawnTime = currentTime;
        }

        if (currentTime - lastHomingSpawnTime > HOMING_SPAWN_INTERVAL && player != null) {
            float laneX = (float) (100 + random.nextFloat() * (viewWidth - 200));
            homingEnemies.add(new HomingEnemy(laneX, -200, player));
            lastHomingSpawnTime = currentTime;
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.isOffScreen(viewWidth, viewHeight)) {
                bullets.remove(i);
                continue;
            }

            boolean bulletHit = false;

            for (Enemy enemy : enemies) {
                if (RectF.intersects(bullet.getRect(), enemy.getRect())) {
                    bulletHit = true;
                    break;
                }
            }

            if (!bulletHit) {
                for (int j = homingEnemies.size() - 1; j >= 0; j--) {
                    HomingEnemy homingEnemy = homingEnemies.get(j);

                    if (RectF.intersects(bullet.getRect(), homingEnemy.getRect())) {
                        homingEnemies.remove(j);
                        bulletHit = true;
                        score += 25;
                        break;
                    }
                }
            }

            if (bulletHit) {
                bullets.remove(i);
            }
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            e.update();

            if (RectF.intersects(e.getRect(), player.getRect())) {
                isGameOver = true;
                running = false;
                if (score > bestScore) {
                    bestScore = score;
                    prefs.edit().putInt("BestScore", bestScore).apply();
                }
                return;
            }

            if (e.isOffScreen(viewHeight)) {
                enemies.remove(i);
                score += 10;
            }
        }

        for (int i = homingEnemies.size() - 1; i >= 0; i--) {
            HomingEnemy he = homingEnemies.get(i);
            he.update();

            if (RectF.intersects(he.getRect(), player.getRect())) {
                isGameOver = true;
                running = false;
                if (score > bestScore) {
                    bestScore = score;
                    prefs.edit().putInt("BestScore", bestScore).apply();
                }
                return;
            }

            if (he.isOffScreen(viewHeight)) {
                homingEnemies.remove(i);
                score += 20;
            }
        }
    }

    private void render() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
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

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, 50, 100, paint);
        canvas.drawText("Best: " + bestScore, 50, 180, paint);

        if (isGameOver) {
            paint.setTextSize(100);
            canvas.drawText("GAME OVER", viewWidth/2f - 250, viewHeight/2f, paint);

            paint.setTextSize(60);
            canvas.drawText("Tap to Restart", viewWidth/2f - 200, viewHeight/2f + 100, paint);
        }

        holder.unlockCanvasAndPost(canvas);

        long timeUntilNextShot = SHOT_COOLDOWN - (System.currentTimeMillis() - lastShotTime);
        if (timeUntilNextShot < 0) timeUntilNextShot = 0;

        canvas.drawText("Next shot: " + timeUntilNextShot + "ms", viewWidth - 300, 100, paint);

        canvas.drawText("Shooting: (" + String.format("%.1f", lastJoystickX) +
                        ", " + String.format("%.1f", lastJoystickY) + ")",
                viewWidth - 300, 140, paint);

        canvas.drawText("Bullets: " + bullets.size(), viewWidth - 300, 180, paint);
    }

    public void movePlayerLeft() {
        if (player != null && viewWidth > 0) {
            player.moveLeft(viewWidth);
        }
    }

    public void movePlayerRight() {
        if (player != null && viewWidth > 0) {
            player.moveRight(viewWidth);
        }
    }

    public void setJoystick(JoystickView joystick) {
        this.joystick = joystick;
        this.joystick.setJoystickListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickMoved(float xPercent, float yPercent) {
                if (xPercent != 0 || yPercent != 0) {
                    lastJoystickX = xPercent;
                    lastJoystickY = yPercent;
                }
            }
        });
    }

    private void checkAutoShooting() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > SHOT_COOLDOWN) {
            shootBullet();
            lastShotTime = currentTime;
        }
    }

    private void shootBullet() {
        if (player != null) {
            bullets.add(new Bullet(player.x, player.y - player.height/2,
                    lastJoystickX, lastJoystickY, 20));
        }
    }


    public void startGame() {
        if (running) return;

        if (player == null && viewWidth > 0 && viewHeight > 0) {
            player = new Player(viewWidth / 2f, viewHeight - 300);
        }

        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pauseGame() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (isGameOver) {
            restartGame();
            return true;
        }
        return false;
    }

    private void restartGame() {
        enemies.clear();
        homingEnemies.clear();
        bullets.clear();
        score = 0;
        isGameOver = false;

        if (viewWidth > 0 && viewHeight > 0) {
            player = new Player(viewWidth / 2f, viewHeight - 300);
        }

        startGame();
    }
}