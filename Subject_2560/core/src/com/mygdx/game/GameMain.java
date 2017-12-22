package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;


public class GameMain extends ApplicationAdapter {
	SpriteBatch batch;
	private TextureAtlas testAtlas;
	private Animation<TextureRegion> rightRollAnimation;
	private float timePassed = 0f;
	private Viewport viewport;
	private Camera camera;
	Player mainPlayer;

	@Override
	public void create () {
		mainPlayer = new Player(0,0,100,100, 1000);
		batch = new SpriteBatch();		//main spritebatch that's used to draw all of the elements (so far) to the screen
		testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
		//rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
	}

	@Override
	public void render () {
		//sets background to white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		//displays background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		/*
		Main Key input conditionals
		 */

		timePassed += Gdx.graphics.getDeltaTime();

		if((Gdx.input.isKeyPressed(Input.Keys.SPACE)
				|| Gdx.input.isKeyPressed(Input.Keys.W)
				|| Gdx.input.isKeyPressed(Input.Keys.UP))
				&& !mainPlayer.getJumping())	//if space is pressed and the char is not already jumping
		{
			mainPlayer.jump();
			mainPlayer.updatePhysics();
		}
		if(mainPlayer.getJumping() && Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			mainPlayer.forceDown();
			mainPlayer.updatePhysics();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
		{
			handleLeft(mainPlayer, timePassed, batch);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
		{
			handleRight(mainPlayer, timePassed, batch);
		}
		else
		{
			batch.draw(mainPlayer.getStill(), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getWidth(), mainPlayer.getHeight());
			mainPlayer.updatePhysics();
		}

		batch.end();
	}

	private void handleRight(Character character, float timePassed, SpriteBatch batch)
	{
		character.moveRight();
		batch.draw(character.getRightAnimation().getKeyFrame(timePassed, true), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getWidth(), mainPlayer.getHeight());
		character.updatePhysics();
	}

	private void handleLeft(Character character, float timePassed, SpriteBatch batch)
	{
		character.moveLeft();
		batch.draw(character.getRightAnimation().getKeyFrame(timePassed, true), (mainPlayer.getX() + mainPlayer.getWidth()), mainPlayer.getY(), mainPlayer.getWidth()*(-1), mainPlayer.getHeight());
		character.updatePhysics();
	}

	@Override
	public void dispose () {
		//method to make garbage collection more efficient
		batch.dispose();
		testAtlas.dispose();
	}
}
