package com.example.android3;

public class Pipe {
    private float x;
    private int gapY;
    private int gapHeight;
    private static final float SPEED = 5f;

    public Pipe(float x, int gapY, int gapHeight) {
        this.x = x;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
    }

    public void update() {
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