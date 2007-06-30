/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import java.awt.Frame;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.atom.Atom;
import etomica.graphics.DisplayBox;
import etomica.box.Box;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * View for listing the species hierarchy via a tree.
 */
public class ConfigurationViewDP extends ViewPart {

	public ConfigurationViewDP() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
	    Composite control = new Composite(parent,SWT.EMBEDDED);

        frame = SWT_AWT.new_Frame(control);

        displayBox = new DisplayBox(box);
        if (box != null) {
            frame.add(displayBox.graphic());
//            refresher = new Refresher(Thread.currentThread(), displayBox.graphic());
        }
        
        updater = new SceneUpdater(control,displayBox);
        updater.setFPS( 10 );
        if (box != null) {
            updater.run();
        }
        
		hookPageSelection();
	}
	
	//registers as listener so that tree root updates with selections in other views
	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {
			public void selectionChanged(
					IWorkbenchPart part,
					ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}
	
	/**
	 * Changes root of tree with change of simulation selected in another view.
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == this) return;
        if (!(part instanceof EtomicaEditor)) return;
		if (!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
        Object firstElement = sel.getFirstElement();
        if(firstElement == null) return;
        if (!(firstElement instanceof PropertySourceWrapper)) {
            return;
        }
        Object obj = ((PropertySourceWrapper)firstElement).getObject();
        if (obj instanceof Box && obj != box) {
            setBox((Box)obj);
        }
	}
    
    public void setBox(Box p) {
        if (box != null) {
            frame.remove(displayBox.graphic());
        }
        box = p;
        displayBox.setBox(box);
        if (updater != null && box != null && !updater.isActive()) {
            updater.run();
        }
        if (box != null) {
            frame.add(displayBox.graphic());
        }
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}
	
	public void dispose() {
		if(pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(pageSelectionListener);
		}
        if (updater != null) {
            updater.dispose();
            updater = null;
        }
        displayBox = null;
        box = null;
		super.dispose();
	}
	
    public static class SceneUpdater implements Runnable {
        private int DELAY = 100;
        
        public SceneUpdater(Composite parent, DisplayBox displayBox) {
            parentWidget = parent;
            this.displayBox = displayBox;
        }
        
        public void setFPS( double fps )
        {
            DELAY = (int)( 1000.0/fps );
        }
        public void run() {
            if (parentWidget == null || parentWidget.isDisposed()) {
                return;
            }
            isActive = true;
            if ( displayBox!=null &&  parentWidget.isVisible() ) {
                displayBox.repaint();
            }
            parentWidget.getDisplay().timerExec(DELAY, this);
        }
        
        public void dispose() {
            parentWidget = null;
            displayBox = null;
            isActive = false;
        }
        
        public boolean isActive() {
            return isActive;
        }
        
        private Composite parentWidget;
        private DisplayBox displayBox;
        private boolean isActive;
    }

    public void setSelectedAtoms( Atom[] atoms )
    {
//        displayBox.setSelectedAtoms( atoms );
    }

    protected Frame frame;
    protected DisplayBox displayBox;
    protected SceneUpdater updater;
    private EtomicaEditor etomicaEditor;
	private ISelectionListener pageSelectionListener;
	private Box box;
}