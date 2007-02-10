package etomica.plugin.wrappers;

public interface RemoverWrapper {

    /**
     * Removes the Object child from the object wrapped by this
     * PropertySourceWrapper.  returns false if the child could not be removed.
     */
    public boolean removeChild(Object obj);

    /**
     * Returns true if the given child can be removed (assumes that
     * the given object is an actual child of the wrapped object).
     */
    public boolean canRemoveChild(Object obj);

}