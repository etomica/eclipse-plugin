/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.atom.Atom;
import etomica.graphics2.SceneManager;
import etomica.phase.Phase;
import etomica.plugin.realtimegraphics.OSGWidget;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * View for listing the species hierarchy via a tree.
 */
public class ConfigurationView extends ViewPart {

	public ConfigurationView() {
		super();
        scene = new SceneManager();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
	    Composite control = new Composite(parent,SWT.NONE);
        GridData gridData3 = new org.eclipse.swt.layout.GridData();
        gridData3.grabExcessVerticalSpace = true;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        control.setLayoutData(gridData3);

        updater = new SceneUpdater(control,scene);
        updater.setFPS( 20 );
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
		if(part == this) return;
//		System.out.println("SpeciesView selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
        Object firstElement = sel.getFirstElement();
        if(firstElement == null) return;
        if (!(firstElement instanceof PropertySourceWrapper)) {
            return;
        }
        Object obj = ((PropertySourceWrapper)firstElement).getObject();
        if (obj instanceof Phase && obj != phase) {
            boolean nullPhase = (phase == null);
            phase = (Phase)obj;
            scene.setPhase(phase);
            if (nullPhase) {
                updater.run();
            }
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
		super.dispose();
	}
	
    public static class SceneUpdater implements Runnable {
        private int DELAY = 100;
        private boolean first_time = true;
        
        public SceneUpdater(Composite parent, SceneManager scene) {
            parentWidget = parent;
            osgWidget = new OSGWidget(parent);
            sceneManager = scene;
            scene.setRenderer( osgWidget.getRenderer() );
        }
        
        public void setFPS( double fps )
        {
            DELAY = (int)( 1000.0/fps );
        }
        public void run() {
            if ( doStop || (osgWidget!=null && parentWidget.isDisposed()) ) 
                return;
            if ( osgWidget!=null &&  parentWidget.isVisible() ) {
                sceneManager.updateAtomPositions();
                if ( first_time )
                {
                    osgWidget.getRenderer().zoomAll();
                    first_time = false;
                }
                osgWidget.render();
            }
            parentWidget.getDisplay().timerExec(DELAY, this);
        }
        
        public void stop() {
            doStop = true;
        }
        
        private final Composite parentWidget;
        private final OSGWidget osgWidget;
        private final SceneManager sceneManager;
        private boolean doStop;
    }

    public void setSelectedAtoms( Atom[] atoms )
    {
        scene.setSelectedAtoms( atoms );
    }

    protected final SceneManager scene;
    protected SceneUpdater updater;
	private ISelectionListener pageSelectionListener;
	private Phase phase;
}