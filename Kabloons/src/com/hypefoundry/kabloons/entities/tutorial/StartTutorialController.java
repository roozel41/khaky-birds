/**
 * 
 */
package com.hypefoundry.kabloons.entities.tutorial;

import com.hypefoundry.engine.controllers.fsm.FSMState;
import com.hypefoundry.engine.controllers.fsm.FiniteStateMachine;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.physics.DynamicObject;
import com.hypefoundry.engine.world.Entity;
import com.hypefoundry.engine.world.World;
import com.hypefoundry.engine.world.WorldView;
import com.hypefoundry.kabloons.entities.baloon.Baloon;
import com.hypefoundry.kabloons.entities.fan.Fan;
import com.hypefoundry.kabloons.entities.player.Player;

/**
 * @author Paksas
 *
 */
public class StartTutorialController extends FiniteStateMachine
{
	private StartTutorial			m_tutorial;
	private World					m_world;
	private Player					m_player;
	private Baloon					m_boyGhost;
	
	
	// tells where a fan was actually placed
	private Vector3					m_fanPosition = new Vector3();
	
	
	// ------------------------------------------------------------------------
	// States
	// ------------------------------------------------------------------------

	
	class TutorialStart extends FSMState implements WorldView
	{		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.attachView( this );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
		}
		
		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) 
		{
			if ( entity instanceof Player )
			{
				m_player = (Player)entity;
				transitionTo( ReleaseGhost.class );
			}
		}

		@Override
		public void onEntityRemoved( Entity entity ) {}
	}
	
	// ------------------------------------------------------------------------
	
	class ReleaseGhost extends FSMState implements WorldView
	{
		private boolean			m_ghostReleased;
		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.RELEASE_GHOST;
			m_tutorial.m_gesturePos.set( 2.4f, 4.0f, 0.0f );
			
			m_world.attachView( this );
			
			m_ghostReleased = false;
			
			// take all the fans away, excluding the one already set,
			// and allow to release of a ghost
			m_player.enableGhostRelease( true );
			m_player.enableFanRemoval( false );
			m_player.setFansCount( 0, 0 );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_ghostReleased )
			{
				transitionTo( WaitUntilBoyIsFullyVisible.class );
			}
		}
		
		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) 
		{
			if ( entity instanceof Baloon )
			{
				m_ghostReleased = true;
				m_boyGhost = (Baloon)entity;
			}
		}

		@Override
		public void onEntityRemoved(Entity entity) {}
	}
	
	// ------------------------------------------------------------------------
	
	class WaitUntilBoyIsFullyVisible extends FSMState
	{		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_tutorial.m_gesturePos.set( -1, -1, 0 );
			// take all the fans away, excluding the one already set,
			// and allow to release of a ghost
			m_player.enableGhostRelease( false );
			m_player.enableFanRemoval( false );
			m_player.setFansCount( 0, 0 );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_boyGhost.getPosition().m_y > 1.5f )
			{
				transitionTo( PlaceFirstFan.class );
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	class PlaceFirstFan extends FSMState implements WorldView
	{
		private boolean	 		m_fanPlaced;
		private float			m_ghostSpeed;
		
		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.PLACE_FIRST_FAN;
			m_world.attachView( this );
			
			m_fanPlaced = false;

			// give the player a single fan - the one that's supposed to be placed,
			// and prevent the ghost release
			m_player.setFansCount( 1, 0 );
			m_player.enableGhostRelease( false );
			m_player.enableFanRemoval( false );
			
			// stop the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			m_ghostSpeed = dynObj.m_linearSpeed;
			dynObj.m_linearSpeed = 0.0f;
			
			
			// make sure the gesture moves along with the boy
			// to indicate that the user should place the fan immediately there
			m_tutorial.m_gesturePos.set( m_boyGhost.getPosition() ).add( 0.8f, 1.0f, 0.0f );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
			
			// restart the ghost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			dynObj.m_linearSpeed = m_ghostSpeed;
		}
		
		@Override
		public void execute( float deltaTime )
		{	
			if ( m_fanPlaced )
			{
				transitionTo( WaitUntilBoyReachesFan.class );
			}
		}

		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) 
		{
			if ( entity instanceof Fan && !m_fanPlaced )
			{
				m_fanPlaced = true;
				
				// no matter where the user puts the fan, adjust that position 
				// so that it blows exactly at the boy
				m_fanPosition.set( m_tutorial.m_gesturePos );
				((Fan)entity).setPosition( m_fanPosition );
			}
		}

		@Override
		public void onEntityRemoved(Entity entity) {}
	}
	
	// ------------------------------------------------------------------------
	
	class WaitUntilBoyReachesFan extends FSMState
	{		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.WAIT_UNTIL_BOY_REACHES_FAN;
			m_tutorial.m_gesturePos.set( -1.0f, -1.0f, 0.0f );
			
			// take all the fans away, excluding the one already set,
			// and allow to release of a ghost
			m_player.enableFanRemoval( false );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_boyGhost.getPosition().m_y >= 3.0f )
			{
				transitionTo( RemoveFirstFan.class );
			}
		}
	}

	// ------------------------------------------------------------------------
	
	class RemoveFirstFan extends FSMState implements WorldView
	{
		private boolean			m_fanRemoved;
		private float			m_ghostSpeed;
		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.REMOVE_FIRST_FAN;
			
			// place the gesture exactly over the placed fan
			m_tutorial.m_gesturePos.set( m_fanPosition );
			
			m_world.attachView( this );
			
			m_fanRemoved = false;
			
			// take all the fans away, excluding the one already set
			// and prevent the ghost release
			m_player.enableFanRemoval( true );
			m_player.setFansCount( 0, 0 );
			
			// stop the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			m_ghostSpeed = dynObj.m_linearSpeed;
			dynObj.m_linearSpeed = 0.0f;
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
			
			// restart the ghost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			dynObj.m_linearSpeed = m_ghostSpeed;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_fanRemoved )
			{
				transitionTo( WaitUntilBoyIsHighEnough.class );
			}
		}
		
		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) {}

		@Override
		public void onEntityRemoved(Entity entity) 
		{
			m_fanRemoved |= ( entity instanceof Fan );
		}
	}
	
	// ------------------------------------------------------------------------
	
	class WaitUntilBoyIsHighEnough extends FSMState
	{		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_tutorial.m_gesturePos.set( -1.0f, -1.0f, 0.0f );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_boyGhost.getPosition().m_y >= 3.5f )
			{
				transitionTo( PlaceSecondFan.class );
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	class PlaceSecondFan extends FSMState implements WorldView
	{
		private boolean	 		m_fanPlaced;
		private float			m_ghostSpeed;
		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.PLACE_SECOND_FAN;
			
			m_world.attachView( this );
			
			m_fanPlaced = false;
			
			// give the player a single fan - the one that's supposed to be placed,
			// and prevent the release of a ghost
			m_player.enableFanRemoval( false );
			m_player.setFansCount( 0, 1 );
			
			// stop the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			m_ghostSpeed = dynObj.m_linearSpeed;
			dynObj.m_linearSpeed = 0.0f;
			
			
			// make sure the gesture moves along with the boy
			// to indicate that the user should place the fan immediately there
			m_tutorial.m_gesturePos.set( m_boyGhost.getPosition() ).add( -0.5f, 0.5f, 0.0f );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
			
			// restart the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			dynObj.m_linearSpeed = m_ghostSpeed;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_fanPlaced )
			{
				transitionTo( WaitUntilBoyIsBackOnTrack.class );
			}
		}
		
		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) 
		{
			if ( entity instanceof Fan && !m_fanPlaced )
			{
				m_fanPosition.set( ((Fan)entity).getPosition() );
				m_fanPlaced = true;
			}
		}

		@Override
		public void onEntityRemoved(Entity entity) {}
	}
	
	// ------------------------------------------------------------------------
	
	class WaitUntilBoyIsBackOnTrack extends FSMState
	{		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_tutorial.m_gesturePos.set( -1, -1, 0 );
						
			// take all the fans away, excluding the one already set,
			// and allow to release of a ghost
			m_player.enableGhostRelease( false );
			m_player.enableFanRemoval( false );
			m_player.setFansCount( 0, 0 );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_boyGhost.getPosition().m_x >= 2.3f )
			{
				transitionTo( RemoveSecondFan.class );
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	class RemoveSecondFan extends FSMState implements WorldView
	{
		private boolean			m_fanRemoved;
		private float			m_ghostSpeed;
		
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.REMOVE_SECOND_FAN;
			
			// place the gesture exactly over the placed fan
			m_tutorial.m_gesturePos.set( m_fanPosition );
			
			m_world.attachView( this );
			
			m_fanRemoved = false;
			
			// take all the fans away, excluding the one already set
			// and prevent the ghost release
			m_player.setFansCount( 0, 0 );
			m_player.enableFanRemoval( true );
			
			// stop the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			m_ghostSpeed = dynObj.m_linearSpeed;
			dynObj.m_linearSpeed = 0.0f;
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			m_world.detachView( this );
			
			// restart the gost
			DynamicObject dynObj = m_boyGhost.query( DynamicObject.class );
			dynObj.m_linearSpeed = m_ghostSpeed;
		}
		
		@Override
		public void execute( float deltaTime )
		{
			if ( m_fanRemoved )
			{
				transitionTo( TutorialEnd.class );
			}
		}
		
		@Override
		public void onAttached(World world) {}

		@Override
		public void onDetached(World world) {}

		@Override
		public void onEntityAdded(Entity entity) {}

		@Override
		public void onEntityRemoved(Entity entity) 
		{
			m_fanRemoved |= ( entity instanceof Fan );
		}
	}
		
	
	// ------------------------------------------------------------------------
	
	class TutorialEnd extends FSMState
	{
		@Override
		public void activate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
			
			// give back the missing fan ( the left one, 'cause the right
			// one is either already set on the screen, or is in the inventory )
			// and allow to release of a ghost
			m_player.changeFansCount( 1, 0 );
			m_player.enableGhostRelease( true );
		}
		
		@Override
		public void deactivate()
		{
			m_tutorial.m_state = StartTutorial.State.NOTHING;
		}
	}
	
	// ------------------------------------------------------------------------
	// API
	// ------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * 
	 * @param world
	 * @param entity
	 */
	public StartTutorialController( World world, Entity entity ) 
	{
		super( entity );
		
		m_tutorial = (StartTutorial)entity;
		
		m_world = world;
		
		register( new TutorialStart() );
		register( new PlaceFirstFan() );
		register( new ReleaseGhost() );
		register( new WaitUntilBoyReachesFan() );
		register( new RemoveFirstFan() );
		register( new PlaceSecondFan() );
		register( new RemoveSecondFan() );
		register( new TutorialEnd() );
		register( new WaitUntilBoyIsFullyVisible() );
		register( new WaitUntilBoyIsBackOnTrack() );
		register( new WaitUntilBoyIsHighEnough() );
		begin( TutorialStart.class );

	}
}

