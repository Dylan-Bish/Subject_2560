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
import com.badlogic.gdx.math.Rectangle;

import static java.lang.Math.abs;

public class GameMain extends ApplicationAdapter {
	SpriteBatch batch;
	private TextureAtlas testAtlas;
	private Animation<TextureRegion> rightRollAnimation;
	private float timePassed = 0f;
	private float charX =  100;
	private float charY = 200;
	private Rectangle still = new Rectangle();
	private Texture stillImg;
	private int moveSpeed = 6;
	private int jumpSpeed = 10;
	private float velocityX = 0;
	private float velocityY = 0;
	private float damping_factor;
	private boolean jumping = false;

	@Override
	public void create () {

		batch = new SpriteBatch();
		testAtlas = new TextureAtlas(Gdx.files.internal("rightroll.Atlas"));
		rightRollAnimation = new Animation<TextureRegion>(1/30f, testAtlas.getRegions());
		stillImg = new Texture(Gdx.files.internal("still.png"));
		damping_factor = 0.07f;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		timePassed += Gdx.graphics.getDeltaTime();

		if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !jumping)
		{
			jumping = true;
			velocityY = 1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
		{
			batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), charX+100, charY, -100, 100);
			charX -= moveSpeed;
			velocityX = -1;
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
		{
			batch.draw(rightRollAnimation.getKeyFrame(timePassed, true), charX, charY, 100, 100);
			charX += moveSpeed;
			velocityX = 1;
		}
		else
			batch.draw(stillImg, charX, charY, 100, 100);

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
			if (charY < 200) {
				charY = 200;
				jumping = false;
				velocityY = 0;
			} else {
				charY += jumpSpeed * velocityY;
				velocityY -= 0.05f;
			}
		}


		if(charX < 0) charX = 0;
		if(charX > 1180) charX = 1180;

		batch.end();
	}


	@Override
	public void dispose () {
		batch.dispose();
		testAtlas.dispose();
	}
}
