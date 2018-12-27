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

//As written, this functionality does nothing. In the future, a label could be added
    //to this code to add the word 'paused' or bring up menu choices
public class PauseScreen implements Disposable{

    public Stage stage;
    private Viewport viewport;

    public PauseScreen(SpriteBatch sb){
        viewport = new FitViewport(RetroGame.V_WIDTH, RetroGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
    }

    public void update(float dt) {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
