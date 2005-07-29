//http://www.lanw.com/training/interop/securityurls.htm

//http://www.geocities.com/SiliconValley/Park/5625/opengl/
//http://www.azillionmonkeys.com/windoze/OpenGLvsDirect3D.html

//http://romka.demonews.com/opengl/demos/other_eng.htm
//http://www.oglchallenge.com/

//http://www.sgi.com/software/opengl/advanced97/notes/node196.html#stencilsection
//http://ask.ii.uib.no/ebt-bin/nph-dweb/dynaweb/SGI_Developer/OpenGL_RM
package etomica.plugin.realtimegraphics;
import org.eclipse.swt.widgets.Control;

import osg.OrientedObject;
import osg.RenderWindow;
import etomica.graphics2.Color;
import etomica.graphics2.ColorScheme;
import etomica.graphics2.Renderable;
import etomica.space.Vector;


//Class used to define canvas onto which configuration is drawn
public class OSGRenderer implements Renderable
{
	public abstract class Shape implements Renderable.Shape
	{
		public void setPosition(Vector pos) {
			obj.setPosition( toFloatArray(pos) );
		}

		public void setScale(Vector scale) {
			obj.setScale( toFloatArray(scale) );
		}

		public void setRotation(Vector fromvec, Vector tovec) {
			// TODO implement!
		}

		public void setRotation(float angle, Vector axis) {
			// TODO implement
		}

		public void setColor(int cindex) {
			color_index =cindex;
			if ( color_scheme!=null )
			{
				Color color = color_scheme.getColor( cindex );
				// obj.setColor( color );
			}
		}

		public void setColorScheme(ColorScheme scheme) {
			color_scheme = scheme;
		}
		
		private ColorScheme color_scheme;
		private int color_index = 0; 
		protected OrientedObject obj;
	};
	
	public class Sphere extends Shape implements Renderable.Sphere
	{
		public Sphere()
		{
			obj = OrientedObject.createSphere();
			// Add to scene
			osg_render.addObject( obj );
		}
	};
	
	public class Polyline extends Shape implements Renderable.Polyline
	{
		public Polyline()
		{
			obj = new OrientedObject();
			// Add to scene
			osg_render.addObject( obj );
		}
		public void appendLine(Vector A, Vector B ) 
		{
			OrientedObject line = OrientedObject.createLine( toFloatArray( B.M(A) ) );
			line.setPosition( toFloatArray(A) );
			obj.addChild( line );
		}
	};
	
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

	public void doPaint()
	{
		osg_render.render();
	}
	
	public void zoomAll()
	{
		osg_render.zoomAll();
	}
	
	protected Control render_in_control;
	protected RenderWindow osg_render;
	static protected float[] tmp_float_array = new float[3];
	protected float[] toFloatArray( Vector v )
	{
		tmp_float_array[0] = (float)v.x(0);
		tmp_float_array[1] = (float)v.x(1);
		tmp_float_array[2] = (float)v.x(2);
		return tmp_float_array;
	}

		

	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#createSphere()
	 */
	public Renderable.Sphere createSphere() {
		return new OSGRenderer.Sphere();
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#createPoly()
	 */
	public Renderable.Polyline createPoly() {
		return new OSGRenderer.Polyline();
	}
} 

