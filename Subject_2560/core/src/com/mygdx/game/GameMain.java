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
	private float charX =  100;
	private float charY = 200;
	private Rectangle still = new Rectangle();
	private Texture stillImg;
	private Texture flatTexture;
	private int moveSpeed = 6;
	private int jumpSpeed = 20;
	private float velocityX = 0;
	private float velocityY = 0;
	private float damping_factor;
	private boolean jumping = false;
	private Viewport viewport;
	private Camera camera;
	Player mainPlayer;

	@Override
	public void create () {
		mainPlayer = new Player(0,0,100,100, 1000);
		camera = new PerspectiveCamera();
		viewport = new FitViewport(800, 480, camera);
		batch = new SpriteBatch();		//main spritebatch that's used to draw all of the elements (so far) to the screen
		testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.atlas"));	//atlas for main "roll" animation
		rightRollAnimation = new Animation<TextureRegion>(1/60f, testAtlas.getRegions()); //Actual animation object (60fps)
		stillImg = new Texture(Gdx.files.internal("still.png"));	//texture for the "still" image (for when the character is moving neither right nor left)
		flatTexture = new Texture("blackbox.png");	//basic all black texture used to spawn test rectangles
		damping_factor = 0.07f;									//damping factor for the sliding effect for when the character stops
	}

	@Override
	public void render () {
		//sets background to white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		//displays background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		//This line is used to get the time differential needed to display the animation at the correct framerate
		timePassed += Gdx.graphics.getDeltaTime();

		/*
		Main Key input conditionals
		 */
		if((Gdx.input.isKeyPressed(Input.Keys.SPACE)
				|| Gdx.input.isKeyPressed(Input.Keys.W)
				|| Gdx.input.isKeyPressed(Input.Keys.UP))
				&& !jumping)	//if space is pressed and the char is not already jumping
		{
			jumping = true;
			velocityY = 1;
		}
		if(jumping && Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			mainPlayer.forceDown();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
		{
			if(velocityX > -1) velocityX -= .15f;
			batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), charX+100, charY, -100, 100);
			charX += moveSpeed*velocityX;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
		{
			if(velocityX < 1) velocityX += .15f;
			batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), charX, charY, 100, 100);
			charX += moveSpeed*velocityX;
		}
		else
			batch.draw(stillImg, charX, charY, 100, 100);

		//boundary limits so that the the character can never be off-screen
		if(charX < 0) charX = 0;
		if(charX > 1180) charX = 1180;

		batch.end();
	}

	@Override
	public void dispose () {
		//method to make garbage collection more efficient
		batch.dispose();
		testAtlas.dispose();
	}
}
