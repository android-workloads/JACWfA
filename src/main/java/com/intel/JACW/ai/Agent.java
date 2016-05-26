package com.intel.JACW.ai;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

/**
 * Class realizes basic entity for AI tests. It is a wrapper over com.badlogic.gdx.ai.steer.Steerable.  
 * 
 */
public class Agent implements Steerable<Vector2>{
	
	public Vector2 position;
	public float orientation;
	public Vector2 linearVelocity;
	public float angularVelocity;
    
	private float maxLinearSpeed;
	private float maxLinearAcceleration;
	private float maxAngularSpeed;
	private float maxAngularAcceleration;
	private float zeroLinearThreshold;

	float boundingRadius;
	
	private final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	boolean tagged;
	
	public SteeringBehavior<Vector2> steeringBehavior;

	public Agent(Vector2 pos, float orient){
		position = pos;
		orientation = orient;
		linearVelocity = new Vector2(0,0);
		angularVelocity = 0;
		
		maxLinearSpeed = 9;
		maxLinearAcceleration = 2;
		maxAngularSpeed = 20;
		maxAngularAcceleration = 20;
		zeroLinearThreshold = 100;
	}
	
	public SteeringBehavior<Vector2> getSteeringBehavior() {
		return steeringBehavior;
	}

	public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}
	
	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxAngularAcceleration(float arg0) {
		maxAngularAcceleration = arg0;
	}

	@Override
	public void setMaxAngularSpeed(float arg0) {
		maxAngularSpeed = arg0;
	}

	@Override
	public void setMaxLinearAcceleration(float arg0) {
		maxLinearAcceleration = arg0;
	}

	@Override
	public void setMaxLinearSpeed(float arg0) {
		maxLinearSpeed = arg0;
	}

	@Override
	public Vector2 angleToVector(Vector2 arg0, float arg1) {
		arg0.x = -(float)Math.sin(arg1);
		arg0.y = (float)Math.cos(arg1);
        return arg0;
	}

	@Override
	public float getAngularVelocity() {
		return angularVelocity;
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return linearVelocity;
	}

	@Override
	public float getOrientation() {
		return orientation;
	}

	@Override
	public Vector2 getPosition() {
		return position;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean arg0) {
		tagged = arg0;		
	}

	@Override
	public float vectorToAngle(Vector2 arg0) {
		return (float)Math.atan2(-arg0.x, arg0.y);
	}
	
	public void update (float deltaTime) {
		if (steeringBehavior != null) {
			steeringBehavior.calculateSteering(steeringOutput);
			applySteering(steeringOutput, deltaTime);
		} else {
			applyWithoutSteering(deltaTime);
		}
	}

	private void applySteering(SteeringAcceleration<Vector2> steering, float deltaTime) {
		position.mulAdd(linearVelocity, deltaTime);
		linearVelocity.mulAdd(steering.linear, deltaTime).limit(this.getMaxLinearSpeed());
		
		orientation += angularVelocity * deltaTime;
        angularVelocity += steering.angular * deltaTime;
	}

	private void applyWithoutSteering(float deltaTime) {
		position.mulAdd(linearVelocity, deltaTime);
		orientation += angularVelocity * deltaTime;
	}

	@Override
	public Location<Vector2> newLocation() {
		return null;
	}

	@Override
	public void setOrientation(float arg0) {
		orientation = arg0;
		
	}
	
	public void setPosition(Vector2 arg0) {
		position = arg0;
		
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		return zeroLinearThreshold;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float arg0) {
		zeroLinearThreshold = arg0;
	}
}
