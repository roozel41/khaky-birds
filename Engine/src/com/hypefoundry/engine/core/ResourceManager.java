/**
 * 
 */
package com.hypefoundry.engine.core;

import java.util.*;

import com.hypefoundry.engine.game.Game;

/**
 * Resources manager interface.
 * 
 * @author Paksas
 *
 */
public class ResourceManager 
{
	private Game 			m_game;
	
	List< Resource >		m_resources;
	boolean					m_isActive;				
	
	/**
	 * Constructor.
	 * 
	 * @param game
	 */
	public ResourceManager( Game game )
	{
		m_game = game;
		
		m_resources = new ArrayList< Resource >();
		m_isActive = false;
	}
	
	/**
	 * Returns a resource of the specified type.
	 * 
	 * @param type
	 * @param assetPath
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public < T extends Resource > T getResource( Class< T > type, String assetPath )
	{
		// try finding an existing resource that matches the name and the type
		int count = m_resources.size();
		for ( int i = 0; i < count; ++i )
		{
			Resource res = m_resources.get( i );
			String resAssetPath = res.getAssetPath();
			if ( type.isInstance( res ) && resAssetPath.equals( assetPath ) )
			{
				// we found it
				return (T)res;
			}
		}
		
		// the resource wasn't found - instantiate it
		T newResource = null;
		try 
		{
			newResource = type.newInstance();
		} 
		catch (InstantiationException e) 
		{
			return null;
		} 
		catch (IllegalAccessException e) 
		{
			return null;
		}
		
		if ( newResource != null )
		{
			newResource.initialize( m_game, this, assetPath );
			if ( m_isActive )
			{
				// load the resource, if the resources manager is active
				newResource.load();
			}
			
			m_resources.add( newResource );
		}
		return newResource;
	}

	/**
	 * Loads all registered resources after they have been unloaded.
	 */
	public void loadResources() 
	{
		m_isActive = true;
		int count = m_resources.size();
		for ( int i = 0; i < count; ++i )
		{
			Resource res = m_resources.get( i );
			res.load();
		}
		
	}

	/**
	 * Releases all registered resources, keeping track of them however
	 * for future reloads.
	 */
	public void releaseResources() 
	{
		int count = m_resources.size();
		for ( int i = 0; i < count; ++i )
		{
			Resource res = m_resources.get( i );
			res.release();
		}
		m_isActive = false;
	}
}
