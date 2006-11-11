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
import etomica.graphics.DisplayPhase;
import etomica.phase.Phase;
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

        displayPhase = new DisplayPhase(phase);
        if (phase != null) {
            frame.add(displayPhase.graphic());
//            refresher = new Refresher(Thread.currentThread(), displayPhase.graphic());
        }
        
        updater = new SceneUpdater(control,displayPhase);
        updater.setFPS( 10 );
        if (phase != null) {
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
        if (obj instanceof Phase && obj != phase) {
            setPhase((Phase)obj);
        }
	}
    
    public void setPhase(Phase p) {
        if (phase != null) {
            frame.remove(displayPhase.graphic());
        }
        phase = p;
        displayPhase.setPhase(phase);
        if (updater != null && phase != null && !updater.isActive()) {
            updater.run();
        }
        if (phase != null) {
            frame.add(displayPhase.graphic());
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
        displayPhase = null;
        phase = null;
		super.dispose();
	}
	
    public static class SceneUpdater implements Runnable {
        private int DELAY = 100;
        
        public SceneUpdater(Composite parent, DisplayPhase displayPhase) {
            parentWidget = parent;
            this.displayPhase = displayPhase;
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
            if ( displayPhase!=null &&  parentWidget.isVisible() ) {
                displayPhase.repaint();
            }
            parentWidget.getDisplay().timerExec(DELAY, this);
        }
        
        public void dispose() {
            parentWidget = null;
            displayPhase = null;
            isActive = false;
        }
        
        public boolean isActive() {
            return isActive;
        }
        
        private Composite parentWidget;
        private DisplayPhase displayPhase;
        private boolean isActive;
    }

    public void setSelectedAtoms( Atom[] atoms )
    {
//        displayPhase.setSelectedAtoms( atoms );
    }

    protected Frame frame;
    protected DisplayPhase displayPhase;
    protected SceneUpdater updater;
    private EtomicaEditor etomicaEditor;
	private ISelectionListener pageSelectionListener;
	private Phase phase;
}