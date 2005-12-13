/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

import etomica.plugin.actions.RefreshDataAction;
import etomica.plugin.actions.ToggleUpdateAction;

/**
 * View for displaying the results from a DataSource.  The DataSource's 
 * getData() method gets invoked in order to display the data, so the view 
 * should not be associated directly with a Meter whose getData method might
 * change its state.  The underlying data structure used is a Table. 
 */
public class DataSourceView extends ViewPart {

    public DataSourceView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent);
        refresher = new Refresher(Thread.currentThread());
		createActions();
		createToolBarButtons();

        refresher.start();
	}
	
	private void createToolBarButtons() {
        getViewSite().getActionBars().getToolBarManager().add(refreshAction);
        
        ActionContributionItem toggleAutoUpdate = new ActionContributionItem(toggleUpdateAction);
        getViewSite().getActionBars().getMenuManager().add(toggleAutoUpdate);
	}
	
    public void refresh() {
        viewer.refresh();
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	
	public TableViewer getViewer() {
		return viewer;
	}
	
	private void createActions() {
        refreshAction = new RefreshDataAction(this);
        toggleUpdateAction = new ToggleUpdateAction(refresher);
//		collapseAction = new CollapseAllAction(this);
	}
    
    public void dispose() {
        refresher.dispose();
        refresher.interrupt();
    }

	private RefreshDataAction refreshAction;
    private ToggleUpdateAction toggleUpdateAction;
    protected TableViewer viewer;
    private Refresher refresher;
    
    /**
     * Thread that refreshers the viewer (on the UI thread) every 10 seconds 
     * while enabled.
     */
    public class Refresher extends Thread {
        protected Refresher(Thread viewerThread) {
            threadUI = viewerThread;
            enabled = true;
            updateJob.setSystem(true);
        }

        public void run() {
            while (true) {
                if (!threadUI.isAlive()) {
                    // the thread is dead so bail
                    break;
                }
                if (enabled) {
                    updateJob.schedule();
                }
                try{Thread.sleep(10000);}
                catch(InterruptedException e){}
                if (isDisposed) {
                    // the viewer is gone so bail
                    break;
                }
            }
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public void dispose() {
            isDisposed = true;
        }

        private boolean isDisposed = false;
        private final Thread threadUI;
        private boolean enabled;
        private final WorkbenchJob updateJob = new WorkbenchJob("refresh") { 
            public IStatus runInUIThread(IProgressMonitor monitor) {
                viewer.refresh();
                return Status.OK_STATUS;
            }
        };
 
    }
}
