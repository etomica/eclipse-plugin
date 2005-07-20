//http://www.lanw.com/training/interop/securityurls.htm

//http://www.geocities.com/SiliconValley/Park/5625/opengl/
//http://www.azillionmonkeys.com/windoze/OpenGLvsDirect3D.html

//http://romka.demonews.com/opengl/demos/other_eng.htm
//http://www.oglchallenge.com/

//http://www.sgi.com/software/opengl/advanced97/notes/node196.html#stencilsection
//http://ask.ii.uib.no/ebt-bin/nph-dweb/dynaweb/SGI_Developer/OpenGL_RM
package etomica.plugin.realtimegraphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.widgets.Control;


import etomica.graphics2.ColorScheme;
import etomica.graphics2.Renderable;
import osg.RenderWindow;
import osg.OrientedObject;


//Class used to define canvas onto which configuration is drawn
public class OSGRenderer implements Renderable
{
	public OSGRenderer()
	{
		
	}
	/**
	 * @param widget - any swt widget, either composite or not
	 */
	public OSGRenderer( org.eclipse.swt.widgets.Control widget ) 
	{
		setControl( widget );
	}
	
	/** Finalize will trash the opengl renderer context */
	protected void finalize()	throws Throwable {
		dispose();
	}
	
	/** dispose will trash and release all opengl context held */
	public void dispose()
	{
    	setControl( null );
	}

	/** Set the control to render into */
	public void setControl( Control widget )
	{
		if ( osg_render!= null )
			osg_render.dispose();
		if ( widget == null )
			return;
		render_in_control = widget;
		osg_render = new RenderWindow( widget.handle );
	}
	
	/** Set the color pattern to be used */
	public void setColorScheme( ColorScheme cscheme )
	{
		colorscheme = cscheme;
	}

	public void doPaint( PaintEvent event )
	{
		osg_render.render();
	}
	
	protected Control render_in_control;
	protected RenderWindow osg_render;
	protected ColorScheme colorscheme;
	protected ArrayList object_pool;
	protected ArrayList object_stack;


	protected void createObjectPool()
	{
		String[] names = new String[]{ null, "sphere.osg" };
		for ( int j=0; j<names.length; j++ )
		{
			String name = names[j];
			if ( name==null )
				continue;
			OrientedObject obj = new OrientedObject();
			int index = obj.loadFromFile( name );
			object_pool.add( obj );
		}
	}
		
	/** 
	 * @see etomica.graphics2.Renderable#createObject(int)
	 */
	public int createObject(int type) {
		if ( object_pool == null )
			createObjectPool();
		
		int index = -1;
		try 
		{
			OrientedObject obj = (OrientedObject) object_pool.get( type );
			if ( obj==null )
				return -1;
			
			// Make a shallow copy of object in pool
			OrientedObject newobj = obj.copy( false );
			if ( newobj==null )
				return -1;
			
			// Hold in stack
			object_stack.add( newobj );
			
			// Return its index
			index = object_stack.size()-1;
		}
		finally 
		{
			// dummy just to make sure that any exception thrown aobve will not go away
		}
		return index;
	}

	/**
	 * @see etomica.graphics2.Renderable#setObjectPosition(int, float, float,
	 *      float)
	 */
	public void setObjectPosition(int index, float x, float y, float z) {
		try
		{
			OrientedObject obj = (OrientedObject) object_stack.get( index );
			if ( obj==null )
				return;
			obj.translate( new float[]{x,y,z} );
		}
		catch( Exception ex )
		{
			System.err.println( ex.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see etomica.graphics2.Renderable#setObjectColor(int, int)
	 */
	public void setObjectColor(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int, float)
	 */
	public void setObjectProperty(int arg0, int arg1, float arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int, float[])
	 */
	public void setObjectProperty(int arg0, int arg1, float[] arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int,
	 *      java.lang.Object)
	 */
	public void setObjectProperty(int arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see etomica.graphics2.Renderable#getCameraPosition()
	 */
	public float[] getCameraPosition() {
		// TODO Auto-generated method stub
		return null;
	}


}  //end of DisplayPhase.Canvas


/*
 
 private void initSphereList(double sphereRadius) {
	  GL.glNewList(sphereList[GL.DRAW_QUALITY_VERY_LOW], GL.GL_COMPILE);
	  GLU.gluSphere.gluSmoothSphere(this.GL, sphereRadius, 2);
	  GL.glEndList();
	  GL.glNewList(sphereList[DRAW_QUALITY_LOW], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, sphereRadius, 4);
	  GL.glEndList();
	  GL.glNewList(sphereList[DRAW_QUALITY_NORMAL], GL_COMPILE);
	   gluSphere.gluSmoothSphere(this.GL, sphereRadius, 7);
	  GL.glEndList();
	  GL.glNewList(sphereList[DRAW_QUALITY_HIGH], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, sphereRadius, 9);
	  GL.glEndList();
	  GL.glNewList(sphereList[DRAW_QUALITY_VERY_HIGH], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, sphereRadius, 11);
	  GL.glEndList();
	  sphereListRadius = sphereRadius;
	}
	    
	private void initWellList(double wellRadius) {
	  GL.glNewList(wellList[DRAW_QUALITY_VERY_LOW], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, wellRadius, 2);
	  GL.glEndList();
	  GL.glNewList(wellList[DRAW_QUALITY_LOW], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, wellRadius, 4);
	  GL.glEndList();
	  GL.glNewList(wellList[DRAW_QUALITY_NORMAL], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, wellRadius, 7);
	  GL.glEndList();
	  GL.glNewList(wellList[DRAW_QUALITY_HIGH], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, wellRadius, 10);
	  GL.glEndList();
	  GL.glNewList(wellList[DRAW_QUALITY_VERY_HIGH], GL_COMPILE);
	  gluSphere.gluSmoothSphere(this.GL, wellRadius, 13);
	  GL.glEndList();
	  wellListRadius = wellRadius;
	}
	*/