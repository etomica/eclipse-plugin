package etomica.ide.ui.configurationview;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import etomica.AtomFilter;
import etomica.AtomIteratorLeafAtoms;
import etomica.Phase;
import etomica.Simulation;
import etomica.Space2D;
import etomica.SpeciesSpheresMono;

/**
 * Superclass for classes that display information from simulation by painting to a canvas.
 * Defines methods useful for dealing with mouse and key events targeted at the display.
 * Much of the class is involved with defining event handling methods to permit display 
 * to be moved or resized; in the future these functions will be handled instead using awt component functions.
 * 
 * @see DisplayPhase.Canvas
 */
public abstract class ConfigurationCanvas implements ControlListener {

    /**
    * Variable specifying whether a line tracing the boundary of the display should be drawn
    * Default value is <code>BOUNDARY_OUTLINE</code>
    */
    int drawBoundary = DRAW_BOUNDARY_OUTLINE;

    /**
     * Variable that sets the quality of the rendered image.
     */
    int quality = DRAW_QUALITY_NORMAL;

    /** 
     * Flag to indicate if value of scale should be superimposed on image
     */
    boolean writeScale = false;
    
    /**
     *  Sets the quality of the rendered image, false = low, true = high
      */
    boolean highQuality = false;

    public ConfigurationCanvas(final Composite parent) {
    	this.parent = parent;
    	display = parent.getDisplay();
    	canvas = new Canvas(parent, SWT.NO_BACKGROUND);
        setTimerInterval(10);
        createContents();
        runner = new Runnable() {
        	public void run() {
        		canvas.redraw();
        		display.timerExec(timerInterval, this);
        	}
        };
    	canvas.addControlListener(this);
        parent.getDisplay().timerExec(timerInterval, runner);
        setAtomFilter(AtomFilter.ACCEPT_ALL);
        setScale(1.0);
        
        Simulation sim = new Simulation(new Space2D());
//        Phase phase = new Phase(sim);
//        SpeciesSpheresMono species = new SpeciesSpheresMono(sim);
//        sim.elementCoordinator.go();
//        setPhase(phase);
    }
    
    private void createContents() {
    	canvas.addPaintListener(new PaintListener() {
    		public void paintControl(PaintEvent event) {
    			if(image == null) image = new Image(display, canvas.getBounds());
    			if(gcImage == null) gcImage = new GC(image);
    			gcImage.setBackground(event.gc.getBackground());
    			gcImage.fillRectangle(image.getBounds());
    			doPaint(gcImage);
    			event.gc.drawImage(image, 0, 0);
    		}
    	});
    }
    double t = 0;
    public abstract void doPaint(GC gcImage);
    
    public void setPhase(Phase phase) {
    	this.phase = phase;
    	atomIterator.setPhase(phase);
    }
    
    public Phase getPhase() {
    	return phase;
    }
    
    public boolean isVisible() {
    	return true;
    }
 
    public void setAtomFilter(AtomFilter filter) {atomFilter = filter;} 

    public void setWriteScale(boolean s) {writeScale = s;}
    public boolean getWriteScale() {return(writeScale);}

    public void setQuality(int q) {
      if(q > DRAW_QUALITY_VERY_HIGH)
        q -= DRAW_QUALITY_MAX;
      if(q < DRAW_QUALITY_VERY_LOW)
        q += DRAW_QUALITY_MAX;
      quality = q;
    }
    public int getQuality() {return(quality);}
    
    public void setDrawBoundary(int b) {
      if(b>DRAW_BOUNDARY_ALL)
        b-=DRAW_BOUNDARY_MAX;
      else if(b<DRAW_BOUNDARY_NONE)
        b+=DRAW_BOUNDARY_MAX;
      drawBoundary = b;
    }
    public int getDrawBoundary() {return drawBoundary;}

	public int getTimerInterval() {
		return timerInterval;
	}
	public void setTimerInterval(int timerInterval) {
		this.timerInterval = timerInterval;
	}

	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}

	public void dispose() {
		display.timerExec(-1, runner);
		if(image != null) image.dispose();
		if(gcImage != null) gcImage.dispose();
		image = null;
		gcImage = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlMoved(ControlEvent e) {
		disposeImage();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
		disposeImage();
	}
	
	private void disposeImage() {
		display.timerExec(-1, runner);
		if(image != null) image.dispose();
		if(gcImage != null) gcImage.dispose();
		image = null;
		gcImage = null;
		display.timerExec(timerInterval, runner);
	}
	
    static final int DRAW_QUALITY_VERY_LOW = 0;
    static final int DRAW_QUALITY_LOW = 1;
    static final int DRAW_QUALITY_NORMAL = 2;
    static final int DRAW_QUALITY_HIGH = 3;
    static final int DRAW_QUALITY_VERY_HIGH = 4;
    static final int DRAW_QUALITY_MAX = 5;
    //Boundary Constants
    static final int DRAW_BOUNDARY_NONE = 0;
    static final int DRAW_BOUNDARY_OUTLINE = 1;
    static final int DRAW_BOUNDARY_SHELL = 2;
    static final int DRAW_BOUNDARY_ALL = 3;
    static final int DRAW_BOUNDARY_MAX = 4;
    
    protected final Composite parent;
    protected final AtomIteratorLeafAtoms atomIterator = new AtomIteratorLeafAtoms();
    protected AtomFilter atomFilter;
    protected Canvas canvas;
    protected Display display;
    protected double scale;
    protected Phase phase;
    private int timerInterval;
    private Runnable runner;
    private Image image;
    private GC gcImage;

} //end of ConfigurationCanvas class

