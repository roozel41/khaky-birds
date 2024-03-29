/**
 * 
 */
package com.hypefoundry.engine.gestures;

import java.util.*;

import com.hypefoundry.engine.core.Input;
import com.hypefoundry.engine.core.Input.TouchEvent;
import com.hypefoundry.engine.game.InputHandler;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.engine.gestures.gestureTypes.DirectionalGesture;
import com.hypefoundry.engine.gestures.gestureTypes.AngularGesture;


/**
 * A tool for recognizing gestures drawn by the user on screen.
 * 
 * @author Paksas
 */
public class GesturesRecognition implements InputHandler 
{
	// ------------------------------------------------------------------------
	// helper types
	// ------------------------------------------------------------------------
	interface GestureFactory
	{
		Gesture create();
	}
	
	static class GesturesFactoryData
	{
		Class< ? extends Gesture >				m_type;
		GestureFactory							m_factory;
		
		GesturesFactoryData( Class< ? extends Gesture > type, GestureFactory factory )
		{
			m_type = type;
			m_factory = factory;
		}
	}
	
	// ------------------------------------------------------------------------
	// REGISTER GESTURE TYPES HERE
	// ------------------------------------------------------------------------
	static private GesturesFactoryData[]			m_gesturesFactories = {
			new GesturesFactoryData( DirectionalGesture.class, new GestureFactory() { @Override public Gesture create() { return new DirectionalGesture(); } } ),
			new GesturesFactoryData( AngularGesture.class, new GestureFactory() { @Override public Gesture create() { return new AngularGesture(); } } ),
	};

	// ------------------------------------------------------------------------
	// Member fields
	// ------------------------------------------------------------------------
	public final static int				MIN_SAMPLES					= 2;
	public final static int				MAX_SAMPLES					= 64;
	
	// gestures DB
	private List< Gesture >				m_gesturesDB				= new ArrayList< Gesture >();
	private int							m_highestGestureScore		= 0;
	
	// listeners
	private List< GesturesListener >	m_listeners					= new ArrayList< GesturesListener >();
	
	// sampling runtime data
	private Vector3						m_lastSamplePoint 			= new Vector3();
	private Vector3						m_tmpVel		 			= new Vector3();
	private boolean						m_gestureInProgress			= false;	
	
	private Vector3[]					m_points					= new Vector3[MAX_SAMPLES];
	private int							m_nextFreeSampleIdx 		= 0;
	private int							m_gestureScore				= -1;
	private boolean						m_continuousGesturePerformed = false;
	
	
	private GesturesAnalyzer			m_gestureAnalyzer			= new GesturesAnalyzer();
	
	
	// ------------------------------------------------------------------------
	// API
	// ------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public GesturesRecognition()
	{
		for ( int i = 0; i < MAX_SAMPLES; ++i )
		{
			m_points[i] = new Vector3();
		}
	}
	
	// ------------------------------------------------------------------------
	// Gestures DB management
	// ------------------------------------------------------------------------
	
	/**
	 * Defines a new gesture.
	 * 
	 * @param gesture
	 */
	public void addGesture( Gesture gesture)
	{
		if ( gesture != null )
		{
			m_gesturesDB.add( gesture );
			
			int score = gesture.getRequiredSamplesCount();
			if ( m_highestGestureScore < score && gesture.m_continuous )
			{
				m_highestGestureScore = score;
			}
		}
	}
	
	/**
	 * Loads the gestures database from the specified loader.
	 * 
	 * @param loader
	 */
	public void load( DataLoader loader ) 
	{
		DataLoader gesturesDBNode = loader.getChild( "GesturesDB" );
		if ( gesturesDBNode == null )
		{
			// no gestures definitions found
			return;
		}
		
		for ( DataLoader gestureNode = gesturesDBNode.getChild( "Gesture" ); gestureNode != null; gestureNode = gestureNode.getSibling() )
		{
			String gestureType = gestureNode.getStringValue( "type" );
			GestureFactory factory = findGestureFactory( gestureType );
			if ( factory != null )
			{
				Gesture gesture =  factory.create();
				gesture.load( gestureNode );
				
				addGesture( gesture );
			}	
		}
	}
	
	/**
	 * Looks for a factory that can instantiate gestures of the specified type.
	 * 
	 * @param gestureType
	 * @return
	 */
	static private GestureFactory findGestureFactory( String gestureType )
	{
		for ( int i = 0; i < m_gesturesFactories.length; ++i )
		{
			GesturesFactoryData data = m_gesturesFactories[i];
			if ( data.m_type.getSimpleName().equals( gestureType ) )
			{
				return data.m_factory;
			}
		}
		
		// factory definition not found
		return null;
	}
	
	// ------------------------------------------------------------------------
	// Listeners management
	// ------------------------------------------------------------------------
	
	/**
	 * Attaches a new gestures listener.
	 * 
	 * @param listener
	 */
	public void attachListener( GesturesListener listener )
	{
		if ( listener != null )
		{
			m_listeners.add( listener );
		}
	}
	
	/**
	 * Attaches a new gestures listener.
	 * 
	 * @param listener
	 */
	public void detachListener( GesturesListener listener )
	{
		if ( listener != null )
		{
			m_listeners.remove( listener );
		}
	}
	
	// ------------------------------------------------------------------------
	// Internals
	// ------------------------------------------------------------------------
	
