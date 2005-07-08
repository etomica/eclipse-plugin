package etomica.plugin.realtimegraphics;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;

import etomica.Atom;
import etomica.Phase;
import etomica.atom.AtomFilter;
import etomica.atom.iterator.AtomIteratorLeafAtoms;
import etomica.graphics.ColorScheme;
import etomica.graphics.ColorSchemeByType;

/**
 * Superclass for classes that display information from simulation by painting to a canvas.
 * Defines methods useful for dealing with mouse and key events targeted at the display.
 * Much of the class is involved with defining event handling methods to permit display 
 * to be moved or resized; in the future these functions will be handled instead using awt component functions.
 */
public final class ConfigurationCanvas 
implements ControlListener, PaintListener, java.io.Serializable
{
    
    public ConfigurationCanvas(final Canvas canvas) 
    {
    	
        setTimerInterval(10);
        createContents();
        runner = new Runnable() {
        	public void run() {
        		if ( !canvas.isDisposed() ) 
        			canvas.redraw();
        		canvas.getDisplay().timerExec(timerInterval, this);
        	}
        };
        canvas.getDisplay().timerExec(timerInterval, runner);
        setAtomFilter(AtomFilter.ACCEPT_ALL);
        setScale(1.0);
    	colorScheme = new ColorSchemeByType();
    }
    
    private void createContents() {
    	canvas.addControlListener(this);
    	canvas.addPaintListener(this);
    }
	public void paintControl(PaintEvent event) {
    			if(image == null) image = new Image(canvas.getDisplay(), canvas.getBounds());
    			if(gcImage == null) gcImage = new GC(image);
    			gcImage.setBackground(event.gc.getBackground());
    			gcImage.fillRectangle(image.getBounds());
    			doPaint( event );
    			event.gc.drawImage(image, 0, 0);

    }
    
    /** Function that others should implement */
    public void doPaint( PaintEvent event ) {
        if(!isVisible() || getPhase() == null) {return;}
        renderer.render( 1.0 );
        atomIterator.reset();
        while(atomIterator.hasNext()) {
            renderer.addAtom(atomIterator.nextAtom() );
        }
    }
    
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
    
	public ColorScheme getColorScheme() {
		return colorScheme;
	}
	public void setColorScheme(ColorScheme colorScheme) {
		this.colorScheme = colorScheme;
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
	
	public Atom[] getSelectedAtoms() {
		return selectedAtoms;
	}
	public void setSelectedAtoms(Atom[] selectedAtoms) {
		this.selectedAtoms = selectedAtoms;
	}
	public void dispose() {
		canvas.getDisplay().timerExec(-1, runner);
		if(image != null) image.dispose();
		if(gcImage != null) gcImage.dispose();
		image = null;
		gcImage = null;
	}

	/**
	 * Causes image to be resized and redrawn if window is moved.
	 */
	public void controlMoved(ControlEvent e) {
		disposeImage();
	}
	/**
	 * Causes image to be resized and redrawn if window is resized.
	 */
	public void controlResized(ControlEvent e) {
		disposeImage();
	}
	
	private void disposeImage() {
		canvas.getDisplay().timerExec(-1, runner);
		if(image != null) image.dispose();
		if(gcImage != null) gcImage.dispose();
		image = null;
		gcImage = null;
		canvas.getDisplay().timerExec(timerInterval, runner);
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
    

    protected final AtomIteratorLeafAtoms atomIterator = new AtomIteratorLeafAtoms();
    protected AtomFilter atomFilter;
    protected double scale;
    protected Phase phase;
    protected Image image;
    protected GC gcImage;
    private Canvas canvas;
    
    private int timerInterval;
    private Runnable runner;

    protected ColorScheme colorScheme;
    protected Atom[] selectedAtoms = new Atom[1];
    protected Renderable renderer;


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

} //end of ConfigurationCanvas class

