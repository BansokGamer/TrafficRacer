package com.example.trafficracer;

import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Random;

public class GameLogicManager {
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<HomingEnemy> homingEnemies;
    private ArrayList<Bullet> bullets;
    private int viewWidth, viewHeight;
    private Random random = new Random();

    private long lastSpawnTime = 0;
    private long lastHomingSpawnTime = 0;
    private static final long HOMING_SPAWN_INTERVAL = 5000;

    private ScoreManager scoreManager;
    private GameStateManager gameState;

    public GameLogicManager(Player player, ScoreManager scoreManager, GameStateManager gameState) {
        this.player = player;
        this.scoreManager = scoreManager;
        this.gameState = gameState;
        this.enemies = new ArrayList<>();
        this.homingEnemies = new ArrayList<>();
        this.bullets = new ArrayList<>();
    }

    public void setViewSize(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }

    public void update() {
        if (player == null || viewWidth == 0 || viewHeight == 0) return;

        long currentTime = System.currentTimeMillis();
        spawnEnemies(currentTime);
        updateBullets();
        updateEnemies();
        updateHomingEnemies();
        checkPlayerCollisions();
    }

    private void spawnEnemies(long currentTime) {
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
    }

    private void updateBullets() {
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
                        scoreManager.addScore(25);
                        break;
                    }
                }
            }

            if (bulletHit) {
                bullets.remove(i);
            }
        }
    }

    private void updateEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            e.update();

            if (e.isOffScreen(viewHeight)) {
                enemies.remove(i);
                scoreManager.addScore(10);
            }
        }
    }

    private void updateHomingEnemies() {
        for (int i = homingEnemies.size() - 1; i >= 0; i--) {
            HomingEnemy he = homingEnemies.get(i);
            he.update();

            if (he.isOffScreen(viewHeight)) {
                homingEnemies.remove(i);
                scoreManager.addScore(20);
            }
        }
    }

    private void checkPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (RectF.intersects(enemy.getRect(), player.getRect())) {
                gameState.gameOver();
                scoreManager.saveBestScore();
                return;
            }
        }

        for (HomingEnemy homingEnemy : homingEnemies) {
            if (RectF.intersects(homingEnemy.getRect(), player.getRect())) {
                gameState.gameOver();
                scoreManager.saveBestScore();
                return;
            }
        }
    }

    public void shootBullet(float directionX, float directionY) {
        if (player != null) {
            bullets.add(new Bullet(player.x, player.y - player.height/2, directionX, directionY, 20));
        }
    }

    public void restart() {
        enemies.clear();
        homingEnemies.clear();
        bullets.clear();
        lastSpawnTime = 0;
        lastHomingSpawnTime = 0;

        if (viewWidth > 0 && viewHeight > 0) {
            player = new Player(viewWidth / 2f, viewHeight - 300);
        }
    }

    public Player getPlayer() { return player; }
    public ArrayList<Enemy> getEnemies() { return enemies; }
    public ArrayList<HomingEnemy> getHomingEnemies() { return homingEnemies; }
    public ArrayList<Bullet> getBullets() { return bullets; }
}
