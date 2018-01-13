package com.mygdx.game.Drops;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Player;

public class HealthBox extends Drop {

    public HealthBox(TextureRegion image, float x, float y, float mapUnitScale, TiledMapTileLayer collisionLayer){
        super(image, x, y, mapUnitScale, collisionLayer);
    }

    public void pickup(Player mainPlayer) {

    }

    @Override
    boolean isPickedUp() {
        return false;
    }


}
