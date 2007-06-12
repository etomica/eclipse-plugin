package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;

/**
 * Interface for wrappers which can have an object added to them
 * @author Andrew Schultz
 */
public interface AdderWrapper {

    /**
     * Adds a new instance of an object of class newObjectClass to the wrapped object.
     * The shell is passed so that a Wizard can be invoked if needed.
     * Returns true if the operation is successful.
     */
    public boolean addObjectClass(Class newObjectClass, Shell shell);

}