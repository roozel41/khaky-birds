/**
 * 
 */
package com.hypefoundry.engine.renderer2D.particleSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.hypefoundry.engine.core.Resource;
import com.hypefoundry.engine.impl.game.GLGame;
import com.hypefoundry.engine.util.Arrays;
import com.hypefoundry.engine.util.serialization.DataLoader;
import com.hypefoundry.engine.util.serialization.xml.XMLDataLoader;

import com.hypefoundry.engine.renderer2D.particleSystem.emitters.*;
import com.hypefoundry.engine.renderer2D.particleSystem.affectors.*;
import com.hypefoundry.engine.renderer2D.particleSystem.particles.*;


interface AbstractParticlesFactory
{
	ParticlesFactory create();
}

/**
 * A tool for creating visual effects that involve may small bitmaps moving around.
 * 
 * @author Paksas
 *
 */
public class ParticleSystem extends Resource
{	
	// ------------------------------------------------------------------------
	// helper types
	// ------------------------------------------------------------------------
	static class EmittersFactoryData
	{
		Class< ? extends ParticleEmitter >		m_type;
		EmitterFactory							m_factory;
		
		EmittersFactoryData( Class< ? extends ParticleEmitter > type, EmitterFactory factory )
		{
			m_type = type;
			m_factory = factory;
		}
	}
	
	static class AffectorsFactoryData
	{
		Class< ? extends ParticleAffector >		m_type;
		AffectorFactory							m_factory;
		
		AffectorsFactoryData( Class< ? extends ParticleAffector > type, AffectorFactory factory )
		{
			m_type = type;
			m_factory = factory;
		}
	}

	
	static class ParticlesFactoryData
	{
		Class< ? extends Particle >				m_type;
		AbstractParticlesFactory				m_factory;
		
		ParticlesFactoryData( Class< ? extends Particle > type, AbstractParticlesFactory factory )
		{
			m_type = type;
			m_factory = factory;
		}
	}
	
	// ------------------------------------------------------------------------
	// REGISTER EMITTERS HERE
	// ------------------------------------------------------------------------
	static private EmittersFactoryData[]			m_emittersFactories = {
			new EmittersFactoryData( RadialParticleEmitter.class, new EmitterFactory() { @Override public ParticleEmitter create() { return new RadialParticleEmitter(); } } ),
			new EmittersFactoryData( DirectionalParticleEmitter.class, new EmitterFactory() { @Override public ParticleEmitter create() { return new DirectionalParticleEmitter(); } } ),
			new EmittersFactoryData( LineParticleEmitter.class, new EmitterFactory() { @Override public ParticleEmitter create() { return new LineParticleEmitter(); } } ),
			new EmittersFactoryData( CircularParticleEmitter.class, new EmitterFactory() { @Override public ParticleEmitter create() { return new CircularParticleEmitter(); } } ),
			new EmittersFactoryData( BoxParticleEmitter.class, new EmitterFactory() { @Override public ParticleEmitter create() { return new BoxParticleEmitter(); } } ),
	};
	
