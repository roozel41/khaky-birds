/**
 * 
 */
package com.hypefoundry.kabloons.utils;

import com.hypefoundry.engine.math.BoundingBox;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.kabloons.entities.background.AnimatedBackground;
import com.hypefoundry.kabloons.entities.baloon.Baloon;
import com.hypefoundry.kabloons.entities.buzzSaw.BuzzSaw;
import com.hypefoundry.kabloons.entities.exitDoor.ExitDoor;
import com.hypefoundry.kabloons.entities.fan.Fan;
import com.hypefoundry.kabloons.entities.toggle.Toggle;


/**
 * @author Paksas
 *
 */
public class AssetsFactory 
{
	/**
	 * A factory that creates baloons.
	 * 
	 * @author Paksas
	 *
	 */
	private class BaloonFactory
	{
		String			m_floatingAnim;
		BoundingBox		m_localBounds;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 */
		BaloonFactory( DataLoader loader )
		{			
			m_localBounds = new BoundingBox();
			m_localBounds.load( "localBounds", loader );
			
			m_floatingAnim = loader.getStringValue( "floatingAnim" );
		}
		
		/**
		 * Instantiates a new baloon at the specified world position.
		 *
		 * @param pos
		 *  
		 * @return
		 */
		Baloon create( Vector3 pos )
		{
			Baloon baloon = new Baloon();
			baloon.initialize( m_localBounds, pos, m_floatingAnim );
			return baloon;
		}
	}
	
	/**
	 * A factory that creates fans.
	 * 
	 * @author Paksas
	 *
	 */
	private class FanFactory
	{
		String			m_fanAnim;
		String			m_windFx;
		BoundingBox		m_localBounds;
		Vector3			m_blowForce;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 * @param blowForce
		 */
		FanFactory( DataLoader loader, Vector3 blowForce )
		{
			m_localBounds = new BoundingBox();
			m_localBounds.load( "localBounds", loader );
			
			m_fanAnim = loader.getStringValue( "anim" );
			m_windFx = loader.getStringValue( "windFx" );
			
			m_blowForce = blowForce;
		}
		
		/**
		 * Initializes a fan.
		 *  
		 * @return
		 */
		void initialize( Fan fan )
		{
			fan.initialize( m_localBounds, m_fanAnim, m_windFx, m_blowForce );
		}
	}
	
	/**
	 * Definition of exit door.
	 * 
	 * @author Paksas
	 */
	public class ExitDoorData
	{
		public String		m_openDoorTexturePath;
		public String		m_closedDoorTexturePath;
		public BoundingBox	m_localBounds;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 */
		ExitDoorData( DataLoader loader )
		{			
			DataLoader exitDoorNode = loader.getChild( "ExitDoor" );
			if ( exitDoorNode != null )
			{
				m_openDoorTexturePath = exitDoorNode.getStringValue( "openTexture" );
				m_closedDoorTexturePath = exitDoorNode.getStringValue( "closedTexture" );
				
				m_localBounds = new BoundingBox();
				m_localBounds.load( "localBounds", exitDoorNode );
			}
		}
		
		public void initialize( ExitDoor door )
		{
			door.m_openDoorTexturePath = m_openDoorTexturePath;
			door.m_closedDoorTexturePath = m_closedDoorTexturePath;
			door.setBoundingBox( m_localBounds );
		}
	}
	
	/**
	 * Definition of a toggle.
	 * 
	 * @author Paksas
	 */
	public class ToggleData
	{
		public String			m_onTexturePath;
		public String			m_offTexturePath;
		public BoundingBox		m_localBounds;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 */
		ToggleData( DataLoader loader )
		{			
			DataLoader toggleNode = loader.getChild( "Toggle" );
			if ( toggleNode != null )
			{
				m_onTexturePath = toggleNode.getStringValue( "onTexture" );
				m_offTexturePath = toggleNode.getStringValue( "offTexture" );
				
				m_localBounds = new BoundingBox();
				m_localBounds.load( "localBounds", toggleNode );
			}
		}
		
		public void initialize( Toggle toggle ) 
		{
			toggle.m_onTexturePath = m_onTexturePath;
			toggle.m_offTexturePath = m_offTexturePath;
			toggle.setBoundingBox( m_localBounds );
		}
	}
	
	/**
	 * Definition of a toggle.
	 * 
	 * @author Paksas
	 */
	public class BuzzSawData
	{
		public String			m_animPath;
		public BoundingBox		m_localBounds;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 */
		BuzzSawData( DataLoader loader )
		{			
			DataLoader buzzSawNode = loader.getChild( "BuzzSaw" );
			if ( buzzSawNode != null )
			{
				m_animPath = buzzSawNode.getStringValue( "anim" );
				
				m_localBounds = new BoundingBox();
				m_localBounds.load( "localBounds", buzzSawNode );
			}
		}
		
