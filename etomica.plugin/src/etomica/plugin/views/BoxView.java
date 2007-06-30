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

import etomica.box.Box;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * View for listing the species hierarchy via a tree.
 */
public class BoxView extends ViewPart {

    public BoxView() {
        super();
    }

    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent);
        vcp = new BoxViewContentProvider();
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
        if (obj instanceof Box && obj != box) {
            box = (Box)obj;
            viewer.setInput(firstElement);
        }
    }
    
    public void setBox(Box newBox) {
        box = newBox;
        viewer.setInput(PropertySourceWrapper.makeWrapper(box));
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
    private BoxViewContentProvider vcp;
    private ISelectionListener pageSelectionListener;
    private Box box;
}
