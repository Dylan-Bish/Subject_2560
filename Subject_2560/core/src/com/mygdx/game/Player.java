package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;

/**
 * Created by Dylan Bish on 12/21/17.
 */
public class Player implements Character {
    private TiledMapTileLayer collisionLayer;
    private Texture stillImg;
    private int health;
    private int grenades;
    private TextureAtlas testAtlas;
    private Animation<TextureRegion> rightRollAnimation;
    private float x;
    private float y;
    private float width;
    private float height;
    private int moveSpeed = 12;
    private int jumpSpeed = 20;
    private float velocityX = 0;
    private float velocityY = 0;
    private float accelerationX = 0.13f;
    private float gravity = -.06f;
    private float damping_factor = 0.13f;
    private boolean jumping = true;
    private boolean xCollision = false;
    private boolean yCollision = false;

    public Player(float x, float y, int width, int height, int health, TiledMapTileLayer collisionLayer)
    {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionLayer = collisionLayer;
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

    public float getWidth()
    {
        return this.width;
    }

    public float getHeight()
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
    }

    public void moveLeft()
    {
        if(velocityX > -1) velocityX -= this.accelerationX;       //causes the acceleration of the player
    }

    public void jump()
    {
        jumping = true;
        velocityY = 1;
    }

    public void forceDown()
    {
        velocityY = -2;
    }

    public void updatePhysics(TiledMapTileLayer collisionLayer)
    {
        /*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
        xCollision = false;
        yCollision = false;

        if(velocityY < -2)  velocityY = -2;
        if(velocityY > 2)   velocityY = 2;

        if(velocityX < 0)
        {
            x += moveSpeed*velocityX;
            velocityX /= (1+damping_factor);
        }
        else if(velocityX > 0)
        {
            x +=  moveSpeed*velocityX;
            velocityX /= (1+damping_factor);
        }

        if(jumping){
            y += jumpSpeed * velocityY;
            velocityY += gravity; //apply acceleration due to gravity
        }

        if (y < 0){
            y = 0;
            yCollision = true;
            jumping = false;
            velocityY = 0;
        }

        //boundary limits so that the the character can never be off-screen
        /**
        if(x < 0) x = 0;
        if(x > (Gdx.graphics.getWidth() - width)) x = Gdx.graphics.getWidth() - width;
         **/

        if(Gdx.graphics.getWidth()-width < x) x = Gdx.graphics.getWidth() - width;
        if(x < 0) x = 0;
        /**
        if(yCollision)
        {
            jumping = false;
        }
         **/


        /**
        System.out.println("velY = " + velocityY);
        System.out.println("x: " + xCollision);
        System.out.println("y: " + yCollision);
        System.out.println("---------");
         **/

    }


    public void draw(SpriteBatch batch, float timePassed)
    {
        batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), x, y, width, height);
    }

    public void drawVerticalMirrored(SpriteBatch batch, float timePassed)
    {
        batch.draw(rightRollAnimation.getKeyFrame(timePassed,true), x+width, y, -width, height);
    }

    public void dispose()
    {
        //disposes of all the atlases used in this file
        ArrayList<TextureAtlas> atlasList = getAllAtlasesUsed();
        for(TextureAtlas atlas : atlasList) {
            atlas.dispose();
            //System.out.println("Disposed of " + atlas.toString());
            //^^once we add more textureAtlases, we should use this line of code to make sure that all
            //of them are getting disposed
        }
    }

}
