package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Dylan Bish on 12/21/17.
 */


public interface Character{
    int health = 0;
    int grenades = 0;
    void setGrenades(int grenades);
    Animation<TextureRegion> getRightAnimation();

    void updatePhysics(MapHandler mh);
    void dispose();
    int getGrenades();
    void setHealth(int health);
    void moveRight();
    void moveLeft();
    void jump();
    void draw(Batch batch, float timePassed);
}

