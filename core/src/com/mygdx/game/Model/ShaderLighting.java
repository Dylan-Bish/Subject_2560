package com.mygdx.game.Model;
/**
 * Created by Dylan Bish on 12/07/2018
 * Used to handle all of the ridiculous nonsense related to shaders in the levels of the game.
 * WARNING: If you look at this code for too long your brain will explode, but it works, so leave it alone.
 * Most of the actual work is done by the shader files (filepath can be seen in class String declarations).
 *
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShaderLighting {
    private final String VERT_SRC = "Shaders/pass.vert";
    private final String FRAG_SRC = "Shaders/pass.frag";
    private final String LIGHT_FRAG_SRC  = "Shaders/shadowRender.frag";

    private int width, height;
    private int lightSize = 720;
    private float upscale = 1.0f;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private TiledMapRenderer tmRenderer;
    private Player mainPlayer;
    private Level currentLevel;
    int[] fgLayers = {1,2};

    private FrameBuffer occludersFBO = new FrameBuffer(Pixmap.Format.RGBA8888,  lightSize,  lightSize, false);
    private TextureRegion occludersTexReg = new TextureRegion(occludersFBO.getColorBufferTexture());
    private FrameBuffer shadowMapFBO = new FrameBuffer(Pixmap.Format.RGBA8888,  lightSize,  1, false);
    private TextureRegion shadowMapTR = new TextureRegion(shadowMapFBO.getColorBufferTexture());
    private ShaderProgram shadowMapShader;
    private ShaderProgram lightShader;

    private final boolean softShadows = true;


    public ShaderLighting(SpriteBatch batch,
                          Player mainPlayer,
                          Level currentLevel){

        this.batch = batch;
        this.mainPlayer =  mainPlayer;
        this.currentLevel = currentLevel;
        this.camera = currentLevel.getCamera();
        this.tmRenderer = currentLevel.getRenderer();

        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();
        shadowMapShader = createShader(VERT_SRC, FRAG_SRC);
        lightShader = createShader(VERT_SRC, LIGHT_FRAG_SRC);
    }

    public void renderOccluders(){
        occludersFBO.begin();
        Gdx.gl.glClearColor(0f,0f,0f,0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //batch.begin();
        camera.setToOrtho(true,  occludersFBO.getWidth(), occludersFBO.getHeight());
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(camera.combined);

        tmRenderer.setView(camera);
        tmRenderer.render(fgLayers);
        //batch.end();
        occludersFBO.end();

        camera.setToOrtho(false);
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(camera.combined);


        //debug render code for the occluder texture
        /**
        camera.setToOrtho(false);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(occludersTexReg.getTexture(), 150, 150);
        batch.end();
        */
    }

    public void renderShadowMap(){
        shadowMapFBO.begin();

        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shadowMapShader);

        batch.begin();

        shadowMapShader.setUniformf("resolution", lightSize, lightSize);
        shadowMapShader.setUniformf("upScale", upscale);

        camera.setToOrtho(false, shadowMapFBO.getWidth(), shadowMapFBO.getHeight());
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.draw(occludersTexReg.getTexture(), 0, 0, lightSize, shadowMapFBO.getHeight());

        batch.setShader(null);
        batch.end();

        shadowMapFBO.end();

/*
        //debug render the shadow map texture
        camera.setToOrtho(false);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(shadowMapTR.getTexture(), 100, 100, lightSize, 50);
        batch.end();
*/

        //reset dat camera
        camera.setToOrtho(false);
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(camera.combined);
    }

    public void renderLight(){

        camera.setToOrtho(true);
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(camera.combined);

        batch.setShader(lightShader);

        batch.begin();

        lightShader.setUniformf("resolution", lightSize, lightSize);
        lightShader.setUniformf("softShadows", 1.0f);

        batch.draw(shadowMapTR.getTexture(), mainPlayer.getCenterX()-lightSize/2f, mainPlayer.getCenterY()-lightSize/2f, lightSize, lightSize);
        batch.setShader(null);
        batch.end();

        camera.setToOrtho(false);
        currentLevel.updateCamera(mainPlayer);
        batch.setProjectionMatrix(camera.combined);
    }

    public static ShaderProgram createShader(String vert, String frag){
        ShaderProgram prog = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
        if(!prog.isCompiled()) throw new GdxRuntimeException("could not compile shader: " + prog.getLog());
        if(prog.getLog().length() != 0) Gdx.app.log("GpuShadows", prog.getLog());
        return prog;
    }
}
