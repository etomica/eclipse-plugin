package etomica.plugin.wrappers;

import java.awt.Color;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.util.EnumeratedType;

public class IntArrayWrapper extends PropertySourceWrapper {

    public IntArrayWrapper(int[] obj) {
        super(obj);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements((Object[])object);
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        if(descriptors == null) generateDescriptors();
        return descriptors;
    }
    
    public Object getEditableValue() {
        return this;
    }

    private void generateDescriptors() {
        int[] intArray = (int[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[intArray.length];
        for (int i=0; i<intArray.length; i++) {
            descriptors[i] = new org.eclipse.ui.views.properties.PropertyDescriptor(new Integer(i),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Integer(((int[])object)[index]);
    }


    public String toString() {
        return "integer array";
    }

    private IPropertyDescriptor[] descriptors;
}
