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
    private ArrayList<ArrayList<Node>> nodes;
    private ArrayList<ArrayList<Edge>> edges;

    ShapeRenderer sr;

    public Graph(Level currentLevel){
        this.currentLevel = currentLevel;
        this.mapUnitScale = currentLevel.getUnitScale();

        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        sr = new ShapeRenderer();
        createGraph();

    }


    private void createGraph(){
        TiledMapTileLayer collisionLayer = currentLevel.getCollisionLayer();

        //go through each position on the map and spawn each light
        for(int row = 0; row  < collisionLayer.getWidth(); row++){
            nodes.add(new ArrayList<Node>());
            for(int col = 0; col < collisionLayer.getHeight(); col++){
                float centerX = collisionLayer.getTileWidth() * (col + 0.5f) * mapUnitScale;
                float centerY = collisionLayer.getTileHeight() * (row + 0.5f) * mapUnitScale;

                if(collisionLayer.getCell(col,row-1) != null) {
                    if (collisionLayer.getCell(col, row-1).getTile().getProperties().containsKey("rigid")) {

                        nodes.get(row).add(new Node(centerX, centerY, Node.Type.PLATFORM));

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

        sr.setProjectionMatrix(currentLevel.getCamera().combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(ArrayList<Node> nodeRow : nodes){
            for(Node node : nodeRow){
                node.debugDraw(sr);
            }
        }
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        for(ArrayList<Edge> edgeRow : edges) {
            for (Edge edge : edgeRow) {
                edge.debugDraw(sr);
            }
        }
        sr.end();
    }
}
