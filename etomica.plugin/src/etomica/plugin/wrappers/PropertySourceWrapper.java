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

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.data.DataPipeForked;
import etomica.data.DataProcessor;
import etomica.phase.Phase;
import etomica.plugin.views.CheckboxPropertyDescriptor;
import etomica.plugin.views.DecimalPropertyDescriptor;
import etomica.plugin.views.EnumeratedTypePropertyDescriptor;
import etomica.plugin.views.IntegerPropertyDescriptor;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.space.Boundary;
import etomica.species.Species;
import etomica.util.EnumeratedType;

/**
 * Wraps an object with an implementation of IPropertySource so that
 * it may be displayed and edited in the eclipse property sheet.  Properties
 * associated with wrapped object are determined through Java reflection.
 */

//take care not to confuse java.beans.PropertyDescriptor and org.eclipse.ui.view.properties.PropertyDescriptor

public class PropertySourceWrapper implements IPropertySource {

	/**
	 * Constructs new instance, wrapping the given object.
	 */
	protected PropertySourceWrapper(Object object) {
		super();
		this.object = object;
	}
	
    public static PropertySourceWrapper makeWrapper(Object obj) {
        if (obj instanceof Object[]) {
            return new ArrayWrapper((Object[])obj);
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
            return new PotentialMasterWrapper((PotentialMaster)obj);
        }
        else if (obj instanceof PotentialGroup) {
            return new PotentialGroupWrapper((PotentialGroup)obj);
        }
        else if (obj instanceof Phase) {
            return new PhaseWrapper((Phase)obj);
        }
        else if (obj instanceof Species) {
            return new SpeciesWrapper((Species)obj);
        }
        else if (obj instanceof ActionGroup) {
            return new ActionGroupWrapper((ActionGroup)obj);
        }
        else if (obj instanceof ActivityIntegrate) {
            return new ActivityIntegrateWrapper((ActivityIntegrate)obj);
        }
        else if (obj instanceof DataStreamHeader) {
            return new DataStreamWrapper((DataStreamHeader)obj);
        }
        else if (obj instanceof DataPipeForked) {
            return new DataForkWrapper((DataPipeForked)obj);
        }
        else if (obj instanceof DataProcessor) {
            return new DataProcessorWrapper((DataProcessor)obj);
        }
        else if (obj instanceof Atom) {
            return new AtomWrapper((Atom)obj);
        }
        return new PropertySourceWrapper(obj);
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
            return PropertySourceWrapper.makeWrapper(value);
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
		
    private IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {

		// Don't display hidden or expert properties.
		if (property.isHidden() || property.isExpert()) {
		    return null;
        }
		
		String name = property.getDisplayName();  //Localized display name 
		if(name.equals("class")) return null;//skip getDimension(), getClass()
		
		Class type = property.getPropertyType();  //Type (class) of this property
		Method getter = property.getReadMethod(); //method used to read value of property in this object

		// Display only read/write properties.
		if (getter == null) return null;
		
		// Do not display dimension specifications as properties
        if(etomica.units.Dimension.class.isAssignableFrom(type)) return null;
        //if(etomica.utility.LinkedList.class.isAssignableFrom(type)) return null;
		
		IPropertyDescriptor pd = null;
		try {
//			//read the current value of the property
			Object args[] = { };
						
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
                Object value = getter.invoke(object, args);
                if (value != null) {
                    pd = new EnumeratedTypePropertyDescriptor(property,name,((EnumeratedType)value).choices());
                }
			}
			else if(String.class.isAssignableFrom(type)) {
				pd = new TextPropertyDescriptor(property, name);
			}
            else if(Boundary.class.isAssignableFrom(type)) {
				pd = new ComboBoxPropertyDescriptor(property,name,new String[] {"test A", "test B"});
			}
			if (pd == null) {
				pd = new org.eclipse.ui.views.properties.PropertyDescriptor(property, name);
			}
//		
//			//find and instantiate the editor used to modify value of the property
//			if(property.isConstrained())
//				editor = new ConstrainedPropertyEditor();
//			//if property is a TypedConstant
//			else if(etomica.Constants.TypedConstant.class.isAssignableFrom(type) && value != null) {
//				editor = new TypedConstantEditor();
//			}
//			else if(etomica.units.Unit.class.isAssignableFrom(type)) {
//				editor = new etomica.UnitEditor((Unit)value);
//			}
//			else if(etomica.MCMove[].class.isAssignableFrom(type)) {
//				JavaWriter javaWriter = (JavaWriter)Etomica.javaWriters.get(target.parentSimulation());
//				editor = new McMoveEditor((etomica.IntegratorMC)parentNode.object(),javaWriter);
//			}
//			else if(etomica.SimulationElement.class.isAssignableFrom(type)) {
//				editor = new etomica.SimulationElementEditor(type);
//			}
//			else {
//			//property is a dimensioned number
//				if(type == Double.TYPE) {
//					//try to get dimension from get(property)Dimension() method
//					etomica.units.Dimension dimension = etomica.units.Dimension.introspect(parentNode.object(),name,bi);
//					//try to get dimension from getDimension() method
//					if(dimension == null) dimension = etomica.units.Dimension.introspect(parentNode.object(),"",bi);
//					if(dimension != null) {
//						editor = new DimensionedDoubleEditor(dimension);
//					}
//				}
//			//property is not a dimensioned number; see if its editor was set explicitly
//				if(editor == null) { 
//					Class pec = property.getPropertyEditorClass();
//					if (pec != null) {
//						try {
//							editor = (PropertyEditor)pec.newInstance();
//						} catch (Exception ex) {}
//					}
//				}
//			//property is not a dimensioned number and was not set explicitly
//			//have editor manager look for an appropriate editor
//			if (editor == null)
//				editor = PropertyEditorManager.findEditor(type);
//			}//done with trying to get an editor for the property
//			
//			// If we can't edit this component, skip it.
//			if (editor == null) {
//			// If it's a user-defined property we give a warning.
//			String getterClass = property.getReadMethod().getDeclaringClass().getName();
//				if (getterClass.indexOf("java.") != 0) {
//					System.err.println("Warning: Can't find public property editor for property \"" + name + "\".  Skipping.");
//				}
//				return null;
//			}
//			
//			//set the editor to the current value of the property
//			try {
//				editor.setValue(value);
//			} catch(NullPointerException e) {}
//			
//			//add listener that causes the wasModified method to be 
//			//invoked when editor fires property-change event
//			editor.addPropertyChangeListener(adaptor);
//			
//			// Now figure out how to display it...
//			if (editor.isPaintable() && editor.supportsCustomEditor())
//				view = new PropertyCanvas(frame, editor);
//			else if (editor instanceof etomica.UnitEditor) 
//				view = ((etomica.UnitEditor)editor).unitSelector();
//			else if (editor.getTags() != null)
//				view = new PropertySelector(editor);
//			else if (editor.getAsText() != null) {
//				view = new PropertyText(editor);
//			}
//			else if (editor instanceof ConstrainedPropertyEditor) {
//				view = new EmptyPanel();
//			}
//			else {
//				System.err.println("Warning: Property \"" + name 
//				+ "\" has non-displayable editor.  Skipping.");
//				return null;
//			}
//			if(editor instanceof DimensionedDoubleEditor) {
//				unitView = ((DimensionedDoubleEditor)editor).unitSelector();
//				if(parentNode.object() instanceof etomica.Meter)
//				((PropertyText)view).setEditable(false);
//			}
//			else unitView = new EmptyPanel();
		} //end of try
		catch (InvocationTargetException ex) {
			System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
//			ex.getTargetException().printStackTrace();
			return null;
		} 
		catch (Exception ex) {
			System.err.println("Skipping property " + name + " ; exception: " + ex);
//			ex.printStackTrace();
			return null;
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
    public static PropertySourceWrapper[] wrapArrayElements(Object[] array) {
        PropertySourceWrapper[] wrappedArray = new PropertySourceWrapper[array.length];
        for(int i=0; i<array.length; i++) {
            wrappedArray[i] = PropertySourceWrapper.makeWrapper(array[i]);
        }
        return wrappedArray;
    }

    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[0];
    }
    
    protected Object object;
	protected IPropertyDescriptor[] descriptors;
    protected String displayName;
}
