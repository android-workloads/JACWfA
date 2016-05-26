package com.intel.JACW.xphysics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * Domino falling down.
 * 
 */
public class Domino extends AbstractTestCase {

	@XMLParameter(defaultValue = "20")
	public int steps;
	
	private World world;
	private Vec2 gravity;
	private FixtureDef platformFixtureDef;
	private FixtureDef dominoFixtureDef;
	private FixtureDef floorFixtureDef;
	private BodyDef dominoBody;
	private BodyDef floor;
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		gravity = new Vec2(0.0f, -10.0f);
		platformFixtureDef = new FixtureDef();
		PolygonShape platformPolygonShape = new PolygonShape();
		platformPolygonShape.setAsBox(15.0f, 0.1f);
		platformFixtureDef.shape = platformPolygonShape;

		dominoFixtureDef = new FixtureDef();
		PolygonShape dominoPolygonShape = new PolygonShape();
		dominoPolygonShape.setAsBox(0.2f, 2.5f);
		dominoFixtureDef.shape = dominoPolygonShape;
		dominoFixtureDef.density = 25.0f;
		dominoFixtureDef.friction = .5f;
		dominoBody = new BodyDef();
		dominoBody.type = BodyType.DYNAMIC;
		
		floorFixtureDef = new FixtureDef();
		PolygonShape floorPolygonShape = new PolygonShape();
		floorPolygonShape.setAsBox(60.0f, 5.0f);
		floorFixtureDef.shape = floorPolygonShape;
		floor = new BodyDef();
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int k = 0; k < repeats; k++) {
			world = new World(gravity);
			for (int i = 0; i < 10; i++) {

				BodyDef platform = new BodyDef();
				platform.position = new Vec2(0.0f, 5f + 5f * i);
				world.createBody(platform).createFixture(platformFixtureDef);
			}

			int numPerRow = 15;
			for (int i = 0; i < 10; ++i) {
				for (int j = 0; j < numPerRow; j++) {
					dominoBody.position = new Vec2(-14.75f + j * (16.5f / (numPerRow - 1)), 7.3f + 5f * i);
					if (j == 0) {
						dominoBody.angle = -0.1f;
						dominoBody.position.x += .1f;
					} else
						dominoBody.angle = 0f;
					world.createBody(dominoBody).createFixture(dominoFixtureDef);
				}
			}

			floor.position.set(0.0f, -10.0f);
			world.createBody(floor).createFixture(floorFixtureDef);
			for (int t = 0; t < steps; t++) {
				world.step(1.0f / 60, 6, 2);
				count++;
			}
		}
		return count;
	}
}
