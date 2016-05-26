package com.intel.JACW.lphysics;

import java.util.HashMap;
import java.util.Map;

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
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * Collision of 2 rag-dolls
 * 
 */
public class Ragdoll extends AbstractTestCase {
    @XMLParameter(defaultValue = "30")
    public int steps;
    @XMLParameter(defaultValue = "-10")
    public float gravityY;

    private CollisionShape groundShape;

    private RigidBody groundRigidBody;
    private RagDollShape dollOne;
    private RagDollShape dollTwo;

    private DiscreteDynamicsWorld world;

    @Override
    public void init(RunConfig config) throws InvalidTestFormatException {
        super.init(config);
        initWorld();
    }

    protected void initWorld() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
                collisionConfiguration);

        world.setGravity(new Vector3f(0, -1, 0));

        groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);

        DefaultMotionState groundMotionState = new DefaultMotionState(
                new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1),
                        new Vector3f(0, -1, 0), 1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(
                0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        groundRigidBody = new RigidBody(groundRigidBodyCI);

        world.addRigidBody(groundRigidBody);

        dollOne = new RagDollShape(world, new Vector3f(0.0f, 30.0f, 0.0f), 5.0f);
        dollTwo = new RagDollShape(world, new Vector3f(10.0f, 30.0f, 0.0f),
                5.0f);

        dollOne.setLinearSpeed(new Vector3f(30f, 0f, 0f));
        dollTwo.setLinearSpeed(new Vector3f(-30f, 0f, 0f));

        dollOne.saveState();
        dollTwo.saveState();
    }

    @Override
    public long iteration() {
        long count = 0;
        for (int i = 0; i < repeats; i++) {
            initWorld();
            dollOne.restoreState();
            dollTwo.restoreState();
            for (int n = 0; n < steps; n++) {
                world.stepSimulation(0.1f, 5, 0.03f); // world stepping 0.1
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

class RagDollShape {
    enum BodyPart {
        MAIN, SPINE, HEAD,

        LEFT_UPPER_LEG, LEFT_LOWER_LEG,

        RIGHT_UPPER_LEG, RIGHT_LOWER_LEG,

        LEFT_UPPER_ARM, LEFT_LOWER_ARM,

        RIGHT_UPPER_ARM, RIGHT_LOWER_ARM,

    }

    enum Joint {
        MAIN_SPINE, SPINE_HEAD,

        LEFT_HIP, LEFT_KNEE,

        RIGHT_HIP, RIGHT_KNEE,

        LEFT_SHOULDER, LEFT_ELBOW,

        RIGHT_SHOULDER, RIGHT_ELBOW,

    }

    private static final float M_PI_2 = (float) (Math.PI / 2);
    private static final float M_PI = (float) Math.PI;
    private static final float M_PI_4 = (float) (Math.PI / 4);

    DynamicsWorld ownerWorld;
    Map<BodyPart, CollisionShape> shapes;
    Map<BodyPart, RigidBody> bodies;
    Map<Joint, TypedConstraint> joints;

    private RigidBody localCreateRigidBody(float mass,
            Transform startTransform, CollisionShape shape) {

        Vector3f localInertia = new Vector3f(0f, 0f, 0f);
        shape.calculateLocalInertia(mass, localInertia);

        DefaultMotionState myMotionState = new DefaultMotionState(
                startTransform);

        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
                myMotionState, shape, localInertia);
        RigidBody body = new RigidBody(rbInfo);

        body.setRestitution(2f);
        body.setDamping(.01f, .3f);

        ownerWorld.addRigidBody(body);
        return body;
    }

    public void setLinearSpeed(Vector3f vec) {
        RigidBody main = bodies.get(BodyPart.MAIN);
        main.setLinearVelocity(vec);
    }

    HashMap<RigidBody, Transform> origTransformMap;
    HashMap<RigidBody, Vector3f> origLinearVelocityMap, origAngularVelocityMap;

    Vector3f getLocation() {
        Vector3f tmp = new Vector3f();
        bodies.get(BodyPart.MAIN).getCenterOfMassPosition(tmp);
        return tmp;
    }

    public void saveState() {
        origTransformMap = new HashMap<>();
        origLinearVelocityMap = new HashMap<>();
        origAngularVelocityMap = new HashMap<>();

        for (RigidBody body : bodies.values()) {
            {
                Transform tmp = new Transform();
                body.getWorldTransform(tmp);
                origTransformMap.put(body, tmp);
            }
            {
                Vector3f tmp = new Vector3f();
                body.getLinearVelocity(tmp);
                origLinearVelocityMap.put(body, tmp);
            }
            {
                Vector3f tmp = new Vector3f();
                body.getAngularVelocity(tmp);
                origAngularVelocityMap.put(body, tmp);
            }
        }
    }

    public void restoreState() {
        for (RigidBody body : bodies.values()) {
            body.setWorldTransform(origTransformMap.get(body));
            body.setLinearVelocity(origLinearVelocityMap.get(body));
            body.setAngularVelocity(origAngularVelocityMap.get(body));
        }
    }

    public RagDollShape(DynamicsWorld ownerWorld, Vector3f positionOffset,
            float scale) {
        this.ownerWorld = ownerWorld;
        shapes = new HashMap<>();
        bodies = new HashMap<>();
        joints = new HashMap<>();

        setupGeometry(scale);
        setupRigidBodies(positionOffset, scale);
        setupConstraints(scale);
    }

    private void setupGeometry(float scale) {
        shapes.put(BodyPart.MAIN,
                new CapsuleShape(0.15f * scale, 0.20f * scale));
        shapes.put(BodyPart.SPINE, new CapsuleShape(0.15f * scale,
                0.28f * scale));
        shapes.put(BodyPart.HEAD,
                new CapsuleShape(0.10f * scale, 0.05f * scale));
        shapes.put(BodyPart.LEFT_UPPER_LEG, new CapsuleShape(0.07f * scale,
                0.45f * scale));
        shapes.put(BodyPart.LEFT_LOWER_LEG, new CapsuleShape(0.05f * scale,
                0.37f * scale));
        shapes.put(BodyPart.RIGHT_UPPER_LEG, new CapsuleShape(0.07f * scale,
                0.45f * scale));
        shapes.put(BodyPart.RIGHT_LOWER_LEG, new CapsuleShape(0.05f * scale,
                0.37f * scale));
        shapes.put(BodyPart.LEFT_UPPER_ARM, new CapsuleShape(0.05f * scale,
                0.33f * scale));
        shapes.put(BodyPart.LEFT_LOWER_ARM, new CapsuleShape(0.04f * scale,
                0.25f * scale));
        shapes.put(BodyPart.RIGHT_UPPER_ARM, new CapsuleShape(0.05f * scale,
                0.33f * scale));
        shapes.put(BodyPart.RIGHT_LOWER_ARM, new CapsuleShape(0.04f * scale,
                0.25f * scale));
    }

    private void setupRigidBodies(Vector3f positionOffset, float scale) {
        Transform offset = new Transform();
        offset.setIdentity();
        offset.origin.set(positionOffset);

        createBodyPart(BodyPart.MAIN, offset, scale, new Vector3f(0.0f, 1.0f,
                0.0f));
        createBodyPart(BodyPart.SPINE, offset, scale, new Vector3f(0.0f, 1.2f,
                0.0f));
        createBodyPart(BodyPart.HEAD, offset, scale, new Vector3f(0.0f, 1.6f,
                0.0f));
        createBodyPart(BodyPart.LEFT_UPPER_LEG, offset, scale, new Vector3f(
                -0.18f, 0.65f, 0.0f));
        createBodyPart(BodyPart.LEFT_LOWER_LEG, offset, scale, new Vector3f(
                -0.18f, 0.2f, 0.0f));
        createBodyPart(BodyPart.RIGHT_UPPER_LEG, offset, scale, new Vector3f(
                0.18f, 0.65f, 0.0f));
        createBodyPart(BodyPart.RIGHT_LOWER_LEG, offset, scale, new Vector3f(
                0.18f, 0.2f, 0.0f));
        createBodyPart(BodyPart.LEFT_UPPER_ARM, offset, scale, new Vector3f(
                -0.35f, 1.45f, 0.0f), new Vector3f(M_PI_2, 0.0f, 0.0f));
        createBodyPart(BodyPart.LEFT_LOWER_ARM, offset, scale, new Vector3f(
                -0.7f, 1.45f, 0.0f), new Vector3f(M_PI_2, 0.0f, 0.0f));
        createBodyPart(BodyPart.RIGHT_UPPER_ARM, offset, scale, new Vector3f(
                0.35f, 1.45f, 0.0f), new Vector3f(-M_PI_2, 0.0f, 0.0f));
        createBodyPart(BodyPart.RIGHT_LOWER_ARM, offset, scale, new Vector3f(
                0.7f, 1.45f, 0.0f), new Vector3f(-M_PI_2, 0.0f, 0.0f));

        // Setup some damping on the m_bodies
        for (RigidBody body : bodies.values()) {
            body.setDeactivationTime(0);
            body.setSleepingThresholds(0, 0);
        }
    }

    private static final void rotate(Transform tr, float z, float y, float x) {
        tr.basis.rotZ(z);
        tr.basis.rotY(y);
        tr.basis.rotX(x);
    }

    private void setupConstraints(float scale) {
        HingeConstraint hingeC;
        ConeTwistConstraint coneC;

        Transform localA = new Transform();
        Transform localB = new Transform();

        localA.setIdentity();
        localA.origin.set(0.0f, scale * 0.15f, 0.0f);
        rotate(localA, 0.0f, M_PI_2, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.15f, 0.0f);
        rotate(localB, 0.0f, M_PI_2, 0.0f);
        hingeC = new HingeConstraint(bodies.get(BodyPart.MAIN),
                bodies.get(BodyPart.SPINE), localA, localB);
        hingeC.setLimit(-M_PI_4, M_PI_2);
        joints.put(Joint.MAIN_SPINE, hingeC);
        ownerWorld.addConstraint(hingeC, true);

        localA.setIdentity();
        localA.origin.set(0.0f, scale * 0.30f, 0.0f);
        rotate(localA, 0.0f, 0.0f, M_PI_2);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.14f, 0.0f);
        rotate(localB, 0.0f, 0.0f, M_PI_2);
        coneC = new ConeTwistConstraint(bodies.get(BodyPart.SPINE),
                bodies.get(BodyPart.HEAD), localA, localB);
        coneC.setLimit(M_PI_4, M_PI_4, M_PI_2);
        joints.put(Joint.SPINE_HEAD, coneC);
        ownerWorld.addConstraint(joints.get(Joint.SPINE_HEAD), true);

        localA.setIdentity();
        localA.origin.set(scale * -0.18f, scale * -0.10f, 0.0f);
        rotate(localA, 0.0f, 0.0f, -M_PI_4 * 5);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * 0.225f, 0.0f);
        rotate(localB, 0.0f, 0.0f, -M_PI_4 * 5);
        coneC = new ConeTwistConstraint(bodies.get(BodyPart.MAIN),
                bodies.get(BodyPart.LEFT_UPPER_LEG), localA, localB);
        coneC.setLimit(M_PI_4, M_PI_4, 0);
        joints.put(Joint.LEFT_HIP, coneC);
        ownerWorld.addConstraint(joints.get(Joint.LEFT_HIP), true);

        localA.setIdentity();
        localA.origin.set(0.0f, scale * -0.225f, 0.0f);
        rotate(localA, 0.0f, M_PI_2, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * 0.185f, 0.0f);
        rotate(localB, 0.0f, M_PI_2, 0.0f);

        hingeC = new HingeConstraint(bodies.get(BodyPart.LEFT_UPPER_LEG),
                bodies.get(BodyPart.LEFT_LOWER_LEG), localA, localB);
        hingeC.setLimit(0, M_PI_2);
        joints.put(Joint.LEFT_KNEE, hingeC);
        ownerWorld.addConstraint(joints.get(Joint.LEFT_KNEE), true);

        localA.setIdentity();
        localA.origin.set(scale * 0.18f, scale * -0.10f, 0.0f);
        rotate(localA, 0.0f, 0.0f, M_PI_4);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * 0.225f, 0.0f);
        rotate(localB, 0.0f, 0.0f, M_PI_4);
        coneC = new ConeTwistConstraint(bodies.get(BodyPart.MAIN),
                bodies.get(BodyPart.RIGHT_UPPER_LEG), localA, localB);
        coneC.setLimit(M_PI_4, M_PI_4, 0);
        joints.put(Joint.RIGHT_HIP, coneC);
        ownerWorld.addConstraint(joints.get(Joint.RIGHT_HIP), true);

        localA.setIdentity();
        localA.origin.set(0.0f, scale * -0.225f, 0.0f);
        rotate(localA, 0.0f, M_PI_2, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * 0.185f, 0.0f);
        rotate(localB, 0.0f, M_PI_2, 0.0f);
        hingeC = new HingeConstraint(bodies.get(BodyPart.RIGHT_UPPER_LEG),
                bodies.get(BodyPart.RIGHT_LOWER_LEG), localA, localB);
        hingeC.setLimit(0, M_PI_2);
        joints.put(Joint.RIGHT_KNEE, hingeC);
        ownerWorld.addConstraint(joints.get(Joint.RIGHT_KNEE), true);

        localA.setIdentity();
        localA.origin.set(scale * -0.2f, scale * 0.15f, 0.0f);
        rotate(localA, 0.0f, 0.0f, M_PI);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.18f, 0.0f);
        rotate(localB, 0.0f, 0.0f, M_PI_2);
        coneC = new ConeTwistConstraint(bodies.get(BodyPart.SPINE),
                bodies.get(BodyPart.LEFT_UPPER_ARM), localA, localB);
        coneC.setLimit(M_PI_2, M_PI_2, 0);
        joints.put(Joint.LEFT_SHOULDER, coneC);
        ownerWorld.addConstraint(joints.get(Joint.LEFT_SHOULDER), true);

        localA.setIdentity();
        localA.origin.set(0.0f, scale * 0.18f, 0.0f);
        rotate(localA, 0.0f, M_PI_2, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.14f, 0.0f);
        rotate(localB, 0.0f, M_PI_2, 0.0f);
        hingeC = new HingeConstraint(bodies.get(BodyPart.LEFT_UPPER_ARM),
                bodies.get(BodyPart.LEFT_LOWER_ARM), localA, localB);
        hingeC.setLimit(-M_PI_2, 0);
        joints.put(Joint.LEFT_ELBOW, hingeC);
        ownerWorld.addConstraint(joints.get(Joint.LEFT_ELBOW), true);

        localA.setIdentity();
        localA.origin.set(scale * 0.2f, scale * 0.15f, 0.0f);
        rotate(localA, 0.0f, 0.0f, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.18f, 0.0f);
        rotate(localB, 0.0f, 0.0f, M_PI_2);

        coneC = new ConeTwistConstraint(bodies.get(BodyPart.SPINE),
                bodies.get(BodyPart.RIGHT_UPPER_ARM), localA, localB);
        coneC.setLimit(M_PI_2, M_PI_2, 0);
        joints.put(Joint.RIGHT_SHOULDER, coneC);
        ownerWorld.addConstraint(joints.get(Joint.RIGHT_SHOULDER), true);

        localA.setIdentity();
        localA.origin.set(0.0f, scale * 0.18f, 0.0f);
        rotate(localA, 0.0f, M_PI_2, 0.0f);
        localB.setIdentity();
        localB.origin.set(0.0f, scale * -0.14f, 0.0f);
        rotate(localB, 0.0f, M_PI_2, 0.0f);

        hingeC = new HingeConstraint(bodies.get(BodyPart.RIGHT_UPPER_ARM),
                bodies.get(BodyPart.RIGHT_LOWER_ARM), localA, localB);
        hingeC.setLimit(-M_PI_2, 0);
        joints.put(Joint.RIGHT_ELBOW, hingeC);
        ownerWorld.addConstraint(joints.get(Joint.RIGHT_ELBOW), true);
    }

    private void createBodyPart(BodyPart bodyPart, Transform offset,
            float scale, Vector3f origin) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(scale * origin.x, scale * origin.y, scale
                * origin.z);
        transform.mul(offset);
        bodies.put(bodyPart,
                localCreateRigidBody(1.0f, transform, shapes.get(bodyPart)));
    }

    private void createBodyPart(BodyPart bodyPart, Transform offset,
            float scale, Vector3f origin, Vector3f angles) {
        Transform transform = new Transform();
        transform.setIdentity();
        transform.origin.set(scale * origin.x, scale * origin.y, scale
                * origin.z);
        transform.basis.rotX(angles.x);
        transform.basis.rotY(angles.y);
        transform.basis.rotZ(angles.z);
        transform.mul(offset);
        bodies.put(bodyPart,
                localCreateRigidBody(1.0f, transform, shapes.get(bodyPart)));
    }

}