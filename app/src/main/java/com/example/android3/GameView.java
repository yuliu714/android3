package com.example.android3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private Canvas canvas;
    private Thread gameThread;
    private boolean isPlaying;
    private boolean isGameOver;
    private Paint paint;
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private int screenWidth, screenHeight;
    private int score;
    private Random random;
    private static final int PIPE_SPACING = 300;
    private static final int PIPE_GAP = 400;
    private static final int PIPE_WIDTH = 100;
    private RectF restartButton;
    private static final String RESTART_TEXT = "重新开始";

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        random = new Random();
        score = 0;
        isGameOver = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();
        initGame();
    }

    private void initGame() {
        bird = new Bird(screenWidth / 4, screenHeight / 2);
        pipes = new ArrayList<>();
        score = 0;
        isGameOver = false;
        isPlaying = true;
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        // 创建重启按钮
        float buttonWidth = 300;
        float buttonHeight = 100;
        float buttonX = (screenWidth - buttonWidth) / 2;
        float buttonY = (screenHeight - buttonHeight) / 2;
        restartButton = new RectF(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!isGameOver) {
                update();
            }
            draw();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        bird.update();

        // Update pipes
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.update();

            // Remove pipes that are off screen
            if (pipe.getX() + PIPE_WIDTH < 0) {
                pipes.remove(i);
                score++;
            }

            // Check collision
            if (bird.checkCollision(pipe)) {
                gameOver();
            }
        }

        // Add new pipes
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getX() < screenWidth - PIPE_SPACING) {
            int gapY = random.nextInt(screenHeight - PIPE_GAP - 200) + 100;
            pipes.add(new Pipe(screenWidth, gapY, PIPE_GAP));
        }

        // Check if bird hits the ground or ceiling
        if (bird.getY() <= 0 || bird.getY() >= screenHeight) {
            gameOver();
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            // Draw background
            canvas.drawColor(Color.rgb(135, 206, 235));

            // Draw bird
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(bird.getX(), bird.getY(), 30, paint);

            // Draw pipes
            paint.setColor(Color.GREEN);
            for (Pipe pipe : pipes) {
                // Draw top pipe
                canvas.drawRect(pipe.getX(), 0, pipe.getX() + PIPE_WIDTH, pipe.getGapY(), paint);
                // Draw bottom pipe
                canvas.drawRect(pipe.getX(), pipe.getGapY() + PIPE_GAP,
                        pipe.getX() + PIPE_WIDTH, screenHeight, paint);
            }

            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 50, 100, paint);

            // Draw game over screen
            if (isGameOver) {
                // Draw semi-transparent overlay
                paint.setColor(Color.argb(128, 0, 0, 0));
                canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

                // Draw game over text
                paint.setColor(Color.WHITE);
                paint.setTextSize(100);
                String gameOverText = "游戏结束";
                float textWidth = paint.measureText(gameOverText);
                canvas.drawText(gameOverText, (screenWidth - textWidth) / 2, screenHeight / 3, paint);

                // Draw final score
                paint.setTextSize(60);
                String finalScoreText = "最终得分: " + score;
                textWidth = paint.measureText(finalScoreText);
                canvas.drawText(finalScoreText, (screenWidth - textWidth) / 2, screenHeight / 2 - 50, paint);

                // Draw restart button
                paint.setColor(Color.rgb(76, 175, 80));
                canvas.drawRect(restartButton, paint);
                paint.setColor(Color.WHITE);
                paint.setTextSize(50);
                textWidth = paint.measureText(RESTART_TEXT);
                canvas.drawText(RESTART_TEXT,
                        restartButton.left + (restartButton.width() - textWidth) / 2,
                        restartButton.top + (restartButton.height() + paint.getTextSize()) / 2,
                        paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                if (restartButton.contains(x, y)) {
                    initGame();
                    return true;
                }
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bird.jump();
            }
        }
        return true;
    }

    private void gameOver() {
        isGameOver = true;
    }
}