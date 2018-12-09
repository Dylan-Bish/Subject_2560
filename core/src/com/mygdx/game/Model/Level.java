package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Drops.AmmoBox;
import com.mygdx.game.Drops.Drop;
import com.mygdx.game.Drops.HealthBox;
import com.mygdx.game.Drops.Key;

import java.util.ArrayList;
import java.util.List;

public class Level{

    private float mapUnitScale;
    private TiledMap map;
    private OrthographicCamera camera;
    private List<Drop> drops;
    private TiledMapRenderer otmRenderer;
    private SpriteBatch batch;

    public Level(float mapUnitScale, String mapFilePath){
        this.mapUnitScale = mapUnitScale;
        map = new TmxMapLoader().load(mapFilePath);
        this.batch = new SpriteBatch();
        otmRenderer = new OrthogonalTiledMapRenderer(map, mapUnitScale, batch);
        camera = new OrthographicCamera();
        drops = new ArrayList<>();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("mainLayer");
        collisionLayer.setOffsetX(0);
        collisionLayer.setOffsetY(0);

        /* take all them Drops and spawn em */
        TiledMapTileLayer dropLayer = (TiledMapTileLayer) map.getLayers().get("drops");
        dropLayer.setOffsetX(0);
        dropLayer.setOffsetY(0);
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
                            case "key":
                                String keyColor = (String)tile.getProperties().get("key");
                                drops.add(new Key(tileTextureRegion,x,y,mapUnitScale,getCollisionLayer(),keyColor));
                                break;
                        }
                    }
                }
            }
        }
    }

    public void resize(int width, int height) {
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
    public TiledMapRenderer getRenderer() {
        return otmRenderer;
    }
    public void dispose() {
        //getRendererBatch().dispose();
        //renderer.dispose();
        map.dispose();
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
    public void updateCamera(Player mainPlayer){
        /**
        if(mainPlayer.getX() > (mainPlayer.getWidth()/2)+Gdx.graphics.getWidth()*(1/4f))
            camera.position.x  = mainPlayer.getX()+(mainPlayer.getWidth()/2)+Gdx.graphics.getWidth()/4f;
        if(mainPlayer.getY() > (mainPlayer.getHeight()/2)+Gdx.graphics.getHeight()/2)
            camera.position.y  = mainPlayer.getY()+(mainPlayer.getHeight()/2);
        */
        camera.position.x  = mainPlayer.getCenterX() ;
        camera.position.y  = mainPlayer.getCenterY();
        camera.update();

    }
    public void openDoor(Player mainPlayer){
        float playerMidX = mainPlayer.getX()+(mainPlayer.getWidth()/2);
        float playerMidY = mainPlayer.getY()+(mainPlayer.getHeight()/2);
        //condition to check door on the right of the player
        if(hasProperty(playerMidX+getCollisionLayer().getTileWidth()*mapUnitScale, playerMidY, "door")){
            int doorTileX = (int)((playerMidX+getCollisionLayer().getTileWidth()*mapUnitScale)/(getCollisionLayer().getTileWidth()*mapUnitScale));
            int doorTileY = (int)(playerMidY/(getCollisionLayer().getTileHeight()*mapUnitScale));
            if(mainPlayer.hasKey((String)(getCollisionLayer().getCell(doorTileX,doorTileY).getTile().getProperties().get("door"))))
                getCollisionLayer().setCell(doorTileX, doorTileY,null);
        }
        //condition to check door on the left of the player
        if(hasProperty(playerMidX-getCollisionLayer().getTileWidth()*mapUnitScale, playerMidY, "door")){
            int doorTileX = (int)((playerMidX-getCollisionLayer().getTileWidth()*mapUnitScale)/(getCollisionLayer().getTileWidth()*mapUnitScale));
            int doorTileY = (int)(playerMidY/(getCollisionLayer().getTileHeight()*mapUnitScale));
            if(mainPlayer.hasKey((String)(getCollisionLayer().getCell(doorTileX,doorTileY).getTile().getProperties().get("door"))))
                getCollisionLayer().setCell(doorTileX, doorTileY,null);
        }
        //conditional to check door below the player
        if(hasProperty(playerMidX, playerMidY-getCollisionLayer().getTileHeight()*mapUnitScale, "door")){
            int doorTileX = (int)(playerMidX/(getCollisionLayer().getTileWidth()*mapUnitScale));
            int doorTileY = (int)((playerMidY-getCollisionLayer().getTileHeight()*mapUnitScale)/(getCollisionLayer().getTileHeight()*mapUnitScale));
            if(mainPlayer.hasKey((String)(getCollisionLayer().getCell(doorTileX,doorTileY).getTile().getProperties().get("door"))))
                getCollisionLayer().setCell(doorTileX, doorTileY,null);
        }
        //conditional to check door above the player
        if(hasProperty(playerMidX, playerMidY+getCollisionLayer().getTileHeight()*mapUnitScale, "door")){
            int doorTileX = (int)(playerMidX/(getCollisionLayer().getTileWidth()*mapUnitScale));
            int doorTileY = (int)((playerMidY+getCollisionLayer().getTileHeight()*mapUnitScale)/(getCollisionLayer().getTileHeight()*mapUnitScale));
            if(mainPlayer.hasKey((String)(getCollisionLayer().getCell(doorTileX,doorTileY).getTile().getProperties().get("door"))))
                getCollisionLayer().setCell(doorTileX, doorTileY,null);
        }
    }
}