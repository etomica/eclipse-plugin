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
import org.eclipse.jface.viewers.Viewer;
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
public class DataTableView extends ViewPart implements ViewRefreshable {

    public DataTableView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent);
        refresher = new Refresher(Thread.currentThread(),viewer);
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
    public static class Refresher extends Thread {
        protected Refresher(Thread viewerThread, Viewer viewer) {
            threadUI = viewerThread;
            enabled = true;
            setUpdateJob(new UpdateJob("refresh",viewer));
            setDelay(10000);
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
                try{Thread.sleep(delay);}
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
        
        public void setDelay(long newDelay) {
            delay = newDelay;
        }
        
        public void setUpdateJob(WorkbenchJob newJob) {
            updateJob = newJob;
            updateJob.setSystem(true);
        }

        private boolean isDisposed = false;
        private final Thread threadUI;
        private boolean enabled;
        private long delay;
        private WorkbenchJob updateJob;
    }
    
    protected static class UpdateJob extends WorkbenchJob {
        public UpdateJob(String name, Viewer viewer) {
            super(name);
            this.viewer = viewer;
        }
        
        public IStatus runInUIThread(IProgressMonitor monitor) {
            viewer.refresh();
            return Status.OK_STATUS;
        }

        private final Viewer viewer;
    }
}
