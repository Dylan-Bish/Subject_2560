package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Drops.AmmoBox;
import com.mygdx.game.Drops.Drop;
import com.mygdx.game.Drops.HealthBox;
import com.mygdx.game.Drops.Key;
import com.mygdx.game.Entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Level{
    //the scale of the map. value of 1 indicates no scaling
    private float mapUnitScale;
    //the tiledMap of our level
    private TiledMap map;
    //the camera which we will use to render the scene
    private OrthographicCamera camera;
    //All of the drops in the level (ammo boxes, health boxes, etc.)
    private ArrayList<Drop> drops;
    //the renderer for the tiledMap
    private TiledMapRenderer otmRenderer;
    //Spritebatch to actually draw everything to the screen. Same batch as the GameMain class
    private SpriteBatch batch;

    public Level(float mapUnitScale, String mapFilePath){
        this.mapUnitScale = mapUnitScale;
        this.batch = new SpriteBatch();

        //load the map from the raw file
        map = new TmxMapLoader().load(mapFilePath);

        this.otmRenderer = new OrthogonalTiledMapRenderer(map, mapUnitScale, batch);
        this.camera = new OrthographicCamera();
        this.drops = new ArrayList<>();

        //set the size to the dimensions of the current window dimensions
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //get the main layer of the level where all the collision happens
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("mainLayer");
        //center the main layer
        collisionLayer.setOffsetX(0);
        collisionLayer.setOffsetY(0);

        //get the layer that has all the drops on it
        TiledMapTileLayer dropLayer = (TiledMapTileLayer) map.getLayers().get("drops");
        //center the layer
        dropLayer.setOffsetX(0);
        dropLayer.setOffsetY(0);
        //we don't want to see this layer, because the drops are spawned and drawn as their own dynamic thing, so
        //if the layer was set to visible, each drop would be seen twice, and you'd never be able to pick up the drops
        //that are displayed on the layer. The layer is use only for getting the initial info for the drops.
        dropLayer.setVisible(false);
        //go through each position on the map and spawn each drop
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
    public ArrayList<Drop> getDropList(){
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
    public ArrayList<Light> getLights(){
        ArrayList<Light> lights = new ArrayList<>();

        //get the layer that has all the lights on it
        TiledMapTileLayer lightsLayer = (TiledMapTileLayer) map.getLayers().get("lights");
        //center the layer
        lightsLayer.setOffsetX(0);
        lightsLayer.setOffsetY(0);

        lightsLayer.setVisible(true);
        //go through each position on the map and spawn each light
        for(int i = 0; i  < lightsLayer.getWidth(); i++){
            for(int j = 0; j < lightsLayer.getHeight(); j++){
                if(lightsLayer.getCell(i,j) != null) {
                    if (lightsLayer.getCell(i, j).getTile().getProperties().containsKey("light")){
                        TiledMapTile tile = lightsLayer.getCell(i, j).getTile();
                        String intesity = (String) tile.getProperties().get("light");
                        Color lightColor = (Color)tile.getProperties().get("color");
                        float centerX = getCollisionLayer().getTileWidth()*(i+0.5f)*mapUnitScale;
                        float centerY = getCollisionLayer().getTileHeight()*(j+0.5f)*mapUnitScale;
                        switch (intesity){
                            case "1":
                                lights.add(new Light(lightColor, centerX, centerY, 400));
                                break;
                            case "2":
                                lights.add(new Light(lightColor, centerX, centerY, 750));
                                break;
                            case "3":
                                lights.add(new Light(lightColor, centerX, centerY, 1200));
                                break;
                        }
                    }
                }
            }
        }
        lightsLayer.setVisible(false);
        return lights;
    }
    public SpriteBatch getBatch(){
        return this.batch;
    }
    public void renderForeground(ArrayList<Entity> entities, ArrayList<Drop> drops){
        int[] layer = {2};
        otmRenderer.setView(camera);
        otmRenderer.render(layer);

        this.batch.begin();
        for(Entity entity : entities) entity.draw(this.batch);
        for(Drop drop : drops) drop.draw(this.batch);
        batch.end();

    }
    public void renderBackground(){
        Gdx.gl.glClearColor(0f,0f,0f,0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int[] layer = {0,1};
        otmRenderer.setView(camera);
        otmRenderer.render(layer);
    }
}