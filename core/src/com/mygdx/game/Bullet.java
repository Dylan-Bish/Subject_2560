package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Bullet {

    //current coordinates of the bullet
    private float x,y;

    //width and height of the bullet texture and collision box
    private float width, height;

    //current components of the velocity of the bullet
    private float velocityX, velocityY;

    //Actual angle of the bullet
    private int angle;

    //Raw damage done by bullet
    private int _damage;

    //the actual bullet image
    private Texture image;

    //tiled collision layer to know when to kill the bullet
    private TiledMapTileLayer collisionLayer;

    //the scaling factor of the map. Needed for collision detection
    private float mapUnitScale;

    //batch to draw the bullet on
    private Batch batch;

    //boolean to check whether the bullet still needs to be rendered or updated or anything
    private boolean isDead = false;

    Bullet(int damage, int velocity, int idealAngle, float x, float y, int variationAngle, float width, float height, TiledMapTileLayer collisionLayer, Batch batch, float mapUnitScale) {
        _damage = damage;

        //calculate the actual angle of this bullet based on the ideal, variation, and some randomness
        Random rand = new Random();
        angle = idealAngle + (rand.nextInt(2*variationAngle)-variationAngle);

        // Split the initial velocity and angle into x and y components
        velocityX = (float)(velocity*cos(toRadians(angle)));
        velocityY = (float)(velocity*sin(toRadians(angle)));

        //the image for the bullet
        image = new Texture(Gdx.files.internal("bullet.png"));

        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.collisionLayer = collisionLayer;
        this.batch = batch;
        this.mapUnitScale = mapUnitScale;
    }
    public void updatePhysics(){
        x += velocityX;
        y += velocityY;
        if(x < 0 || y < 0 || x > Gdx.graphics.getWidth() || y > Gdx.graphics.getHeight()) isDead = true;

    }
    public void draw()
    {
        batch.draw(new TextureRegion(image), x, y,width/2,height/2, width, height, 1, 1, angle);
    }
    private boolean hasProperty(float x, float y, String property) {
        //takes in world coordinates and converts to tile coordinates
        //for example, if the tile size is 100 and the input coordinates are (150,50),
        //tile coordinates are (1,0). This is required to access individual tile properties
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;  //if we don't check for null cells, the next line will give a null pointer exception
        else return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey(property));  //get the boolean of whether the tile has the input property
    }
    public boolean isDead(){
        return isDead;
    }
    public void dispose(){
        image.dispose();
    }

}
