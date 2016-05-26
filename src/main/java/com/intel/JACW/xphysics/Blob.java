package com.intel.JACW.xphysics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * Test simulates box falling.
 * 
 * This test performs creating and simulating the interaction of physical
 * objects. It uses jBox2d engine and can be used in games. Each step has many position iterations. Idea of this test was
 * inspired by similar test from jBox2D Testbed.
 * 
 */
public class Blob extends AbstractTestCase {

	@XMLParameter(defaultValue = "50")
	public int steps;
	
	private World world;
	private Body fallingBox;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		Vec2 gravity = new Vec2(0.0f, -10.0f);
		world = new World(gravity);

		ConstantVolumeJointDef constantVolumeJointDef = new ConstantVolumeJointDef();

		float cx = 0.0f;
		float cy = 10.0f;
		float rx = 5.0f;
		float ry = 5.0f;
		int n = 20;
		float circleRadius = 0.5f;
		for (int i = 0; i < n; ++i) {
			float angle = MathUtils.map(i, 0, n, 0, 2 * (float) Math.PI);
			BodyDef circleBodyDef = new BodyDef();
			circleBodyDef.fixedRotation = true;

			float x = cx + rx * (float) Math.sin(angle);
			float y = cy + ry * (float) Math.cos(angle);
			circleBodyDef.position.set(new Vec2(x, y));
			circleBodyDef.type = BodyType.DYNAMIC;
			Body circle = world.createBody(circleBodyDef);

			FixtureDef circleFixtureDef = new FixtureDef();
			CircleShape cd = new CircleShape();
			cd.m_radius = circleRadius;
			circleFixtureDef.shape = cd;
			circleFixtureDef.density = 1.0f;
			circle.createFixture(circleFixtureDef);
			constantVolumeJointDef.addBody(circle);
		}

		constantVolumeJointDef.frequencyHz = 10.0f;
		constantVolumeJointDef.dampingRatio = 1.0f;
		constantVolumeJointDef.collideConnected = false;
		world.createJoint(constantVolumeJointDef);

		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyType.DYNAMIC;
		PolygonShape psd = new PolygonShape();
		psd.setAsBox(3.0f, 1.5f, new Vec2(cx, cy + 15.0f), 0.0f);
		bodyDef2.position = new Vec2(cx, cy + 15.0f);
		fallingBox = world.createBody(bodyDef2);
		fallingBox.createFixture(psd, 1.0f);

		Body ground = null;
		PolygonShape sd = new PolygonShape();
		sd.setAsBox(50.0f, 0.4f);

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(0.0f, 0.0f);
		ground = world.createBody(bodyDef);
		ground.createFixture(sd, 0f);

		sd.setAsBox(0.4f, 50.0f, new Vec2(-10.0f, 0.0f), 0.0f);
		ground.createFixture(sd, 0f);
		sd.setAsBox(0.4f, 50.0f, new Vec2(10.0f, 0.0f), 0.0f);
		ground.createFixture(sd, 0f);
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int i = 0; i < repeats; i++) {
			float step = 1.0f / 60.0f;
			fallingBox.setTransform(new Vec2(0, 20), 0);
			fallingBox.setLinearVelocity(new Vec2(0, -10));
			fallingBox.setAngularVelocity(0);

			for (int t = 0; t < steps; t++) {
				world.step(step, 6, 2);
				count++;
			}
		}
		return count;
	}
}
