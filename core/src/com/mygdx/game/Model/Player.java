package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Drops.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan Bish on 12/21/17.
 */
public class Player implements Character {
    private TiledMapTileLayer collisionLayer;
    private Texture stillImg;
    public int bullets =  100;
    private int health;
    private int maxHealth;
    public  int grenades = 5;
    private TextureAtlas testAtlas;
    private Animation<TextureRegion> rightRollAnimation;
    private float x;
    private float y;
    private int width;
    private int height;
    private float moveSpeed = 7.5f;
    private float jumpSpeed = 20;
    private float waterSpeed = 0.35f;
    private float mapUnitScale;
    private float velocityX = 0;
    private float velocityY = 0;
    private float accelerationX = 0.13f;
    private float gravity = -.032f;
    private float damping_factor = 0.15f;
    private float antiGravAccel = 0.1f;
    private float forceDownAccel = -0.07f;
    private boolean jumping = true;
    private TextureRegion arrow;
    private float armAngle;
    private boolean movingRight = false;
    private boolean movingLeft = false;
    public boolean isDead = false;
    public boolean isFacingRight = true;
    private List<Key> keys;
    private Level level;

    public Player(int x, int y, int width, int height, int maxHealth, float mapUnitScale, Level level) {
        testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
        rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
        stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        health = maxHealth;
        this.mapUnitScale = mapUnitScale;
        this.level = level;
        keys = new ArrayList<>();
        arrow = new TextureRegion(new Texture(Gdx.files.internal("guntest.png")));
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
        if((level.hasProperty(x,y,"water") && level.hasProperty(x+width,y,"water"))
            || (level.hasProperty(x,y-1,"water") && level.hasProperty(x+width,y-1,"water"))){
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
        velocityY = 0.75f;
    }
    public void forceDown() {
        if (velocityY > -1f)
            velocityY += forceDownAccel;
    }
    public void updatePhysics(Level level) {
        /*
		main conditionals to handle the very basic physics and collision detection that have so far been implemented
		 */
        float oldX = x;
        float oldY = y;

        if((level.hasProperty(x,y,"water") && level.hasProperty(x+width-1,y,"water"))
            || (level.hasProperty(x,y-1,"water") && level.hasProperty(x+width-1,y-1,"water")))
        {
            if(velocityY > waterSpeed) velocityY = waterSpeed/2;
            if(velocityY < -waterSpeed) velocityY = -waterSpeed/2;
            if(velocityX > waterSpeed) velocityX = waterSpeed;
            if(velocityX < -waterSpeed) velocityX = -waterSpeed;

            if(jumping){y += (jumpSpeed*velocityY);}
            velocityY += gravity/4;
            x += moveSpeed * velocityX;
            velocityX /= (1 + damping_factor*2);
        }else if(!(level.hasProperty(x,y,"water") && level.hasProperty(x+width,y,"water"))
              &&(level.hasProperty(oldX, oldY, "water") && level.hasProperty(oldX, oldY, "water"))) {
            jump();
            System.out.println("debuG");
        }else{
            //if(!xCollision) {
                x += moveSpeed * velocityX;
                velocityX /= (1 + damping_factor);
            //}
            if (jumping) {
                y += jumpSpeed * velocityY;
                velocityY += gravity; //apply acceleration due to gravity
            }

            if (y < 0) {
                jumping = false;
                velocityY = 0;
            }

            if (level.hasProperty(x + width / 2, y+1, "anti-gravity")) {
                jumping = true;
                velocityY += antiGravAccel;
            }

            if (level.hasProperty(x, y, "damage")
                    || level.hasProperty(x + width, y, "damage")
                    || level.hasProperty(x, y + height, "damage")
                    || level.hasProperty(x + width, y + height, "damage")) {
                takeDamage(5);
            }

            //X and Y velocity clamping
            if(isFacingRight != movingRight) {  //if player is not facing the same direction as they are moving
                if(velocityX > 0.4f) velocityX = 0.4f;
                if(velocityX < -0.4f) velocityX = -0.4f;
            }
            //we want to clamp these values so that one updatePhysics iteration can't move the player all the way through a tile
            if (velocityY < -1f) velocityY = -1f;
            if (velocityY > 1f) velocityY = 1f;
        }

        checkCollision(oldX, oldY);

        if(x < 0) x = 0;

        //conditional for when the camera should follow the player
        if(x > Gdx.graphics.getWidth()*(1/4f)) level.getCamera().position.x  = this.x+Gdx.graphics.getWidth()/4f;
        if(y > Gdx.graphics.getHeight()/2) level.getCamera().position.y  = this.y;
        level.getCamera().update();

        if(this.health <= 0)
        {
            isDead = true;
            kill();
        }
    }
    public void draw(Batch batch, float timePassed){
        if(movingRight)
            batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), x, y, width, height);
        else if(movingLeft)
            batch.draw(rightRollAnimation.getKeyFrame(timePassed,true), x+width, y, -width, height);
        else
            batch.draw(stillImg, x, y, width, height);

