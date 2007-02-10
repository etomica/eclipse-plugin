package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;

public interface OpenerWrapper {

    /**
     * Opens the wrapped Object in the view identified by viewName.  page and
     * shell are provided in case they are needed by the open implementation.
     * An Eclipse "view" may not necessarily be opened.  viewName might, for
     * instance, identify an extenral application.
     */
    public boolean open(String viewName, IWorkbenchPage page, Shell shell);

}