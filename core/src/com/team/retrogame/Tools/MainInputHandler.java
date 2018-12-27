package com.team.retrogame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.team.retrogame.Screens.PlayScreen;
import com.team.retrogame.Sprites.Blobb;

public class MainInputHandler implements InputProcessor {
    
    PlayScreen screen;
    Blobb blobb;

    public enum tapState {TAPPED, RELEASED}

    public tapState currentTapState = tapState.RELEASED;
    public tapState previousTapState = tapState.RELEASED;

    public float tappedTime = 0;
    public float releasedTime = 0;

    public boolean longPress = false;


    public MainInputHandler(PlayScreen activeScreen, Blobb activeBlobb) {
        this.screen = activeScreen;
        this.blobb = activeBlobb;
    }

    //Whenever a key is pressed, this method is called
    @Override
    public boolean keyDown(int keycode) {
        //if Blobb isn't dead, these inputs are valid
        if(blobb.currentState != Blobb.State.DYING) {
            //if game isn't paused and blobb isn't performing a special action, these inputs are valid
            if(screen.setToResume || !screen.setToPause) {
                //If player is pressing space, check the tap state
                if (keycode == Input.Keys.SPACE) {
                    tapCheck();
                }
                //if blobb is standing or running
                if(blobb.currentState == Blobb.State.STANDING || blobb.currentState == Blobb.State.RUNNING) {
                    if (keycode == Input.Keys.SPACE) {
                        blobb.startGroundJump();
                    }
                }

                //if blobb is jumping, falling, or butt bouncing
                if(blobb.currentState == Blobb.State.JUMPING || blobb.currentState == Blobb.State.FALLING || blobb.currentState == Blobb.State.BUTT_BOUNCING) {
                    if (keycode == Input.Keys.SPACE) {
                        blobb.startPound();
                    }
                }

                //if blobb is dashing
                if(blobb.currentState == Blobb.State.DASHING) {

                }

                //if blobb is floating
                if(blobb.currentState == Blobb.State.FLOATING) {

                }

                //if blobb is pounding
                if(blobb.currentState == Blobb.State.POUNDING) {
                    if (keycode == Input.Keys.SPACE) {
                        if (blobb.canDash) {
                            blobb.startDash();
                        }
                    }
                }


                //if blobb is splatting
                if(blobb.currentState == Blobb.State.SPLATTING) {
                    if (keycode == Input.Keys.SPACE)
                        blobb.startButtBounce();
                }

                //if blobb is sliding
                if(blobb.currentState == Blobb.State.SLIDING) {
                    if (keycode == Input.Keys.SPACE)
                        blobb.startGrab();
                }

                //if blobb is grabbing
                if(blobb.currentState == Blobb.State.GRABBING) {

                }
            }

            //pause and unpause functionality
            if (keycode == Input.Keys.P) {
                if (!screen.setToPause)
                    screen.pause();

                else if (!screen.setToResume)
                    screen.resume();
            }

            //mute and unmute the in-game music
            if (keycode == Input.Keys.M) {
                if (screen.music.getVolume() != 0)
                    screen.music.setVolume(0);
                else
                    screen.music.setVolume(0.3f);
            }
        }

        //switch level with tab
        if (keycode == Input.Keys.TAB) {
            blobb.floorClear = true;
        }
        return false;
    }

    //whenever a key is released, this method is called
    @Override
    public boolean keyUp(int keycode) {
        //if Blobb isn't dead, these inputs are valid
        if(blobb.currentState != Blobb.State.DYING) {
            //if game isn't paused and blobb isn't performing a special action, these inputs are valid
            if(screen.setToResume || !screen.setToPause) {
                //If player is releasing space, check the tap state
                if (keycode == Input.Keys.SPACE) {
                    tapCheck();
                }
                //if blobb is standing or running
                if(blobb.currentState == Blobb.State.STANDING || blobb.currentState == Blobb.State.RUNNING) {
                    if (keycode == Input.Keys.SPACE) {

                    }
                }

                //if blobb is jumping or falling
                if(blobb.currentState == Blobb.State.JUMPING || blobb.currentState == Blobb.State.FALLING) {
                    if (keycode == Input.Keys.SPACE) {
                        blobb.startFall();
                    }
                }

                //if blobb is dashing
                if(blobb.currentState == Blobb.State.DASHING) {
                    if (keycode == Input.Keys.SPACE) {

                    }

                }

                //if blobb is floating
                if(blobb.currentState == Blobb.State.FLOATING) {
                    if (keycode == Input.Keys.SPACE) {

                    }
                }

                //if blobb is pounding
                if(blobb.currentState == Blobb.State.POUNDING) {
                    if (keycode == Input.Keys.SPACE) {

                    }
                }

                //if blobb is splatting
                if(blobb.currentState == Blobb.State.SPLATTING) {

                }

                //if blobb is sliding
                if(blobb.currentState == Blobb.State.SLIDING) {
                    if (keycode == Input.Keys.SPACE) {
                        blobb.wallJump();
                    }
                }

                //if blobb is grabbing
                if(blobb.currentState == Blobb.State.GRABBING) {
                    if (keycode == Input.Keys.SPACE) {
                        blobb.wallJump();
                    }
                }
            }
        }
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

    //if Blobb is in a TAPPED state, increase the tapped time
    public void incrementTappedTime(float dt) {
        tappedTime += dt;
    }

    //if Blobb is in a RELEASED state, increase the released time
    public void incrementReleasedTime(float dt) {
        releasedTime += dt;
    }

    //This method handles the tap state functionality
    public void tapCheck() {
        //debugging line
        System.out.println("\nTapped Time: " + screen.getInputHandler().tappedTime
                + "\nReleased Time: " + screen.getInputHandler().releasedTime);

        previousTapState = currentTapState;

        //decide the current tap state based on the previous one
        switch(previousTapState) {
            case RELEASED:
                currentTapState = tapState.TAPPED;
                if (isDoubleTap())
                    System.out.println("Double Tapped");
                break;
            case TAPPED:
                currentTapState = tapState.RELEASED;
                break;

            default:
                currentTapState = tapState.RELEASED;
        }
        tapReset();
    }

    //if the time between a tap and release is short enough
    public boolean isDoubleTap() {
        return (tappedTime + releasedTime < 0.1f);
    }

    public void tapReset() {
        longPress = false;
        tappedTime = 0;
        releasedTime = 0;
    }
}
