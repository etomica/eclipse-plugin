/*
 * Created on Jul 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.realtimegraphics;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OSGWidget {

    protected final Control parent;
    private final OSGRenderer renderer;
    private double delay = 100; // ms
    
    /**
     * Creates a new scene owned by the specified parent component.
     * 
     * @param parent The parent of this scene.
     */
    public OSGWidget(Control parentControl) {
    	//canvas = new Canvas( parent, SWT.NONE );
        parent = parentControl;
        
        parent.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Point p = parent.getSize();
                renderer.resize(p.x,p.y);
            }
        });  
        parent.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                dispose();
            }

        });
        
        //Rectangle clientArea = parent.getClientArea();
        //canvas.setSize(clientArea.width, clientArea.height);
        renderer = new OSGRenderer( parent );
    }
    
    public void dispose() {
    	renderer.dispose();
    }
    
    /**
     * Returns whether or not this scene is disposed.
     * 
     * @return Whether or not the scene is disposed.
     */
    public boolean isDisposed() {
        return this.renderer == null || parent==null || parent.isDisposed();
    }
    
    /**
     * Causes the receiver to have the <em>keyboard focus</em>, 
     *
     * @return Whether or not the control got focus.
     */
    public boolean setFocus() {
    	return parent.setFocus();
    }
    
    public OSGRenderer getRenderer() {
        return renderer;
    }
    
/*    protected Canvas getCanvas() {
        return canvas;
    }
    
    public Display getDisplay() {
        return canvas.getDisplay();
    }
*/    
    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------
    
    /**
     * Renders the next scene.
     */
    public void render() {
        renderer.doPaint();
    }



    /*control.addPaintListener( new PaintListener() {
		
		int counter = 0;
		public void paintControl(PaintEvent e) {
			if ( !render_initialized )
			{
				try
				{
					// Create timer task
					final TimerTask task = new TimerTask() {

						private boolean init = false;
						public void run() {
							//control.redraw();
							if ( !init )
							{ 
								init=true;
								renderer.zoomAll();
							}
							
							control.redraw();
							//control.redraw();
							//if ( control.isVisible() )
							//	paintControl( null );
						}
						
					};
					
					renderer.setControl( control );
					scene.setRenderer( renderer );
											
					scene.updateAtomPositions();

					final Timer timer = new Timer();
					timer.scheduleAtFixedRate( task, 0, 100 );
					
				}
				finally {
					render_initialized = true;
				}
			}
			scene.updateAtomPositions();
			renderer.doPaint( e );
			System.err.println( "Render..." + String.valueOf( counter++ ) );
			//control.redraw();
		}
	}
	);
	
	control.addFocusListener( new FocusListener() {

	
		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			//timer.cancel();				
		}
		
	}
	);
*/	
    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------
    
    /*
     * Loads and returns ImageData that may be used as texture with the 
     * <code>GL.glTexImage2D</code> method. 
   
    protected ImageData getTextureImageData(String resource) {
        return this.getTextureImageData(this.getClass().getResourceAsStream(resource));
    }
    
   
     * Loads and returns ImageData that may be used as texture with the 
     * <code>GL.glTexImage2D</code> method. 

    protected ImageData getTextureImageData(InputStream is) {
        ImageData source = new ImageData(is);
        Image resized = null, original = null;
        
        if (!((source.width == 256 && source.height == 256) ||
            (source.height == 128 && source.width == 128) ||
            (source.height == 64 && source.width == 64))) {
            original = new Image(this.canvas.getDisplay(), source);
            resized = new Image(this.canvas.getDisplay(), 256, 256);
            GC gc = new GC(resized);
            gc.drawImage(original, 0, 0, source.width, source.height, 0, 0, 256, 256);
            source = resized.getImageData();
            gc.dispose();
        }
        
        source = this.getRenderer().convertImageData(source);
        
        if (original != null) {
            resized.dispose();
            original.dispose();
        }
        
        return source;
    }*/

	
}
