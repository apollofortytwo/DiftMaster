package com.game.drift;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * To synchronize the location and orientation between a ModelInstance and btRigidBody
 * @author ApolloFortyTwo
 *
 */
class MyMotionState extends btMotionState {
	public Matrix4 transform = new Matrix4();

	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(transform);
	}

	@Override
	public void setWorldTransform(Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
}