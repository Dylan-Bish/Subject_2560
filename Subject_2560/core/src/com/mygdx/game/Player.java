package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

/**
 * Created by Dylan Bish on 12/21/17.
 */
public class Player implements Character {
    private Texture stillImg;
    private int health;
    private int grenades;
    private TextureAtlas testAtlas;
    private Animation<TextureRegion> rightRollAnimation;
    private float x;
    private float y;
    private int width;
    private int height;
    private int moveSpeed = 6;
    private int jumpSpeed = 25;
    private float velocityX = 0;
    private float velocityY = 0;
    private float accelerationX = 0.1f;
    private float damping_factor = 0.1f;
    private boolean jumping = false;

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

    private ArrayList<TextureAtlas> getAllAtlasesUsed()
    {
        ArrayList<TextureAtlas> atlases = new ArrayList<>();
        atlases.add(testAtlas);
        /*
        Add any TextureAtlas files used in the Player file here.
        This Arraylist gets passed to the dispose() function to dispose
        of each of the textureAtlases used in the file
         */
        return atlases;
    }

    public Animation<TextureRegion> getRightAnimation()
    {
        return rightRollAnimation;
    }

    public Texture getStill()
    {
        return this.stillImg;
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

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public boolean getJumping()
    {
        return jumping;
    }

    public void moveRight()
    {
        if(velocityX < 1) velocityX += this.accelerationX;  //causes the acceleration of the player
        this.x += moveSpeed*velocityX;                       //main player displacement code

    }

    public void moveLeft()
    {
        if(velocityX > -1) velocityX -= this.accelerationX;       //causes the acceleration of the player
        this.x += moveSpeed*velocityX;              //main player displacement code
    }

    public void jump()
    {
        this.jumping = true;
        this.velocityY = 1;
    }

    public void forceDown()
    {
        velocityY = -0.8f;
        updatePhysics();
    }

    public void updatePhysics()
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

        //boundary limits so that the the character can never be off-screen
        if(this.x < 0) this.x = 0;
        if(this.x > 1180) this.x = 1180;
    }

    public void dispose()
    {
        //disposes of all the atlases used in this file
        ArrayList<TextureAtlas> atlasList = this.getAllAtlasesUsed();
        for(TextureAtlas atlas : atlasList) {
            atlas.dispose();
            //System.out.println("Disposed of " + atlas.toString());
            //^^once we add more textureAtlases, we should use this line of code to make sure that all
            //of them are getting disposed
        }
    }

}
