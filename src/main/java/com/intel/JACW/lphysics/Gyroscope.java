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
 * Gyroscope mechanics.
 * 
 */
public class Gyroscope extends AbstractTestCase {
    @XMLParameter(defaultValue = "30")
    public int steps;

    @XMLParameter(defaultValue = "5")
    public int cntGyro;

    private CompoundShape gyroShape;
    private RigidBody[] gyro;
    private Transform gyroOrigTransform[];
    private Vector3f gyroAngularVelocity[];
    private DiscreteDynamicsWorld world;

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

        world.setGravity(new Vector3f(0, 0, 0));

        Transform groundTransform = new Transform();
        groundTransform.setIdentity();

        Transform trans1 = new Transform();
        trans1.setIdentity();
        Transform trans2 = new Transform();
        trans2.setIdentity();

        CylinderShapeZ top = new CylinderShapeZ(new Vector3f(1, 1, .125f));
        CapsuleShapeZ pin = new CapsuleShapeZ(.05f, 1.5f);
        top.setMargin(.01f);
        pin.setMargin(.01f);
        gyroShape = new CompoundShape();
        gyroShape.addChildShape(trans1, top);
        gyroShape.addChildShape(trans2, pin);

        Vector3f localInertia = new Vector3f();
        top.calculateLocalInertia(1, localInertia);

        gyroOrigTransform = new Transform[cntGyro];
        gyro = new RigidBody[cntGyro];
        gyroAngularVelocity = new Vector3f[cntGyro];
        for (int i = 0; i < cntGyro; i++) {

            gyroOrigTransform[i] = new Transform(new Matrix4f(new Quat4f(0, 0,
                    0, 1), new Vector3f(-cntGyro * 5 + i * 10, 0, 0), 1.0f));
            DefaultMotionState gyroMotion = new DefaultMotionState(
                    gyroOrigTransform[i]);

            gyro[i] = new RigidBody(1, gyroMotion, gyroShape, localInertia);
            gyro[i].setCenterOfMassTransform(gyroOrigTransform[i]);
            float k = 3f + 7f * i / cntGyro;
            gyroAngularVelocity[i] = new Vector3f(15 * k, 25 * k, 35 * k);
            gyro[i].setAngularVelocity(gyroAngularVelocity[i]);
            gyro[i].setFriction(.5f);
            gyro[i].setDamping(0.3f, .7f);

            world.addRigidBody(gyro[i]);
        }
    }

    @Override
    public long iteration() {
        long count = 0;
        for (int i = 0; i < repeats; i++) {
            long stamp = System.currentTimeMillis();
            for (int j = 0; j < cntGyro; j++) {
                gyro[j].setWorldTransform(gyroOrigTransform[j]);
                gyro[j].setAngularVelocity(gyroAngularVelocity[j]);
            }
            for (int n = 0; n < steps; n++) {
                world.stepSimulation(0.1f, 50, 0.003f); // world stepping 0.1
                                                        // sec, virtual timeStep
                                                        // is 0.003 sec,
                                                        // expected simulated
                                                        // steps count < 50
            }
            count++;
        }
        return count;
    }
}