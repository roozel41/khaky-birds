/**
 * 
 */
package com.hypefoundry.bubbly.test.tests.openGL;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.view.Surface;

import com.hypefoundry.engine.core.GLGraphics;
import com.hypefoundry.engine.game.Screen;
import com.hypefoundry.engine.impl.game.GLGame;
import com.hypefoundry.engine.renderer2D.Geometry;


/**
 * @author Paksas
 *
 */
public class MoviesPlayback extends GLGame 
{
	@Override
	public Screen getStartScreen() 
	{
		return new MoviesPlaybackScreen( this );
	}
}

//----------------------------------------------------------------------------

class MoviesPlaybackScreen extends Screen implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, OnFrameAvailableListener
{
	protected static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
	
	Context				m_context;
	GL10				m_gl;
	int 				m_viewportWidth, m_viewportHeight;
	
	MediaPlayer 		m_mediaPlayer;
	Surface				m_renderSurface;

	
	SurfaceTexture		m_renderTexture;
	int 				m_textureId;
	Geometry			m_fullscreenQuad;
	float[]				m_texCoordMtx = new float[16];
	
	float				m_posX, m_posY;
	
	public MoviesPlaybackScreen( GLGame game ) 
	{
		super( game );
		
		m_context = game;
		
	}

	@Override
	public void present( float deltaTime ) 
	{
		if ( m_gl == null )
		{
			return;
		}
		
		m_gl.glPushMatrix();
		
		m_gl.glViewport( 0, 0, m_viewportWidth, m_viewportHeight );
		m_gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		m_gl.glMatrixMode( GL10.GL_PROJECTION );
		m_gl.glLoadIdentity();
		m_gl.glOrthof( 0, m_viewportWidth, 0, m_viewportHeight, 1, -1 );
		
		m_gl.glDisable( GL10.GL_BLEND );
		m_gl.glEnable( GL_TEXTURE_EXTERNAL_OES );

		// bind the render texture to the GL device
		m_renderTexture.updateTexImage();
		m_renderTexture.getTransformMatrix( m_texCoordMtx );

		m_gl.glMatrixMode( GL10.GL_TEXTURE );
		m_gl.glLoadMatrixf( m_texCoordMtx, 0 );
		m_gl.glBindTexture(GL_TEXTURE_EXTERNAL_OES, m_textureId);
		
		m_gl.glMatrixMode( GL10.GL_MODELVIEW );
		m_gl.glTranslatef( m_posX, m_posY, 0);
		
		m_fullscreenQuad.bind();
		m_fullscreenQuad.draw( GL10.GL_TRIANGLES, 0, 6 );
		m_fullscreenQuad.unbind();
		
		m_gl.glPopMatrix();
	}

	@Override
	public void pause() 
	{
		m_mediaPlayer.release();
	}

	@Override
	public void resume() 
	{
		GLGraphics graphics = m_game.getGraphics();
		m_gl = graphics.getGL();
		m_viewportWidth = graphics.getWidth();
		m_viewportHeight = graphics.getHeight();
	
		// create the media player
		m_mediaPlayer = new MediaPlayer();
		m_mediaPlayer.setOnErrorListener( this );
		m_mediaPlayer.setOnPreparedListener( this );
		
		// set the data source
		try
		{
			AssetFileDescriptor movieAsset = m_game.getFileIO().getAssetFileDescriptor( "movies/logo.mp4" );
			m_mediaPlayer.setDataSource( movieAsset.getFileDescriptor(), movieAsset.getStartOffset(), movieAsset.getLength() );//"/mnt/sdcard/filmy/logo.mp4" );
		}
		catch( IllegalStateException ex )
		{
			ex.printStackTrace();
		}
		catch( IOException ex )
		{
			ex.printStackTrace();
		}
		catch( IllegalArgumentException ex )
		{
			ex.printStackTrace();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		
		// set the rendering surface
		{
			// create the texture
			
			int[] textures = new int[1];
			
			// generate one texture pointer and bind it as an external texture.
			GLES20.glGenTextures( 1, textures, 0 );		
			GLES20.glBindTexture( GL_TEXTURE_EXTERNAL_OES, textures[0] );
			
			// No mip-mapping with camera source.
			GLES20.glTexParameterf( GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,  GL10.GL_LINEAR );   		
			GLES20.glTexParameterf( GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
			
			// Clamp to edge is only option.
			GLES20.glTexParameteri( GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );	
			GLES20.glTexParameteri( GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
			
			
			int[] textureIds = new int[1];
			m_gl.glGenTextures( 1, textureIds, 0 );
			
			m_textureId = textureIds[0] ;
			m_renderTexture = new SurfaceTexture( m_textureId );
			m_renderTexture.setOnFrameAvailableListener( this );
		}
		
		m_renderSurface = new Surface( m_renderTexture );
		m_mediaPlayer.setSurface( m_renderSurface );
	
		m_mediaPlayer.prepareAsync(); // prepare async to not block main thread
	}

	@Override
	public void dispose() 
	{
		m_mediaPlayer.release();
	}

	@Override
	public void onPrepared( MediaPlayer player ) 
	{
		int height = player.getVideoHeight();
		int width = player.getVideoWidth();
		
		// We want the movie to fill as much of the screen as possible, while maintaining
		// the correct aspect ratio.
		// So let's see which dimension would be largest after rescaling and calculate the other
		// one using the ratio factor.
		
		float ratio = (float)width/(float)height;
		
		float allowedWidth = Math.min( width, m_viewportWidth );
		float allowedHeight = Math.min( height, m_viewportHeight );
		if ( allowedWidth < allowedHeight )
		{
			// width will change the least, height will get scaled to maintain the correct aspect ratio
			allowedHeight = allowedHeight / ratio;
		}
		else
		{
			// height will change the least, width will get scaled to maintain the correct aspect ratio
			allowedWidth = allowedWidth * ratio;
		}

		// create the fullscreen quad now that we know the dimensions of the movie clip
		initializeFullscreenQuad( m_game.getGraphics(), allowedWidth, allowedHeight );
		
		// initialize rendering position
		m_posX = (float)( m_viewportWidth - allowedWidth ) * 0.5f; 
		m_posY = (float)( m_viewportHeight - allowedHeight ) * 0.5f; 
		
		// start the playback
		player.start();
	}

	@Override
	public boolean onError( MediaPlayer player, int what, int extra ) 
	{
		if ( what == MediaPlayer.MEDIA_ERROR_UNKNOWN )
		{
			int a;
			a = 0;
		}
		else if ( what == MediaPlayer.MEDIA_ERROR_SERVER_DIED )
		{
			int a;
			a = 0;
		}
		return false;
	}
	
	/**
	 * Initializes the fullscreen quad geometry.
	 * 
	 * @param graphics
	 * @param width
	 * @param height
	 */
	private void initializeFullscreenQuad( GLGraphics graphics, float width, float height )
	{
		// create a fullscreen quad
		float[] vertices = new float[] {	
				0.0f,	 	0.0f, 			0.0f, 0.0f,
				width, 		0.0f, 			1.0f, 0.0f,
				width, 		height, 		1.0f, 1.0f,
				0.0f, 		height,			0.0f, 1.0f
		};
		
		short[] indices = new short[] { 0, 1, 2, 0, 2, 3 };
		
		
		m_fullscreenQuad = new Geometry( graphics, 4, 6, false, true );
		m_fullscreenQuad.setVertices( vertices, 0, vertices.length );
		m_fullscreenQuad.setIndices( indices, 0, indices.length );
	}

	@Override
	public void onFrameAvailable( SurfaceTexture surfaceTexture ) 
	{
	}
}
