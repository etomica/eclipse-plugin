/*
 * History
 * Created on Oct 10, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Label;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import javax.swing.JLabel;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import etomica.graphics.DimensionedDoubleEditor;
import etomica.graphics.PropertyText;
import etomica.units.Meter;
import etomica.units.Unit;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

//take care not to confuse java.beans.PropertyDescriptor and org.eclipse.ui.view.properties.PropertyDescriptor

public class PropertySourceWrapper implements IPropertySource {

	/**
	 * 
	 */
	public PropertySourceWrapper(Object object) {
		super();
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object arg0) {
		java.beans.PropertyDescriptor pd = (java.beans.PropertyDescriptor)arg0;
		Method getter = pd.getReadMethod(); //method used to read value of property in this object
		Object value = null;
		Object args[] = { };
		try {value = getter.invoke(object, args);}
		catch(NullPointerException ex) {value = null;}
		catch(InvocationTargetException ex) {value = null;}
		catch(IllegalAccessException ex) {value = null;}
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
		// TODO Auto-generated method stub

	}
	
	public String toString() {
		return object.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors == null) generateDescriptors();
		return descriptors;
	}
	
	private void generateDescriptors() {
       //Introspection to get array of all properties
        java.beans.PropertyDescriptor[] properties = null;
        BeanInfo bi = null;
        try {
	        bi = Introspector.getBeanInfo(object.getClass());
	        properties = bi.getPropertyDescriptors();
	    } 
	    catch (IntrospectionException ex) {
	        error("PropertySheet: Couldn't introspect", ex);
	        return;
	    }

	    LinkedList list = new LinkedList();
	    for (int i = 0; i < properties.length; i++) {
	        IPropertyDescriptor pd = makeDescriptor(properties[i], bi);
	        if(pd != null) list.add(pd);
	    }//end of loop over properties
	    Object[] array = list.toArray();
	    descriptors = new IPropertyDescriptor[array.length];
	    for(int i=0; i<array.length; i++) descriptors[i] = (IPropertyDescriptor)array[i];
	}
		
    private IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property, BeanInfo bi) {

		// Don't display hidden or expert properties.
		if (property.isHidden() || property.isExpert())
		return null;
		
		Object value = null;
		Component view = null;
		Component unitView = null;
		JLabel label = null;
		PropertyEditor editor = null;
		
		String name = property.getDisplayName();  //Localized display name 
		if(name.equals("dimension") || name.equals("class")) return null;//skip getDimension(), getClass()
		
		org.eclipse.ui.views.properties.PropertyDescriptor pd = new org.eclipse.ui.views.properties.TextPropertyDescriptor(property, name);

//		Class type = property.getPropertyType();  //Type (class) of this property
//		Method getter = property.getReadMethod(); //method used to read value of property in this object
//		Method setter = property.getWriteMethod();//method used to set value of property
//		// Display only read/write properties.
//		if (getter == null) {
//			return null;
//		}
//		try {
//			//read the current value of the property
//			Object args[] = { };
//			try {value = getter.invoke(parentNode.object(), args);}
//			catch(NullPointerException ex) {value = null;}
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
//		} //end of try
//		catch (InvocationTargetException ex) {
//			System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
//			ex.getTargetException().printStackTrace();
//			return null;
//		} 
//		catch (Exception ex) {
//			System.err.println("Skipping property " + name + " ; exception: " + ex);
//			ex.printStackTrace();
//			return null;
//		}
//		
//		MyLabel newLabel = new MyLabel(name, Label.LEFT);
//		
//		PropertyNode child = new PropertyNode(value, newLabel, view, unitView, editor, property);
//		
//		//See if object can have child objects in tree (cannot if it is primitive)
//		if(!(value == null || 
//		value instanceof Number || 
//		value instanceof Boolean ||
//		value instanceof Character ||
//		value instanceof String ||
//		value instanceof Color ||
//		value instanceof etomica.Constants.TypedConstant ||
//		value instanceof java.awt.Font)) {/*add dummy child*/
//		child.add(new PropertyNode(null,new JLabel(),new EmptyPanel(), new EmptyPanel(), null, null));
//		}
//		
//		parentNode.add(child);
//		
//		return child;
		return pd;
	}//end of processProperty


    private void error(String message, Throwable th) {
	    System.err.println(message);
	    th.printStackTrace();
    }

	
	public static PropertySourceWrapper[] wrapArrayElements(Object[] array) {
		PropertySourceWrapper[] wrappedArray = new PropertySourceWrapper[array.length];
		for(int i=0; i<array.length; i++) {
			wrappedArray[i] = new PropertySourceWrapper(array[i]);
		}
		return wrappedArray;
	}

	Object object;
	IPropertyDescriptor[] descriptors;
}
