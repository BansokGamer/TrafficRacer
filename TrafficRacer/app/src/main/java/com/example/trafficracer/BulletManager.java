package com.example.trafficracer;

import java.util.ArrayList;

public class BulletManager {
    private ArrayList<Bullet> bullets = new ArrayList<>();

    public void update(int screenWidth, int screenHeight) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.isOffScreen(screenWidth, screenHeight)) {
                bullets.remove(i);
            }
        }
    }

    public void shootBullet(float x, float y, float dirX, float dirY) {
        bullets.add(new Bullet(x, y, dirX, dirY, 20));
    }

    public ArrayList<Bullet> getBullets() { return bullets; }
}