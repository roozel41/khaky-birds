/**
 * 
 */
package com.hypefoundry.engine.physics;

import java.util.*;

import android.util.Log;

import com.hypefoundry.engine.game.Entity;
import com.hypefoundry.engine.game.Updatable;
import com.hypefoundry.engine.game.World;
import com.hypefoundry.engine.game.WorldView;
import com.hypefoundry.engine.renderer2D.EntityVisual;
import com.hypefoundry.engine.util.GenericFactory;
import com.hypefoundry.engine.util.SpatialGrid2D;

/**
 * This world view runs the physics simulation.
 * 
 * @author Paksas
 *
 */
public class PhysicsView extends GenericFactory< Entity, PhysicalBody > implements WorldView, Updatable
{
	private float								m_cellSize = 2.0f;
	private final int							MAX_ENTITIES = 512;		// TODO: config
	private SpatialGrid2D< PhysicalBody >		m_bodiesGrid = null;
	private List< PhysicalBody > 				m_bodies;
	
	
	/**
	 * Constructor.
	 */
	public PhysicsView( float cellSize )
	{
		m_cellSize = cellSize;
		m_bodies = new ArrayList< PhysicalBody >( MAX_ENTITIES );
	}
	
	@Override
	public void update( float deltaTime )
	{	
		// run physics simulation
		simulateForces( deltaTime );
		
		// update the positions of dynamic objects in the grid AFTER the simulation changed them
		m_bodiesGrid.update();
		
		// resolve collisions
		resolveCollisions();
	}
	
	@Override
	public void onAttached( World world ) 
	{
		m_bodiesGrid = new SpatialGrid2D< PhysicalBody >( world.getWidth(), world.getHeight(), m_cellSize );
	}

	@Override
	public void onDetached( World world ) 
	{
		m_bodiesGrid = null;
	}

	@Override
	public void onEntityAdded( Entity entity ) 
	{
		PhysicalBody body = findBodyFor( entity );
		if ( body != null )
		{
			// the entity already has a body created
			return;
		}
		
		try
		{
			body = create( entity );
			
			// add the visual to the render list
			if ( entity.hasAspect( DynamicObject.class ) )
			{
				m_bodiesGrid.insertDynamicObject( body );
			}
			else
			{
				m_bodiesGrid.insertStaticObject( body );
			}
			m_bodies.add( body );
		}
		catch( IndexOutOfBoundsException e )
		{
			// ups... - no visual representation defined - notify about it
			Log.d( "PhysicsView", "Physical representation not defined for entity '" + entity.getClass().getName() + "'" );
		}
	}

	@Override
	public void onEntityRemoved( Entity entity ) 
	{
		PhysicalBody body = findBodyFor( entity );
		if ( body != null )
		{
			m_bodiesGrid.removeObject( body );
			m_bodies.remove( body );
		}
	}
	
	/**
	 * Looks for a registered body for the specified entity
	 * 
	 * @param entity
	 * @return
	 */
	private PhysicalBody findBodyFor( Entity entity )
	{
		for ( PhysicalBody body : m_bodies )
		{
			if ( body.isBodyOf( entity ) )
			{
				return body;
			}
		}
		
		return null;
	}
	
	/**
	 * Resolve the collisions.
	 */
	private void resolveCollisions() 
	{
		// go through all the entities and test 
		// their mutual overlap O(n2)
		int count = m_bodies.size();
		for( int i = 0; i < count; ++i )
		{
			PhysicalBody body = m_bodies.get(i);
			List< PhysicalBody > collidingBodies = m_bodiesGrid.getPotentialColliders( body );
			
			int collidersCount = collidingBodies.size();
			for ( int j = 0; j < collidersCount; ++j )
			{
				PhysicalBody collider = collidingBodies.get(j);
				if ( body.doesOverlap( collider ) )
				{
					body.onCollision( collider );
				}
			}
		}
	}
	
	/**
	 * Simulates the forces that apply to particular physical bodies.
	 * 
	 * @param deltaTime
	 */
	private void simulateForces( float deltaTime )
	{
		for( PhysicalBody body : m_bodies )
		{
			body.simulate( deltaTime );
		}
	}

}