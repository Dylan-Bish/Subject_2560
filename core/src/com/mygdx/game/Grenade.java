package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static java.lang.Math.abs;

public class Grenade {

    private float x,y;
    private int damage;
    private Batch batch;
    private Texture image;
    private int counter = 1;
    private float mapUnitScale;
    private float width, height;
    private float gravity = -.2f;
    private float damping = .75f;
    private boolean exploded = false;
    private Texture explosionTexture;
    private boolean exploding = false;
    private float antiGravAccel = 0.8f;
    private float velocityX, velocityY;
    private float air_friction = 0.015f;
    private TiledMapTileLayer collisionLayer;
    private Player mainPlayer;

    Grenade(int damage, float angleDeg, int initialVelocity, float mapUnitScale, float initialX, float initialY, float width, float height, TiledMapTileLayer collisionLayer, Batch batch, Player mainPlayer) {
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
        //need to have mainPlayer to take damage
        this.mainPlayer = mainPlayer;
    }
    public void updatePhysics(){
        //if the grenade is currently exploding, we don't want to assign any motion to it, we want it to be centered at the point where it exploded
        if(!exploding) {
            //get the previous x and y values before any motion is assigned for collision detection purposes
            float oldX = x;
            float oldY = y;

            if((hasProperty(x,y,"water") && hasProperty(x+width,y,"water"))
                    || (hasProperty(x,y-1,"water") && hasProperty(x+width,y-1,"water")))
            {
                if(velocityY > 2) velocityY = 2;
                if(velocityY < -2) velocityY = -2;
                if(velocityX > 2) velocityX = 2;
                if(velocityX < -2) velocityX = -2;
                y += (velocityY);
                velocityY += gravity/10;
                x += velocityX;
                velocityX /= (1 + air_friction*3);
            }else {
                //move x by the amount of velocity in that direction
                x += velocityX;
                velocityX /= (1 + air_friction);  //apply air friction to the velocity

                //move in y direction by the amount of velocity in that direction
                y += velocityY;
                velocityY += gravity; //apply acceleration due to gravity
                velocityY /= (1 + air_friction);  //apply air friction to the velocity

                if (hasProperty(x + width / 2, y, "anti-gravity")) {  //if the grenade is currently in an anti-gravity tile
                    velocityY += antiGravAccel;         //accelerate it by the amount given by the global variable
                }

                //velocity clamping to eliminate the possibility that the body gains enough velocity to clip through tiles completely
                if (velocityY < -20f) velocityY = -20f;
                if (velocityY > 20f) velocityY = 20f;
            }
            checkYcollision(oldX, oldY);   //check for collision in the Y direction based on
            if (velocityX < 0) {      //check for collision on the left side
                if (hasProperty(x, y, "rigid") || hasProperty(x, y + height, "rigid")) {
                    velocityX = -velocityX*damping;
                    x = oldX - (oldX % (collisionLayer.getTileWidth() * mapUnitScale)) + 1;
                }
            } else if (velocityX > 0) {     //check for collision on the right side
                if (hasProperty(x + width, y, "rigid") || hasProperty(x + width, y + height, "rigid")) {
                    velocityX = -velocityX*damping;
                    x = x - (x + width) % (collisionLayer.getTileWidth() * mapUnitScale) - 1;
                }
            }
            checkYcollision(oldX, oldY);

            if (x < 0) x = 0;
            if (counter >= 242) {
                exploding = true;     // once counter has reached 242, 4 seconds have passed
                counter = 0;
            }
        }
        counter ++;
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
    private void checkYcollision(float oldX, float oldY) {
        if (velocityY < 0) {
            //case where player is currently falling
            float nearestYborder = oldY - oldY % (collisionLayer.getTileHeight() * mapUnitScale);
            if (hasProperty(x, y-1, "rigid") || hasProperty(x + width, y-1, "rigid")){
                if (!(hasProperty(x, oldY, "rigid") || hasProperty(x + width, oldY, "rigid"))){
                    velocityY = -velocityY*damping;
                    y = nearestYborder;
                }
            }
            if (hasProperty(x, y-1, "platform") || hasProperty(x + width, y-1, "platform")) {
                if(y < nearestYborder){
                    velocityY = -velocityY*damping;
                    y =nearestYborder;
                }
            }
        } else if (velocityY > 0) {   //velocityY is positive, which means the character is traveling upward
            if (hasProperty(x, y + height, "rigid") || hasProperty(x + width, y + height, "rigid")) {
                if (hasProperty(oldX, y + height, "rigid") || hasProperty(oldX + width, y + height, "rigid")) {
                    velocityY = -velocityY*damping;
                    y = y - (y + height) % (collisionLayer.getTileHeight() * mapUnitScale) - 1;
                }
            }
        }
    }
    public void draw(Batch batch){
        if(!exploding)
            batch.draw(image, x, y, width, height);
        else
            explode();
    }
    public void dispose(){
        image.dispose();
        explosionTexture.dispose();
    }
    private void explode() {
        if(counter <= 15)
        {
            int radius = 6*counter;
            /* This is the horrendous conditional that checks to see if the explosion has hit the player */
            if(((abs(mainPlayer.getX()-x) < radius) && (abs(mainPlayer.getY()-y) < radius))
              ||(abs((mainPlayer.getX()+mainPlayer.getWidth())-x) < radius && (abs(mainPlayer.getY()-y) < radius))
              ||(abs(mainPlayer.getX()-x) < radius) && (abs((mainPlayer.getY()+mainPlayer.getHeight())-y) < radius)
              ||(abs((mainPlayer.getX()+mainPlayer.getWidth())-x) < radius && (abs((mainPlayer.getY()+mainPlayer.getHeight())-y) < radius))) {
                mainPlayer.takeDamage(damage);
            }
            batch.draw(explosionTexture, x-radius, y-radius, 2*radius, 2*radius);
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
