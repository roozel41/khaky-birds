/**
 * 
 */
package com.hypefoundry.engine.core;

import javax.microedition.khronos.opengles.GL10;

import com.hypefoundry.engine.math.Vector3;

/**
 * A 2D camera with an orthogonal projection.
 * 
 * @author paksas
 *
 */
public interface Camera2D 
{
	/**
	 * Positions the camera in the world before rendering.
	 */
	void setViewportAndMatrices();
	
	/**
	 * Translates the touch position to the world coordinates
	 * 
	 * @param touch
	 */
	void touchToWorld( Vector3 touch );
}