package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapHandler{

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    MapHandler(float unitScale)
    {
        map = new TmxMapLoader().load("maps/MyMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
    }

    public void render(float delta)
    {
        renderer.setView(camera);
        renderer.render();
    }

    public void dispose()
    {
        map.dispose();
        renderer.dispose();
    }

    public void resize(int width, int height)
    {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.translate(width/2,height/2);
        camera.update();
    }

    public void translateCamera(int x) {
        camera.translate(x, 0);
        camera.update();
    }

    public TiledMapTileLayer getCollisionLayer()
    {
        return (TiledMapTileLayer) map.getLayers().get(0);
    }

    public void show() {
        map = new TmxMapLoader().load("maps/MyMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map,0.25f);
        camera = new OrthographicCamera();
    }
}
