package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * Created by root on 12/21/17.
 */
public class Player implements Character {
    private int health;
    private int grenades;
    private TextureAtlas testAtlas;
    private Animation<TextureRegion> rightRollAnimation;
    private float timePassed = 0f;
    private float x;
    private float y;
    private int width;
    private int height;
    private int moveSpeed = 6;
    private int jumpSpeed = 20;
    private float velocityX = 0;
    private float velocityY = 0;
    private float damping_factor;
    private boolean jumping = false;
    private Texture stillImg;

    public Player(float x, float y, int width, int height, int health)
    {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setGrenades(int grenades)
    {
        this.grenades = grenades;
    }

    public int getGrenades()
    {
        return this.grenades;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }

    public int getHealth()
    {
        return this.health;
    }

    public void moveRight()
    {

    }

    public void moveLeft()
    {

    }

    public void jump()
    {

    }

    private void updatePhysics()
    {
        /*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
        if(velocityX < 0)
        {
            this.x += moveSpeed*velocityX;
            velocityX /= (1+damping_factor);
        }
        else if(velocityX > 0)
        {
            this.x +=  moveSpeed*velocityX;
            velocityX /= (1+damping_factor);
        }
        if(jumping) {
            if (this.y < 0) {
                this.y = 0;
                jumping = false;
                velocityY = 0;
            } else {
                this.y += jumpSpeed * velocityY;
                velocityY -= 0.05f;
            }
        }
    }

    public void forceDown()
    {
        velocityY = -1.5f;
    }
}
