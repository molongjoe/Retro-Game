package com.team.retrogame.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
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
    String[] dashing = {"Dashing-1", "Dashing-2", "Dashing-3"};
    String[] sliding = {"Sliding-1", "Sliding-2"};
    String[] dying = {"Dying-1","Dying-2","Dying-3","Dying-4","Dying-6","Dying-7","Dying-8","Dying-9","Dying-10"};

    //All the states Blobb can be in
    public enum State {FALLING, JUMPING, STANDING, RUNNING, SPLATTING, POUNDING, FLOATING, GRABBING,
        SLIDING, DASHING, DYING}

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
    private Animation<TextureRegion> BlobbSlide;
    private Animation<TextureRegion> BlobbDash;
    private Animation<TextureRegion> BlobbDie;

    //behavioral checks
    private float stateTimer;
    private float floatTimer;
    private boolean facingRight;
    public boolean touchingWall;
    public boolean feetOnGround;
    public boolean headOnCeiling;
    public boolean onLeftWall;
    public boolean onRightWall;
    public boolean canSlide; // TODO: Are we using this? Redundant and confusing since right now, every check for can slide manually checks all conditions.
    public boolean canFloat = true;
    public boolean canDash = true;
    public boolean initialFloat = true;
    public float buttBounceHeight = 0;

    public boolean setToJump = false;
    public boolean setToFall = false;
    public boolean setToPound = false;
    public boolean setToFloat = false;
    public boolean setToSplat = false;
    public boolean setToGrab = false;
    public boolean setToSlide = false;
    public boolean setToButtBounce = false;
    public boolean setToDash = false;
    public boolean setToDie = false;

    public boolean floorClear = false;
    public boolean deadStatus = false;

    public Blobb(PlayScreen screen) {
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        facingRight = true;

        //Make Texture Regions representing different frames that we will
        //use for animation later
        Array<TextureRegion> running_frames = new Array<TextureRegion>();
        Array<TextureRegion> jumping_frames = new Array<TextureRegion>();
        Array<TextureRegion> splatting_frames = new Array<TextureRegion>();
        Array<TextureRegion> pounding_frames = new Array<TextureRegion>();
        Array<TextureRegion> grabbing_frames = new Array<TextureRegion>();
        Array<TextureRegion> falling_frames = new Array<TextureRegion>();
        Array<TextureRegion> floating_frames = new Array<TextureRegion>();
        Array<TextureRegion> sliding_frames = new Array<TextureRegion>();
        Array<TextureRegion> dashing_frames = new Array<TextureRegion>();
        Array<TextureRegion> dying_frames = new Array<TextureRegion>();

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

        for(int i = 0; i <= 2; i++){
            dashing_frames.add(new TextureRegion(screen.getAtlas().findRegion(dashing[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 1; i++){
            sliding_frames.add(new TextureRegion(screen.getAtlas().findRegion(sliding[i]), 0, 0, 16, 16));
        }

        for(int i = 0; i <= 8; i++){
            dying_frames.add(new TextureRegion(screen.getAtlas().findRegion(dying[i]), 0, 0, 16, 16));
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

        //Create the animation of Dashing
        BlobbDash = new Animation<TextureRegion>(0.06f, dashing_frames);
        dashing_frames.clear();

        //Create the animation for Dying
        BlobbDie = new Animation<TextureRegion>(0.1f, dying_frames);
        dying_frames.clear();

        BlobbSlide = new Animation<TextureRegion>(0.1f, sliding_frames);
        sliding_frames.clear();

        //create texture region for Blobb standing
        BlobbStand = new TextureRegion(screen.getAtlas().findRegion("Running-1"), 0, 0, 16, 16);


        //define Blobb in Box2d
        defineBlobb();

        //set initial values for Blobbs location, width and height. And initial frame as BlobbStand.
        setBounds(0, -16, 16 / RetroGame.PPM, 16 / RetroGame.PPM);
        setRegion(BlobbStand);
    }

    public void update(float dt) {
        setPosition(b2Body.getPosition().x - getWidth() / 2, b2Body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));

        //if he's touching a left or right boundary, he's touching a wall
        touchingWall = onLeftWall || onRightWall;

        //if the blobb has landed, the float properties have been reset
        if (feetOnGround)
            initialFloat = true;

        if (setToFall && b2Body.getLinearVelocity().y < 0)
            setToFall = false;

        System.out.println("feet: " + feetOnGround + " " +
                "head: " + headOnCeiling + " " +
                "left: " + onLeftWall + " " +
                "right: " + onRightWall);

        System.out.println(currentState);


    }


    private TextureRegion getFrame(float dt) {
        //get Blobbs current state. ie. jumping, running, standing...
        currentState = getState();
        TextureRegion region = BlobbStand;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState) {
            case STANDING:
                region = BlobbStand;
                break;
            case JUMPING:
                region = BlobbJump.getKeyFrame(stateTimer,false);
                groundJumpCheck();
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
                floatCheck(floatTimer);
                break;
            case GRABBING:
                region = BlobbGrab.getKeyFrame(stateTimer, false);
                grabCheck();
                break;
            case SLIDING:
                region = BlobbSlide.getKeyFrame(stateTimer, false);
                slideCheck();
                break;
            case DASHING:
                region = BlobbDash.getKeyFrame(stateTimer, false);
                dashCheck();
                break;
            case DYING:
                region = BlobbDie.getKeyFrame(stateTimer, false);
                dieCheck();
                break;

            default:
                region = BlobbStand;
                break;
        }

        //if Blobb is moving left and the texture isn't facing left... flip it.
        if ((b2Body.getLinearVelocity().x < 0 || !facingRight) && !region.isFlipX()) {
            region.flip(true, false);
            facingRight = false;
        }

        //if Blobb is moving right and the texture isnt facing right... flip it.
        else if ((b2Body.getLinearVelocity().x > 0 || facingRight) && region.isFlipX()) {
            region.flip(true, false);
            facingRight = true;
        }


        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and the timer needs to be reset
        stateTimer = currentState == previousState ? stateTimer + dt : 0;

        //floatTimer uses the delta time to find if the float duration has exceeded the threshold
        floatTimer += dt;

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
        b2Body.setGravityScale(0.5f);

        //Box2D fixture settings
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5 / RetroGame.PPM, 7 / RetroGame.PPM);

        //Set what Blobb can collide with
        fdef.filter.categoryBits = RetroGame.BLOBB_GENERAL_BIT;
        fdef.filter.maskBits = RetroGame.GROUND_BIT |
                RetroGame.SPIKE_BIT |
                RetroGame.TRAMPOLINE_BIT |
                RetroGame.PLATFORM_ON_BIT |
                RetroGame.CRUMBLE_PLATFORM_BIT |
                RetroGame.GOAL_BIT;
        fdef.shape = shape;
        //fdef.friction = (float)0.5;
        b2Body.createFixture(fdef).setUserData(this);

        //Give Blobb an edge to serve as his head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-4 / RetroGame.PPM, 7.2f / RetroGame.PPM), new Vector2(4 / RetroGame.PPM, 7.2f / RetroGame.PPM));
        fdef.filter.categoryBits = RetroGame.BLOBB_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);

        //Give Blobb an edge to serve as his feet
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-4 / RetroGame.PPM, -7.2f / RetroGame.PPM), new Vector2(4 / RetroGame.PPM, -7.2f / RetroGame.PPM));
        fdef.filter.categoryBits = RetroGame.BLOBB_FEET_BIT;
        fdef.shape = feet;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);

        //Give Blobb an edge to serve as his left
        EdgeShape left = new EdgeShape();
        left.set(new Vector2(-5.2f / RetroGame.PPM, 6 / RetroGame.PPM), new Vector2(-5.2f / RetroGame.PPM, -6 / RetroGame.PPM));
        fdef.filter.categoryBits = RetroGame.BLOBB_LEFT_BIT;
        fdef.shape = left;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);

        //Give Blobb an edge to serve as his right
        EdgeShape right = new EdgeShape();
        right.set(new Vector2(5.2f / RetroGame.PPM, 6 / RetroGame.PPM), new Vector2(5.2f / RetroGame.PPM, 6 / RetroGame.PPM));
        fdef.filter.categoryBits = RetroGame.BLOBB_RIGHT_BIT;
        fdef.shape = right;
        fdef.isSensor = true;
        b2Body.createFixture(fdef).setUserData(this);
    }

    private State getState(){
        if (!specialMovement()) {
            //if touching a wall in midair or was Grabbing and still falling, start slide
             if ( (touchingWall && !feetOnGround && b2Body.getLinearVelocity().y < 0) /* touching wall and not ground, while falling */
                     // || (previousState == State.GRABBING && !feetOnGround) && canSlide ) {
             ) {
                 startSlide();
                 return State.SLIDING; }
            //if positive in Y-Axis or negative but was jumping, Blobb is jumping
            else if (b2Body.getLinearVelocity().y > 0 || (b2Body.getLinearVelocity().y < 0 && (previousState == State.JUMPING)))
                return State.JUMPING;
                //if negative in Y-Axis and was standing, floating, or falling, Blobb is falling
            else if ((b2Body.getLinearVelocity().y < 0 && (previousState == State.STANDING || previousState == State.FLOATING || previousState == State.FALLING || previousState == State.DASHING || previousState == State.GRABBING)))
                return State.FALLING;
                 //
             else if (b2Body.getGravityScale()==0)
                 return State.DASHING;
                //if Blobb is positive or negative in the X axis he is running
            else if (previousState == State.GRABBING) // Only reachable if special movement flags, include grab and slide are false
                return State.FALLING;
            else if (b2Body.getLinearVelocity().x != 0 && b2Body.getLinearVelocity().y == 0) {
                return State.RUNNING; }
            //default him standing
            else {
                clearMovementFlags();
                return State.STANDING;
            }
        }

        else {
            //if pounding
            if (setToPound) {
                //if he has landed and the pound is finished, he is now splatting
                if (b2Body.getGravityScale() == 0.5f && feetOnGround) {
                    setToPound = false;
                    startSplat();
                    RetroGame.manager.get("audio/sounds/splatDown.mp3", Sound.class).play(0.1f);
                    return State.SPLATTING;
                }
                //otherwise still pounding
                else
                    return State.POUNDING;
            } else if (setToSplat) {
                if (BlobbSplat.isAnimationFinished(stateTimer)) {
                    setToSplat = false;
                    return State.STANDING;
                } else
                    return State.SPLATTING;
            } else if (setToFloat) {
                return State.FLOATING;
            } else if (setToSlide) {
                return State.SLIDING;
            } else if (setToGrab) {
                return State.GRABBING;
            } else if (setToDash) {
                return State.DASHING;
            } else if (setToDie) {
                return State.DYING;
            } else
                return State.STANDING;

        }
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void setCurrentState(State newState) {
        currentState = newState;
    }

    public void moveLeftGround() {
        //put a cap on left ground movement speed
        if (b2Body.getLinearVelocity().x >= -2)
            b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveRightGround() {
        //put a cap on right ground movement speed
        if (b2Body.getLinearVelocity().x <= 2)
            b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveLeftAir() {
        //put a cap on left air movement speed
        if (b2Body.getLinearVelocity().x >= -2)
            b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveRightAir() {
        //put a cap on right air movement speed
        if (b2Body.getLinearVelocity().x <= 2)
            b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveLeftFloat() {
        //put a cap on left air movement speed
        if (b2Body.getLinearVelocity().x >= -0.7f)
            b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveRightFloat() {
        //put a cap on right air movement speed
        if (b2Body.getLinearVelocity().x <= 0.7f)
            b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveLeftSlide() {
        //put a cap on left air movement speed
        if (onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
    }

    public void moveRightSlide() {
        //put a cap on left air movement speed
        if (onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }


    public void startGroundJump() {
        clearMovementFlags();
        setToJump = true;
        b2Body.applyLinearImpulse(new Vector2(0, 2.3f), b2Body.getWorldCenter(), true);

        RetroGame.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.5f);
    }

    public void groundJumpCheck() {
        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE))
            startFall();
        if (setToFall) {
            if (b2Body.getLinearVelocity().y >= 0) {
                b2Body.applyLinearImpulse(new Vector2(0, -0.3f), b2Body.getWorldCenter(), true);
            }
        }
    }

    public void wallJump() { //TODO: should he wall jump even if he's not grabbing or sliding?
        //set velocity to zero to accomodate for impulse
        b2Body.setLinearVelocity(0,0);

        //take in the modifier keys being input and apply the linear impulse based on the combination and which wall the blobb is on
        if (Gdx.input.isKeyPressed(Input.Keys.W) && onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-0.5f, 2), b2Body.getWorldCenter(), true);
        else if (Gdx.input.isKeyPressed(Input.Keys.W) && onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(0.5f, 2), b2Body.getWorldCenter(), true);

        else if (Gdx.input.isKeyPressed(Input.Keys.A) && onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-2, 0.5f), b2Body.getWorldCenter(), true);
        else if (Gdx.input.isKeyPressed(Input.Keys.D) && onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(2, 0.5f), b2Body.getWorldCenter(), true);

        else if (Gdx.input.isKeyPressed(Input.Keys.A) && Gdx.input.isKeyPressed(Input.Keys.W) && onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-1.5f, 1.3f), b2Body.getWorldCenter(), true);
        else if (Gdx.input.isKeyPressed(Input.Keys.D) && Gdx.input.isKeyPressed(Input.Keys.W) && onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(1.5f, 1.3f), b2Body.getWorldCenter(), true);


        else if (onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-1, 0.5f), b2Body.getWorldCenter(), true);
        else if (onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(1, 0.5f), b2Body.getWorldCenter(), true);

        //revert state to normal
        clearMovementFlags();
        b2Body.setGravityScale(0.5f);
        RetroGame.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.5f);
    }

    public void startFall() {
        setToFall = true;
    }

    public void startPound() {
        clearMovementFlags();
        setToPound = true;

        buttBounceHeight = b2Body.getPosition().y * RetroGame.PPM;
        System.out.println(buttBounceHeight);
    }

    public void poundCheck() {
        if (BlobbPound.isAnimationFinished(stateTimer)) {
            b2Body.setGravityScale(0.5f);
            b2Body.applyLinearImpulse(new Vector2(0, -.3f), b2Body.getWorldCenter(), true);
        }
        else {
            b2Body.setLinearVelocity(0,0);
            b2Body.setGravityScale(0);
        }
    }

    public void startSplat() {
        clearMovementFlags();
        setToSplat = true;
    }

    public void splatCheck() {
    }

    public void startFloat() {
        clearMovementFlags();
        setToFloat = true;

        b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);

        //on initial float, give an upwards impulse and begin the float timer
        if (initialFloat) {
            b2Body.applyLinearImpulse(new Vector2(0, 0.7f), b2Body.getWorldCenter(), true);
            initialFloat = false;
            floatTimer = 0;
        }
        RetroGame.manager.get("audio/sounds/float.mp3", Sound.class).play(0.6f);
    }

    public void floatCheck(float floatTimer) {
        //if key isn't pressed or timer is exceeded threshold or the player is on the ground or set to grab
        if (!Gdx.input.isKeyPressed(Input.Keys.K) || floatTimer > 5 || (feetOnGround && b2Body.getLinearVelocity().y == 0) || setToGrab) {
            setToFloat = false;
            b2Body.setGravityScale(0.5f);

            if (floatTimer > 5) {
                canFloat = false;
                RetroGame.manager.get("audio/sounds/pop.mp3", Sound.class).play(0.4f);
            }
            else
                canFloat = true;


        }
        else {
            b2Body.setGravityScale(0.1f);

            if (b2Body.getLinearVelocity().y <= -0.3f)
                b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, -0.3f);
        }
    }

    public void startSlide() {
        clearMovementFlags();
        setToSlide = true;
        canSlide = true;
    }

    /**
     * Logic for ending slide and mechanics for movement while sliding.
     * Ending slide:
     *      If not touching wall, fall freely
     *      If touching ground, do nothing, and allow state to return to STANDING
     *      If setToGrab, player is trying to grab wall, call startGrab to halt motion
     *      TODO: are we still relying on variable canSlide? Function calls to all the lifting to halt sliding
     * If y velocity is positive, shouldn't be sliding. Move freely upwards. Player would grab wall if he didn't want to rise.
     *
     * Moving while sliding:
     *      Maintain constant speed, slow descent.
     */
    public void slideCheck() {
        //if not touching a wall or on the ground or set to grab, no more sliding
        if (!touchingWall || feetOnGround || setToGrab || !canSlide ||
            b2Body.getLinearVelocity().y > 0) {
            if(setToGrab) {
                startGrab();
            } else {
                setToSlide = false;
                b2Body.setGravityScale(0.5f);
            }
        } else { // continue sliding motion
            b2Body.setLinearVelocity(0, -.5f);
        }
    }

    public void startGrab() {
        clearMovementFlags();
        setToGrab = true;
    }

    public void grabCheck() {
        //if not pressing the grab key or not touching wall, grab ends
        if (!Gdx.input.isKeyPressed(Input.Keys.L)){
            startSlide();
        } else if (!touchingWall) {
            setToGrab = false;
            b2Body.setGravityScale(0.5f);
            b2Body.setLinearVelocity(0,-0.1f);
        }
        else {
            b2Body.setLinearVelocity(0,0);
            b2Body.setGravityScale(0);
        }
    }

    public void startDash() {
        //set gravity to zero and apply linear impulse in that direction
        clearMovementFlags();
        stateTimer = 0;
        setToDash = true;

        b2Body.setLinearVelocity(0,0);
        b2Body.setGravityScale(0);
        if (Gdx.input.isKeyPressed(Input.Keys.A) || !facingRight) {
            if (currentState == State.GRABBING && !Gdx.input.isKeyPressed(Input.Keys.A))
                b2Body.applyLinearImpulse(new Vector2(3, 0), b2Body.getWorldCenter(), true);
            else
                b2Body.applyLinearImpulse(new Vector2(-3, 0), b2Body.getWorldCenter(), true);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D) || facingRight) {
            if (currentState == State.GRABBING && !Gdx.input.isKeyPressed(Input.Keys.D))
                b2Body.applyLinearImpulse(new Vector2(-3, 0), b2Body.getWorldCenter(), true);
            else
                b2Body.applyLinearImpulse(new Vector2(3, 0), b2Body.getWorldCenter(), true);
        }

        RetroGame.manager.get("audio/sounds/dash.mp3", Sound.class).play(0.4f);
    }

    public void dashCheck() {
        //if dash animation is finished, set state back to normal
        if (BlobbDash.isAnimationFinished(stateTimer)) {
            b2Body.setGravityScale(0.5f);
            b2Body.setLinearVelocity(0,0);
            setToDash = false;
            canDash = false;
        }
        else {
            b2Body.setGravityScale(0);
        }
    }

    public void startDie() {
        clearMovementFlags();
        stateTimer = 0;

        setToDie = true;
        b2Body.setGravityScale(0);
        b2Body.setLinearVelocity(0,0);

        Filter filter = new Filter();
        filter.maskBits = RetroGame.NOTHING_BIT;

        for (Fixture fixture : b2Body.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        RetroGame.manager.get("audio/sounds/die.mp3", Sound.class).play(0.4f);
    }

    public void dieCheck() {
        if (BlobbDie.isAnimationFinished(stateTimer)) {
            deadStatus = true;
        }
    }

    public boolean isDead() {
        return deadStatus;
    }

    /**
    TODO: Decide behavior. There's not much room to be better than a normal jump but worse than a trampoline.
     Currently written so jump height mirrors fall height
     */
    public void buttBounce() {
        clearMovementFlags();
        //buttBounceHeight += 1;
        b2Body.applyLinearImpulse(new Vector2(b2Body.getLinearVelocity().x, buttBounceHeight), b2Body.getWorldCenter(), true);
        RetroGame.manager.get("audio/sounds/splatUp.mp3", Sound.class).play(0.1f);
    }

    public void clearMovementFlags() {
        setToPound = false;
        setToFloat = false;
        setToSplat = false;
        setToGrab = false;
        setToButtBounce = false;
        setToDash = false;
        setToSlide = false;
        setToDie = false;
    }

    //if no special movement is happening, return false. Otherwise true
    public boolean specialMovement() {
        return (setToFloat || setToPound || setToSplat || setToGrab || setToDash || setToSlide || setToDie);
    }

    public void trampolineBounce() {
        if (b2Body.getLinearVelocity().y <= 0) {
            clearMovementFlags();
            b2Body.setGravityScale(0.5f);
            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, 0);

            b2Body.applyLinearImpulse(new Vector2(0, 4), b2Body.getWorldCenter(), true);
            setToFloat = false;
            initialFloat = true;
            RetroGame.manager.get("audio/sounds/bounce.mp3", Sound.class).play();
        }
    }

    public boolean isFloorCleared() {
        return floorClear;
    }

    //Draw Blobb's physics body in the world
    public void draw(Batch batch){
        super.draw(batch);
    }


}
