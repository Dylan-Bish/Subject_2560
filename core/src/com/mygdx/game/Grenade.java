package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Grenade {
    private int damage;
    private float x,y;
    private float width, height;
    private float velocityX, velocityY;
    private float antiGravAccel = 0.1f;
    private float gravity = -.05f;
    private float damping_factor = 0.15f;
    private float mapUnitScale;
    private Texture image;

    Grenade(int damage, int angleDeg, int initialVelocity, float mapUnitScale, int initialX, int initialY, int width, int height)
    {
        // need the map unit scale for the collision detection of the grenades
        this.mapUnitScale = mapUnitScale;
        this.damage = damage;
        // Split the initial velocity and angle into x and y components
        velocityX = (float)(initialVelocity*cos(toRadians(angleDeg)));
        velocityY = (float)(initialVelocity*sin(toRadians(angleDeg)));
        //the image for the grenade
        image = new Texture(Gdx.files.internal("temp_grenade.png"));
        //set the initial coordinates for the grenade
        this.x = initialX;
        this.y = initialY;
        //set the width and height of the image
        this.width = width;
        this.height = height;
    }

    public void updatePhysics(){

    }

    public void draw(Batch batch){
        batch.draw(image, x, y, width, height);
    }

    public void dispose(){

    }
}
