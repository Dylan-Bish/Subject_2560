package com.mygdx.game.Utilities.LevelGraph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.Model.Level;
import com.mygdx.game.Model.Light;

import java.util.ArrayList;

public class Graph {
    private Level currentLevel;
    private float mapUnitScale;
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;

    public Graph(Level currentLevel){
        this.currentLevel = currentLevel;
        this.mapUnitScale = currentLevel.getUnitScale();

        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        createGraph();

    }


    private void createGraph(){
        TiledMapTileLayer collisionLayer = currentLevel.getCollisionLayer();

        //go through each position on the map and spawn each light
        for(int row = 0; row  < collisionLayer.getWidth(); row++){
            for(int col = 0; col < collisionLayer.getHeight(); col++){
                if(collisionLayer.getCell(row,col) != null) {
                    if (!collisionLayer.getCell(row, col).getTile().getProperties().containsKey("rigid")) {
                        float centerX = collisionLayer.getTileWidth() * (row + 0.5f) * mapUnitScale;
                        float centerY = collisionLayer.getTileHeight() * (col + 0.5f) * mapUnitScale;
                        nodes.add(new Node(centerX, centerY, Node.Type.PLATFORM));
                    }
                }
            }
        }
/*
        for(int row = 0; row  < collisionLayer.getWidth(); row++){
            for(int col = 0; col < collisionLayer.getHeight(); col++){
                if(collisionLayer.getCell(i,j) != null) {
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
        }*/
    }

    public void debugDrawGraph(){
        ShapeRenderer sr = new ShapeRenderer();
        sr.setProjectionMatrix(currentLevel.getCamera().combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(Node node : nodes)
            node.debugDraw(sr);
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        for(Edge edge : edges)
            edge.debugDraw(sr);

    }
}
