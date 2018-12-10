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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.team.retrogame.Scenes.Hud;
import com.team.retrogame.Scenes.PauseScreen;
import com.team.retrogame.Sprites.Blobb;
import com.team.retrogame.Tools.B2WorldCreator;
import com.team.retrogame.Tools.WorldContactListener;
import com.team.retrogame.RetroGame;

import java.awt.*;
import java.util.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

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
    private Point spawnPoint;
    private String currentMap;

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

    //modules/module utilities
    private Random rand = new Random();
    private int moduleNum = 0;
    //String[] module = {"tiled/module_one.tmx", "tiled/module_two.tmx", "tiled/module_three.tmx", "tiled/module_four.tmx", "tiled/module_five.tmx", "tiled/module_six.tmx"};
    private LinkedList<String> levelModules = new LinkedList<String>(){
        {
            add("tiled/module_one.tmx");
            add("tiled/module_two.tmx");
            add("tiled/module_three.tmx");
            add("tiled/module_four.tmx");
            add("tiled/module_five.tmx");
            add("tiled/module_six.tmx");
            add("tiled/module_seven.tmx");
            add("tiled/graphic_test.tmx");
            add("tiled/master_module.tmx");
        }
    };

    public PlayScreen(RetroGame game, String newMap) {
        //helps to locate sprites
        atlas = new TextureAtlas("tiled/Blobb.atlas");

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
        map = mapLoader.load(newMap);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / RetroGame.PPM);
        currentMap = newMap;

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

        music = RetroGame.manager.get("audio/music/TheLegendOfBubbleBoyAndTheInfiniteFloat.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

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

        //if Blobb isn't dead, these inputs are valid
        if(player.currentState != Blobb.State.DYING) {
            //if game isn't paused and player isn't performing a special action, these inputs are valid
            if(setToResume || !setToPause) {
                //if player is standing or running
                if(player.currentState == Blobb.State.STANDING || player.currentState == Blobb.State.RUNNING) {
                    player.canFloat = true;
                    player.canDash = true;
                    if (Gdx.input.isKeyPressed(Input.Keys.A))
                        player.moveLeftGround();
                    if (Gdx.input.isKeyPressed(Input.Keys.D))
                        player.moveRightGround();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                        player.startGroundJump();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && (player.canFloat))
                        player.startFloat();
                }

                //if player is jumping or falling
                if(player.currentState == Blobb.State.JUMPING || player.currentState == Blobb.State.FALLING) {
                    if (Gdx.input.isKeyPressed(Input.Keys.A))
                        player.moveLeftAir();
                    if (Gdx.input.isKeyPressed(Input.Keys.D))
                        player.moveRightAir();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && (player.canFloat))
                        player.startFloat();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.J) && (player.canDash))
                        player.startDash();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.L) && player.touchingWall)
                        player.startGrab();
                }

                //if player is dashing
                if(player.currentState == Blobb.State.DASHING) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && (player.canFloat))
                        player.startFloat();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.L) && player.touchingWall)
                        player.startGrab();
                }

                //if player is floating
                if(player.currentState == Blobb.State.FLOATING) {
                    if (Gdx.input.isKeyPressed(Input.Keys.A))
                        player.moveLeftFloat();
                    if (Gdx.input.isKeyPressed(Input.Keys.D))
                        player.moveRightFloat();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.J) && (player.canDash))
                        player.startDash();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.L) && player.touchingWall)
                        player.startGrab();
                }

                //if player is pounding
                if(player.currentState == Blobb.State.POUNDING) {

                }


                //if player is splatting
                if(player.currentState == Blobb.State.SPLATTING) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                        player.buttBounce();
                }

                //if player is sliding
                if(player.currentState == Blobb.State.SLIDING) {
                    if (Gdx.input.isKeyPressed(Input.Keys.A))
                        player.moveLeftSlide();
                    if (Gdx.input.isKeyPressed(Input.Keys.D))
                        player.moveRightSlide();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && (player.canFloat))
                        player.startFloat();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.J) && (player.canDash))
                        player.startDash();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.L))
                        player.startGrab();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                        player.wallJump();
                }

                //if player is grabbing
                if(player.currentState == Blobb.State.GRABBING) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.K) && (player.canFloat))
                        player.startFloat();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.J) && (player.canDash))
                        player.startDash();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.H))
                        player.startPound();
                    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                        player.wallJump();
                    if (!Gdx.input.isKeyPressed(Input.Keys.L))
                        player.startSlide();
                }
            }

            //pause and unpause functionality
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                if (!setToPause)
                    pause();

                else if (!setToResume)
                    resume();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.M))
                if (music.getVolume() != 0)
                    music.setVolume(0);
                else
                    music.setVolume(0.3f);
        }
    }

    private void update(float dt) {
        //handle user input first
        handleInput(dt);

        if (setToResume || !setToPause) {

            //takes 1 step in the physics simulation(60 times per second)
            world.step(1 / 60f, 6, 2);

            player.update(dt);

            //attach the gamecam to the world
            if (player.currentState != Blobb.State.DYING) {
                gamecam.position.x = gamePort.getWorldWidth()/2;
                gamecam.position.y = gamePort.getWorldHeight()/2;
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
        //b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.setShader(game.shaderProgram);


        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();

        //Set the batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (player.isDead()) {
            RetroGame.lives--;
            if (RetroGame.lives < 0) {
                game.setScreen(new GameOverScreen(game));
                dispose();
            }

            else {
                player.deadStatus = false;
                game.setScreen(new PlayScreen(game,currentMap));
            }
        }

        //if a floor is cleared, set the screen to be the new floor (lacks transition)
        if (player.isFloorCleared()) {
            moduleNum = rand.nextInt(levelModules.size());
            game.setScreen(new PlayScreen(game, levelModules.remove(moduleNum)));
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
        if(player.currentState == Blobb.State.DYING && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }

    private boolean floorClear() {
        //if player has reached the top of the floor, the module is complete
        if ((player.getY() > RetroGame.V_HEIGHT/RetroGame.PPM) &&
                ((player.currentState == Blobb.State.STANDING) || (player.currentState == Blobb.State.RUNNING))) {
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
