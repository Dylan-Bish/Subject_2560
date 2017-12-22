package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

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

	@Override
	public void create () {
		batch = new SpriteBatch();		//main spritebatch that's used to draw all of the elements (so far) to the screen
		testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.Atlas"));	//atlas for main "roll" animation
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

		//these two lines are used to get the time differential needed to display the animation at the correct framerate
		batch.begin();
		timePassed += Gdx.graphics.getDeltaTime();

		/*
		Main Key input conditionals
		 */
		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !jumping)	//if space is pressed and the char is not already jumping
		{
			jumping = true;
			velocityY = 1;
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

		/*
		main conditionals to handle the very basic "physics" that have so far been implemented
		 */
		if(velocityX < 0)
		{
			charX += moveSpeed*velocityX;
			velocityX /= (1+damping_factor);
		}
		else if(velocityX > 0)
		{
			charX +=  moveSpeed*velocityX;
			velocityX /= (1+damping_factor);
		}
		if(jumping) {
			if (charY < 0) {
				charY = 0;
				jumping = false;
				velocityY = 0;
			} else {
				charY += jumpSpeed * velocityY;
				velocityY -= 0.05f;
			}
		}

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
