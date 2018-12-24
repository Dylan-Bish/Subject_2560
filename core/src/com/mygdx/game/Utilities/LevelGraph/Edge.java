package com.mygdx.game.Utilities.LevelGraph;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Edge {
    private Node leftNode, rightNode;   //"left" or "right" do not necessarily pertain to actual direction, it is just an arbitrary method of distinction
    private int weight;

    public Edge(Node leftNode, Node rightNode){
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public void debugDraw(ShapeRenderer sr){

    }
}
