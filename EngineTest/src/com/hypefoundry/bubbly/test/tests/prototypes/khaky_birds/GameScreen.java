package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds;


import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.bird.Bird;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.bird.BirdController;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.bird.BirdVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.cables.ElectricCables;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.cables.ElectricCablesAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.cables.ElectricCablesVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.Crap;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.CrapAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.CrapVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.DemolisheCrap;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.DemolisheCrapAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.DemolisheCrapVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.GranadeCrap;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.GranadeCrapAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.crap.GranadeCrapVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.falcon.Falcon;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.falcon.FalconAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.falcon.FalconVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.gameCamera.GameCamera;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.gameCamera.GameCameraController;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.ground.Ground;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.ground.GroundVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hideout.Hideout;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hideout.HideoutAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hideout.HideoutVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.Bullet;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.BulletBody;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.BulletVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.Fire;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.Hunter;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.HunterAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter.HunterVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.Pedestrian;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.PedestrianAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.PedestrianVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.perkPedestrian.PerkPedestrian;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.perkPedestrian.PerkPedestrianAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.perkPedestrian.PerkPedestrianVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.shock.ElectricShock;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.shock.ElectricShockAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.shock.ElectricShockVisual;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.zombie.Zombie;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.zombie.ZombieAI;
import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.zombie.ZombieVisual;
import com.hypefoundry.engine.controllers.ControllersView;
import com.hypefoundry.engine.controllers.EntityController;
import com.hypefoundry.engine.controllers.EntityControllerFactory;
import com.hypefoundry.engine.util.FPSCounter;
import com.hypefoundry.engine.util.serialization.xml.XMLDataLoader;
import com.hypefoundry.engine.world.Entity;
import com.hypefoundry.engine.world.EntityEvent;
import com.hypefoundry.engine.game.Game;
import com.hypefoundry.engine.game.Screen;
import com.hypefoundry.engine.world.World;
import com.hypefoundry.engine.world.serialization.EntityFactory;
import com.hypefoundry.engine.physics.PhysicalBody;
import com.hypefoundry.engine.physics.PhysicalBodyFactory;
import com.hypefoundry.engine.physics.PhysicsView;
import com.hypefoundry.engine.physics.bodies.CollisionBody;
import com.hypefoundry.engine.renderer2D.EntityVisual;
import com.hypefoundry.engine.renderer2D.EntityVisualFactory;
import com.hypefoundry.engine.renderer2D.Renderer2D;
import com.hypefoundry.engine.renderer2D.animation.AnimEventFactory;
import com.hypefoundry.engine.renderer2D.animation.Animation;

public class GameScreen extends Screen 
{
	World										m_world;
	Renderer2D									m_worldRenderer;
	ControllersView								m_controllersView;
	PhysicsView									m_physicsView;
	
	FPSCounter									m_fpsCounter = new FPSCounter();
	
