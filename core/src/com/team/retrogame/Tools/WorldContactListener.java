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
       

    }

    //unused methods currently
    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}


