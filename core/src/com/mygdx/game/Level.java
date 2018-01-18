package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

public class Level {

    private float mapUnitScale;
    private TiledMap map;
    private OrthographicCamera camera;
    private List<Drop> drops;
    private OrthogonalTiledMapRenderer renderer;

    Level(float mapUnitScale, String mapFilePath){
        this.mapUnitScale = mapUnitScale;
        map = new TmxMapLoader().load(mapFilePath);
        renderer = new OrthogonalTiledMapRenderer(map, mapUnitScale);
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
                        TiledMapTile tile = dropLayer.getCell(i, j).getTile();
                        String label = (String) tile.getProperties().get("drop");
                        TextureRegion tileTextureRegion = tile.getTextureRegion();
                        float x = getCollisionLayer().getTileWidth()*i*mapUnitScale;
                        float y = getCollisionLayer().getTileHeight()*j*mapUnitScale;
                        switch (label){
                            case "ammo":
                                drops.add(new AmmoBox(tileTextureRegion,x,y,mapUnitScale,getCollisionLayer()));
                                break;

                            case "health":
                                drops.add(new HealthBox(tileTextureRegion,x,y,mapUnitScale,getCollisionLayer()));
                                break;
                        }
                    }
                }
            }
        }
    }

    private void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.translate(width/2,height/2);
        camera.update();
    }

    public boolean hasProperty(float x, float y, String property, boolean roundUpX, boolean roundUpY){
        if(roundUpX) x -= 0.125f;
        if(roundUpY) y -= 0.125f;
        return hasProperty(x,y,property);
    }

    public boolean hasProperty(float x, float y, String property){
        int tileX = (int)(x / (getCollisionLayer().getTileWidth()*mapUnitScale));
        int tileY = (int)(y / (getCollisionLayer().getTileHeight()*mapUnitScale));

        if(getCollisionLayer().getCell(tileX, tileY) == null) return false;
        else
            return getCollisionLayer().getCell(tileX, tileY).getTile().getProperties().containsKey(property);
    }

    public TiledMapTileLayer getCollisionLayer(){
        return (TiledMapTileLayer) map.getLayers().get("mainLayer");
    }

    public TiledMapTileLayer getDropLayer() {
        return (TiledMapTileLayer) map.getLayers().get("drops");
    }

    public List<Drop> getDropList(){
        return drops;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getMapUnitScale(){
        return mapUnitScale;
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    public Batch getRendererBatch() {
        return renderer.getBatch();
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    public void checkForDrops(Player mainPlayer){
        for(int i = 0; i < drops.size(); i++){
            if(((mainPlayer.getX() + mainPlayer.getWidth()) > drops.get(i).getX())
             && (mainPlayer.getX() < (drops.get(i).getX() + drops.get(i).getWidth()))
             &&((mainPlayer.getY() + mainPlayer.getHeight()) > drops.get(i).getY())
             && (mainPlayer.getY() < (drops.get(i).getY()) + drops.get(i).getHeight())){
                if(drops.get(i).giveTo(mainPlayer)){
                    drops.remove(i);
                    i++;
                }
            }
        }
    }
}
