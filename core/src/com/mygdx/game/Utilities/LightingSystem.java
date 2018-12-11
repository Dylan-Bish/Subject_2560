package com.mygdx.game.Utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Model.Level;
import com.mygdx.game.Model.Light;
import com.mygdx.game.Model.Player;

import java.util.ArrayList;

/**
 * Created by Dylan Bish on 12/10/2018
 */

public class LightingSystem {
    private ArrayList<Light> levelLights;
    private ArrayList<Light> dynamicLights;
    private Level currentLevel;

    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;

    /* ints used to hold the old blend functions of the batch so that
     * we can reset them after we're done blending the lights onto the level
     */
    private int oldSF, oldDF;

    public LightingSystem(Level currentLevel){
        this.currentLevel = currentLevel;
        this.levelLights = currentLevel.getLights();
        this.dynamicLights = new ArrayList<>();

        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
    }


    public void addLight(Light light){
        dynamicLights.add(light);
    }

    public void renderLights(SpriteBatch batch, Player mainPlayer){

        lightBuffer.begin();
            Gdx.gl.glClearColor(0f,0f,0f,0f);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

            currentLevel.getCamera().setToOrtho(true);
            currentLevel.updateCamera(mainPlayer);
            currentLevel.getRenderer().setView(currentLevel.getCamera());
            batch.begin();
            for(Light light : levelLights){
                light.draw(batch);
            }
            for (Light light : dynamicLights){
                light.draw(batch);
            }

            batch.end();
            batch.setColor(1f,1f, 1f, 1f);
        lightBuffer.end();
        currentLevel.getCamera().setToOrtho(false);


        batch.setProjectionMatrix(currentLevel.getCamera().combined);
        oldSF = batch.getBlendSrcFunc();
        oldDF = batch.getBlendDstFunc();
        batch.begin();
            batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
            batch.draw(lightBufferRegion.getTexture(),0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(currentLevel.getCamera().combined);
        batch.setBlendFunction(oldSF, oldDF);

        dynamicLights.clear();
    }
}
