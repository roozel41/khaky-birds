/**
 * 
 */
package com.hypefoundry.engine.hud.visuals;

import com.hypefoundry.engine.hud.HudWidgetVisualTemplate;
import com.hypefoundry.engine.hud.widgets.ButtonState;
import com.hypefoundry.engine.renderer2D.SpriteBatcher;

/**
 * Base button visual template.
 * 
 * @author Paksas
 */
public abstract class ButtonVisualTemplate implements HudWidgetVisualTemplate 
{
	int 				m_id;
	
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 */
	ButtonVisualTemplate( int id )
	{
		m_id = id;
	}
	
	/**
	 * Draws a button with a caption.
	 * 
	 * @param batcher
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param state
	 * @param caption
	 */
	public abstract void drawButton( SpriteBatcher batcher, float x, float y, float width, float height, ButtonState state, String caption );
}
