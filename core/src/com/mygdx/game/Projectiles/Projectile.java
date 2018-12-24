package com.mygdx.game.Projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Projectile {
    float getCenterX();
    float getCenterY();
    void kill();
    void draw(Batch batch);
    boolean isDead();
    void updatePhysics();
}
