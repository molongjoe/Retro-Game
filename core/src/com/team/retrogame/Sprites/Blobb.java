package com.team.retrogame.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.team.retrogame.RetroGame;
import com.team.retrogame.Screens.PlayScreen;

/**
 * created by Ben Mankin on 09/13/18.
 */

//Sprite class for RetroGame. Draws physics body, sprites, and handles behavior
public class Blobb extends Sprite {

    //All the states RetroGame can be in
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD}

    //log current state and previous state
    public State currentState;
    private State previousState;

    private World world;
    public Body b2Body;

    //All sprite Texture Regions and Animations for RetroGame
    private TextureRegion BlobbStand;
    private Animation<TextureRegion> BlobbRun;
    private TextureRegion BlobbJump;
    private TextureRegion BlobbDead;

    //behavioral checks
    private float stateTimer;
    private boolean runningRight;

    private PlayScreen screen;

    public Blobb(PlayScreen screen) {
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get run animation frames and add them to BlobbRun Animation
        for(int i = 1; i<8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Player Running"), 8, 8, 16, 16));
        BlobbRun = new Animation<TextureRegion>(0.1f, frames);

        frames.clear();

        //get jump animation frames and add them to BlobbJump Animation
        BlobbJump = new TextureRegion(screen.getAtlas().findRegion("Player Running"), 8, 8, 16, 16);

        //create texture region for RetroGame standing
        BlobbStand = new TextureRegion(screen.getAtlas().findRegion("Player Running"), 8, 8, 16, 16);

        //create dead RetroGame texture region
        BlobbDead = new TextureRegion(screen.getAtlas().findRegion("Player Running"), 96, 0, 16, 16);

        //define RetroGame in Box2d
        defineBlobb();

        //set initial values for Blobbs location, width and height. And initial frame as BlobbStand.
        setBounds(0, 0, 16 / RetroGame.PPM, 16 / RetroGame.PPM);
        setRegion(BlobbStand);
    }

    public void update(float dt) {
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }


    private TextureRegion getFrame(float dt) {
        //get Blobbs current state. ie. jumping, running, standing...
        currentState = getState();

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState) {
            case DEAD:
                region = BlobbDead;
                break;
            case JUMPING:
                region = BlobbJump;
                break;
            case RUNNING:
                region = BlobbRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = BlobbStand;
                break;
        }

        //if RetroGame is running left and the texture isnt facing left... flip it.
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }

        //if RetroGame is running right and the texture isnt facing right... flip it.
        else if((b2Body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and the timer needs to be reset
        stateTimer = currentState == previousState ? stateTimer + dt : 0;

        //update previous state
        previousState = currentState;

        //return the final adjusted frame
        return region;

    }

    private State getState(){
        //Test to Box2D for velocity on the X and Y-Axis
        if (b2Body.getLinearVelocity().y > 0 || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if((b2Body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
            //if negative in Y-Axis RetroGame is falling
        else if (b2Body.getLinearVelocity().y < 0)
            return State.FALLING;
            //if RetroGame is positive or negative in the X axis he is running
        else if (b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
            //if none of these return then he must be standing
        else
            return State.STANDING;

    }

    public float getStateTimer() {
        return stateTimer;
    }

    //Define RetroGame in the Box2D world
    private void defineBlobb() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / RetroGame.PPM, 32 / RetroGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        //Box2D fixture settings
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / RetroGame.PPM);

        //Set what RetroGame can collide with
        fdef.filter.categoryBits = RetroGame.BLOBB_BIT;
        fdef.filter.maskBits = RetroGame.GROUND_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        //Give RetroGame an edge to serve as his feet
        FixtureDef fdef2 = new FixtureDef();
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / RetroGame.PPM, -6 / RetroGame.PPM), new Vector2(2 / RetroGame.PPM, -6 / RetroGame.PPM));
        fdef2.shape = feet;
        fdef2.isSensor = true;
        b2Body.createFixture(fdef2).setUserData("feet");
    }

    //Draw RetroGame's physics body in the world
    public void draw(Batch batch){
        super.draw(batch);
    }


}
