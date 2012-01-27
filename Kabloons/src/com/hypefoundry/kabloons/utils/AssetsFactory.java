/**
 * 
 */
package com.hypefoundry.kabloons.utils;

import com.hypefoundry.engine.math.BoundingBox;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.kabloons.entities.baloon.Baloon;
import com.hypefoundry.kabloons.entities.fan.Fan;


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
		String			m_fanOnAnim;
		String			m_fanOffAnim;
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
			
			m_fanOnAnim = loader.getStringValue( "fanOnAnim" );
			m_fanOffAnim = loader.getStringValue( "fanOffAnim" );
			
			m_blowForce = blowForce;
		}
		
		/**
		 * Initializes a fan.
		 *  
		 * @return
		 */
		void initialize( Fan fan )
		{
			fan.initialize( m_localBounds, m_fanOnAnim, m_fanOffAnim, m_blowForce );
		}
	}
	
	
	// baloon factories
	private BaloonFactory[]		m_baloonTypes;
	private FanFactory[]		m_fanTypes = new FanFactory[Fan.Direction.values().length];
	
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
}
