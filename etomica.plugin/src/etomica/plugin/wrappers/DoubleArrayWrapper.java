package etomica.plugin.wrappers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DoubleArrayWrapper extends PropertySourceWrapper {

    public DoubleArrayWrapper(double[] obj) {
        super(obj);
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        if(descriptors == null) generateDescriptors();
        return descriptors;
    }
    
    public Object getEditableValue() {
        return this;
    }

    private void generateDescriptors() {
        double[] doubleArray = (double[])object;
       //Introspection to get array of all properties
        descriptors= new PropertyDescriptor[doubleArray.length];
        for (int i=0; i<doubleArray.length; i++) {
            descriptors[i] = new org.eclipse.ui.views.properties.PropertyDescriptor(new Integer(i),Integer.toString(i));
        }
    }

    public Object getPropertyValue(Object key) {
        int index = ((Integer)key).intValue();
        return new Double(((double[])object)[index]);
    }


    public String toString() {
        return "double array";
    }

    private IPropertyDescriptor[] descriptors;
}
