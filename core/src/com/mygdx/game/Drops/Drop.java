package com.mygdx.game.Drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Player;

public abstract class Drop {
    private float velocityY = 0;
    private final float gravity = -.2f;
    TiledMapTileLayer collisionLayer;
    TextureRegion image;
    float mapUnitScale;
    float x,y;
    float width, height;
    boolean isGrounded = false;

    public Drop(TextureRegion image, float x, float y, float mapUnitScale, TiledMapTileLayer collisionLayer) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.mapUnitScale = mapUnitScale;
        this.collisionLayer = collisionLayer;
        this.width = collisionLayer.getTileWidth()*mapUnitScale;
        this.height = collisionLayer.getTileHeight()*mapUnitScale;
    }
    public void draw(Batch batch) {
        batch.draw(image,x,y,width,height);
    }
    public void updatePhysics(){
        if(!isGrounded) {
            float oldY = y;

            velocityY += gravity;
            y += velocityY;
            if((hasProperty(x,y,"rigid") || hasProperty(x+width, y,  "rigid"))
            &&!(hasProperty(x,oldY,"rigid") || hasProperty(x,oldY,"rigid"))){
                y = oldY - (oldY % (collisionLayer.getTileHeight()*mapUnitScale));
                isGrounded = true;
            }
            if((hasProperty(x,y,"platform") || hasProperty(x+width, y,  "platform"))
                    &&!(hasProperty(x,oldY,"platform") || hasProperty(x,oldY,"platform"))){
                y = oldY - (oldY % (collisionLayer.getTileHeight()*mapUnitScale));
                isGrounded = true;
            }
            if (hasProperty(x + width / 2, y, "anti-gravity")) {
                velocityY += 1;
            }
        }
    }
    public abstract boolean giveTo(Player mainPlayer);
    private boolean hasProperty(float x, float y, String property) {
        //takes in world coordinates and converts to tile coordinates
        //for example, if the tile size is 100 and the input coordinates are (150,50),
        //tile coordinates are (1,0). This is required to access individual tile properties
        float tileX = x / (collisionLayer.getTileWidth() * mapUnitScale);
        float tileY = y / (collisionLayer.getTileHeight() * mapUnitScale);
        if (collisionLayer.getCell((int) tileX, (int) tileY) == null) return false;  //if we don't check for null cells, the next line will give a null pointer exception
        else return (collisionLayer.getCell((int) tileX, (int) tileY).getTile().getProperties().containsKey(property));  //get the boolean of whether the tile has the input property
    }
    public float getX(){
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
}
