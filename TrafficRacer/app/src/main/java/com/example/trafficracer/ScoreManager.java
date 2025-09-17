package com.example.trafficracer;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {
    private int score = 0;
    private int bestScore = 0;
    private SharedPreferences prefs;

    public ScoreManager(Context context) {
        prefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        bestScore = prefs.getInt("BestScore", 0);
    }

    public void addScore(int points) {
        score += points;
        if (score > bestScore) {
            bestScore = score;
        }
    }

    public void saveBestScore() {
        prefs.edit().putInt("BestScore", bestScore).apply();
    }

    public void resetScore() {
        score = 0;
    }

    public int getScore() { return score; }
    public int getBestScore() { return bestScore; }
}