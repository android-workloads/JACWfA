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
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * Ball breaks bricks which form the wall.
 * 
 */
public class BreakingTheWall extends AbstractTestCase {

    @XMLParameter(defaultValue = "10")
    public int steps;
    @XMLParameter(defaultValue = "-50")
    public float gravityY;
    @XMLParameter(defaultValue = "3")
    public int wallHeight;
    @XMLParameter(defaultValue = "5")
    public int wallLen;
    @XMLParameter(defaultValue = "1")
    public int wallWidth;
    @XMLParameter(defaultValue = "100")
    protected float ballMass;
    @XMLParameter(defaultValue = "1")
    protected float brickMass;

    private RigidBody ground;
    private CollisionShape groundShape;

    private RigidBody ball;
    private CollisionShape ballShape;
    private Transform ballOrigTransform;
    private Vector3f ballLinearVelocity;
    private Vector3f ballAngularVelocity;

    private RigidBody[] brick;
    private CollisionShape brickShape;
    private Transform[] brickOrigTransform;

    private Vector3f zero3f;

    private DiscreteDynamicsWorld world;
    private int numBricks;
    private float sz = 4;

    @Override
    public void init(RunConfig config) throws InvalidTestFormatException {
        super.init(config);

        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
                collisionConfiguration);

        world.setGravity(new Vector3f(0, gravityY, 0));

        groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);
        ballShape = new SphereShape(10);
        brickShape = new BoxShape(new Vector3f(sz, sz, sz));

        DefaultMotionState groundMotion = new DefaultMotionState(new Transform(
                new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0),
                        1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
                0, groundMotion, groundShape, new Vector3f(0, 0, 0));
        ground = new RigidBody(groundRigidBodyCI);

        world.addRigidBody(ground);

        ballOrigTransform = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
                new Vector3f(sz * wallLen, 8, 30), 1.0f));
        DefaultMotionState ballMotion = new DefaultMotionState(
                ballOrigTransform);
        Vector3f ballInertia = new Vector3f(1000, 1000, 1000);
        ballShape.calculateLocalInertia(ballMass, ballInertia);
        RigidBodyConstructionInfo ballRigidBodyCI = new RigidBodyConstructionInfo(
                ballMass, ballMotion, ballShape, ballInertia);
        ball = new RigidBody(ballRigidBodyCI);
        ballLinearVelocity = new Vector3f(0, 20, -150);
        ballAngularVelocity = new Vector3f(50, 50, 50);
        ball.setLinearVelocity(ballLinearVelocity);
        ball.setAngularVelocity(ballAngularVelocity);
        world.addRigidBody(ball);

        zero3f = new Vector3f(0, 0, 0);
        Vector3f brickInertia = new Vector3f(0, 0, 0);
        brickShape.calculateLocalInertia(brickMass, brickInertia);
        brickInertia.x *= 20;
        brickInertia.y *= 20;
        brickInertia.z *= 20;

        wallWidth = 3;
        numBricks = wallHeight * wallLen * wallWidth;

        brickOrigTransform = new Transform[numBricks];
        brick = new RigidBody[numBricks];

        float offsetW = 0;
        for (int w = 0; w < wallWidth; w++) {
            float offsetH = 0;
            for (int h = 0; h < wallHeight; h++) {
                float offsetL = 0;
                for (int i = 0; i < wallLen; i++) {
                    makeBrick(w * wallHeight * wallLen + h * wallLen + i,
                            brickMass, brickInertia, new Vector3f(offsetL,
                                    offsetH + sz, offsetW), 0);
                    offsetL += 2 * sz;
                }
                offsetH += 2 * sz;
            }
            offsetW += 2 * sz;
        }
    }

    @Override
    public long iteration() {
        long count = 1;
        for (int i = 0; i < repeats; i++) {
            long stamp = System.currentTimeMillis();
            ball.setLinearVelocity(ballLinearVelocity);
            ball.setAngularVelocity(ballAngularVelocity);
            ball.setWorldTransform(ballOrigTransform);
            for (int j = 0; j < numBricks; j++) {
                brick[j].setWorldTransform(brickOrigTransform[j]);
                brick[j].setLinearVelocity(zero3f);
                brick[j].setAngularVelocity(zero3f);
            }
            for (int n = 0; n < steps; n++) {
                Vector3f cent = new Vector3f();
                ball.getCenterOfMassPosition(cent);
                world.stepSimulation(0.1f, 5, 0.03f); // world stepping 0.1 sec,
                                                      // virtual timeStep is
                                                      // 0.03 sec, expected
                                                      // simulated steps count <
                                                      // 5
            }
            count++;
        }
        return count;
    }

    /**
     * This method creates one individual physical domino.
     */
    public RigidBody makeBrick(int i, float mass, Vector3f inertia,
            Vector3f loc, float angle) {
        brickOrigTransform[i] = new Transform(new Matrix4f(new Quat4f(0f, 0f,
                0f, 1f), new Vector3f(loc), 1f));
        MotionState brickMotion = new DefaultMotionState(brickOrigTransform[i]);
        RigidBodyConstructionInfo brickRigidBodyCI = new RigidBodyConstructionInfo(
                mass, brickMotion, brickShape, inertia);
        brick[i] = new RigidBody(brickRigidBodyCI);
        world.addRigidBody(brick[i]);
        return brick[i];
    }
}