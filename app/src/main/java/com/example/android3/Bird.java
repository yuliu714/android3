package com.example.android3;

import android.graphics.Point;

import java.util.ArrayList;

public class Bird {
    private float x, y;//属性
    private float velocity ;//速度，只有垂直的速度
    //private static final float GRAVITY = 1.5f;//重力
    private static final float JUMP_FORCE = -15f;//跳跃力



    public Bird(float x, float y) {//构造函数
        this.x = x;//初始化x坐标
        this.y = y;//初始化y坐标
        this.velocity = 0;//初始化速度
    }

    public void update() {//更新坐标
        //velocity += GRAVITY;//重力
        y += velocity;//更新y坐标

    }

    public void jump() {//速度=跳跃力
        velocity = JUMP_FORCE;
    }
    public void fall() {//速度=跳跃力
        velocity = -JUMP_FORCE;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean checkCollision(Pipe pipe) {//和Pipe做碰撞检测
        float birdLeft = x - 30;
        float birdRight = x + 30;
        float birdTop = y - 30;
        float birdBottom = y + 30;

        float pipeLeft = pipe.getX();
        float pipeRight = pipe.getX() + 100;
        float pipeGapTop = pipe.getGapY();
        float pipeGapBottom = pipe.getGapY() + 400;

        return birdRight > pipeLeft && birdLeft < pipeRight &&
               (birdTop < pipeGapTop || birdBottom > pipeGapBottom);
    }
}