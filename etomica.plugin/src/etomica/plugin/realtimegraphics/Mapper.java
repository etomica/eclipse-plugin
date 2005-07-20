/*
 * Created on Jul 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.realtimegraphics;

/** A class that allows mapping I2-R2 (2d) with methods for zooming, panning etc
 * @author Henrique
 */

public class Mapper
{
	Mapper()
	{
		setViewPort(0,0,800,600);
		setWindow( 0,0,1,1);
	}
	public void setViewPort( int x1, int y1, int x2, int y2 )
	{
		vporg[0] = x1;
		vporg[1] = y1;
		vpsz[0]= x2-x1;
		vpsz[1] = y2-y1;
	}
	public void resizeViewport( int w, int h )
	{
		vpsz[0] = w;
		vpsz[1] = h;
	}
	public void setWindow( float x1, float y1, float x2, float y2 )
	{
		worg[0]=x1;
		worg[1]=y1;
		wsz[0]=x2-x1;
		wsz[1]=y2-y1;
	}
	
	public void toScreen( float[] wld, int[] scr )
	{
		scr[0] = (int) ( (wld[0]-worg[0])/wsz[0]*vpsz[0] ) + vporg[0];
		scr[1] = (int) ( (wld[1]-worg[1])/wsz[1]*vpsz[1] ) + vporg[1];
	}
	private float[] worg = new float[2];
	private float[] wsz = new float[2];
	private int[] vporg = new int[2];
	private int[] vpsz  = new int[2];
}