package com.example.android3;

public class Pipe {
    private float x;//x坐标
    private int gapY;//y坐标
    private int gapHeight;//高度
    private static final float SPEED = 15f;//速度

    public Pipe(float x, int gapY, int gapHeight) {//构造函数
        this.x = x;//初始化x坐标
        this.gapY = gapY;//初始化y坐标
        this.gapHeight = gapHeight;//初始化高度
    }

    public void update() {//更新位置
        x -= SPEED;
    }

    public float getX() {
        return x;
    }

    public int getGapY() {
        return gapY;
    }

    public int getGapHeight() {
        return gapHeight;
    }
}