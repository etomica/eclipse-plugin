/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import java.awt.Component;
import java.awt.Frame;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

import etomica.data.DataPump;
import etomica.data.DataSource;
import etomica.graphics.DisplayPlot;
import etomica.plugin.actions.RefreshDataAction;
import etomica.plugin.actions.ToggleUpdateAction;

/**
 * View for displaying the results from a DataSource.  The DataSource's 
 * getData() method gets invoked in order to display the data, so the view 
 * should not be associated directly with a Meter whose getData method might
 * change its state.  The underlying data structure used is a Table. 
 */
public class DataPlotView extends ViewPart implements ViewRefreshable {

    public DataPlotView() {
		super();
	}

	public void createPartControl(Composite parent) {
        displayPlot = new DisplayPlot();
        Composite subParent = new Composite(parent,SWT.EMBEDDED);
        frame = SWT_AWT.new_Frame(subParent);
        frame.add(displayPlot.getPlot());
		createActions();
		createToolBarButtons();

	}
    
    public void setDataSource(DataSource newDataSource) {
        if (refresher != null) {
            refresher.setEnabled(false);
            refresher = null;
        }
        displayPlot.getDataSet().reset();
        pump = new DataPump(newDataSource, displayPlot.getDataSet().makeDataSink());
        refresher = new Refresher(pump, displayPlot.getPlot());
        refresher.start();
        toggleUpdateAction.setRefresher(refresher);
    }
	
	private void createToolBarButtons() {
        getViewSite().getActionBars().getToolBarManager().add(refreshAction);
        
        ActionContributionItem toggleAutoUpdate = new ActionContributionItem(toggleUpdateAction);
        getViewSite().getActionBars().getMenuManager().add(toggleAutoUpdate);
	}
	
    public void refresh() {
        if (pump != null) {
            pump.actionPerformed();
        }
    }
    
	public void setFocus() {
		displayPlot.getPlot().grabFocus();
	}

	
	private void createActions() {
        refreshAction = new RefreshDataAction(this);
        toggleUpdateAction = new ToggleUpdateAction(null);
	}
    
    public void dispose() {
        if (refresher != null) {
            refresher.dispose();
            refresher.interrupt();
        }
    }

	private RefreshDataAction refreshAction;
    private ToggleUpdateAction toggleUpdateAction;
    protected TableViewer viewer;
    protected Frame frame;
    protected DisplayPlot displayPlot;
    protected DataPump pump;
    private Refresher refresher;
    
    /**
     * Thread that refreshers the viewer (on the UI thread) every 10 seconds 
     * while enabled.
     */
    public static class Refresher extends DataTableView.Refresher {
        protected Refresher(DataPump pump, Component component) {
            super(null);
            setUpdateJob(new UpdateJob("refresher", pump, component));
        }

    }

    protected static class UpdateJob extends WorkbenchJob {
        public UpdateJob(String name, DataPump pump, Component component) {
            super(name);
            this.pump = pump;
            this.component = component;
        }
        
        public IStatus runInUIThread(IProgressMonitor monitor) {
            if (component.isVisible()) {
                pump.actionPerformed();
            }
            return Status.OK_STATUS;
        }

        private final DataPump pump;
        private final Component component;
    }
}
