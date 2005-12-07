/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

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
		createActions();
		createToolBarButtons();

	}
	
	private void createToolBarButtons() {
	    //TODO should have a refresh button
        //		getViewSite().getActionBars().getToolBarManager().add(collapseAction);
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
//		collapseAction = new CollapseAllAction(this);
	}
		
	private TableViewer viewer;
}
