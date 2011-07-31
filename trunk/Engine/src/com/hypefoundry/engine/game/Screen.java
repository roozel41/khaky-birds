package com.hypefoundry.engine.game;

import java.util.*;

/**
 * This abstract class represents a single screen of the running game.
 * 
 * Example of a screen would be the main menu screen, the options screen, 
 * the main game screen etc.
 *  
 * @author paksas
 *
 */
public abstract class Screen implements UpdatesManager 
{
	
	/// Host game instance
	protected final Game 			m_game;
	private List<Updatable>			m_updatables;
	private List<Updatable>			m_updatablesToAdd;
	private List<Updatable>			m_updatablesToRemove;
	
	/**
	 * Constructor.
	 * 
	 * @param game				host game
	 */
	public Screen( Game game ) 
	{
		this.m_game = game;
		
		// create the list that will store the registered updatable objects
		m_updatables = new ArrayList<Updatable>();
		m_updatablesToAdd = new ArrayList<Updatable>();
		m_updatablesToRemove = new ArrayList<Updatable>();
	}
	
	/**
	 * Update the state of the screen.
	 * 
	 * @param deltaTime
	 */
	public final void update( float deltaTime )
	{
		// remove updatables			
		for ( Updatable updatable : m_updatablesToRemove )
		{
			m_updatables.remove( updatable );
		}
		m_updatablesToRemove.clear();
		
		// add updatables			
		for ( Updatable updatable : m_updatablesToAdd )
		{
			m_updatables.add( updatable );
		}
		m_updatablesToAdd.clear();
		
		// update updatables		
		for ( Updatable updatable : m_updatables )
		{
			updatable.update( deltaTime );
		}
	}
	
	/**
	 * Draw the screen in the frame buffer.
	 * 
	 * @param deltaTime
	 */
	public abstract void present(float deltaTime);
	
	/**
	 * Called when the screen's functionality should be paused.
	 */
	public abstract void pause();
	
	/**
	 * Called when the screen's functionality should resume 
	 * after it's been paused.
	 */
	public abstract void resume();
	
	/**
	 * Called when the screen is about to be closed. 
	 * This is the place where you want to clean up after the screen.
	 */
	public abstract void dispose();
	
	@Override
	public void addUpdatable( Updatable updatable )
	{
		if ( updatable != null )
		{
			m_updatablesToRemove.remove( updatable );
			m_updatablesToAdd.add( updatable );
		}
	}
	
	@Override
	public void removeUpdatable( Updatable updatable )
	{
		if ( updatable != null )
		{
			m_updatablesToAdd.remove( updatable );
			m_updatablesToRemove.add( updatable );
		}
	}
}