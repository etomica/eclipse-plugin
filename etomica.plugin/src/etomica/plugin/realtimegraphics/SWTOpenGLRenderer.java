//http://www.lanw.com/training/interop/securityurls.htm

//http://www.geocities.com/SiliconValley/Park/5625/opengl/
//http://www.azillionmonkeys.com/windoze/OpenGLvsDirect3D.html

//http://romka.demonews.com/opengl/demos/other_eng.htm
//http://www.oglchallenge.com/

//http://www.sgi.com/software/opengl/advanced97/notes/node196.html#stencilsection
//http://ask.ii.uib.no/ebt-bin/nph-dweb/dynaweb/SGI_Developer/OpenGL_RM
package etomica.plugin.realtimegraphics;
import java.util.HashMap;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.opengl.GL;
import org.eclipse.swt.opengl.GLContext;
import org.eclipse.swt.opengl.GLU;
import org.eclipse.swt.widgets.Canvas;

import etomica.graphics2.Renderable;


//Class used to define canvas onto which configuration is drawn
public class SWTOpenGLRenderer implements Renderable
{

	public SWTOpenGLRenderer( Canvas acanvas ) 
	{
		setCanvas( acanvas );
	}

	/** Set the canvas to be used */
	public void setCanvas( Canvas acanvas )
	{
		canvas = acanvas;
		context = new GLContext( canvas );
		glInitialized = false;
	}
	
	/** Set the color pattern to be used */
	public void setColorScheme( ColorScheme cscheme )
	{
		colorscheme = cscheme;
	}


	public void render( PaintEvent event )
	{
		context.setCurrent();
  	    if(!glInitialized) 
	  	  initOpenGL();
	    drawScene();
		context.swapBuffers();
	}
	
	public void addObject( RenderObject obj )
	{
		object_list.put( obj, obj );
	}
	
	public void cleanup()
	{
		object_list.clear();
	}

	public void removeObject( RenderObject obj )
	{
		object_list.remove( obj );
	}
	
	protected Canvas canvas;
	protected GLContext context;
	protected ColorScheme colorscheme;
	protected HashMap object_list;
    protected int quadric = 0; // Quadric object used to draw spheres

    private double sphereListRadius = 0f;
    private double wellListRadius = 0f;

    private final float MaterialSpecular[] = { 0.8f, 0.8f, 0.8f, 1f };
    private final float MaterialShininess[] = { 70f };
    private final float materialTransparent[] = {0.0f, 0.8f, 0.0f, 0.5f};
    private final float LightSpecular[] = { 1f, 1f, 1f, 1f };
    private final float LightDiffuse[] = { 0.93f, 0.93f, 0.93f, 1f };
    private final float LightPosition[] = { 1f, 1f, 3f, 0f };
    private final float LightPosition2[] = { -1f, -1f, -3f, 0f };

    private boolean glInitialized = false;
    
    //The transparent grey color for the wells
    private final static byte wR=(byte)200, wG=(byte)200, wB=(byte)200, wA=(byte)160;
    //The marker color for the orientations
    private final static byte mR=(byte)255, mG=(byte)10, mB=(byte)60;

