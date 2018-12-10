package com.team.retrogame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.team.retrogame.RetroGame;
import com.team.retrogame.Sprites.Blobb;

import java.sql.Blob;

/**
 * created by Ben Mankin on 09/13/18.
 */

/*
WorldContactListener serves to handle all possible collisions between objects in the game
and behave appropriately
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        //A Collision has occured. Identify the proponents
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        //Do a bitwise operation to determine the unique collision (more in the RetroGame class)
        int cdef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        /*
         This block of code is to determine what two objects collided, which object
         is fixture A, and which object is fixture B.
         If the first object is fixture A, then the second is assumed to be B, and vice versa.
         The resulting method is then acted upon the appropriate object.
         */
        switch (cdef) {
            case RetroGame.BLOBB_HEAD_BIT | RetroGame.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_HEAD_BIT)
                    ((Blobb) fixA.getUserData()).headOnCeiling = true;
                else
                    ((Blobb) fixB.getUserData()).headOnCeiling = true;
                break;
            case RetroGame.BLOBB_FEET_BIT | RetroGame.GROUND_BIT:
            case RetroGame.BLOBB_FEET_BIT | RetroGame.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_FEET_BIT)
                    ((Blobb) fixA.getUserData()).feetOnGround = true;
                else
                    ((Blobb) fixB.getUserData()).feetOnGround = true;
                break;
            case RetroGame.BLOBB_LEFT_BIT | RetroGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_LEFT_BIT)
                    ((Blobb) fixA.getUserData()).onLeftWall = true;
                else
                    ((Blobb) fixB.getUserData()).onLeftWall = true;
                    break;

            case RetroGame.BLOBB_RIGHT_BIT | RetroGame.GROUND_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_RIGHT_BIT)
                    ((Blobb) fixA.getUserData()).onRightWall = true;
                else
                    ((Blobb) fixB.getUserData()).onRightWall = true;
                break;

            //Blobb collides with spike
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_GENERAL_BIT)
                    ((Blobb) fixA.getUserData()).die();
                else
                    ((Blobb) fixB.getUserData()).die();
                break;
            //Blobb collides with trampoline
            case RetroGame.BLOBB_FEET_BIT | RetroGame.TRAMPOLINE_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_FEET_BIT)
                    ((Blobb) fixA.getUserData()).trampolineBounce();
                else
                    ((Blobb) fixB.getUserData()).trampolineBounce();
                break;



            //Blobb collides with goal
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.GOAL_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_GENERAL_BIT)
                    ((Blobb) fixA.getUserData()).floorClear = true;
                else
                    ((Blobb) fixB.getUserData()).floorClear = true;
                break;


            //Blobb collides from above with a platform that is deactivated. Turn it on. Otherwise, leave it off (do nothing)
            case RetroGame.BLOBB_FEET_BIT | RetroGame.PLATFORM_OFF_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_FEET_BIT) {
                    if (fixA.getBody().getLinearVelocity().y <= 0) {
                        Filter filter = new Filter();
                        filter.maskBits = RetroGame.PLATFORM_ON_BIT;
                        fixB.setFilterData(filter);
                    }
                }
                else {
                    if (fixB.getBody().getLinearVelocity().y <= 0) {
                        Filter filter = new Filter();
                        filter.maskBits = RetroGame.PLATFORM_ON_BIT;
                        fixA.setFilterData(filter);
                    }
                }
                break;


            }

            /*
            //Blobb collides with crumble platform
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.CRUMBLE_PLATFORM_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.CRUMBLE_PLATFORM_BIT)
                    ((Platform) fixA.getUserData()).startCrumbling();
                else
                    ((Platform) fixB.getUserData()).startCrumbling();
                break;
            }*/

        }


    @Override
    public void endContact(Contact contact) {
        //A Collision has finished. Identify the proponents
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        //Do a bitwise operation to determine the unique collision ending (more in the RetroGame class)
        int cdef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        /*
         This block of code is to determine what two objects have finished colliding, which object
         is fixture A, and which object is fixture B.
         If the first object is fixture A, then the second is assumed to be B, and vice versa.
         The resulting method is then acted upon the appropriate object.
         */
        switch (cdef) {
            //Blobb has ended collision with the ground
            case RetroGame.BLOBB_FEET_BIT | RetroGame.GROUND_BIT:
            case RetroGame.BLOBB_FEET_BIT | RetroGame.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_FEET_BIT)
                    ((Blobb) fixA.getUserData()).feetOnGround = false;
                else
                    ((Blobb) fixB.getUserData()).feetOnGround = false;
                break;
            //Blobb has ended collision with the ground
            case RetroGame.BLOBB_HEAD_BIT | RetroGame.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_HEAD_BIT)
                    ((Blobb) fixA.getUserData()).headOnCeiling = false;
                else
                    ((Blobb) fixB.getUserData()).headOnCeiling = false;
                break;
            //Blobb has ended collision with the ground
            case RetroGame.BLOBB_LEFT_BIT | RetroGame.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_LEFT_BIT)
                    ((Blobb) fixA.getUserData()).onLeftWall = false;
                else
                    ((Blobb) fixB.getUserData()).onLeftWall = false;
                break;
            //Blobb has ended collision with the ground
            case RetroGame.BLOBB_RIGHT_BIT | RetroGame.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_RIGHT_BIT)
                    ((Blobb) fixA.getUserData()).onRightWall = false;
                else
                    ((Blobb) fixB.getUserData()).onRightWall = false;
                break;
            //Blobb has ended collision with a platform that was activated. Turn off the one way platform
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.PLATFORM_ON_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_GENERAL_BIT) {
                    Filter filter = new Filter();
                    filter.maskBits = RetroGame.PLATFORM_OFF_BIT;
                    fixB.setFilterData(filter);
                }
                else {
                    Filter filter = new Filter();
                    filter.maskBits = RetroGame.PLATFORM_OFF_BIT;
                    fixA.setFilterData(filter);
                }


            /*
            //Blobb has ended collision with a one way platform
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.ONE_WAY_PLATFORM_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.ONE_WAY_PLATFORM_BIT)
                    ((Platform) fixA.getUserData()).checkStandable();
                else
                    ((Platform) fixB.getUserData()).checkStandable();
                break;
            */

            /*
            //Blobb has ended collision with a crumble platform
            case RetroGame.BLOBB_GENERAL_BIT | RetroGame.CRUMBLE_PLATFORM_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.CRUMBLE_PLATFORM_BIT)
                    ((Platform) fixA.getUserData()).crumble();
                else
                    ((Platform) fixB.getUserData()).crumble();
                break;
            */
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}


