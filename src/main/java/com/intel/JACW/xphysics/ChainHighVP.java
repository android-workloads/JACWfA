package com.intel.JACW.xphysics;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * Movement of chained objects with high position and velocity precisely.
 * 
 * Test uses high position and velocity iterations on world update in step()
 * 
 */
public class ChainHighVP extends AbstractTestCase {
	
	@XMLParameter(defaultValue = "100")
	public int steps;
	
	private World world;
	private EdgeShape eshape;
	private PolygonShape shape; 
	private FixtureDef fixtureDef; 
	private BodyDef bodyDef;
	private Vec2 anchor;
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);


		eshape = new EdgeShape();
		eshape.set(new Vec2(-40.0f, 0.0f), new Vec2(40.0f, 0.0f));
		shape = new PolygonShape();
		shape.setAsBox(0.6f, 0.125f);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 20.0f;
		fixtureDef.friction = 0.2f;
		bodyDef = new BodyDef();
		anchor = new Vec2(0, 0);
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int k = 0; k < repeats; k++) {
			Vec2 gravity = new Vec2(0.0f, -10.0f);
			world = new World(gravity);
			Body ground = world.createBody(bodyDef);
			ground.createFixture(eshape, 0.0f);

			RevoluteJointDef jd = new RevoluteJointDef();
			jd.collideConnected = false;

			final float y = 25.0f;
			Body prevBody = ground;

			for (int i = 0; i < 35; ++i) {
				bodyDef.type = BodyType.DYNAMIC;
				bodyDef.position.set(0.5f + i, y);
				Body body = world.createBody(bodyDef);
				body.createFixture(fixtureDef);
				anchor.set(i, y);
				jd.initialize(prevBody, body, anchor);
				world.createJoint(jd);
				prevBody = body;
			}
			for (int t = 0; t < steps; t++) {
				world.step(1.0f / 60, 80, 80);
				count++;
			}
		}
		return count;
	}
}
