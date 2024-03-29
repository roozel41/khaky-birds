package com.hypefoundry.engine.world;

import java.util.*;

import com.hypefoundry.engine.core.Resource;
import com.hypefoundry.engine.core.ResourceManager;
import com.hypefoundry.engine.core.Texture;
import com.hypefoundry.engine.game.Updatable;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.engine.util.serialization.DataSaver;
import com.hypefoundry.engine.world.serialization.EntityFactory;

import android.util.Log;


/**
 * Represents the game world.
 * 
 * @author paksas
 *
 */
public class World implements Updatable
{	
	// ------------------------------------------------------------------------
	//	Entity factory data
	// ------------------------------------------------------------------------
	
	class EntityFactoryData
	{
		Class< ? extends Entity >		m_type;
		EntityFactory					m_factory;
		
		EntityFactoryData( Class< ? extends Entity > type, EntityFactory factory )
		{
			m_type = type;
			m_factory = factory;
		}
	}
	
	// ------------------------------------------------------------------------
	//	Resource factory definition and data
	// ------------------------------------------------------------------------
	
	class ResourceFactory
	{
		Class< ? extends Resource >		m_type;
		
		ResourceFactory( Class< ? extends Resource > type )
		{
			m_type = type;
		}
		
		Resource load( ResourceManager resMgr, String path )
		{
			return resMgr.getResource( m_type, path );
		}
	}
	
	// define cachable resources here
	private ResourceFactory[] 		m_resourceFactories = {
			new ResourceFactory( Texture.class ),
		};
	
	// ------------------------------------------------------------------------
	// Members
	// ------------------------------------------------------------------------
	
	private float						m_width;
	private float						m_height;
	private List< Entity >				m_entities;
	private List< Entity >				m_entitiesToAdd;
	private List< Entity >				m_entitiesToRemove;
	private List< WorldView >			m_views;
	private List< EntityFactoryData >	m_entityFactories;
	
	private Vector3 					m_entityQueryTargetPos		= new Vector3();
	
	// ------------------------------------------------------------------------
	// API
	// ------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public World()
	{
		m_width = 0;
		m_height = 0;
		
		m_entities = new ArrayList< Entity >();
		m_entitiesToAdd = new ArrayList< Entity >();
		m_entitiesToRemove = new ArrayList< Entity >();
		m_views = new ArrayList< WorldView >();
		m_entityFactories = new ArrayList< EntityFactoryData >();
	}
	
	/**
	 * Allows to manually define the size of the world.
	 * 
	 * CAUTION: Call it as soon as possible before you attach any views - they
	 * may be defining some internals with respect to the world size.
	 * 
	 * @param width			world width
	 * @param height		world height
	 */
	public void setSize( float	width, float height )
	{
		m_width = width;
		m_height = height;
	}
	
	/**
	 * Returns the width of the world.
	 * 
	 * @return
	 */
	public float getWidth()
	{
		return m_width;
	}
	
	/**
	 * Returns the height of the world.
	 * 
	 * @return
	 */
	public float getHeight()
	{
		return m_height;
	}
	
	/**
	 * Adds a new entity to the world.
	 * 
	 * @param entity
	 */
	public void addEntity( Entity entity )
	{
		m_entitiesToRemove.remove( entity );
		m_entitiesToAdd.add( entity );
	}
	
	/**
	 * Removes the entity from the world
	 * 
	 * @param entity
	 */
	public void removeEntity( Entity entity )
	{
		m_entitiesToRemove.add( entity );
		m_entitiesToAdd.remove( entity );
	}
	
	/**
	 * Actual attachment of an entity to the world.
	 * 
	 * @param entity
	 */
	private void attachEntity( Entity entity )
	{
		if ( m_entities.indexOf( entity ) < 0 )
		{
			m_entities.add( entity );
			
			// inform the entity itself
			entity.onAddedToWorld( this );
		}
		
		// notify the views
		int count = m_views.size();
		for( int i = 0; i < count; ++i )
		{
			m_views.get(i).onEntityAdded( entity );
		}
	}
	
	/**
	 * Actual detachment of an entity to the world.
	 * 
	 * @param entity
	 */
	public void detachEntity( Entity entity )
	{		
		if ( m_entities.remove( entity ) )
		{
			// inform the entity itself
			entity.onRemovedFromWorld( this );
			
			// notify the views
			int count = m_views.size();
			for( int i = 0; i < count; ++i )
			{
				m_views.get(i).onEntityRemoved( entity );
			}
		}
	}
	
