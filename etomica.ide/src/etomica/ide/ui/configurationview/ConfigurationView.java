/*
 * History
 * Created on Oct 2, 2004 by kofke
 */
package etomica.ide.ui.configurationview;

import java.util.HashMap;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.Phase;
import etomica.Simulation;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigurationView extends ViewPart {

	/**
	 * 
	 */
	public ConfigurationView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		canvas = new ConfigurationCanvas2D(parent);
		hookPageSelection();
//		System.out.println("part control 1");
//		parent = new Composite(parent.getShell(),SWT.EMBEDDED);		
//		java.awt.Frame frame = SWT_AWT.new_Frame(parent);
//		System.out.println("part control 2");
//		SimulationGraphic sim = new SimulationGraphic(new Space2D());
//		etomica.graphics.DisplayPhase display = new etomica.graphics.DisplayPhase(sim);
//		Phase phase = new Phase(sim);
//		new SpeciesSpheresMono(sim);
//		sim.elementCoordinator.go();
//		System.out.println("part control 3");
//		frame.add(display.graphic());
//		System.out.println("part control 4");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	public void dispose() {
		canvas.dispose();
		super.dispose();
		if(pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(pageSelectionListener);
		}
	}
	
	/**
	 * Changes root of tree with change of simulation selected in another view.
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if(part == this) return;
		System.out.println("Configuration Selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
		if(sel.getFirstElement() instanceof Phase) {
			Phase phase = (Phase)sel.getFirstElement();
			System.out.println("Phase "+phase.toString());
			canvas.setPhase(phase);
		} else if(sel.getFirstElement() instanceof Simulation) {
			Simulation sim = (Simulation)sel.getFirstElement();
			Phase phase = (Phase)lastPhase.get(sim);//get phase last viewed with selected simulation
			if(phase == null) {
				phase = sim.phase(0);
				if(phase != null) lastPhase.put(sim, phase);
			}
			canvas.setPhase(phase);	
		}

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

    private ISelectionListener pageSelectionListener;
	private ConfigurationCanvas canvas;
	private final HashMap lastPhase = new HashMap(8);//store last phase viewed for each simulation

}
