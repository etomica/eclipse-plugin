//http://www.lanw.com/training/interop/securityurls.htm

//http://www.geocities.com/SiliconValley/Park/5625/opengl/
//http://www.azillionmonkeys.com/windoze/OpenGLvsDirect3D.html

//http://romka.demonews.com/opengl/demos/other_eng.htm
//http://www.oglchallenge.com/

//http://www.sgi.com/software/opengl/advanced97/notes/node196.html#stencilsection
//http://ask.ii.uib.no/ebt-bin/nph-dweb/dynaweb/SGI_Developer/OpenGL_RM
package etomica.plugin.realtimegraphics;
import org.eclipse.swt.graphics.Color;

import etomica.Atom;
import etomica.Phase;
import etomica.atom.AtomFilter;
import etomica.atom.AtomTypeOrientedSphere;
import etomica.atom.AtomTypeSphere;
import etomica.atom.AtomTypeWell;
import etomica.atom.iterator.AtomIteratorListTabbed;
import etomica.math.geometry.LineSegment;
import etomica.math.geometry.Polyhedron;
import etomica.space.Boundary;
import etomica.space.Vector;
import etomica.space3d.Vector3D;
import org.eclipse.swt.opengl.GL;
import org.eclipse.swt.opengl.GLU;
import org.eclipse.swt.opengl.GLContext;
import org.eclipse.swt.widgets.Canvas;




//Class used to define canvas onto which configuration is drawn
public class SWTOpenGLRenderer implements Renderable {
  
	/** Set the canvas to be used */
	public void setCanvas( Canvas acanvas )
	{
		canvas = acanvas;
		context = new GLContext( canvas );
	}
	/** Set the color pattern to be used */
	public void setColorScheme( ColorScheme cscheme )
	{
		colorscheme = cscheme;
	}
	/** Set the phase to be drawn */
	public void setPhase( Phase aphase )
	{
		phase = aphase;
	}
	
	/** Called prior to any drawing function */
	public void beginRender()
	{
		context.setCurrent();
	}
	/** Called several times for each atom visible */
	public void drawAtom(Atom a, boolean selected)
	{
		
	}
	/** Called after all drawing function are called */
	public void finishRender()
	{
		context.swapBuffers();
	}
	
