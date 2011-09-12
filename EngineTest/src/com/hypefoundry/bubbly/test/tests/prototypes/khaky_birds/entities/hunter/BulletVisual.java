/**
 * 
 */
package com.hypefoundry.bubbly.test.tests.prototypes.khaky_birds.entities.hunter;

import com.hypefoundry.engine.core.ResourceManager;
import com.hypefoundry.engine.math.BoundingShape;
import com.hypefoundry.engine.math.Vector3;
import com.hypefoundry.engine.renderer2D.EntityVisual;
import com.hypefoundry.engine.renderer2D.SpriteBatcher;
import com.hypefoundry.engine.renderer2D.TextureRegion;
import com.hypefoundry.engine.world.Entity;

/**
 * @author azagor
 *
 */
public class BulletVisual  extends EntityVisual
{
	private TextureRegion m_pixmap;
	private Bullet        m_bullet;
	private	Vector3 m_velocity 		 = new Vector3();
	private	Vector3 m_tempvelocity   = new Vector3();
	private float m_facing           = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param resMgr
	 * @param entity
	 */
	public BulletVisual( ResourceManager resMgr, Entity entity ) 
	{
		super( entity );
		m_bullet = (Bullet)entity;
		m_pixmap = resMgr.getResource( TextureRegion.class, "khaky_birds_prototype/textures/bullet.xml" );
		m_facing = m_entity.m_facing;
		m_velocity.set(1, 0, 0);
		m_velocity.rotateZ(m_facing );
		
	}

	@Override
	public void draw( SpriteBatcher batcher, float deltaTime ) 
	{
		Vector3 pos = m_entity.getPosition();
		BoundingShape bs = m_entity.getBoundingShape();
		
		
		batcher.drawSprite( pos.m_x, pos.m_y, bs.getWidth(), bs.getHeight(), m_pixmap );
		
		m_tempvelocity.set(m_velocity);
		m_tempvelocity.scale(deltaTime * m_bullet.maxLinearSpeed);	
		m_entity.translate(m_tempvelocity.m_x, m_tempvelocity.m_y, 0 );
		
		
		
	}
}

