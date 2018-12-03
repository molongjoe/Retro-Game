package com.team.retrogame.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
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
            //Blobb collides with Wall
            case RetroGame.BLOBB_BIT | RetroGame.WALL_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).touchingWall = true;
                else
                    ((Blobb) fixB.getUserData()).touchingWall = true;
                break;
            //Blobb collides with spike
            case RetroGame.BLOBB_BIT | RetroGame.SPIKE_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).die();
                else
                    ((Blobb) fixB.getUserData()).die();
                break;
            //Blobb collides with trampoline
            case RetroGame.BLOBB_BIT | RetroGame.TRAMPOLINE_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).bounce();
                else
                    ((Blobb) fixB.getUserData()).bounce();
                break;
            }

            /*
            //Blobb collides with goal
            case RetroGame.BLOBB_BIT | RetroGame.GOAL_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).transition();
                else
                    ((Blobb) fixB.getUserData()).transition();
                break;
            }*/

            /*
            //Blobb collides with one way platform
            case RetroGame.BLOBB_BIT | RetroGame.ONE_WAY_PLATFORM_BIT:
                if (fixA.getFilterData().categoryBits == RetroGame.ONE_WAY_PLATFORM_BIT)
                    ((Platform) fixA.getUserData()).checkStandable();
                else
                    ((Platform) fixB.getUserData()).checkStandable();
                break;
            }*/

            /*
            //Blobb collides with crumble platform
            case RetroGame.BLOBB_BIT | RetroGame.CRUMBLE_PLATFORM_BIT:
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
            //Blobb has ended collision with a wall
            case RetroGame.BLOBB_BIT | RetroGame.WALL_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).touchingWall = false;
                else
                    ((Blobb) fixB.getUserData()).touchingWall = false;
                break;

            /*
            //Blobb has ended collision with the ground
            case RetroGame.BLOBB_BIT | RetroGame.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.BLOBB_BIT)
                    ((Blobb) fixA.getUserData()).touchingGround = false;
                else
                    ((Blobb) fixB.getUserData()).touchingGround = false;
                break;
            */

            /*
            //Blobb has ended collision with a one way platform
            case RetroGame.BLOBB_BIT | RetroGame.ONE_WAY_PLATFORM_BIT:
                if(fixA.getFilterData().categoryBits == RetroGame.ONE_WAY_PLATFORM_BIT)
                    ((Platform) fixA.getUserData()).checkStandable();
                else
                    ((Platform) fixB.getUserData()).checkStandable();
                break;
            */

            /*
            //Blobb has ended collision with a crumble platform
            case RetroGame.BLOBB_BIT | RetroGame.CRUMBLE_PLATFORM_BIT:
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


