package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BooleanArrayWrapper extends PropertySourceWrapper {

    public BooleanArrayWrapper(boolean[] obj) {
        super(obj);
        setDisplayName("boolean array");
    }

    protected void generateDescriptors() {
        boolean[] booleanArray = (boolean[])object;
        //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[booleanArray.length];
        for (int i=0; i<booleanArray.length; i++) {
            descriptors[i] = new org.eclipse.ui.views.properties.PropertyDescriptor(new Integer(i),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Boolean(((boolean[])object)[index]);
    }
}
