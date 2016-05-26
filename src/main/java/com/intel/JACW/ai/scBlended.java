package com.intel.JACW.ai;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.limiters.NullLimiter;
import com.badlogic.gdx.math.Vector2;

/**
 * Artificial intelligence: agent has blended behavior, it moves and turns. 
 * Test uses libGdxAI engine. It can be used in games.
 * 
 */
public class scBlended extends AbstractTestCase {

	@XMLParameter(defaultValue = "1000")
	public int steps;
	
	private Agent Ann;
	private Agent Bob;

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		Ann = new Agent(new Vector2(0, 0), 0);
		Bob = new Agent(new Vector2(10000, 0), 0);

		Arrive<Vector2> aSB = new Arrive<Vector2>(Ann, Bob);
		LookWhereYouAreGoing<Vector2> lwyagSB = new LookWhereYouAreGoing<Vector2>(
				Ann);

		BlendedSteering<Vector2> sb = new BlendedSteering<Vector2>(Ann)
				.setLimiter(NullLimiter.NEUTRAL_LIMITER).add(aSB, 1f)
				.add(lwyagSB, 1f);

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
			for (int j = 0; j < steps; j++)
				Ann.update(0.5f);
			count += steps;
		}
		return count;
	}
}