		public void initialize( BuzzSaw buzzSaw ) 
		{
			buzzSaw.m_animPath = m_animPath;
			buzzSaw.setBoundingBox( m_localBounds );
		}
	}
	
	/**
	 * Definition of a toggle.
	 * 
	 * @author Paksas
	 */
	public class PuffData
	{
		public String			m_animPath;
		public BoundingBox		m_localBounds;
		
		/**
		 * Constructor.
		 * 
		 * @param loader
		 */
		PuffData( DataLoader loader )
		{			
			DataLoader buzzSawNode = loader.getChild( "Puff" );
			if ( buzzSawNode != null )
			{
				m_animPath = buzzSawNode.getStringValue( "anim" );
				
				m_localBounds = new BoundingBox();
				m_localBounds.load( "localBounds", buzzSawNode );
			}
		}
		
		public void initialize( AnimatedBackground entity ) 
		{
			entity.m_path = m_animPath;
			entity.setBoundingBox( m_localBounds );
		}
	}

	
	// baloon factories
	private BaloonFactory[]		m_baloonTypes;
	private FanFactory[]		m_fanTypes = new FanFactory[Fan.Direction.values().length];
	private ExitDoorData 		m_exitDoorDefinition;
	private ToggleData 			m_toggleDefinition;
	private BuzzSawData 		m_buzzSawDefinition;
	private PuffData			m_puffData;
	
	// ------------------------------------------------------------------------
	// API
	// ------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * 
	 * @param loader
	 */
	public AssetsFactory( DataLoader loader ) 
	{
		// load definitions of the baloons
		{
			int baloonTypesCount = loader.getChildrenCount( "Baloon" );
			m_baloonTypes = new BaloonFactory[baloonTypesCount];
			
			int i = 0;
			for ( DataLoader baloonTypeNode = loader.getChild( "Baloon" ); baloonTypeNode != null; baloonTypeNode = baloonTypeNode.getSibling(), ++i )
			{
				m_baloonTypes[i] = new BaloonFactory( baloonTypeNode );
			}
		}
		
		// load fan definitions
		{
			for ( DataLoader fanTypeNode = loader.getChild( "Fan" ); fanTypeNode != null; fanTypeNode = fanTypeNode.getSibling() )
			{
				Fan.Direction direction = loadFanDirection( fanTypeNode );
				Vector3 blowForce = new Vector3();
				blowForce.load( "BlowForce", fanTypeNode );
				blowForce.m_z = 0;
				
				m_fanTypes[ direction.m_idx ] = new FanFactory( fanTypeNode, blowForce );
			}
		}
		
		// devices
		m_exitDoorDefinition = new ExitDoorData( loader );
		m_toggleDefinition = new ToggleData( loader );
		m_buzzSawDefinition = new BuzzSawData( loader );
		m_puffData = new PuffData( loader );
	}
	
	/**
	 * Creates a baloon selected at random and places it at the specified position.
	 * 
	 * @param pos
	 * @return
	 */
	public Baloon createRandomBaloon( Vector3 pos )
	{
		int baloonType = (int)( Math.random() * m_baloonTypes.length );
		pos.m_z = 20;
		return m_baloonTypes[baloonType].create( pos );
	}
	
	/**
	 * Parses the direction of the fan from an XML doc.
	 * 
	 * @param loader
	 * @return
	 */
	public static Fan.Direction loadFanDirection( DataLoader loader )
	{
		String directionStr = loader.getStringValue( "direction" );
		Fan.Direction direction = Fan.Direction.valueOf( directionStr );
		return direction;
	}
	
	/**
	 * Initializes a fan to blow in the speicfied direction.
	 * 
	 * @param pos
	 * @param direction
	 */
	public void initializeFan( Fan fan, Fan.Direction direction )
	{
		m_fanTypes[ direction.m_idx ].initialize( fan );
	}

	/**
	 * Initializes an exit door instance.
	 * 
	 * @param exitDoor
	 */
	public void initializeDoor( ExitDoor exitDoor ) 
	{
		m_exitDoorDefinition.initialize( exitDoor );
	}
	
	/**
	 * Initializes a toggle instance.
	 * 
	 * @param toggle
	 */
	public void initializeToggle( Toggle toggle ) 
	{
		m_toggleDefinition.initialize( toggle );
	}
	
	/**
	 * Initializes a buzz saw instance.
	 * 
	 * @param toggle
	 */
	public void initializeBuzzSaw( BuzzSaw buzzSaw ) 
	{
		m_buzzSawDefinition.initialize( buzzSaw );
	}

	/**
	 * Initializes a puff effect that appears when we add or remove a fan.
	 * 
	 * @param effect
	 * @param position
	 */
	public void initializePuff( AnimatedBackground effect, Vector3 position ) 
	{
		m_puffData.initialize( effect );
		effect.setPosition( position.m_x, position.m_y, 30.0f );
	}
}
