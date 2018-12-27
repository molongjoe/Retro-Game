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
import com.team.retrogame.Tools.MainInputHandler;

/**
 * created by Blobb Team on 09/13/18.
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
    public enum State {FALLING, JUMPING, STANDING, RUNNING, SPLATTING, POUNDING, BUTT_BOUNCING, FLOATING, GRABBING,
        SLIDING, DASHING, DYING}

    //log current state and previous state
    public State currentState;
    public State previousState;

    public World world;
    public PlayScreen screen;
    public Body b2Body;

    public MainInputHandler inputHandler;

    //All sprite Texture Regions and Animations for Blobb
    private TextureRegion BlobbStand;
    private Animation<TextureRegion> BlobbRun;
    private Animation<TextureRegion> BlobbJump;
    private Animation<TextureRegion> BlobbSplat;
    private Animation<TextureRegion> BlobbPound;
    private Animation<TextureRegion> BlobbButtBounce;
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
    public boolean canFloat = true;
    public boolean canDash = true;
    public boolean initialFloat = true;
    public float buttBounceHeight = 0;
    public float buttBounceBottom = 0;
    public float longPressThreshold = 0.35f;

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

        BlobbButtBounce = BlobbJump;

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
        if (feetOnGround && currentState != State.FLOATING)
            initialFloat = true;

        if (setToFall && b2Body.getLinearVelocity().y < 0)
            setToFall = false;

        if (feetOnGround) {
            canDash = true;
            canFloat = true;
        }

        if ((currentState == State.POUNDING || currentState == State.JUMPING || currentState == State.FALLING) && (screen.getInputHandler().longPress) && (canFloat)) {
            startFloat();
        }

        incrementTapTime(dt);

        /*
        System.out.println("feet: " + feetOnGround + " " +
                "head: " + headOnCeiling + " " +
                "left: " + onLeftWall + " " +
                "right: " + onRightWall);
        */
    }


    private TextureRegion getFrame(float dt) {
        //get Blobbs current state. ie. jumping, running, standing...
        currentState = getState();
        TextureRegion region = BlobbStand;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState) {
            case STANDING:
                region = BlobbStand;
                standCheck();
                break;
            case JUMPING:
                region = BlobbJump.getKeyFrame(stateTimer,false);
                jumpCheck();
                break;
            case RUNNING:
                region = BlobbRun.getKeyFrame(stateTimer, true);
                runCheck();
                break;
            case FALLING:
                region = BlobbFall.getKeyFrame(stateTimer, false);
                fallCheck();
                break;
            case POUNDING:
                region = BlobbPound.getKeyFrame(stateTimer, false);
                poundCheck();
                break;
            case SPLATTING:
                region = BlobbSplat.getKeyFrame(stateTimer, false);
                splatCheck();
                break;
            case BUTT_BOUNCING:
                region = BlobbButtBounce.getKeyFrame(stateTimer, false);
                buttBounceCheck();
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

        //make sure texture is facing the right way
        if (currentState != State.SLIDING && currentState != State.GRABBING) {
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
        }

        //if Blobb is sliding, make sure texture is flipped correctly
        else {
            if (onLeftWall && !region.isFlipX())
                region.flip(true, false);
            else if (onRightWall && region.isFlipX())
                region.flip(true,false);
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and the timer needs to be reset
        stateTimer = currentState == previousState ? stateTimer + dt : 0;

        //floatTimer uses the delta time to find if the float duration has exceeded the threshold
        floatTimer += dt;

        //For debugging, displays the new state whenever the Blobb's state changes
        if (currentState != previousState)
            System.out.println(currentState);

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
                RetroGame.PLATFORM_BIT |
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
            //if touching a wall in midair and falling, Blobb is sliding
             if (((onLeftWall && !facingRight) || (onRightWall && facingRight)) && !feetOnGround && b2Body.getLinearVelocity().y < 0) {
                 startSlide();
                 return State.SLIDING; }
            //if positive in Y-Axis or negative but was jumping, Blobb is jumping
            else if (b2Body.getLinearVelocity().y > 0 || (b2Body.getLinearVelocity().y < 0 && (previousState == State.JUMPING)))
                return State.JUMPING;
            //if negative in Y-Axis and isn't coming down from a jump, Blobb is falling (to be used for a potential falling sprite separate from jumping sprite)
            else if ((b2Body.getLinearVelocity().y < 0 && (previousState != State.JUMPING)))
                return State.FALLING;
            //if the gravity is at 0, Blobb is dashing (the only nonspecial movement state where gravity is 0)
             else if (b2Body.getGravityScale()==0)
                 return State.DASHING;
             // Only reachable if special movement flags, include grab and slide are false
            else if (previousState == State.GRABBING)
                return State.FALLING;
            //if Blobb is positive or negative in the X axis and not falling or rising, Blobb is running
            else if (b2Body.getLinearVelocity().x != 0 && b2Body.getLinearVelocity().y == 0) {
                return State.RUNNING; }
            //default Blobb standing
            else {
                clearMovementFlags();
                return State.STANDING;
            }
        }

        else {
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
            } else if (setToButtBounce) {
                return State.BUTT_BOUNCING;
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

    //this method defines all normal left and right movement
    public void normalMovementHandler() {
        //put a cap on left movement speed
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            if (b2Body.getLinearVelocity().x >= -1.3f)
                b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
        //put a cap on right movement speed
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            if (b2Body.getLinearVelocity().x <= 1.3f)
                b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    //this method defines float movement
    public void floatMovementHandler() {
        //set gravity influence on Blobb to be lower
        b2Body.setGravityScale(0.1f);

        //put a cap on float descent speed
        if (b2Body.getLinearVelocity().y <= -0.3f)
            b2Body.setLinearVelocity(b2Body.getLinearVelocity().x, -0.3f);

        //put a cap on left float movement speed
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            if (b2Body.getLinearVelocity().x >= -0.7f)
                b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
        //put a cap on right float movement speed
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            if (b2Body.getLinearVelocity().x <= 0.7f)
                b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
    }

    //this method defines sliding movement
    public void slideMovementHandler() {
        if ((Gdx.input.isKeyPressed(Input.Keys.A)) && onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-0.08f, 0), b2Body.getWorldCenter(), true);
        else if ((Gdx.input.isKeyPressed(Input.Keys.D)) && onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(0.08f, 0), b2Body.getWorldCenter(), true);
        else
            b2Body.setLinearVelocity(0, -.5f);
    }

    //this method defines dashing movement
    public void dashMovementHandler() {
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
    }

    public void standCheck() {
        canFloat = true;
        normalMovementHandler();
    }

    public void runCheck() {
        //if not moving, the player will slow down rapidly
        if ((!Gdx.input.isKeyPressed(Input.Keys.A)) && (!Gdx.input.isKeyPressed(Input.Keys.D))) {
            if (b2Body.getLinearVelocity().x > 0.1f)
                b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), b2Body.getWorldCenter(), true);
            if (b2Body.getLinearVelocity().x < -0.1f)
                b2Body.applyLinearImpulse(new Vector2(0.1f, 0), b2Body.getWorldCenter(), true);
        }

        canFloat = true;
        normalMovementHandler();
    }

    public void startGroundJump() {
        clearMovementFlags();
        setToJump = true;

        b2Body.applyLinearImpulse(new Vector2(0, 2.3f), b2Body.getWorldCenter(), true);
        RetroGame.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.5f);
    }

    public void jumpCheck() {
        //if not pressing space, the player will stop ascending in a jump
        if (setToFall) {
            if (b2Body.getLinearVelocity().y >= 0)
                b2Body.applyLinearImpulse(new Vector2(0, -0.3f), b2Body.getWorldCenter(), true);
        }

        longPressThreshold = 0.35f;
        normalMovementHandler();
    }

    public void wallJump() {
        //set velocity to zero to accomodate for impulse
        b2Body.setLinearVelocity(0,0);

        if (onRightWall)
            b2Body.applyLinearImpulse(new Vector2(-1, 1.7f), b2Body.getWorldCenter(), true);
        else if (onLeftWall)
            b2Body.applyLinearImpulse(new Vector2(1, 1.7f), b2Body.getWorldCenter(), true);

        normalMovementHandler();

        //revert state to normal
        clearMovementFlags();
        b2Body.setGravityScale(0.5f);
        RetroGame.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.5f);
    }

    public void startFall() {
        clearMovementFlags();
        setToFall = true;
    }

    public void fallCheck() {
        longPressThreshold = 0.35f;
        normalMovementHandler();
    }

    public void startPound() {
        clearMovementFlags();
        setToPound = true;

        buttBounceHeight = b2Body.getPosition().y;
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

        longPressThreshold = 0.14f;
        normalMovementHandler();
    }

    public void startSplat() {
        clearMovementFlags();
        setToSplat = true;
    }

    public void splatCheck() {
        if (BlobbSplat.isAnimationFinished(stateTimer)) {
            setToSplat = false;
            buttBounceBottom = b2Body.getPosition().y;
            startButtBounce();
        }
        else
            b2Body.setLinearVelocity(0,0);
    }

    public void startButtBounce() {
        clearMovementFlags();
        setToButtBounce = true;

        b2Body.setGravityScale(0);

        //change speed of ascent based on how big the drop is
        if (buttBounceHeight - buttBounceBottom < 0.5f)
            b2Body.applyLinearImpulse(new Vector2(0, 1.6f), b2Body.getWorldCenter(), true);
        else if (buttBounceHeight - buttBounceBottom < 1)
            b2Body.applyLinearImpulse(new Vector2(0, 1.8f), b2Body.getWorldCenter(), true);
        else if (buttBounceHeight - buttBounceBottom < 1.5f)
            b2Body.applyLinearImpulse(new Vector2(0, 2), b2Body.getWorldCenter(), true);
        else if (buttBounceHeight - buttBounceBottom < 2)
            b2Body.applyLinearImpulse(new Vector2(0, 2.2f), b2Body.getWorldCenter(), true);
        else if (buttBounceHeight - buttBounceBottom < 2.5f)
            b2Body.applyLinearImpulse(new Vector2(0, 2.3f), b2Body.getWorldCenter(), true);
        else
            b2Body.applyLinearImpulse(new Vector2(0, 2.4f), b2Body.getWorldCenter(), true);

        RetroGame.manager.get("audio/sounds/splatUp.mp3", Sound.class).play(0.1f);
    }

    public void buttBounceCheck() {
        //if the blobb exceeds his starting height or if the player releases space, tge ascent is halted
        if (b2Body.getPosition().y >= buttBounceHeight)
            b2Body.setGravityScale(0.5f);

        if (b2Body.getLinearVelocity().y < 0)
            setToButtBounce = false;

        normalMovementHandler();
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
        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) || floatTimer > 5 || (feetOnGround && b2Body.getLinearVelocity().y == 0) || setToGrab) {
            setToFloat = false;
            b2Body.setGravityScale(0.5f);

            if (floatTimer > 5) {
                canFloat = false;
                RetroGame.manager.get("audio/sounds/pop.mp3", Sound.class).play(0.4f);

                /*
                TODO: these next three lines counteract the above lines. Just a placeholder until we have something to take the place of an empty float
                 */
                canFloat = true;
                initialFloat = true;
                startFloat();
            }
            else
                canFloat = true;
        }
        else
            floatMovementHandler();
    }

    public void startSlide() {
        clearMovementFlags();
        setToSlide = true;
    }

    public void slideCheck() {
        //if not touching a wall or on the ground or set to grab, no more sliding
        if (!touchingWall || feetOnGround || setToGrab || b2Body.getLinearVelocity().y > 0) {
            if(setToGrab) {
                startGrab();
            } else {
                setToSlide = false;
                b2Body.setGravityScale(0.5f);
            }
        } else { // continue sliding motion
            slideMovementHandler();
        }
    }

    public void startGrab() {
        clearMovementFlags();
        setToGrab = true;
    }

    public void grabCheck() {
        //if not pressing the grab key or not touching wall, grab ends
        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            startSlide();
        } else if (!touchingWall) {
            setToGrab = false;
            b2Body.setGravityScale(0.5f);
            b2Body.setLinearVelocity(0,-0.1f);
        }
        else {
            b2Body.setLinearVelocity(0,0);
            b2Body.setGravityScale(0);
            canDash = true;
        }
    }

    public void startDash() {
        //set gravity to zero and apply linear impulse in that direction
        clearMovementFlags();
        stateTimer = 0;
        setToDash = true;

        dashMovementHandler();

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
        else if (touchingWall) {
            setToDash = false;
            canDash = false;
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
                startGrab();
            else
                startSlide();
        }

        else
            b2Body.setGravityScale(0);
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
        return (setToFloat || setToPound || setToSplat || setToButtBounce || setToGrab || setToDash || setToSlide || setToDie);
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

    //method uses delta time to increment the tap state time depending on the current tap state
    public void incrementTapTime(float dt) {
        if (screen.getInputHandler().currentTapState == MainInputHandler.tapState.TAPPED)
            screen.getInputHandler().incrementTappedTime(dt);

        if (screen.getInputHandler().currentTapState == MainInputHandler.tapState.RELEASED)
            screen.getInputHandler().incrementReleasedTime(dt);

        //if tapped for long enough, the tap is a long press
        if (screen.getInputHandler().tappedTime > longPressThreshold)
            screen.getInputHandler().longPress = true;
        else
            screen.getInputHandler().longPress = false;
    }

    //Draw Blobb's physics body in the world
    public void draw(Batch batch){
        super.draw(batch);
    }

}
