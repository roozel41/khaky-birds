package com.hypefoundry.engine.renderer2D;

import java.util.*;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.hypefoundry.engine.core.Camera2D;
import com.hypefoundry.engine.core.GLGraphics;
import com.hypefoundry.engine.core.Texture;
import com.hypefoundry.engine.game.Entity;
import com.hypefoundry.engine.game.Game;
import com.hypefoundry.engine.game.World;
import com.hypefoundry.engine.game.WorldView;
import com.hypefoundry.engine.impl.openGL.GLCamera2D;
import com.hypefoundry.engine.renderer2D.EntityVisual;
import com.hypefoundry.engine.math.BoundingBox;
import com.hypefoundry.engine.physics.DynamicObject;
import com.hypefoundry.engine.util.GenericFactory;
import com.hypefoundry.engine.physics.DynamicObject;
import com.hypefoundry.engine.util.SpatialGrid2D;
import com.hypefoundry.engine.util.SpatialGridObject;


/**
 * An operation that renders the world contents.
 * 
 * @author paksas
 *
 */
public class Renderer2D extends GenericFactory< Entity, EntityVisual > implements WorldView
{
	private final int							MAX_SPRITES = 512;			// TODO: config
	private final int							MAX_ENTITIES = 512;			// TODO: config
	private final float 						VIEWPORT_WIDTH = 9.6f;		// TODO: config
	private final float 						VIEWPORT_HEIGHT = 4.8f;		// TODO: config
	
	private Game								m_game;
	private GLGraphics 							m_graphics;
	private SpatialGrid2D< EntityVisual >		m_visualsGrid;
	private List< EntityVisual >				m_visuals;
	private SpriteBatcher						m_batcher = null;
	
	private Texture								m_atlasTexture = null;
	private Camera2D							m_camera = null;
	
	/**
	 * Constructor.
	 * 
	 * @param game
	 * @param atlasTexture
	 */
	public Renderer2D( Game game, String atlasTextureName )
	{
		m_game = game;
		m_graphics = game.getGraphics();
		m_batcher = new SpriteBatcher( m_graphics, MAX_SPRITES );
		
		m_camera = new GLCamera2D( m_graphics, VIEWPORT_WIDTH, VIEWPORT_HEIGHT );
		m_atlasTexture = new Texture( m_game, atlasTextureName );
		
		m_visuals = new ArrayList< EntityVisual >( MAX_ENTITIES );
	}
	
	@Override
	public void onAttached( World world )
	{
		float cellSize = ( VIEWPORT_HEIGHT < VIEWPORT_WIDTH ) ? VIEWPORT_HEIGHT : VIEWPORT_WIDTH; 
		m_visualsGrid = new SpatialGrid2D< EntityVisual >( world.getWidth(), world.getHeight(), cellSize );
	}
	
	@Override
	public void onDetached( World world )
	{
		m_visualsGrid = null;
	}
	
	/**
	 * Initializes the renderer.
	 */
	public void initialize( ) 
	{
		m_atlasTexture.reload();
	}
	
	/**
	 * Initializes the renderer.
	 */
	public void deinitialize() 
	{
		m_atlasTexture.dispose();
	}
	
	/**
	 * Returns the active camera.
	 * 
	 * @return
	 */
	final public Camera2D getCamera()
	{
		return m_camera;
	}
	
	/**
	 * Draws the contents of the view.
	 */
	public void draw()
	{
		if ( m_camera == null || m_visualsGrid == null )
		{
			// nothing to do for us here
			return;
		}
		
		// update the grid
		m_visualsGrid.update();
		
		// set the render state
		GL10 gl = m_graphics.getGL();
		gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		m_camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		// begin the rendering batch
		m_batcher.beginBatch( m_atlasTexture );
		
		// draw the visuals
		List< EntityVisual > visuals = m_visualsGrid.getPotentialColliders( m_camera.getFrustum() );
		for ( EntityVisual visual : visuals )
		{
			visual.draw( m_batcher );
		}
		
		m_batcher.endBatch();
	}
	
	
	@Override
	public void onEntityAdded( Entity entity ) 
	{
		EntityVisual visual = findVisualFor( entity );
		if ( visual != null )
		{
			// the entity already has a visual created
			return;
		}
		
		try
		{
			visual = create( entity );
			
			// add the visual to the render list
			if ( entity.hasAspect( DynamicObject.class ) )
			{
				m_visualsGrid.insertDynamicObject( visual );
			}
			else
			{
				m_visualsGrid.insertStaticObject( visual );
			}
			m_visuals.add( visual );
		}
		catch( IndexOutOfBoundsException e )
		{
			// ups... - no visual representation defined - notify about it
			Log.d( "Renderer2D", "Visual representation not defined for entity '" + entity.getClass().getName() + "'" );
		}
	}

	@Override
	public void onEntityRemoved( Entity entity ) 
	{
		EntityVisual visual = findVisualFor( entity );
		if ( visual != null )
		{
			m_visualsGrid.removeObject( visual );
			m_visuals.remove( visual );
		}
	}
	
	/**
	 * Looks for a registered visual for the specified entity
	 * 
	 * @param entity
	 * @return
	 */
	private EntityVisual findVisualFor( Entity entity )
	{
		for ( EntityVisual visual : m_visuals )
		{
			if ( visual.isVisualOf( entity ) )
			{
				return visual;
			}
		}
		
		return null;
	}

}