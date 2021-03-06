package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;

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
        this(object,new SimulationObjects());
    }

    /**
	 * Constructs new instance, wrapping the given object.
	 */
	public InterfaceWrapper(Object object, SimulationObjects simObjects) {
		super();
		this.object = object;
        if (simObjects == null) {
            throw new NullPointerException("You can't pass a null SimulationObjects.  Pass an empty one if you must");
        }
        this.simObjects = simObjects;
	}
	
	/**
	 * @return the wrapped object
	 */
	public Object getObject() {
		return object;
	}
    
    public void setEditor(EtomicaEditor newEditor) {
        editor = newEditor;
    }
    
    /**
     * Returns the property value if the given key requires special handling 
     * by this interfaceWrapper.  If the property is not special to the
     * interface at all and should be handled by the PropertySourceWrapper,
     * null is returned
     */
    public Object getPropertyValue(Object key) {
        return null;
    }

    /**
     * Sets the property value and returns true if the given key requires 
     * special handling by this interfaceWrapper.  If the property is not
     * special to the interface at all and should be handled by the 
     * PropertySourceWrapper, the method returns false.
     */
	public boolean setPropertyValue(Object key, Object value) {
        return false;
    }
	
    public IPropertyDescriptor[] generateDescriptors() {
        return new IPropertyDescriptor[0];
    }

    /**
     * Creates the property descriptor if the given key requires special handling 
     * by this interfaceWrapper.  If the property descriptor should not be
     * returned at all because of something specific to the interface, 
     * PropertySourceWrapper.PROPERTY_VETO.  If the property is not special to 
     * the interface at all and should be handled by the PropertySourceWrapper,
     * null is returned.
     */
    protected IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) {
        return null;
    }

    /**
     * Returns any special children of the object associated with the interface
     */
    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[0];
    }

    /**
     * Returns true if the given child (possibly found via reflection from
     * another wrapper) should be excluded.  This gives the interfaceWrapper
     * an opportunity to veto children as well as add them.
     */
    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        return false;
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
    protected SimulationObjects simObjects;
    protected EtomicaEditor editor;
}
