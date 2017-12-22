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
    PISTOLADV,
    RIFLEADV,
    SHOTGUNADV,
    PLASMAKNIFE;

    public int dmg;

    Weapons (int dmg)
    {
        this.dmg = dmg;
    }

    Weapons() {}

    public int getDmg()
    {
        return dmg;
    }

}