	private Canvas canvas;
	private GLContext context;
	private ColorScheme colorscheme;
	private Phase phase;

private final double rightClipPlane[] = new double[4];
private final double leftClipPlane[] = new double[4];
private final double topClipPlane[] = new double[4];
private final double bottomClipPlane[] = new double[4];
private final double frontClipPlane[] = new double[4];
private final double backClipPlane[] = new double[4];
private final float MaterialSpecular[] = { 0.8f, 0.8f, 0.8f, 1f };
private final float MaterialShininess[] = { 70f };
private final float materialTransparent[] = {0.0f, 0.8f, 0.0f, 0.5f};
private final float LightSpecular[] = { 1f, 1f, 1f, 1f };
private final float LightDiffuse[] = { 0.93f, 0.93f, 0.93f, 1f };
private final float LightPosition[] = { 1f, 1f, 3f, 0f };
private final float LightPosition2[] = { -1f, -1f, -3f, 0f };
private int sphereList[]; // Storage number for our sphere
private int wellList[]; // Storage number for our wells
private int displayList; // Storage number for displaying image shells
private double sphereListRadius = 0f;
private double wellListRadius = 0f;
private boolean glInitialized = false, canvasInitialized = false;
private double drawExpansionFactor = 1.0;
private final Vector3D vertex = new Vector3D();

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

//The groups of atoms
private Atom sphereCores[];
private Atom sphereWells[];
private Atom sphereRotators[];
private Atom walls[];
//The verticies of said atoms
private float vertSphereCores[];
private int vertSphereWellBase[];
private int vertSphereRotatorBase[];
private float vertWalls[];
    
//Work vector for overflow images
private float[][] originShifts;
private double[][] shellOrigins;

//Local function variables (primarily used in display(), drawDisplay(), and drawBoundary())
private java.awt.Color lastColor;
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
public void setZoom(float z) {shiftZ = z;/*System.out.println(z);*/}
public float getShiftX() {return(shiftX);}
public float getShiftY() {return(shiftY);}
public float getPrevX() {return(prevx);}
public float getPrevY() {return(prevy);}
public float getXRot() {return(xRot);}
public float getYRot() {return(yRot);}
public float getZoom() {return(shiftZ);}

private ColorScheme colorScheme;
private AtomFilter atomFilter;
        
public SWTOpenGLRenderer( Canvas acanvas ) 
{
	context = new GLContext( acanvas );
}
    
//public void preInit() {
  //doubleBuffer = true;
  //stereoView = false;
//}

public synchronized void init() {
  if(glInitialized) return;
  
  // DAK - 09/27/02 rescale display to adjust to size of phase

      if(phase != null) {
          float b = (float)phase.boundary().dimensions().x(0);
//			float z = -70f + (30f/b - 1f)*22f;
//			float z = -190f + (30f/b - 1f)*22f;//08/12/03 DAK changed 70 to 190
			float z = -1.30847f - 2.449f * b;//08/14/03 DAK changed to this by linear regressing observed "best" z vs b values
//			System.out.println(b+"  "+z); 
          setZoom(z);
      }
  
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
  
  /*sphereList = new int[DRAW_QUALITY_MAX];
  wellList = new int[DRAW_QUALITY_MAX];
  sphereList[0] = GL.glGenLists(11);
  wellList[0] = sphereList[0] + 1;
  for(int j = 1; j < DRAW_QUALITY_MAX; j++) {
    sphereList[j] = wellList[j-1] + 1;
    wellList[j] = sphereList[j] + 1;
  }
  displayList = wellList[DRAW_QUALITY_MAX-1] + 1;
  */
  //setUseRepaint(true);
  //setUseFpsSleep(true);
  //setUseRepaint(false);
  //setUseFpsSleep(false);

  rightClipPlane[0] = bottomClipPlane[1] = backClipPlane[2] = 1.;
  leftClipPlane[0] = topClipPlane[1] = frontClipPlane[2] = -1.;
  
	  T0=System.currentTimeMillis();
  //start();
  //stop();
  
  glInitialized = true;
}
    
public synchronized void initialize() {
  int countSphereCores = 0, countSphereWells = 0, countSphereRotators = 0;
  int countWalls = 0, countAll = 0;
  float vertAll[];
  Atom atoms[];

  countAll = phase.speciesMaster.node.leafAtomCount();
  
  if(countAll==0) return;
  
  vertAll = new float[countAll*3];
  atoms = new Atom[countAll];
  
  int i = 0;
  
	drawExpansionShiftX = 0f; 
	drawExpansionShiftY = 0f;
	drawExpansionShiftZ = 0f;
	if(drawExpansionFactor != 1.0) {
		Vector box = phase.boundary().dimensions();
		float mult = (float)(0.5*(drawExpansionFactor - 1.0)/* *(2.0*displayPhase.getImageShells()+1)*/);
		drawExpansionShiftX = (float)(mult*box.x(0));
		drawExpansionShiftY = (float)(mult*box.x(1));
		drawExpansionShiftZ = (float)(mult*box.x(2));
	}

  AtomIteratorListTabbed iter = new AtomIteratorListTabbed(phase.speciesMaster.atomList);
  iter.reset();
  while(iter.hasNext()) {
    Atom a = iter.nextAtom();
    atoms[i/3] = a;
    vertAll[i] = (float)a.coord.position().x(0);// + drawExpansionShiftX;
    vertAll[i+1] = (float)a.coord.position().x(1);// + drawExpansionShiftY;
    vertAll[i+2] = (float)a.coord.position().x(2);// + drawExpansionShiftZ;
    if(a.type instanceof AtomTypeOrientedSphere) countSphereRotators++;
    if(a.type instanceof AtomTypeWell) countSphereWells++;
    if(a.type instanceof AtomTypeSphere) countSphereCores++;
    i += 3;
  }
  
  sphereCores = new Atom[countSphereCores];
  sphereWells = new Atom[countSphereWells];
  sphereRotators = new Atom[countSphereRotators];
  walls = new Atom[countWalls];
  vertSphereCores = new float[countSphereCores*3];
  vertSphereWellBase = new int[countSphereWells];
  vertSphereRotatorBase = new int[countSphereRotators];
  vertWalls = new float[countWalls*3];
  for(int j=0,k=0,l=0,m=0; (j/3) < atoms.length; j+=3) {
    if(atoms[j/3].type instanceof AtomTypeSphere) {
      sphereCores[m/3] = atoms[j/3];
      vertSphereCores[m] = vertAll[j];
      vertSphereCores[m+1] = vertAll[j+1];
      vertSphereCores[m+2] = vertAll[j+2];
      m+=3;
    }
    if(atoms[j/3].type instanceof AtomTypeOrientedSphere) {
      sphereRotators[k] = atoms[j/3];
      vertSphereRotatorBase[k] = m-3;
      k++;
    }
    if(atoms[j/3].type instanceof AtomTypeWell) {
      sphereWells[l] = atoms[j/3];
      vertSphereWellBase[l] = m-3;
      l++;
    }
  }
  canvasInitialized = true;
}

public void reshape(int width, int height) {
  if(!glInitialized) return;
  GL.glMatrixMode(GL.GL_PROJECTION);
  GL.glLoadIdentity();
  GLU.gluPerspective(35, (float)width/(float)height, 1, 500.0);
  //glu.gluPerspective(45, (float)width/(float)height, 0.1, 100.0);//orig
  GL.glMatrixMode(GL.GL_MODELVIEW);
  GL.glLoadIdentity();
//  GL.glViewport(0,0,width,height);
}

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

/**
* Sets the size of the display to a new value and scales the image so that
* the phase fits in the canvas in the same proportion as before.
*/
public void scaleSetSize(int width, int height) {
  System.out.println("Scale: New WxH: "+width+"x"+height);
  /*if(getBounds().width * getBounds().height != 0) {  //reset scale based on larger size change
    double ratio1 = (double)width/(double)getBounds().width;
    double ratio2 = (double)height/(double)getBounds().height;
    double factor = Math.min(ratio1, ratio2);
    //double factor = (Math.abs(Math.log(ratio1)) > Math.abs(Math.log(ratio2))) ? ratio1 : ratio2;
    displayPhase.setScale(displayPhase.getScale()*factor);
    setSize(width, height);
  }*/
}

public void setAtomFilter(AtomFilter filter) {atomFilter = filter;}

