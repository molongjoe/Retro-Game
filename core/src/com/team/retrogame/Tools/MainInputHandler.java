package com.team.retrogame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.team.retrogame.Screens.PlayScreen;
import com.team.retrogame.Sprites.Blobb;

public class MainInputHandler implements InputProcessor {
    
    PlayScreen screen;
    Blobb blobb;
    
    public MainInputHandler(PlayScreen activeScreen, Blobb activeBlobb) {
        this.screen = activeScreen;
        this.blobb = activeBlobb;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        //if Blobb isn't dead, these inputs are valid
        if(blobb.currentState != Blobb.State.DYING) {
            //if game isn't paused and blobb isn't performing a special action, these inputs are valid
            if(screen.setToResume || !screen.setToPause) {
                //if blobb is standing or running
                if(blobb.currentState == Blobb.State.STANDING || blobb.currentState == Blobb.State.RUNNING) {
                    blobb.canFloat = true;
                    blobb.canDash = true;
                    /*
                    if ((keycode == Input.Keys.A) || (keycode == Input.Keys.D))
                        blobb.startGroundMove();
                        */
                    if (keycode == Input.Keys.SPACE)
                        blobb.startGroundJump();
                    if (keycode == Input.Keys.K)
                        blobb.startFloat();
                }

                //if blobb is jumping or falling
                if(blobb.currentState == Blobb.State.JUMPING || blobb.currentState == Blobb.State.FALLING) {
                    /*
                    if ((keycode == Input.Keys.A) || (keycode == Input.Keys.D))
                        blobb.startAirMove();
                        */
                    if ((keycode == Input.Keys.K) && (blobb.canFloat))
                        blobb.startFloat();
                    if ((keycode == Input.Keys.J) && (blobb.canDash))
                        blobb.startDash();
                    if (keycode == Input.Keys.H)
                        blobb.startPound();
                    if ((keycode == Input.Keys.L) && (blobb.touchingWall))
                        blobb.startGrab();
                }

                //if blobb is dashing
                if(blobb.currentState == Blobb.State.DASHING) {
                    if ((keycode == Input.Keys.K) && (blobb.canFloat))
                        blobb.startFloat();
                    if (keycode == Input.Keys.H)
                        blobb.startPound();
                    if ((keycode == Input.Keys.L) && blobb.touchingWall)
                        blobb.startGrab();
                }

                //if blobb is floating
                if(blobb.currentState == Blobb.State.FLOATING) {
                    /*
                    if ((keycode == Input.Keys.A) || (keycode == Input.Keys.D))
                        blobb.startFloatMove();
                        */
                    if (keycode == Input.Keys.H)
                        blobb.startPound();
                    if ((keycode == Input.Keys.J) && (blobb.canDash))
                        blobb.startDash();
                    if ((keycode == Input.Keys.L) && blobb.touchingWall)
                        blobb.startGrab();
                }

                //if blobb is pounding
                if(blobb.currentState == Blobb.State.POUNDING) {

                }


                //if blobb is splatting
                if(blobb.currentState == Blobb.State.SPLATTING) {
                    if (keycode == Input.Keys.SPACE)
                        blobb.startButtBounce();
                }

                //if blobb is sliding
                if(blobb.currentState == Blobb.State.SLIDING) {
                    /*
                    if ((keycode == Input.Keys.A) || (keycode == Input.Keys.D))
                        blobb.startSlideMove();
                        */
                    if ((keycode == Input.Keys.K) && (blobb.canFloat))
                        blobb.startFloat();
                    if ((keycode == Input.Keys.J) && (blobb.canDash))
                        blobb.startDash();
                    if (keycode == Input.Keys.H)
                        blobb.startPound();
                    if (keycode == Input.Keys.L)
                        blobb.startGrab();
                    if (keycode == Input.Keys.SPACE)
                        blobb.wallJump();
                }

                //if blobb is grabbing
                if(blobb.currentState == Blobb.State.GRABBING) {
                    if ((keycode == Input.Keys.K) && (blobb.canFloat))
                        blobb.startFloat();
                    if ((keycode == Input.Keys.J) && (blobb.canDash))
                        blobb.startDash();
                    if (keycode == Input.Keys.H)
                        blobb.startPound();
                    if (keycode == Input.Keys.SPACE)
                        blobb.wallJump();
                }
            }

            //pause and unpause functionality
            if (keycode == Input.Keys.P) {
                if (!screen.setToPause)
                    screen.pause();

                else if (!screen.setToResume)
                    screen.resume();
            }

            if (keycode == Input.Keys.M) {
                if (screen.music.getVolume() != 0)
                    screen.music.setVolume(0);
                else
                    screen.music.setVolume(0.3f);
            }
        }

        if (keycode == Input.Keys.TAB) {
            blobb.floorClear = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
}
