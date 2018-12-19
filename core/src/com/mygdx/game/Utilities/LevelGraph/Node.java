package com.mygdx.game.Utilities.LevelGraph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class Node {
    //in-game coordinates of the node
    private float x, y;
    //all edges attached to the node
    private ArrayList<Edge> edges;
    //used to group nodes together that belong to the same  platform
    private int id;
    //the type of node it is, important for pathfinding algo. Similar to this implementation:
    // https://www.gamasutra.com/blogs/YoannPignole/20150427/241995/The_Hobbyist_Coder_3__2D_platformers_pathfinding__part_12.php
    private Type type;

    public Node(float x, float y, Type type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void addEdge(Edge edge){
        edges.add(edge);
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public int getID(){
        return this.id;
    }

    public void debugDraw(ShapeRenderer sr){
        switch(this.type) {
            case NONE:
                //set the color of the node to be drawn to gray
                sr.setColor(0.5f, 0.5f, 0.5f, 1f);
                break;

            case ANTIGRAV:
                //set the color of the node to be drawn to magenta
                sr.setColor(1, 0, 1, 1);
                break;

            case LEFTEND:
                //set the color of the node to be drawn to red
                sr.setColor(1, 0, 0, 1);
                break;

            case RIGHTEND:
                //set the color of the node to be drawn to green
                sr.setColor(0, 1, 0, 1);
                break;

            case PLATFORM:
                //set the color of the node to be drawn to yellow
                sr.setColor(1, 1, 0, 1);
                break;

            case DOUBLEEND:
                //set the color of the node to be drawn to blue
                sr.setColor(0, 0, 1, 1);
                break;
        }
        sr.circle(this.x, this.y, 5);
    }

    enum Type{
        NONE,
        ANTIGRAV,
        LEFTEND,
        RIGHTEND,
        PLATFORM,
        DOUBLEEND;
    }
}
