package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Grenade {

    private float x,y;
    private int damage;
    private Batch batch;
    private Texture image;
    private int counter = 1;
    private float mapUnitScale;
    private float width, height;
    private float gravity = -.2f;
    private float damping = 0.55f;
    private boolean exploded = false;
    private Texture explosionTexture;
    private boolean exploding = false;
    private float antiGravAccel = 0.8f;
    private float velocityX, velocityY;
    private float air_friction = 0.015f;
    private TiledMapTileLayer collisionLayer;

    Grenade(int damage, float angleDeg, int initialVelocity, float mapUnitScale, float initialX, float initialY, float width, float height, TiledMapTileLayer collisionLayer, Batch batch)
    {
        // need the map unit scale for the collision detection of the grenades
        this.mapUnitScale = mapUnitScale;
        this.damage = damage;
        // Split the initial velocity and angle into x and y components
        velocityX = (float)(initialVelocity*cos(toRadians(angleDeg)));
        velocityY = (float)(initialVelocity*sin(toRadians(angleDeg)));
        //the image for the grenade
        image = new Texture(Gdx.files.internal("temp_grenade.png"));
        explosionTexture = new Texture(Gdx.files.internal("explosion_temp.png"));
        //set the initial coordinates for the grenade
        this.x = initialX;
        this.y = initialY;
        //set the width and height of the image
        this.width = width;
        this.height = height;
        //need to get the collision layer of the map for collision detection
        this.collisionLayer = collisionLayer;
        //need to have a batch to draw the grenade itself and the explosion
        this.batch = batch;
    }
    public void updatePhysics(){
        if(!exploding) {
            float oldX = x;
            float oldY = y;

            x += velocityX;
            velocityX /= (1 + air_friction);

            y += velocityY;
            velocityY += gravity; //apply acceleration due to gravity
            velocityY /= (1 + air_friction);

            if (isAntiGrav(x + width / 2, y)) {
                velocityY += antiGravAccel;
            }

            //velocity clamping to eliminate the possibility that the body gains enough velocity to clip through tiles completely
            if (velocityY < -20f) velocityY = -20f;
            if (velocityY > 20f) velocityY = 20f;

            checkYcollision(oldX, oldY);
            if (velocityX < 0) {      //check for collision on the left side
                if (isRigid(x, y) || isRigid(x, y + height)) {
                    if (!(isRigid(oldX, y) && isRigid(oldX, y + height))) {
                        velocityX = -velocityX * damping;
                        x = oldX - (oldX % (collisionLayer.getTileWidth() * mapUnitScale)) + 1;
                    }
                }
            } else if (velocityX > 0) {     //check for collision on the right side
                if (isRigid(x + width, y) || isRigid(x + width, y + height)) {
                    if (!(isRigid(oldX + width, y) && isRigid(oldX + width, y + height))) {
                        velocityX = -velocityX * damping;
                        x = x - (x + width) % (collisionLayer.getTileWidth() * mapUnitScale) - 1;
                    }
                }
            }
            checkYcollision(oldX, oldY);

            if (x < 0) x = 0;
            counter ++;
            if (counter >= 242) {
                exploding = true;     // once counter has reached 242, 4 seconds have passed
                counter = 0;
            }
        }
        else explode();
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
    private void checkYcollision(float oldX, float oldY) {
        if (velocityY < 0) {
            if (isUnpassable(x, y-1) || isUnpassable(x + width, y-1)) {
                if (!isUnpassable(x, oldY) && !isUnpassable(x + width, oldY)) {
                    velocityY = -velocityY*damping;
                    y = oldY - oldY % (collisionLayer.getTileHeight() * mapUnitScale);
                }
            }
        } else if (velocityY > 0) {   //velocityY is positive, which means the character is traveling upward
            if (isRigid(x, y + height) || isRigid(x + width, y + height)) {
                if (isRigid(oldX, y + height) || isRigid(oldX + width, y + height)) {
                    velocityY = -velocityY*damping;
                    y = y - (y + height) % (collisionLayer.getTileHeight() * mapUnitScale) - 1;
                }
            }
        }
    }
    public void draw(Batch batch){
        if(!exploding)
            batch.draw(image, x, y, width, height);
    }
    public void dispose(){
        image.dispose();
    }
    private void explode()
    {
        if(counter <= 15)
        {
            batch.draw(explosionTexture, x-4*counter, y-4*counter, 8*counter, 8*counter);
            counter++;
        }else {
            exploded = true;
            this.dispose();
        }
    }
    public boolean isExploded()
    {
        return exploded;
    }
}