	/**
	 * Constructor.
	 * 
	 * @param game				host game
	 */
	public GameScreen( Game game ) 
	{
		super( game );
		
		// create the game world
		m_world = new World();
		
		// serialization support
		m_world.registerEntity( Bird.class, new EntityFactory() { @Override public Entity create() { return new Bird(); } } );
		m_world.registerEntity( ElectricCables.class, new EntityFactory() { @Override public Entity create() { return new ElectricCables(); } } );
		m_world.registerEntity( ElectricShock.class, new EntityFactory() { @Override public Entity create() { return new ElectricShock(); } } );
		m_world.registerEntity( Ground.class, new EntityFactory() { @Override public Entity create() { return new Ground(); } } );
		m_world.registerEntity( Pedestrian.class, new EntityFactory() { @Override public Entity create() { return new Pedestrian(); } } );
		m_world.registerEntity( Crap.class, new EntityFactory() { @Override public Entity create() { return new Crap(); } } );
		m_world.registerEntity( DemolisheCrap.class, new EntityFactory() { @Override public Entity create() { return new DemolisheCrap(); } } );
		m_world.registerEntity( GranadeCrap.class, new EntityFactory() { @Override public Entity create() { return new GranadeCrap(); } } );
		m_world.registerEntity( Falcon.class, new EntityFactory() { @Override public Entity create() { return new Falcon(); } } );
		m_world.registerEntity( Hunter.class, new EntityFactory() { @Override public Entity create() { return new Hunter(); } } );
		m_world.registerEntity( Bullet.class, new EntityFactory() { @Override public Entity create() { return new Bullet(); } } );
		m_world.registerEntity( Zombie.class, new EntityFactory() { @Override public Entity create() { return new Zombie(); } } );
		m_world.registerEntity( Hideout.class, new EntityFactory() { @Override public Entity create() { return new Hideout(); } } );
		m_world.registerEntity( PerkPedestrian.class, new EntityFactory() { @Override public Entity create() { return new PerkPedestrian(); } } );
		m_world.registerEntity( GameCamera.class, new EntityFactory() { @Override public Entity create() { return new GameCamera(); } } );
		
		// register animation events
		Animation.registerAnimEvent( Fire.class, new AnimEventFactory() { @Override public EntityEvent create() { return new Fire(); } } );
		
		// load the world
		try 
		{
			InputStream worldFileStream = game.getFileIO().readAsset( "khaky_birds_prototype/test_world.xml" );
			m_world.load( XMLDataLoader.parse( worldFileStream, "World" ), m_resourceManager );
		} 
		catch ( IOException e ) 
		{
			Log.d( "Game", "Error while loading world" );
			throw new RuntimeException( e );
		}
		
		// create the views
		m_worldRenderer = new Renderer2D( game, 480, 800 );
		m_physicsView = new PhysicsView( 2.0f ); // TODO: configure cell size
		m_controllersView = new ControllersView( this );
		
		// register visuals
		m_world.attachView( m_worldRenderer );
		m_worldRenderer.register( Bird.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new BirdVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( ElectricCables.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new ElectricCablesVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( ElectricShock.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new ElectricShockVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Ground.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new GroundVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Pedestrian.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new PedestrianVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Crap.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new CrapVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( DemolisheCrap.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new DemolisheCrapVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( GranadeCrap.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new GranadeCrapVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Falcon.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new FalconVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Hunter.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new HunterVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Bullet.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new BulletVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Zombie.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new ZombieVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( Hideout.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new HideoutVisual( m_resourceManager, parentEntity ); } } );
		m_worldRenderer.register( PerkPedestrian.class, new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new PerkPedestrianVisual( m_resourceManager, parentEntity ); } } );

		// register controllers
		m_world.attachView( m_controllersView );
		m_controllersView.register( Bird.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new BirdController( m_game.getInput(), m_worldRenderer.getCamera(), parentEntity ); } } );
		m_controllersView.register( ElectricCables.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new ElectricCablesAI( m_world, parentEntity ); } } );
		m_controllersView.register( ElectricShock.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new ElectricShockAI( parentEntity ); } } );
		m_controllersView.register( Pedestrian.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new PedestrianAI( parentEntity ); } } );
		m_controllersView.register( Crap.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new CrapAI( m_world, parentEntity ); } } );
		m_controllersView.register( DemolisheCrap.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new DemolisheCrapAI( m_world, parentEntity ); } } );
		m_controllersView.register( GranadeCrap.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new GranadeCrapAI( m_world, parentEntity ); } } );
		m_controllersView.register( Falcon.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new FalconAI( m_world, parentEntity ); } } );
		m_controllersView.register( Hunter.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new HunterAI( m_world, parentEntity ); } } );
		m_controllersView.register( Zombie.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new ZombieAI( parentEntity ); } } );
		m_controllersView.register( Hideout.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new HideoutAI( m_world, parentEntity ); } } );
		m_controllersView.register( PerkPedestrian.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new PerkPedestrianAI( m_world, parentEntity ); } } );
		m_controllersView.register( GameCamera.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new GameCameraController( m_world, parentEntity, m_worldRenderer.getCamera() ); } } );
		
		// register physics
		m_world.attachView( m_physicsView );
		m_physicsView.register( Bird.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( ElectricShock.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Pedestrian.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Crap.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( DemolisheCrap.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( GranadeCrap.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Falcon.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Hunter.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Bullet.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new BulletBody( parentEntity ); } } );
		m_physicsView.register( Zombie.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( Hideout.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		m_physicsView.register( PerkPedestrian.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new CollisionBody( parentEntity, true ); } } );
		
		// register the updatables
		addUpdatable( m_world );
		addUpdatable( m_physicsView );
		
		// add the game camera to the world
		m_world.addEntity( new GameCamera() );
	}

	@Override
	public void present( float deltaTime ) 
	{			
		// draw the world contents
		m_worldRenderer.draw( deltaTime );
		// m_fpsCounter.logFrame();
	}

	@Override
	public void pause() 
	{
		m_resourceManager.releaseResources();
	}

	@Override
	public void resume() 
	{
		m_resourceManager.loadResources();
	}

	@Override
	public void dispose() 
	{
	}

}
