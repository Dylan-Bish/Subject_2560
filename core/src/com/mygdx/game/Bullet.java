package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Bullet {

    //Raw damage done by bullet
    private int _damage;

    //Fired velocity of the bullet
    private int _velocity;

    //The angle at which the bullet SHOULD travel, ideally
    private int _idealAngle;

    //The Vector2 point of the origin of the bullet
    private Vector2 _initialPoint;

    //Max angle, in both directions, that the fired bullet may vary from the _idealAngle
    private int _variationAngle;

    //All instance variables set by constructor

    Bullet(int damage, int velocity, int idealAngle, Vector2 initialPoint, int variationAngle) {
        _damage = damage;
        _velocity = velocity;
        _idealAngle = idealAngle;
        _initialPoint = initialPoint;
        _variationAngle = variationAngle;
    }

    public void updatePhysics(){

    }
}
