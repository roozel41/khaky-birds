/**
 * 
 */
package com.hypefoundry.engine.hud.widgets;


import com.hypefoundry.engine.core.ResourceManager;
import com.hypefoundry.engine.hud.HudWidget;
import com.hypefoundry.engine.hud.visuals.CaptionedHudWidget;
import com.hypefoundry.engine.hud.visuals.ClickableHudWidget;
import com.hypefoundry.engine.util.serialization.DataLoader;

/**
 * A button that can be clicked.
 * 
 * @author Paksas
 */
public class ButtonWidget extends HudWidget implements ClickableHudWidget, CaptionedHudWidget
{
	public String				m_id					= "";
	public String				m_caption				= "";

	@Override
	public void onLoad( ResourceManager resMgr, DataLoader loader ) 
	{
		m_id			= loader.getStringValue( "id" );
		m_caption		= loader.getStringValue( "caption" );
	}

	@Override
	public void onButtonPressed( int id ) 
	{
		m_layout.onButtonPressed( m_id );
	}

	@Override
	public String getCaption() 
	{
		return m_caption;
	}
}