	// ------------------------------------------------------------------------
	// REGISTER AFFECTORS HERE
	// ------------------------------------------------------------------------
	static private AffectorsFactoryData[]			m_affectorsFactories = {
			new AffectorsFactoryData( LinearMovementAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new LinearMovementAffector(); } } ),
			new AffectorsFactoryData( BlowMovementAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new BlowMovementAffector(); } } ),
			new AffectorsFactoryData( RotationAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new RotationAffector(); } } ),
			new AffectorsFactoryData( AccelerationAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new AccelerationAffector(); } } ),
			new AffectorsFactoryData( LifetimeAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new LifetimeAffector(); } } ),
			new AffectorsFactoryData( ClearSkyAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new ClearSkyAffector(); } } ),
			new AffectorsFactoryData( AlphaAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new AlphaAffector(); } } ),
			new AffectorsFactoryData( BubblesAffector.class, new AffectorFactory() { @Override public ParticleAffector create() { return new BubblesAffector(); } } ),
	};
	
	// ------------------------------------------------------------------------
	// REGISTER PARTICLES HERE
	// ------------------------------------------------------------------------
	static private ParticlesFactoryData[]			m_particlesFactories = {
		new ParticlesFactoryData( AnimatedParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new AnimatedParticleFactory(); } } ),
		new ParticlesFactoryData( TexturedParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new TexturedParticleFactory(); } } ),
		new ParticlesFactoryData( RandomlyTexturedParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new RandomlyTexturedParticleFactory(); } } ),
		new ParticlesFactoryData( TracerParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new TracerParticleFactory(); } } ),
		new ParticlesFactoryData( LightShaftParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new LightShaftParticleFactory( GLGame.getInstance().getGraphics() ); } } ),
		new ParticlesFactoryData( CircleParticle.class, new AbstractParticlesFactory() { @Override public ParticlesFactory create() { return new CircleParticleFactory( GLGame.getInstance().getGraphics() ); } } ),
	};
	
	// ------------------------------------------------------------------------
	// members
	// ------------------------------------------------------------------------
	ParticleEmitter[] 							m_emitters 		= new ParticleEmitter[0];
	ParticleAffector[] 							m_affectors 	= new ParticleAffector[0];
	public int									m_maxParticles;
	
	// runtime data
	private	List< ParticleSystemListener >		m_listeners		= new ArrayList< ParticleSystemListener >();
	
	/**
	 * Default constructor ( required by the resources manager ).
	 */
	public ParticleSystem()
	{
		m_maxParticles = 0;
	}
	
	
	/**
	 * Adds a new particle emitter.
	 * 
	 * @param emitter
	 * @param listenerToExclude		sometimes we want to add the affector definitions
	 * 								in response to the resource being reloaded - and we'll usualy use
	 * 								the listeners mechanism to learn about that. This additional param
	 * 								provides a way to append them from the callbackj method without running into a stack overload
	 */
	public void addEmitter( ParticleEmitter emitter, ParticleSystemListener listenerToExclude )
	{
		if ( emitter != null )
		{
			m_emitters = Arrays.append( m_emitters, emitter );
			m_maxParticles += emitter.m_particlesCount;
			
			reinitializeListeners( listenerToExclude );
		}
	}
	
	/**
	 * Adds a new particle emitter.
	 * 
	 * @param emitter
	 * @param listenerToExclude		sometimes we want to add the affector definitions
	 * 								in response to the resource being reloaded - and we'll usualy use
	 * 								the listeners mechanism to learn about that. This additional param
	 * 								provides a way to append them from the callbackj method without running into a stack overload
	 */
	public void addAffector( ParticleAffector affector, ParticleSystemListener listenerToExclude )
	{
		if ( affector != null )
		{
			m_affectors = Arrays.append( m_affectors, affector );
			
			reinitializeListeners( listenerToExclude );
		}
	}
	
	/**
	 * Adds a batch of affectors.
	 * 
	 * @param affectors
	 * @param listenerToExclude		sometimes we want to add the affector definitions
	 * 								in response to the resource being reloaded - and we'll usualy use
	 * 								the listeners mechanism to learn about that. This additional param
	 * 								provides a way to append them from the callbackj method without running into a stack overload
	 */
	public void addAffectors( List< ? extends ParticleAffector > affectors, ParticleSystemListener listenerToExclude ) 
	{
		int count = affectors.size();
		for ( int i = 0; i < count; ++i )
		{
			ParticleAffector affector = affectors.get(i);
			if ( affector != null )
			{
				m_affectors = Arrays.append( m_affectors, affector );
			}
		}
		
		reinitializeListeners( listenerToExclude );
	}
	
	// ------------------------------------------------------------------------
	// Listeners management
	// ------------------------------------------------------------------------
	
	/**
	 * Attaches a new particle system listener.
	 * 
	 * @param listener
	 */
	public void attachListener( ParticleSystemListener listener )
	{
		if ( listener != null )
		{
			m_listeners.add( listener );
			
			if ( m_maxParticles > 0 )
			{
				// initialize the listener, if there's something to be initialized for
				listener.onSystemInitialized();
			}
		}
	}
	
	/**
	 * Detaches an existing particle system listener.
	 * 
	 * @param listener
	 */
	public void detachListener( ParticleSystemListener listener )
	{
		if ( listener != null )
		{
			m_listeners.remove( listener );
		}
	}
	
	/**
	 * Reinitializes all attached listeners.
	 * 
	 * @param listenerToExclude		informs all the listeners but this one
	 */
	private void reinitializeListeners( ParticleSystemListener listenerToExclude )
	{
		int listenersCount = m_listeners.size();
		for ( int i = 0; i < listenersCount; ++i )
		{
			ParticleSystemListener listener = m_listeners.get(i);
			if ( listener == listenerToExclude )
			{
				continue;
			}
				
			listener.onSystemReleased();
			
			if ( m_maxParticles > 0 )
			{
				listener.onSystemInitialized();
			}
		}
	}
	
	// ------------------------------------------------------------------------
	// Resource implementation
	// ------------------------------------------------------------------------
	@Override
	public void load() 
	{	
		InputStream stream = null;
		try 
		{
			stream = m_game.getFileIO().readAsset( m_assetPath );
		} 
		catch ( IOException e ) 
		{
			throw new RuntimeException( e );
		}
		
		// parse the animation data
		DataLoader psNode = XMLDataLoader.parse( stream, "ParticleSystem" );
		if ( psNode != null )
		{
			m_maxParticles = 0;
			
			// load the emitters
			for( DataLoader child = psNode.getChild( "Emitter" ); child != null; child = child.getSibling() )
			{
				String emitterType = child.getStringValue( "type" );
				EmitterFactory factory = findEmitterFactory( emitterType );
				if ( factory != null )
				{
					ParticleEmitter emitter = factory.create();
					emitter.load( child, m_resMgr );
					
					// add the emitter
					m_emitters = Arrays.append( m_emitters, emitter );
					m_maxParticles += emitter.m_particlesCount;
				}
			}	
			
			// load the affectors
			for( DataLoader child = psNode.getChild( "Affector" ); child != null; child = child.getSibling() )
			{
				String affectorType = child.getStringValue( "type" );
				AffectorFactory factory = findAffectorFactory( affectorType );
				if ( factory != null )
				{
					ParticleAffector affector = factory.create();
					affector.load( child );
					
					// add the affector
					m_affectors = Arrays.append( m_affectors, affector );
				}
			}	
		}
		
		// initialize the listeners, providing the system is capable of producing any particles
		if ( m_maxParticles > 0 )
		{
			int listenersCount = m_listeners.size();
			for ( int i = 0; i < listenersCount; ++i )
			{
				m_listeners.get(i).onSystemInitialized();
			}
		}
	}

	@Override
	public void release() 
	{
		m_emitters 		= new ParticleEmitter[0];
		m_affectors 	= new ParticleAffector[0];
		m_maxParticles 	= 0;
		
		// deinitialzie the listeners
		int listenersCount = m_listeners.size();
		for ( int i = 0; i < listenersCount; ++i )
		{
			m_listeners.get(i).onSystemReleased();
		}
	}
	
	/**
	 * Looks for a factory that can instantiate emitters of the specified type.
	 * 
	 * @param emitterType
	 * @return
	 */
	static private EmitterFactory findEmitterFactory( String emitterType )
	{
		for ( int i = 0; i < m_emittersFactories.length; ++i )
		{
			EmittersFactoryData data = m_emittersFactories[i];
			if ( data.m_type.getSimpleName().equals( emitterType ) )
			{
				return data.m_factory;
			}
		}
		
		// factory definition not found
		return null;
	}
	
	/**
	 * Looks for a factory that can instantiate affectors of the specified type.
	 * 
	 * @param affectorType
	 * @return
	 */
	static private AffectorFactory findAffectorFactory( String affectorType )
	{
		for ( int i = 0; i < m_affectorsFactories.length; ++i )
		{
			AffectorsFactoryData data = m_affectorsFactories[i];
			if ( data.m_type.getSimpleName().equals( affectorType ) )
			{
				return data.m_factory;
			}
		}
		
		// factory definition not found
		return null;
	}
	
	/**
	 * Looks for a factory that can instantiate particle of the specified type.
	 * 
	 * @param particleType
	 * @return
	 */
	static ParticlesFactory findParticleFactory( String particleType )
	{
		for ( int i = 0; i < m_particlesFactories.length; ++i )
		{
			ParticlesFactoryData data = m_particlesFactories[i];
			if ( data.m_type.getSimpleName().equals( particleType ) )
			{
				return data.m_factory.create();
			}
		}
		
		// factory definition not found
		return null;
	}
}
