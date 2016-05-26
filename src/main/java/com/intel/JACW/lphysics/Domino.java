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

import static java.lang.Math.PI;

/**
 * Domino falling down.
 * 
 */
public class Domino extends AbstractTestCase {

    @XMLParameter(defaultValue = "20")
    public int steps;

    @XMLParameter(defaultValue = "-150")
    public float gravityY;
    @XMLParameter(defaultValue = "100")
    protected float ballMass;
    @XMLParameter(defaultValue = "1")
    protected float dominoMass;

    private static final int numDominoes = 40;
    private CollisionShape groundShape;
    private RigidBody ground;

    private CollisionShape ballShape;
    private RigidBody ball;
    private Transform ballOrigTransform;
    private Vector3f ballLinearVelocity;

    private CollisionShape dominoShape;
    private RigidBody[] domino;
    private Transform[] dominoOrigTransform;

    private Vector3f zero3f;

    private DiscreteDynamicsWorld world;

    @Override
    public void init(RunConfig config) throws InvalidTestFormatException {
        super.init(config);

        cntCreated = 0;
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
                collisionConfiguration);

        world.setGravity(new Vector3f(0, gravityY, 0));

        groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
        ballShape = new SphereShape(5);
        dominoShape = new BoxShape(new Vector3f(0.20f, 1.4f, 0.8f));

        DefaultMotionState groundMotionState = new DefaultMotionState(
                new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
                        new Vector3f(0, -1, 0), 1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
                0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        ground = new RigidBody(groundRigidBodyCI);

        world.addRigidBody(ground);

        // ballOrigTransform = new Transform(new Matrix4f(new Quat4f(0, 0, 0,
        // 1), new Vector3f(-1, 1, 1), 1.0f));
        ballOrigTransform = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
                new Vector3f(13, 15, 15), 1.0f));
        DefaultMotionState ballMotion = new DefaultMotionState(
                ballOrigTransform);

        Vector3f ballInertia = new Vector3f(0, 0, 0);
        ballShape.calculateLocalInertia(ballMass, ballInertia);

        RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(
                ballMass, ballMotion, ballShape, ballInertia);
        ball = new RigidBody(fallRigidBodyCI);
        ball.setRestitution(0.5f);
        // ballLinearVelocity = new Vector3f(15, 2, -10);

        ballLinearVelocity = new Vector3f(0, -10, 0);
        ball.setLinearVelocity(ballLinearVelocity);
        world.addRigidBody(ball);

        zero3f = new Vector3f(0, 0, 0);

        Vector3f dominoInertia = new Vector3f(0, 0, 0);
        dominoShape.calculateLocalInertia(dominoMass, dominoInertia);
        dominoInertia.x *= 15;
        dominoInertia.y *= 15;
        dominoInertia.z *= 15;

        dominoOrigTransform = new Transform[numDominoes];
        domino = new RigidBody[numDominoes];

        int num = 15;
        for (int i = 0; i < num; i++) {
            float angle = (float) (-i * (PI / 2) / (num - 1));
            float R = 10;
            makeDomino(dominoMass, dominoInertia, new Vector3f(R * cos(angle),
                    2f, 10 + R * sin(angle)), (float) (angle));

        }
        for (int i = 0; i < 10; i++) {
            makeDomino(dominoMass, dominoInertia, new Vector3f(11f, 1.4f,
                    i + 11), (float) (-PI * 1 / 2));
        }
        for (int i = 0; i < num; i++) {
            float angle = (float) (-i * (PI / 2) / (num - 1) + PI / 2);
            float R = 10;
            makeDomino(dominoMass, dominoInertia, new Vector3f(2 + R
                    * cos(angle), 1.4f, 21 + R * sin(angle)),
                    (float) (PI / 4 + angle));

        }
        for (int i = 0;; i++) {
            boolean f = makeDomino(dominoMass, dominoInertia, new Vector3f(
                    -i + 1, 1.4f, 31f), 0);
            if (!f)
                break;
        }
    }

    @Override
    public long iteration() {
        long count = 0;
        for (int i = 0; i < repeats; i++) {
            ball.setLinearVelocity(ballLinearVelocity);
            ball.setAngularVelocity(zero3f);
            ball.setWorldTransform(ballOrigTransform);

            for (int j = 0; j < numDominoes; j++) {
                domino[j].setLinearVelocity(zero3f);
                domino[j].setAngularVelocity(zero3f);
                domino[j].setWorldTransform(dominoOrigTransform[j]);
            }
            for (int n = 0; n < steps; n++) {
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
    int cntCreated;

    public boolean makeDomino(float mass, Vector3f inertia, Vector3f loc,
            float angle) {
        if (cntCreated >= numDominoes)
            return false;
        int i = cntCreated++;
        dominoOrigTransform[i] = new Transform(new Matrix4f(new Quat4f(0,
                sin(angle) / (float) Math.sqrt(2), 0, 1), loc, 1.0f));
        DefaultMotionState dominoMotion = new DefaultMotionState(
                dominoOrigTransform[i]);
        RigidBodyConstructionInfo dominoRigidBodyCI = new RigidBodyConstructionInfo(
                mass, dominoMotion, dominoShape, inertia);
        domino[i] = new RigidBody(dominoRigidBodyCI);
        domino[i].setRestitution(0.5f);
        world.addRigidBody(domino[i]);
        return true;
    }

    private static float cos(float num) {
        return (float) Math.cos((double) num);
    }

    private static float sin(float num) {
        return (float) Math.sin((double) num);
    }
}