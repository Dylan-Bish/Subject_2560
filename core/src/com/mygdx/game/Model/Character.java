package com.mygdx.game.Model;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Dylan Bish on 12/21/17.
 */

public interface Character{
    int health = 0;
    int grenades = 0;
    int bullets = 0;

    //method to update the collision detection, acceleration, velocity, etc of the character
    void updatePhysics(Level level);
    //dispose of all textures, texturatlases, or anything in the class that implements disposable
    void dispose();
    /* main motion methods for the character */
    void moveRight();
    void moveLeft();
    void jump();
    //draw the character. This should handle the difference between drawing the character facing left, right, still, jumping, etc.
    void draw(Batch batch, float timePassed);
}

