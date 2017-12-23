package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
 * Created by Dylan Bish 12/19/2017
 */

public class GameMain extends ApplicationAdapter {
	private SpriteBatch batch;
	private Player mainPlayer;
	private float timePassed = 0f;

	@Override
	public void create () {
		mainPlayer = new Player(0,0,70,70, 1000);
		batch = new SpriteBatch();		//main spritebatch that's used to draw all of the elements (so far) to the screen
	}

	@Override
	public void render () {
		//sets background to white
		Gdx.gl.glClearColor(1, 1, 1, 1);
		//displays background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();								//start rendering the spritebatch
		timePassed += Gdx.graphics.getDeltaTime();	//increment timepassed
		inputHandler(timePassed);					//use incremented timepassed to get input
		batch.end();								//stop rendering the spritebatch and restart
	}

	private void handleRight(Character character, float timePassed, SpriteBatch batch)
	{
		character.moveRight();		//call the moveright player method
		//draw out the component of the spritebatch based on properties of the mainplayer, but based on the timepassed given by this class's global float variable timepassed
		batch.draw(character.getRightAnimation().getKeyFrame(timePassed, true), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getWidth(), mainPlayer.getHeight());
		character.updatePhysics();	//each time the character's motion is adjusted, updatephysics should be called
	}

	private void handleLeft(Character character, float timePassed, SpriteBatch batch)
	{
		character.moveLeft();		//call the moveright player method
		//draw out the component of the spritebatch based on properties of the mainplayer, but based on the timepassed given by this class's global float variable timepassed
		batch.draw(character.getRightAnimation().getKeyFrame(timePassed, true), (mainPlayer.getX() + mainPlayer.getWidth()), mainPlayer.getY(), mainPlayer.getWidth()*(-1), mainPlayer.getHeight());
		character.updatePhysics();	//each time the character's motion is adjusted, updatephysics should be called
	}

	private void inputHandler(float timePassed)
	{
		if((Gdx.input.isKeyPressed(Input.Keys.SPACE)
				|| Gdx.input.isKeyPressed(Input.Keys.W)
				|| Gdx.input.isKeyPressed(Input.Keys.UP))
				&& !mainPlayer.getJumping())	//if space is pressed and the char is not already jumping
		{
			/* main jumping conditional  */
			mainPlayer.jump();
			mainPlayer.updatePhysics();
		}
		if(mainPlayer.getJumping() && Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			/* main "force down" conditional  */
			mainPlayer.forceDown();
			mainPlayer.updatePhysics();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
		{
			/* main left movement handler */
			handleLeft(mainPlayer, timePassed, batch);
		}
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
		{
			/* main right movement handler */
			handleRight(mainPlayer, timePassed, batch);
		}
		else
		{
			/* main no-input handler */
			batch.draw(mainPlayer.getStill(), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getWidth(), mainPlayer.getHeight());
			mainPlayer.updatePhysics();
		}
	}

	@Override
	public void dispose () {
		//method to make garbage collection more efficient
		mainPlayer.dispose();
	}
}
