package com.mygdx.game.Model;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Projectiles.Bullet;

public abstract class Weapon {

    private int _damage;
    private Bullet _bullet;

    public int getDamage() {
        return _damage;
    }

    public void setDamage(int Damage) {
        _damage = Damage;
    }

    public void setBullet(int damage, int velocity, int idealAngle, Vector2 initialPoint, int variationAngle) {
        //_bullet = new Bullet(damage, velocity, idealAngle, initialPoint, variationAngle);
    }
}
