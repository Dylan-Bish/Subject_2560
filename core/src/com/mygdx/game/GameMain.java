package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Dylan Bish 12/19/2017
 **/

public class GameMain extends Game {
    private Batch batch;
    private Player mainPlayer;
    private float timePassed = 0f;
    private float mapUnitScale = 0.25f;
    private MapHandler mapHandler;

    @Override
    public void create () {
        mainPlayer = new Player(0, 600, 40, 40, 1000, mapUnitScale);
        mapHandler = new MapHandler(mapUnitScale, mainPlayer);
        mainPlayer.setCollisionLayer(mapHandler.getCollisionLayer());
        this.batch =  mapHandler.getRendererBatch();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timePassed += Gdx.graphics.getDeltaTime();

        mapHandler.getRenderer().setView(mapHandler.getCamera());
        mapHandler.getRenderer().render();
        mapHandler.getRendererBatch().begin();
        inputHandler(timePassed);
        mainPlayer.updatePhysics(mapHandler);
        mapHandler.getRendererBatch().end();
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
        if(mainPlayer.getJumping() && (Gdx.input.isKeyPressed(Input.Keys.DOWN)) || Gdx.input.isKeyPressed(Input.Keys.S)){
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
        mapHandler.dispose();
    }
}
