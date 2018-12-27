package com.team.retrogame.Scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.team.retrogame.RetroGame;

/**
 * created by Blobb Team on 09/13/18.
 */

public class Hud implements Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    public Hud(SpriteBatch sb){
        //setup the HUD viewport using a new camera separate from the gamecam
        //define the stage using that viewport and the games spritebatch
        viewport = new FitViewport(RetroGame.V_WIDTH, RetroGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
