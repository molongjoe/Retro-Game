package com.team.retrogame.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.team.retrogame.RetroGame;
import com.team.retrogame.Screens.PlayScreen;

/**
 * created by Ben Mankin on 09/13/18.
 */

//Sprite class for Blobb. Draws physics body, sprites, and handles behavior
public class Blobb extends Sprite {

    String[] running = {"Running-1", "Running-2","Running-3","Running-4","Running-5",
            "Running-6","Running-7", "Running-8"};
    String[] jumping = {"Jumping-1","Jumping-2","Jumping-3"};
    String[] splatting = {"Splat-1", "Splat-2", "Splat-3","Splat-4"};
    String[] pounding = {"Pound-1", "Pound-2", "Pound-3"};
    String[] grabbing = {"Grab-1", "Grab-2", "Grab-3", "Grab-4", "Grab-5", "Grab-6"};

    //All the states Blobb can be in
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD, SPLATTING, POUNDING
    }

    //log current state and previous state
    public State currentState;
    public State previousState;

    private World world;
    public Body b2Body;

    //All sprite Texture Regions and Animations for Blobb
    private TextureRegion BlobbStand;
    private Animation<TextureRegion> BlobbRun;
    private Animation<TextureRegion> BlobbJump;
    private Animation<TextureRegion> BlobbSplat;
    private Animation<TextureRegion> BlobbPound;
    private Animation<TextureRegion> BlobbGround;
    private Animation<TextureRegion> BlobbFall;
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

        //Make Texture Regions representing different frames that we will
        //use for animation later
        Array<TextureRegion> running_frames = new Array<TextureRegion>();
        Array<TextureRegion> jumping_frames = new Array<TextureRegion>();
        Array<TextureRegion> splatting_frames = new Array<TextureRegion>();
        Array<TextureRegion> pounding_frames = new Array<TextureRegion>();
        Array<TextureRegion> grabbing_frames = new Array<TextureRegion>();
        Array<TextureRegion> falling_frames = new Array<TextureRegion>();

        //Add the different running sprites to our running frames
        for(int i = 0; i <= 7; i++) {
            running_frames.add(new TextureRegion(screen.getAtlas().findRegion(running[i]), 0, 0, 16, 16));
        }

        //Add the different jumping sprites to our jumping frames
        for(int i = 0; i <= 2; i++) {
            jumping_frames.add(new TextureRegion(screen.getAtlas().findRegion(jumping[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 3; i++){
            splatting_frames.add(new TextureRegion(screen.getAtlas().findRegion(splatting[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 2; i++){
            pounding_frames.add(new TextureRegion(screen.getAtlas().findRegion(pounding[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 5; i++){
            grabbing_frames.add(new TextureRegion(screen.getAtlas().findRegion(grabbing[i]), 0, 0, 16, 16));
        }

        for(int i = 2; i >= 0; i--){
            falling_frames.add(new TextureRegion(screen.getAtlas().findRegion(jumping[i]), 0, 0, 16, 16));
        }


        //Create the animation of Running
        BlobbRun = new Animation<TextureRegion>(0.1f, running_frames);
        running_frames.clear();

        //Create the animation of Jumping
        BlobbJump = new Animation<TextureRegion>(0.1f, jumping_frames);
        jumping_frames.clear();

        //Create the animation of Splatting
        BlobbSplat = new Animation<TextureRegion>(0.1f, splatting_frames);

        //Create the animation of Pounding
        BlobbPound = new Animation<TextureRegion>(0.1f, pounding_frames);

        //Create the animation of Grabbing
        BlobbGround = new Animation<TextureRegion>(0.1f, grabbing_frames);

        //Create the animation of Falling
        BlobbFall = new Animation<TextureRegion>(0.1f, falling_frames);

        //create texture region for Blobb standing
        BlobbStand = new TextureRegion(screen.getAtlas().findRegion("Running-1"), 0, 0, 16, 16);

        //create dead Blobb texture region
        BlobbDead = new TextureRegion(screen.getAtlas().findRegion("Running-1"), 96, 0, 16, 16);

        //define Blobb in Box2d
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
                region = BlobbJump.getKeyFrame(stateTimer,false);
                break;
            case RUNNING:
                region = BlobbRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                region = BlobbFall.getKeyFrame(stateTimer, false);
            case STANDING:
            case SPLATTING:
                region = BlobbSplat.getKeyFrame(stateTimer, false);
            case POUNDING:
                region = BlobbPound.getKeyFrame(stateTimer, false);

            default:
                region = BlobbStand;
                break;
        }

        //if Blobb is running left and the texture isnt facing left... flip it.
        if ((b2Body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }

        //if Blobb is running right and the texture isnt facing right... flip it.
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
        //else if((b2Body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            //return State.JUMPING;
            //if negative in Y-Axis Blobb is falling
        else if (b2Body.getLinearVelocity().y < 0 )
            return State.FALLING;
            //if Blobb is positive or negative in the X axis he is running
        else if (b2Body.getLinearVelocity().x != 0)
            return State.RUNNING;
        //else if (b2Body.getLinearVelocity().y < -3)
            //return State.POUNDING;
            //if none of these return then he must be standing
        else
            return State.STANDING;

    }

    public float getStateTimer() {
        return stateTimer;
    }

    //Define Blobb in the Box2D world
    private void defineBlobb() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(64 / RetroGame.PPM, 16 / RetroGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        //Box2D fixture settings
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5 / RetroGame.PPM, 5 / RetroGame.PPM);


        //Set what Blobb can collide with
        fdef.filter.categoryBits = RetroGame.BLOBB_BIT;
        fdef.filter.maskBits = RetroGame.GROUND_BIT;

        fdef.shape = shape;
        //fdef.friction = (float)0.5;
        b2Body.createFixture(fdef).setUserData(this);

        //Give Blobb an edge to serve as his feet
        FixtureDef fdef2 = new FixtureDef();
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-2 / RetroGame.PPM, -6 / RetroGame.PPM), new Vector2(2 / RetroGame.PPM, -6 / RetroGame.PPM));
        fdef2.shape = feet;
        fdef2.isSensor = true;
        b2Body.createFixture(fdef2).setUserData("feet");
    }

    //Draw Blobb's physics body in the world
    public void draw(Batch batch){
        super.draw(batch);
    }


}