	/**
	 * Attaches a new view to the world.
	 * 
	 * @param newView
	 */
	public void attachView( WorldView view )
	{
		int count = m_views.size();
		for( int i = 0; i < count; ++i )
		{
			WorldView v = m_views.get(i);
			if ( v.equals( view ) )
			{
				return;
			}
		}
		m_views.add( view );

		// inform the view that it's been attached to a world
		view.onAttached( this );
		
		// inform the view about all present entities
		count = m_entities.size();
		for( int i = 0; i < count; ++i )
		{
			view.onEntityAdded( m_entities.get(i) );
		}
	}
	
	/**
	 * Attaches a new view to the world.
	 * 
	 * @param newView
	 */
	public void detachView( WorldView view )
	{
		if ( view == null )
		{
			return;
		}
		
		// remove all entities from the view
		int count = m_entities.size();
		for( int i = 0; i < count; ++i )
		{
			view.onEntityRemoved( m_entities.get(i) );
		}
		
		// inform the view that it's been detached from a world
		view.onDetached( this );
		
		// remove the view
		m_views.remove( view );
	}
	
	/**
	 * Executes an operation on all registered entities.
	 * 
	 * @param operation
	 */
	public void executeOperation( EntityOperation operation )
	{
		int count = m_entities.size();
		for( int i = 0; i < count; ++i )
		{
			operation.visit( m_entities.get(i) );
		}
	}
	
	/**
	 * Updates the state of the world.
	 * 
	 * @param deltaTime
	 */
	@Override
	public void update( float deltaTime )
	{	
		// execute world attachment & detachment - detach first to save
		// memory
		int count = m_entitiesToRemove.size();
		for( int i = 0; i < count; ++i )
		{
			detachEntity( m_entitiesToRemove.get(i) );
		}
		m_entitiesToRemove.clear();
		
		count = m_entitiesToAdd.size();
		for( int i = 0; i < count; ++i )
		{
			attachEntity( m_entitiesToAdd.get(i) );
		}
		m_entitiesToAdd.clear();
		
		// process the entity events
		count = m_entities.size();
		for( int i = 0; i < count; ++i )
		{
			m_entities.get(i).processEvents();
		}
	}
	
	// ------------------------------------------------------------------------
	// Utilities
	// ------------------------------------------------------------------------

