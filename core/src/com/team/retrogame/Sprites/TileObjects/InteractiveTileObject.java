package com.team.retrogame.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.team.retrogame.RetroGame;
import com.team.retrogame.Screens.PlayScreen;

/**
 * created by Blobb Team on 09/13/18.
 */

//InteractiveTileObject defines all tiles RetroGame can interact with
public abstract class InteractiveTileObject {
    private World world;
    private TiledMap map;
    protected TiledMapTile tile;
    private Rectangle bounds;
    private Body body;
    private PlayScreen screen;
    private MapObject object;

    private Fixture fixture;


    public InteractiveTileObject(PlayScreen screen, MapObject object) {
        //Define the tile in the world as a physics body
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / RetroGame.PPM, (bounds.getY() + bounds.getHeight() / 2) / RetroGame.PPM);

        body = world.createBody(bdef);

        shape.setAsBox((bounds.getWidth() / 2) / RetroGame.PPM, (bounds.getHeight() / 2) / RetroGame.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

    }

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * RetroGame.PPM / 16),
                (int)(body.getPosition().y * RetroGame.PPM / 16));
    }
}
