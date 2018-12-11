package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Entity {
    float getCenterX();
    float getCenterY();
    void kill();
    void draw(Batch batch);
    boolean isDead();
    void updatePhysics();
}
