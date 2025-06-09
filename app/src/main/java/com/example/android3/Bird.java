package com.example.android3;

public class Bird {
    private float x, y;
    private float velocity;
    private static final float GRAVITY = 0.8f;
    private static final float JUMP_FORCE = -15f;

    public Bird(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocity = 0;
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;
    }

    public void jump() {
        velocity = JUMP_FORCE;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean checkCollision(Pipe pipe) {
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