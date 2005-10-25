package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class IntArrayWrapper extends PropertySourceWrapper {

    public IntArrayWrapper(int[] obj) {
        super(obj);
        setDisplayName("integer array");
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements((Object[])object);
    }

    public Object getEditableValue() {
        return this;
    }

    protected void generateDescriptors() {
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
}
