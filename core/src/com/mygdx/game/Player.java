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
    private int width;
    private int height;
    private float moveSpeed = 7.5f;
    private int jumpSpeed = 20;
    private float mapUnitScale;
    private float velocityX = 0;
    private float velocityY = 0;
    private float accelerationX = 0.13f;
    private float gravity = -.05f;
    private float damping_factor = 0.15f;
    private float antiGravAccel = 0.1f;
    private float forceDownAccel = -0.39f;
    private boolean jumping = true;
    private boolean xCollision = false;
    private TextureRegion arrow;
    private float armAngle;

    public Player(int x, int y, int width, int height, int health, float mapUnitScale)
    {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/30f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.mapUnitScale = mapUnitScale;
        arrow = new TextureRegion(new Texture(Gdx.files.internal("arrow.png")));
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
        if (velocityY > -1f)
            velocityY += forceDownAccel;
    }
    public void updatePhysics(MapHandler mh)
    {
        /*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
        float oldX = x;
        float oldY = y;

        if (velocityX != 0 && !xCollision)
        {
            x += moveSpeed * velocityX;
            velocityX /= (1+damping_factor);
        }

        if(jumping){
            y += jumpSpeed * velocityY;
            velocityY += gravity; //apply acceleration due to gravity
        }

        if (y < 0){
            jumping = false;
            velocityY = 0;
        }

        if (isAntiGrav( x + width / 2, y)) {
            jumping = true;
            velocityY += antiGravAccel;
        }

        if (isDmgTile(x,y)
                || isDmgTile(x+width, y)
                || isDmgTile(x, y + height)
                || isDmgTile(x+width, y+height)){
            takeDamage(5);
        }

        if (velocityY < -1f) velocityY = -1f;
        if (velocityY > 1f) velocityY = 1f;

        checkYcollision(oldX, oldY);
        if (velocityX < 0) {      //check for collision on the left side
            if (isRigid(x, y) || isRigid(x, y + height)) {
                if (!(isRigid(oldX, y) && isRigid(oldX, y + height))) {
                    velocityX = 0;
                    x = oldX - (oldX % (collisionLayer.getTileWidth() * mapUnitScale)) + 1;
                }
            }
        } else if (velocityX > 0) {     //check for collision on the right side
            if (isRigid(x + width, y) || isRigid(x + width, y + height)) {
                if (!(isRigid(oldX + width, y) && isRigid(oldX + width, y + height))) {
                    velocityX = 0;
                    x = x - (x + width) % (collisionLayer.getTileWidth() * mapUnitScale) - 1;
                }
            }
        }
        checkYcollision(oldX, oldY);

        if(x < 0) x = 0;

        //conditional for when the camera should follow the player
        if(x > Gdx.graphics.getWidth()*(1/4f)){
            //System.out.println("Camera condition met");
            mh.getCamera().position.x  = this.x+Gdx.graphics.getWidth()/4f;

        }
        //mh.getCamera().position.y  = this.y;
        mh.getCamera().update();
    }
    public void draw(Batch batch, float timePassed)
    {
        batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), x, y, width, height);
        drawArm(batch);
    }
    public void drawVerticalMirrored(Batch batch, float timePassed)
    {
        batch.draw(rightRollAnimation.getKeyFrame(timePassed,true), x+width, y, -width, height);
        drawArm(batch);
    }
    public void drawStill(Batch batch)
    {
        batch.draw(stillImg, x, y, width, height);
        drawArm(batch);
    }
    private void drawArm(Batch batch)
    {
        batch.draw(arrow, x,y,width/2,height/2, width, height, 2, 2, armAngle);
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
    private boolean isUnpassable(float x, float y) {
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;
        else {
            return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey("rigid")
                    || collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey("platform"));
        }
    }
    private boolean isRigid(float x, float y) {
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;
        else return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey("rigid"));
    }
    private boolean isAntiGrav(float x, float y) {
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;
        else return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey("anti-gravity"));
    }
    private boolean isDmgTile(float x, float y) {
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;
        else return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey("damage"));
    }
    private void checkYcollision(float oldX, float oldY) {
        if (velocityY < 0) {
            if (isUnpassable(x, y-1) || isUnpassable(x + width, y-1)) {
                if (!isUnpassable(x, oldY) && !isUnpassable(x + width, oldY)) {
                    velocityY = 0;
                    y = oldY - oldY % (collisionLayer.getTileHeight() * mapUnitScale);
                    jumping = false;
                }
            }
        } else if (velocityY > 0) {   //velocityY is positive, which means the character is traveling upward
            if (isRigid(x, y + height) || isRigid(x + width, y + height)) {
                if (isRigid(oldX, y + height) || isRigid(oldX + width, y + height)) {
                    velocityY = 0;
                    y = y - (y + height) % (collisionLayer.getTileHeight() * mapUnitScale) - 1;
                }
            }
        } else {
            if (!(isUnpassable(x, y - 1)))
                jumping = true;
        }
    }
    public void setCollisionLayer(TiledMapTileLayer collisionLayer){
        this.collisionLayer = collisionLayer;
    }
    public void takeDamage(int damage){
        if(health > 0) this.health -= damage;
    }
    public void updateArmAngle(double angle)
    {
        armAngle = (float) angle;
    }
}
