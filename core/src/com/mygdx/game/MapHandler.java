package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Drops.AmmoBox;
import com.mygdx.game.Drops.Drop;
import com.mygdx.game.Drops.HealthBox;

import java.util.ArrayList;
import java.util.List;

public class MapHandler{
    private float mapUnitScale;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private List<Drop> drops;

    MapHandler(float unitScale) {
        mapUnitScale = unitScale;
        map = new TmxMapLoader().load("maps/MyMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
        drops = new ArrayList<>();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        /* take all them Drops and spawn em */
        TiledMapTileLayer dropLayer = (TiledMapTileLayer) map.getLayers().get("drops");
        dropLayer.setVisible(false);
        for(int i = 0; i  < dropLayer.getWidth(); i++){
            for(int j = 0; j < dropLayer.getHeight(); j++){
                if(dropLayer.getCell(i,j) != null) {
                    if (dropLayer.getCell(i, j).getTile().getProperties().containsKey("drop")){
                        addDrop(dropLayer.getCell(i, j).getTile(), i, j);
                    }
                }
            }
        }

    }
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
    private void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.translate(width/2,height/2);
        camera.update();
    }
    public TiledMapTileLayer getCollisionLayer() {
        return (TiledMapTileLayer) map.getLayers().get("mainLayer");
    }
    public OrthographicCamera getCamera() {
        return camera;
    }
    public Batch getRendererBatch() {
        return renderer.getBatch();
    }
    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    private void addDrop(TiledMapTile tile, int tileX, int tileY){
        String label = (String) tile.getProperties().get("drop");
        TextureRegion tileTextureRegion = tile.getTextureRegion();
        float x = getCollisionLayer().getTileWidth()*tileX*mapUnitScale;
        float y = getCollisionLayer().getTileHeight()*tileY*mapUnitScale;
        System.out.println("x: " + x + "y: " + y);
        switch (label){
            case "ammo":
                drops.add(new AmmoBox(tileTextureRegion,x,y,mapUnitScale,getCollisionLayer()));
                break;

            case "health":
                drops.add(new HealthBox(tileTextureRegion,x,y,mapUnitScale,getCollisionLayer()));
                break;
        }
    }

    public List<Drop> getDropsList() {
        return drops;
    }
}