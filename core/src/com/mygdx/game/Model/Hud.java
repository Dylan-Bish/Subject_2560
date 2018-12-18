package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Drops.Key;

public class Hud {

    //font used to display the health number on the health bar
    private BitmapFont font;
    //the batch to hold the hud images
    private SpriteBatch batch;
    //image of the healthbar
    TextureRegion health = new TextureRegion(new Texture(Gdx.files.internal("healthGradient.png")));

    String healthAsText;
    String bulletsAsText;
    String grenadesAsText;

    /** stuff for fps */
    private Float frameRate;
    private long lastTimeCounted;
    private float sinceChange;


    public Hud(){
        //instantiate the font for the health bar
        font = new BitmapFont();
        //
        this.batch = new SpriteBatch();
    }


    private void updateFPS(){
        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();

        sinceChange += delta;
        if(sinceChange >= 1000) {
            sinceChange = 0;
            frameRate = (float)Gdx.graphics.getFramesPerSecond();
        }
    }

    public void draw(Player mainPlayer, float mapUnitScale){

        //width of the rectangle should be the full width of the screen multiplied by the percentage of the player's health left
        int rectWidth = (int) (Gdx.graphics.getWidth() * (mainPlayer.getHealth() / 1000f));
        //height of the rectangle should be 1/30th of the total height of the window
        int rectHeight = Gdx.graphics.getHeight() / 30;
        //x offset of the rectangle in order to center it on screen
        int rectX = Gdx.graphics.getWidth() / 2 - rectWidth / 2;
        //opacity of the healthbar
        float alpha = 0.6f;
        //text to be displayed on the center of the healthbar
        healthAsText = (((float)mainPlayer.getHealth() / 10f) + "%");
        bulletsAsText = (mainPlayer.bullets + " bullets");
        grenadesAsText = (mainPlayer.grenades + " grenades");
        updateFPS();


        batch.begin();
        for(Key key : mainPlayer.getKeys()){
            batch.draw(key.getImage(), 50+100*mainPlayer.getKeys().indexOf(key), 50, 2*key.getImage().getRegionWidth()*mapUnitScale, 2*key.getImage().getRegionHeight()*mapUnitScale);
        }
        batch.setColor(1, 1, 1, alpha);  //set the alpha channel
        batch.draw(health, rectX, 0, rectWidth, rectHeight); //actually draw the health bar
        //draw the text that goes over the health bar
        font.setColor(1f,1f,1f,1f);
        font.draw(batch, healthAsText, Gdx.graphics.getWidth() / 2f, 20, 50, healthAsText.length(), false);
        font.setColor(0f,1f,0.3f,1f);
        font.draw(batch, grenadesAsText, 5, Gdx.graphics.getHeight()-5, 30, grenadesAsText.length(), false);
        font.draw(batch, bulletsAsText, 5, Gdx.graphics.getHeight()-30, 30, bulletsAsText.length(), false);
        font.draw(batch, (frameRate + " fps"), 30, Gdx.graphics.getHeight()-55, 30, 60, false);
        batch.end();
    }
}
