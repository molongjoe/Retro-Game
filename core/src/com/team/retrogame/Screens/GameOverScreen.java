package com.team.retrogame.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.team.retrogame.RetroGame;

import java.util.LinkedList;
import java.util.Random;

/**
 * created by Blobb Team on 09/13/18.
 */

//GameOverScreen replaces PlayScreen when RetroGame dies
public class GameOverScreen implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;

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

    public GameOverScreen(Game game) {
        this.game = game;
        viewport = new FitViewport(RetroGame.V_WIDTH, RetroGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((RetroGame) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("DOMINIC PLEASE PASS US", font);
        Label playAgainLabel = new Label("Press Space to Play Again!", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //upon Mouse click, set the screen to a new PlayScreen and close the gameoverscreen
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            RetroGame.lives = 5;

            moduleNum = rand.nextInt(levelModules.size());
            game.setScreen(new PlayScreen((RetroGame) game,levelModules.remove(moduleNum)));
            dispose();
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
