package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.lang.CharSequence;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;
import static java.lang.Math.atan;

/**
 * Created by Dylan Bish 12/19/2017
 **/

public class GameMain extends Game {
    private Batch batch;
    private SpriteBatch hudBatch;
    private Player mainPlayer;
    private float timePassed = 0f;
    private float mapUnitScale = 0.25f;
    private MapHandler mapHandler;
    private ShapeRenderer healthRenderer;
    private float angle = 0;
    private double dx, dy;
    private List<Grenade> grenades;
    private BitmapFont font;
    private CharSequence healthAsText;


    @Override
    public void create () {
        mainPlayer = new Player(0, 600, 40, 40, 1000, mapUnitScale);
        mapHandler = new MapHandler(mapUnitScale, mainPlayer);
        mainPlayer.setCollisionLayer(mapHandler.getCollisionLayer());
        this.batch =  mapHandler.getRendererBatch();
        hudBatch = new SpriteBatch();
        healthRenderer = new ShapeRenderer();
        grenades = new ArrayList<>();
        font = new BitmapFont();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

        timePassed += Gdx.graphics.getDeltaTime();

        mapHandler.getRenderer().setView(mapHandler.getCamera());
        batch.enableBlending();  //enabling blending seems to make camera panning generally much smoother
        mapHandler.getRenderer().render();
        batch.begin();
        inputHandler(timePassed);
        for(Grenade grenade : grenades) {
            grenade.updatePhysics();
            grenade.draw(mapHandler.getRendererBatch());
            if(grenade.isExploded()) {
                grenades.remove(grenade);
                break;
            }
        }
        mainPlayer.updatePhysics(mapHandler);
        batch.end();
        drawHud();
    }

    private void inputHandler(float timePassed) {
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.UP))
                && !mainPlayer.getJumping())    //if space is pressed and the char is not already jumping
        {
            /* main jumping conditional  */
            mainPlayer.jump();
        }
        if (mainPlayer.getJumping() && (Gdx.input.isKeyPressed(Input.Keys.DOWN)) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            /* main "force down" conditional  */
            mainPlayer.forceDown();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            /* main left movement handler */
            mainPlayer.moveLeft();
            mainPlayer.drawVerticalMirrored(batch, timePassed);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            /* main right movement handler */
            mainPlayer.moveRight();
            mainPlayer.draw(batch, timePassed);
        } else {
            /* main no-input handler */
            mainPlayer.drawStill(batch);
        }

        /**  it works, don't fuck with it **/
        dx = Gdx.input.getX() - (mainPlayer.getX() - (mapHandler.getCamera().position.x - Gdx.graphics.getWidth() / 2) + mainPlayer.getWidth() / 2);
        dy = (Gdx.graphics.getHeight() - Gdx.input.getY()) - (mainPlayer.getY() + mainPlayer.getHeight() / 2);
        angle = (float) (atan(dy / dx) * (180 / 3.14159265358979323846264338328));
        if (dx < 0) angle += 180;
        mainPlayer.updateArmAngle(angle);

        if (Gdx.input.justTouched()) {
            grenades.add(new Grenade(0,
                    angle,
                    20,
                    mapUnitScale,
                    mainPlayer.getX() + mainPlayer.getWidth()/2 - mainPlayer.getWidth()/8,
                    mainPlayer.getY() + mainPlayer.getHeight()/2 - mainPlayer.getWidth()/8,
                    mainPlayer.getWidth() / 4,
                    mainPlayer.getHeight() / 4,
                    mapHandler.getCollisionLayer(),
                    batch));
        }
    }

    private void drawHud()
    {
        int rectWidth = (int)(Gdx.graphics.getWidth()*(mainPlayer.getHealth()/1000f));
        int rectHeight = Gdx.graphics.getHeight()/30;
        int rectX = Gdx.graphics.getWidth()/2 - rectWidth/2;
        float alpha = 0.2f;
        //healthRenderer.setColor(1,0,0,0);
        healthRenderer.begin(Filled);
        healthRenderer.rect(rectX, 0, rectWidth, rectHeight,
                new Color(0,0,0,alpha),
                new Color(0,0,0,alpha),
                new Color(1,0,0,alpha),
                new Color(1,0,0,alpha));
        healthRenderer.end();

        healthAsText = (mainPlayer.getHealth()/10 + "%");
        hudBatch.begin();
        font.draw(hudBatch, healthAsText, Gdx.graphics.getWidth()/2, 20, 20, 20, false);
        hudBatch.end();
    }

    @Override
    public void dispose () {
        //method to make garbage collection more efficient
        mainPlayer.dispose();
        mapHandler.dispose();
        healthRenderer.dispose();
        for(Grenade grenade : grenades)
            grenade.dispose();
    }
}
