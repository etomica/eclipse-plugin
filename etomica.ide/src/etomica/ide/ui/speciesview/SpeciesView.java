/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.ide.ui.speciesview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.Phase;
import etomica.Simulation;

/**
 * View for listing the species hierarchy via a tree.
 */
public class SpeciesView extends ViewPart {

	public SpeciesView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		setViewer(viewer);
		vcp = new SpeciesViewContentProvider();
		getViewer().setContentProvider(vcp);
		getViewer().setLabelProvider(new LabelProvider());
		hookPageSelection();
		createActions();
		createToolBarButtons();
//		getViewer().setModel(phase.speciesMaster);

	}
	
	private void createToolBarButtons() {
		getViewSite().getActionBars().getToolBarManager().add(collapseAction);
	}
	
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
		System.out.println("Selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
		if(!(sel.getFirstElement() instanceof Simulation)) return;
		Simulation sim = (Simulation)sel.getFirstElement();
		System.out.println("Simulation "+sim.toString());
		viewer.setInput(sim);
//		try {
//			if(sim.phase(0) == null) return;
//			System.out.println("Phase "+sim.phase(0).toString());
//			viewer.setInput(sim.phase(0).speciesMaster);
//		}
//		catch(IndexOutOfBoundsException ex) {System.out.println("no phase");}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
//		Simulation sim = new Simulation();
//		phase = new Phase(sim);
//		Species species = new SpeciesSpheres(sim, 32, 3);
//		sim.elementCoordinator.go();
//		viewer.setInput(phase.speciesMaster);
		viewer.getControl().setFocus();
		getSite().setSelectionProvider(viewer);
//		phase = Simulation.instance.phase(0);
	}

	
	public TreeViewer getViewer() {
		return viewer;
	}
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}
	
	public void dispose() {
		if(pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(pageSelectionListener);
		}
		super.dispose();
	}
	
	private void createActions() {
		collapseAction = new CollapseAllAction(this);
	}
		
	private TreeViewer viewer;
	private Action collapseAction;
	private SpeciesViewContentProvider vcp;
	private Phase phase;
	private ISelectionListener pageSelectionListener;
}
