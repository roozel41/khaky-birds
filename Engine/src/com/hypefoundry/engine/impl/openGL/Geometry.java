/**
 * 
 */
package com.hypefoundry.engine.impl.openGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.hypefoundry.engine.impl.openGL.GLGraphics;

/**
 * The class instance contains geometry that can be rendered using an OpenGL context.
 * 
 * @author paksas
 */
public class Geometry 
{
	private final GLGraphics 		m_graphics;
	private final boolean 			m_hasColor;
	private final boolean 			m_hasTexCoords;
	private final int 				m_vertexSize;
	private final FloatBuffer 		m_vertices;
	private final ShortBuffer 		m_indices;
	
	/**
	 * Constructor.
	 * 
	 * @param graphics
	 * @param maxVertices
	 * @param maxIndices
	 * @param hasColor
	 * @param hasTexCoords
	 */
	public Geometry( GLGraphics graphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords ) 
	{
		m_graphics = graphics;
		m_hasColor = hasColor;
		m_hasTexCoords = hasTexCoords;
		m_vertexSize = ( 2 + ( hasColor ? 4 : 0 ) + ( hasTexCoords ? 2 : 0 ) ) * 4;
			
		// allocate the vertex buffer
		ByteBuffer buffer = ByteBuffer.allocateDirect( maxVertices * m_vertexSize );
		buffer.order( ByteOrder.nativeOrder() );
		m_vertices = buffer.asFloatBuffer();
			
		// allocate the index buffer, if needed
		if( maxIndices > 0 ) 
		{
			buffer = ByteBuffer.allocateDirect( maxIndices * Short.SIZE / 8 );
			buffer.order( ByteOrder.nativeOrder() );
			m_indices = buffer.asShortBuffer();
		} 
		else 
		{
			m_indices = null;
		}
	}
	
	/**
	 * Set the geometry vertices
	 * 
	 * @param vertices
	 * @param offset
	 * @param length
	 */
	public void setVertices( float[] vertices, int offset, int length )
	{
		m_vertices.clear();
		m_vertices.put(vertices, offset, length);
	}
	
	/**
	 * Set the geometry indices.
	 * 
	 * @param indices
	 * @param offset
	 * @param length
	 */
	public void setIndices( short[] indices, int offset, int length ) 
	{
		m_indices.clear();
		m_indices.put( indices, offset, length );
		m_indices.flip();
	}
	
	/**
	 * Binds the geometry to the current device context before drawing.
	 */
	public void bind()
	{
		GL10 gl = m_graphics.getGL();
		
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		m_vertices.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, m_vertexSize, m_vertices);
		if( m_hasColor ) 
		{
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			m_vertices.position( 2 );
			gl.glColorPointer( 4, GL10.GL_FLOAT, m_vertexSize, m_vertices );
		}
		
		if( m_hasTexCoords ) 
		{
			gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
			m_vertices.position( m_hasColor ? 6 : 2 );
			gl.glTexCoordPointer( 2, GL10.GL_FLOAT, m_vertexSize, m_vertices );
		}
	}
	
	/**
	 * Renders the geometry.
	 */
	public void draw( int primitiveType, int offset, int numVertices ) 
	{
		GL10 gl = m_graphics.getGL();
				
		if( m_indices != null ) 
		{
			m_indices.position( offset );
			gl.glDrawElements( primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, m_indices );
		} 
		else 
		{
			gl.glDrawArrays( primitiveType, offset, numVertices );
		}
		
		if( m_hasTexCoords )
		{
			gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		}
		
		if( m_hasColor )
		{
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
	
	/**
	 * Unbinds the geometry from the drawing context once it's not needed for drawing any more.
	 */
	public void unbind()
	{
		GL10 gl = m_graphics.getGL();
		
		if( m_hasTexCoords )
		{
			gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		}
		
		if( m_hasColor )
		{
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
}