	@Override
	public boolean handleInput( Input input, float deltaTime ) 
	{
		// We keep recording as long as a finger is down. After it's detached we
		// submit a gesture for analysis
		boolean inputHandled = false;
		
		List< TouchEvent > inputEvents = input.getTouchEvents();	
		int count = inputEvents.size();
		for ( int i = 0 ; i < count; ++i )
		{	
			TouchEvent lastEvent = inputEvents.get(i);
			
			if ( lastEvent.type == TouchEvent.TOUCH_DOWN )
			{
				assert( m_gestureInProgress == false );
				
				// start recording the gesture
				m_lastSamplePoint.set( lastEvent.x, lastEvent.y, 0 );
				m_nextFreeSampleIdx = 0;
				m_gestureScore = -1;
				m_gestureInProgress = true;
				m_continuousGesturePerformed = false;
				
				inputHandled = true;
			}
			else if ( lastEvent.type == TouchEvent.TOUCH_UP )
			{
				assert ( m_gestureInProgress == true );
				
				// calculate the last direction
				addDirection( lastEvent.x, lastEvent.y, deltaTime );
				
				// submit the gesture for recognition
				submitGesture();
				
				// reset the samples counter
				m_nextFreeSampleIdx = 0;
				m_gestureScore = -1;
				m_continuousGesturePerformed = false;
				
				inputHandled = true;
			}
			else if ( lastEvent.type == TouchEvent.TOUCH_DRAGGED )
			{
				assert( m_gestureInProgress == true );
				
				// calculate the direction
				addDirection( lastEvent.x, lastEvent.y, deltaTime );
				
				// increase the samples counter
				++m_nextFreeSampleIdx;		
				
				// if the number of samples was exceeded, or the gesture we drew
				// so far is the best one we can get, submit it and start anew
				boolean resetGesture = peekContinuousGesture();
				if ( resetGesture )
				{
					// a continuous gesture was performed
					m_continuousGesturePerformed = true;
				}
				
				if ( !resetGesture && m_nextFreeSampleIdx >= MAX_SAMPLES )
				{
					submitGesture();
					resetGesture = true;
				}

				if ( resetGesture )
				{	
					m_lastSamplePoint.set( lastEvent.x, lastEvent.y, 0 );
					m_nextFreeSampleIdx = 0;
					m_gestureScore = -1;
				}
				
				inputHandled = true;
			}
		}
		return inputHandled;
	}

	/**
	 * Samples a new touch direction.
	 * 
	 * @param x
	 * @param y
	 * @param deltaTime
	 */
	private void addDirection( int x, int y, float deltaTime )
	{
		m_tmpVel.set( x, y, 0 ).sub( m_lastSamplePoint );
		
		float speed = m_tmpVel.mag2D() / deltaTime;
		if ( speed < 1e-2 )
		{
			// reject movements that are too slow
			return;
		}
		
		// memorize the point as the last sampled point
		m_lastSamplePoint.set( x, y, 0 );
		m_points[m_nextFreeSampleIdx].set( m_lastSamplePoint );
	}
	
	/**
	 * Checks if a ontinuous gesture was performed
	 * 
	 * @return
	 */
	private boolean peekContinuousGesture()
	{
		if ( m_nextFreeSampleIdx < MIN_SAMPLES )
		{
			return false;
		}
		
		Gesture bestMatchingGesture = m_gestureAnalyzer.analyze( m_points, m_nextFreeSampleIdx, m_gesturesDB );
		if ( bestMatchingGesture != null && bestMatchingGesture.m_continuous )
		{
			int score = bestMatchingGesture.getRequiredSamplesCount();
			if ( score >= m_gestureScore )
			{
				m_gestureScore = score;
			}
		}
		
		if ( m_gestureScore >= m_highestGestureScore )
		{
			if ( m_gestureScore < 3 )
			{
				int a = 0;
			}
			
			// that's our gesture
			notifyGesture( bestMatchingGesture );	
			return true;
		}
		
		return false;
	}
	
	/**
	 * Submits a gesture for recognitions
	 */
	private void submitGesture()
	{
		// analyze the gesture
			
		Gesture bestMatchingGesture = m_gestureAnalyzer.analyze( m_points, m_nextFreeSampleIdx, m_gesturesDB );
		
		// if any matching gesture was found, inform the listener about it 
		if ( bestMatchingGesture == null )
		{
			return;
		}
		
		if ( m_continuousGesturePerformed && !bestMatchingGesture.m_continuous )
		{
			// if a continuous gesture was already performed, and this one is not continuous, don't submit it
			return;
		}
		
		notifyGesture( bestMatchingGesture );
	}
	
	/**
	 * Notifies the listeners about a successfully identified gesture.
	 * 
	 * @param gesture
	 */
	private void notifyGesture( Gesture gesture )
	{
		if ( gesture == null )
		{
			return;
		}
		
		// but first - set the samples on it - it will be needed for checking stuff such as
		// positioning etc.
		gesture.setInstancePoints( m_points, m_nextFreeSampleIdx );
					
		int listenersCount = m_listeners.size();
		for ( int i = 0; i < listenersCount; ++i )
		{
			GesturesListener listener = m_listeners.get(i);
			listener.onGestureRecognized( gesture );
		}
	}
}

