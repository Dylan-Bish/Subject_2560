package com.mygdx.game.Drops;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Model.Player;

public class Key extends Drop {

    private String color;

    public Key(TextureRegion image, float x, float y, float mapUnitScale, TiledMapTileLayer collisionLayer, String color) {
        super(image, x, y, mapUnitScale, collisionLayer);
        this.color = color;
    }

    @Override
    public boolean giveTo(Player mainPlayer) {
        mainPlayer.getKeys().add(this);
        return true;
    }

    public String getColor(){
        return this.color;
    }
}