        //draw the arm over top of the running or still  animation
        if(isFacingRight)
            batch.draw(arrow, x,y,width/2,height/2, width, height, 2, 2, armAngle);
        else
            batch.draw(arrow, x,y,width/2,height/2, width, height, -2, 2, armAngle+180);
    }
    private void checkCollision(float oldX, float oldY) {

        /* Start collision checking in X direction */
        if(velocityX < 0){
            //the nearest tile X border when the player is traveling left
            float nearestXborder = oldX - oldX % (level.getCollisionLayer().getTileWidth()*mapUnitScale);
            /* we want to check for X collision when the player passes over the nearest X border.
             * In the case of velocityX < 0, this occurs when the new X value is less than the border, and the old X
             * value is greater than the border. Since the old X value will always be greater than the border, we only
             * have to check the former condition */
            if(x < nearestXborder){     //if we have passed the nearest x border
                if(level.hasProperty(x, oldY, "rigid", false, false)
                || level.hasProperty(x, (oldY+height), "rigid", false, false)){    //if there is collision
                    velocityX = 0;
                    x = nearestXborder;
                }
            }
        }else if (velocityX > 0){
            //the nearest tile X border when the player is traveling left
            float nearestXborder = (x+width) - (x+width) % (level.getCollisionLayer().getTileWidth()*mapUnitScale);
            /* we want to check for X collision when the player passes over the nearest X border.
             * In the case of velocityX > 0, this occurs when the [old X value plus width] is less than the border, and
             * the [new X value plus width] is greater than the border. Since the [new X value plus width] will always
             * be greater  than the border, we only have to check the former condition */
            if(x + width > nearestXborder){  //if we have passed the nearest x border
                if(level.hasProperty((x+width), oldY, "rigid", true, false)
                || level.hasProperty((x+width), (oldY+height), "rigid", true, false)){  //if there is collision
                    velocityX = 0;
                    x = nearestXborder - width;
                }
            }
        }

        /* Start checking collision in the Y direction */
        if (velocityY < 0) {
            //case where player is currently falling
            //the nearest Y border will be the y value on which the player will land if they indeed should land
            float nearestYborder = oldY - oldY % (collisionLayer.getTileHeight() * mapUnitScale);
            //if y is less than the nearest Y border, and the player is falling, this means they have passed the nearest Y border
            if(y < nearestYborder){
                //if the new y value has either the property "platform" or "rigid", the player should stop falling
                //this needs to be checked at each end of the player's collision box,
                // meaning one check for (x,y), and another check for ((x+width),y) for each property
                if(level.hasProperty(x, y,"rigid",false,true)
                || level.hasProperty(x+width,y,"rigid",true,true)
                || level.hasProperty(x, y,"platform",false,true)
                || level.hasProperty(x+width,y,"platform",true,true)){
                    jumping =  false;
                    velocityY = 0;
                    y = nearestYborder;
                }
            }
        }else if(velocityY > 0){
            //case where the player is moving upward in the y-direction
            //nearest Y border in this case is the y value that the player will stop at if they've hit an unpassable tile
            float nearestYborder = y+height - (y+height) % (collisionLayer.getTileHeight() * mapUnitScale);
            /* if old y is less than the nearest y border, and y is greater than the nearest y border, the player has passed
             * the y border, but since y will always be greater than the nearest y border in the case that velocityY > 0,
             * we only have to check if old y is less than the nearest y border.
            /* Since we want to check the top of the player's collision box, the "y" values are actually y + height */
            if(oldY+height < nearestYborder){
                //since the hasproperty method with only 3 params assumes that the latter two booleans are false,
                //and this is ideal for this case, we don't need to use the longer method call for hasProperty
                if(level.hasProperty(x, y+height, "rigid", false, false)
                || level.hasProperty(x+width, y+height, "rigid", true, false)){
                    velocityY = 0;
                    y = nearestYborder - height-1;
                }
            }
        }
        /* End collision checking in Y direction */



        if (!(level.hasProperty(x, y - 1, "rigid") || level.hasProperty(x+width, y-1, "rigid"))
         && !(level.hasProperty(x, y - 1, "platform") || level.hasProperty(x+width, y-1, "platform")))
            jumping = true;
        //since jumping is a generic boolean meant to describe simply whether or not the player
        // is in the air, set it to true whenever the player is not standing on something
    }
    public void setCollisionLayer(TiledMapTileLayer collisionLayer){
        this.collisionLayer = collisionLayer;
    }
    public void takeDamage(int damage){
        if(health > 0) this.health -= damage;
        if(health < 0) this.health = 0;
    }
    public void updateArmAngle(double angle)
    {
        armAngle = (float) angle;
    }
    public void noInput(){
        movingRight = false;
        movingLeft = false;
    }
    private void kill(){
        this.dispose();
    }
    public void addHealth(int health){
        this.health += health;
        if(this.health > maxHealth) this.health = maxHealth;
    }
    public void addBullets(int bullets){
        this.bullets += bullets;
    }
    public boolean isHealthMax(){
        return health >= maxHealth;
    }
    public List<Key> getKeys(){
        return keys;
    }
    public boolean hasKey(String color){
        for(Key key : keys){
            if(key.getColor().equals(color))
                return true;
        }
        return false;
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
        stillImg.dispose();
        testAtlas.dispose();
    }
}
