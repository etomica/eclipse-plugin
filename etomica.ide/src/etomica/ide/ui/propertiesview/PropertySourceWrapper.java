/*
 * History
 * Created on Oct 10, 2004 by kofke
 */
package etomica.ide.ui.propertiesview;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors == null) generateDescriptors();
		return descriptors;
	}
	
	private void generateDescriptors() {
		descriptors = new IPropertyDescriptor[] {new org.eclipse.ui.views.properties.PropertyDescriptor(new Object(), "test")};
        //Introspection to get array of all properties
//        PropertyDescriptor[] properties = null;
//        BeanInfo bi = null;
//        try {
//	        bi = Introspector.getBeanInfo(object.getClass());
//	        properties = bi.getPropertyDescriptors();
//	    } 
//	    catch (IntrospectionException ex) {
////	        error("PropertySheet: Couldn't introspect", ex);
//	        return;
//	    }

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object arg0) {
		// TODO Auto-generated method stub
		return null;
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
