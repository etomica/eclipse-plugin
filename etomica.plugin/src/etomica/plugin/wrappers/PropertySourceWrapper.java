/*
 * History
 * Created on Oct 10, 2004 by kofke
 */
package etomica.plugin.wrappers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import etomica.atom.AtomAddressManager;
import etomica.atom.AtomFactory;
import etomica.atom.AtomPositionDefinition;
import etomica.atom.iterator.AtomsetIterator;
import etomica.data.DataSink;
import etomica.integrator.Integrator;
import etomica.math.geometry.Shape;
import etomica.nbr.NeighborCriterion;
import etomica.phase.Phase;
import etomica.plugin.Registry;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.views.CheckboxPropertyDescriptor;
import etomica.plugin.views.ComboClassPropertyDescriptor;
import etomica.plugin.views.ComboPropertyDescriptor;
import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.plugin.views.IntegerPropertyDescriptor;
import etomica.simulation.Simulation;
import etomica.space.Boundary;
import etomica.space.IVector;
import etomica.space.Tensor;
import etomica.species.Species;
import etomica.units.Dimension;
import etomica.units.Unit;
import etomica.units.systems.UnitSystem;
import etomica.util.Arrays;
import etomica.util.EnumeratedType;

/**
 * Wraps an object with an implementation of IPropertySource so that
 * it may be displayed and edited in the eclipse property sheet.  Properties
 * associated with wrapped object are determined through Java reflection.
 */

//take care not to confuse java.beans.PropertyDescriptor and org.eclipse.ui.view.properties.PropertyDescriptor

public class PropertySourceWrapper implements IPropertySource {

    public PropertySourceWrapper(Object object) {
        this(object,null);
    }

    /**
	 * Constructs new instance, wrapping the given object.
	 */
	public PropertySourceWrapper(Object object, Simulation sim) {
		super();
		this.object = object;
        simulation = sim;
        interfaceWrappers = new InterfaceWrapper[0];
        childWrappers = null;
        status = null;
	}
	
    public static PropertySourceWrapper makeWrapper(Object obj) {
        return makeWrapper(obj,null);
    }
    
    public static PropertySourceWrapper makeWrapper(Object obj, Simulation sim) {
        return makeWrapper(obj, sim, null);
    }
    
    public static PropertySourceWrapper makeWrapper(Object obj, Simulation sim, EtomicaEditor editor) {
        if (wrapperClassHash == null) {
            initWrapperHash();
        }
        PropertySourceWrapper wrapper = null;
        Class objClass = obj.getClass();
        // wrapped class is the class handled by the wrapper
        Class wrappedClass = null;
        if (obj instanceof Object[]) {
            wrappedClass = Object[].class;
            // we have to special-case Object[] since Phase[].class != Object[].class
            wrapper = new ArrayWrapper((Object[])obj, sim);
        }
        while (wrapper == null) {
            Class wrapperClass = (Class)wrapperClassHash.get(objClass);
            if (wrapperClass != null) {
                wrappedClass = objClass;
                wrapper = (PropertySourceWrapper)makeWrapperForClass(wrapperClass, obj, objClass, sim);
            }
            objClass = objClass.getSuperclass();
            // if we make it to Object, we'll fall back to PropertySourceWrapper itself
        }
        
        objClass = obj.getClass();
        if (objClass.getComponentType() == null) {
            // look for interfaces.  arrays have no interfaces
            // we'll walk up the class hierarchy looked for interfaces of 
            // subclasses of the wrapped class.  The wrapper is expected to
            // expose special functionality related to any interfaces it 
            // implements, possibly by manually adding the interfaceWrappers
            // manually
            while (wrappedClass != objClass) {
                Class[] interfaces = objClass.getInterfaces();
                for (int i=0; i<interfaces.length; i++) {
                    addInterfaceWrappersToWrapper(wrapper, interfaces[i], wrappedClass, editor);
                }
                
                // now look at the parent class' interfaces.  They weren't returned
                // when we asked for the interfaces of objClass
                objClass = objClass.getSuperclass();
            }
        }
        if (editor != null) {
            wrapper.setEditor(editor);
        }
        return wrapper;
    }
    
