package com.game.drift;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class DriftMaster extends ApplicationAdapter {

	PerspectiveCamera cam;
	FirstPersonCameraController camController;
	ModelBatch modelBatch;
	Environment environment;
	Array<GameObject> instances;
	GameObject player;
	
	/*
	 * I have no idea what this does, but it add's collision to the game
	 * ¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯
	 */
	btCollisionConfiguration collisionConfig;
	btDispatcher dispatcher;
	btBroadphaseInterface broadphase;
	// I will be adding the game objects to the world to have physics properties
	btDynamicsWorld dynamicsWorld;
	btConstraintSolver constraintSolver;

	
	@Override
	public void create() {
		// starts physics engine
		Bullet.init();

		modelBatch = new ModelBatch();
		environment = new Environment();

		// let there be LIGHT!!!
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// sets the camera at the origin, and 3 units back
		cam.position.set(0f, 0f, 3f);
		cam.lookAt(0f, 0f, 0f);

		/*
		 * what the furtherest and closest objects that can rendered are
		 */
		cam.near = 0.1f;
		cam.far = 300f;

		/*
		 * FirstPersonCameraController is a preset controller I use for inputs
		 * Tells LibGDX that I want the inputs to be interpreted via
		 * FirstPersonCameraController
		 */
		camController = new FirstPersonCameraController(cam);
		Gdx.input.setInputProcessor(camController);

		/*
		 * Don't ask about what these classes do, I don't know.
		 * ¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯¯\_(ツ)_/¯
		 */
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -10f, 0));

		/*
		 * I'll be adding the objects I create to this array so I can have
		 * access to render them later on
		 */
		instances = new Array<GameObject>();

		GameObject floor = new GameObject.Constructor(Color.DARK_GRAY, // What
																		// the
																		// prisms
																		// colour
																		// will
																		// be
				new Vector3(0f, -5f, 0f), // location that the prism will appear
											// (x,y,z)
				100f, 1f, 100f, 0) // the size of the prism (width, height
									// depth)
						.construct(); // this method returns the actual
										// GameObject

		instances.add(floor); // add the created Game Object to the array
		dynamicsWorld.addRigidBody(floor.body); // add the object to the physics
												// world

		player = new GameObject.Constructor(Color.BLACK, new Vector3(0f,7f,0f), 2f, 3f, 3f, 1).construct();
		instances.add(player);
		dynamicsWorld.addRigidBody(player.body);
		
		
		/*
		 * lets make a bunch of Game objects and add them to the physics world
		 * they will spawn all in the same x,z position, but have a vertical
		 * displacement
		 */
//		for (int i = 0; i < 300; i++) {
//			GameObject ob = new GameObject.Constructor(
//					Color.RED, 
//					new Vector3(0f, (i * 5) + 10f, 0f), 
//					5f, 1f, 2f, 1)
//					.construct();
//			instances.add(ob);
//			dynamicsWorld.addRigidBody(ob.body);
//		}

	}

	/**
	 * does something when 1 second has passed
	 * 
	 * @param f
	 *            duration of time between the last time this method was called
	 */
	float dt = 0;
	long frames = 0;
	public void tick(float f) {
		dt += f;
		frames++;
		
		if (dt >= 1) {
			System.out.println("FPS: " + frames + " || Block Count: " + instances.size);
			dt = 0;
			frames = 0;

//			for (int i = 0; i < 10; i++) {
//				GameObject ob = new GameObject.Constructor(Color.RED, new Vector3(0f, (i * 5) + 10f, 0f), 5f, 1f, 2f, 1)
//						.construct();
//				instances.add(ob);
//				dynamicsWorld.addRigidBody(ob.body);
//			}
		}
	}
	Matrix4 tmpMatrix;
	Vector3 position;


	@Override
	public void render() {

		/*
		 * general setup for the window, and how LibGDX will render things
		 */
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f); // sets the colour to the
													// worlds background to grey
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		dynamicsWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 5, 1f / 60f);
		
		
		cam.transform(player.body.getWorldTransform());
		cam.update();
		camController.update();

		modelBatch.begin(cam);
		modelBatch.render(instances, environment); // render the game objects
													// and in their environment
		modelBatch.end();

		tick(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		instances.clear();

		dynamicsWorld.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();

		modelBatch.dispose();

	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void resize(int width, int height) {
	}
}