	/**
	 * Looks for the first entity of the specified type
	 * 
	 * @param entityType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Entity findEntity( Class entityType ) 
	{
		int count = m_entities.size();
		for( int i = 0; i < count; ++i )
		{
			Entity entity = m_entities.get(i);
			if ( entityType.isInstance( entity ) )
			{
				return entity;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Looks for the nearest entity of the specified type.
	 * 
	 * @param entityType
	 * @param range
	 * @param sourcePos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public < T > T findNearestEntity( Class< T > entityType, float range, Vector3 sourcePos ) 
	{
		int count 			= m_entities.size();
		if ( count == 0 )
		{
			return null;
		}
		
		float closestDistSq 	= range*range;
		int closestEntIdx 		= -1;

		for( int i = 0; i < count; ++i )
		{
			Entity entity = m_entities.get(i);
			if ( entityType.isInstance( entity ) )
			{
				entity.getWorldBounds().getNearestPoint( sourcePos, m_entityQueryTargetPos );
				float distanceSq = sourcePos.distSq2D( m_entityQueryTargetPos );
				if ( distanceSq <= closestDistSq )
				{
					closestDistSq = distanceSq;
					closestEntIdx = i;	
				}
			}
		}
		
		if ( closestEntIdx >= 0 )
		{
			return (T)m_entities.get( closestEntIdx );
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Looks for the entities of the specified type in the specified query range.
	 * 
	 * @param entityType
	 * @param range
	 * @param sourcePos
	 * @param outEntities
	 */
	@SuppressWarnings("unchecked")
	public < T > void findEntitiesInRange( Class< T > entityType, float range, Vector3 sourcePos, ArrayList< T > outEntities ) 
	{
		if ( outEntities == null )
		{
			return;
		}
		
		int count 			= m_entities.size();
		if ( count == 0 )
		{
			return;
		}
		
		float radiusSq 			= range*range;

		for( int i = 0; i < count; ++i )
		{
			Entity entity = m_entities.get(i);
			if ( entityType.isInstance( entity ) )
			{
				entity.getWorldBounds().getNearestPoint( sourcePos, m_entityQueryTargetPos );
				float distanceSq = sourcePos.distSq2D( m_entityQueryTargetPos );
				if ( distanceSq <= radiusSq )
				{
					outEntities.add( (T)entity );
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------
	// Serialization
	// ------------------------------------------------------------------------
	/**
	 * Loads the world's contents.
	 * 
	 * CAUTION: it doesn't remove the previous world's contents!!!
	 * 
	 * @param loader		loader that persists the world
	 * @param resMgr		resource manager to which all precached resources will be loaded
	 */
	public void load( DataLoader loader, ResourceManager resMgr )
	{
		if ( loader == null )
		{
			return;
		}
		
		try
		{
			m_width = loader.getFloatValue( "width" );
			m_height = loader.getFloatValue( "height" );
			
			// precache large assets - such as atlases or sounds, so that they
			// don't need to be loaded at runtime and stall the application
			DataLoader resourcesCache = loader.getChild( "ResourcesCache" );
			if ( resourcesCache != null )
			{
				for( DataLoader child = resourcesCache.getChild( "Resource" ); child != null; child = child.getSibling() )
				{
					String resourceType = child.getStringValue( "type" );
					ResourceFactory factory = findResourceFactory( resourceType );
					
					String assetPath = child.getStringValue( "path" );
					factory.load( resMgr, assetPath );
				}
			}
			
			// parse the entities
			for( DataLoader child = loader.getChild( "Entity" ); child != null; child = child.getSibling() )
			{
				String entityType = child.getStringValue( "type" );
				EntityFactory factory = findEntityFactory( entityType );
				if ( factory != null )
				{
					Entity entity = factory.create();
					entity.load( child );
					
					// add entity to the world
					addEntity( entity );
				}
			}
		}
		catch( Exception ex )
		{
			Log.d( "World", "Error while loading: " + ex.getMessage() );
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * Saves the world's contents.
	 * 
	 * @param saver		saver that persists the world
	 */
	public void save( DataSaver saver )
	{
		if ( saver == null )
		{
			return;
		}
		
		try
		{
			saver.setFloatValue( "width", m_width );
			saver.setFloatValue( "height", m_height );
			
			// parse the entities
			int entitiesCount = m_entities.size();
			for ( int i = 0; i < entitiesCount; ++i )
			{
				DataSaver childEntity = saver.addChild( "Entity" );
				Entity entity = m_entities.get(i);
				childEntity.setStringValue( "type", entity.getClass().getSimpleName() );
				entity.save( childEntity );
			}
		}
		catch( Exception ex )
		{
			Log.d( "World", "Error while saving: " + ex.getMessage() );
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * Informs the world about a factory that will be instantiating entities
	 * of the specified type.
	 * 
	 * @param type
	 * @param factory
	 */
	public void registerEntity( Class< ? extends Entity > type, EntityFactory factory )
	{
		// look for a duplicate
		int count = m_entityFactories.size();
		for ( int i = 0; i < count; ++i )
		{
			EntityFactoryData data = m_entityFactories.get(i);
			if ( data.m_type == type )
			{
				// there's one - redefine
				data.m_factory = factory;
				break;
			}
		}
		
		// none was found - add a new definition
		m_entityFactories.add( new EntityFactoryData( type, factory ) );
	}
	
	/**
	 * Looks for a factory that can instantiate entities of the specified type.
	 * 
	 * @param entityType
	 * @return
	 */
	private EntityFactory findEntityFactory( String entityType )
	{
		int count = m_entityFactories.size();
		for ( int i = 0; i < count; ++i )
		{
			EntityFactoryData data = m_entityFactories.get(i);
			if ( data.m_type.getSimpleName().equals( entityType ) )
			{
				return data.m_factory;
			}
		}
		
		// factory definition not found
		return null;
	}
	
	/**
	 * Looks for a factory that can instantiate resources of the specified type.
	 * 
	 * @param resourceType
	 * @return
	 */
	private ResourceFactory findResourceFactory( String resourceType )
	{
		for ( int i = 0; i < m_resourceFactories.length; ++i )
		{
			ResourceFactory factory = m_resourceFactories[i];
			if ( factory.m_type.getSimpleName().equals( resourceType ) )
			{
				return factory;
			}
		}
		
		// factory definition not found
		return null;
	}

	/**
	 * Constrains the specified position to lie inside the world boundaries.
	 * 
	 * @param pos
	 * @param xBoundary			boundary on the OX axis
	 * @param yBoundary			boundary on the OY axis
	 */
	public void constrainToWorldBoundaries( Vector3 pos, float xBoundary, float yBoundary ) 
	{
		float minX = xBoundary;
		float maxX = m_width - xBoundary;
		float minY = yBoundary;
		float maxY = m_height - yBoundary;
		
		// safeguard the case when the view frustum is actually larger than the world.
		if ( minX > maxX )
		{
			maxX = minX;
		}
		if ( minY > maxY )
		{
			maxY = minY;
		}
		
		// OX axis
		if ( pos.m_x < minX ) 
		{ 
			pos.m_x = minX; 
		}
		else if ( pos.m_x > maxX ) 
		{ 
			pos.m_x = maxX; 
		}
		
		// OY axis
		if ( pos.m_y < minY ) 
		{ 
			pos.m_y = minY; 
		}
		else if ( pos.m_y > maxY ) 
		{ 
			pos.m_y = maxY; 
		}
	}
}

