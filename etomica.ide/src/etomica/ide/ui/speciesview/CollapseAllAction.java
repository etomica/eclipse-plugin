//developed from org.eclipse.jdt.ui.internal.packageview.PackagesMessages

package etomica.ide.ui.speciesview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Collapse all nodes.
 */
class CollapseAllAction extends Action {
	
	private SpeciesView view;
	
	CollapseAllAction(SpeciesView view) {
		super("Collapse All");
		setDescription("Collapse all of tree");
		setToolTipText("Collapse All");
//		JavaPluginImages.setLocalImageDescriptors(this, "collapseall.gif");
		ImageDescriptor image = ImageDescriptor.createFromFile(etomica.ide.ui.speciesview.SpeciesView.class,"collapseall.gif");
		this.setImageDescriptor(image);
		this.view= view;
//		WorkbenchHelp.setHelp(this, IJavaHelpContextIds.COLLAPSE_ALL_ACTION);
	}
 
	public void run() { 
		view.getViewer().collapseAll();
	}
}
