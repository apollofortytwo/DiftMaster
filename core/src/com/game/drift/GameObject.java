package com.game.drift;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

class GameObject extends ModelInstance implements Disposable {

	static ModelBuilder mb = new ModelBuilder();
	btRigidBodyConstructionInfo constructionInfo;
	btRigidBody body;
	public MyMotionState motionState;
	BoundingBox bounds;
	Vector3 position = new Vector3();
	
	public GameObject(Model model, btRigidBodyConstructionInfo constructionInfo, Vector3 position) {
		super(model);

		bounds = new BoundingBox();
		this.calculateBoundingBox(bounds);

		this.constructionInfo = constructionInfo;
		this.body = new btRigidBody(constructionInfo);
		
		motionState = new MyMotionState();
		
		this.transform.setTranslation(position);
		motionState.transform = this.transform;
		
		this.body.setMotionState(motionState);
		this.body.proceedToTransform(this.transform);
		



	}

	@Override
	public void dispose() {

	}

	/**
	 * because the GameObject requires a Model as a parameter, and we can't create a model 
	 * without knowing it's dimensions, and position. we have a constructor class that creates
	 * a model based on properties, and uses that model to create and return GameObject 
	 * @author ApolloFortyTwo
	 *
	 */
	static class Constructor {
		Vector3 localInertia = new Vector3();
		Model model;
		btCollisionShape shape;
		btRigidBodyConstructionInfo constructionInfo;
		Vector3 position;

		/**
		 * 
		 * @param color
		 * @param position
		 * @param width
		 * @param height
		 * @param depth
		 * @param mass
		 */
		public Constructor(Color color, Vector3 position, float width, float height, float depth, float mass) {
			model = mb.createBox(
					width, height, depth, 
					new Material(ColorAttribute.createDiffuse(color)),
					Usage.Position | Usage.Normal);

			this.position = position;
			shape = new btBoxShape(new Vector3(width/2, height/2, depth/2));

			if (mass > 0f)
				shape.calculateLocalInertia(mass, localInertia);
			else
				localInertia.set(0, 0, 0);
			constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		}

		/**
		 * returns a GameObject with the properties defined in the Constructor
		 */
		public GameObject construct() {
			return new GameObject(model, constructionInfo, position);

		}
	}



}