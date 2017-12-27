package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private int x;
    private int y;
    private int width;
    private int height;
    private int moveSpeed = 12;
    private int jumpSpeed = 20;
    private float mapUnitScale;
    private float velocityX = 0;
    private float velocityY = 0;
    private float accelerationX = 0.13f;
    private float gravity = -.06f;
    private float damping_factor = 0.13f;
    private boolean jumping = true;
    private boolean xCollision = false;
    private boolean yCollision = false;
    private boolean onPlatform = false;

    public Player(int x, int y, int width, int height, int health, TiledMapTileLayer collisionLayer, float mapUnitScale)
    {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.collisionLayer = collisionLayer;
        this.mapUnitScale = mapUnitScale;
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

    public void updatePhysics()
    {
        /*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
        xCollision = false;
        yCollision = false;

        if(velocityY < -2)  velocityY = -2;
        if(velocityY > 2)   velocityY = 2;

        int oldX = x;
        int oldY = y;

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
            yCollision = true;
            jumping = false;
            velocityY = 0;
        }

        if (Gdx.graphics.getWidth() - width < x || x < 0) xCollision = true;
        if (x < 0 && oldX > 0) x = 0;
        if (y < 0 && oldY > 0) y = 0;

        if (isPassable(x + width / 2, y)) {
            onPlatform = true;
        } else
            onPlatform = false;

        if (onPlatform) {
            velocityY = 0;
            jumping = false;
            yCollision = true;
        }
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

    private boolean isPassable(int x, int y) {
        int w = (int) (collisionLayer.getTileWidth() * mapUnitScale);
        int h = (int) (collisionLayer.getTileHeight() * mapUnitScale);

        if (collisionLayer.getCell(x / w, y / h) == null)
            return false;
        else {
            return (collisionLayer.getCell(x / w, y / h).getTile().getProperties().containsKey("rigid")
                    || collisionLayer.getCell(x / w, y / h).getTile().getProperties().containsKey("platform"));
        }

    }
}
