/*
 * History
 * Created on Oct 10, 2004 by kofke
 */
package etomica.plugin.wrappers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.atom.AtomType;
import etomica.data.DataPipeForked;
import etomica.data.DataProcessor;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorMC;
import etomica.phase.Phase;
import etomica.plugin.views.CheckboxPropertyDescriptor;
import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.plugin.views.ComboClassPropertyDescriptor;
import etomica.plugin.views.ComboPropertyDescriptor;
import etomica.plugin.views.IntegerPropertyDescriptor;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.space.Boundary;
import etomica.space.BoundaryDeformablePeriodic;
import etomica.space.BoundaryRectangularNonperiodic;
import etomica.space.BoundaryRectangularPeriodic;
import etomica.space.BoundaryRectangularSlit;
import etomica.space3d.BoundaryTruncatedOctahedron;
import etomica.species.Species;
import etomica.util.Arrays;
import etomica.util.EnumeratedType;

/**
 * Wraps an object with an implementation of IPropertySource so that
 * it may be displayed and edited in the eclipse property sheet.  Properties
 * associated with wrapped object are determined through Java reflection.
 */

//take care not to confuse java.beans.PropertyDescriptor and org.eclipse.ui.view.properties.PropertyDescriptor

public class PropertySourceWrapper implements IPropertySource {

    protected PropertySourceWrapper(Object object) {
        this(object,null);
    }

    /**
	 * Constructs new instance, wrapping the given object.
	 */
	protected PropertySourceWrapper(Object object, Simulation sim) {
		super();
		this.object = object;
        simulation = sim;
	}
	
    public static PropertySourceWrapper makeWrapper(Object obj) {
        return makeWrapper(obj,null);
    }
    
    public static PropertySourceWrapper makeWrapper(Object obj, Simulation sim) {
        if (obj instanceof Object[]) {
            return new ArrayWrapper((Object[])obj,sim);
        }
        if (obj instanceof double[]) {
            return new DoubleArrayWrapper((double[])obj);
        }
        if (obj instanceof int[]) {
            return new IntArrayWrapper((int[])obj);
        }
        if (obj instanceof Simulation) {
            return new SimulationWrapper((Simulation)obj);
        }
        else if (obj instanceof PotentialMaster) {
            return new PotentialMasterWrapper((PotentialMaster)obj,sim);
        }
        else if (obj instanceof PotentialGroup) {
            return new PotentialGroupWrapper((PotentialGroup)obj,sim);
        }
        else if (obj instanceof Phase) {
            return new PhaseWrapper((Phase)obj,sim);
        }
        else if (obj instanceof Species) {
            return new SpeciesWrapper((Species)obj,sim);
        }
        else if (obj instanceof ActionGroup) {
            return new ActionGroupWrapper((ActionGroup)obj,sim);
        }
        else if (obj instanceof ActivityIntegrate) {
            return new ActivityIntegrateWrapper((ActivityIntegrate)obj,sim);
        }
        else if (obj instanceof Integrator) {
            if (obj instanceof IntegratorMC) {
                return new IntegratorMCWrapper((IntegratorMC)obj,sim);
            }
            return new IntegratorWrapper((Integrator)obj,sim);
        }
        else if (obj instanceof DataStreamHeader) {
            return new DataStreamWrapper((DataStreamHeader)obj,sim);
        }
        else if (obj instanceof DataPipeForked) {
            return new DataForkWrapper((DataPipeForked)obj,sim);
        }
        else if (obj instanceof DataProcessor) {
            return new DataProcessorWrapper((DataProcessor)obj,sim);
        }
        else if (obj instanceof Atom) {
            return new AtomWrapper((Atom)obj,sim);
        }
        else if (obj instanceof AtomType) {
            return new AtomTypeWrapper((AtomType)obj,sim);
        }
        return new PropertySourceWrapper(obj,sim);
    }
    
