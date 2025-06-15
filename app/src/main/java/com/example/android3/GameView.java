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
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private boolean isPlaying;
    private boolean isGameOver;
    private int screenWidth, screenHeight;
    private int score;
    private SurfaceHolder holder;//表面
    private Canvas canvas;//画布
    private Thread gameThread;//线程
    private Paint paint;//画笔
    private Random random;
    private static final int PIPE_SPACING = 300;
    private static final int PIPE_GAP = 400;
    private static final int PIPE_WIDTH = 100;
    private RectF restartButton;//矩形类
    private static final String RESTART_TEXT = "重新开始";

    public GameView(Context context) {//构造函数
        super(context);
        holder = getHolder();//获取表面
        holder.addCallback(this);//添加回调，在屏幕变化、应用关闭时重置surface
        paint = new Paint();
        random = new Random();
        score = 0;
        isGameOver = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//在surface创建时初始化游戏
        screenWidth = getWidth();
        screenHeight = getHeight();
        initGame();
    }

    private void initGame() {//初始化游戏
        bird = new Bird(screenWidth / 4, screenHeight / 2);
        pipes = new ArrayList<>();
        score = 0;
        isGameOver = false;
        isPlaying = true;
        if (gameThread == null || !gameThread.isAlive()) {//如果线程不存在或不活跃，则创建新线程
            gameThread = new Thread(this);//创建新线程
            gameThread.start();//启动线程
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
    public void surfaceDestroyed(SurfaceHolder holder) {//在surface销毁时停止游戏
        isPlaying = false;
        try {
            gameThread.join();//等待线程结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {//游戏主循环
        while (isPlaying) {
            if (!isGameOver) {
                update();//更新游戏状态
            }
            draw();//绘制游戏画面
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();//打印异常
            }
        }
    }

    private void update() {//更新游戏状态
        bird.update();//更新鸟的位置

        // 更新pipes位置
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.update();

            // 如果pipe超出屏幕
            if (pipe.getX() + PIPE_WIDTH < bird.getX()) {
                pipes.remove(i);
                score++;
            }

            // 如果碰撞
            if (bird.checkCollision(pipe)) {
                gameOver();
            }
        }

        // 如果没有pipe，或者最后一个pipe移动够远，就创建一个pipe
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).getX() < screenWidth - PIPE_SPACING) {
            int gapY = random.nextInt(screenHeight - PIPE_GAP - 200) + 100;
            pipes.add(new Pipe(screenWidth, gapY, PIPE_GAP));
        }

        // 游戏结束条件
        if (bird.getY() <= 0 || bird.getY() >= screenHeight) {
            gameOver();
        }
    }

    private void draw() {//绘制游戏画面
        if (holder.getSurface().isValid()) {//如果表面有效
            canvas = holder.lockCanvas();//锁定画布

            // 用canvas画背景
            canvas.drawColor(Color.rgb(135, 206, 235));

            // 用paint画一个圆代表鸟
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(bird.getX(), bird.getY(), 30, paint);

            // 用paint画长方体代表pipe
            paint.setColor(Color.GREEN);
            for (Pipe pipe : pipes) {
                // Draw top pipe
                canvas.drawRect(pipe.getX(), 0, pipe.getX() + PIPE_WIDTH, pipe.getGapY(), paint);
                // Draw bottom pipe
                canvas.drawRect(pipe.getX(), pipe.getGapY() + PIPE_GAP,
                        pipe.getX() + PIPE_WIDTH, screenHeight, paint);
            }

            //Paint设置字体颜色大小，Canvas在指定位置画出指定文字
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 50, 100, paint);

            // 处理GameOver游戏循环
            if (isGameOver) {
                // Canvas画各半透明黑色
                paint.setColor(Color.argb(128, 0, 0, 0));
                canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

                // canvas画文本1
                paint.setColor(Color.WHITE);//颜色
                paint.setTextSize(100);//大小
                String gameOverText = "游戏结束";//文本
                float textWidth = paint.measureText(gameOverText);//宽度
                //在指定位置画出指定文字
                canvas.drawText(gameOverText, (screenWidth - textWidth) / 2, screenHeight / 3, paint);

                // canvas画文本2
                paint.setTextSize(60);
                String finalScoreText = "最终得分: " + score;
                textWidth = paint.measureText(finalScoreText);
                canvas.drawText(finalScoreText, (screenWidth - textWidth) / 2, screenHeight / 2 - 50, paint);

                // canvas画矩形
                paint.setColor(Color.rgb(76, 175, 80));
                canvas.drawRect(restartButton, paint);
                //canvas画文本3
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