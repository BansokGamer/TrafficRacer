package com.example.trafficracer;

public class EnemySpawner {
    private long lastSpawnTime = 0;
    private long lastHomingSpawnTime = 0;

    public Enemy maybeSpawnEnemy(int screenWidth) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > 1500) {
            lastSpawnTime = currentTime;
            float laneX = (float) (100 + Math.random() * (screenWidth - 200));
            return new Enemy(laneX, -200);
        }
        return null;
    }

    public HomingEnemy maybeSpawnHomingEnemy(int screenWidth, Player player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHomingSpawnTime > 5000 && player != null) {
            lastHomingSpawnTime = currentTime;
            float laneX = (float) (100 + Math.random() * (screenWidth - 200));
            return new HomingEnemy(laneX, -200, player);
        }
        return null;
    }
}