    public static void addInterfaceWrappersToWrapper(PropertySourceWrapper wrapper, Class someInterface, Class wrappedClass, EtomicaEditor editor) {
        Class wrapperClass = (Class)wrapperClassHash.get(someInterface);
        if (wrapperClass != null) {
            // we found a wrapper for this specific interface
            Object obj = wrapper.getObject();
            InterfaceWrapper interfaceWrapper = (InterfaceWrapper)makeWrapperForClass(wrapperClass, obj, someInterface, editor.getSimulation());
            wrapper.setEditor(editor);
            wrapper.addInterfaceWrapper(interfaceWrapper);
        }
        Class[] parentInterfaces = someInterface.getInterfaces();
        for (int i=0; i<parentInterfaces.length; i++) {
            // Interfaces implemented by the class wrapped by the wrapper or
            // any of its superclasses are not needed.  The wrapper itself is
            // expected to handle any special features of the object related
            // to the interface
            if (!parentInterfaces[i].isAssignableFrom(wrappedClass)) {
                addInterfaceWrappersToWrapper(wrapper, parentInterfaces[i], wrappedClass, editor);
            }
        }
    }
    
    public InterfaceWrapper[] getInterfaceWrappers() {
        return interfaceWrappers;
    }

    public static Object makeWrapperForClass(Class wrapperClass, Object obj, Class objClass, Simulation sim) {
        Object wrapper = null;
        try {
            Constructor wrapperConstructor = null;
            try {
                try {
                    wrapperConstructor = wrapperClass.getConstructor(new Class[]{objClass, Simulation.class});
                }
                catch (NoSuchMethodException e) {
                    // some wrappers don't have a constructor that takes a simulation
                    // we'll fall through to the second attempt
                }
                if (wrapperConstructor == null) {
                    wrapperConstructor = wrapperClass.getConstructor(new Class[]{objClass});
                    wrapper = wrapperConstructor.newInstance(new Object[]{obj});
                }
                else {
                    wrapper = wrapperConstructor.newInstance(new Object[]{obj, sim});
                }
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return wrapper;
        }
        catch (RuntimeException e) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
        }
        return null;
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
     * Adds the given wrapper for an interface to this wrapper.  If the wrapper
     * already has the given interfaceWrapper (perhaps due to multiple 
     * inheritence), the interfaceWrapper is not added.
     */
    public void addInterfaceWrapper(InterfaceWrapper interfaceWrapper) {
        for (int i=0; i<interfaceWrappers.length; i++) {
            if (interfaceWrapper.getClass() == interfaceWrappers[i].getClass()) {
                return;
            }
        }
        interfaceWrappers = (InterfaceWrapper[])Arrays.addObject(interfaceWrappers, interfaceWrapper);
    }

	/**
	 * Returns the wrapped object, which is the editable value of 
	 * this PropertySource.
	 */
    public Object getEditableValue() {
        return this;
    }


    /**
     * Returns the one of this source's properties as specified by the key.
     * We use the java.beans.PropertyDescriptor as the default key for the 
     * properties.  Subclasses may override and use different types of keys.
     * If the property is a double and the object's class defines the dimension
     * the value is converted to the current unit system.
     */
    public Object getPropertyValue(Object key) {
        Object value = null;
        for (int i=0; i<interfaceWrappers.length; i++) {
            value = interfaceWrappers[i].getPropertyValue(key);
            if (value != null) {
                return value;
            }
        }
        java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
        Method getter = pd.getReadMethod(); //method used to read value of property in this object
        Object args[] = { };
        try {value = getter.invoke(object, args);}
        catch(NullPointerException ex) {value = null;}
        catch(InvocationTargetException ex) {value = null;}
        catch(IllegalAccessException ex) {value = null;}
        if (value != null && (value.getClass().isArray() || value instanceof IVector)) {
            return PropertySourceWrapper.makeWrapper(value, simulation, etomicaEditor);
        }
        if (value instanceof Double) {
            value = getDisplayValue((Double)value, pd.getReadMethod().getName());
        }
        return value;
    }

	public boolean isPropertySet(Object key) {
        // not relevant
		return false;
	}

	public void resetPropertyValue(Object key) {
        // not relevant -- do nothing
	}

	public void setPropertyValue(Object key, Object value) {
        if (value instanceof PropertySourceWrapper) {
            value = ((PropertySourceWrapper)value).getObject();
        }

        boolean handled = false;
        for (int i=0; i<interfaceWrappers.length; i++) {
            if (interfaceWrappers[i].setPropertyValue(key, value)) {
                // true means the interfaceWrapper handled it
                handled = true;
                break;
            }
        }
        if (!handled) {
    		java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
    		Method setter = pd.getWriteMethod(); //method used to read value of property in this object
    		if(setter == null) return;
            if (value instanceof Double) {
                value = getSimValue((Double)value, pd.getReadMethod().getName());
            }
            
            try {
    			setter.invoke(object, new Object[] {value});
            }
            catch (IllegalAccessException ex) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, ex.getMessage(), ex.getCause()));
            }
            catch (InvocationTargetException ex) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.WARNING, PlatformUI.PLUGIN_ID, 0, ex.getMessage(), ex.getCause()));
            }
        }
		if (etomicaEditor != null) {
            etomicaEditor.markDirty();
            etomicaEditor.getInnerPanel().getViewer().refresh(this);
        }
	}
	
    /**
     * Probes the wrapped object for the appropriate unit (from the simulation
     * unit system) for the given property.  If no suitable unit can be found
     * (probably because the object's class does not implement getFooDimension)
     * null is returned.
     * @param getterName the name of the getter associated with the property of 
     * interest (for "temperature", getterName would be "getTemperature")
     */
	protected Unit getPropertyUnit(String getterName) {
        try {
            Method dimensionGetter = object.getClass().getMethod(getterName+"Dimension",new Class[0]);
            Dimension dimension = (Dimension)dimensionGetter.invoke(object, new Object[0]);
            UnitSystem unitSystem = simulation.getDefaults().unitSystem;
            return dimension.getUnit(unitSystem);
        }
        catch (NoSuchMethodException ex) {} //likely
        catch (InvocationTargetException ex) {} //unlikely
        catch (IllegalAccessException ex) {} //unlikely
        return null;
    }
    
    /**
     * If possible, converts the given value to simulation units
     * @param getterName the name of the getter associated with the property of 
     * interest (for "temperature", getterName would be "getTemperature")
     */
    protected Double getSimValue(Double value, String getterName) {
        Unit unit = getPropertyUnit(getterName);
        if (unit != null) {
            value = new Double(unit.toSim(value.doubleValue()));
        }
        return value;
    }

    /**
     * If possible, converts the given value from simulation units to 
     * the appropriate units for display.
     * @param getterName the name of the getter associated with the property of 
     * interest (for "temperature", getterName would be "getTemperature")
     */
    protected Double getDisplayValue(Double value, String getterName) {
        Unit unit = getPropertyUnit(getterName);
        if (unit != null) {
            value = new Double(unit.fromSim(value.doubleValue()));
        }
        return value;
    }
    
    /**
	 * toString for the wrapper is just the toString for the wrapped object.
	 * This ensures the object's label is displayed when in views other than
	 * the PropertySheet.
	 */
	public String toString() {
        if (displayName == null) {
            return object.toString();
        }
        return displayName;
	}

    public void setDisplayName(String newName) {
        displayName = newName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public final IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors != null) return descriptors;
        descriptors = generateDescriptors();
        return descriptors;
	}

    /**
     * Returns an array of property descriptors associated with the wrapped
     * object.  Subclasses can override this method to add properties.
     * Properties can also be vetoed by removing them after-the-fact, but that
     * should be done via makeDescriptor.
     */
    protected IPropertyDescriptor[] generateDescriptors() {
        //Introspection to get array of all properties
        java.beans.PropertyDescriptor[] properties = new java.beans.PropertyDescriptor[0];
        try {
            BeanInfo bi = Introspector.getBeanInfo(object.getClass());
            properties = bi.getPropertyDescriptors();
        } 
        catch (IntrospectionException ex) {
            error("PropertySheet: Couldn't introspect", ex);
        }

	    //loop through properties and generate descriptors
	    ArrayList list = new ArrayList();
	    for (int i = 0; i < properties.length; i++) {
	        IPropertyDescriptor pd = makeDescriptor(properties[i]);
	        if(pd != null) list.add(pd);
	    }//end of loop over properties
	    
        for (int i=0; i<interfaceWrappers.length; i++) {
            IPropertyDescriptor[] interfaceProperties = interfaceWrappers[i].generateDescriptors();
            for (int j=0; j<interfaceProperties.length; j++) {
                boolean duplicate = false;
                for (int k=0; k<list.size(); k++) {
                    if (((IPropertyDescriptor)list.get(k)).getDisplayName().equals(interfaceProperties[j].getDisplayName())) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    list.add(interfaceProperties[j]);
                }
            }
        }
        
	    //make array of descriptors from list
	    return (IPropertyDescriptor[])list.toArray(new IPropertyDescriptor[list.size()]);
	}

    /**
     * Creates an IPropertyDescriptor (what we want) from a java beans
     * PropertyDescriptor (what java gives us).  InterfaceWrappers get a first
     * crack at handling the property.
     */
    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        
		// Don't display hidden or expert properties.
		if (property.isHidden() || property.isExpert()) {
		    return null;
        }
		
		String name = property.getDisplayName();  //Localized display name 
		if(name.equals("class")) return null;//skip getDimension(), getClass()
		
		Method getter = property.getReadMethod(); //method used to read value of property in this object
        Class type = property.getPropertyType();

		// Display only readable properties.
		if (getter == null) {
//            if (property instanceof IndexedPropertyDescriptor) {
//                getter = ((IndexedPropertyDescriptor)property).getIndexedReadMethod();
//                type = ((IndexedPropertyDescriptor)property).getIndexedPropertyType();
//            }
            return null;
        }

        Object value = null;
        // Some classes can be chosen in PropertySheet, but the current
        // value should be the selected item in the list of choices, so retrieve
        // it now
        boolean getValue = false;
        if (simulation != null && (AtomPositionDefinition.class.isAssignableFrom(type) ||
                Boundary.class.isAssignableFrom(type) || AtomFactory.class.isAssignableFrom(type) ||
                DataSink.class.isAssignableFrom(type) || NeighborCriterion.class.isAssignableFrom(type) ||
                UnitSystem.class.isAssignableFrom(type))) {
            getValue = true;
        }
        if (getValue) {
            try {
                value = getter.invoke(object, null);
            }
            catch (InvocationTargetException ex) {
                System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
    //          ex.getTargetException().printStackTrace();
                return null;
            } 
            catch (IllegalAccessException ex) {
                System.err.println("Skipping property " + name + " ; exception on target: " + ex.getMessage());
    //          ex.getTargetException().printStackTrace();
                return null;
            }
        }
                        
        for (int i=0; i<interfaceWrappers.length; i++) {
            IPropertyDescriptor descriptor = interfaceWrappers[i].makeDescriptor(property, value, type, name);
            if (descriptor == PROPERTY_VETO) {
                // throwing an exception is the interfaceWrapper's way of
                // vetoing the property
                return null;
            }
            if (descriptor != null) {
                return descriptor;
            }
        }

        return makeDescriptor(property,value,type,name,simulation);
    }

    /**
     * Creates a PropertyDescriptor for the given |property| of Class |type|,
     * having name |name|. For properties which have a list of choices, the
     * |value| (if not null) is used as the current choice.
     */
    protected static IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name, Simulation sim) {

        // Do not display dimension specifications as properties
        if(etomica.units.Dimension.class.isAssignableFrom(type)) return null;
        //if(etomica.utility.LinkedList.class.isAssignableFrom(type)) return null;
		
		IPropertyDescriptor pd = null;
		if(type == boolean.class) {
			pd = new CheckboxPropertyDescriptor(property, name);
		}
        else if(type == int.class) {
			pd = new IntegerPropertyDescriptor(property, name);
		}
        else if(type == double.class) {
			pd = new DecimalPropertyDescriptor(property, name);
		}
		else if(EnumeratedType.class.isAssignableFrom(type)) {
            EnumeratedType[] choices = null;
            try {
                Method method = type.getMethod("choices",null);
                if (method != null) {
                    choices = (EnumeratedType[])method.invoke(null,null);
                }
            }
            catch (InvocationTargetException e) {
                // choices threw an excpetion
            }
            catch (NoSuchMethodException e) {
                // choices doesn't exist
            }
            catch (IllegalAccessException e) {
            }
            catch (NullPointerException e) {
                // choices() wasn't static as it should be
            }
            if (choices != null) {
                pd = new ComboPropertyDescriptor(property,name,choices);
            }
		}
		else if(String.class == type) {
			pd = new TextPropertyDescriptor(property, name);
		}
        else if (Phase.class.isAssignableFrom(type) && sim != null) {
            Phase[] simPhases = sim.getPhases();
            Phase[] phases = new Phase[0];
            for (int i=0; i<simPhases.length; i++) {
                if (type.isInstance(simPhases[i])) {
                    phases = (Phase[])Arrays.addObject(phases, simPhases[i]);
                }
            }
            pd = new ComboPropertyDescriptor(property, name, phases);
        }
        else if (Integrator.class.isAssignableFrom(type) && sim != null) {
            Iterator integratorIterator = sim.getIntegratorList().iterator();
            Object[] integrators = new Object[0];
            while (integratorIterator.hasNext()) {
                Integrator integrator = (Integrator)integratorIterator.next();
                if (type.isInstance(integrator)) {
                    integrators = Arrays.addObject(integrators, integrator);
                }
            }
            pd = new ComboPropertyDescriptor(property, name, integrators);
        }
        else if (sim != null && (AtomPositionDefinition.class.isAssignableFrom(type) ||
                Boundary.class.isAssignableFrom(type) || AtomFactory.class.isAssignableFrom(type) ||
                DataSink.class.isAssignableFrom(type) || NeighborCriterion.class.isAssignableFrom(type) ||
                UnitSystem.class.isAssignableFrom(type))) {
            Collection collection = Registry.queryWhoExtends(type);
            Iterator classIterator = collection.iterator();
            Object[] choices;
            if (value != null) {
                choices = new Object[]{value};
            }
            else {
                choices = new Object[0];
            }
            while (classIterator.hasNext()) {
                Class thisClass = (Class)classIterator.next();
                if (type.isAssignableFrom(thisClass)) {
                    choices = Arrays.addObject(choices,thisClass);
                }
            }
            pd = new ComboClassPropertyDescriptor(property, name, choices, new Object[]{sim});
        }
		if (pd == null) {
			pd = new org.eclipse.ui.views.properties.PropertyDescriptor(property, name);
		}
		return pd;
	}//end of processProperty


    private void error(String message, Throwable th) {
	    System.err.println(message);
	    th.printStackTrace();
    }

	/**
	 * Convenience method that wraps all elements of a given array with 
	 * a PropertySourceWrapper.
	 * @param array the input array with elements to be wrapped
	 * @return the array of wrapped elements
	 */
    public static PropertySourceWrapper[] wrapArrayElements(Object[] array, Simulation sim, EtomicaEditor editor) {
        int nonNullCount = 0;
        for(int i=0; i<array.length; i++) {
            if (array[i] != null) {
                nonNullCount++;
            }
        }
        PropertySourceWrapper[] wrappedArray = new PropertySourceWrapper[nonNullCount];
        for(int i=0; i<array.length; i++) {
            if (array[i] != null) {
                wrappedArray[i] = PropertySourceWrapper.makeWrapper(array[i], sim, editor);
            }
        }
        return wrappedArray;
    }

    public PropertySourceWrapper[] getChildren(LinkedList parentList) {
        if (childWrappers != null) {
            return childWrappers;
        }
        
        childWrappers = new PropertySourceWrapper[0];
        
        for (int i=0; i<interfaceWrappers.length; i++) {
            PropertySourceWrapper[] moreChildren = interfaceWrappers[i].getChildren();
            if (moreChildren.length > 0) {
                PropertySourceWrapper[] newChildren = new PropertySourceWrapper[childWrappers.length + moreChildren.length];
                System.arraycopy(childWrappers, 0, newChildren, 0, childWrappers.length);
                System.arraycopy(moreChildren, 0, newChildren, childWrappers.length, moreChildren.length);
                childWrappers = newChildren;
            }
        }

        // this assigns the descriptors to the |descriptors| field
        getPropertyDescriptors();
        int count = 0;
        for (int i=0; i<descriptors.length; i++) {
            Object pd = descriptors[i].getId();
            Object value = getPropertyValue(pd);
            if (value == null) {
                continue;
            }
            PropertySourceWrapper childWrapper;
            Object obj;
            if (value instanceof PropertySourceWrapper) {
                childWrapper = (PropertySourceWrapper)value;
                obj = ((PropertySourceWrapper)value).getObject();
            }
            else {
                obj = value;
                childWrapper = PropertySourceWrapper.makeWrapper(obj,simulation,getEditor());
            }
            
            Iterator parentIterator = parentList.iterator();
            boolean excluded = false;
            while (parentIterator.hasNext()) {
                if (parentIterator.next() == obj) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }

            if (isChildExcluded(descriptors[i], childWrapper, obj)) {
                continue;
            }
            
            // append the new wrapper
            childWrappers = (PropertySourceWrapper[])Arrays.resizeArray(childWrappers,++count);
            childWrappers[count-1] = childWrapper;
        }
        return childWrappers;
    }
    
    /**
     * Returns true if the child associated with the given descriptor and
     * childWrapper should not be considered a "child" of the wrapped object.
     */
    public boolean isChildExcluded(IPropertyDescriptor descriptor, PropertySourceWrapper childWrapper, Object child) {
        for (int i=0; i<interfaceWrappers.length; i++) {
            if (interfaceWrappers[i].isChildExcluded(descriptor, childWrapper, child)) return true;
        }

        Class objClass = child.getClass();
        if (objClass.isArray()) {
            if (!(child instanceof Object[])) {
                // arrays of natives aren't children
                return true;
            }
            if (((Object[])child).length == 0) {
                // just omit empty arrays
                return true;
            }
            objClass = objClass.getComponentType();
        }

        for (int j=0; j<excludedChildClasses.length; j++) {
            if (childWrapper.getClass() == PropertySourceWrapper.class) {
                // don't auto-exclude objects with PropertySourceWrapper subclasses
                // since they might have open/remove functionality
                if ((objClass.getModifiers() & Modifier.NATIVE) != 0) {
                    // no native types
                    return true;
                }
                String canonicalName = objClass.getCanonicalName();
                String firstPackage = canonicalName.substring(0, canonicalName.indexOf("."));
                if (firstPackage.equals("java")) {
                    // no LinkedList, Class, etc
                    // this might be better off as !equals("etomica")
                    return true;
                }
                IPropertyDescriptor[] childDescriptors = childWrapper.getPropertyDescriptors();
                if (childDescriptors.length == 0) {
                    return true;
                }
            }
                
            if (excludedChildClasses[j].isAssignableFrom(objClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns an array of MenuItemWrappers appropriate for this wrapper and
     * underlying object.  The MenuItemWrappers are taken from the
     * PropertySourceWrapper (sub)class itself along with any
     * InterfaceWrappers.
     */
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];

        // removal depends on the parent rather than the child
        if (parentWrapper != null) {
            RemoverWrapper removerWrapper = getRemoverWrapper(parentWrapper);
            if (removerWrapper != null) {
                itemWrappers = (MenuItemWrapper[])Arrays.addObject(itemWrappers, 
                        new RemoveItemWrapper(removerWrapper));
            }
        }

        for (int i=0; i<interfaceWrappers.length; i++) {
            MenuItemWrapper[] interfaceItemWrappers = interfaceWrappers[i].getMenuItemWrappers(parentWrapper);
            itemWrappers = combineMenuItemWrappers(itemWrappers, interfaceItemWrappers);
        }
        return itemWrappers;
    }

    /**
     * Returns an array containing the union of the two given arrays of item
     * wrappers.  Wrappers are considered duplicates if
     * wrapper1.equals(wrapper2).  With duplicate Cascade wrappers, the submenu
     * items from wrapper2 are added to the wrapper1.
     */
    public static MenuItemWrapper[] combineMenuItemWrappers(MenuItemWrapper[] wrappers1, MenuItemWrapper[] wrappers2) {
        for (int j=0; j<wrappers2.length; j++) {
            boolean duplicate = false;
            for (int k=0; k<wrappers1.length; k++) {
                // include all wrappers from wrappers2 unless they're "equal" by their own definition,
                // meaning that they accomplish the same task
                if (wrappers1[k].equals(wrappers2[j])) {
                    if (wrappers1[k] instanceof MenuItemCascadeWrapper) {
                        // combine sub menus for wrappers of the same class
                        ((MenuItemCascadeWrapper)wrappers1[k]).addSubmenuItems(
                                ((MenuItemCascadeWrapper)wrappers1[j]).getSubmenuWrapperItems());
                    }
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                wrappers1 = (MenuItemWrapper[])Arrays.addObject(wrappers1, wrappers2[j]);
            }
        }

        java.util.Arrays.sort(wrappers1);
        return wrappers1;
    }
    
    /**
     * Check the parentWrapper and all its interfaceWrappers for ability to 
     * remove us.  Returns the parent wrapper that can remove us, or null if
     * no parent wrapper can.
     */
    protected RemoverWrapper getRemoverWrapper(PropertySourceWrapper parentWrapper) {
        if (parentWrapper instanceof RemoverWrapper) {
            if (((RemoverWrapper)parentWrapper).canRemoveChild(object)) {
                return (RemoverWrapper)parentWrapper;
            }
        }
        InterfaceWrapper[] parentInterfaceWrappers = parentWrapper.getInterfaceWrappers();
        for (int i=0; i<parentInterfaceWrappers.length; i++) {
            if (parentInterfaceWrappers[i] instanceof RemoverWrapper) {
                if (((RemoverWrapper)parentInterfaceWrappers[i]).canRemoveChild(object)) {
                    return (RemoverWrapper)parentInterfaceWrappers[i];
                }
            }
        }
        return null;
    }
        
    /**
     * Returns the status of the underlying object (PEACHY, WARNING or ERROR).
     * An object might not be happy becuase of its own internal state or
     * because of its relationship with another object in the simulation.
     */
    public EtomicaStatus getStatus(LinkedList parentList) {
        if (status != null) {
            return status;
        }
        
        if (parentList.size() > 10) {
            throw new RuntimeException("parent list too long.  probable infinite recursion");
        }

        status = EtomicaStatus.PEACHY;
        for (int i=0; i<interfaceWrappers.length; i++) {
            EtomicaStatus interfaceStatus = interfaceWrappers[i].getStatus();
            if (interfaceStatus.type.severity > status.type.severity) {
                status = interfaceStatus;
            }
        }
        
        // now check children
        LinkedList childsParentList = new LinkedList();
        childsParentList.addAll(parentList);
        childsParentList.add(object);
        getChildren(childsParentList);
        
        for (int i=0; i<childWrappers.length; i++) {
            EtomicaStatus childStatus = childWrappers[i].getStatus(childsParentList);
            if (childStatus.type.severity > status.type.severity) {
                status = childStatus;
            }
        }
        
        return status;
    }
    
    public void refresh() {
        status = null;
        childWrappers = null;
    }

    protected Object object;
	private IPropertyDescriptor[] descriptors;
    protected String displayName;
    protected Simulation simulation;
    protected EtomicaEditor etomicaEditor;
    protected InterfaceWrapper[] interfaceWrappers;
    private static HashMap wrapperClassHash;
    protected PropertySourceWrapper[] childWrappers;
    protected EtomicaStatus status;

    /**
     * Initializes the hash of Wrapper classes.
     */
    private static void initWrapperHash() {
        wrapperClassHash = new HashMap();
        addToWrapperHash(Registry.queryWhoExtends(PropertySourceWrapper.class));
        addToWrapperHash(Registry.queryWhoExtends(InterfaceWrapper.class));
    }
    
    /**
     * Adds the given wrappers to the wrapper hash.
     */
    private static void addToWrapperHash(Collection wrappers) {
        Iterator wrapperIterator = wrappers.iterator();
        while (wrapperIterator.hasNext()) {
            Class wrapperClass = (Class)wrapperIterator.next();
            if ((wrapperClass.getModifiers() & Modifier.ABSTRACT) != 0) {
                // We're only interested in concrete classes
                continue;
            }
            // Use the first constructor parameter to be the key in the hash.
            // This should be the type of Object that the Wrapper wraps.
            Constructor[] constructors = wrapperClass.getConstructors();
            try {
                if (constructors.length == 0) {
                    throw new RuntimeException("Constructorless wrapper for "+wrapperClass);
                }
                Class[] constructorParameterClasses = constructors[0].getParameterTypes();
                if (constructorParameterClasses.length == 0) {
                    throw new RuntimeException("Parameterless wrapper constructor for "+wrapperClass);
                }
                Class wrappedClass = constructorParameterClasses[0];
                if (wrapperClassHash.get(wrappedClass) != null) {
                    throw new RuntimeException("duplicate wrapper classes for "+wrappedClass+": "+wrapperClass+" and "+wrapperClassHash.get(wrappedClass));
                }
                wrapperClassHash.put(wrappedClass, wrapperClass);
            }
            catch (RuntimeException e) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
            }
        }
    }

    private static final Class[] excludedChildClasses = new Class[]{
        IVector.class,Tensor.class,Shape.class,
        AtomAddressManager.class,AtomsetIterator.class,
        Phase.class,Species.class,
    };

    // this fields sole purpose is to allow InterfaceWrappers to return
    // something that tells us to not include a property descriptor (as opposed
    // to returning null, which means, it doesn't handle that property)
    public static final IPropertyDescriptor PROPERTY_VETO = new PropertyDescriptorVeto();
    
    protected static final class PropertyDescriptorVeto implements IPropertyDescriptor {
        public CellEditor createPropertyEditor(Composite parent) {return null;}
        public String getCategory() {return null;}
        public String getDescription() {return null;}
        public String getDisplayName() {return null;}
        public String[] getFilterFlags() {return null;}
        public Object getHelpContextIds() {return null;}
        public Object getId() {return null;}
        public ILabelProvider getLabelProvider() {return null;}
        public boolean isCompatibleWith(IPropertyDescriptor anotherProperty) {return false;}
    }
}
