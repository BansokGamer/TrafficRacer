package com.example.trafficracer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private GameView gameView;
    private Button buttonLeft, buttonRight;
    private JoystickView joystickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);
        joystickView = findViewById(R.id.joystickView);

        buttonLeft.setOnClickListener(this);
        buttonRight.setOnClickListener(this);

        gameView.setJoystick(joystickView);

        gameView.setFocusable(true);
        gameView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.startGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLeft) {
            gameView.movePlayerLeft();
        } else if (view == buttonRight) {
            gameView.movePlayerRight();
        }
    }
}