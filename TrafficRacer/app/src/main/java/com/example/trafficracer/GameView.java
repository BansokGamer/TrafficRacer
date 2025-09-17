package com.example.trafficracer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Thread gameThread;
    private SurfaceHolder holder;

    private GameLogicManager gameLogic;
    private ScoreManager scoreManager;
    private GameStateManager gameState;
    private InputHandler inputHandler;
    private GameRenderer renderer;

    private long lastShotTime = 0;
    private static final long SHOT_COOLDOWN = 200;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        scoreManager = new ScoreManager(context);
        gameState = new GameStateManager();
        inputHandler = new InputHandler();
        renderer = new GameRenderer();

        Player player = new Player(0, 0);
        gameLogic = new GameLogicManager(player, scoreManager, gameState);

        gameState.setGameView(this);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = getWidth();
        int height = getHeight();

        gameLogic.setViewSize(width, height);
        renderer.setViewSize(width, height);

        gameLogic.restart();

        startGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameLogic.setViewSize(width, height);
        renderer.setViewSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pauseGame();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1000000000.0 / 60.0;
        double delta = 0;

        while (gameState.isPlaying()) {
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
        gameLogic.update();
        checkShooting();
    }

    private void checkShooting() {
        if (inputHandler.isShooting()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > SHOT_COOLDOWN) {
                gameLogic.shootBullet(inputHandler.getDirectionX(), inputHandler.getDirectionY());
                lastShotTime = currentTime;
            }
        }
    }

    private void render() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            renderer.render(canvas,
                    gameLogic.getPlayer(),
                    gameLogic.getEnemies(),
                    gameLogic.getHomingEnemies(),
                    gameLogic.getBullets(),
                    scoreManager.getScore(),
                    scoreManager.getBestScore(),
                    gameState.isGameOver());
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameState.isGameOver()) {
                float x = event.getX();
                float y = event.getY();

                if (renderer.getRestartButtonRect() != null &&
                        renderer.getRestartButtonRect().contains(x, y)) {
                    restartGame();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void startGame() {
        if (gameState.isPlaying()) return;

        gameState.startGame();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pauseGame() {
        gameState.pauseGame();
        try {
            if (gameThread != null) {
                gameThread.join();
                gameThread = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void movePlayerLeft() {
        if (gameLogic.getPlayer() != null && getWidth() > 0) {
            gameLogic.getPlayer().moveLeft(getWidth());
        }
    }

    public void movePlayerRight() {
        if (gameLogic.getPlayer() != null && getWidth() > 0) {
            gameLogic.getPlayer().moveRight(getWidth());
        }
    }

    public void setJoystick(JoystickView joystick) {
        if (joystick != null) {
            joystick.setJoystickListener(new JoystickView.JoystickListener() {
                @Override
                public void onJoystickActive(float xPercent, float yPercent) {
                    inputHandler.setShootingDirection(xPercent, yPercent);
                }

                @Override
                public void onJoystickInactive() {
                    inputHandler.stopShooting();
                }
            });
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    public void showRestartButton() {
        renderer.showRestartButton(true);
        render();
    }

    public void restartGame() {
        pauseGame();
        scoreManager.resetScore();
        gameState.restart();
        gameLogic.restart();
        renderer.showRestartButton(false);
        startGame();
    }

    public boolean isGameRunning() {
        return gameState.isPlaying();
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    public int getCurrentScore() {
        return scoreManager.getScore();
    }

    public int getBestScore() {
        return scoreManager.getBestScore();
    }
}