package etomica.plugin.realtimegraphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import etomica.graphics2.Renderable;
import etomica.graphics2.ColorScheme;

    /* GCRenderer draws the atom configuration inside a org.eclipse.swt.graphics.GC
     * @author Henrique Bucher
     */

//Class used to define canvas onto which configuration is drawn
public class GCRenderer implements Renderable {
    
	protected abstract class Drawable {
		protected Color color;
		public void setColor( Color c ) { color = c; }
		public abstract void draw( GC gc, Mapper map ); 
		public abstract void setPosition( float[] pos );
	};
	protected class Ellipse extends Drawable {
		private float[] min = new float[2];
		private float[] max = new float[2];
		
		Ellipse() { min[0]=0; min[1]=0; max[0]=1; max[1]=1; }
		public void draw( GC gc, Mapper m )
		{
            gc.setBackground( color );
            m.toScreen( min, scrmin );
            m.toScreen( max, scrmax );
            gc.fillOval( scrmin[0], scrmin[1], scrmax[0], scrmax[1] );
		}
		public void setPosition( float[] pos )
		{
			float dx2 = 0.5f*(max[0]-min[0]);
			float dy2 = 0.5f*(max[1]-min[1]);
			min[0]=pos[0]-dx2;
			min[1]=pos[1]-dx2;
			max[0]=min[0]+dx2;
			max[1]=min[1]+dy2;
		}
		public void setSize( float sx, float sy )
		{
			float dx2 = 0.5f*(max[0]-min[0]);
			float dy2 = 0.5f*(max[1]-min[1]);
			float x = 0.5f*(max[0]+min[0]);
			float y = 0.5f*(max[1]+min[1]);
			min[0]=x-dx2;
			min[1]=y-dy2;
			max[0]=x+dx2;
			max[1]=y+dy2;
		}
	};

	// Temporary scratch space to be used by Drawables
	static final int[] scrmin = new int[2];
	static final int[] scrmax = new int[2];
	static final float[] tmp = new float[4];

	Mapper mapper = new Mapper();
    private int[] shiftOrigin = new int[2];     //work vector for drawing overflow images
    private Color[] colors;
    private final int[] phaseOrigin = new int[2];
    private double toPixels;
    protected ColorScheme color_scheme;
    protected ArrayList objects = new ArrayList();
    protected Display display;

    public GCRenderer( Display d ) 
    {
    	display = d;
    	
    }
    public void updateInternalColorTable()
    {
    	int ncolors = color_scheme.getNumColors();
    	colors = new Color[ncolors];
    	for ( int j=0; j<ncolors; j++ )
    	{
    		etomica.graphics2.Color c = color_scheme.getColor( j );
    		int red = (int)( 255*c.r +0.5);
    		int green = (int)( 255*c.g +0.5);
    		int blue = (int) ( 255*c.b +0.5);
    		colors[j]= new Color( display, red, green, blue );
    	}
    }
    
    public void doPaint( PaintEvent event )
    {
    	GC gc = event.gc;
    	Iterator ia = objects.iterator();
    	while ( ia.hasNext() )
    	{
    		Drawable dr = (Drawable) ia.next();
    		dr.draw( gc, mapper );
    	}
    }
    
	public void doResize(int w, int h ) {
		mapper.resizeViewport( w, h);
	}


	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#createObject(int)
	 */
	public int createObject(int type ) {
		// TODO Auto-generated method stub
		switch ( type )
		{
		case Renderable.ELLIPSE:
			objects.add( new Ellipse() );
			break;
		}
		return 0;
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setObjectPosition(int, float, float, float)
	 */
	public void setObjectPosition(int index, float x, float y, float z) {
		tmp[0]=x;
		tmp[1]=y;
		Drawable obj = (Drawable) objects.get(index);
		obj.setPosition( tmp );		
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setObjectColor(int, int)
	 */
	public void setObjectColor(int index, int cindex) {
		Drawable obj = (Drawable) objects.get(index);
		Color mycolor = colors[ cindex ];
		obj.setColor( mycolor );
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int, float)
	 */
	public void setObjectProperty(int arg0, int arg1, float arg2) {
		
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int, float[])
	 */
	public void setObjectProperty(int arg0, int arg1, float[] arg2) {
		
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setObjectProperty(int, int, java.lang.Object)
	 */
	public void setObjectProperty(int arg0, int arg1, Object arg2) {
		
	}
	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#getCameraPosition()
	 */
	public float[] getCameraPosition() {
		return null;
	}

	/* (non-Javadoc)
	 * @see etomica.graphics2.Renderable#setColorScheme(etomica.graphics2.ColorScheme)
	 */
	public void setColorScheme(ColorScheme sc) {
		color_scheme = sc;
		updateInternalColorTable();		
	}


    
}  //end of DisplayPhase.Canvas
