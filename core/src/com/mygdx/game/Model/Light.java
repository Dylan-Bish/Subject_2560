package com.mygdx.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Dylan Bish on 12/6/2018
 */

public class Light {
    private float x,y,radius, angle;
    private float centerX, centerY;
    private int width, height;
    //3 dimensions of color are R, G, and B. Alpha is always set to 1.0f
    private Vector3 color;
    private boolean on = true;
    private boolean flicker = false;
    private Texture alphaTexture;


    public Light(Vector3 color, float centerX, float centerY, float radius){
        this.color = color;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.angle = 0;

        this.x = centerX - radius;
        this.y = centerY - radius;

        this.width = (int)(2f*radius);
        this.height = (int)(2f*radius);

        alphaTexture = new Texture(Gdx.files.internal("Lights/DimLight.png"));
    }

    public Light(Color color, float centerX, float centerY, float radius){
        this.color = new Vector3(color.r, color.g, color.b);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.angle = 0;

        this.x = centerX - radius;
        this.y = centerY - radius;

        this.width = (int)(2f*radius);
        this.height = (int)(2f*radius);

        alphaTexture = new Texture(Gdx.files.internal("Lights/BrightLight.png"));
    }

    public void toggleActive(){
        on = !on;
    }

    public void updateCoords(float centerX, float centerY){
        this.centerX = centerX;
        this.centerY = centerY;

        this.x = centerX - radius;
        this.y = centerY - radius;
    }

    public void updateRadius(float radius){
        this.radius = radius;

        this.x = centerX - radius;
        this.y = centerY - radius;

        this.width = (int)(2f*radius);
        this.height = (int)(2f*radius);
    }

    public void draw(SpriteBatch batch){
        if(on){
            batch.setColor(color.x, color.y, color.z, 1.0f);
            batch.draw(new TextureRegion(alphaTexture),
                    x,
                    y,
                    centerX,
                    centerY,
                    width,
                    height,
                    1,
                    1,
                    angle);
        }
    }

    public void kill(){
        this.alphaTexture.dispose();
    }
}
