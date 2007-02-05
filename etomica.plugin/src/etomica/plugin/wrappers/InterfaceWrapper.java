package etomica.plugin.wrappers;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.action.Action;
import etomica.plugin.editors.EtomicaEditor;
import etomica.simulation.Simulation;

/**
 * Wraps an object specifically to expose the functionality of an interface.
 * A PropertySoruceWrapper object will invoke this class' methods and combine
 * the results with those of the PropertySourceWrapper itself.  The 
 * InterfaceWrapper need not expose basic getter/setter functionality that 
 * is handled by the PropertySourceWrapper and should not expose any 
 * functionality provided by any parent interface.
 */
public abstract class InterfaceWrapper {

    public InterfaceWrapper(Object object) {
        this(object,null);
    }

    /**
	 * Constructs new instance, wrapping the given object.
	 */
	public InterfaceWrapper(Object object, Simulation sim) {
		super();
		this.object = object;
        simulation = sim;
	}
	
    public void setEditor(EtomicaEditor editor) {
        etomicaEditor = editor;
    }
    
    public EtomicaEditor getEditor() {
        return etomicaEditor;
    }
    
	/**
	 * @return the wrapped object
	 */
	public Object getObject() {
		return object;
	}
    
    /**
     * Returns the property value if the given key requires special handling 
     * by this interfaceWrapper.  If the property value should not be returned
     * at all because of something specific to the interface, an 
     * IllegalArgumentException is thrown.  If the property is not special to 
     * the interface at all and should be handled by the PropertySourceWrapper,
     * null is returned
     */
    public Object getPropertyValue(Object key) {
        return null;
    }

    /**
     * Sets the property value and returns true if the given key requires 
     * special handling by this interfaceWrapper.  If the property value should
     * not be set at all because of something specific to the interface, the
     * method returns false.  If the property is not special to the interface
     * at all and should be handled by the PropertySourceWrapper.
     */
	public boolean setPropertyValue(Object key, Object value) {
        return false;
    }
	
    /**
     * Creates the property descriptor if the given key requires special handling 
     * by this interfaceWrapper.  If the property descriptor should not be
     * returned at all because of something specific to the interface, an 
     * IllegalArgumentException is thrown.  If the property is not special to 
     * the interface at all and should be handled by the PropertySourceWrapper,
     * null is returned.
     */
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) throws IllegalArgumentException {
        return null;
    }
    
    /**
     * Creates the property descriptor if the given key requires special handling 
     * by this interfaceWrapper.  If the property descriptor should not be
     * returned at all because of something specific to the interface, an 
     * IllegalArgumentException is thrown.  If the property is not special to 
     * the interface at all and should be handled by the PropertySourceWrapper,
     * null is returned.
     */
    protected IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) throws IllegalArgumentException {
        return null;
    }

    /**
     * Returns any special children of the object associated with the interface
     */
    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[0];
    }
    
    /**
     * Removes the Object child from the object wrapped by this
     * PropertySourceWrapper.  returns false if the child could not be removed.
     */
    public boolean removeChild(Object child) {
        return false;
    }

    /**
     * Returns true if the given child can be removed (assumes that
     * the given object is an actual child of the wrapped object).
     */
    public boolean canRemoveChild(Object child) {
        return false;
    }

    /**
     * returns an array of Classes which can be added to the wrapped object.
     */
    public Class[] getAdders() {
        return new Class[0];
    }

    /**
     * Adds a new instance of an object of class newObjectClass to the wrapped object.
     * The shell is passed so that a Wizard can be invoked if needed.
     * Returns true if the operation is successful.
     */
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        return false;
    }

    /**
     * returns an array of Actions relevant to the wrapped object.
     */
    public Action[] getActions() {
        return new Action[0];
    }

    /**
     * Returns an array of views in which the given object can be opened in
     * @return
     */
    public String[] getOpenViews() {
        return new String[0];
    }

    /**
     * Opens the given object in the given type of view in the given page.
     * Returns true if the object was opened successfully.
     */
    public boolean open(String openView, IWorkbenchPage page, Shell shell) {
        return false;
    }

    public EtomicaStatus getStatus() {
        return EtomicaStatus.PEACHY;
    }

    protected Object object;
    protected Simulation simulation;
    protected EtomicaEditor etomicaEditor;
}
