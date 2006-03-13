/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import etomica.plugin.views.ViewRefreshable;

/**
 * Action that refreshes the DataSourceView (pulling new data in with its pump)
 */
public class RefreshDataAction extends Action {

	/**
	 * Constructs action and associates "resume" icon.
	 */
	public RefreshDataAction(ViewRefreshable view) {
		super("Run simulation");
        dataSourceView = view;
        ImageDescriptor eImage = ImageDescriptor.createFromFile(RefreshDataAction.class, "../icons/repeat.gif");
		setImageDescriptor(eImage);
		setToolTipText("Refresh");
	}

	/**
	 * Causes most recently set simulation to resume execution.
	 * Performs no action if simulation has not be set, or is null.
	 */
	public void run() {
        dataSourceView.refresh();
	}
	
	//may not need this
	public void dispose() {
	    dataSourceView = null;
	}
	
	private ViewRefreshable dataSourceView;
}
