package com.team.retrogame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.team.retrogame.Scenes.Hud;
import com.team.retrogame.Scenes.PauseScreen;
import com.team.retrogame.Sprites.Blobb;
import com.team.retrogame.Tools.B2WorldCreator;
import com.team.retrogame.Tools.WorldContactListener;
import com.team.retrogame.RetroGame;

/**
 * created by Ben Mankin on 09/13/18.
 */

public class PlayScreen implements Screen {

    //Reference to the Game, used to set Screens
    private RetroGame game;
    private TextureAtlas atlas;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private PauseScreen pause;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    private Blobb player;

    //music
    private Music music;

    //pause State shifting variables
    private boolean setToPause;
    private boolean setToResume;

    public PlayScreen(RetroGame game) {
        //helps to locate sprites
        atlas = new TextureAtlas("blobb.pack");

        this.game = game;
        //create cam used to follow the player through game world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gamePort = new FitViewport(RetroGame.V_WIDTH / RetroGame.PPM, RetroGame.V_HEIGHT / RetroGame.PPM, gamecam);

        //create the game HUD for scores/timers/level info
        hud = new Hud(game.batch);

        pause = new PauseScreen(game.batch);

        //Load the map and setup the map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("sandbox.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / RetroGame.PPM);

        //initially set the gamcam to be centered correctly at the start of of map
        gamecam.position.set((gamePort.getWorldWidth() / 2), (gamePort.getWorldHeight() / 2), 0);

        //create the Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);

        //allows for debug lines of the box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //create RetroGame in the game world
        player = new Blobb(this);

        world.setContactListener(new WorldContactListener());

        /*
        music = RetroGame.manager.get("audio/music/Blobb_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
        */

        setToPause = false;
        setToResume = false;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    private void handleInput(float dt) {
        //control RetroGame using immediate impulses

        //if RetroGame isn't dead, these inputs are valid
        if(player.currentState != Blobb.State.DEAD) {
            if(setToResume || !setToPause) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                    player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x <= 2)
                    player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x >= -2)
                    player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
            }

            //pause and unpause functionality
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                if (!setToPause)
                    pause();

                else if (!setToResume)
                    resume();
            }
        }
    }

    private void update(float dt) {
        //handle user input first
        handleInput(dt);

        if (setToResume || !setToPause) {

            //takes 1 step in the physics simulation(60 times per second)
            world.step(1 / 60f, 6, 2);

            player.update(dt);

            //attach the gamecam to our players.x coordinate
            if (player.currentState != Blobb.State.DEAD) {
                gamecam.position.x = player.b2Body.getPosition().x;
            }

            //update the gamecam with correct coordinates after changes
            gamecam.update();

            //tell the renderer to draw only what the camera can see in the game world.
            renderer.setView(gamecam);
        }

    }

    @Override
    public void render(float delta) {
        //separate the update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render the game map
        renderer.render();

        //render the Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        //Set the batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if (setToPause) {
            pause.stage.draw();
        }

        if (setToResume) {
            pause.stage.dispose();
        }
    }

    private boolean gameOver() {
        if(player.currentState == Blobb.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        //update the game viewport
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {
        setToPause = true;
        setToResume = false;
        //music.pause();
    }

    @Override
    public void resume() {
        setToResume = true;
        setToPause = false;
        //music.play();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose of all the opened resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();

    }

    public Hud getHud() {
        return hud;
    }
}
