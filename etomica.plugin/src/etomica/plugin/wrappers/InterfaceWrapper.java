package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemWrapper;
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
     * Returns an array of MenuItemWrappers appropriate for this wrapper.  The
     * only MenuItemWrappers returned are those special to the interface.
     */
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        return new MenuItemWrapper[0];
    }
    
    /**
     * Returns the status of the given object related to the wrapper interface.
     */
    public EtomicaStatus getStatus() {
        return EtomicaStatus.PEACHY;
    }

    protected Object object;
    protected Simulation simulation;
    protected EtomicaEditor etomicaEditor;
}
