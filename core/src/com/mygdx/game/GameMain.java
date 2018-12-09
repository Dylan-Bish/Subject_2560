package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Drops.Drop;
import com.mygdx.game.Entities.Bullet;
import com.mygdx.game.Entities.Entity;
import com.mygdx.game.Entities.Grenade;
import com.mygdx.game.Model.Hud;
import com.mygdx.game.Model.Level;
import com.mygdx.game.Model.Player;
import com.mygdx.game.Model.ShaderLighting;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.atan;

/**
 * Created by Dylan Bish 12/19/2017
 **/

public class GameMain extends Game {
    //main Batch object for rendering the map, the player, grenades, bullets, etc
    private SpriteBatch batch;
    //Spritebatch for rendering the background image
    private SpriteBatch backgroundBatch;

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
    //boolean for keeping track of whether or not the player should be able to throw a grenade based on mouse presses
    private boolean grenadeSpawnable = true;
    //boolean to keep track of whether or not the game is paused
    private boolean paused = false;
    //Texture to display the background image
    private Texture backgroundTexture;
    //The hud object to be drawn over the actual game
    private Hud hud;
    //custom shader used for level lighting effects
    private ShaderProgram shader;

    private FrameBuffer frameBuffer;
    private TextureRegion bufferTexture;
    private ShaderLighting shaderLighting;

    @Override
    public void create () {
        //set the batch for this class to the batch used to render the map
        this.batch =  new SpriteBatch();
        //create the mapHandler
        currentLevel = new Level(mapUnitScale, "maps/MyMap.tmx");
        //instantiate the main player
        int initialY = (int)(currentLevel.getCollisionLayer().getTileHeight()*currentLevel.getCollisionLayer().getHeight()*mapUnitScale-100);
        mainPlayer = new Player(0, initialY, 40, 40, 1000, mapUnitScale, currentLevel);
        //pass the collision layer to the player so that it can be used for collision detection
        mainPlayer.setCollisionLayer(currentLevel.getCollisionLayer());

        //instantiate the batch for the background
        backgroundBatch = new SpriteBatch();
        //keep lists of grenades and bullets to be rendered
        entities = new ArrayList<>();
        //instantiate the background spriteBatch
        backgroundTexture = new Texture(Gdx.files.internal("maps/test_landscape.png"));
        this.hud = new Hud();
        drops = currentLevel.getDropList();

        shader = new ShaderProgram(
                Gdx.files.internal("Shaders/VertexShader.glsl"),
                Gdx.files.internal("Shaders/FragmentShader.glsl"));
        ShaderProgram.pedantic = false;

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());

        shaderLighting = new ShaderLighting(batch, mainPlayer, currentLevel);
    }

    @Override
    public void render() {
        /*------ main rendering loop ------*/
        //set the default background color and do some other openGL nonsense
        Gdx.gl.glClearColor(0f,0f,0f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        //update the global timepassed variable to update the framing for animations
        timePassed += Gdx.graphics.getDeltaTime();


        batch.enableBlending();                     //enabling blending seems to make camera panning generally much smoother

        currentLevel.updateCamera(mainPlayer);

        //Set the map renderer to render in the view of the camera
        currentLevel.getRenderer().setView(currentLevel.getCamera());
        currentLevel.getRenderer().render();          //actually render the map

        /**
        shader.begin();
        shader.setUniformMatrix("u_projTrans", currentLevel.getCamera().combined);
        shader.setUniformf("u_lightPos", new Vector2(mainPlayer.getX()+mainPlayer.getWidth()/2,
                                                                mainPlayer.getY() + mainPlayer.getHeight()/2));
        currentLevel.getRenderer().getBatch().setShader(shader);
        currentLevel.getRenderer().render();          //actually render the map
        currentLevel.getRenderer().getBatch().setShader(null);
        shader.end();
         */

        /**
        renderFBO();
        batch.setProjectionMatrix(currentLevel.getCamera().combined);

        batch.begin();
        batch.draw(bufferTexture.getTexture(), 50,50, 320, 240);
        batch.end();

        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(currentLevel.getCamera().combined);
        */

        shaderLighting.renderOccluders();
        shaderLighting.renderShadowMap();
        shaderLighting.renderLight();

        batch.begin();                              //begin the main batch drawing process
        //batch.setShader(shader);
        if(!mainPlayer.isDead && !paused)           //if the game is not paused and the player is not dead, do the main input handling
            inputHandler();                         //main input handling

        if(!paused){                                //this stuff should only be done when the game is not paused
            updateAllPhysics();
        }
        drawEverything();                           //no matter what, draw everything to the screen
        batch.end();                                //stop drawing on the main batch
        hud.draw(mainPlayer, mapUnitScale);
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) paused = !paused;  //pause game input handler
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();   //end game input handler
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
                        2,
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
        for(Entity entity : entities) entity.draw(this.batch);
        //draw the main player. This method call alone handles whether the player should be drawn backwards, forwards, or still
        if(!mainPlayer.isDead)
            mainPlayer.draw(batch, timePassed);
        //draw each drop item
        for(Drop drop : drops) drop.draw(batch);
    }
    private void updateAllPhysics(){
        //****do not change to forEach loop, it breaks shit****//
        for (int i = 0; i < entities.size(); i++) { //for each grenade
            entities.get(i).updatePhysics();        //update the physics of the grenade
            if (entities.get(i).isDead()) {         //if the entity should be killed (not rendered or have it's physics updated anymore)
                entities.get(i).kill();          //dispose of it
                entities.remove(i);                 //and remove it from the list
                i++;                                //and increment the loop counter so that there's no nullpointer nonsense happening
            }
        }
        for(Drop drop : drops) drop.updatePhysics();
        mainPlayer.updatePhysics(currentLevel);       //update the physics of the main player
        currentLevel.checkForDrops(mainPlayer);
    }
    private void renderFBO(){
        frameBuffer.begin();

        Gdx.gl.glClearColor(0f,0f,0f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        currentLevel.getCamera().setToOrtho(true);
        currentLevel.updateCamera(mainPlayer);
        currentLevel.getRenderer().setView(currentLevel.getCamera());
        currentLevel.getRenderer().render();

        frameBuffer.end();
        currentLevel.getCamera().setToOrtho(false);

    }
    @Override
    public void dispose () {
        //method to make garbage collection more efficient
        //any object that implements disposable should be seen here
        batch.dispose();
        mainPlayer.dispose();
        backgroundBatch.dispose();
        backgroundTexture.dispose();
        currentLevel.dispose();
        for(Entity entity : entities)
            entity.kill();
    }
}
