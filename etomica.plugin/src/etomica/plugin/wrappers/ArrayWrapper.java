package etomica.plugin.wrappers;

import java.awt.Color;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import etomica.util.EnumeratedType;

public class ArrayWrapper extends PropertySourceWrapper {

    public ArrayWrapper(Object[] obj) {
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
        Object[] objArray = (Object[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[objArray.length];
        for (int i=0; i<objArray.length; i++) {
            descriptors[i] = new org.eclipse.ui.views.properties.PropertyDescriptor(new Integer(i),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        Object value = ((Object[])object)[index];
        if(!(value == null || 
                value instanceof Number || 
                value instanceof Boolean ||
                value instanceof Character ||
                value instanceof String ||
                value instanceof Color ||
                value instanceof EnumeratedType)) {
            return PropertySourceWrapper.makeWrapper(value);
        }
        return value;
    }


    public String toString() {
        return object.getClass().getComponentType().getSimpleName()+" array";
    }

    private IPropertyDescriptor[] descriptors;
}
