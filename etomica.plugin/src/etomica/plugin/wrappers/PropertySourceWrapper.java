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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.atom.AtomFactory;
import etomica.atom.AtomFactoryHetero;
import etomica.atom.AtomPositionDefinition;
import etomica.atom.AtomSource;
import etomica.atom.AtomType;
import etomica.data.DataAccumulator;
import etomica.data.DataPipeForked;
import etomica.data.DataProcessor;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorMC;
import etomica.integrator.MCMove;
import etomica.integrator.mcmove.MCMoveManager;
import etomica.nbr.NeighborCriterion;
import etomica.phase.Phase;
import etomica.plugin.Registry;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.views.CheckboxPropertyDescriptor;
import etomica.plugin.views.ComboClassPropertyDescriptor;
import etomica.plugin.views.ComboPropertyDescriptor;
import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.plugin.views.IntegerPropertyDescriptor;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.space.Boundary;
import etomica.space.Vector;
import etomica.species.Species;
import etomica.util.Default;
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
        return makeWrapper(obj, sim, null);
    }
    
    public static PropertySourceWrapper makeWrapper(Object obj, Simulation sim, EtomicaEditor editor) {
        PropertySourceWrapper wrapper = null;
        if (obj instanceof Object[]) {
            wrapper = new ArrayWrapper((Object[])obj,sim);
        }
        else if (obj instanceof double[]) {
            wrapper = new DoubleArrayWrapper((double[])obj);
        }
        else if (obj instanceof int[]) {
            wrapper = new IntArrayWrapper((int[])obj);
        }
        else if (obj instanceof boolean[]) {
            wrapper = new BooleanArrayWrapper((boolean[])obj);
        }
        else if (obj instanceof Simulation) {
            wrapper = new SimulationWrapper((Simulation)obj);
        }
        else if (obj instanceof PotentialMaster) {
            wrapper = new PotentialMasterWrapper((PotentialMaster)obj,sim);
        }
        else if (obj instanceof PotentialGroup) {
            wrapper = new PotentialGroupWrapper((PotentialGroup)obj,sim);
        }
        else if (obj instanceof Phase) {
            wrapper = new PhaseWrapper((Phase)obj,sim);
        }
        else if (obj instanceof Species) {
            wrapper = new SpeciesWrapper((Species)obj,sim);
        }
        else if (obj instanceof ActionGroup) {
            wrapper = new ActionGroupWrapper((ActionGroup)obj,sim);
        }
        else if (obj instanceof ActivityIntegrate) {
            wrapper = new ActivityIntegrateWrapper((ActivityIntegrate)obj,sim);
        }
        else if (obj instanceof Integrator) {
            if (obj instanceof IntegratorMC) {
                wrapper = new IntegratorMCWrapper((IntegratorMC)obj,sim);
            }
            else {
                wrapper = new IntegratorWrapper((Integrator)obj,sim);
            }
        }
        else if (obj instanceof MCMoveManager) {
            wrapper = new MCMoveManagerWrapper((MCMoveManager)obj,sim);
        }
        else if (obj instanceof MCMove) {
            wrapper = new MCMoveWrapper((MCMove)obj,sim);
        }
        else if (obj instanceof AtomSource) {
            wrapper = new AtomSourceWrapper((AtomSource)obj,sim);
        }
        else if (obj instanceof DataStreamHeader) {
            wrapper = new DataStreamWrapper((DataStreamHeader)obj,sim);
        }
        else if (obj instanceof DataAccumulator) {
            wrapper = new DataAccumulatorWrapper((DataAccumulator)obj,sim);
        }
        else if (obj instanceof DataPipeForked) {
            wrapper = new DataForkWrapper((DataPipeForked)obj,sim);
        }
        else if (obj instanceof DataProcessor) {
            wrapper = new DataProcessorWrapper((DataProcessor)obj,sim);
        }
        else if (obj instanceof DataSource) {
            wrapper = new DataSourceWrapper((DataSource)obj,sim);
        }
        else if (obj instanceof Atom) {
            wrapper = new AtomWrapper((Atom)obj,sim);
        }
        else if (obj instanceof AtomType) {
            wrapper = new AtomTypeWrapper((AtomType)obj,sim);
        }
        else if (obj instanceof Vector) {
            wrapper = new VectorWrapper((Vector)obj);
        }
        else if (obj instanceof Default) {
            wrapper = new DefaultWrapper((Default)obj,sim);
        }
        else if (obj instanceof AtomFactoryHetero) {
            wrapper = new AtomFactoryHeteroWrapper((AtomFactoryHetero)obj,sim);
        }
        else {
            wrapper = new PropertySourceWrapper(obj,sim);
        }
        if (editor != null) {
            wrapper.setEditor(editor);
        }
        return wrapper;
    }
    
    public void setEditor(EtomicaEditor editor) {
        etomicaEditor = editor;
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
     * We use the java.beans.PropertyDescriptor as the default key for the 
     * properties.  Subclasses may override and use different types of keys.
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
            return PropertySourceWrapper.makeWrapper(value, simulation, etomicaEditor);
        }
        if (value instanceof Vector) {
            return new VectorWrapper((Vector)value);
        }
        return value;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object key) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object key, Object value) {
		java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)key;
		Method setter = pd.getWriteMethod(); //method used to read value of property in this object
		if(setter == null) return;
        if (value instanceof PropertySourceWrapper) {
            value = ((PropertySourceWrapper)value).getObject();
        }
		try {
			setter.invoke(object, new Object[] {value});
            if (etomicaEditor != null) {
                etomicaEditor.markDirty();
            }
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
        if (simulation != null && (type == AtomPositionDefinition.class ||
                type == Boundary.class || type == AtomFactory.class ||
                type == DataSink.class || type == NeighborCriterion.class)) {
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
                        
        return makeDescriptor(property,value,type,name);
    }
    
    protected IPropertyDescriptor makeDescriptor(Object property, Object value, Class type, String name) {
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
		else if(String.class.isAssignableFrom(type)) {
			pd = new TextPropertyDescriptor(property, name);
		}
        else if (type == Phase.class && simulation != null) {
            pd = new ComboPropertyDescriptor(property, name, simulation.getPhases());
        }
        else if (type == Integrator.class && simulation != null) {
            pd = new ComboPropertyDescriptor(property, name, simulation.getIntegratorList().toArray());
        }
        else if (simulation != null && (type == AtomPositionDefinition.class ||
                type == Boundary.class || type == AtomFactory.class ||
                type == DataSink.class || type == NeighborCriterion.class)) {
            Collection collection = Registry.queryWhoExtends(type);
            Iterator classIterator = collection.iterator();
            int length = collection.size();
            if (value != null) {
                length++;
            }
            Object[] classes = new Object[length];
            int i = 0;
            if (value != null) {
                classes[i++] = value;
            }
            while (classIterator.hasNext()) {
                classes[i++] = classIterator.next();
            }
            pd = new ComboClassPropertyDescriptor(property, name, classes, new Object[]{simulation});
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
    
    public String[] getOpenViews() {
        return new String[0];
    }
    
    public boolean open(String openView, IWorkbenchPage page, Shell shell) {
        return false;
    }
    
    public EtomicaStatus getStatus() {
        return EtomicaStatus.PEACHY;
    }
    
    protected Object object;
	protected IPropertyDescriptor[] descriptors;
    protected String displayName;
    protected PropertySourceWrapper[] children;
    protected Simulation simulation;
    protected EtomicaEditor etomicaEditor;
    
}
