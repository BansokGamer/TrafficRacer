package com.example.trafficracer;

import android.os.Handler;
import android.os.Looper;

public class GameStateManager {
    private boolean isPlaying = false;
    private boolean isGameOver = false;
    private GameView gameView;

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public void startGame() {
        isPlaying = true;
        isGameOver = false;
    }

    public void pauseGame() {
        isPlaying = false;
    }

    public void resumeGame() {
        isPlaying = true;
    }

    public void gameOver() {
        isPlaying = false;
        isGameOver = true;

        if (gameView != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    gameView.showRestartButton();
                }
            }, 1000);
        }
    }

    public void restart() {
        isPlaying = true;
        isGameOver = false;
    }

    public boolean isPlaying() { return isPlaying; }
    public boolean isGameOver() { return isGameOver; }
}