  private etomica.math.geometry.Plane plane; // (DAK) 09/21/02
  private etomica.space3d.Vector3D[] points;
  private etomica.space3d.Vector3D normal;
  private etomica.space3d.Vector3D nearest;
  
private void drawDisplay() {
  
  colorScheme = displayPhase.getColorScheme();


  if(displayPhase.getColorScheme() instanceof ColorSchemeCollective) {
      ((ColorSchemeCollective)displayPhase.getColorScheme()).colorAllAtoms(displayPhase.getPhase());
  }

	

  if(walls.length > 0) {
    lastColor = null;
    int i = walls.length - 1;
    i += i<<1;
    while(i >= 0) {
      Atom a = walls[i/3];
      if(!atomFilter.accept(a)) {i-=3; continue;}
      Color c = colorScheme.atomColor(a);
      Vector r = a.coord.position();
      //Update the positions of the atom
      vertWalls[i] = (float)r.x(0) - xCenter + drawExpansionShiftX;
      vertWalls[i+1] = (float)r.x(1) - yCenter + drawExpansionShiftY;
      vertWalls[i+2] = (float)r.x(2) - zCenter + drawExpansionShiftZ;
      //Update the color for the atom
      if(!c.equals(lastColor)) {
        GL.glColor4ub((byte)c.getRed(), (byte)c.getGreen(), (byte)c.getBlue(), (byte)c.getAlpha());
        lastColor = c;
      }
      GL.glPushMatrix();
      GL.glTranslatef(vertWalls[i], vertWalls[i+1], vertWalls[i+2]);
      //!!! Draw wall here
      //Draw overflow images if so indicated
      if(displayPhase.getDrawOverflow()) {
//        setDrawExpansionFactor(1.0);
        if(computeShiftOrigin(a, displayPhase.getPhase().boundary())) {
          int j = originShifts.length;
          while((--j) >= 0) {
            GL.glPushMatrix();
            GL.glTranslatef(originShifts[j][0], originShifts[j][1], originShifts[j][2]);
            //!!! Draw wall here
            GL.glPopMatrix();
          }
        }
      }
      GL.glPopMatrix();
      i-=3;
    }
  }
  if(sphereCores.length > 0) {
    lastColor = null;
    int i = sphereCores.length - 1;
    i += i<<1;
    while(i >= 0) {
      Atom a = sphereCores[i/3];
      if(!atomFilter.accept(a)) {i-=3; continue;}
      Color c = colorScheme.atomColor(a);
      Vector r = a.coord.position();
      //Update the positions of the atom
      vertSphereCores[i] = (float)r.x(0) - xCenter + drawExpansionShiftX;
      vertSphereCores[i+1] = (float)r.x(1) - yCenter + drawExpansionShiftY;
      vertSphereCores[i+2] = (float)r.x(2) - zCenter + drawExpansionShiftZ;
      //Update the color for the atom
      if(!c.equals(lastColor)) {
        GL.glColor4ub((byte)c.getRed(), (byte)c.getGreen(), (byte)c.getBlue(), (byte)c.getAlpha());
        lastColor = c;
      }
      if(sphereListRadius != ((AtomTypeSphere)a.type).radius(a))
        initSphereList(((AtomTypeSphere)a.type).radius(a));
      GL.glPushMatrix();
      GL.glTranslatef(vertSphereCores[i], vertSphereCores[i+1], vertSphereCores[i+2]);
      GL.glCallList(sphereList[getQuality()]);
      //GL.glDrawArrays(GL_TRIANGLE_STRIP, 0, 2);
      //Draw overflow images if so indicated
      if(displayPhase.getDrawOverflow()) {
//        setDrawExpansionFactor(1.0);
        if(computeShiftOrigin(a, displayPhase.getPhase().boundary())) {
          int j = originShifts.length;
          while((--j) >= 0) {
            GL.glPushMatrix();
            GL.glTranslatef(originShifts[j][0], originShifts[j][1], originShifts[j][2]);
            GL.glCallList(sphereList[getQuality()]);
            GL.glPopMatrix();
          }
        }
      }
      GL.glPopMatrix();
      i-=3;
    }
  }
  if(sphereWells.length > 0) {
    int i = sphereWells.length;
    GL.glColor4ub(wR, wG, wB, wA);
    while((--i) >= 0) {
      Atom a = sphereWells[i];
      if(!atomFilter.accept(a)) {continue;}
      if(wellListRadius != ((AtomTypeWell)a.type).wellRadius())
        initWellList(((AtomTypeWell)a.type).wellRadius());
      GL.glPushMatrix();
      GL.glTranslatef(vertSphereCores[vertSphereWellBase[i]], vertSphereCores[vertSphereWellBase[i]+1], vertSphereCores[vertSphereWellBase[i]+2]);
      GL.glCallList(wellList[getQuality()]);
      //Draw overflow images if so indicated
      if(displayPhase.getDrawOverflow()) {
//        setDrawExpansionFactor(1.0);
        if(computeShiftOrigin(a, displayPhase.getPhase().boundary())) {
          int j = originShifts.length;
          while((--j) >= 0) {
            GL.glPushMatrix();
            GL.glTranslatef(originShifts[j][0], originShifts[j][1], originShifts[j][2]);
            GL.glCallList(wellList[getQuality()]);
            GL.glPopMatrix();
          }
        }
      }
      GL.glPopMatrix();
    }
  }
  if(sphereRotators.length > 0) {
    int i = sphereRotators.length;
    GL.glColor3ub(mR, mG, mB);
    while((--i) >= 0) {
      Atom a = sphereRotators[i];
      if(!atomFilter.accept(a)) {continue;}
      GL.glPushMatrix();
      GL.glTranslatef(vertSphereCores[vertSphereRotatorBase[i]], vertSphereCores[vertSphereRotatorBase[i]+1], vertSphereCores[vertSphereRotatorBase[i]+2]);
      ///!!! Draw rotator orientation here
      //Draw overflow images if so indicated
      if(displayPhase.getDrawOverflow()) {
//        setDrawExpansionFactor(1.0);
        if(computeShiftOrigin(a, displayPhase.getPhase().boundary())) {
          int j = originShifts.length;
          while((--j) >= 0) {
            GL.glPushMatrix();
            GL.glTranslatef(originShifts[j][0], originShifts[j][1], originShifts[j][2]);
            ///!!! Draw rotator orientation here
            GL.glPopMatrix();
          }
        }
      }
      GL.glPopMatrix();
    }
  }
  
  //Draw other features if indicated
  if(drawBoundary >= DRAW_BOUNDARY_ALL) {
    GL.glDisable(GL_CLIP_PLANE0);
    GL.glDisable(GL_CLIP_PLANE1);
    GL.glDisable(GL_CLIP_PLANE2);
    GL.glDisable(GL_CLIP_PLANE3);
    GL.glDisable(GL_CLIP_PLANE4);
    GL.glDisable(GL_CLIP_PLANE5);
    GL.glDisable(GL_LIGHT0);
    GL.glDisable(GL_LIGHTING);
    GL.glColor3ub((byte)0, (byte)-1, (byte)-1);
    drawBoundary(0);
    GL.glEnable(GL_LIGHT0);
    GL.glEnable(GL_LIGHTING);
    GL.glEnable(GL_CLIP_PLANE0);
    GL.glEnable(GL_CLIP_PLANE1);
    GL.glEnable(GL_CLIP_PLANE2);
    GL.glEnable(GL_CLIP_PLANE3);
    GL.glEnable(GL_CLIP_PLANE4);
    GL.glEnable(GL_CLIP_PLANE5);
  }
  //////////////******drawing of plane*******/////////////////////////
  //(DAK) added this section 09/21/02
  /* do drawing of all drawing objects that have been added to the display */
  for(Iterator iter=displayPhase.getDrawables().iterator(); iter.hasNext(); ) {
    Object obj = iter.next();
    if(obj instanceof etomica.lattice.LatticePlane) {
      plane = ((etomica.lattice.LatticePlane)obj).planeCopy(plane);
      points = plane.inPlaneSquare(plane.nearestPoint(center,nearest),40., points);
      normal = plane.getNormalVector(normal);
  GL.glDisable(GL_CULL_FACE);
      for(int i=0; i<4; i++) points[i].ME(center);
        GL.glBegin(GL_QUADS);
         GL.glNormal3f((float)normal.x(0), (float)normal.x(1), (float)normal.x(2));
//		   GL.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
         GL.glMaterialfv(GL_FRONT, GL_AMBIENT, materialTransparent);
         GL.glMaterialfv(GL_FRONT, GL_DIFFUSE, materialTransparent);
         GL.glColor4ub(wR, wG, wB, wA);
         GL.glVertex3f((float)points[0].x(0), (float)points[0].x(1), (float)points[0].x(2));
         GL.glVertex3f((float)points[2].x(0), (float)points[2].x(1), (float)points[2].x(2));
         GL.glVertex3f((float)points[1].x(0), (float)points[1].x(1), (float)points[1].x(2));
         GL.glVertex3f((float)points[3].x(0), (float)points[3].x(1), (float)points[3].x(2));
        GL.glEnd();
      }
  GL.glEnable(GL_CULL_FACE);
  }
  //////////////////////////////////////////
}
            
protected boolean computeShiftOrigin(Atom a, Boundary b) {
  if(a.type instanceof AtomTypeSphere)
    originShifts = b.getOverflowShifts(a.coord.position(),((AtomTypeSphere)a.type).radius(a));
  else
    originShifts = new float[0][0];
  if(originShifts.length == 0) return(false);
  return(true);
}
      
/**
* display is the method that handles the drawing of the phase to the screen.
*/
public synchronized void display() {
  //Makes sure there is something to draw
  if(!canvasInitialized) {this.initialize();return;}
  //Ensure GL is initialized correctly
  if (glj.gljMakeCurrent() == false)
    return;
                          
  //Clear The Screen And The Depth Buffer
  GL.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
  //Reset The View
  GL.glLoadIdentity();
  //PhaseTranslate & Zoom to the desired position
//  GL.glTranslatef(shiftX, shiftY, shiftZ-(displayPhase.getImageShells()<<5));//changed to '5' from '7' (08/12/03 DAK)
	GL.glTranslatef(shiftX, shiftY, (float)(shiftZ-2.5*2*displayPhase.getImageShells()*displayPhase.getPhase().boundary().dimensions().x(0)));//changed to this (08/14/03 DAK)
  //Rotate accordingly
  GL.glRotatef(xRot, 1f, 0f, 0f);
  GL.glRotatef(yRot, 0f, 1f, 0f);
  
  //Color all atoms according to colorScheme in DisplayPhase
//  displayPhase.getColorScheme().colorAllAtoms();

  xCenter = (float)(drawExpansionFactor*displayPhase.getPhase().boundary().dimensions().x(0)*.5);
  yCenter = (float)(drawExpansionFactor*displayPhase.getPhase().boundary().dimensions().x(1)*.5);
  zCenter = (float)(drawExpansionFactor*displayPhase.getPhase().boundary().dimensions().x(2)*.5);
  center.E(xCenter, yCenter, zCenter);
  rightClipPlane[3] = leftClipPlane[3] = xCenter + ((2*xCenter)*displayPhase.getImageShells());
  topClipPlane[3] = bottomClipPlane[3] = yCenter + ((2*yCenter)*displayPhase.getImageShells());
  backClipPlane[3] = frontClipPlane[3] = zCenter + ((2*zCenter)*displayPhase.getImageShells());

  GL.glClipPlane(GL_CLIP_PLANE0, rightClipPlane);
  GL.glClipPlane(GL_CLIP_PLANE1, leftClipPlane);
  GL.glClipPlane(GL_CLIP_PLANE2, topClipPlane);
  GL.glClipPlane(GL_CLIP_PLANE3, bottomClipPlane);
  GL.glClipPlane(GL_CLIP_PLANE4, backClipPlane);
  GL.glClipPlane(GL_CLIP_PLANE5, frontClipPlane);

  GL.glEnable(GL_CLIP_PLANE0);
  GL.glEnable(GL_CLIP_PLANE1);
  GL.glEnable(GL_CLIP_PLANE2);
  GL.glEnable(GL_CLIP_PLANE3);
  GL.glEnable(GL_CLIP_PLANE4);
  GL.glEnable(GL_CLIP_PLANE5);

  //Draw periodic images if indicated
  // The following if() block sets up the display list.
  int k = 0;
  if(displayPhase.getImageShells() > 0) {
//    setDrawExpansionFactor(1.0);
    int j = DRAW_QUALITY_VERY_LOW;
    if(displayPhase.getImageShells() == 1) j=DRAW_QUALITY_LOW;
    else if(displayPhase.getImageShells() > 1) j=DRAW_QUALITY_VERY_LOW;
    k = getQuality();
    setQuality(j);
    shellOrigins = displayPhase.getPhase().boundary().imageOrigins(displayPhase.getImageShells());
    //more efficient to save rather than recompute each time
    GL.glNewList(displayList, GL_COMPILE_AND_EXECUTE);
  }
  // We always need to draw the display at least once
  drawDisplay();
  // Finish and compile the display list, then redraw it for each shell image
  if(displayPhase.getImageShells() > 0) {
    GL.glEndList();
    int j = shellOrigins.length;
    while((--j) >= 0) {
      GL.glPushMatrix();
      GL.glTranslated(shellOrigins[j][0],shellOrigins[j][1],shellOrigins[j][2]);
      GL.glCallList(displayList);
      GL.glPopMatrix();
    }
    setQuality(k);
  }

  GL.glDisable(GL_CLIP_PLANE0);
  GL.glDisable(GL_CLIP_PLANE1);
  GL.glDisable(GL_CLIP_PLANE2);
  GL.glDisable(GL_CLIP_PLANE3);
  GL.glDisable(GL_CLIP_PLANE4);
  GL.glDisable(GL_CLIP_PLANE5);
  
  //Draw other features if indicated
  if(drawBoundary >= DRAW_BOUNDARY_OUTLINE) {
    GL.glDisable(GL_LIGHT0);
    GL.glDisable(GL_LIGHTING);
    GL.glColor3ub((byte)0, (byte)-1, (byte)-1);
    drawBoundary(displayPhase.getImageShells());
    if (drawBoundary == DRAW_BOUNDARY_SHELL) {
      int j = displayPhase.getImageShells();
      while((--j) >= 0) {
        drawBoundary(j);
      } 
    }
    GL.glEnable(GL_LIGHT0);
    GL.glEnable(GL_LIGHTING);
  }
  

  Frames++;
  long t=System.currentTimeMillis();
  if(t - T0 >= 5000) {
    double seconds = (double)(t - T0) / 1000.0;
    double fps = (double)Frames / seconds;
//    System.out.println(Frames+" frames in "+seconds+" seconds = "+fps+" FPS");
    T0 = t;
    Frames = 0;
  }
  
  //Swap buffers
  glj.gljSwap();
  glj.gljFree();
  //!!!glj.gljCheckGL();
}
      
private void drawBoundary(int num) {
  GL.glBegin(GL_LINES);
  Polyhedron shape = (Polyhedron)displayPhase.getPhase().boundary().getShape();
  LineSegment[] edges = shape.getEdges();
  for(int i=0; i<edges.length; i++) {
      vertex.E(edges[i].getVertices()[0]);
      vertex.TE((1+2*num));
      GL.glVertex3f((float)vertex.x(0), (float)vertex.x(1), (float)vertex.x(2));
      vertex.E(edges[i].getVertices()[1]);
      vertex.TE((1+2*num));
      GL.glVertex3f((float)vertex.x(0), (float)vertex.x(1), (float)vertex.x(2));
  }
  GL.glEnd();
}
  
/**
* Returns the drawExpansionFactor.
* @return double
*/
public double getDrawExpansionFactor() {
	return drawExpansionFactor;
}

/**
* Sets the drawExpansionFactor.
* @param drawExpansionFactor The drawExpansionFactor to set
*/
public void setDrawExpansionFactor(double drawExpansionFactor) {
	this.drawExpansionFactor = drawExpansionFactor;
	if(displayPhase != null && displayPhase.getPhase() != null) {
		Vector box = displayPhase.getPhase().boundary().dimensions();
		float mult = (float)(0.5*(drawExpansionFactor - 1.0));//*(2*I+1);
		drawExpansionShiftX = (float)(mult*box.x(0));
		drawExpansionShiftY = (float)(mult*box.x(1));
		drawExpansionShiftZ = (float)(mult*box.x(2));
	}
}

}  //end of DisplayPhase.Canvas
