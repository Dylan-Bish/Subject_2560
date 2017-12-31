package com.mygdx.game;

public enum Weapon {

    FISTS,
    KNIFE,
    PISTOL,
    RIFLE,
    SHOTGUN,
    SNIPER,
    PLASMA_KNIFE,
    PISTOL_ADV,
    RIFLE_ADV,
    SHOTGUN_ADV;

    private int damage;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int Damage) {
        damage = Damage;
    }


}
