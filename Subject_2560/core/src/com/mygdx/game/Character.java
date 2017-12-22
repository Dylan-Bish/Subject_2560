package com.mygdx.game;

/**
 * Created by root on 12/21/17.
 */
public interface Character {
    public int health = 0;
    public int grenades = 0;
    public void setGrenades(int grenades);
    public int getGrenades();
    public void setHealth(int health);
    public void moveRight();
    public void moveLeft();
    public void jump();
}

