/**
 * 
 */
package com.hypefoundry.engine.renderer2D.particleSystem;

import com.hypefoundry.engine.core.ResourceManager;
import com.hypefoundry.engine.util.serialization.DataLoader;

/**
 * Emitter spawns new particles and manages their life time.
 * 
 * @author Paksas
 *
 */
public abstract class ParticleEmitter 
{
	protected int				m_particlesCount;
	private float 				m_emissionFrequency;
	private float				m_timeToLive;
	private int					m_numEmittedEachTick;
	
	private float				m_timeElapsed;
	private ParticlesFactory 	m_factory;
	
	/**
	 * Constructor.
	 */
	public ParticleEmitter( ) 
	{
		m_particlesCount = 0;
		m_emissionFrequency = 0.01f;
		m_timeToLive = 0;
		m_numEmittedEachTick = 0;

		m_timeElapsed = 0;
		m_factory = null;
	}
	
	/**
	 * Sets the total emitted particles count and the factory that cretes them.
	 * 
	 * @param val
	 * @param factory
	 * @return
	 */
	public ParticleEmitter setParticlesCount( int val, ParticlesFactory factory )
	{
		m_particlesCount = val;
		m_factory = factory;
		return this;
	}

	/**
	 * Sets how long each particle should live.
	 * 
	 * @param val
	 */
	public ParticleEmitter setTimeToLive( float time )
	{
		m_timeToLive = time;
		return this;
	}
	
	
	/**
	 * Sets the wait period between subsequent particle emissions.
	 * 
	 * @param val
	 */
	public ParticleEmitter setEmissionFrequency( float emissionFrequency )
	{
		m_emissionFrequency = emissionFrequency;
		return this;
	}
	
	/**
	 * Sets the amount of particles that should be emitted in each tick.
	 * 
	 * @param val
	 */
	public ParticleEmitter setAmountEmittedEachTick( int val )
	{
		m_numEmittedEachTick = val;
		return this;
	}
	
	
	/**
	 * Called when a player receives a particle system to play
	 * and needs to get its particles initialized
	 * 
	 * @param particles
	 * @param startIdx
	 * @return	number of particles added
	 */
	int onPlayerAttached( Particle[] particles, int startIdx )
	{
		for ( int i = 0; i < m_particlesCount; ++i )
		{
			particles[i + startIdx] = m_factory.create();
		}
		
		return m_particlesCount;
	}

	/**
	 * Manages the particles, spawning new ones and removing the old ones.
	 * 
	 * @param deltaTime
	 * @param particles
	 * @param timeMultipliers
	 * @param firstParticleIdx
	 * @param lastParticleIdx
	 */
	void update( float deltaTime, Particle[] particles, float[] timeMultipliers, int firstParticleIdx, int lastParticleIdx )
	{			
		int toBeReborn = 0;
		m_timeElapsed += deltaTime;
		while( m_timeElapsed >= m_emissionFrequency )
		{
			toBeReborn += m_numEmittedEachTick;
			m_timeElapsed -= m_emissionFrequency;
		}
		
		float totalToBeReborn = toBeReborn;
		
		// change the remaining life time of the particles
		for ( int i = firstParticleIdx; i < lastParticleIdx; ++i )
		{
			if ( particles[i].m_timeToLive <= 0 && toBeReborn > 0 )
			{
				particles[i].setTimeToLive( m_timeToLive );
				--toBeReborn;
				
				initialize( i, particles[i] );
				
				// inform the particle that it was reinitialized
				particles[i].onInitialized();
				
				// some time has passed - so simulate the particle for a little while to create an illusion
				// that they were emitted in a continuous manner, and not at discrete time steps
				timeMultipliers[i] = ( totalToBeReborn - toBeReborn ) / totalToBeReborn;
			}
		}
	}
	
	/**
	 * Initializes the particle.
	 * 
	 * @param particleIdx
	 * @param particle
	 */
	protected abstract void initialize( int particleIdx, Particle particle );
	
	/**
	 * Loads the emitter definition from a stream.
	 * 
	 * @param loader
	 * @param resMgr
	 */
	void load( DataLoader loader, ResourceManager resMgr )
	{
		// load the basic parameters
		m_emissionFrequency 			= Math.max( loader.getFloatValue( "frequency" ), 0.01f );
		m_timeToLive 					= loader.getFloatValue( "timeToLive" );
		m_numEmittedEachTick			= loader.getIntValue( "amountEmittedEachTick" );
		m_particlesCount				= loader.getIntValue( "totalAmount" );
		
		// create the particles
		String particleType				= loader.getStringValue( "particleType" );
		m_factory						= ParticleSystem.findParticleFactory( particleType );
		if ( m_factory != null )
		{
			m_factory.load( loader, resMgr );
		}

		
		// load the implementation specific data
		onLoad( loader );
	}
	
	/**
	 * Loads the implementation specific emitter definition from a stream.
	 * 
	 * @param loader
	 */
	protected abstract void onLoad( DataLoader loader );
}
