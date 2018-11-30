package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Drops.Drop;
import com.mygdx.game.Drops.Key;
import com.mygdx.game.Entities.Bullet;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.Grenade;
import java.lang.CharSequence;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.atan;

/**
 * Created by Dylan Bish 12/19/2017
 **/

public class GameMain extends Game {
    //main Batch object for rendering the map, the player, grenades, bullets, etc
    private Batch batch;
    //Spritebatch for rendering the background image
    private SpriteBatch backgroundBatch;
    //Spritebatch for rendering the hud elements
    private SpriteBatch hudBatch;
    //the main player
    private Player mainPlayer;
    //timePassed variable. Used to keep track of the differential time for animations
    private float timePassed = 0f;
    //unit scale of the map. Ex) tileSize=180px, mapUnitScale=0.25f, actual tile size = 180px*0.25 = 45px
    private float mapUnitScale = 0.25f;
    //mapHandler object to handle getting the map, creating the renderer, fetching the collision layer, etc
    private Level currentLevel;
    //angle of the arm on the player
    private float angle = 0;
    //x and y distances between the mouse pointer and the arm of the player
    private double dx, dy;
    //list of entities to be rendered
    private List<Entity> entities;
    //list of drops to be rendered
    private List<Drop> drops;
    //font used to display the health number on the health bar
    private BitmapFont font;
    //boolean for keeping track of whether or not the player should be able to throw a grenade based on mouse presses
    private boolean grenadeSpawnable = true;
    //boolean to keep track of whether or not the game is paused
    private boolean paused = false;
    //Texture to display the background image
    private Texture backgroundTexture;
    @Override
    public void create () {
        //create the mapHandler
        currentLevel = new Level(mapUnitScale, "maps/MyMap.tmx");
        //instantiate the main player
        int initialY = (int)(currentLevel.getCollisionLayer().getTileHeight()*currentLevel.getCollisionLayer().getHeight()*mapUnitScale-100);
        mainPlayer = new Player(0, initialY, 40, 40, 1000, mapUnitScale, currentLevel);
        //pass the collision layer to the player so that it can be used for collision detection
        mainPlayer.setCollisionLayer(currentLevel.getCollisionLayer());
        //set the batch for this class to the batch used to render the map
        this.batch =  currentLevel.getRendererBatch();
        //instantiate the SpriteBatch for the hud
        hudBatch = new SpriteBatch();
        //instantiate the batch for the background
        backgroundBatch = new SpriteBatch();
        //keep lists of grenades and bullets to be rendered
        entities = new ArrayList<>();
        //instantiate the font for the health bar
        font = new BitmapFont();
        //instantiate the background spriteBatch
        backgroundTexture = new Texture(Gdx.files.internal("maps/test_landscape.png"));

        drops = currentLevel.getDropList();
    }
    @Override
    public void render() {
        /*------ main rendering loop ------*/
        //set the default background color and do some other openGL nonsense
        Gdx.gl.glClearColor(0,0.15f,0.05f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        //update the global timepassed variable to update the framing for animations
        timePassed += Gdx.graphics.getDeltaTime();

        //process of drawing the background texture on the background SpriteBatch
        //backgroundBatch.begin();
        //backgroundBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //backgroundBatch.end();

        //Set the map renderer to render in the view of the camera
        currentLevel.getRenderer().setView(currentLevel.getCamera());
        batch.enableBlending();                     //enabling blending seems to make camera panning generally much smoother
        currentLevel.getRenderer().render();          //actually render the map
        batch.begin();                              //begin the main batch drawing process

        hudBatch.setColor(1,1,1,1);    //set the color of everything to be normal. Required in case the game just became unpaused
        if(!mainPlayer.isDead && !paused)           //if the game is not paused and the player is not dead, do the main input handling
            inputHandler();                         //main input handling

        if(!paused){                                //this stuff should only be done when the game is not paused
            updateAllPhysics();
        }
        drawEverything();                           //no matter what, draw everything to the screen
        batch.end();                                //stop drawing on the main batch

        drawHud();
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) paused = !paused;  //pause game input handler
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();   //end game input handler
    }
    @Override
    public void dispose () {
        //method to make garbage collection more efficient
        //any object that implements disposable should be seen here
        mainPlayer.dispose();
        backgroundBatch.dispose();
        backgroundTexture.dispose();
        currentLevel.dispose();
        for(Entity entity : entities)
            entity.dispose();
    }
    private void inputHandler() {
        //handle case where up and down are being simultaneously pressed
        if((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE))
            &&(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))){
            mainPlayer.noInput();
        }else {
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE)
                    || Gdx.input.isKeyPressed(Input.Keys.W)
                    || Gdx.input.isKeyPressed(Input.Keys.UP))
                    && !mainPlayer.getJumping())    //if space is pressed and the char is not already jumping
            {
                /* main jumping conditional  */
                mainPlayer.jump();
            }if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                /* main left movement handler */
                mainPlayer.moveLeft();
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                /* main right movement handler */
                mainPlayer.moveRight();
            } else
                mainPlayer.noInput();
        }
        if (mainPlayer.getJumping() && (Gdx.input.isKeyPressed(Input.Keys.DOWN)) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            /* main "force down" conditional  */
            mainPlayer.forceDown();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
            currentLevel.openDoor(mainPlayer);
        }
        //  dx and dy get the difference between the center of the player and the mouse. It kind of makes sense if you don't think about it too much
        dx = Gdx.input.getX() - (mainPlayer.getX() - (currentLevel.getCamera().position.x - Gdx.graphics.getWidth() / 2) + mainPlayer.getWidth() / 2);
        dy = (Gdx.graphics.getHeight() - Gdx.input.getY()) - (mainPlayer.getY() + mainPlayer.getHeight() / 2) + (currentLevel.getCamera().position.y-Gdx.graphics.getHeight()/2);

        //change the direction that the main player is facing based on whether the cursor is to the left or to the right of them
        if(dx < 0) mainPlayer.isFacingRight = false;
        else    mainPlayer.isFacingRight = true;

        // get the angle between the mouse pointer and the center of the player based on dx and dy and convert to degrees
        angle = (float) (atan(dy / dx) * (180 / 3.14159265358979323846264338328));
        //since we don't want negative angles, we need to add 180 any time the angle is in quadrants 2 or 3, which occurs whenever x is negative
        if (dx < 0) angle += 180;
        mainPlayer.updateArmAngle(angle);

        //middle mouse button press
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            //if the grenade key is not already being held down
            if(grenadeSpawnable)
                if(mainPlayer.grenades > 0) {
                    //spawn a new grenade
                    entities.add(new Grenade(23,
                            angle,
                            20,
                            mapUnitScale,
                            mainPlayer.getX() + mainPlayer.getWidth() / 2 - mainPlayer.getWidth() / 8,
                            mainPlayer.getY() + mainPlayer.getHeight() / 2 - mainPlayer.getWidth() / 8,
                            mainPlayer.getWidth() / 4,
                            mainPlayer.getHeight() / 4,
                            currentLevel.getCollisionLayer(),
                            batch,
                            mainPlayer));
                    mainPlayer.grenades -= 1;
                }
            /* We don't want the player to be able to spawn any new grenades until the key is released and pressed again
            so, set spawnable to false unless the button is not pressed  */
            grenadeSpawnable = false;
        }else grenadeSpawnable = true;
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if(mainPlayer.bullets > 0) {
                entities.add(new Bullet(0,
                        30,
                        angle,
                        mainPlayer.getX() + mainPlayer.getWidth() / 2 - mainPlayer.getWidth() / 16,
                        mainPlayer.getY() + mainPlayer.getHeight() / 2 - mainPlayer.getHeight() / 16,
                        3,
                        mainPlayer.getWidth() / 8,
                        mainPlayer.getHeight() / 16,
                        currentLevel.getCollisionLayer(),
                        mapUnitScale));
                mainPlayer.bullets -= 1;
            }
        }
    }
    private void drawEverything(){
        //draw each entity
        for(Entity entity : entities) entity.draw(batch);
        //draw the main player. This method call alone handles whether the player should be drawn backwards, forwards, or still
        if(!mainPlayer.isDead)
            mainPlayer.draw(batch, timePassed);
        //draw each drop item
        for(Drop drop : drops) drop.draw(batch);
    }
    private void updateAllPhysics(){
        for (int i = 0; i < entities.size(); i++) { //for each grenade
            entities.get(i).updatePhysics();        //update the physics of the grenade
            if (entities.get(i).isDead()) {         //if the entity should be killed (not rendered or have it's physics updated anymore)
                entities.get(i).dispose();          //dispose of it
                entities.remove(i);                 //and remove it from the list
                i++;                                //and increment the loop counter so that there's no nullpointer nonsense happening
            }
        }
        for(Drop drop : drops) drop.updatePhysics();
        mainPlayer.updatePhysics(currentLevel);       //update the physics of the main player
        currentLevel.checkForDrops(mainPlayer);
    }
    private void drawHud() {
        //width of the rectangle should be the full width of the screen multiplied by the percentage of the player's health left
        int rectWidth = (int) (Gdx.graphics.getWidth() * (mainPlayer.getHealth() / 1000f));
        //height of the rectangle should be 1/30th of the total height of the window
        int rectHeight = Gdx.graphics.getHeight() / 30;
        //x offset of the rectangle in order to center it on screen
        int rectX = Gdx.graphics.getWidth() / 2 - rectWidth / 2;
        //opacity of the healthbar
        float alpha = 0.6f;
        //image of the healthbar
        Texture health = new Texture(Gdx.files.internal("healthGradient.png"));
        //text to be displayed on the center of the healthbar
        String healthAsText = (((float)mainPlayer.getHealth() / 10f) + "%");
        String bulletsAsText = (mainPlayer.bullets + " bullets");
        String grenadesAsText = (mainPlayer.grenades + " grenades");
        hudBatch.begin();       //start the drawing process on the hudbatch
        for(Key key : mainPlayer.getKeys()){
            this.hudBatch.draw(key.getImage(), 50+100*mainPlayer.getKeys().indexOf(key), 50, 2*key.getImage().getRegionWidth()*this.mapUnitScale, 2*key.getImage().getRegionHeight()*this.mapUnitScale);
        }
        if (!paused) {          //if the game is not paused
            hudBatch.setColor(1, 1, 1, alpha);  //set the alpha channel
            hudBatch.draw(health, rectX, 0, rectWidth, rectHeight); //actually draw the health bar
            //draw the text that goes over the health bar
            font.draw(hudBatch, healthAsText, Gdx.graphics.getWidth() / 2, 20, 20, 20, false);
            font.draw(hudBatch, grenadesAsText, 5, Gdx.graphics.getHeight()-5, 30, 30, false);
            font.draw(hudBatch, bulletsAsText, 5, Gdx.graphics.getHeight()-30, 30, 30, false);
        } else {
            //if the game is paused, display a half opaque black box over the whole screen
            hudBatch.setColor(1, 1, 1, 0.5f);
            hudBatch.draw(new Texture(Gdx.files.internal("blackbox.png")), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        hudBatch.end();     //stop drawing on the hudbatch
    }
}
