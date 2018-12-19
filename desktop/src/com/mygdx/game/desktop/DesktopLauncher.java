package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//if we set fullscreen and also set v-sync on, the game seems to consistently run with a very smooth framerate and no jitter
		config.width = 1600;
		config.height = 900;
		config.fullscreen = false;
        config.vSyncEnabled = true;
		config.resizable = false;
		config.title = "Subject 2560";
		new LwjglApplication(new GameMain(), config);
	}
}
