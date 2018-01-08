package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private TextureRegion arrow;
    private float armAngle;
    private boolean movingRight = false;
    private boolean movingLeft = false;
    private boolean xCollision = false;

    Player(int x, int y, int width, int height, int health, float mapUnitScale) {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.mapUnitScale = mapUnitScale;
        arrow = new TextureRegion(new Texture(Gdx.files.internal("arrow.png")));
    }
    private ArrayList<TextureAtlas> getAllAtlasesUsed() {
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
    public boolean getJumping() {
        if((hasProperty(x,y,"water") && hasProperty(x+width,y,"water"))
            || (hasProperty(x,y-1,"water") && hasProperty(x+width,y-1,"water"))){
            return false;
        }else {
            return jumping;
        }
    }
    public void moveRight() {
        if(velocityX < 1) velocityX += this.accelerationX;  //causes the acceleration of the player
        movingLeft = false;
        movingRight = true;
    }
    public void moveLeft() {
        if(velocityX > -1) velocityX -= this.accelerationX;       //causes the acceleration of the player
        movingLeft = true;
        movingRight = false;
    }
    public void jump() {
        jumping = true;
        velocityY = 1;
    }
    public void forceDown() {
        if (velocityY > -1f)
            velocityY += forceDownAccel;
    }
    public void updatePhysics(MapHandler mh) {
        /*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
        float oldX = x;
        float oldY = y;

        if((hasProperty(x,y,"water") && hasProperty(x+width,y,"water"))
            || (hasProperty(x,y-1,"water") && hasProperty(x+width,y-1,"water")))
        {
            if(velocityY > 0.2f) velocityY = .2f;
            if(velocityY < -0.2f) velocityY = -0.2f;
            if(velocityX > 0.2f) velocityX = 0.2f;
            if(velocityX < -0.2f) velocityX = -0.2f;
            y += (jumpSpeed*velocityY);
            velocityY += gravity/10;
            x += moveSpeed * velocityX;
            velocityX /= (1 + damping_factor*2);
        }else {
            if(!xCollision) {
                x += moveSpeed * velocityX;
                velocityX /= (1 + damping_factor);
            }

            if (jumping) {
                y += jumpSpeed * velocityY;
                velocityY += gravity; //apply acceleration due to gravity
            }

            if (y < 0) {
                jumping = false;
                velocityY = 0;
            }

            if (hasProperty(x + width / 2, y, "anti-gravity")) {
                jumping = true;
                velocityY += antiGravAccel;
            }

            if (hasProperty(x, y, "damage")
                    || hasProperty(x + width, y, "damage")
                    || hasProperty(x, y + height, "damage")
                    || hasProperty(x + width, y + height, "damage")) {
                takeDamage(5);
            }

            if (velocityY < -1f) velocityY = -1f;
            if (velocityY > 1f) velocityY = 1f;
        }

        xCollision = false;
        checkYcollision(oldX, oldY);
        if (velocityX < 0) {      //check for collision on the left side
            if (hasProperty(x, y, "rigid") || hasProperty(x, y + height, "rigid")) {
                velocityX = 0;
                x = oldX - (oldX % (collisionLayer.getTileWidth() * mapUnitScale)) + 1;
                xCollision = true;
            }
        } else if (velocityX > 0) {     //check for collision on the right side
            if (hasProperty(x + width, y, "rigid") || hasProperty(x + width, y + height, "rigid")) {
                velocityX = 0;
                x = x - (x + width) % (collisionLayer.getTileWidth() * mapUnitScale) - 1;
                xCollision = true;
            }
        }
        checkYcollision(oldX, oldY);

        if(x < 0) x = 0;

        //conditional for when the camera should follow the player
        if(x > Gdx.graphics.getWidth()*(1/4f)) mh.getCamera().position.x  = this.x+Gdx.graphics.getWidth()/4f;
        //if(y > Gdx.graphics.getHeight()/2) mh.getCamera().position.y  = this.y;
        mh.getCamera().update();
    }
    public void draw(Batch batch, float timePassed){
        if(movingRight)
            batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), x, y, width, height);
        else if(movingLeft)
            batch.draw(rightRollAnimation.getKeyFrame(timePassed,true), x+width, y, -width, height);
        else
            batch.draw(stillImg, x, y, width, height);

        //draw the arm over top of the running or still  animation
        batch.draw(arrow, x,y,width/2,height/2, width, height, 2, 2, armAngle);
    }
    public void dispose() {
        //disposes of all the atlases used in this file
        ArrayList<TextureAtlas> atlasList = getAllAtlasesUsed();
        for(TextureAtlas atlas : atlasList) {
            atlas.dispose();
            //System.out.println("Disposed of " + atlas.toString());
            //^^once we add more textureAtlases, we should use this line of code to make sure that all
            //of them are getting disposed
        }

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
                    velocityY = 0;
                    y = nearestYborder;
                    jumping = false;
                }
            }
            if (hasProperty(x, y-1, "platform") || hasProperty(x + width, y-1, "platform")) {
                if(y < nearestYborder){
                    velocityY = 0;
                    y =nearestYborder;
                    jumping = false;
                }
            }


        } else if (velocityY > 0) {   //velocityY is positive, which means the character is traveling upward
            if (hasProperty(x, y + height, "rigid") || hasProperty(x + width, y + height, "rigid")) {
                if (hasProperty(oldX, y + height, "rigid") || hasProperty(oldX + width, y + height, "rigid")) {
                    velocityY = 0;
                    y = y - (y + height) % (collisionLayer.getTileHeight() * mapUnitScale) - 1;
                }
            }
        } else {
            if (!(hasProperty(x, y - 1, "rigid") || hasProperty(x+width, y-1, "rigid"))
                && !(hasProperty(x, y - 1, "platform") || hasProperty(x+width, y-1, "platform")))
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
    public void noInput(){
        movingRight = false;
        movingLeft = false;
    }
}
