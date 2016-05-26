package com.intel.JACW.lphysics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * Ball damping.
 * 
 */
public class FallingBall extends AbstractTestCase {

  @XMLParameter(defaultValue = "50")
    public int steps;
  @XMLParameter(defaultValue = "-25")
    public float gravityY;
  @XMLParameter(defaultValue = "100")
    public float mass;

    private RigidBody ground;
    private CollisionShape groundShape;

    private int cntBalls = 20;
    private RigidBody ball[];
    private CollisionShape ballShape;
    private Transform ballOrigTransform[];
    private DiscreteDynamicsWorld world;
    private Vector3f ballLinearVelocity;

    private Vector3f zero3f;
    private DefaultMotionState dmt[];
    
    @Override
    public void init(RunConfig config) throws InvalidTestFormatException {
        super.init(config);
        
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        
        world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        world.setGravity(new Vector3f(0, gravityY, 0));

        groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
        ballShape = new SphereShape(1);

        DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0), 1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        ground = new RigidBody(groundRigidBodyCI);
        ground.setRestitution(1f);
        world.addRigidBody(ground);

        zero3f = new Vector3f(0, 0, 0);
        ballLinearVelocity = new Vector3f(0, -5 , 0);
        ballOrigTransform = new Transform[cntBalls];
        ball = new RigidBody[cntBalls];
        dmt = new DefaultMotionState[cntBalls];
        for(int i = 0; i < cntBalls; i++) {
            ballOrigTransform[i] = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(3 * i, 10, 0), 1.0f));
            dmt[i] = new DefaultMotionState(ballOrigTransform[i]);
    
            Vector3f ballInertia = new Vector3f(0, 0, 0);
            ballShape.calculateLocalInertia(mass, ballInertia);
    
            RigidBodyConstructionInfo ballRigidBodyCI = new RigidBodyConstructionInfo(mass, dmt[i], ballShape, ballInertia);
    
            ball[i] = new RigidBody(ballRigidBodyCI);
            ball[i].setRestitution(1 - .1f * i / cntBalls);
            ball[i].setDamping(.5f * i / cntBalls, .5f * i / cntBalls);
            ball[i].setFriction(.75f - .5f * i / cntBalls);
            ball[i].setLinearVelocity(ballLinearVelocity);
            world.addRigidBody(ball[i]);
        }
    }

    @Override
    public long iteration() {
        long count = 0;
        
        for (int i = 0; i < repeats; i++) {
            for(int j = 0; j < cntBalls; j++){
                ball[j].setLinearVelocity(ballLinearVelocity);
                ball[j].setAngularVelocity(zero3f);
                ball[j].setWorldTransform(ballOrigTransform[j]);
            }
            for (int n = 0; n < steps; n++) {
                world.stepSimulation(0.1f, 5, 0.03f); // world stepping 0.1 sec, virtual timeStep is 0.03 sec, expected simulated steps count < 5 
            }
            count++;
        }
        return count;
    }
}