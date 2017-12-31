package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Dylan Bish 12/19/2017
 **/

public class GameMain extends Game {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player mainPlayer;
    private float timePassed = 0f;
    private float mapUnitScale = 0.2f;
    private MapHandler sh;
    private Vector2 point = new Vector2();

    @Override
    public void create () {
        batch = new SpriteBatch();        //main spritebatch that's used to draw all of the elements (so far) to the screen
        sh = new MapHandler(mapUnitScale);
        mainPlayer = new Player(0, 600, 30, 50, 1000, sh.getCollisionLayer(), mapUnitScale);
        camera = sh.getCamera();
    }

    @Override
    public void render() {
        //sets background to white
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        //displays background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //batch.setProjectionMatrix(camera.combined);
        timePassed += Gdx.graphics.getDeltaTime();	//increment timepassed
        sh.show();
        sh.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sh.render(timePassed, mainPlayer);

        batch.begin();								//start rendering the spritebatch
        inputHandler(timePassed);					//use incremented timepassed to get input
        mainPlayer.updatePhysics(sh);
        batch.end();								//stop rendering the spritebatch and restart
        sh.dispose();
    }

    private void inputHandler(float timePassed) {
        if((Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.UP))
                && !mainPlayer.getJumping())	//if space is pressed and the char is not already jumping
        {
            /* main jumping conditional  */
            mainPlayer.jump();
        }
        if(mainPlayer.getJumping() && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            /* main "force down" conditional  */
            mainPlayer.forceDown();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            /* main left movement handler */
            mainPlayer.moveLeft();
            mainPlayer.drawVerticalMirrored(batch, timePassed);
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            /* main right movement handler */
            mainPlayer.moveRight();
            mainPlayer.draw(batch, timePassed);
        } else {
            /* main no-input handler */
            batch.draw(mainPlayer.getStill(), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getWidth(), mainPlayer.getHeight());
        }

    }

    @Override
    public void dispose () {
        //method to make garbage collection more efficient
        mainPlayer.dispose();
    }
}
