package com.team.retrogame.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.team.retrogame.RetroGame;
import com.team.retrogame.Screens.PlayScreen;

/**
 * created by Ben Mankin on 09/13/18.
 */

/*
Collect all resources from Tiled Map, and place rigid bodies using the Box2D physics
simulation on the PlayScreen.
 */
public class B2WorldCreator {

    public B2WorldCreator(PlayScreen screen) {
        //assign the playscreen's world and map to the current WorldCreator
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        /*
        The tiled map is analyzed by layer. Each layer has a different type of object assigned
        to it. This first one is the ground layer, which happens to be the layer at index 2.
        Each rectangle in this layer is a piece of ground, and so each rectangle is then
        drawn at the map coordinates and given a physics body. This method is used for each type of
        Object.
         */

        //Create Ground Fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.GROUND_BIT;
            body.createFixture(fdef);
        }

        //Create Wall Fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.WALL_BIT;
            body.createFixture(fdef);
        }

        //Create Spike Fixtures
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.SPIKE_BIT;
            body.createFixture(fdef);
        }

        //Create Trampoline Fixtures
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.TRAMPOLINE_BIT;
            body.createFixture(fdef);
        }

        //Create One Way Platform Fixtures
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.ONE_WAY_PLATFORM_BIT;
            body.createFixture(fdef);
        }

        //Create Crumble Platform Fixtures
        for (MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.CRUMBLE_PLATFORM_BIT;
            body.createFixture(fdef);
        }

        //Create Crumble Wall Fixtures
        for (MapObject object : map.getLayers().get(10).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / RetroGame.PPM, (rect.getY() + rect.getHeight() / 2) / RetroGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / RetroGame.PPM, (rect.getHeight() / 2) / RetroGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = RetroGame.GOAL_BIT;
            body.createFixture(fdef);
        }
    }
}
