package com.intel.JACW.lphysics;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;

/**
 * Tower falling down
 * 
 */
public class BreakingTheTower extends AbstractTestCase {

    @XMLParameter(defaultValue = "10")
    public int steps;
    @XMLParameter(defaultValue = "-500")
    public float gravityY;
    @XMLParameter(defaultValue = "100")
    public float ballMass;
    @XMLParameter(defaultValue = "1")
    public float brickMass;
    @XMLParameter(defaultValue = "20")
    public int towerHeight;
    @XMLParameter(defaultValue = "3")
    public int towerRotSize;

    private RigidBody ground;
    private CollisionShape groundShape;

    private RigidBody[] brick;
    private CollisionShape brickShape;
    private Transform[] brickOrigTransform;

    private Vector3f zero3f;

    private DiscreteDynamicsWorld world;
    private int numBricks;

    @Override
    public void init(RunConfig config) throws InvalidTestFormatException {
        super.init(config);
        numBricks = towerRotSize * towerHeight;

        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
                collisionConfiguration);

        world.setGravity(new Vector3f(0, gravityY, 0));

        groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);
        Vector3f brickSize = new Vector3f(1f, 1f, 1f);
        brickShape = new BoxShape(brickSize);

        DefaultMotionState groundMotion = new DefaultMotionState(new Transform(
                new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0),
                        1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
                0, groundMotion, groundShape, new Vector3f(0, 0, 0));
        ground = new RigidBody(groundRigidBodyCI);
        world.addRigidBody(ground);
        zero3f = new Vector3f(0, 0, 0);
        Vector3f dominoInertia = new Vector3f(0, 0, 0);
        brickShape.calculateLocalInertia(brickMass, dominoInertia);

        brickOrigTransform = new Transform[numBricks];
        brick = new RigidBody[numBricks];
        float radius = 1.3f * towerRotSize * brickSize.z;
        Quat4f rotY = new Quat4f(0f, 1f, 0f, 0f);
        float posY = brickSize.y;
        Vector3f offsetPosition = new Vector3f(0.8f, 0f, 0f);
        int currentBrick = 0;
        for (int n = 0; n < towerHeight; n++) {
            for (int i = 0; i < towerRotSize; i++) {
                Vector3f position = new Vector3f();
                position.add(offsetPosition);
                position.add(rotate(rotY, new Vector3f(0.0f, posY, radius)));
                Transform trans = new Transform(new Matrix4f(rotY, position,
                        1.0f));
                makeBrick(currentBrick++, brickMass, dominoInertia, trans);
                rotY.mul(new Quat4f(0f, 1f, 0f, (float) Math.PI
                        / (towerRotSize * 0.5f)));
            }

            posY += brickSize.y * 2.0f;
            rotY.mul(new Quat4f(0f, 1f, 0f, (float) Math.PI
                    / (float) towerRotSize));
        }
    }

    @Override
    public long iteration() {
        long count = 0;

        for (int i = 0; i < repeats; i++) {
            for (int j = 0; j < numBricks; j++) {
                brick[j].setLinearVelocity(zero3f);
                brick[j].setAngularVelocity(zero3f);
                brick[j].setWorldTransform(brickOrigTransform[j]);
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
     * This method creates one individual physical brick.
     */
    public void makeBrick(int i, float mass, Vector3f inertia, Transform trans) {
        brickOrigTransform[i] = trans;
        DefaultMotionState brickMotion = new DefaultMotionState(
                brickOrigTransform[i]);
        RigidBodyConstructionInfo brickRigidBodyCI = new RigidBodyConstructionInfo(
                mass, brickMotion, brickShape, inertia);
        brick[i] = new RigidBody(brickRigidBodyCI);
        world.addRigidBody(brick[i]);
    }

    private Vector3f rotate(Quat4f quat, Vector3f vec) {
        float tmpX, tmpY, tmpZ, tmpW;
        tmpX = (((quat.w * vec.x) + (quat.y * vec.z)) - (quat.z * vec.y));
        tmpY = (((quat.w * vec.y) + (quat.z * vec.x)) - (quat.x * vec.z));
        tmpZ = (((quat.w * vec.z) + (quat.x * vec.y)) - (quat.y * vec.x));
        tmpW = (((quat.x * vec.x) + (quat.y * vec.y)) + (quat.z * vec.z));
        return new Vector3f(
                ((((tmpW * quat.x) + (tmpX * quat.w)) - (tmpY * quat.z)) + (tmpZ * quat.y)),
                ((((tmpW * quat.y) + (tmpY * quat.w)) - (tmpZ * quat.x)) + (tmpX * quat.z)),
                ((((tmpW * quat.z) + (tmpZ * quat.w)) - (tmpX * quat.y)) + (tmpY * quat.x)));
    }
}