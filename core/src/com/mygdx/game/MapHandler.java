package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapHandler{

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private float unitScale;
    private Player mainPlayer;
    private float timePassed = 0f;

    MapHandler(float unitScale, Player mainPlayer)
    {
        map = new TmxMapLoader().load("maps/MyMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        camera = new OrthographicCamera();
        this.unitScale = unitScale;
        this.mainPlayer = mainPlayer;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    public TiledMapTileLayer getCollisionLayer()
    {
        return (TiledMapTileLayer) map.getLayers().get(0);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Batch getRendererBatch()
    {
        return renderer.getBatch();
    }

    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }
}
