/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.atom.SpeciesRoot;
import etomica.phase.Phase;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.simulation.Simulation;
import etomica.species.Species;

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
//		setViewer(viewer);
        vcp = new SpeciesViewContentProvider();
		viewer.setContentProvider(vcp);
		viewer.setLabelProvider(new LabelProvider());
		hookPageSelection();
		addKeyListener();
		createActions();
		createToolBarButtons();
//		getViewer().setModel(phase.speciesMaster);

	}
	
	private void createToolBarButtons() {
//		getViewSite().getActionBars().getToolBarManager().add(collapseAction);
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
	
	//causes escape key to deselect all
	private void addKeyListener() {
		viewer.getTree().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.ESC) {
					viewer.getTree().deselectAll();
					viewer.setSelection(null);
				}
			}
		});
	}
	
	/**
	 * Changes root of tree with change of simulation selected in another view.
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part instanceof EtomicaEditor) {
            Simulation sim = ((EtomicaEditor)part).getSimulation();
            if (sim != null) {
                vcp.setSimulation(sim);
            }
        }
		if(part == this) return;
//		System.out.println("SpeciesView selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
		if(sel.getFirstElement() == null) return;
		Object obj = ((PropertySourceWrapper)sel.getFirstElement()).getObject();
        if (obj instanceof Species) {
            viewer.setInput(sel.getFirstElement());
        }
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
//	public void setViewer(TreeViewer viewer) {
//		this.viewer = viewer;
//	}
	
	public void dispose() {
		if(pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(pageSelectionListener);
		}
		super.dispose();
	}
	
	private void createActions() {
//		collapseAction = new CollapseAllAction(this);
	}
		
	private TreeViewer viewer;
	private Action collapseAction;
    private SpeciesViewContentProvider vcp;
	private ISelectionListener pageSelectionListener;
	private TreeItem root;
}
