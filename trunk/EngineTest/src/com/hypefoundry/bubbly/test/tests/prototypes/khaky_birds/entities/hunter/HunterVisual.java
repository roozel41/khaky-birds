/**
 * 
 */
package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter;

import com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.pedestrian.Pedestrian;
import com.hypefoundry.engine.core.ResourceManager;
import com.hypefoundry.engine.math.BoundingShape;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.renderer2D.Animation;
import com.hypefoundry.engine.renderer2D.AnimationPlayer;
import com.hypefoundry.engine.renderer2D.EntityVisual;
import com.hypefoundry.engine.renderer2D.SpriteBatcher;
import com.hypefoundry.engine.world.Entity;

/**
 * Visual aspect of a hunter.
 * 
 * @author azagor
 */
public class HunterVisual extends EntityVisual 
{
	private Hunter				m_hunter;
	private AnimationPlayer		m_animationPlayer;

	private int 				ANIM_AIM;
	private int 				ANIM_SHOOT;
	private int 				ANIM_SHITTED;
	
	/**
	 * Constructor.
	 * 
	 * @param resMgr
	 * @param entity
	 */
	public HunterVisual( ResourceManager resMgr, Entity entity ) 
	{
		super( entity );
		
		m_hunter = (Hunter)entity;
		
		// load animations
		Animation aimingAnimation = resMgr.getResource( Animation.class, "khaky_birds_prototype/animations/hunter/aiming.xml");
		Animation shootAnimation = resMgr.getResource( Animation.class, "khaky_birds_prototype/animations/hunter/shootAnimation.xml");
		Animation shittedAnimation = resMgr.getResource( Animation.class, "khaky_birds_prototype/animations/hunter/shittedAnimation.xml");
		
		// create an animation player
		m_animationPlayer = new AnimationPlayer();
		ANIM_AIM = m_animationPlayer.addAnimation( aimingAnimation );
		ANIM_SHOOT = m_animationPlayer.addAnimation( shootAnimation );
		ANIM_SHITTED = m_animationPlayer.addAnimation( shittedAnimation );
	}

	@Override
	public void draw( SpriteBatcher batcher, float deltaTime ) 
	{
		Vector3 pos = m_hunter.getPosition();
		BoundingShape bs = m_hunter.getBoundingShape();
	
		// select an animation appropriate to the state the pedestrian's in
		switch( m_hunter.m_state )
		{
			case Aiming:
			{
				m_animationPlayer.select( ANIM_AIM );
				break;
			}
			
			case Shooting:
			{
				m_animationPlayer.select( ANIM_SHOOT );
				break;
			}
			
			case Shitted:
			{
				m_animationPlayer.select( ANIM_SHITTED );
				break;
			}
		}
		
		// draw the hunter
		batcher.drawSprite( pos.m_x, pos.m_y, bs.getWidth(), bs.getHeight(), m_hunter.m_facing, m_animationPlayer.getTextureRegion( deltaTime ) );	
	}
}