	/**
	 * @return the wrapped object
	 */
	public Object getObject() {
		return object;
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
     * We use the java.beans.PropertyDescriptor as the key for the properties.
     */
    public Object getPropertyValue(Object key) {
        java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
        Method getter = pd.getReadMethod(); //method used to read value of property in this object
        Object value = null;
        Object args[] = { };
        try {value = getter.invoke(object, args);}
        catch(NullPointerException ex) {value = null;}
        catch(InvocationTargetException ex) {value = null;}
        catch(IllegalAccessException ex) {value = null;}
        if (value != null && value.getClass().isArray()) {
            return PropertySourceWrapper.makeWrapper(value,simulation);
        }
        return value;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object arg0, Object arg1) {
		java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)arg0;
		Method setter = pd.getWriteMethod(); //method used to read value of property in this object
		if(setter == null) return;
		try {
			setter.invoke(object, new Object[] {arg1});
		} 
		catch(IllegalAccessException ex) {error("Cannot set value", ex);}
		catch(InvocationTargetException ex) {error("Cannot set value", ex);}
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
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors == null) generateDescriptors();
		return descriptors;
	}
	
	protected void generateDescriptors() {
       //Introspection to get array of all properties
        java.beans.PropertyDescriptor[] properties = null;
        try {
	        BeanInfo bi = Introspector.getBeanInfo(object.getClass());
	        properties = bi.getPropertyDescriptors();
	    } 
	    catch (IntrospectionException ex) {
	        error("PropertySheet: Couldn't introspect", ex);
	        return;
	    }
	    //loop through properties and generate descriptors
	    LinkedList list = new LinkedList();
	    for (int i = 0; i < properties.length; i++) {
	        IPropertyDescriptor pd = makeDescriptor(properties[i]);
	        if(pd != null) list.add(pd);
	    }//end of loop over properties
	    
	    //make array of descriptors from list
	    descriptors = (IPropertyDescriptor[])list.toArray(new IPropertyDescriptor[list.size()]);
	}

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

		// Don't display hidden or expert properties.
		if (property.isHidden() || property.isExpert()) {
		    return null;
        }
		
		String name = property.getDisplayName();  //Localized display name 
		if(name.equals("class")) return null;//skip getDimension(), getClass()
		
		Method getter = property.getReadMethod(); //method used to read value of property in this object

		// Display only read/write properties.
		if (getter == null) {
            // getter will be null if it was an IndexedPropertyDescriptor
//            if (property instanceof IndexedPropertyDescriptor) {
                // do something!
//            }
            return null;
        }

        Class type = property.getPropertyType();  //Type (class) of this property
		// Do not display dimension specifications as properties
        if(etomica.units.Dimension.class.isAssignableFrom(type)) return null;
        //if(etomica.utility.LinkedList.class.isAssignableFrom(type)) return null;
		
		IPropertyDescriptor pd = null;
        Object value = null;
        if ((Boundary.class.isAssignableFrom(type) && simulation != null)
                || EnumeratedType.class.isAssignableFrom(type)) {
            try {
                value = getter.invoke(object, null);
            }
            catch (InvocationTargetException ex) {
                System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
    //          ex.getTargetException().printStackTrace();
                return null;
            } 
            catch (Exception ex) {
                System.err.println("Skipping property " + name + " ; exception: " + ex);
    //          ex.printStackTrace();
                return null;
            }
        }
						
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
            if (value != null) {
                pd = new ComboPropertyDescriptor(property,name,((EnumeratedType)value).choices());
            }
		}
		else if(String.class.isAssignableFrom(type)) {
			pd = new TextPropertyDescriptor(property, name);
		}
        else if (Phase.class.isAssignableFrom(type) && simulation != null) {
            pd = new ComboPropertyDescriptor(property, name, simulation.getPhases());
        }
        else if (Boundary.class.isAssignableFrom(type) && simulation != null) {
            Object[] boundaryClasses = new Object[]{BoundaryRectangularPeriodic.class,BoundaryRectangularNonperiodic.class,
                    BoundaryRectangularSlit.class,BoundaryDeformablePeriodic.class,BoundaryTruncatedOctahedron.class};
            if (value != null) {
                boundaryClasses = Arrays.resizeArray(boundaryClasses,boundaryClasses.length+1);
                for (int i=boundaryClasses.length-1; i>0; i--) {
                    boundaryClasses[i] = boundaryClasses[i-1];
                }
                boundaryClasses[0] = value;
            }
            pd = new ComboClassPropertyDescriptor(property, name, boundaryClasses, new Object[]{simulation});
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
    public static PropertySourceWrapper[] wrapArrayElements(Object[] array, Simulation sim) {
        int nonNullCount = 0;
        for(int i=0; i<array.length; i++) {
            if (array[i] != null) {
                nonNullCount++;
            }
        }
        PropertySourceWrapper[] wrappedArray = new PropertySourceWrapper[nonNullCount];
        for(int i=0; i<array.length; i++) {
            if (array[i] != null) {
                wrappedArray[i] = PropertySourceWrapper.makeWrapper(array[i],sim);
            }
        }
        return wrappedArray;
    }

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
    
    protected Object object;
	protected IPropertyDescriptor[] descriptors;
    protected String displayName;
    protected PropertySourceWrapper[] children;
    protected Simulation simulation;
}
