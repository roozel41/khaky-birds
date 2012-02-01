/**
 * 
 */
package com.hypefoundry.kabloons.entities.background;

import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.engine.world.Entity;

/**
 * A simple entity with no logic that just plays an animation.
 * 
 * @author Paksas
 *
 */
public class AnimatedBackground extends Entity 
{
	public String		m_path;
	

	@Override
	public void onLoad( DataLoader loader ) 
	{
		m_path = loader.getStringValue( "path" );
	}
}
