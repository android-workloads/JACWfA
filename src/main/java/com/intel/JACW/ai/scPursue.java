package com.intel.JACW.ai;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;

/**
 * Artificial intelligence: one agent pursues another one, which moves. 
 * Test uses uses libGdxAI engine. It can be used in games.
 * 
 */
public class scPursue extends AbstractTestCase {

	@XMLParameter(defaultValue = "1000")
	public int steps;
	
	private Agent Ann;
	private Agent Bob;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		Ann = new Agent(new Vector2(0, 0), 0);
		Bob = new Agent(new Vector2(10000, 0), 0);
		Bob.linearVelocity = new Vector2(50, -50);
		Pursue<Vector2> sb = new Pursue<Vector2>(Ann, Bob);
		Ann.setSteeringBehavior(sb);
	}

	@Override
	public long iteration() {
		long count = 0;
		for (int k = 0; k < repeats; k++) {
			Ann.position.set(0, 0);
			Ann.orientation = 0;
			Ann.linearVelocity.set(0, 0);
			Ann.angularVelocity = 0;
			Bob.position.set(0, 0);
			Bob.orientation = 0;
			Bob.linearVelocity.set(50, -50);
			Bob.angularVelocity = 0;
			for (int j = 0; j < steps; j++){
				Ann.update(0.5f);
				Bob.update(0.5f);
			}
			count += steps;
		}
		return count;
	}

}
