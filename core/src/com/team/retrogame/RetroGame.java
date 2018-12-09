package com.team.retrogame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.team.retrogame.Screens.PlayScreen;

/**
 * created by Ben Mankin on 09/13/18.
 */

public class RetroGame extends Game {
	/*
	Because Box2D scales things out very far initially,
	Virtual Machine Width and Height are used to constrain the game to a smaller
	play area. PPM (Pixels per Meter) is the scaling factor used to make the conversion
	to this area
	 */
	public static final int V_WIDTH = 640;
	public static final int V_HEIGHT = 350;
	public static final float PPM = 100;

	/*
	Declare a series of bits that will be used for Box2D's collision system.
	Bitwise operations can be performed using them as arguments to determine what
	fixtures collided. For instance, when RetroGame collides with a Brick, an OR operation
	will yield a unique number (6 in this case), and appropriate code can follow
	 */
	public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
	public static final short WALL_BIT = 2048;
	public static final short BLOBB_GENERAL_BIT = 2;
	public static final short BLOBB_FEET_BIT = 4;
	public static final short BLOBB_HEAD_BIT = 8;
	public static final short BLOBB_LEFT_BIT = 16;
	public static final short BLOBB_RIGHT_BIT = 32;
	public static final short SPIKE_BIT = 64;
	public static final short TRAMPOLINE_BIT = 128;
	public static final short ONE_WAY_PLATFORM_BIT = 256;
	public static final short CRUMBLE_PLATFORM_BIT = 512;
	public static final short GOAL_BIT = 1024;

	//Universal SpriteBatch. All sprites contained, and passed around by this one instance
	public SpriteBatch batch;

	//Universal AssetManager. All assets contained, and passed around by this one instance
	public static AssetManager manager;

	//Shader stuff
	private String vertShader, fragShader;
	public ShaderProgram shaderProgram;

	/*
	Create SpriteBatch and AssetManager. Load manager with sounds and music. Set Beginning
	screen to PlayScreen
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/iwishthiswasundertale.mp3", Music.class);

		//Find the vertex/fragment shaders,start running a shader program utilizing both.
		vertShader = Gdx.files.internal("Shaders/levelVertex.glsl").readString();
		fragShader = Gdx.files.internal("Shaders/levelFragment.glsl").readString();
		shaderProgram = new ShaderProgram(vertShader, fragShader);
		//Allows the shader program to run without using every uniform.
		shaderProgram.pedantic = false;


		/*
		manager.load("audio/sounds/bubbleJump.mp3", Sound.class);
		manager.load("audio/sounds/bubbleLand.mp3", Sound.class);
		manager.load("audio/sounds/bubblePound.mp3", Sound.class);
		manager.load("audio/sounds/bubbleSplat.mp3", Sound.class);
		manager.load("audio/sounds/bubbleFloat.mp3", Sound.class);
		manager.load("audio/sounds/bubbleFloatEnd.mp3", Sound.class);
		manager.load("audio/sounds/bubbleFloatPop.mp3", Sound.class);
		manager.load("audio/sounds/bubbleButtBounce.mp3", Sound.class);
		manager.load("audio/sounds/bubbleDie.mp3", Sound.class);
		manager.load("audio/sounds/spring.mp3", Sound.class);
		*/
		manager.finishLoading();
		setScreen(new PlayScreen(this, "tiled/master_module.tmx"));
	}

	//dispose of resources
	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}

	//render resources
	@Override
	public void render () {
		super.render();
		/*
		if(manager.update())
			waits to see if all assets are loaded, and returns true if they are. Can use this
			to wait and see if things are loaded, and put code here that requires
			those assets. manager.finishLoading() above waits til all assets are loaded
			and then continues. This option is just if there is something more specific
			thing to do with the assets once they're loaded.
		 */
	}
}