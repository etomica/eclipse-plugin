/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import etomica.phase.Phase;
import etomica.plugin.wrappers.PhaseWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * View for listing the species hierarchy via a tree.
 */
public class PhaseView extends ViewPart {

    public PhaseView() {
        super();
    }

    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent);
        vcp = new PhaseViewContentProvider();
        viewer.setContentProvider(vcp);
        viewer.setLabelProvider(new LabelProvider());
        hookPageSelection();
        addKeyListener();
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
        if(part == this) return;
        if(!(selection instanceof IStructuredSelection)) return;
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object firstElement = sel.getFirstElement();
        if(firstElement == null) return;
        if (!(firstElement instanceof PropertySourceWrapper)) {
            return;
        }
        Object obj = ((PropertySourceWrapper)firstElement).getObject();
        if (obj instanceof Phase && obj != phase) {
            phase = (Phase)obj;
            viewer.setInput(firstElement);
        }
    }
    
    public void setPhase(Phase newPhase) {
        phase = newPhase;
        viewer.setInput(new PhaseWrapper(phase,null));
    }

    public void setFocus() {
        viewer.getControl().setFocus();
        getSite().setSelectionProvider(viewer);
    }
	
    public TreeViewer getViewer() {
        return viewer;
    }
	
    public void dispose() {
        if(pageSelectionListener != null) {
            getSite().getPage().removePostSelectionListener(pageSelectionListener);
        }
        super.dispose();
    }
	
    protected TreeViewer viewer;
    private PhaseViewContentProvider vcp;
    private ISelectionListener pageSelectionListener;
    private Phase phase;
}