    //Variables for translations and zooms
    private float shiftZ = -70f, shiftX = 0f, shiftY = 0f;
    //Rotation Variables
private float prevx, prevy, xRot = 0f, yRot = 0f;
//Centers the phase in the canvas
private float xCenter, yCenter, zCenter;
private etomica.space3d.Vector3D center = new etomica.space3d.Vector3D();


    
//Work vector for overflow images
private float[][] originShifts;
private double[][] shellOrigins;

private etomica.math.geometry.Plane plane; // (DAK) 09/21/02
private etomica.space3d.Vector3D[] points;
private etomica.space3d.Vector3D normal;
private etomica.space3d.Vector3D nearest;

//Local function variables (primarily used in display(), drawDisplay(), and drawBoundary())
private Color lastColor;
private long T0 = 0, Frames = 0;
private float drawExpansionShiftX = 0f, drawExpansionShiftY = 0f, drawExpansionShiftZ = 0f;
//private TextField scaleText = new TextField();
//private Font font = new Font("sansserif", Font.PLAIN, 10);
//private int annotationHeight = font.getFontMetrics().getHeight();
//private int annotationHeight = 12;
    
public void setShiftX(float x) {shiftX = x;}
public void setShiftY(float y) {shiftY = y;}
public void setPrevX(float x) {prevx = x;}
public void setPrevY(float y) {prevy = y;}
public void setXRot(float x) {xRot = x;}
public void setYRot(float y) {yRot = y;}
public void setZoom(float z) {shiftZ = z; }
public float getShiftX() {return(shiftX);}
public float getShiftY() {return(shiftY);}
public float getPrevX() {return(prevx);}
public float getPrevY() {return(prevy);}
public float getXRot() {return(xRot);}
public float getYRot() {return(yRot);}
public float getZoom() {return(shiftZ);}


    
//public void preInit() {
  //doubleBuffer = true;
  //stereoView = false;
//}

public synchronized void initOpenGL() {
  if(glInitialized) return;
  
	if ( quadric==0 ) 
		quadric = GLU.gluNewQuadric();

  // DAK - 09/27/02 rescale display to adjust to size of phase


  
  //Set the background clear color
  Color c = canvas.getBackground();
  GL.glClearColor((float)c.getRed()/255f, (float)c.getGreen()/255f, (float)c.getBlue()/255f, (float)0.0f);

  //Enables Clearing Of The Depth Buffer
  GL.glClearDepth(1.0);
  //The Type Of Depth To Do
  GL.glDepthFunc(GL.GL_EQUAL);
  //Enables Depth Surface Removal
  GL.glEnable(GL.GL_DEPTH_TEST);
  
  //Face culling? worth it or not?
  GL.glEnable(GL.GL_CULL_FACE);
  
  //Disable Dithering, provides speedup on low end systems
  GL.glDisable(GL.GL_DITHER);
  
  //Enable transparency
  GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
  GL.glEnable(GL.GL_BLEND);

  //Disable normalization
  GL.glDisable(GL.GL_NORMALIZE);
  
  //glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
//   	GL.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);//09/17/02


  
  //Let OpenGL know that we wish to use the fastest systems possible
  //GL.glHint(GL.GL_CLIP_VOLUME_CLIPPING_HINT_EXT, GL.GL_FASTEST);
  GL.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
  GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
  GL.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
  GL.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
  
  //Set the light properties for the system
  GL.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, LightSpecular);
  GL.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, LightDiffuse);
  GL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, LightPosition);
  //Enable light
  GL.glEnable(GL.GL_LIGHT0);
  
  //new 09/21/02 (DAK)
  //Set the light properties for the system
/*   GL.glLightfv(GL_LIGHT1, GL_SPECULAR, LightSpecular);
  GL.glLightfv(GL_LIGHT1, GL_DIFFUSE, LightDiffuse);
  GL.glLightfv(GL_LIGHT1, GL_POSITION, LightPosition2);
  //Enable light
  GL.glEnable(GL_LIGHT1);*/
  //end new
  
  GL.glEnable(GL.GL_LIGHTING);
  
  //Set the material properties for the spheres
  GL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, MaterialSpecular);
  GL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, MaterialShininess);
  //Set the material to track the current color
  GL.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
  GL.glEnable(GL.GL_COLOR_MATERIAL);

  //Enables Smooth Color Shading
  GL.glShadeModel(GL.GL_SMOOTH);
  
  T0=System.currentTimeMillis();

  glInitialized = true;
}
   

public void resize(int width, int height) {
  if(!glInitialized) return;
  GL.glMatrixMode(GL.GL_PROJECTION);
  GL.glLoadIdentity();
  GLU.gluPerspective(35, (float)width/(float)height, 1, 500.0);
  //glu.gluPerspective(45, (float)width/(float)height, 0.1, 100.0);//orig
  GL.glMatrixMode(GL.GL_MODELVIEW);
  GL.glLoadIdentity();
//  GL.glViewport(0,0,width,height);
}


      
/**
* display is the method that handles the drawing of the phase to the screen.
*/
public synchronized void drawScene() {

                          
  //Clear The Screen And The Depth Buffer
  GL.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT|GL.GL_STENCIL_BUFFER_BIT);
  //Reset The View
  GL.glLoadIdentity();
  //PhaseTranslate & Zoom to the desired position
//  GL.glTranslatef(shiftX, shiftY, shiftZ-(displayPhase.getImageShells()<<5));//changed to '5' from '7' (08/12/03 DAK)
	GL.glTranslatef(shiftX, shiftY, shiftZ );
  //Rotate accordingly
  GL.glRotatef(xRot, 1f, 0f, 0f);
  GL.glRotatef(yRot, 0f, 1f, 0f);
  
   drawScene();
   
   Frames++;
  long t=System.currentTimeMillis();
  if(t - T0 >= 5000) {
    double seconds = (double)(t - T0) / 1000.0;
    double fps = (double)Frames / seconds;
//    System.out.println(Frames+" frames in "+seconds+" seconds = "+fps+" FPS");
    T0 = t;
    Frames = 0;
  }
  
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