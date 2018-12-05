package com.team.retrogame.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    String[] splatting = {"Splat-1", "Splat-2", "Splat-3","Splat-4", "Splat-3", "Splat-2"};
    String[] pounding = {"Pound-1", "Pound-2", "Pound-3"};
    String[] grabbing = {"Grab-1", "Grab-2", "Grab-3", "Grab-4", "Grab-5", "Grab-6"};
    String[] floating = {"Floating-1","Floating-2","Floating-3","Floating-4"};

    //All the states Blobb can be in
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD, SPLATTING, POUNDING, FLOATING, GRABBING,
        SLIDING_S /* State used to develop sliding behavior */}

    //log current state and previous state
    public State currentState;
    public State previousState;

    public World world;
    public PlayScreen screen;
    public Body b2Body;

    //All sprite Texture Regions and Animations for Blobb
    private TextureRegion BlobbStand;
    private Animation<TextureRegion> BlobbRun;
    private Animation<TextureRegion> BlobbJump;
    private Animation<TextureRegion> BlobbSplat;
    private Animation<TextureRegion> BlobbPound;
    private Animation<TextureRegion> BlobbGrab;
    private Animation<TextureRegion> BlobbFall;
    private Animation<TextureRegion> BlobbFloat;
    private TextureRegion BlobbDead;

    //behavioral checks
    private float stateTimer;
    private boolean runningRight;
    public boolean touchingWall;
    public boolean touchingGround;

    public boolean setToPound = false;
    public boolean setToFloat = false;
    public boolean setToSplat = false;
    public boolean setToGrab = false;
    public boolean setToSlide = false;
    public boolean setToButtBounce = false;

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
        Array<TextureRegion> floating_frames = new Array<TextureRegion>();

        //Add the different running sprites to our running frames
        for(int i = 0; i <= 7; i++) {
            running_frames.add(new TextureRegion(screen.getAtlas().findRegion(running[i]), 0, 0, 16, 16));
        }

        //Add the different jumping sprites to our jumping frames
        for(int i = 0; i <= 2; i++) {
            jumping_frames.add(new TextureRegion(screen.getAtlas().findRegion(jumping[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 5; i++){
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

        for(int i = 0; i <= 3; i++){
            floating_frames.add(new TextureRegion(screen.getAtlas().findRegion(floating[i]), 0, 0, 16, 16));
        }


        //Create the animation of Running
        BlobbRun = new Animation<TextureRegion>(0.1f, running_frames);
        running_frames.clear();

        //Create the animation of Jumping
        BlobbJump = new Animation<TextureRegion>(0.1f, jumping_frames);
        jumping_frames.clear();

        //Create the animation of Splatting
        BlobbSplat = new Animation<TextureRegion>(0.05f, splatting_frames);
        splatting_frames.clear();

        //Create the animation of Pounding
        BlobbPound = new Animation<TextureRegion>(0.08f, pounding_frames);
        pounding_frames.clear();

        //Create the animation of Grabbing
        BlobbGrab = new Animation<TextureRegion>(0.1f, grabbing_frames);
        grabbing_frames.clear();

        //Create the animation of Falling
        BlobbFall = new Animation<TextureRegion>(0.1f, falling_frames);
        falling_frames.clear();

        //Create the animation of Floating
        BlobbFloat = new Animation<TextureRegion>(0.1f, floating_frames);
        floating_frames.clear();

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
        TextureRegion region = BlobbStand;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState) {
            case STANDING:
                break;
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
                break;
            case POUNDING:
                region = BlobbPound.getKeyFrame(stateTimer, false);
                poundCheck();
                break;
            case SPLATTING:
                region = BlobbSplat.getKeyFrame(stateTimer, false);
                break;
            case FLOATING:
                region = BlobbFloat.getKeyFrame(stateTimer, false);
                floatCheck();
                break;
            case GRABBING:
                region = BlobbGrab.getKeyFrame(stateTimer, false);
                grabCheck();
                break;
            case SLIDING_S:
                // handle sliding animation here
                slideCheck();
                break;

            default:
                region = BlobbStand;
                break;
        }

        //if Blobb is running left and the texture isn't facing left... flip it.
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

    //Define Blobb in the Box2D world
    private void defineBlobb() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(320 / RetroGame.PPM, 16 / RetroGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        //Box2D fixture settings
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5 / RetroGame.PPM, 5 / RetroGame.PPM);

        //Set what Blobb can collide with
        fdef.filter.categoryBits = RetroGame.BLOBB_BIT;
        fdef.filter.maskBits = RetroGame.GROUND_BIT |
                RetroGame.WALL_BIT |
                RetroGame.SPIKE_BIT |
                RetroGame.TRAMPOLINE_BIT |
                RetroGame.ONE_WAY_PLATFORM_BIT |
                RetroGame.CRUMBLE_PLATFORM_BIT |
                RetroGame.GOAL_BIT;
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

    private State getState(){

        //if not doing special action (pounding, floating, wall-jumping)
        if (!specialMovement()) {
            //if positive in Y-Axis or negative but was jumping, Blobb is jumping
            if (b2Body.getLinearVelocity().y > 0 || (b2Body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
                return State.JUMPING;
            //if negative in Y-Axis Blobb is falling
            else if (b2Body.getLinearVelocity().y < 0) // TODO: create setToFall flag to force falling even after no jump
                return State.FALLING;
                //if Blobb is positive or negative in the X axis he is running
            else if (b2Body.getLinearVelocity().x != 0) {
                //if (currentState == State.JUMPING)
                    //RetroGame.manager.get("audio/sounds/bubbleLand.mp3",Sound.class).play();
                return State.RUNNING;
            }
            //if none of these return then he must be standing.
            else {
                clearMovementFlags();
                //if (currentState == State.JUMPING)
                    //RetroGame.manager.get("audio/sounds/bubbleLand.mp3",Sound.class).play();
                return State.STANDING;
            }
        } // end if not special movement


        //if pounding
        else if (setToPound) {
            //if he has landed and the pound is finished, he is now splatting
            if (b2Body.getGravityScale() == 1 && b2Body.getLinearVelocity().y == 0) {
                setToPound = false;
                setToSplat = true;
                //RetroGame.manager.load("audio/sounds/bubbleSplat.mp3", Sound.class);
                return State.SPLATTING;
            }
            //otherwise still pounding
            else
                return State.POUNDING;
        }
        // TODO: State walljumping
        // TODO: setToWallJump

        else if (setToSplat) {
            if (BlobbSplat.isAnimationFinished(stateTimer)) {
                setToSplat = false;
                return State.STANDING;
            }
            else
                return State.SPLATTING;
        }

        else if (setToFloat) {
            return State.FLOATING;
        }

        else if (setToSlide) {
            return State.SLIDING_S;
        }

        else if (setToGrab) {
            return State.GRABBING;
        }

//        else if (b2Body.getLinearVelocity().y < 0) {
//            return State.SLIDING_S
//        }

        //default him standing
        else {
            System.err.println("Back to standing");
            clearMovementFlags();
            return State.STANDING;
        }
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void setCurrentState(State newState) {
        currentState = newState;
    }

    public void jump() {
        b2Body.applyLinearImpulse(new Vector2(0, 3.3f), b2Body.getWorldCenter(), true);
        //RetroGame.manager.load("audio/sounds/bubbleJump.mp3", Sound.class);
    }

    public void moveLeft() {
        b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveRight() {
        b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void wallJump() {
        // TODO: Jump Logic -- get direction facing and apply angular impulse in opposite direction
//        b2Body.applyAngularImpulse();
        jump();
    }

    public void bounce() {
        if (b2Body.getLinearVelocity().y < 0) {
            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);
            b2Body.applyLinearImpulse(new Vector2(0, 5), b2Body.getWorldCenter(), true);
        }
    }

    public void startPound() {
        setToPound = true;
        //RetroGame.manager.load("audio/sounds/bubblePound.mp3", Sound.class);
    }

    public void startSplat() {
        setToSplat = true;
    }

    public void poundCheck() {
        if (BlobbPound.isAnimationFinished(stateTimer)) {
            b2Body.setGravityScale(1);
            b2Body.applyLinearImpulse(new Vector2(0, -.3f), b2Body.getWorldCenter(), true);
        }
        else {
            b2Body.setLinearVelocity(0,0);
            b2Body.setGravityScale(0);
        }
    }

    public void startSlide() {
        System.err.println("startSlide() method called");
        setToSlide = true;
    }

    public void slideCheck() {
        if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) || !touchingWall) {
            setToSlide = false;
            System.err.println("slideCheck just failed");
            b2Body.setGravityScale(1);
        } else {
            b2Body.setLinearVelocity(0, -0.5f);
        }
    }

    public void startFloat() {
        setToFloat = true;
        //RetroGame.manager.load("audio/sounds/bubbleFloat.mp3", Sound.class);
    }

    public void floatCheck() {
        if (!Gdx.input.isKeyPressed(Input.Keys.S) || b2Body.getLinearVelocity().y == 0 || stateTimer > 3) {
            setToFloat = false;
            b2Body.setGravityScale(1);
            //if (stateTimer > 3)
                //RetroGame.manager.load("audio/sounds/bubblePop.mp3", Sound.class);

            //else
                //RetroGame.manager.load("audio/sounds/bubbleFloatEnd.mp3", Sound.class);
        }

        else {
            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);
            b2Body.setGravityScale(-1f);
        }
    }

    public void startGrab() {
        System.err.println("Starting grab");
        setToGrab = true;
    }

    public void grabCheck() {
        if (!Gdx.input.isKeyPressed(Input.Keys.D) || !touchingWall) {
            setToGrab = false;
            b2Body.setGravityScale(1);
            System.out.println("Ending Grab");
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            setToSlide = true;
            setToGrab = false;
        }

        else {
            b2Body.setLinearVelocity(0,0);
            b2Body.setGravityScale(0);
        }
    }

    /**
    TODO: Decide behavior. There's not much room to be better than a normal jump but worse than a trampoline.
     Currently written so jump height mirrors fall height
     */
    public void buttBounce() {
//        b2Body.applyLinearImpulse(new Vector2(0, 4f), b2Body.getWorldCenter(), true);
        float vy = (b2Body.getLinearVelocity().y * -1) + .5f;
        b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, vy);
    }

    public void clearMovementFlags() {
        setToPound = false;
        setToFloat = false;
        setToSplat = false;
        setToGrab = false;
        setToSlide = false;
        setToButtBounce = false;
    }

    //if no special movement is happening, return false. Otherwise true
    public boolean specialMovement() {
        return (setToFloat || setToPound || setToSplat || setToGrab || setToButtBounce);
    }

    public void die() {
        System.out.println("You just died you stupid idiot");
    }

    //Draw Blobb's physics body in the world
    public void draw(Batch batch){
        super.draw(batch);
    }


}
