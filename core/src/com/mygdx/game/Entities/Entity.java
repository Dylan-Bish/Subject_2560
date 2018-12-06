package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Entity {
    void kill();
    void draw(Batch batch);
    boolean isDead();
    void updatePhysics();
}
