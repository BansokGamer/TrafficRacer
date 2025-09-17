package com.example.trafficracer;

public class InputHandler {
    private boolean isShooting = false;
    private float currentDirectionX = 0;
    private float currentDirectionY = -1;

    public void setShootingDirection(float x, float y) {
        this.currentDirectionX = x;
        this.currentDirectionY = y;
        this.isShooting = true;
    }

    public void stopShooting() {
        this.isShooting = false;
    }

    public boolean isShooting() { return isShooting; }
    public float getDirectionX() { return currentDirectionX; }
    public float getDirectionY() { return currentDirectionY; }
}