package com.mygdx.game;

/**
 * Created by root on 12/21/17.
 */
public enum Weapons {
    FISTS,
    KNIFE,
    PISTOL,
    RIFLE,
    SHOTGUN,
    PISTOL_ADV,
    RIFLE_ADV,
    SHOTGUN_ADV,
    PLASMAKNIFE;

    public int dmg;

    Weapons() {}

    Weapons (int dmg)
    {
        this.dmg = dmg;
    }

    public int getDmg()
    {
        return dmg;
    }

}
